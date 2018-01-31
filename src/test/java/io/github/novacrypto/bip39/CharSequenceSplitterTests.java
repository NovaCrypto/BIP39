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

package io.github.novacrypto.bip39;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public final class CharSequenceSplitterTests {

    @Test
    public void empty_sequence() {
        final List<CharSequence> list = new CharSequenceSplitter(' ', ' ').split("");
        assertEquals(1, list.size());
        assertEquals("", list.get(0).toString());
    }

    @Test
    public void sequence_containing_one() {
        final List<CharSequence> list = new CharSequenceSplitter(' ', ' ').split("abc");
        assertEquals(1, list.size());
        assertEquals("abc", list.get(0).toString());
    }

    @Test
    public void two_items() {
        final List<CharSequence> list = new CharSequenceSplitter(' ', ' ').split("abc def");
        assertEquals(2, list.size());
        assertEquals("abc", list.get(0).toString());
        assertEquals("def", list.get(1).toString());
    }

    @Test
    public void two_items_different_separator() {
        final List<CharSequence> list = new CharSequenceSplitter('-', '-').split("abc-def");
        assertEquals(2, list.size());
        assertEquals("abc", list.get(0).toString());
        assertEquals("def", list.get(1).toString());
    }

    @Test
    public void just_separator() {
        final List<CharSequence> list = new CharSequenceSplitter('-', '-').split("-");
        assertEquals(2, list.size());
        assertEquals("", list.get(0).toString());
        assertEquals("", list.get(1).toString());
    }

    @Test
    public void separator_at_end() {
        final List<CharSequence> list = new CharSequenceSplitter('-', '-').split("a-b-c-");
        assertEquals(4, list.size());
        assertEquals("a", list.get(0).toString());
        assertEquals("b", list.get(1).toString());
        assertEquals("c", list.get(2).toString());
        assertEquals("", list.get(3).toString());
    }

    @Test
    public void two_separators_in_middle() {
        final List<CharSequence> list = new CharSequenceSplitter('-', '-').split("a--b-c");
        assertEquals(4, list.size());
        assertEquals("a", list.get(0).toString());
        assertEquals("", list.get(1).toString());
        assertEquals("b", list.get(2).toString());
        assertEquals("c", list.get(3).toString());
    }

    @Test
    public void different_separators() {
        final List<CharSequence> list = new CharSequenceSplitter('-', '+').split("a-b+c");
        assertEquals(3, list.size());
        assertEquals("a", list.get(0).toString());
        assertEquals("b", list.get(1).toString());
        assertEquals("c", list.get(2).toString());
    }

    @Test
    public void whiteBox_number_of_expected_calls() {
        final CharSequence inner = "abc-def-123";
        final Spy spy = new Spy(inner);
        new CharSequenceSplitter('-', '-').split(spy);
        assertEquals(1, spy.lengthCalls);
        assertEquals(inner.length(), spy.charAtCalls);
        assertEquals(3, spy.subSequenceCalls);
        assertEquals(0, spy.toStringCalls);
    }

    private class Spy implements CharSequence {
        private final CharSequence inner;
        int lengthCalls;
        int charAtCalls;
        int subSequenceCalls;
        int toStringCalls;

        public Spy(CharSequence inner) {
            this.inner = inner;
        }

        @Override
        public int length() {
            lengthCalls++;
            return inner.length();
        }

        @Override
        public char charAt(int index) {
            charAtCalls++;
            return inner.charAt(index);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            subSequenceCalls++;
            return inner.subSequence(start, end);
        }

        @Override
        public String toString() {
            toStringCalls++;
            return super.toString();
        }
    }
}