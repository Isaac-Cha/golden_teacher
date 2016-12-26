package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
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
 * 邀请码页面
 */
public class CodeActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private EditText etCode;
    private String mToken;
    private Socket mSocket;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CodeActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CodeActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_code;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        etCode = (EditText) findViewById(R.id.etCode);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    /**
     * 检验邀请码
     */
    private void checkCode(){

        CustomProgress.show(this, "加载中...", true, null);
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String inviteCode = etCode.getText().toString();

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/add-invitecode");
//        params.addQueryStringParameter("inviteCode", inviteCode);
//        params.addQueryStringParameter("token", token);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("inviteCode", inviteCode);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/add-invitecode", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            Intent intent = new Intent(CodeActivity.this, InfoActivity.class);
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
        mTitleBar.setCenterTitle("邀请码");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("确认");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCode();
            }
        });
    }

}
