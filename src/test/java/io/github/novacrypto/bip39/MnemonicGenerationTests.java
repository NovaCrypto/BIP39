package io.github.novacrypto.bip39;

import io.github.novacrypto.bip39.testjson.EnglishJson;
import io.github.novacrypto.bip39.testjson.JapaneseJson;
import io.github.novacrypto.bip39.testjson.JapaneseJsonTestCase;
import io.github.novacrypto.bip39.wordlists.English;
import io.github.novacrypto.bip39.wordlists.Japanese;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-05.
 */
public final class MnemonicGenerationTests {

    private static String createMnemonic(String f, WordList instance) {
        final StringBuilder sb = new StringBuilder();
        MnemonicGeneration.createMnemonic(f, instance, sb::append);
        return sb.toString();
    }

    @Test(expected = RuntimeException.class)
    public void tooSmallEntropy() throws Exception {
        createMnemonic(repeatString(31, "f"), English.INSTANCE);
    }

    @Test(expected = RuntimeException.class)
    public void tooLargeEntropy() throws Exception {
        createMnemonic(repeatString(66, "f"), English.INSTANCE);
    }

    @Test(expected = RuntimeException.class)
    public void nonMultipleOf32() throws Exception {
        createMnemonic(repeatString(34, "f"), English.INSTANCE);
    }

    @Test(expected = RuntimeException.class)
    public void notHexPairs() throws Exception {
        createMnemonic(repeatString(33, "f"), English.INSTANCE);
    }

    @Test
    public void sevenFRepeated() throws Exception {
        assertEquals("legal winner thank year wave sausage worth useful legal winner thank year wave sausage worth useful legal will",
                createMnemonic("7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f7f", English.INSTANCE)
        );
    }

    @Test
    public void eightZeroRepeated() throws Exception {
        assertEquals("letter advice cage absurd amount doctor acoustic avoid letter advice cage above",
                createMnemonic("80808080808080808080808080808080", English.INSTANCE)
        );
    }

    @Test
    public void all_english_test_vectors() throws Exception {
        final EnglishJson data = EnglishJson.load();
        for (final String[] testCase : data.english) {
            assertEquals(testCase[1], createMnemonic(testCase[0], English.INSTANCE));
        }
    }

    @Test
    public void all_japanese_test_vectors() throws Exception {
        final JapaneseJson data = JapaneseJson.load();
        for (final JapaneseJsonTestCase testCase : data.data) {
            assertEquals(testCase.mnemonic, createMnemonic(testCase.entropy, Japanese.INSTANCE));
        }
    }

    private static String repeatString(int n, String repeat) {
        return new String(new char[n]).replace("\0", repeat);
    }
}
