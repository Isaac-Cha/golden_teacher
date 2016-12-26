package com.yl.teacher.util;

import android.content.pm.PackageManager;
import android.webkit.MimeTypeMap;

import java.util.UUID;

public class StringUtils {

    /** 判断字符串是否有值，如果为null或者是空字符串或者只有空格或者为"null"字符串，则返回true，否则则返回false */
    public static boolean isEmpty(String value) {
        if (value != null && !"".equalsIgnoreCase(value.trim()) && !"null".equalsIgnoreCase(value.trim())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取文件mime类型
     * @param url
     *          file path or whatever suitable URL you want
     * @return
     *          文件mime类型
     */
    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getVersionName() {
        String versionName = null;
        try {
            versionName = UiUtils.getContext().getPackageManager().getPackageInfo(UiUtils.getContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String newVersionName = versionName.replace(".", "");
        return newVersionName;
    }

    public static String getVersionNameFull() {
        String versionName = null;
        try {
            versionName = UiUtils.getContext().getPackageManager().getPackageInfo(UiUtils.getContext().getPackageName(), 0).versionName;
            versionName = "V" + versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getUUID() {
        String mUUID = UUID.randomUUID().toString();
        String result = mUUID.replace("-", "");
        return result;
    }

}
