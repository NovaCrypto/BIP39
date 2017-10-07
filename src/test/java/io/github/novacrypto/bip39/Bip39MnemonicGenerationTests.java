package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.Japanese;
import io.github.novacrypto.bip39.wordlists.WordList;
import org.junit.Test;

import java.security.MessageDigest;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-05.
 */
public final class Bip39MnemonicGenerationTests {

//    @Test
//    public void bip39_english() throws Exception {
//        assertEquals("2eea1e4d099089606b7678809be6090ccba0fca171d4ed42c550194ca8e3600cd1e5989dcca38e5f903f5c358c92e0dcaffc9e71a48ad489bb868025c907d1e1",
//                bip39Seed("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice"));
//    }
//
//    @Test
//    public void bip39_english_with_passphrase() throws Exception {
//        assertEquals("36732d826f4fa483b5fe8373ef8d6aa3cb9c8fb30463d6c0063ee248afca2f87d11ebe6e75c2fb2736435994b868f8e9d4f4474c65ee05ac47aad7ef8a497846",
//                bip39Seed("solar puppy hawk oxygen trip brief erase slot fossil mechanic filter voice", "CryptoIsCool"));
//    }

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[1], bip39Mnemonic(testCase[0], English.INSTANCE));
        }
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            assertEquals(testCase.mnemonic, bip39Mnemonic(testCase.entropy, Japanese.INSTANCE));
        }
    }

    @Test
    public void sevenFRepeated() throws Exception {
        assertEquals("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will",
                bip39Mnemonic("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f", English.INSTANCE)
        );
    }

    @Test
    public void eightZeroRepeated() throws Exception {
        assertEquals("letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
                bip39Mnemonic("80808080808080808080808080808080", English.INSTANCE)
        );
    }

//    @Test
//    public void all_japanese_test_vectors() throws Exception {
//        final JapaneseJson data = JapaneseJson.load();
//        for (final JapaneseJsonTestCase testCase : data.data) {
//            assertEquals(testCase.seed, bip39Seed(testCase.mnemonic, testCase.passphrase));
//        }
//    }

    @Test(expected = RuntimeException.class)
    public void tooSmallEntropy() throws Exception {
        bip39Mnemonic(repeatString(31, "f"), English.INSTANCE);
    }

    @Test(expected = RuntimeException.class)
    public void tooLargeEntropy() throws Exception {
        bip39Mnemonic(repeatString(65, "f"), English.INSTANCE);
    }

    @Test(expected = RuntimeException.class)
    public void nonMultipleOf32() throws Exception {
        bip39Mnemonic(repeatString(33, "f"), English.INSTANCE);
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

    private static int next11Bits(byte[] bytes, int i) {
        final int skip = i / 8;
        final int lowerBitsToRemove = (24 - 11) - (i % 8);
        final int b1 = ((int) bytes[skip] & 0xff) << 16;
        final int b2 = ((int) bytes[skip + 1] & 0xff) << 8;
        final int b3 = lowerBitsToRemove < 8 ? ((int) bytes[skip + 2] & 0xff) : 0;
        final int firstThreeBytes = b1 | b2 | b3;
        final int mask = (1 << 11) - 1;
        return firstThreeBytes >>> lowerBitsToRemove & mask;
    }

    private static String bip39Mnemonic(final String entropyHex, WordList instance) throws Exception {
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

    private static String bip39Mnemonic(String mnemonic, String passphrase) throws Exception {
        return Bip39.bip39Seed(mnemonic, passphrase);
    }

    private static String repeatString(int n, String repeat) {
        return new String(new char[n]).replace("\0", repeat);
    }
}
