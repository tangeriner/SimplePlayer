package com.sunny.player.utils;

/**
 * Created by Sunny on 2017/8/22.
 */

public class StringUtil {
    public static String stick(Object... array) {
        if (array == null || array.length == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Object o : array) {
            stringBuilder.append(o);
        }
        return stringBuilder.toString();
    }
}
