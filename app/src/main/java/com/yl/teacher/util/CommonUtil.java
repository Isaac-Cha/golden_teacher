package com.yl.teacher.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;

import com.yl.teacher.model.Response;
import com.yl.teacher.widget.SystemBarTintManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yl.teacher.util.GsonUtils.PREFIX_LEXUETAO;
import static com.yl.teacher.util.GsonUtils.SECRET_KEY;
import static com.yl.teacher.util.HttpUtils.Decrypt;

/**
 * Created by yiban on 2016/5/3.
 */
public class CommonUtil {

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 把日期转化为年龄
     *
     * @param dateOfBirth
     * @return
     */
    public static int getAge(Date dateOfBirth) {
        int age = 0;
        Calendar born = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        if (dateOfBirth != null) {
            now.setTime(new Date());
            born.setTime(dateOfBirth);
            if (born.after(now)) {
                throw new IllegalArgumentException(
                        "Can't be born in the future");
            }
            age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < born.get(Calendar.DAY_OF_YEAR)) {
                age -= 1;
            }
        }
        return age;
    }


    public static int getMaxPage(int count, int pageNum) {
        int num = 1;
        if (count % pageNum == 0) {
            num = count / pageNum;
        } else {
            num = count / pageNum + 1;
        }
        return num;
    }

    /**
     * 校验密码是否正确
     *
     * @param str
     * @return
     */
    public static boolean isPwdRight(String str) {

        return validateStr(str, 6, 20) && IsPasswValid(str);
    }

    /**
     * 验证密码是否由英文及数字组成，亦可纯数字或纯字母
     *
     * @param str
     * @return
     */
    public static boolean IsPasswValid(String str) {
        String regex1 = "[0-9]*";
        String regex2 = "[a-zA-Z]*";
        String regex3 = "[0-9a-zA-Z]*";

        return match(regex1, str) || match(regex2, str) || match(regex3, str);
    }

    /**
     * 验证用户名，支持中英文（包括全角字符）、数字、下划线和减号 （全角及汉字算两位）,长度为4-20位,中文按二位计数
     *
     * @param msg
     * @return
     * @author www.zuidaima.com
     */
    public static boolean validateStr(String msg, int min, int max) {
        String validateStr = "^[\\w\\-－＿[０-９]\u4e00-\u9fa5\uFF21-\uFF3A\uFF41-\uFF5A]+$";
        boolean rs = false;
        rs = match(validateStr, msg);
        if (rs) {
            int strLenth = getStrLength(msg);
            if (strLenth < min || strLenth > max) {
                rs = false;
            }
        }
        return rs;
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 获取字符串的长度，对双字符（包括汉字）按两位计数
     *
     * @param value
     * @return
     */
    public static int getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chinese)) {
                valueLength += 1;
            } else {
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 验证手机号码
     *
     * @param mobileNumber
     * @return
     */
    public static boolean checkMobileNumber(String mobileNumber) {
        boolean flag = false;
        try {
            //Pattern regex = Pattern.compile("^(((13[0-9])|(15([0-3]|[5-9]))|(18[0,5-9])|(17[0-9]))\\d{8})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Pattern regex = Pattern.compile("^((1)\\d{10})|(0\\d{2}-\\d{8})|(0\\d{3}-\\d{7})$");
            Matcher matcher = regex.matcher(mobileNumber);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    public static Response checkResponse(String result) {
        Response response = new Response();
        JSONObject jsonObject = null;
        boolean status;
        String desResult;
        String msgPrefix = result.substring(0, GsonUtils.PREFIX_LEXUETAO.length());
        //LogUtil.d("msgPrefix=="+msgPrefix);
        if (GsonUtils.PREFIX_LEXUETAO.equals(msgPrefix)) {
            try {
                desResult = Decrypt(result.substring(GsonUtils.PREFIX_LEXUETAO.length()), GsonUtils.SECRET_KEY);
                jsonObject = new JSONObject(desResult);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        status = jsonObject.optBoolean("status");
        response.setData(jsonObject);
        response.setStatus(status);
        return response;
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {
        String img = "";
        Bitmap bm = getSmallBitmap(filePath);
        if (null != bm) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                baos.flush();
                baos.close();
                byte[] b = baos.toByteArray();
                img = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img.trim();

    }

    /**
     * string转成bitmap
     *
     * @param str
     */
    public static Bitmap convertStringToIcon(String str) {
        // OutputStream out;
        Bitmap bitmap = null;
        try {
            // out = new FileOutputStream("/sdcard/aa.jpg");
            byte[] bitmapArray;
            bitmapArray = Base64.decode(str, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
            // bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 480, 800);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        options.inDither = false;    /*不进行图片抖动处理*/
        options.inPreferredConfig = null;  /*设置让解码器以最佳方式解码*/

        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置沉浸式通知栏的背景颜色
     *
     * @param mTintManager
     */
    public static void systemBarTint(SystemBarTintManager mTintManager, int res) {
        mTintManager.setStatusBarTintEnabled(true);
        //mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(res);
    }

    /**
     * 获取唯一机器码
     *
     * @param ctx
     * @return
     */
    public static String getAppuuid(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
        String appuuid = tm.getDeviceId();// 获取机器码
        if (StringUtils.isEmpty(appuuid)) {
            return "No machine code";
        }
        return appuuid;
    }

    /**
     * 获取当前应用的版本号
     *
     * @param ctx
     * @return
     */
    public static String getVersionName() {
        return StringUtils.getVersionName();
    }
}
