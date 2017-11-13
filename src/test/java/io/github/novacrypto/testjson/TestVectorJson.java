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

package io.github.novacrypto.testjson;

import com.google.gson.annotations.SerializedName;
import io.github.novacrypto.Resources;

import static org.junit.Assert.assertEquals;

public final class TestVectorJson {
    @SerializedName("data")
    public TestVector[] vectors;

    public static TestVectorJson loadJapanese() {
        final TestVectorJson data = Resources.loadJsonResource("bip39_japanese_test_vectors.json", TestVectorJson.class);
        assertEquals(24, data.vectors.length);
        return data;
    }

    public static TestVectorJson loadFrench() {
        final TestVectorJson data = Resources.loadJsonResource("bip39_french_test_vectors.json", TestVectorJson.class);
        assertEquals(18, data.vectors.length);
        return data;
    }

    public static TestVectorJson loadSpanish() {
        final TestVectorJson data = Resources.loadJsonResource("bip39_spanish_test_vectors.json", TestVectorJson.class);
        assertEquals(18, data.vectors.length);
        return data;
    }
}