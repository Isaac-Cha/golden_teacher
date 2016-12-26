package com.yl.teacher.view;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
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

/**
 * 忘记密码页面
 */
public class ForgetActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private Button btnValid;

    private int second=60;

    private boolean flagValid;

    private TextView tvLogin;

    private EditText etPhone;

    private EditText etPwd;

    private EditText etValid;

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
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ForgetActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ForgetActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_forget_pwd;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        btnValid = (Button) findViewById(R.id.btnValid);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        etPhone = (EditText) findViewById(R.id.etPhone);
        etPwd = (EditText) findViewById(R.id.etPwd);
        etValid = (EditText) findViewById(R.id.etValid);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtils.isEmpty(etPwd.getText().toString()) || StringUtils.isEmpty(etPhone.getText().toString())||StringUtils.isEmpty(etValid.getText().toString())) {
                    Toast.makeText(ForgetActivity.this, "手机号，验证码和新密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                flagName = CommonUtil.checkMobileNumber(etPhone.getText().toString());

                flagPwd = CommonUtil.isPwdRight(etPwd.getText().toString());

                if(!flagName){
                    Toast.makeText(ForgetActivity.this, "用户名必须是手机！", Toast.LENGTH_SHORT).show();
                }
                if(!flagPwd){
                    Toast.makeText(ForgetActivity.this, "密码格式不正确！", Toast.LENGTH_SHORT).show();
                }
                if (flagName && flagPwd) {
                    handlerKeyboard(getWindow().peekDecorView(), false);
                    doReset();
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
     * 获取验证码
     */
    private void getValidNum(){

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/common/v1/sms/send-verification-code?phone=" + etPhone.getText().toString() + "&kind=3");

        Map<String, String> mParams = new HashMap<>();
        mParams.put("phone", etPhone.getText().toString().trim());
        mParams.put("kind", "3");

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

    private void doReset(){

        CustomProgress.show(this, "加载中...", true, null);

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/reset-pwd?account=" + etPhone.getText().toString() + "&password=" + etPwd.getText().toString() + "&code=" + etValid.getText().toString());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("account", etPhone.getText().toString().trim());
        mParams.put("password", etPwd.getText().toString().trim());
        mParams.put("code", etValid.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/reset-pwd", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            MyApplication.getInstance().setShareApp(etPhone.getText().toString(), etPwd.getText().toString());
                            Intent intent = new Intent(ForgetActivity.this, ClassesActivity.class);
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
    private void initBar(){

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("忘记密码");
        mTitleBar.setActivity(this);
    }

}
