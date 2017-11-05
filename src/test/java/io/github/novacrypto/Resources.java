/*
 *  BIP39 library, a Java implementation of BIP39
 *  Copyright (C) 2017 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP39
 *  You can contact the authors via github issues.
 */

package io.github.novacrypto;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

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
