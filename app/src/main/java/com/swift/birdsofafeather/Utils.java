package com.swift.birdsofafeather;

import android.content.Context;
import android.content.pm.PackageManager;

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

    public static boolean hasPermission(Context context, String permission) {
        int res = context.checkSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
