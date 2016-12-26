package com.yl.teacher.util;

import android.util.Base64;

import org.xutils.http.RequestParams;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by $USER_NAME on 2016/9/6.
 */
public class HttpUtils {

    private static final String SIGN = "sign";

    /**
     * 不带body参数的
     *
     * @param host
     * @param data
     * @return
     */
    public static RequestParams getRequestParams(String host, Map<String, String> data) {

        RequestParams params = new RequestParams(Static.URL_SERVER + host);
        params.addHeader("appversion", StringUtils.getVersionName());

        /*if (null != data) {
            Iterator it = data.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String val = data.get(key);
                params.addQueryStringParameter(key, val);
            }
        }*/

        params.addQueryStringParameter(SIGN, showAesc(data, params, null));

        return params;
    }

    /**
     * 带body参数的
     *
     * @param host
     * @param data
     * @param dataBody
     * @return
     */
    public static RequestParams getRequestParams(String host, Map<String, String> data, Map<String, String> dataBody) {

        List<String> bodyKeys = new ArrayList<>();

        RequestParams params = new RequestParams(Static.URL_SERVER + host);
        params.addHeader("appversion", StringUtils.getVersionName());

        /*if (null != data) {
            Iterator it = data.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String val = data.get(key);
                params.addQueryStringParameter(key, val);
            }
        }*/


        if (null != dataBody) {
            Iterator it = dataBody.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                String val = dataBody.get(key);
                params.addBodyParameter(key, val);
                bodyKeys.add(key);
            }
        }

        params.addQueryStringParameter(SIGN, showAesc(data, params, bodyKeys));

        return params;
    }

    /**
     * 进行升序排序
     */
    public static String showAesc(Map<String, String> data, RequestParams params, List<String> bodyKeys) {

        StringBuilder stringBuilder = new StringBuilder();

        if (null != data) {
            //将map.entrySet()转换成list
            List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(data.entrySet());
            Collections.sort(list, new Comparator<Map.Entry<String, String>>() {
                //降序排序
                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());

                }
            });

            for (Map.Entry<String, String> mapping : list) {
                stringBuilder.append(mapping.getKey() + "=" + mapping.getValue() + "&");
            }

            List<Map.Entry<String, String>> tempList = new ArrayList<>();
            if (bodyKeys != null && bodyKeys.size() > 0) {
                for (Map.Entry<String, String> mapping : list) {
                    for (String bodykey : bodyKeys) {
                        if (mapping.getKey().equals(bodykey)) {
                            tempList.add(mapping);
                        }
                    }
                }
                list.removeAll(tempList);
            }

            for (Map.Entry<String, String> mapping : list) {
                params.addQueryStringParameter(mapping.getKey(), mapping.getValue());
            }

        }

        stringBuilder.append(Static.APP_KEY);

        return getMD5(stringBuilder.toString());
    }

    /**
     * MD5编码
     *
     * @param info
     * @return
     */
    public static String getMD5(String info) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(info.getBytes("UTF-8"));
            byte[] encryption = md5.digest();

            StringBuffer strBuf = new StringBuffer();
            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 解密
     */
    public static String Decrypt(String sSrc, String sKey) {
        try {
            // 判断Key是否正确
            if (sKey == null) {
                System.out.print("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
                System.out.print("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = Base64.decode(sSrc, Base64.NO_WRAP);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

}
