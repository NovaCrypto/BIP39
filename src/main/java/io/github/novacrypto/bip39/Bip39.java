package io.github.novacrypto.bip39;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.Arrays;

/**
 * Created by aevans on 2017-10-05.
 */
public final class Bip39 {

    private static final byte[] fixedSalt = getUtf8Bytes("mnemonic");
    private static SecretKeyFactory skf = getPbkdf2WithHmacSHA512();

    private static SecretKeyFactory getPbkdf2WithHmacSHA512() {
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getUtf8Bytes(final String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String bip39Seed(String mnemonic, String passphrase) throws Exception {
        mnemonic = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD);
        passphrase = Normalizer.normalize(passphrase, Normalizer.Form.NFKD);

        final char[] chars = mnemonic.toCharArray();
        final byte[] salt2 = getUtf8Bytes(passphrase);
        final byte[] salt = combine(fixedSalt, salt2);
        clear(salt2);
        final PBEKeySpec spec = new PBEKeySpec(chars, salt, 2048, 512);
        Arrays.fill(chars, '\0');
        clear(salt);

        final byte[] hash = skf.generateSecret(spec).getEncoded();
        spec.clearPassword();
        return toHex(hash);
    }

    private static byte[] combine(byte[] array1, byte[] array2) {
        final byte[] bytes = new byte[array1.length + array2.length];
        for (int i = 0; i < array1.length; i++) {
            bytes[i] = array1[i];
        }
        for (int i = array1.length; i < bytes.length; i++) {
            bytes[i] = array2[i - array1.length];
        }
        return bytes;
    }

    private static void clear(byte[] salt) {
        Arrays.fill(salt, (byte) 0);
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

}
