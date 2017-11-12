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

import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.WordList;
import io.github.novacrypto.bip39.wordlists.English;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class MnemonicGenerationWordCountTests {

    @Test
    public void twelveWordsBitLength() throws Exception {
        assertEquals(128, Words.TWELVE.bitLength());
    }

    @Test
    public void twelveWords() throws Exception {
        assertEquals(12, countWords(Words.TWELVE));
    }

    @Test
    public void fifteenWordsBitLength() throws Exception {
        assertEquals(160, Words.FIFTEEN.bitLength());
    }

    @Test
    public void fifteenWords() throws Exception {
        assertEquals(15, countWords(Words.FIFTEEN));
    }

    @Test
    public void eighteenWordsBitLength() throws Exception {
        assertEquals(192, Words.EIGHTEEN.bitLength());
    }

    @Test
    public void eighteenWords() throws Exception {
        assertEquals(18, countWords(Words.EIGHTEEN));
    }

    @Test
    public void twentyOneWordsBitLength() throws Exception {
        assertEquals(224, Words.TWENTY_ONE.bitLength());
    }

    @Test
    public void twentyOneWords() throws Exception {
        assertEquals(21, countWords(Words.TWENTY_ONE));
    }

    @Test
    public void twentyFourWordsBitLength() throws Exception {
        assertEquals(256, Words.TWENTY_FOUR.bitLength());
    }

    @Test
    public void twentyFourWords() throws Exception {
        assertEquals(24, countWords(Words.TWENTY_FOUR));
    }

    private static int countWords(Words words) {
        return createMnemonic(new byte[words.byteLength()], English.INSTANCE)
                .split("" + English.INSTANCE.getSpace()).length;
    }

    private static String createMnemonic(byte[] f, WordList wordList) {
        final StringBuilder sb = new StringBuilder();
        new MnemonicGenerator(wordList)
                .createMnemonic(f, sb::append);
        return sb.toString();
    }
}