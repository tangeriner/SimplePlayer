package com.sunny.simple_video.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by Sunny on 2017/8/6.
 */

public class SDCardUtils {
    public static String getSDCardPath() {
        return getSDCardFile().getPath();
    }

    public static File getSDCardFile() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);//判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir;
    }
}
