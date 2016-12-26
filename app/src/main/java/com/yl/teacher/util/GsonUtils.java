package com.yl.teacher.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * Gson工具类
 */
public class GsonUtils {

    public static final String PREFIX_LEXUETAO = "lexuetao";
    public static final String SECRET_KEY = "JPG*LXT#shawn@==";

    private static Gson mGson;

    /**
     * 对象转换成json字符串
     */
    public static String toJson(Object obj) {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson.toJson(obj);
    }

    /**
     * json字符串转成对象
     */
    public static <T> T fromJson(String str, Type type) {
        if (mGson == null) {
            mGson = new Gson();
        }

        String result = "";

        String msgPrefix = str.substring(0, PREFIX_LEXUETAO.length());
        if (PREFIX_LEXUETAO.equals(msgPrefix)) {

            try {
                result = HttpUtils.Decrypt(str.substring(PREFIX_LEXUETAO.length()), SECRET_KEY);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            result = str;
        }

        return mGson.fromJson(result, type);
    }

    /**
     * json字符串转成对象
     */
    public static <T> T fromJson(String str, Class<T> type) {
        if (mGson == null) {
            mGson = new Gson();
        }

        String result = "";

        String msgPrefix = str.substring(0, PREFIX_LEXUETAO.length());
        if (PREFIX_LEXUETAO.equals(msgPrefix)) {

            try {
                result = HttpUtils.Decrypt(str.substring(PREFIX_LEXUETAO.length()), SECRET_KEY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            result = str;
        }

        return mGson.fromJson(result, type);
    }

}
