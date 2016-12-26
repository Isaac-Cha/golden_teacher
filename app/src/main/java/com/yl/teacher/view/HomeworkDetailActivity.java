package com.yl.teacher.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.db.Push;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

public class HomeworkDetailActivity extends BaseActivity {

    private static final int KEY_HOMEWORK = 6; // 作业

    private CustomTitleBar custom_titlebar;
    private WebView webView;
    private int homeworkId = -1;
    private String token;
    private DbManager db;
    private Push push;
    private int lastId;

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

        db = x.getDb(MyApplication.daoConfig);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        CustomProgress.show(this, "加载中...", true, null);
        custom_titlebar.setCenterTitle("作业详情");
        custom_titlebar.displayBackBtn(true);
        webView.setWebChromeClient(mWebChromeClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(mWebViewClient);
        webView.setWebChromeClient(mWebChromeClient);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        homeworkId = getIntent().getIntExtra("homeworkId", -1);

    }

    @Override
    protected void initListener() {
        custom_titlebar.setActivity(this);
    }

    WebViewClient mWebViewClient = new WebViewClient() {

        // 加载完成
        @Override
        public void onPageFinished(WebView view, String url) {
            CustomProgress.hideDialog();
        }

        // 加载出错
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            CustomProgress.hideDialog();
        }

        // 开始加载
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

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
        if (webView != null) {
            webView.onPause();
            webView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {

        if (homeworkId == -1) {
            try {
                push = getPush();
                homeworkId = push.tid;
                lastId = homeworkId;
            } catch (DbException e) {
                homeworkId = -1;
            }
        }

        if (lastId != 0) {
            try {
                push = getPush();
                if (lastId != push.tid) {
                    homeworkId = push.tid;
                    lastId = homeworkId;
                }
            } catch (DbException e) {

            }
        }
        LogUtil.e("homeworkId: " + homeworkId);
        String url = Static.URL_SERVER + "/teacher/v1/homework/info?token=" + token + "&Homework_id=" + homeworkId;;
        webView.loadUrl(url);

        super.onResume();
        MobclickAgent.onPageStart("HomeworkDetailActivity");
        MobclickAgent.onResume(this);
    }

    private Push getPush() throws DbException {
        return db.selector(Push.class).where("type", "=", KEY_HOMEWORK).findFirst();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeworkDetailActivity");
        MobclickAgent.onPause(this);
    }

}
