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

import org.junit.Test;

import static io.github.novacrypto.bip39.ByteUtils.next11Bits;
import static org.junit.Assert.assertEquals;

public final class ByteUtilTests {

    @Test
    public void forCodeCoverageOnly_create() {
        new ByteUtils();
    }

    @Test
    public void take11Bits() {
        byte[] bytes = new byte[]{(byte) 0b11111111, (byte) 0b11101111, 0b01100111, 0};
        assertEquals(0b11111111111, next11Bits(bytes, 0));
        assertEquals(0b11111111110, next11Bits(bytes, 1));
        assertEquals(0b11101111011, next11Bits(bytes, 8));
        assertEquals(0b11011110110, next11Bits(bytes, 9));
        assertEquals(0b10111101100, next11Bits(bytes, 10));
        assertEquals(0b01111011001, next11Bits(bytes, 11));
        assertEquals(0b01100111000, next11Bits(bytes, 16));
    }

    @Test
    public void take11Bits7F() {
        byte[] bytes = new byte[]{0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f, 0x7f};
        assertEquals(0b01111111011, next11Bits(bytes, 0));
        assertEquals(0b11111110111, next11Bits(bytes, 1));
        assertEquals(0b11111101111, next11Bits(bytes, 2));
        assertEquals(0b11111011111, next11Bits(bytes, 3));
        assertEquals(0b11110111111, next11Bits(bytes, 4));
        assertEquals(0b11101111111, next11Bits(bytes, 5));
        assertEquals(0b11011111110, next11Bits(bytes, 6));
        assertEquals(0b10111111101, next11Bits(bytes, 7));
        assertEquals(0b01111111011, next11Bits(bytes, 8));
        assertEquals(0b11111110111, next11Bits(bytes, 9));
    }

    @Test
    public void take11Bits80() {
        byte[] bytes = new byte[]{(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80};
        assertEquals(0b10000000100, next11Bits(bytes, 0));
        assertEquals(0b00000001000, next11Bits(bytes, 1));
        assertEquals(0b00000010000, next11Bits(bytes, 2));
        assertEquals(0b00000100000, next11Bits(bytes, 3));
        assertEquals(0b00001000000, next11Bits(bytes, 4));
        assertEquals(0b00010000000, next11Bits(bytes, 5));
        assertEquals(0b00100000001, next11Bits(bytes, 6));
        assertEquals(0b01000000010, next11Bits(bytes, 7));
        assertEquals(0b10000000100, next11Bits(bytes, 8));
        assertEquals(0b00000001000, next11Bits(bytes, 9));
    }
}
