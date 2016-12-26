package com.yl.teacher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.MyClass;
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
 * 创建班级页面
 */
public class CreateActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private EditText etName;

    private boolean clickable = true;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CreateActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CreateActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_create;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        etName = (EditText) findViewById(R.id.etName);


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    private void doCreate() {

        CustomProgress.show(this, "加载中...", true, null);

        String token = MyApplication.getInstance().getShareUser().getString("token", "");
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/schoolClass/add?token="+ token);
//        params.addQueryStringParameter("name",etName.getText().toString());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("name", etName.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/schoolClass/add", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {

                            MyClass myClass = MyClass.getCreateClassFromJsonObj(response.getData().optJSONObject("data"));

                            Intent intent = new Intent(CreateActivity.this, CreateSuccessActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("myClass", myClass);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                        }
                        clickable = !clickable;
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
                        clickable = true;
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                        clickable = true;
                    }

                    @Override
                    public void onFinished() {
                        clickable = true;
                    }
                });
    }

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("创建班级");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("确认");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(etName.getText().toString())) {
                    Toast.makeText(x.app(), "班级名字不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (clickable) {
                    clickable = false;
                    doCreate();
                }

            }
        });
    }

}
