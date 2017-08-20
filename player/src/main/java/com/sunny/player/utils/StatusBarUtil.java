package com.sunny.player.utils;

import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Sunny on 2017/8/16.
 */

public class StatusBarUtil {
    /**
     * 隐藏status bar
     *
     * @param window
     */
    public static void hideStatusBar(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }

    /**
     * 显示status bar
     *
     * @param window
     */
    public static void showStatusBar(Window window) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        window.setAttributes(attrs);
    }
}
