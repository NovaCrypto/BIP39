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

package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.French;
import io.github.novacrypto.bip39.wordlists.Japanese;
import io.github.novacrypto.bip39.wordlists.Spanish;
import org.junit.Test;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;

import static io.github.novacrypto.TestCharSequence.preventToStringAndSubSequence;
import static org.junit.Assert.*;

public final class WordListMapNormalizationTests {

    @Test
    public void given_WordList_and_get_normalized_form_returns_same_instance_twice() {
        final String word = Japanese.INSTANCE.getWord(2);
        assertWordIsNotNormalized(word);
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        final String word1 = map.normalize(word);
        final String word2 = map.normalize(word);
        assertWordIsNormalized(word1);
        assertSame(word1, word2);
    }

    @Test
    public void all_words_in_WordList_are_cached() {
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        for (int i = 0; i < 2048; i++) {
            final String word = Japanese.INSTANCE.getWord(i);
            final String word1 = map.normalize(word);
            final String word2 = map.normalize(word);
            assertWordIsNormalized(word1);
            assertSame(word1, word2);
        }
    }

    @Test
    public void all_normalized_words_in_WordList_are_cached() {
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        for (int i = 0; i < 2048; i++) {
            final String word = map.normalize(Japanese.INSTANCE.getWord(i));
            final String word1 = map.normalize(word);
            final String word2 = map.normalize(word);
            assertWordIsNormalized(word1);
            assertSame(word1, word2);
        }
    }

    @Test
    public void all_un_normalized_words_in_WordList_are_cached() {
        for (WordList wordList : Arrays.asList(Japanese.INSTANCE, English.INSTANCE, French.INSTANCE, Spanish.INSTANCE)) {
            final WordListMapNormalization map = new WordListMapNormalization(wordList);
            for (int i = 0; i < 2048; i++) {
                final String originalWord = wordList.getWord(i);
                final String nfcWord = Normalizer.normalize(originalWord, Normalizer.Form.NFC);
                final String nfkcWord = Normalizer.normalize(originalWord, Normalizer.Form.NFKC);
                final String nfkdWord = Normalizer.normalize(originalWord, Normalizer.Form.NFKD);
                final String word1 = map.normalize(nfcWord);
                final String word2 = map.normalize(nfkcWord);
                final String word3 = map.normalize(nfkdWord);
                assertWordIsNormalized(word1);
                assertSame(word1, word2);
                assertSame(word1, word3);
            }
        }
    }

    @Test
    public void English_returns_same_word() {
        final WordListMapNormalization map = new WordListMapNormalization(English.INSTANCE);
        for (int i = 0; i < 2048; i++) {
            final String word = English.INSTANCE.getWord(i);
            final String word1 = map.normalize(word);
            assertWordIsNormalized(word1);
            assertSame(word1, word);
        }
    }

    @Test
    public void given_WordList_and_get_normalized_form_of_word_off_WordList_returns_different_instances() {
        final String word = Japanese.INSTANCE.getWord(2) + "X";
        assertWordIsNotNormalized(word);
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        final String word1 = map.normalize(word);
        final String word2 = map.normalize(word);
        assertWordIsNormalized(word1);
        assertWordIsNormalized(word2);
        assertNotSame(word1, word2);
        assertEquals(word1, Normalizer.normalize(word, Normalizer.Form.NFKD));
    }

    @Test
    public void does_not_call_to_string_when_in_the_dictionary() {
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        final String word = Japanese.INSTANCE.getWord(51);
        assertWordIsNotNormalized(word);
        final CharSequence wordAsSecureSequence = preventToStringAndSubSequence(word);
        final String word1 = map.normalize(wordAsSecureSequence);
        assertWordIsNormalized(word1);
        final String word2 = map.normalize(wordAsSecureSequence);
        assertSame(word1, word2);
    }

    /**
     * This works because the split creates char sequences with 0 hashcode
     */
    @Test
    public void a_fresh_char_sequence_from_a_split_still_does_not_need_to_to_string() {
        final WordListMapNormalization map = new WordListMapNormalization(Japanese.INSTANCE);
        final String word2 = Japanese.INSTANCE.getWord(2);
        final String word51 = Japanese.INSTANCE.getWord(51);
        final String sentence = word2 + Japanese.INSTANCE.getSpace() + word51;
        final List<CharSequence> split = new CharSequenceSplitter(' ', Japanese.INSTANCE.getSpace()).split(sentence);
        assertNotSame(split.get(0), word2);
        assertNotSame(split.get(1), word51);
        assertSame(map.normalize(word2), map.normalize(split.get(0)));
        assertSame(map.normalize(word51), map.normalize(split.get(1)));
        assertSame(map.normalize(word2), map.normalize(preventToStringAndSubSequence(split.get(0))));
        assertSame(map.normalize(word51), map.normalize(preventToStringAndSubSequence(split.get(1))));
    }

    private static void assertWordIsNotNormalized(String word) {
        assertFalse(isNormalized(word));
    }

    private static void assertWordIsNormalized(String word) {
        assertTrue(isNormalized(word));
    }

    private static boolean isNormalized(String word) {
        return Normalizer.isNormalized(word, Normalizer.Form.NFKD);
    }
}