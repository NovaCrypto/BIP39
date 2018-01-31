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

import io.github.novacrypto.bip39.Validation.InvalidChecksumException;
import io.github.novacrypto.bip39.Validation.InvalidWordCountException;
import io.github.novacrypto.bip39.Validation.UnexpectedWhiteSpaceException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ValidationExceptionMessagesTests {

    @Test
    public void InvalidWordCountException_message() throws Exception {
        assertEquals("Not a correct number of words", new InvalidWordCountException().getMessage());
    }

    @Test
    public void InvalidChecksumException_message() throws Exception {
        assertEquals("Invalid checksum", new InvalidChecksumException().getMessage());
    }

    @Test
    public void UnexpectedWhiteSpaceException_message() throws Exception {
        assertEquals("Unexpected whitespace", new UnexpectedWhiteSpaceException().getMessage());
    }

}