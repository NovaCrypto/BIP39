package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import org.junit.Test;

import java.math.BigInteger;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-05.
 */
public final class SeedGenerationTests {

    @Test
    public void bip39_english() throws Exception {
        assertEquals("2eea1e4d099089606b7678809be6090ccba0fca171d4ed42c550194ca8e3600cd1e5989dcca38e5f903f5c358c92e0dcaffc9e71a48ad489bb868025c907d1e1",
                bip39SeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice"));
    }

    @Test
    public void bip39_english_with_passphrase() throws Exception {
        assertEquals("36732d826f4fa483b5fe8373ef8d6aa3cb9c8fb30463d6c0063ee248afca2f87d11ebe6e75c2fb2736435994b868f8e9d4f4474c65ee05ac47aad7ef8a497846",
                bip39SeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "CryptoIsCool"));
    }

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[2], bip39SeedHex(testCase[1], "TREZOR"));
        }
    }

    @Test
    public void passphrase_normalization() throws Exception {
        assertEquals(bip39SeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "ｶ"),
                bip39SeedHex("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "カ"));
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            assertEquals(testCase.seed, bip39SeedHex(testCase.mnemonic, testCase.passphrase));
        }
    }

    private static String bip39SeedHex(final String mnemonic) {
        return bip39SeedHex(mnemonic, "");
    }

    private static String bip39SeedHex(String mnemonic, String passphrase) {
        return toHex(Bip39.getSeed(mnemonic, passphrase));
    }

    private static String toHex(byte[] array) {
        final BigInteger bi = new BigInteger(1, array);
        final String hex = bi.toString(16);
        final int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
}
