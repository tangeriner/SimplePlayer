package com.sunny.player.utils;

import java.util.Locale;


/**
 * Created by Sunny on 2017/8/16.
 */

public class TimeUtil {
    private static final String FORMAT_TIME = "%02d:%02d";

    public static String convert(int time) {
        time /= 1000;
        int seconds = time % 60;
        int minutes = time / 60 % 3600;
        int hours = time / 3600;
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(hours);
            stringBuilder.append(":");
        }
        stringBuilder.append(String.format(Locale.getDefault(), FORMAT_TIME, minutes, seconds));
        return stringBuilder.toString();
    }
}
