package com.sunny.simple_video;

import android.app.Application;

/**
 * Created by Sunny on 2017/8/6.
 */

public class VideoApplication extends Application {

    private static VideoApplication app;

    public static VideoApplication getInstance() {
        return app;
    }

    public VideoApplication() {
        VideoApplication.app = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
