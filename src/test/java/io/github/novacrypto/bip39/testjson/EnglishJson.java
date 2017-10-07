package io.github.novacrypto.bip39.testjson;

import io.github.novacrypto.bip39.Resources;

import static org.junit.Assert.assertEquals;

/**
 * Created by aevans on 2017-10-07.
 */
public final class EnglishJson {
    public String[][] english;

    public static EnglishJson load() {
        final EnglishJson data = Resources.loadJsonResource("bip39_english_test_vectors.json", EnglishJson.class);
        assertEquals(24, data.english.length);
        return data;
    }
}
