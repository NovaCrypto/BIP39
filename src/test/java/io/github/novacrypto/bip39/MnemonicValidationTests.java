package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

import static org.junit.Assert.*;

/**
 * Created by aevans on 2017-10-08.
 */
public final class MnemonicValidationTests {

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertTrue(validate(testCase[1], English.INSTANCE));
        }
    }

    @Test
    public void all_english_test_vectors_words_swapped() throws Exception {
        int testCaseCount = 0;
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            final String mnemonic = swapWords(testCase[1], 0, 1, English.INSTANCE);
            if (mnemonic.equals(testCase[1])) continue; //word were same
            assertFalse(validate(mnemonic, English.INSTANCE));
            testCaseCount++;
        }
        assertEquals(18, testCaseCount);
    }

    private static String swapWords(String mnemonic, int index1, int index2, WordList wordList) {
        final String[] split = mnemonic.split(String.valueOf(wordList.getSpace()));
        String temp = split[index1];
        split[index1] = split[index2];
        split[index2] = temp;
        StringJoiner joiner = new StringJoiner(String.valueOf(wordList.getSpace()));
        for (String string : split) {
            joiner.add(string);
        }
        return joiner.toString();
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            assertTrue(validate(testCase.mnemonic, Japanese.INSTANCE));
        }
    }

    @Test
    public void all_japanese_test_vectors_words_swapped() throws Exception {
        int testCaseCount = 0;
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            final String mnemonic = swapWords(testCase.mnemonic, 1, 3, Japanese.INSTANCE);
            if (mnemonic.equals(testCase.mnemonic)) continue; //word were same
            assertFalse(validate(mnemonic, Japanese.INSTANCE));
            testCaseCount++;
        }
        assertEquals(18, testCaseCount);
    }

    private boolean validate(String mnemonic, WordList wordList) {
        //build a map to look up word indexes
        Map<String, Integer> map = new HashMap<>(1 << 11);
        for (int i = 0; i < 1 << 11; i++) {
            map.put(wordList.getWord(i), i);
        }

        //split the mnemonic
        String[] words = mnemonic.split(String.valueOf(wordList.getSpace()));

        //reverse calculate some of the variables from mnemonic generation, ms, ent, cs
        final int ms = words.length;

        final int entPlusCs = ms * 11;
        final int ent = (entPlusCs * 32) / 33;
        final int cs = ent / 32;
        assertEquals(entPlusCs, ent + cs);
        byte[] entropyWithChecksum = new byte[entPlusCs / 8 + (entPlusCs % 8 > 0 ? 1 : 0)];

        //look up the words
        int[] wordIndexes = new int[ms];
        for (int i = 0; i < ms; i++) {
            String word = words[i];
            Integer index = map.get(word);
            if (index == null) throw new RuntimeException("Word not found in word list \"" + word + "\"");
            wordIndexes[i] = index;
        }

        //build
        for (int i = 0, bi = 0; i < ms; i++, bi += 11) {
            writeNext11(entropyWithChecksum, wordIndexes[i], bi);
        }

        //strip the last byte
        byte[] entropy = Arrays.copyOf(entropyWithChecksum, entropyWithChecksum.length - 1);
        byte lastByte = entropyWithChecksum[entropyWithChecksum.length - 1];

        //recalculate hash
        byte sha = firstByteOfSha256(entropy);

        //we only want to compare the first cs bits
        byte mask = (byte) ~((1 << (8 - cs)) - 1);

        //if the first cs bits are the same, it's valid
        return ((sha ^ lastByte) & mask) == 0;
    }

    private void writeNext11(byte[] bytes, int value, int offset) {
        int skip = offset / 8;
        int bitSkip = offset % 8;
        {//byte 0
            byte firstValue = bytes[skip];
            byte toWrite = (byte) (value >> (3 + bitSkip));
            bytes[skip] = (byte) (firstValue | toWrite);
        }

        {//byte 1
            byte valueInByte = bytes[skip + 1];
            final int i = 5 - bitSkip;
            byte toWrite = (byte) (i > 0 ? (value << i) : (value >> -i));
            bytes[skip + 1] = (byte) (valueInByte | toWrite);
        }

        if (bitSkip >= 6) {//byte 2
            byte valueInByte = bytes[skip + 2];
            byte toWrite = (byte) (value << 13 - bitSkip);
            bytes[skip + 2] = (byte) (valueInByte | toWrite);
        }
    }

    private static byte firstByteOfSha256(final byte[] entropy) {
        final byte[] hash = sha256().digest(entropy);
        final byte firstByte = hash[0];
        Arrays.fill(hash, (byte) 0);
        return firstByte;
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


}
