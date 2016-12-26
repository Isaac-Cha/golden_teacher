package com.yl.teacher.util;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by neal on 2014/12/2.
 */
public class NetworkUtil {
    public static boolean isNetworkConnect() {
        if (UiUtils.getContext() == null) {
            return false;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) UiUtils.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected()) {
                return true;
            }
        }
        return false;
    }
}
