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

import java.security.MessageDigest;
import java.text.Normalizer;

import static io.github.novacrypto.Hex.toHex;
import static io.github.novacrypto.toruntime.CheckedExceptionToRuntime.toRuntime;

final class WordListHashing {

    static final int WORD_COUNT = 2048;

    static String hashWordList(final WordList wordList) {
        final MessageDigest digest = toRuntime(() -> MessageDigest.getInstance("SHA-256"));
        for (int i = 0; i < WORD_COUNT; i++) {
            digest.update((wordList.getWord(i) + "\n").getBytes());
        }
        digest.update(("" + wordList.getSpace()).getBytes());
        return toHex(digest.digest());
    }

    static String hashWordListNormalized(final WordList wordList) {
        return hashWordList(normalizeNFKD(wordList));
    }

    private static WordList normalizeNFKD(WordList wordList) {
        return new WordList() {
            @Override
            public String getWord(int index) {
                return Normalizer.normalize(wordList.getWord(index), Normalizer.Form.NFKD);
            }

            @Override
            public char getSpace() {
                return wordList.getSpace();
            }
        };
    }
}