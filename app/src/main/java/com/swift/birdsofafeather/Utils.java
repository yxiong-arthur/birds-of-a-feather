package com.swift.birdsofafeather;

public class Utils {
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        else return str.equals("");
    }

    public static int toIntNullsafe(String str) {
        if (isEmpty(str)) {
            return 0;
        }
        return Integer.parseInt(str);
    }
}
