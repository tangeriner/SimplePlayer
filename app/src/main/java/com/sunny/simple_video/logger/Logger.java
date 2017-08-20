package com.sunny.simple_video.logger;

import android.util.Log;

/**
 * Created by Sunny on 2017/8/6.
 */

public class Logger {
    public static final int SWITCH_D = 1;
    public static final int SWITCH_I = 2;
    public static final int SWITCH_W = 4;
    public static final int SWITCH_E = 8;
    private static String mDefaultTag = "simple_video";
    private static int mSwitch = 15;

    /**
     * 开关
     * @param mSwitch
     * @see #SWITCH_D
     * @see #SWITCH_I
     * @see #SWITCH_W
     * @see #SWITCH_E
     */
    public static void setmSwitch(int mSwitch) {
        Logger.mSwitch = mSwitch;
    }

    /**
     * if true mSwitch=15 else mSwitch=0;
     * @param enable
     */
    public static void setLoggerEnable(boolean enable) {
        Logger.mSwitch = enable ? 15 : 0;
    }

    public static void d(String msg) {
        d(mDefaultTag, msg);
    }

    public static void d(String tag, String msg) {
        if ((mSwitch & SWITCH_D) == SWITCH_D) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(mDefaultTag, msg);

    }

    public static void i(String tag, String msg) {
        if ((mSwitch & SWITCH_I) == SWITCH_I) {
            Log.i(tag, msg);
        }
    }

    public static void w(String msg) {
        w(mDefaultTag, msg);
    }

    public static void w(String tag, String msg) {
        if ((mSwitch & SWITCH_W) == SWITCH_W) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        e(mDefaultTag, msg);

    }

    public static void e(String tag, String msg) {
        if ((mSwitch & SWITCH_E) == SWITCH_E) {
            Log.e(tag, msg);
        }
    }
}
