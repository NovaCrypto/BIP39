package io.github.novacrypto.bip39;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Created by aevans on 2017-10-05.
 */
public final class Resources {

    private Resources() {
    }

    public static <T> T loadJsonResource(String resourceName, Class<T> classOfT) {
        try {
            try (final InputStreamReader in = new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream(resourceName))) {
                final String json = new BufferedReader(in).lines().collect(Collectors.joining("\n"));
                return new Gson().fromJson(json, classOfT);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
