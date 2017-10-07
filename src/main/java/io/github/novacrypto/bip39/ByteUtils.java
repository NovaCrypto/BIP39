package io.github.novacrypto.bip39;

/**
 * Created by aevans on 2017-10-07.
 */
final class ByteUtils {

    private ByteUtils() {
    }

    static int next11Bits(byte[] bytes, int i) {
        final int skip = i / 8;
        final int lowerBitsToRemove = (24 - 11) - (i % 8);
        final int b1 = ((int) bytes[skip] & 0xff) << 16;
        final int b2 = ((int) bytes[skip + 1] & 0xff) << 8;
        final int b3 = lowerBitsToRemove < 8 ? ((int) bytes[skip + 2] & 0xff) : 0;
        final int firstThreeBytes = b1 | b2 | b3;
        final int mask = (1 << 11) - 1;
        return firstThreeBytes >>> lowerBitsToRemove & mask;
    }
}
