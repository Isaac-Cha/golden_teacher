package com.yl.teacher.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.db.Push;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.Student;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

public class StudentsActivity extends BaseActivity {

    private WebView webView;
    private CustomTitleBar custom_titlebar;
    private static final int KEY_STUDENTLOG = 2; // 学生点滴
    private DbManager db;
    private String token;
    private String classId;

    @Override
    protected int loadLayout() {
        return R.layout.activity_students;
    }

    @Override
    protected void initViews() {
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        webView = (WebView) findViewById(R.id.webview);
    }

    @Override
    protected void initData() {
        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar.displayLeftBtnTwo(true);
        custom_titlebar.setLeftBtnTwoIcon(R.drawable.back);
        CustomProgress.show(this, "加载中...", true, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.addJavascriptInterface(this, "jsObj");
        classId = MyApplication.getInstance().getClassId();
        LogUtil.e("classId: " + classId);
        db = x.getDb(MyApplication.daoConfig);
        String url = Static.URL_SERVER + "/teacher/v1/classMember/list?token=" + token + "&Class_id=" + classId;
        webView.loadUrl(url);
    }

    @JavascriptInterface
    public void writeStudentLog(String studentId) {
        LogUtil.e("studentId: " + studentId);
        getStudentInfo(studentId);
    }

    @Override
    protected void initListener() {
        custom_titlebar.setLeftBtnOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    WebViewClient mWebViewClient = new WebViewClient() {

        // 加载完成
        @Override
        public void onPageFinished(WebView view, String url) {
            CustomProgress.hideDialog();
            custom_titlebar.setCenterTitle(webView.getTitle());
        }

        // 加载出错
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            CustomProgress.hideDialog();
        }

        // 开始加载
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            custom_titlebar.setCenterTitle("加载中...");
        }
    };

    WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                CustomProgress.hideDialog();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.onPause();
            webView.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }

        return true;
    }

    @Override
    protected void onResume() {
        try {
            Push push = db.selector(Push.class).where("type", "=", KEY_STUDENTLOG).findFirst();
            if (push != null) {
                LogUtil.e("push.classId: " + push.classId);
                if (!classId.equals(push.classId)) {
                    classId = push.classId + "";
                    webView.clearHistory();
                    String url = Static.URL_SERVER + "/teacher/v1/classMember/list?token=" + token + "&Class_id=" + classId;
                    webView.loadUrl(url);
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        webView.reload();
        super.onResume();
        MobclickAgent.onPageStart("StudentsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("StudentsActivity");
        MobclickAgent.onPause(this);
    }


    private void getStudentInfo(String studentId) {

        CustomProgress.show(this, "请等待...", false, null);

        // $host/teacher/v2/studentLog/create-log
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("to_User_id", studentId);

        x.http().post(HttpUtils.getRequestParams("/teacher/v2/studentLog/create-log", mParams),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        CustomProgress.hideDialog();

                        Response response = CommonUtil.checkResponse(s);
                        if (response.isStatus()) {
                            LogUtil.e("getStudentInfo: " + s);
                            Student mStudent = GsonUtils.fromJson(s, Student.class);
                            Intent mIntent = new Intent(StudentsActivity.this, StudentLogWriteActivity.class);
                            mIntent.putExtra(StudentLogWriteActivity.INTENT_STUDENT, mStudent.data);
                            startActivity(mIntent);

                        } else {
                            UiUtils.showToast(response.getData().optString("message"));
                        }

                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        if (throwable instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) throwable;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            LogUtil.d(responseCode + ":" + responseMsg);
                            UiUtils.showToast(R.string.net_error);
                            // ...
                        } else { // 其他错误
                            // ...
                        }
                        CustomProgress.hideDialog();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                        CustomProgress.hideDialog();
                    }

                    @Override
                    public void onFinished() {
                        CustomProgress.hideDialog();
                    }
                });

    }

}
