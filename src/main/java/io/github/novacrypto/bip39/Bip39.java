package io.github.novacrypto.bip39;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.Normalizer;
import java.util.Arrays;

/**
 * Created by aevans on 2017-10-05.
 */
public final class Bip39 {

    private static final byte[] fixedSalt = getUtf8Bytes("mnemonic");
    private static SecretKeyFactory skf = getPbkdf2WithHmacSHA512();


    public static byte[] getSeed(String mnemonic, String passphrase) {
        mnemonic = Normalizer.normalize(mnemonic, Normalizer.Form.NFKD);
        passphrase = Normalizer.normalize(passphrase, Normalizer.Form.NFKD);

        final char[] chars = mnemonic.toCharArray();
        final byte[] salt2 = getUtf8Bytes(passphrase);
        final byte[] salt = combine(fixedSalt, salt2);
        clear(salt2);
        final PBEKeySpec spec = new PBEKeySpec(chars, salt, 2048, 512);
        Arrays.fill(chars, '\0');
        clear(salt);

        try {
            return skf.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        } finally {
            spec.clearPassword();
        }
    }

    private static byte[] combine(byte[] array1, byte[] array2) {
        final byte[] bytes = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, bytes, 0, array1.length);
        System.arraycopy(array2, 0, bytes, array1.length, bytes.length - array1.length);
        return bytes;
    }

    private static void clear(byte[] salt) {
        Arrays.fill(salt, (byte) 0);
    }

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

}
