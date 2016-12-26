package com.yl.teacher.view;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.MyClassAdapter;
import com.yl.teacher.db.MsgUpdate;
import com.yl.teacher.db.SysMsg;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.CampusLoginSend;
import com.yl.teacher.model.MyClass;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.SocketReceive;
import com.yl.teacher.model.SocketReceiveMsg;
import com.yl.teacher.model.VersionInfo;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.DownLoadManager;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;
import com.zhy.autolayout.AutoLinearLayout;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONObject;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.KeyValue;
import org.xutils.common.util.LogUtil;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 班级列表页面
 */
public class ClassesActivity extends BaseActivity {

    private SysMsg sysMsg;
    private MsgUpdate msgUpdate;
    private DbManager dbManager;

    private PullToRefreshListView lvClasses;

    private LinearLayout linearNodata;

    private boolean flagFirst = true;

    private int modeEvent;

    private String school;

    //记录第一次点击的时间
    private long clickTime = 0;

    private AutoLinearLayout linearUser;

    private MyClassAdapter adapter;

    private final static int limit = 10;

    private int page = 1;

    private int maxPage;

    private List<MyClass> mList;

    private TextView tvSchool;

    private TextView tvCreate;
    private Socket mSocket;
    private String mToken;
    private List<String> classIds;

    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_STATUS) {
            modeEvent = Static.LIST_REFRESH;
            page = 1;
            getClassesData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ClassesActivity");
        MobclickAgent.onResume(this);
        modeEvent = Static.LIST_REFRESH;
        page = 1;
        getClassesData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSocket.off(Static.SOCKET_LOGIN_RETRUN, onLogin);
        mSocket.off(Static.SOCKET_ADD_DOT_RETRUN, onAddDots);
        mSocket.off(Static.SOCKET_DEL_DOT_RETRUN, onDelDots);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ClassesActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_classes;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        linearUser = (AutoLinearLayout) findViewById(R.id.linearUser);
        linearNodata = (LinearLayout) findViewById(R.id.linearNodata);
        lvClasses = (PullToRefreshListView) findViewById(R.id.lvClasses);
        tvSchool = (TextView) findViewById(R.id.tvSchool);
        tvCreate = (TextView) findViewById(R.id.tvCreate);
    }

    @Override
    protected void initData() {
        dbManager = x.getDb(MyApplication.daoConfig);
        mToken = MyApplication.getInstance().getShareUser().getString("token", "");
        mSocket = MyApplication.getInstance().getSocket();
        mSocket.connect();
        mSocket.on(Static.SOCKET_LOGIN_RETRUN, onLogin);
        mSocket.on(Static.SOCKET_ADD_DOT_RETRUN, onAddDots);
        mSocket.on(Static.SOCKET_DEL_DOT_RETRUN, onDelDots);
        checkUpdate();
    }

    @Override
    protected void initListener() {

        tvCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassesActivity.this, CreateActivity.class);
                startActivity(intent);
            }
        });

        linearUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mList != null && mList.size() > 0) {
                    Intent intent = new Intent(ClassesActivity.this, UserActivity.class);
                    intent.putExtra("school", school);
                    startActivity(intent);
//                }
            }
        });

        lvClasses.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                modeEvent = Static.LIST_REFRESH;
                page = 1;
                getClassesData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                page = page + 1;
                if (page <= maxPage) {
                    modeEvent = Static.LIST_MORE;
                    getClassesData();
                } else {

                    Toast.makeText(x.app(), x.app().getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
                    lvClasses.onRefreshComplete();
                }

            }
        });

        lvClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MyApplication.getInstance().setClassesName(mList.get(position - 1).getName());
                MyApplication.getInstance().setClassId("" + mList.get(position - 1).getId());
                MyApplication.getInstance().setClassedNo(mList.get(position - 1).getClassCode());
                Intent intent = new Intent(ClassesActivity.this, DetaileActivity.class);
                startActivity(intent);

            }
        });

    }

    /**
     * 获取班级列表
     */
    private void getClassesData() {

        if (flagFirst) {
            flagFirst = false;
            CustomProgress.show(this, "加载中...", true, null);
        }

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/schoolClass/list?token=" + mToken + "&offset=" + page + "&limit=" + limit);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", mToken);
        mParams.put("offset", page + "");
        mParams.put("limit", limit + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/schoolClass/list", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        lvClasses.onRefreshComplete();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            linearUser.setVisibility(View.VISIBLE);
                            int count = response.getData().optJSONObject("data").optInt("count");
                            school = response.getData().optJSONObject("data").optString("schoolName");
                            MyApplication.getInstance().schoolName = school;
                            maxPage = CommonUtil.getMaxPage(count, limit);
                            List<MyClass> tmpData = MyClass.getMyClassListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));

                            tvSchool.setText(school);
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    if (tmpData != null && tmpData.size() > 0) {
                                        linearNodata.setVisibility(View.GONE);
                                        lvClasses.setVisibility(View.VISIBLE);
                                        mList = tmpData;
                                        adapter = new MyClassAdapter(ClassesActivity.this, mList);
                                        lvClasses.setAdapter(adapter);
                                    } else {
                                        linearNodata.setVisibility(View.VISIBLE);
                                        lvClasses.setVisibility(View.GONE);
                                    }


                                    break;

                                case Static.LIST_MORE:
                                    mList.addAll(mList.size(), tmpData);
                                    adapter.setData(mList);
                                    break;
                            }

                            socketLogin();

                        } else {

                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    page = 1;
                                    linearUser.setVisibility(View.GONE);
                                    linearNodata.setVisibility(View.VISIBLE);
                                    lvClasses.setVisibility(View.GONE);
                                    break;

                                case Static.LIST_MORE:
                                    page--;
                                    break;
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();
                        lvClasses.onRefreshComplete();

                        if (ex instanceof HttpException) { // 网络错误
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    page = 1;
                                    linearUser.setVisibility(View.GONE);
                                    linearNodata.setVisibility(View.VISIBLE);
                                    lvClasses.setVisibility(View.GONE);
                                    break;

                                case Static.LIST_MORE:
                                    page--;
                                    break;
                            }
                            HttpException httpEx = (HttpException) ex;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            LogUtil.d(responseCode + ":" + responseMsg);
                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        } else { // 其他错误
                            // ...
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    private void socketLogin() {
        CampusLoginSend campusLoginSend = new CampusLoginSend();
        campusLoginSend.token = mToken;
        campusLoginSend.device = CommonUtil.getAppuuid(this);
        mSocket.emit(Static.SOCKET_LOGIN, GsonUtils.toJson(campusLoginSend));
        LogUtil.i("campusLoginSend.token: " + campusLoginSend.token);
        LogUtil.i("campusLoginSend.device: " + campusLoginSend.device);
    }

    /**
     * socket登录回调监听
     */
    private Emitter.Listener onLogin = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                LogUtil.i("data: " + data.toString());
                String result = data.toString();
                SocketReceive socketReceive = GsonUtils.fromJson(result, SocketReceive.class);
                if (socketReceive.status) {
                    SocketReceiveMsg socketReceiveMsg = GsonUtils.fromJson(socketReceive.message, SocketReceiveMsg.class);
                    classIds = socketReceiveMsg.classX;
                }

                if (classIds != null && classIds.size() > 0 && mList != null && mList.size() > 0) {
                    for (MyClass myClass : mList) {
                        for (String classId : classIds) {
                            if (classId.equals(myClass.getId() + "")) {
                                myClass.isRed = true;
                            }
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });

                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                socketLogin();
            }

        }
    };

    private Emitter.Listener onAddDots = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                String result = data.toString();
                LogUtil.i("onAddDots.data: " + result);
                SocketReceive socketReceive = GsonUtils.fromJson(result, SocketReceive.class);
                if (socketReceive.status) {
                    socketLogin();
                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                socketLogin();
            }

        }
    };

    private Emitter.Listener onDelDots = new Emitter.Listener() {

        @Override
        public void call(Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                String result = data.toString();
                LogUtil.i("onDelDots.data: " + result);
                SocketReceive socketReceive = GsonUtils.fromJson(result, SocketReceive.class);
                if (socketReceive.status) {
                    socketLogin();
                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                socketLogin();
            }

        }
    };

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            Toast.makeText(this, "再按一次退出校园管家",
                    Toast.LENGTH_SHORT).show();
            clickTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkUpdate() {

        Map<String, String> mParams = new HashMap<>();
        mParams.put("source", "teacher");

        x.http().get(HttpUtils.getRequestParams("/common/v1/index/app-update", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("checkUpdate: " + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            VersionInfo versionInfo = VersionInfo.getVersionInfoFromJson(response.getData().optJSONObject("data").optJSONObject("appupdate"));
                            VersionInfo sysInfo = VersionInfo.getNoticeInfoFromJson(response.getData().optJSONObject("data").optJSONObject("sysmsg"));
                            try {
                                sysMsg = dbManager.selector(SysMsg.class).where("mydate", "=", sysInfo.getSysDate()).findFirst();
                                if (null == sysMsg) {
                                    if (sysInfo.isShow()) {
                                        new SweetAlertDialog(ClassesActivity.this, SweetAlertDialog.NORMAL_TYPE, true)
                                                .setTitleText("温馨提醒")
                                                //.showTitle(false)
                                                .setContentText(sysInfo.getContent())
                                                //.setCancelText("取消")
                                                .setConfirmText("确定")
                                                .showCancelButton(false)
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        sDialog.dismiss();
                                                    }
                                                })
                                                .show();

                                        SysMsg tmp = new SysMsg();
                                        tmp.setMydate(sysInfo.getSysDate());
                                        tmp.setShow(1);
                                        tmp.setShownum(1);
                                        dbManager.saveBindingId(tmp);
                                    }

                                } else {

                                    int num = sysMsg.getShownum();
                                    if (sysInfo.getCount() != num) {
                                        if (sysInfo.isShow()) {
                                            new SweetAlertDialog(ClassesActivity.this, SweetAlertDialog.NORMAL_TYPE, true)
                                                    .setTitleText("温馨提醒")
                                                    .setContentText(sysInfo.getContent())
                                                    //.setCancelText("取消")
                                                    .setConfirmText("确定")
                                                    .showCancelButton(false)
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {

                                                            sDialog.dismiss();

                                                        }
                                                    })
                                                    .show();
                                            dbManager.update(SysMsg.class,
                                                    WhereBuilder.b("mydate", "=", sysInfo.getSysDate()),
                                                    new KeyValue("shownum", num + 1));
                                        }

                                    }

                                }

                            } catch (DbException e) {
                                e.printStackTrace();
                            }


                            if (Integer.valueOf(StringUtils.getVersionName()) >= versionInfo.getVersion()) {
                                return;
                            }
                            try {
                                msgUpdate = dbManager.selector(MsgUpdate.class).where("mydate", "=", versionInfo.getSysDate()).findFirst();
                                if (null == msgUpdate) {

                                    DownLoadManager downloadmanager = new DownLoadManager(ClassesActivity.this,
                                            versionInfo);
                                    downloadmanager.startUpdata();

                                    MsgUpdate tmp = new MsgUpdate();
                                    tmp.setMydate(versionInfo.getSysDate());
                                    tmp.setShow(1);
                                    tmp.setShownum(1);
                                    dbManager.saveBindingId(tmp);
                                } else {

                                    if (versionInfo.isforce()) {
                                        DownLoadManager downloadmanager = new DownLoadManager(ClassesActivity.this,
                                                versionInfo);
                                        downloadmanager.startUpdata();
                                    } else {
                                        int num = msgUpdate.getShownum();
                                        if (versionInfo.getCount() != num) {
                                            DownLoadManager downloadmanager = new DownLoadManager(ClassesActivity.this,
                                                    versionInfo);
                                            downloadmanager.startUpdata();
                                            dbManager.update(MsgUpdate.class,
                                                    WhereBuilder.b("mydate", "=", versionInfo.getSysDate()),
                                                    new KeyValue("shownum", num + 1));
                                        }
                                    }

                                }

                            } catch (DbException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            LogUtil.d(responseCode + ":" + responseMsg);
                            // ...
                        } else { // 其他错误
                            // ...
                        }

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }
}
