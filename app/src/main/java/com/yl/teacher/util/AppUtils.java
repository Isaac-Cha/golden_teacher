package com.yl.teacher.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.User;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by $USER_NAME on 2016/9/19.
 * App工具类
 */
public class AppUtils {

    private static final String SP_DEFAULT_VALUE = "";
    private static SharedPreferences mSP;

    /**
     * 退出操作
     *
     * @param context
     */
    public static void loginOut(Context context, Intent intent) {
        MobclickAgent.onProfileSignOff();

        // 极光取消别名
        JPushInterface.setAlias(context, "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i == 0) {
                    LogUtil.i("极光推送别名取消成功，别名：" + s);
                }
            }
        });
        // 取消标签
        JPushInterface.setTags(context, new HashSet<String>(), new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                if (i == 0) {
                    LogUtil.i("极光推送标签取消成功，标签：" + set.toString());
                }
            }
        });

        // 登出环信
        EMClient.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                LogUtil.i("退出聊天成功");
            }

            @Override
            public void onError(int i, String message) {
                LogUtil.e("退出聊天失败: " + message);
            }

            @Override
            public void onProgress(int i, String message) {

            }
        });

        // 清空环信信息
        clearEMInfo();

        MyApplication.getInstance().clearShareUser();
        MyApplication.getInstance().finishAllActivity();
        context.startActivity(intent);
    }

    /**
     * 获取存储环信SP
     *
     * @return
     */
    public synchronized static SharedPreferences getEMSP() {
        return UiUtils.getContext().getSharedPreferences(Static.EM_SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 环信信息存储
     */
    public static void saveEMUserInfo(String username, String password, String nickname, String avatarUrl) {

        if (mSP == null) {
            mSP = getEMSP();
        }

        // 存储用户名
        mSP.edit().putString(Static.EM_USERNAME, username).commit();

        // 存储密码
        mSP.edit().putString(Static.EM_PASSWORD, password).commit();

        // 存储昵称
        mSP.edit().putString(Static.EM_NICKNAME, nickname).commit();

        // 存储头像URL
        mSP.edit().putString(Static.EM_AVATAR_URL, avatarUrl).commit();

    }

    /**
     * 获取环信帐号
     */
    public static String getEMUsername() {

        if (mSP == null) {
            mSP = getEMSP();
        }

        return mSP.getString(Static.EM_USERNAME, SP_DEFAULT_VALUE);
    }

    /**
     * 获取环信密码
     */
    public static String getEMPassword() {

        if (mSP == null) {
            mSP = getEMSP();
        }

        return mSP.getString(Static.EM_PASSWORD, SP_DEFAULT_VALUE);
    }

    /**
     * 获取环信昵称
     */
    public static String getEMNickname() {

        if (mSP == null) {
            mSP = getEMSP();
        }

        return mSP.getString(Static.EM_NICKNAME, SP_DEFAULT_VALUE);
    }

    /**
     * 获取头像URL
     */
    public static String getEMAvatarUrl() {

        if (mSP == null) {
            mSP = getEMSP();
        }

        return mSP.getString(Static.EM_AVATAR_URL, SP_DEFAULT_VALUE);
    }

    /**
     * 退出时清空存储的环信信息
     */
    public static void clearEMInfo() {

        if (mSP == null) {
            mSP = getEMSP();
        }

        mSP.edit().clear().commit();

    }

    /**
     * 登录环信
     *
     * @param user
     */
    public static void loginEmmobAndSaveInfo(final User user) {

        // 环信登录
        EMClient.getInstance().login(user.hxId, user.hxPwd, new EMCallBack() { //回调
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                LogUtil.i("登录聊天服务器成功！");
                LogUtil.i("ID: " + user.hxId);

                // 保存用户环信信息
                saveEMUserInfo(user.hxId, user.hxPwd, user.hxNickname, user.hxAvatar);

            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                LogUtil.e("ID: " + user.hxId);
                LogUtil.e("PWD: " + user.hxPwd);
                LogUtil.e("message: " + message);
                LogUtil.e("登录聊天服务器失败！");
            }
        });

    }

    /**
     * 清除Glide缓存
     *
     * @param context
     */
    public static void clearGlideCache(final Context context) {
        Glide.get(context).clearMemory();
        new Thread() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }.start();
    }

    /**
     * 保存图片到手机
     *
     * @param imgurl
     */
    public static void downloadImage(String imgurl) {

        // 判断SD卡是否挂载
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            UiUtils.showToast("没有找到SD卡，无法保存");
            return;
        }

        String saveDir = Environment.getExternalStorageDirectory() + "/DCIM/jxt/";
        LogUtil.e("saveDir: " + saveDir);
        File fileDir = new File(saveDir);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        // 设置图片名称
        int biasLastIndex = imgurl.lastIndexOf("/");
        LogUtil.e("imgurl: " + imgurl);
        // 截取字符串
        String imageName = imgurl.substring(biasLastIndex + 1);
        LogUtil.e("imageName: " + imageName);

        RequestParams mParams = new RequestParams(imgurl);
        mParams.setSaveFilePath(saveDir + imageName);
        x.http().get(mParams, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File file) {
                UiUtils.showToast("保存成功");
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                throwable.printStackTrace();
                UiUtils.showToast("保存失败，请检查网络和SD卡");
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(long l, long l1, boolean b) {

            }
        });

    }

    public static void saveFile(String msg) throws IOException {

        File file = new File(Environment.getExternalStorageDirectory(), "log.log");

        BufferedWriter bw = null;

        bw = new BufferedWriter(new FileWriter(file, true));
        bw.write(msg);
        bw.newLine();
        bw.flush();

        if (bw != null) {
            bw.close();
        }

    }

}
