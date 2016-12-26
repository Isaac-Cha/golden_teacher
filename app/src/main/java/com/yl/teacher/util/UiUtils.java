package com.yl.teacher.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.yl.teacher.global.MyApplication;

/**
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class UiUtils {

    /**
     * 设置ListView的高度
     *
     * @param listView 需要设置高度的ListView
     */
    public static void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 获取Context
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * 弹出吐司
     */
    public static void showToast(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出吐司
     */
    public static void showToast(int resId) {
        Toast.makeText(getContext(), UiUtils.getContext().getString(resId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 弹出长吐司
     */
    public static void showToastLong(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }

    /**
     * 弹出长吐司
     */
    public static void showToastLong(int resId) {
        Toast.makeText(getContext(), UiUtils.getContext().getString(resId), Toast.LENGTH_LONG).show();
    }

    /**
     * 设置popupWindow背景变暗
     *
     * @param popupWindow
     */
    public static void darkenScreen(final Activity activity, PopupWindow popupWindow) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = 0.5f;
        activity.getWindow().setAttributes(lp);
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                lp.alpha = 1f;
                activity.getWindow().setAttributes(lp);
            }
        });
    }

    /**
     * 关闭软键盘
     */
    public static void closeKeyboard(Activity activity) {
        if (activity.getCurrentFocus() != null) {
            ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断当前线程是否运行在主线程
     *
     * @return true：是  false：否
     */
    public static boolean isRunOnUiThread() {

        // 获取主线程id
        int mainThreadId = getMainThreadId();

        // 获取当前线程id
        int currentThreadId = android.os.Process.myTid();

        return mainThreadId == currentThreadId;

    }

    /**
     * 获取主线程的Handler对象
     *
     * @return
     */
    public static Handler getMainThreadHandler() {
        return MyApplication.getInstance().getHandler();
    }

    /**
     * 获取主线程id
     *
     * @return
     */
    public static int getMainThreadId() {
        return MyApplication.getInstance().getMainThreadId();
    }

    /**
     * 保存线程运行在主线程
     *
     * @param r
     */
    public static void runOnUiThread(Runnable r) {

        // 判断线程是否是主线程
        if (isRunOnUiThread()) {
            // 是，直接运行
            r.run();
        } else {
            // 不是，将r放到主线程的消息队列中
            getMainThreadHandler().post(r);
        }

    }

    /**
     * 获取字符串资源
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getContext().getResources().getString(resId);
    }

    /**
     * 获取字符串数组资源
     *
     * @param resId
     * @return
     */
    public static String[] getStringArray(int resId) {
        return getContext().getResources().getStringArray(resId);
    }

    /**
     * 获取drawable资源
     *
     * @param resId
     * @return
     */
    public static Drawable getDrawable(int resId) {
        return ContextCompat.getDrawable(getContext(), resId);
    }

    /**
     * 获取color资源
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        return ContextCompat.getColor(getContext(), resId);
    }

    /**
     * 获取颜色状态选择器
     *
     * @param resId
     * @return
     */
    public static ColorStateList getColorStateList(int resId) {
        return ContextCompat.getColorStateList(getContext(), resId);
    }

    public static int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
