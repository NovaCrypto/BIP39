/*
 *  BIP39 library, a Java implementation of BIP39
 *  Copyright (C) 2017-2018 Alan Evans, NovaCrypto
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

package io.github.novacrypto;

import io.github.novacrypto.bip39.MnemonicValidator;
import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import io.github.novacrypto.bip39.Validation.WordNotFoundException;
import io.github.novacrypto.bip39.WordList;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.French;
import io.github.novacrypto.bip39.wordlists.Japanese;
import io.github.novacrypto.bip39.wordlists.Spanish;
import io.github.novacrypto.testjson.EnglishJson;
import io.github.novacrypto.testjson.TestVector;
import io.github.novacrypto.testjson.TestVectorJson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static io.github.novacrypto.TestCharSequence.preventToString;
import static io.github.novacrypto.TestCharSequence.preventToStringAndSubSequence;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public final class MnemonicValidationTests {

    private final Mode mode;

    enum Mode {
        ValidateWholeString,
        ValidateAsStringList
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Mode.ValidateWholeString},
                {Mode.ValidateAsStringList}
        });
    }

    public MnemonicValidationTests(final Mode mode) {
        this.mode = mode;
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word() throws Exception {
        try {
            validateExpectingBadWord(
                    "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"alan\", suggestions \"aisle\", \"alarm\"", e.getMessage());
            assertEquals("alan", e.getWord());
            assertEquals("aisle", e.getSuggestion1());
            assertEquals("alarm", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void word_too_short() throws Exception {
        try {
            validateExpectingBadWord(
                    "aero abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"aero\", suggestions \"advice\", \"aerobic\"", e.getMessage());
            assertEquals("aero", e.getWord());
            assertEquals("advice", e.getSuggestion1());
            assertEquals("aerobic", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word_alphabetically_before_all_others() throws Exception {
        try {
            validateExpectingBadWord(
                    "aardvark abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"aardvark\", suggestions \"abandon\", \"ability\"", e.getMessage());
            assertEquals("aardvark", e.getWord());
            assertEquals("abandon", e.getSuggestion1());
            assertEquals("ability", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_english_word_alphabetically_after_all_others() throws Exception {
        try {
            validateExpectingBadWord(
                    "zymurgy abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon alan",
                    English.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"zymurgy\", suggestions \"zone\", \"zoo\"", e.getMessage());
            assertEquals("zymurgy", e.getWord());
            assertEquals("zone", e.getSuggestion1());
            assertEquals("zoo", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_japanese_word() throws Exception {
        try {
            validateExpectingBadWord(
                    "そつう　れきだ　ほんやく　わかす　りくつ　ばいか　ろせん　やちん　そつう　れきだい　ほんやく　わかめ",
                    Japanese.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"れきだ\", suggestions \"れきし\", \"れきだい\"", e.getMessage());
            assertEquals("れきだ", e.getWord());
            assertEquals("れきし", e.getSuggestion1());
            assertEquals("れきだい", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = WordNotFoundException.class)
    public void bad_japanese_word_normalized_behaviour() throws Exception {
        try {
            validateExpectingBadWord(
                    "そつう　れきだ　ほんやく　わかす　りくつ　ばいか　ろせん　やちん　そつう　れきだい　ほんやく　わかめ",
                    Japanese.INSTANCE);
        } catch (WordNotFoundException e) {
            assertEquals("Word not found in word list \"れきだ\", suggestions \"れきし\", \"れきだい\"", e.getMessage());
            assertEquals("れきだ", e.getWord());
            assertEquals("れきし", e.getSuggestion1());
            assertEquals("れきだい", e.getSuggestion2());
            throw e;
        }
    }

    @Test(expected = InvalidWordCountException.class)
    public void eleven_words() throws Exception {
        validate("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon",
                English.INSTANCE);
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

    @Test
    public void additional_space_end_English() {
        if (mode == Mode.ValidateAsStringList) return;
        assertThatThrownBy(() ->
                validate("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about ",
                        English.INSTANCE)
        ).isInstanceOf(UnexpectedWhiteSpaceException.class);
    }

    @Test
    public void additional_space_start_English() {
        assertThatThrownBy(() ->
                validate(" abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
                        English.INSTANCE)
        ).isInstanceOf(UnexpectedWhiteSpaceException.class);
    }

    @Test
    public void additional_space_middle_English() {
        assertThatThrownBy(() ->
                validate("abandon  abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about",
                        English.INSTANCE)
        ).isInstanceOf(UnexpectedWhiteSpaceException.class);
    }

    @Test
    public void normalize_Japanese() throws Exception {
        assertTrue(validate("あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あおぞら",
                Japanese.INSTANCE));
    }

    @Test
    public void normalize_Japanese_2() throws Exception {
        assertTrue(validate("あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あおぞら",
                Japanese.INSTANCE));
    }

    @Test
    public void normalize_Japanese_regular_spaces() throws Exception {
        assertTrue(validate("あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あおぞら",
                Japanese.INSTANCE));
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
        final TestVectorJson data = TestVectorJson.loadJapanese();
        for (final TestVector testVector : data.vectors) {
            assertTrue(validate(testVector.mnemonic, Japanese.INSTANCE));
        }
    }

    @Test
    public void all_french_test_vectors() throws Exception {
        final TestVectorJson data = TestVectorJson.loadFrench();
        for (final TestVector testVector : data.vectors) {
            assertTrue(validate(testVector.mnemonic, French.INSTANCE));
        }
    }

    @Test
    public void all_spanish_test_vectors() throws Exception {
        final TestVectorJson data = TestVectorJson.loadSpanish();
        for (final TestVector testVector : data.vectors) {
            assertTrue(validate(testVector.mnemonic, Spanish.INSTANCE));
        }
    }

    @Test
    public void all_japanese_test_vectors_words_swapped() throws Exception {
        int testCaseCount = 0;
        final TestVectorJson data = TestVectorJson.loadJapanese();
        for (final TestVector testVector : data.vectors) {
            final String mnemonic = swapWords(testVector.mnemonic, 1, 3, Japanese.INSTANCE);
            if (mnemonic.equals(testVector.mnemonic)) continue; //word were same
            assertFalse(validate(mnemonic, Japanese.INSTANCE));
            testCaseCount++;
        }
        assertEquals(18, testCaseCount);
    }

    private enum ValidateMode {
        NOT_EXPECTING_BAD_WORD,
        EXPECTING_BAD_WORD
    }

    private boolean validate(String mnemonic, WordList wordList) throws
            InvalidWordCountException,
            WordNotFoundException,
            UnexpectedWhiteSpaceException {
        return validate(mnemonic, wordList, ValidateMode.NOT_EXPECTING_BAD_WORD);
    }

    private boolean validateExpectingBadWord(String mnemonic, WordList wordList) throws
            InvalidWordCountException,
            WordNotFoundException,
            UnexpectedWhiteSpaceException {
        return validate(mnemonic, wordList, ValidateMode.EXPECTING_BAD_WORD);
    }

    private boolean validate(String mnemonic, WordList wordList, ValidateMode validateMode) throws
            InvalidWordCountException,
            WordNotFoundException,
            UnexpectedWhiteSpaceException {
        try {
            switch (mode) {
                case ValidateWholeString: {
                    MnemonicValidator
                            .ofWordList(wordList)
                            .validate(validateMode == ValidateMode.EXPECTING_BAD_WORD
                                    ? mnemonic
                                    : preventToString(mnemonic));
                    break;
                }
                case ValidateAsStringList: {
                    final List<CharSequence> words = Arrays.stream(mnemonic.split("[ \u3000]"))
                            .map(sequence -> validateMode == ValidateMode.EXPECTING_BAD_WORD
                                    ? sequence
                                    : preventToStringAndSubSequence(sequence))
                            .collect(Collectors.toList());
                    MnemonicValidator
                            .ofWordList(wordList)
                            .validate(words);
                    break;
                }
            }
            return true;
        } catch (InvalidChecksumException e) {
            return false;
        }
    }
}