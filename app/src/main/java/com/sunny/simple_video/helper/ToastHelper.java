package com.sunny.simple_video.helper;

import android.os.SystemClock;
import android.widget.Toast;

import com.sunny.simple_video.VideoApplication;

/**
 * Created by Sunny on 2017/8/7.
 */

public class ToastHelper {

    private static final int INTERVAL = 3000;
    private static long mLastShowCut = 0L;

    public static void show(CharSequence charSequence) {
        long cut = SystemClock.currentThreadTimeMillis();
        if (cut - mLastShowCut > INTERVAL) {
            Toast.makeText(VideoApplication.getInstance().getBaseContext(), charSequence, Toast.LENGTH_SHORT).show();
            mLastShowCut = cut;
        }
    }
}
