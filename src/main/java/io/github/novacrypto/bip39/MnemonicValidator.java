/*
 *  BIP39 library, a Java implementation of BIP39
 *  Copyright (C) 2017 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP39
 *  You can contact the authors via github issues.
 */

package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;

import java.util.Arrays;
import java.util.Comparator;

import static io.github.novacrypto.bip39.MnemonicGenerator.firstByteOfSha256;

public final class MnemonicValidator {
    private final WordAndIndex[] words;
    private final char space;

    private MnemonicValidator(final WordList wordList) {
        words = new WordAndIndex[1 << 11];
        for (int i = 0; i < 1 << 11; i++) {
            final String word = wordList.getWord(i);
            words[i] = new WordAndIndex(i, word.toCharArray(), word);
        }
        space = wordList.getSpace();
        Arrays.sort(words, wordListSortOrder);
    }

    public static MnemonicValidator ofWordList(WordList wordList) {
        return new MnemonicValidator(wordList);
    }

    public void validate(final CharSequence mnemonic) throws
            InvalidChecksumException,
            InvalidWordCountException,
            WordNotFoundException {
        final int[] wordIndexes = findWordIndexes(mnemonic);
        final int ms = wordIndexes.length;

        final int entPlusCs = ms * 11;
        final int ent = (entPlusCs * 32) / 33;
        final int cs = ent / 32;
        if (entPlusCs != ent + cs)
            throw new InvalidWordCountException();
        final byte[] entropyWithChecksum = new byte[(entPlusCs + 7) / 8];

        wordIndexesToEntropyWithCheckSum(wordIndexes, entropyWithChecksum);

        final byte[] entropy = Arrays.copyOf(entropyWithChecksum, entropyWithChecksum.length - 1);
        final byte lastByte = entropyWithChecksum[entropyWithChecksum.length - 1];
        Arrays.fill(entropyWithChecksum, (byte) 0);

        final byte sha = firstByteOfSha256(entropy);

        final byte mask = maskOfFirstNBits(cs);

        if (((sha ^ lastByte) & mask) != 0)
            throw new InvalidChecksumException();
    }

    private int[] findWordIndexes(final CharSequence mnemonic) throws WordNotFoundException {
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
        result[w] = findWordIndex(buffer);
        Arrays.fill(buffer, '\0');
        return result;
    }

    private int findWordIndex(char[] buffer) throws WordNotFoundException {
        final WordAndIndex key = new WordAndIndex(-1, buffer, "");
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

    private void wordIndexesToEntropyWithCheckSum(int[] wordIndexes, byte[] entropyWithChecksum) {
        for (int i = 0, bi = 0; i < wordIndexes.length; i++, bi += 11) {
            ByteUtils.writeNext11(entropyWithChecksum, wordIndexes[i], bi);
        }
    }

    private byte maskOfFirstNBits(int n) {
        return (byte) ~((1 << (8 - n)) - 1);
    }

    static final Comparator<WordAndIndex> wordListSortOrder = new Comparator<WordAndIndex>() {
        @Override
        public int compare(WordAndIndex o1, WordAndIndex o2) {
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

    private class WordAndIndex {
        final char[] word;
        final String string;
        final int index;
        final int length;

        WordAndIndex(int i, char[] word, String string) {
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

}
