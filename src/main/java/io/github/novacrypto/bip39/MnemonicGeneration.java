package io.github.novacrypto.bip39;

import java.security.MessageDigest;
import java.util.Arrays;

import static io.github.novacrypto.bip39.ByteUtils.next11Bits;

/**
 * Created by aevans on 2017-10-07.
 */
public final class MnemonicGeneration {

    public static String createMnemonic(final String entropyHex, WordList instance) throws Exception {
        final int byteSize = entropyHex.length() / 2;
        final byte[] bytes = new byte[byteSize];
        for (int i = 0; i < byteSize; i++) {
            bytes[i] = (byte) Integer.parseInt(entropyHex.substring(i * 2, i * 2 + 2), 16);
        }
        final int ent = entropyHex.length() * 4;
        if (ent < 128)
            throw new RuntimeException("Entropy too low 128-256 bits allowed");
        if (ent > 256)
            throw new RuntimeException("Entropy too high 128-256 bits allowed");
        if (ent % 32 > 0)
            throw new RuntimeException("Number of entropy bits must be divisible by 32");

        final int cs = ent / 32;
        final int ms = (ent + cs) / 11;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hash = digest.digest(bytes);
        final byte top = hash[0];
        byte toKeep = (byte) (top >>> (8 - cs));
        final byte[] newBytes = Arrays.copyOf(bytes, bytes.length + 1);
        newBytes[bytes.length] = top;

        final int[] wordIndexes = new int[ms];
        for (int i = 0, wi = 0; wi < ms; i += 11, wi++) {
            wordIndexes[wi] = next11Bits(newBytes, i);
        }

        StringBuilder sb = new StringBuilder();
        for (int word : wordIndexes) {
            sb.append(instance.getWord(word));
            sb.append(instance.getSpace());
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

}
