package com.yl.teacher.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.User;
import com.yl.teacher.util.AppUtils;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private AlertDialog mUploadDialog;

    private View dialogView;

    private TextView tvPhone;

    private TextView tvLogin;

    private TextView tvRegister;

    private TextView tvForget;

    private TextView tvCancel;

    private TextView tvConfirm;

    private TextView tvService;

    private EditText etName;

    private EditText etPwd;

    private boolean flagName;
    private boolean flagPwd;
    private int loginType;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("LoginActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("LoginActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_login;
    }

    @Override
    protected void initViews() {

        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        tvService = (TextView) findViewById(R.id.tvService);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);
        tvForget = (TextView) findViewById(R.id.tvForget);
        etName = (EditText) findViewById(R.id.etName);
        etPwd = (EditText) findViewById(R.id.etPwd);
    }

    @Override
    protected void initData() {

        loginType = getIntent().getIntExtra(Static.LOGIN_TYPE, -1);
        showLoginDialog();

    }

    @Override
    protected void initListener() {
        tvService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (StringUtils.isEmpty(etName.getText().toString())) {

                    Toast.makeText(LoginActivity.this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtils.isEmpty(etPwd.getText().toString())) {

                    Toast.makeText(LoginActivity.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                flagName = CommonUtil.checkMobileNumber(etName.getText().toString());

                flagPwd = CommonUtil.isPwdRight(etPwd.getText().toString());

                if (!flagName) {
                    Toast.makeText(LoginActivity.this, "用户名必须是手机！", Toast.LENGTH_SHORT).show();
                }
                if (!flagPwd) {
                    Toast.makeText(LoginActivity.this, "密码格式不正确！", Toast.LENGTH_SHORT).show();
                }
                if (flagName && flagPwd) {
                    handlerKeyboard(getWindow().peekDecorView(), false);
                    doLogin();
                }
            }
        });
        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        tvForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 登录操作
     */
    private void doLogin() {

        CustomProgress.show(this, "登录中...", true, null);

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/login");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("account", etName.getText().toString());
        mParams.put("password", etPwd.getText().toString());
        mParams.put("ip", MyApplication.getPhoneIp());
        mParams.put("deviceId", MyApplication.UUID);
        mParams.put("source", Static.SOURCE);
        mParams.put("version", StringUtils.getVersionName());

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/login", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            if (response.getData().optJSONObject("data") != null && response.getData().optJSONObject("data").toString().length() > 0) {

                                User user = User.getUserFromJsonObj(response.getData().optJSONObject("data"));
                                User.setCurrentUser(user);
                                MobclickAgent.onProfileSignIn(user.getNickName());

                                // 极光推送设置别名
                                JPushInterface.setAlias(LoginActivity.this, user.getId() + "", new TagAliasCallback() {
                                    @Override
                                    public void gotResult(int i, String s, Set<String> set) {
                                        if (i == 0) {
                                            LogUtil.i("极光推送别名设置成功，别名：" + s);
                                        }
                                    }
                                });

                                if (!StringUtils.isEmpty(user.hxId) && !StringUtils.isEmpty(user.hxPwd))
                                    AppUtils.loginEmmobAndSaveInfo(user);

                                if ("".equals(user.getInviteCode())) {
                                    Intent intent = new Intent(LoginActivity.this, CodeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    if ("".equals(user.getSchoolId())) {
                                        Intent intent = new Intent(LoginActivity.this, InfoActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, ClassesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }

                            }
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
     * isOpen = true  显示
     * isOpen = false 隐藏
     * 当v为空时可以调用getWindow().peekDecorView()获取整个屏幕的View
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

    private void showDialog() {
        if (null == dialogView) {
            dialogView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_service, null);
            tvPhone = (TextView) dialogView.findViewById(R.id.tvPhone);
            tvConfirm = (TextView) dialogView.findViewById(R.id.tvConfirm);
            tvCancel = (TextView) dialogView.findViewById(R.id.tvCancel);

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUploadDialog.dismiss();
                }
            });

            tvConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUploadDialog.dismiss();
                    toLineService();
                }
            });
            mUploadDialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();
        }
        mUploadDialog.setCanceledOnTouchOutside(false);
        mUploadDialog.show();
    }

    private void toLineService() {
        //判断QQ是否安装（“*”是需要联系QQ号）
        if (!isQQClientAvailable()) {
            //没有安装QQ会展示网页
//            url = "http://wpa.qq.com/msgrd?v=3&uin=173793765&site=qq&menu=yes";
            UiUtils.showToast("请先安装手机QQ");
            return;
        } else {
            //安装了QQ会直接调用QQ，打开手机QQ进行会话
            String url = "mqqwpa://im/chat?chat_type=wpa&uin=205020059&version=1&src_type=web&web_src=oicqzone.com";
            Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(mIntent);
        }

    }

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("登录");

    }

    private boolean isQQClientAvailable() {
        final PackageManager packageManager = getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 根据登录状态显示弹窗
     */
    private void showLoginDialog() {

        switch (loginType) {

            case Static.ACCOUNT_CONFLICT: // 帐号别处登录
                showConflictDialog("您的帐号在其它设备登录");
                break;

            case Static.ACCOUNT_REMOVED: // 帐号被移除
                showConflictDialog("您的帐号被移除");
                break;

            default:
                break;

        }

    }

    private void showConflictDialog(String text) {

        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE, false)
                .setTitleText("请重新登录")
                .setContentText(text)
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .show();

    }

}
