package com.yl.teacher.view;

import android.content.Intent;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.db.Guide;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.User;
import com.yl.teacher.util.AppUtils;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 启动页面
 */
public class StartActivity extends BaseActivity {
    private DbManager dbManager;

    private Guide guide;

    private static android.os.Handler handler = new android.os.Handler() {


    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_start;
    }

    @Override
    protected void initViews() {
        dbManager = x.getDb(MyApplication.getInstance().daoConfig);
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            guide = dbManager.selector(Guide.class).where("version", "=", MyApplication.getInstance().VERSION_CODE).findFirst();
                            if (guide != null) {
                                LogUtil.d("" + guide.getVersion() + "---" + guide.getIntial());
                                if (!"".equals(MyApplication.getInstance().getShareUser().getString("token", ""))) {
                                    doLogin();
                                } else {
                                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                MyApplication.getInstance().clearShareUser();
                                Intent intent = new Intent(StartActivity.this, GuideActivity.class);
                                startActivity(intent);
                                finish();
                            }

                        } catch (DbException e) {
                            e.printStackTrace();
                            MyApplication.getInstance().clearShareUser();
                            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }
                });
            }
        });
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }

    /**
     * 自动登录
     */
    private void doLogin() {
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/autoLogin");

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("source", Static.SOURCE);
        mParams.put("version", StringUtils.getVersionName());

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/autoLogin", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            if (response.getData().optJSONObject("data") != null && response.getData().optJSONObject("data").toString().length() > 0) {

                                User user = User.getUserFromJsonObj(response.getData().optJSONObject("data"));
                                User.setCurrentUser(user);
                                MobclickAgent.onProfileSignIn(user.getNickName());

                                // 环信登录
                                if (!EMClient.getInstance().isLoggedInBefore() && !StringUtils.isEmpty(user.hxId) && !StringUtils.isEmpty(user.hxPwd))
                                    AppUtils.loginEmmobAndSaveInfo(user);

                                // 极光推送设置别名
                                JPushInterface.setAlias(StartActivity.this, user.getId() + "", new TagAliasCallback() {
                                    @Override
                                    public void gotResult(int i, String s, Set<String> set) {
                                        if (i == 0) {
                                            LogUtil.i("极光推送别名设置成功，别名：" + s);
                                        }
                                    }
                                });

                                if ("".equals(user.getInviteCode())) {
                                    Intent intent = new Intent(StartActivity.this, CodeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    if ("".equals(user.getSchoolId())) {
                                        Intent intent = new Intent(StartActivity.this, InfoActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(StartActivity.this, ClassesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
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
                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        } else { // 其他错误
                            // ...
                        }
                        Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

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