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

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class SeedCalculatorByWordListLookUp {
    private final SeedCalculator seedCalculator;
    private final Map<CharSequence, char[]> map = new HashMap<>();

    SeedCalculatorByWordListLookUp(final SeedCalculator seedCalculator, final WordList wordList) {
        this.seedCalculator = seedCalculator;
        for (int i = 0; i < 1 << 11; i++) {
            final String word = wordList.getWord(i);
            map.put(word, Normalizer.normalize(word, Normalizer.Form.NFKD).toCharArray());
        }
    }

    /**
     * Calculate the seed given a mnemonic and corresponding passphrase.
     * The phrase is not checked for validity here, for that use a {@link MnemonicValidator}.
     * The word supplied need to be exactly the same as the ones in the word list, if they are not a
     * {@link WordNotExactlyAsInSuppliedWordList} will be thrown.
     * <p>
     * The purpose of this method is to avoid constructing a mnemonic String if you have gathered a list of
     * words from the user and also to avoid having to normalize it, all words in the {@link WordList} are normalized
     * instead.
     * <p>
     * Due to normalization, the passphrase still needs to be {@link String}, and not {@link CharSequence}, this is an
     * open issue: https://github.com/NovaCrypto/BIP39/issues/7
     *
     * @param mnemonicFromWordList The memorable list of words, selected from the word list that was supplied while
     *                             creating this object.
     * @param passphrase           An optional passphrase, use "" if not required
     * @return a seed for HD wallet generation
     */
    public byte[] calculateSeed(final Collection<? extends CharSequence> mnemonicFromWordList, final String passphrase) {
        final int words = mnemonicFromWordList.size();
        final char[][] chars = new char[words][];
        int count = 0;
        int wordIndex = 0;
        for (final CharSequence sequence : mnemonicFromWordList) {
            final char[] wordChars = map.get(sequence);
            if (wordChars == null) {
                throw new WordNotExactlyAsInSuppliedWordList();
            }
            chars[wordIndex++] = wordChars;
            count += wordChars.length;
        }
        count += words - 1;
        final char[] mnemonicChars = new char[count];
        try {
            int index = 0;
            for (int i = 0; i < chars.length; i++) {
                System.arraycopy(chars[i], 0, mnemonicChars, index, chars[i].length);
                index += chars[i].length;
                if (i < chars.length - 1) {
                    mnemonicChars[index++] = ' ';
                }
            }
            return seedCalculator.calculateSeed(mnemonicChars, passphrase);
        } finally {
            Arrays.fill(mnemonicChars, '\0');
            Arrays.fill(chars, null);
        }
    }
}
