package io.github.novacrypto.bip39;

import java.security.MessageDigest;
import java.util.Arrays;

import static io.github.novacrypto.bip39.ByteUtils.next11Bits;

/**
 * Created by aevans on 2017-10-07.
 */
public final class MnemonicGeneration {

    public static String createMnemonic(final String entropyHex, WordList instance) throws Exception {
        final int byteCount = entropyHex.length() / 2;
        final byte[] entropy = new byte[byteCount];
        for (int i = 0; i < byteCount; i++) {
            entropy[i] = (byte) Integer.parseInt(entropyHex.substring(i * 2, i * 2 + 2), 16);
        }
        final int ent = entropy.length * 8;
        if (ent < 128)
            throw new RuntimeException("Entropy too low 128-256 bits allowed");
        if (ent > 256)
            throw new RuntimeException("Entropy too high 128-256 bits allowed");
        if (ent % 32 > 0)
            throw new RuntimeException("Number of entropy bits must be divisible by 32");

        final byte[] entropyWithChecksum = Arrays.copyOf(entropy, entropy.length + 1);
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        final byte[] hash = digest.digest(entropy);
        entropyWithChecksum[entropy.length] = hash[0];

        //checksum length
        final int cs = ent / 32;
        //mnemonic length
        final int ms = (ent + cs) / 11;

        //get the indexes into the word list
        final int[] wordIndexes = new int[ms];
        for (int i = 0, wi = 0; wi < ms; i += 11, wi++) {
            wordIndexes[wi] = next11Bits(entropyWithChecksum, i);
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
