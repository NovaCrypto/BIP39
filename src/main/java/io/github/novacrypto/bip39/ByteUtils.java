package io.github.novacrypto.bip39;

/**
 * Created by aevans on 2017-10-07.
 */
final class ByteUtils {

    private ByteUtils() {
    }

    static int next11Bits(byte[] bytes, int offset) {
        //what's the index of the first byte
        final int skip = offset / 8;

        //how many bits will we need to drop from
        // the end of a 3 byte word?
        final int lowerBitsToRemove = (3 * 8 - 11) - (offset % 8);

        //get the first byte, the 0xff trick is
        // due to Java not having unsigned types
        final int b1 = (int) bytes[skip] & 0xff;

        //second byte
        final int b2 = (int) bytes[skip + 1] & 0xff;

        //third byte, but only if we need it,
        // or we might go out of bounds
        final int b3 = lowerBitsToRemove < 8
                ? ((int) bytes[skip + 2] & 0xff)
                : 0;

        //build up a 3 byte word from the three bytes
        final int firstThreeBytes = b1 << 16 | b2 << 8 | b3;

        //this mask is fixed, it's to keep the last 11 bits
        final int mask = (1 << 11) - 1;

        //drop the last n bits and apply the
        // mask to loose the upper bits
        return firstThreeBytes >> lowerBitsToRemove & mask;
    }
}
