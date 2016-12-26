package com.yl.teacher.global;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.chat.ChatHelper;
import com.yl.teacher.model.EMMobGroup;
import com.yl.teacher.model.User;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.Static;

import org.xutils.DbManager;
import org.xutils.x;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by yiban on 2016/5/3.
 */
public class MyApplication extends Application {
    final MyApplication self = this;
    private Stack<Activity> activityStack;
    private static Context context;

    //班级编号
    public String classedNo;
    private Handler handler;
    private int mainThreadId;

    /**
     * 环信群组
     */
    public static EMMobGroup emmGroup;

    /**
     * 头像是否发生变化
     */
    public static boolean avatarIsChanged = false;

    public String getClassedNo() {
        return classedNo;
    }

    public void setClassedNo(String classedNo) {
        this.classedNo = classedNo;
    }

    //班级名字
    public String classesName;
    public String schoolName; // 学校名字

    public String getClassesName() {
        return classesName;
    }

    public void setClassesName(String classesName) {
        this.classesName = classesName;
    }

    //班级id
    public String classId;

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    //记录登录后返回的用户信息
    public SharedPreferences shareUser;

    //记录用户的自动登录名和密码
    public SharedPreferences ShareApp;

    //记录是否勾选自动登录
    public SharedPreferences ShareAutoLogin;

    public String SHAREUSER = "shareuser";
    public String SHAREAPP = "shareapp";
    public String AUTOLOGIN = "auto_login";

    public String USERNAME = "user_name";
    public String PASSWORD = "pass_word";
    public String SHAREMSG = "sharemsg";

    public static String UUID;
    public String VERSION_CODE;

    private static MyApplication instance;

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket(Static.CHAT_SERVER);
        } catch (URISyntaxException e) {
//            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }

    public static MyApplication getInstance() {
        if (instance == null) {
            instance = new MyApplication();
        }
        return instance;
    }

    public static DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
            .setDbName("edu.db")
            // 不设置dbDir时, 默认存储在app的私有目录."sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
//            .setDbDir(new File("/sdcard"))
            .setDbVersion(1)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // 开启WAL, 对写入加速提升巨大
                    db.getDatabase().enableWriteAheadLogging();
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // db.addColumn(...);
                    // db.dropTable(...);
                    // ...
                    // or
                    // db.dropDb();
                }
            });

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //httpClient = new OkHttpClient();
        instance = this;
        //初始化context对象
        context = getApplicationContext();
        //xutil初始化
        x.Ext.init(this);
        //是否打印日志,发正服包的时候关闭。
//        x.Ext.setDebug(true);
        Fresco.initialize(this);
        //初始化友盟统计分析sdk
        MobclickAgent.openActivityDurationTrack(false);
        UUID = CommonUtil.getAppuuid(this);
        // 初始化Handler对象
        handler = new Handler();

        // 获取主线程的线程id
        mainThreadId = android.os.Process.myTid();

        VERSION_CODE = CommonUtil.getVersionName();
        removeTempFromPref();
        // 初始化极光推送
        JPushInterface.init(this);

        // init chat helper
        ChatHelper.getInstance().init(context);

    }

    /**
     * @return
     */
    public SharedPreferences getShareUser() {
        if (shareUser == null) {
            shareUser = instance.getApplicationContext().getSharedPreferences(SHAREUSER,
                    MODE_APPEND);
        }
        return shareUser;
    }

    public boolean clearShareUser() {
        if (shareUser == null) {
            shareUser = instance.getApplicationContext().getSharedPreferences(SHAREUSER,
                    MODE_APPEND);
        }
        User.setCurrentUser(new User());
        SharedPreferences.Editor editor = shareUser.edit();
        editor.clear();
        return editor.commit();
    }

    public boolean setShareApp(String username, String password) {
        if (ShareApp == null) {
            ShareApp = instance.getApplicationContext().getSharedPreferences(SHAREAPP, MODE_APPEND);
        }
        SharedPreferences.Editor editor = ShareApp.edit();
        editor.putString(USERNAME, username);
        editor.putString(PASSWORD, password);
        return editor.commit();
    }

    public SharedPreferences getShareApp() {
        if (ShareApp == null) {
            ShareApp = instance.getApplicationContext().getSharedPreferences(SHAREAPP, MODE_APPEND);
        }
        return ShareApp;
    }

    public SharedPreferences getShareAutoLogin() {
        if (ShareAutoLogin == null) {
            ShareAutoLogin = instance.getApplicationContext().getSharedPreferences(AUTOLOGIN,
                    MODE_APPEND);
        }
        return ShareAutoLogin;
    }


    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束所有Activity，退出应用程序
     */
    public void finishAllActivity() {
        if (activityStack != null && activityStack.size() > 0) {
            for (Activity activity : activityStack) {
                activity.finish();
            }
            activityStack.clear();
        }
    }

    public static Context getContext() {
        return context;
    }

    private void removeTempFromPref() {
        SharedPreferences sp = getSharedPreferences(
                Static.APPLICATION_NAME, MODE_PRIVATE);
        sp.edit().remove(Static.PREF_TEMP_IMAGES).commit();
    }

    public static String getPhoneIp() {
        //获取wifi服务
        WifiManager wifiManager = (WifiManager) instance.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        String ip = "";
        if (wifiManager.isWifiEnabled()) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            ip = intToIp(ipAddress);
        } else {
            ip = getLocalIpAddress();
        }
        return ip;
    }

    private static String intToIp(int i) {

        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }

    //升级
    public SharedPreferences shareUpdate;

    public SharedPreferences getShareUpdate() {
        if (shareUpdate == null) {
            shareUpdate = instance.getApplicationContext().getSharedPreferences("update", MODE_APPEND);
        }
        return shareUpdate;
    }

    //记录消息数
    public SharedPreferences shareMessage;

    public SharedPreferences getShareMsg() {
        if (shareMessage == null) {
            shareMessage = instance.getApplicationContext().getSharedPreferences(SHAREMSG, MODE_APPEND);
        }
        return shareMessage;
    }

    /**
     *
     * @return
     */
    public Handler getHandler() {
        return handler;
    }

    public int getMainThreadId() {
        return mainThreadId;
    }

}
