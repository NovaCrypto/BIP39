package io.github.novacrypto.bip39;

import java.util.Arrays;
import java.util.Comparator;

import static io.github.novacrypto.bip39.MnemonicGeneration.firstByteOfSha256;

/**
 * Created by aevans on 2017-10-08.
 */
public final class MnemonicValidator {

    private class Pair {
        final char[] word;
        final String string;
        final int index;
        final int length;

        Pair(int i, char[] word, String string) {
            this.word = word;
            index = i;
            length = findWordLength(word);
            this.string = string;
        }

        private int findWordLength(char[] word) {
            final int len = word.length;
            for (int j = 0; j < len; j++) {
                if (word[j] == '\0') {
                    return j;
                }
            }
            return len;
        }
    }

    private final Pair[] words;
    private final char space;

    private MnemonicValidator(final WordList wordList) {
        words = new Pair[1 << 11];
        for (int i = 0; i < 1 << 11; i++) {
            final String word = wordList.getWord(i);
            words[i] = new Pair(i, word.toCharArray(), word);
        }
        space = wordList.getSpace();
        Arrays.sort(words, wordListSortOrder);
    }

    public static MnemonicValidator ofWordList(WordList wordList) {
        return new MnemonicValidator(wordList);
    }

    public boolean validate(final CharSequence mnemonic) {
        final int[] wordIndexes = findWordIndexes(mnemonic);
        final int ms = wordIndexes.length;

        final int entPlusCs = ms * 11;
        final int ent = (entPlusCs * 32) / 33;
        final int cs = ent / 32;
        // assertEquals(entPlusCs, ent + cs);
        final int entropyWithCheckSumByteLength = entPlusCs / 8 + (entPlusCs % 8 > 0 ? 1 : 0);
        final byte[] entropyWithChecksum = new byte[entropyWithCheckSumByteLength];

        wordIndexsToEntropyWithCheckSum(wordIndexes, entropyWithChecksum);

        final byte[] entropy = Arrays.copyOf(entropyWithChecksum, entropyWithChecksum.length - 1);
        final byte lastByte = entropyWithChecksum[entropyWithChecksum.length - 1];
        Arrays.fill(entropyWithChecksum, (byte) 0);

        final byte sha = firstByteOfSha256(entropy);

        final byte mask = (byte) ~((1 << (8 - cs)) - 1);

        return ((sha ^ lastByte) & mask) == 0;
    }

    private int[] findWordIndexes(final CharSequence mnemonic) {
        final int ms = countSpaces(mnemonic) + 1;
        int w = 0;
        int bi = 0;
        final int[] result = new int[ms];
        final int length = mnemonic.length();
        final char[] buffer = new char[length];
        for (int i = 0; i < length; i++) {
            final char c = mnemonic.charAt(i);
            if (c == space) {
                buffer[bi] = '\0';
                result[w++] = findWordIndex(buffer);
                bi = 0;
            } else {
                buffer[bi++] = c;
            }
        }
        buffer[bi] = '\0';
        result[w++] = findWordIndex(buffer);
        Arrays.fill(buffer, '\0');
        return result;
    }

    private int findWordIndex(char[] buffer) {
        final Pair key = new Pair(-1, buffer, "");
        final int index = Arrays.binarySearch(words, key, wordListSortOrder);
        if (index < 0) {
            final int insertionPoint = -index - 1;
            final String buffer1 = new String(buffer, 0, key.length);
            int suggestion = insertionPoint == 0 ? insertionPoint : insertionPoint - 1;
            if (suggestion + 1 == words.length) suggestion--;
            throw new WordNotFoundException(buffer1, words[suggestion].string, words[suggestion + 1].string);

        }
        return words[index].index;
    }

    private int countSpaces(final CharSequence mnemonic) {
        int spaces = 0;
        final int length = mnemonic.length();
        for (int i = 0; i < length; i++) {
            if (mnemonic.charAt(i) == space) spaces++;
        }
        return spaces;
    }

    private void wordIndexsToEntropyWithCheckSum(int[] wordIndexes, byte[] entropyWithChecksum) {
        for (int i = 0, bi = 0; i < wordIndexes.length; i++, bi += 11) {
            ByteUtils.writeNext11(entropyWithChecksum, wordIndexes[i], bi);
        }
    }

    static final Comparator<Pair> wordListSortOrder = new Comparator<Pair>() {
        @Override
        public int compare(Pair o1, Pair o2) {
            final char[] word1 = o1.word;
            final char[] word2 = o2.word;
            final int length1 = o1.length;
            final int length2 = o2.length;
            final int length = Math.min(length1, length2);
            for (int i = 0; i < length; i++) {
                final int compare = Character.compare(word1[i], word2[i]);
                if (compare != 0) return compare;
            }
            return Integer.compare(length1, length2);
        }
    };
}
