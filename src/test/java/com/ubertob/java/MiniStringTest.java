package com.ubertob.java;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MiniStringTest {

    @Test
    void someValues() {

        expectEquals("A");
        expectEquals("ABC");
        expectEquals("ABC 123");
        expectEquals("AB Z 123!");
        expectEquals("1234567890");

    }

    private void expectEquals(String str) {
        MiniString miniString = new MiniString(str);
        assertEquals(str, miniString.get());
    }

    @Test
    void encodeTest() {
        assertEquals(0L, MiniString.encode("="));
        assertEquals(1L, MiniString.encode("A"));
        assertEquals(65L, MiniString.encode("AA"));
        assertEquals(90L, MiniString.encode("AZ"));
        assertEquals(4161L, MiniString.encode("AAA"));
        assertEquals(270532L, MiniString.encode("ABCD"));
        assertEquals(18590823326541792L, MiniString.encode("ABCDE12345"));

        assertEquals(1L, MiniString.encode("asdwgrrAbewrfsfd||"));
    }

    @Test
    void decodeTest() {
        assertEquals("=", MiniString.decode(0));
        assertEquals("A", MiniString.decode(1));
        assertEquals("AA", MiniString.decode(65));
        assertEquals("AZ", MiniString.decode(90));
        assertEquals("AAA", MiniString.decode(4161));
        assertEquals("ABCD", MiniString.decode(270532L));
        assertEquals("ABCDE12345", MiniString.decode(18590823326541792L));
    }
}