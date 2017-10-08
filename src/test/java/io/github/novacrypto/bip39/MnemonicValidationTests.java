package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

import java.util.StringJoiner;

import static org.junit.Assert.*;

/**
 * Created by aevans on 2017-10-08.
 */
public final class MnemonicValidationTests {

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word() throws Exception {
        try {
            validate("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"alan\", suggestions \"aisle\", \"alarm\"", e.getMessage());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word_alphabetically_before_all_others() throws Exception {
        try {
            validate("aardvark abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"aardvark\", suggestions \"abandon\", \"ability\"", e.getMessage());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word_alphabetically_after_all_others() throws Exception {
        try {
            validate("zymurgy abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"zymurgy\", suggestions \"zone\", \"zoo\"", e.getMessage());
            throw e;
        }
    }

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
        return MnemonicValidator
                .ofWordList(wordList)
                .validate(mnemonic);
    }
}
