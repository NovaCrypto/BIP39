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

package io.github.novacrypto;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.WordList;
import io.github.novacrypto.bip39.wordlists.Spanish;
import io.github.novacrypto.testjson.EnglishJson;
import io.github.novacrypto.testjson.TestVector;
import io.github.novacrypto.testjson.TestVectorJson;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.French;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

public final class MnemonicGenerationTests {

    private static String createMnemonic(String f, WordList wordList) {
        final StringBuilder sb = new StringBuilder();
        new MnemonicGenerator(wordList)
                .createMnemonic(f, sb::append);
        return sb.toString();
    }

    private static String createMnemonic(byte[] f, WordList wordList) {
        final StringBuilder sb = new StringBuilder();
        new MnemonicGenerator(wordList)
                .createMnemonic(f, sb::append);
        return sb.toString();
    }

    @Test
    public void tooSmallEntropy() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(repeatString(30, "f"), English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Entropy too low, 128-256 bits allowed");
    }

    @Test
    public void tooSmallEntropyBytes() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(new byte[15], English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Entropy too low, 128-256 bits allowed");
    }

    @Test
    public void tooLargeEntropy() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(repeatString(66, "f"), English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Entropy too high, 128-256 bits allowed");
    }

    @Test
    public void tooLargeEntropyBytes() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(new byte[33], English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Entropy too high, 128-256 bits allowed");
    }

    @Test
    public void nonMultipleOf32() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(repeatString(34, "f"), English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Number of entropy bits must be divisible by 32");
    }

    @Test
    public void notHexPairs() throws Exception {
        assertThatThrownBy(
                () -> createMnemonic(repeatString(33, "f"), English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Length of hex chars must be divisible by 2");
    }

    @Test
    public void sevenFRepeated() throws Exception {
        assertEquals("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will",
                createMnemonic("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f", English.INSTANCE)
        );
    }

    @Test
    public void eightZeroRepeated() throws Exception {
        assertEquals("letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
                createMnemonic("80808080808080808080808080808080", English.INSTANCE)
        );
    }

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[1], createMnemonic(testCase[0], English.INSTANCE));
        }
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final TestVectorJson data = TestVectorJson.loadJapanese();
        for (final TestVector testVector : data.vectors) {
            assertEquals(testVector.mnemonic, createMnemonic(testVector.entropy, Japanese.INSTANCE));
        }
    }

    @Test
    public void all_french_test_vectors() throws Exception {
        final TestVectorJson data = TestVectorJson.loadFrench();
        for (final TestVector testVector : data.vectors) {
            assertEquals(testVector.mnemonic, createMnemonic(testVector.entropy, French.INSTANCE));
        }
    }

    @Test
    public void all_spanish_test_vectors() throws Exception {
        final TestVectorJson data = TestVectorJson.loadSpanish();
        for (final TestVector testVector : data.vectors) {
            assertEquals(testVector.mnemonic, createMnemonic(testVector.entropy, Spanish.INSTANCE));
        }
    }

    @Test
    public void upper_and_lower_case_hex_handled_the_same() throws Exception {
        final String hex = "0123456789abcdef0123456789abcdef";
        assertEquals(createMnemonic(hex, English.INSTANCE),
                createMnemonic(hex.toUpperCase(), English.INSTANCE));
    }

    @Test
    public void bad_hex_throws_g() throws Exception {
        final String hex = "0123456789abcdef0123456789abcdeg";
        assertThatThrownBy(
                () -> createMnemonic(hex, English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid hex char 'g'");
    }

    @Test
    public void bad_hex_throws_Z() throws Exception {
        final String hex = "0123456789abcdef0123456789abcdeZ";
        assertThatThrownBy(
                () -> createMnemonic(hex, English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid hex char 'Z'");
    }

    @Test
    public void bad_hex_throws_space() throws Exception {
        final String hex = "0123456789 abcdef0123456789abcde";
        assertThatThrownBy(
                () -> createMnemonic(hex, English.INSTANCE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid hex char ' '");
    }

    @Test
    public void forFinallyCodeCoverage_createMnemonicWhenTargetThrowsException() throws Exception {
        assertThatThrownBy(
                () -> new MnemonicGenerator(English.INSTANCE)
                        .createMnemonic(repeatString(32, "f"),
                                (s) -> {
                                    throw new OutOfMemoryError();
                                }))
                .isInstanceOf(OutOfMemoryError.class);
    }

    private static String repeatString(int n, String repeat) {
        return new String(new char[n]).replace("\0", repeat);
    }
}
