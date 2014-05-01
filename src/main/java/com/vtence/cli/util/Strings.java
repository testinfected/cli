package com.vtence.cli.util;

public final class Strings
{
    private Strings() {}

    public static boolean empty(String s) {
        return s == null || s.isEmpty();
    }

    public static boolean blank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static String quote(String s) {
        return "`" + s + "'";
    }
}
