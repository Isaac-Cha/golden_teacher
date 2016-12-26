
package com.yl.teacher.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.VersionInfo;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

public class DownLoadManager {

    private File installFile;
    private Activity act;
    private VersionInfo versionInfo;
    private final static int CLEAR_DATA = 3;
    private final static int DOWN_ERROR = 1;
    private final static int NOSDCARE_ERROR = 4;
    private final static int GET_UNDATAINFO_ERROR = 2;
    private final static int UPDATA_CLIENT = 0;
    private ProgressDialog pd;
    private SharedPreferences shareUpdate = MyApplication.getInstance().getShareUpdate();

    public DownLoadManager(Activity act, VersionInfo v) {
        this.act = act;
        this.versionInfo = v;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {

                case CLEAR_DATA:

                    installApk();
                    pd.dismiss();

                    break;
                case UPDATA_CLIENT:
                    // 对话框通知用户升级程序
                    showUpdataDialog();
                    break;
                case GET_UNDATAINFO_ERROR:
                    // 服务器超时
                    showToast(act.getResources().getString(R.string.update_info_error));
                    pd.cancel();

                    break;
                case DOWN_ERROR:
                    // 下载apk失败
                    showToast(act.getResources().getString(R.string.update_download_error));
                    pd.cancel();

                    break;

                case NOSDCARE_ERROR:
                    // 下载apk失败
                    showToast(act.getResources().getString(R.string.update_nosdcard_error));
                    pd.cancel();

                    break;

            }
        }
    };

    public void startUpdata() {
        Message msg = new Message();
        msg.what = UPDATA_CLIENT;
        handler.sendMessage(msg);
    }

    public void showToast(String msg) {
        Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
    }

    public void endUpdata(int ERROR) {
        Message msg = new Message();
        msg.what = ERROR;
        handler.sendMessage(msg);
    }

    /*
     * 从服务器中下载APK
     */
    public void downLoadApk() {

        pd = new ProgressDialog(act);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage(act.getString(R.string.version_start_download));
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

            public void onCancel(DialogInterface dialog) {

            }
        });
        pd.setCancelable(false);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                try {

                    Log.d("DownLoadManager", "downloadurl====" + versionInfo.downUrl);

                    getFileFromServer(versionInfo.downUrl, pd);
                    /*installFile = getFileFromServer(versionInfo.downUrl, pd);
                    if (installFile != null) {
                        Message msg = new Message();
                        msg.what = CLEAR_DATA;
                        handler.sendMessageDelayed(msg, 3000);
                    }*/

                } catch (Exception e) {
                    endUpdata(DOWN_ERROR);

                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void getFileFromServer(String downloadUrl, final ProgressDialog pd) {


        // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            RequestParams requestParams = new RequestParams(downloadUrl);
            LogUtil.e("APK: " + Environment.getExternalStorageDirectory() + "/jxt_t.apk");
            requestParams.setSaveFilePath(Environment.getExternalStorageDirectory() + "/jxt_t.apk");
            x.http().get(requestParams, new Callback.ProgressCallback<File>() {

                @Override
                public void onWaiting() {
                }

                @Override
                public void onStarted() {
                }

                @Override
                public void onLoading(long total, long current, boolean isDownloading) {
                    pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pd.setMessage("亲，努力下载中。。。");
                    pd.show();
                    pd.setMax((int) total);
                    pd.setProgress((int) current);
                }

                @Override
                public void onSuccess(File result) {
                    UiUtils.showToast("下载成功");
                    pd.dismiss();
                    installFile = new File(Environment.getExternalStorageDirectory() + "/jxt_t.apk");
                    if (installFile != null) {
                        Message msg = new Message();
                        msg.what = CLEAR_DATA;
                        handler.sendMessageDelayed(msg, 3000);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ex.printStackTrace();
                    UiUtils.showToast("下载失败，请检查网络和SD卡");
                    pd.dismiss();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });

        } else {
            Log.i("DownLoadManager", "没有可用的下载sdcard空间");
            endUpdata(NOSDCARE_ERROR);
        }
    }

    // 安装apk
    protected void installApk() {

        if (versionInfo.isforce) {
            // 如果是强制升级，如果用户下载完了apk还是取消安装，则强制退出应用。
            Intent mIntent = new Intent();
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mIntent.setAction(Intent.ACTION_VIEW);
            Log.d("DownLoadManager", "installFile=====" + installFile);
            mIntent.setDataAndType(Uri.fromFile(installFile),
                    "application/vnd.android.package-archive");
            act.startActivity(mIntent);
            MyApplication.getInstance().finishAllActivity();
        } else {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(installFile),
                    "application/vnd.android.package-archive");
            act.startActivity(intent);
        }

    }

    /*
     * 弹出对话框通知用户更新程序 弹出对话框的步骤： 1.创建alertDialog的builder. 2.要给builder设置属性,
     * 对话框的内容,样式,按钮 3.通过builder 创建一个对话框 4.对话框show()出来
     */
    protected void showUpdataDialog() {
        Log.d("DownLoadManager", "flagForce====" + versionInfo.isforce);
        if (versionInfo.isforce()) {

            Dialog dialog = new AlertDialog.Builder(act).setCancelable(false)
                    .setTitle(act.getString(R.string.page_setting_version_update))

                    .setMessage(
                            versionInfo.getContent())

                    .setPositiveButton(R.string.version_begin_download,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    downLoadApk();
                                }
                            })
                    /*.setNeutralButton(R.string.version_download_later,
                            new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog,
                                        int which) {

                                    dialog.dismiss();
                                    if (act != null) {
                                        act.finish();
                                    }

                                }
                            })*/.setOnKeyListener(new OnKeyListener() {

                        @Override
                        public boolean onKey(DialogInterface dialog, int keyCode,
                                             KeyEvent event) {
                            if (keyCode == KeyEvent.KEYCODE_BACK
                                    && event.getRepeatCount() == 0) {
                                /*dialog.dismiss();
                                if (act != null) {
                                    act.finish();
                                }*/
                                return true;
                            }
                            return false;
                        }
                    })

                    .create();
            dialog.show();
        } else {
            if (versionInfo.isShow()) {
                //LogUtil.d(""+shareUpdate.getBoolean(MyApplication.getInstance().AUTOUPDATE,false));
                //if(!shareUpdate.getBoolean(MyApplication.getInstance().AUTOUPDATE,false)){
                Dialog dialog = new AlertDialog.Builder(act).setCancelable(false)
                        .setTitle(act.getString(R.string.page_setting_version_update))

                        .setMessage(
                                versionInfo.getContent())

                        .setPositiveButton(R.string.version_begin_random,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        downLoadApk();
                                    }
                                })
                        .setNeutralButton(R.string.version_download_random,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                           /* SharedPreferences.Editor editor = shareUpdate.edit();
                                            editor.putBoolean(MyApplication.getInstance().AUTOUPDATE, true);
                                            editor.commit();*/
                                        dialog.dismiss();

                                    }
                                }).setOnKeyListener(new OnKeyListener() {

                            public boolean onKey(DialogInterface dialog, int keyCode,
                                                 KeyEvent event) {
                                boolean isReturn = true;
                                if (keyCode == KeyEvent.KEYCODE_BACK
                                        && event.getRepeatCount() == 0) {
                                    dialog.cancel();
                                    isReturn = false;
                                }

                                return isReturn;
                            }
                        })

                        .create();
                dialog.show();
                //}
            }


        }

    }

    /**
     * 已经是最新版本了。
     */
    public void showVersionIsNewDialog() {

        Dialog dialog = new AlertDialog.Builder(act)
                .setTitle(act.getString(R.string.page_setting_version_update))
                .setMessage(
                        act.getString(
                                R.string.page_setting_check_version_no_update,
                                MyApplication.getInstance().VERSION_CODE))

                .setNeutralButton("确定",
                        new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {

                                dialog.cancel();
                            }
                        }).setOnKeyListener(new OnKeyListener() {

                    public boolean onKey(DialogInterface dialog, int keyCode,
                                         KeyEvent event) {
                        boolean isReturn = true;
                        if (keyCode == KeyEvent.KEYCODE_BACK
                                && event.getRepeatCount() == 0) {
                            dialog.cancel();
                            isReturn = false;
                        }

                        return isReturn;
                    }

                }).create();

        dialog.setCancelable(false);
        dialog.show();
    }
}
