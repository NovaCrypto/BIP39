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

import io.github.novacrypto.bip39.*;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.French;
import io.github.novacrypto.bip39.wordlists.Japanese;
import io.github.novacrypto.bip39.wordlists.Spanish;
import io.github.novacrypto.testjson.EnglishJson;
import io.github.novacrypto.testjson.TestVector;
import io.github.novacrypto.testjson.TestVectorJson;
import org.junit.Test;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import static io.github.novacrypto.Hex.toHex;
import static org.junit.Assert.assertEquals;

public final class SeedCalculationFromWordListTests {

    @Test
    public void bip39_english() {
        assertEquals("2eea1e4d099089606b7678809be6090ccba0fca171d4ed42c550194ca8e3600cd1e5989dcca38e5f903f5c358c92e0dcaffc9e71a48ad489bb868025c907d1e1",
                calculateSeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", ""));
    }

    @Test(expected = WordNotExactlyAsInSuppliedWordList.class)
    public void bip39_english_word_not_found() {
        calculateSeedHex("solar puppies hawk oxygen trip brief erase slot fossil mechanic filter voice", "");
    }

    @Test
    public void bip39_english_with_passphrase() {
        assertEquals("36732d826f4fa483b5fe8373ef8d6aa3cb9c8fb30463d6c0063ee248afca2f87d11ebe6e75c2fb2736435994b868f8e9d4f4474c65ee05ac47aad7ef8a497846",
                calculateSeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "CryptoIsCool"));
    }

    @Test
    public void all_english_test_vectors() {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[2], calculateSeedHex(testCase[1], "TREZOR"));
        }
    }

    @Test
    public void passphrase_normalization() {
        assertEquals(calculateSeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "ｶ"),
                calculateSeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "カ"));
    }

    @Test
    public void normalize_Japanese() {
        assertEquals("646f1a38134c556e948e6daef213609a62915ef568edb07ffa6046c87638b4b140fef2e0c6d7233af640c4a63de6d1a293288058c8ac1d113255d0504e63f301",
                calculateSeedHex("あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あおぞら",
                        "",
                        Japanese.INSTANCE));
    }

    @Test
    public void normalize_Japanese_2() {
        assertEquals("646f1a38134c556e948e6daef213609a62915ef568edb07ffa6046c87638b4b140fef2e0c6d7233af640c4a63de6d1a293288058c8ac1d113255d0504e63f301",
                calculateSeedHex("あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あいこくしん　あおぞら",
                        "",
                        Japanese.INSTANCE));
    }

    @Test
    public void normalize_Japanese_regular_spaces() {
        assertEquals("646f1a38134c556e948e6daef213609a62915ef568edb07ffa6046c87638b4b140fef2e0c6d7233af640c4a63de6d1a293288058c8ac1d113255d0504e63f301",
                calculateSeedHex("あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あいこくしん あおぞら",
                        "",
                        Japanese.INSTANCE));
    }

    @Test
    public void all_japanese_test_vectors() {
        final TestVectorJson data = TestVectorJson.loadJapanese();
        for (final TestVector testVector : data.vectors) {
            testSeedGeneration(testVector, Japanese.INSTANCE);
        }
    }

    @Test
    public void all_french_test_vectors() {
        final TestVectorJson data = TestVectorJson.loadFrench();
        for (final TestVector testVector : data.vectors) {
            testSeedGeneration(testVector, French.INSTANCE);
        }
    }

    @Test
    public void all_spanish_test_vectors() {
        final TestVectorJson data = TestVectorJson.loadSpanish();
        for (final TestVector testVector : data.vectors) {
            testSeedGeneration(testVector, Spanish.INSTANCE);
        }
    }

    private static void testSeedGeneration(TestVector testVector, WordList wordList) {
        assertEquals(testVector.seed, calculateSeedHex(testVector.mnemonic, testVector.passphrase, wordList));
    }

    private static String calculateSeedHex(final String mnemonic, String passphrase) {
        return calculateSeedHex(mnemonic, passphrase, English.INSTANCE);
    }

    private static String calculateSeedHex(final String mnemonic, String passphrase, WordList wordList) {
        final List<String> mnemonic1 = Arrays.asList(mnemonic.split("[ \u3000]"));
        for (int i = 0; i < mnemonic1.size(); i++) {
            mnemonic1.set(i, findWordFromWordList(mnemonic1.get(i), wordList));
        }
        return calculateSeedHex(mnemonic1, passphrase, wordList);
    }

    private static String findWordFromWordList(String s, WordList wordList) {
        final String find = Normalizer.normalize(s, Normalizer.Form.NFKD);
        for (int i = 0; i < 2048; i++) {
            final String wordListWord = wordList.getWord(i);
            if (find.equals(Normalizer.normalize(wordListWord, Normalizer.Form.NFKD)))
                return wordListWord;
        }
        return find;
    }

    private static String calculateSeedHex(List<String> mnemonic, String passphrase, WordList wordList) {
        final String seed1 = calculateSeed(mnemonic, passphrase, new SeedCalculator()
                .withWordsFromWordList(wordList));
        final String seed2 = calculateSeed(mnemonic, passphrase, new SeedCalculator(JavaxPBKDF2WithHmacSHA512.INSTANCE)
                .withWordsFromWordList(wordList));
        assertEquals(seed1, seed2);
        return seed1;
    }

    private static String calculateSeed(List<String> mnemonic, String passphrase, SeedCalculatorByWordListLookUp seedCalculator) {
        return toHex(seedCalculator.calculateSeed(mnemonic, passphrase));
    }
}