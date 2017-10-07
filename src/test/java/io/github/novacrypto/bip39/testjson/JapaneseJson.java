package io.github.novacrypto.bip39.testjson;

import io.github.novacrypto.bip39.Resources;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-07.
 */
public class JapaneseJson {
    public JapaneseJsonTestCase[] data;

    public static JapaneseJson load() {
        final JapaneseJson data = Resources.loadJsonResource("bip39_japanese_test_vectors.json", JapaneseJson.class);
        assertEquals(24, data.data.length);
        return data;
    }
}
