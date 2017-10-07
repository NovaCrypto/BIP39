package io.github.novacrypto.bip39;

import org.junit.Test;

import static io.github.novacrypto.bip39.ByteUtils.next11Bits;
import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-07.
 */
public final class ByteUtilTests {

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
