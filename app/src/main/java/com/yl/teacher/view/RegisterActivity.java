package com.yl.teacher.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.CampusLoginSend;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.User;
import com.yl.teacher.util.AppUtils;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 注册页面
 */
public class RegisterActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private TextView tvRegister;

    private Button btnValid;

    private int second=60;

    private boolean flagValid;

    private EditText etPhone;

    private EditText etValid;

    private EditText etPwd;

    private boolean flagName;
    private boolean flagPwd;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    second--;
                    if(second>0){

                        btnValid.setText(""+second+"s");
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    if (second==0){
                        flagValid = false;
                        second = 60;
                        btnValid.setBackgroundResource(R.drawable.valid_bg);
                        btnValid.setText("再次获取");
                    }


                    break;

            }
        }
    };
    private Socket mSocket;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("RegisterActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RegisterActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_register;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        btnValid = (Button) findViewById(R.id.btnValid);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etValid = (EditText) findViewById(R.id.etValid);
        etPwd = (EditText) findViewById(R.id.etPwd);
    }

    @Override
    protected void initData() {
        mSocket = MyApplication.getInstance().getSocket();
        mSocket.connect();
    }

    @Override
    protected void initListener() {
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(StringUtils.isEmpty(etPhone.getText().toString())){
                    Toast.makeText(x.app(),"用户名不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(StringUtils.isEmpty(etValid.getText().toString())){
                    Toast.makeText(x.app(),"验证码不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(StringUtils.isEmpty(etPwd.getText().toString())){
                    Toast.makeText(x.app(),"密码不能为空！",Toast.LENGTH_SHORT).show();
                    return;
                }

                flagName = CommonUtil.checkMobileNumber(etPhone.getText().toString());

                flagPwd = CommonUtil.isPwdRight(etPwd.getText().toString());

                if(!flagName){
                    Toast.makeText(RegisterActivity.this, "用户名必须是手机！", Toast.LENGTH_SHORT).show();
                }
                if(!flagPwd){
                    Toast.makeText(RegisterActivity.this, "密码格式不正确！", Toast.LENGTH_SHORT).show();
                }

                if (flagName && flagPwd) {
                    handlerKeyboard(getWindow().peekDecorView(), false);
                    checkValid();
                }

            }
        });
        btnValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!flagValid){
                    flagValid = true;
                    btnValid.setBackgroundResource(R.drawable.btn_valid_grey);
                    btnValid.setText(""+second+"s");
                    handler.removeMessages(0);
                    //每隔1秒钟发送一次handler消息
                    handler.sendEmptyMessageDelayed(0, 1000);
                    getValidNum();
                }

            }
        });
    }
    /**
     isOpen = true  显示
     isOpen = false 隐藏
     当v为空时可以调用getWindow().peekDecorView()获取整个屏幕的View
     */
    public static void handlerKeyboard(View v, boolean isOpen) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isOpen) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        }
    }
    /**
     * 获取验证码
     */
    private void getValidNum(){

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/common/v1/sms/send-verification-code?phone=" + etPhone.getText().toString() + "&kind=1");

        Map<String, String> mParams = new HashMap<>();
        mParams.put("phone", etPhone.getText().toString().trim());
        mParams.put("kind", "1");

        x.http().get(HttpUtils.getRequestParams("/common/v1/sms/send-verification-code", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {


                        } else {
                            flagValid = false;
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        flagValid = false;


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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
     * 检查验证码是否正确
     */
    private void checkValid(){

        CustomProgress.show(this, "加载中...", true, null);

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/common/v1/sms/check-verification-code?account=" + etPhone.getText().toString() + "&code=" + etValid.getText().toString());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("account", etPhone.getText().toString().trim());
        mParams.put("code", etValid.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/common/v1/sms/check-verification-code", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            if (response.getData().optBoolean("status")) {

                                doRegister();

                            }

                        } else {

                            CustomProgress.hideDialog();
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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
    private void doRegister(){
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/register");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("account", etPhone.getText().toString());
        mParams.put("password", etPwd.getText().toString());
        mParams.put("code", etValid.getText().toString());

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/register", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            SharedPreferences shareAutoLogin = MyApplication.getInstance().getShareAutoLogin();
                            SharedPreferences.Editor editor = shareAutoLogin.edit();
                            editor.putBoolean(MyApplication.getInstance().AUTOLOGIN, true);
                            editor.commit();
                            MyApplication.getInstance().setShareApp(etPhone.getText().toString(), etPwd.getText().toString());
                            User user = User.getUserFromJsonObj(response.getData().optJSONObject("data"));
                            User.setCurrentUser(user);
                            MobclickAgent.onProfileSignIn(user.getNickName());

                            // 登录环信
                            if (!StringUtils.isEmpty(user.hxId) && !StringUtils.isEmpty(user.hxPwd))
                                AppUtils.loginEmmobAndSaveInfo(user);

                            // 极光推送设置别名
                            JPushInterface.setAlias(RegisterActivity.this, user.getId() + "", new TagAliasCallback() {
                                @Override
                                public void gotResult(int i, String s, Set<String> set) {
                                    if (i == 0) {
                                        LogUtil.i("极光推送别名设置成功，别名：" + s);
                                    }
                                }
                            });

                            socketLogin(user.getToken());

                            Intent intent = new Intent(RegisterActivity.this, CodeActivity.class);
                            startActivity(intent);
                            finish();
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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    private void initBar(){

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("注册");
        mTitleBar.setActivity(this);
    }

    private void socketLogin(String token) {
        CampusLoginSend campusLoginSend = new CampusLoginSend();
        campusLoginSend.token = token;
        campusLoginSend.device = CommonUtil.getAppuuid(this);
        mSocket.emit(Static.SOCKET_LOGIN, GsonUtils.toJson(campusLoginSend));
        LogUtil.i("campusLoginSend.token: " + campusLoginSend.token);
        LogUtil.i("campusLoginSend.device: " + campusLoginSend.device);
    }

}
