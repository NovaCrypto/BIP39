package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-05.
 */
public final class Bip39SeedGenerationTests {

    @Test
    public void bip39_english() throws Exception {
        assertEquals("2eea1e4d099089606b7678809be6090ccba0fca171d4ed42c550194ca8e3600cd1e5989dcca38e5f903f5c358c92e0dcaffc9e71a48ad489bb868025c907d1e1",
                bip39Seed("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice"));
    }

    @Test
    public void bip39_english_with_passphrase() throws Exception {
        assertEquals("36732d826f4fa483b5fe8373ef8d6aa3cb9c8fb30463d6c0063ee248afca2f87d11ebe6e75c2fb2736435994b868f8e9d4f4474c65ee05ac47aad7ef8a497846",
                bip39Seed("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "CryptoIsCool"));
    }

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[2], bip39Seed(testCase[1], "TREZOR"));
        }
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            assertEquals(testCase.seed, bip39Seed(testCase.mnemonic, testCase.passphrase));
        }
    }

    private static String bip39Seed(final String mnemonic) throws Exception {
        return bip39Seed(mnemonic, "");
    }

    private static String bip39Seed(String mnemonic, String passphrase) throws Exception {
        return Bip39.bip39Seed(mnemonic, passphrase);
    }
}
