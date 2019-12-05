package com.ubertob.java;

public class MiniString {

    long raw;

    public MiniString(String str) {
        raw = encode(str);
    }

    public String get() {
        return decode(raw);
    }

    public static final int MAX_MINISTR_LEN = 10;
    public static final int MINI_STR_BASE = 64;
    public static final String letters = "=ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 _-!?.$&%@#:[]{}()*<>:;',/^";

    public static long encode(String str) {
        String prepared = prepareString(str);
        long encoded = 0;
        for (char c : prepared.toCharArray()) {
            int x = letters.indexOf(c);
            encoded = encoded * MINI_STR_BASE + x;
        }
        return encoded;
    }

    private static String prepareString(String str) {
        StringBuilder prepared = new StringBuilder();
        for (char c : str.toCharArray()) {
            if (letters.indexOf(c) >= 0)
                prepared.append(c);
            if (prepared.length() > MAX_MINISTR_LEN)
                break;
        }
        return prepared.toString();
    }

    public static String decode(long number) {
        StringBuilder decoded = new StringBuilder();
        long remaining = number;

        while (true) {
            int mod = (int) ( remaining % MINI_STR_BASE);
            char c = letters.charAt(mod);
            decoded.insert(0, c);
            if ( remaining < MINI_STR_BASE)
                break;
            remaining = remaining / MINI_STR_BASE;
        }
        return decoded.toString();

    }

}
