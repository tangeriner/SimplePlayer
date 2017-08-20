package com.sunny.player.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.provider.Settings;

/**
 * Created by Sunny on 2017/8/17.
 */

public class ScreenRotateUtil {
    /**
     * 屏幕旋转开关
     *
     * @param context ‘
     * @return true false
     */
    public static boolean rotateIsOn(Context context) {
        try {
            return Settings.System.getInt(context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 横竖屏切换
     *
     * @param activity
     */
    public static void toggle(Activity activity) {
        if (isLandscape(activity)) {
            setPortrait(activity);
        } else {
            setLandscape(activity);
        }
    }

    /**
     * 切换横屏
     *
     * @param activity
     */
    public static void setLandscape(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

    }

    /**
     * 切换竖屏
     *
     * @param activity
     */
    public static void setPortrait(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    /**
     * 用于还原强制旋转屏幕的Orientation
     *
     * @param activity
     */
    public static void setUnspecified(Activity activity) {
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    /**
     * 是否横屏
     *
     * @param activity
     * @return
     */
    public static boolean isLandscape(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }
}
