package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.adapter.DetaileAdapter;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.EMMobGroup;
import com.yl.teacher.model.Manager;
import com.yl.teacher.model.ModuleDotsSend;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.SocketReceive;
import com.yl.teacher.model.SocketReceiveModule;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.SystemBarTintManager;

import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

import static com.yl.teacher.global.MyApplication.emmGroup;

/**
 * 班级管理页面
 */
public class DetaileActivity extends BaseActivity {

    private Manager manager;

    private ListView lvDetail;

    private DetaileAdapter adapter;

    private String school;
    private boolean isFirst;
    private Socket mSocket;
    private String mToken;
    private String mClassId;
    private List<String> moduleIds;
    private ImageView iv_right;
    private RelativeLayout rlUser;
    private TextView tv_title;
    private RelativeLayout rl_back;

    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_STATUS) {
            initClassDetail();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mSocket.off(Static.SOCKET_ADD_DOT_RETRUN, onAddDots);
        mSocket.off(Static.SOCKET_ADD_DOT_RETRUN, onModule);
        mSocket.off(Static.SOCKET_DEL_DOT_RETRUN, onDelDots);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("DetaileActivity");
        MobclickAgent.onResume(this);

        getEMGroupId();
        initClassDetail();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("DetaileActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_detaile;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        school = MyApplication.getInstance().schoolName;
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        lvDetail = (ListView) findViewById(R.id.lvDetail);
    }

    @Override
    protected void initData() {
        mToken = MyApplication.getInstance().getShareUser().getString("token", "");
        mClassId = MyApplication.getInstance().getClassId();
        mSocket = MyApplication.getInstance().getSocket();
        mSocket.connect();
        mSocket.on(Static.SOCKET_ADD_DOT_RETRUN, onAddDots);
        mSocket.on(Static.SOCKET_MODULE_RETRUN, onModule);
        mSocket.on(Static.SOCKET_DEL_DOT_RETRUN, onDelDots);
    }

    @Override
    protected void initListener() {
        rlUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetaileActivity.this, UserActivity.class);
//                intent.putExtra("school",mList.get(0).getSchool());
                startActivity(intent);
            }
        });

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void toScoket() {
        ModuleDotsSend moduleDotsSend = new ModuleDotsSend();
        moduleDotsSend.token = mToken;
        moduleDotsSend.device = CommonUtil.getAppuuid(this);
        moduleDotsSend.classId = mClassId;
        mSocket.emit(Static.SOCKET_MODULE, GsonUtils.toJson(moduleDotsSend));
    }

    private Emitter.Listener onAddDots = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                String result = data.toString();
                LogUtil.i("onAddDots.data: " + result);
                SocketReceive socketReceive = GsonUtils.fromJson(result, SocketReceive.class);
                if (socketReceive.status) {
                    toScoket();
                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                toScoket();
            }

        }
    };

    private Emitter.Listener onModule = new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            try {

                JSONObject data = (JSONObject) args[0];
                String result = data.toString();
                LogUtil.i("onModule.data: " + result);
                SocketReceive socketReceive = GsonUtils.fromJson(result, SocketReceive.class);
                if (socketReceive.status) {
                    SocketReceiveModule socketReceiveModule = GsonUtils.fromJson(socketReceive.message, SocketReceiveModule.class);
                    moduleIds = socketReceiveModule.module;
                }

                if (manager != null && moduleIds != null && moduleIds.size() > 0) {
                    manager.moduleIds = moduleIds;

                    if (moduleIds.contains(Static.KEY_PERMIT + "")) {
                        manager.isPermit = true;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });
                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                toScoket();
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
                    toScoket();
                }

            } catch (WebsocketNotConnectedException e) {
                e.printStackTrace();
                toScoket();
            }

        }
    };

    /**
     * 获取列表
     */
    private void initClassDetail() {

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "加载中...", true, null);
        }

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/userMenu/class-init?token=" + mToken + "&Class_id=" + MyApplication.getInstance().getClassId());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", mToken);
        mParams.put("Class_id", mClassId);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/userMenu/class-init", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            manager = Manager.getManagerFromJsonObj(response.getData().optJSONObject("data"));
                            adapter = new DetaileAdapter(DetaileActivity.this, manager, DetaileActivity.this, mSocket);
                            lvDetail.setAdapter(adapter);
                            toScoket();
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();
                        if (ex instanceof HttpException) { // 网络错误
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

    /**
     * 获取环信群组ID
     */
    private void getEMGroupId() {

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", mToken);
        mParams.put("Class_id", mClassId);

        x.http().get(HttpUtils.getRequestParams("/common/v1/easemobGroup/group", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.i("getEMGroupId(): " + result);
                Response response = CommonUtil.checkResponse(result);
                if (response.isStatus()) {

                    emmGroup = EMMobGroup.getEMGroupIdFromJsonObj(response.getData().optJSONObject("data"));

                } else {
                    LogUtil.e("环信群组ID错误信息: " + response.getData().optString("message"));
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void initBar() {

        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rlUser = (RelativeLayout) findViewById(R.id.rl_gohome);
        iv_right = (ImageView) findViewById(R.id.iv_right);


        tv_title.setText(school);

    }

}