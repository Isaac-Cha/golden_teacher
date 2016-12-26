package cn.finalteam.galleryfinal.utils;

import cn.finalteam.galleryfinal.widget.SystemBarTintManager;

/**
 * Created by yiban on 2016/5/3.
 */
public class CommonUtil {

    /**
     * 设置沉浸式通知栏的背景颜色
     * @param mTintManager
     *
     */
    public static void systemBarTint(SystemBarTintManager mTintManager, int res){
        mTintManager.setStatusBarTintEnabled(true);
        //mTintManager.setNavigationBarTintEnabled(true);
        mTintManager.setStatusBarTintResource(res);
    }
}
