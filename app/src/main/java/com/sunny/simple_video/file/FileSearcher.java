package com.sunny.simple_video.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件搜索工具
 * Created by Sunny on 2017/8/8.
 */

public class FileSearcher {
    /**
     * 检索当前目录下所有
     *
     * @param file     文件目录
     * @param fileType 检索文件类型
     * @return 结果
     */
    public static List<String> searchAllFile(File file, String[] fileType) {
        List<String> list = new ArrayList<>();
        File[] files = file.listFiles();
        for (File f : files) {
            if (f.exists()) {
                if (f.isDirectory()) {
                    list.addAll(searchAllFile(f, fileType));
                } else if (endsWithMatches(f.getName(), fileType)) {
                    list.add(f.getPath());
                }
            }
        }
        return list;
    }

    /**
     * 获取matches结尾匹配
     * 忽略大小写
     *
     * @param string  src
     * @param matches 匹配类型
     * @return bool
     */
    public static boolean endsWithMatches(String string, String[] matches) {
        if (matches == null || matches.length == 0 || string == null) {
            return false;
        }
        for (String match : matches) {
            if (string.toUpperCase().endsWith(match)) {
                return true;
            }
        }
        return false;
    }
}
