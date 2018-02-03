/*
 *  BIP39 library, a Java implementation of BIP39
 *  Copyright (C) 2017-2018 Alan Evans, NovaCrypto
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

public final class TestCharSequence {
    public static CharSequence preventToStringAndSubSequence(final CharSequence sequence) {
        return new CharSequence() {
            @Override
            public int length() {
                return sequence.length();
            }

            @Override
            public char charAt(int index) {
                return sequence.charAt(index);
            }

            @Override
            public boolean equals(Object obj) {
                return sequence.equals(obj);
            }

            @Override
            public int hashCode() {
                return sequence.hashCode();
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                throw new RuntimeException("subSequence Not Allowed");
            }

            @Override
            public String toString() {
                throw new RuntimeException("toString Not Allowed");
            }
        };
    }

    public static CharSequence preventToString(final CharSequence sequence) {
        return new CharSequence() {
            @Override
            public int length() {
                return sequence.length();
            }

            @Override
            public char charAt(int index) {
                return sequence.charAt(index);
            }

            @Override
            public boolean equals(Object obj) {
                return sequence.equals(obj);
            }

            @Override
            public int hashCode() {
                return sequence.hashCode();
            }

            @Override
            public CharSequence subSequence(int start, int end) {
                return preventToString(sequence.subSequence(start, end));
            }

            @Override
            public String toString() {
                throw new RuntimeException("toString Not Allowed");
            }
        };
    }
}