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

package io.github.novacrypto.wordlists;

import io.github.novacrypto.bip39.WordList;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

import static io.github.novacrypto.wordlists.WordListHashing.WORD_COUNT;
import static org.junit.Assert.assertEquals;

public final class JapaneseListContentTests {

    private final WordList wordList = Japanese.INSTANCE;

    @Test
    public void hashCheck() {
        assertEquals("2f61e05f096d93378a25071de9238ef2ce8d12d773a75640a3a881797e9e2148",
                WordListHashing.hashWordList(wordList));
    }

    @Test
    public void normalizedHashCheck() {
        assertEquals("b20ee3499703a2a0e02ba886edc61363ce380989a8212aaf1866e5bdc6b60c61",
                WordListHashing.hashWordListNormalized(wordList));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void correctNumberOfWords() {
        wordList.getWord(WORD_COUNT + 1);
    }
}