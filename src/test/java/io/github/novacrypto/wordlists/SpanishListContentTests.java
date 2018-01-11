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
import io.github.novacrypto.bip39.wordlists.Spanish;
import org.junit.Test;

import static io.github.novacrypto.wordlists.WordListHashing.WORD_COUNT;
import static org.junit.Assert.assertEquals;

public final class SpanishListContentTests {

    private final WordList wordList = Spanish.INSTANCE;

    @Test
    public void hashCheck() {
        assertEquals("134e8bfaf106863a7f10c04bdf922e15bbce43c30c6558c8537199d7c09ea0b2",
                WordListHashing.hashWordList(wordList));
    }

    @Test
    public void normalizedHashCheck() {
        assertEquals("134e8bfaf106863a7f10c04bdf922e15bbce43c30c6558c8537199d7c09ea0b2",
                WordListHashing.hashWordListNormalized(wordList));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void correctNumberOfWords() {
        wordList.getWord(WORD_COUNT + 1);
    }
}