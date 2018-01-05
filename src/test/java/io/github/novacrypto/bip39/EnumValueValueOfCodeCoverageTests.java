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

package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.French;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

public final class EnumValueValueOfCodeCoverageTests {

    private static void superficialEnumCodeCoverage(Class<? extends Enum<?>> enumClass) throws Exception {
        for (Object o : (Object[]) enumClass.getMethod("values").invoke(null)) {
            enumClass.getMethod("valueOf", String.class).invoke(null, o.toString());
        }
    }

    @Test
    public void forCodeCoverageOnly_allEnums() throws Exception {
        superficialEnumCodeCoverage(English.class);
        superficialEnumCodeCoverage(Japanese.class);
        superficialEnumCodeCoverage(French.class);
        superficialEnumCodeCoverage(CharSequenceComparators.class);
        superficialEnumCodeCoverage(SpongyCastlePBKDF2WithHmacSHA512.class);
        superficialEnumCodeCoverage(JavaxPBKDF2WithHmacSHA512.class);
        superficialEnumCodeCoverage(Words.class);
    }
}