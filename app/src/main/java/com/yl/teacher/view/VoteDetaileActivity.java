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
import org.xutils.ex.DbException;
import org.xutils.x;

/**
 * 投票详情页面
 */
public class VoteDetaileActivity extends BaseActivity {

    private static final int KEY_VOTE = 5; // 投票

    private CustomTitleBar mTitleBar;

    private WebView webVote;

    private String subId;
    private String token;
    private String lastId;

    @Override
    protected void onResume() {

        if (StringUtils.isEmpty(subId)) {
            try {
                Push push = getPush();
                if (push != null) {
                    subId = push.tid+"";
                    lastId = subId;
                }
            } catch (DbException e) {

            }
        }

        if (!StringUtils.isEmpty(lastId)) {
            try {
                Push push = getPush();
                if (push != null && !lastId.equals(push.tid)) {
                    subId = push.tid+"";
                    lastId = subId;
                }
            } catch (DbException e) {

            }
        }

        webVote.loadUrl(Static.URL_SERVER + "/teacher/v1/vote/info?token=" + token + "&VoteSubject_id="+subId);

        super.onResume();
        MobclickAgent.onPageStart("VoteDetaileActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("VoteDetaileActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_vote_detaile;
    }

    @Override
    protected void initViews() {
        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        subId = getIntent().getStringExtra("id");
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        CustomProgress.show(this, "加载中...", true, null);
        webVote = (WebView)findViewById(R.id.webVote);
        webVote.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webVote.getSettings().setJavaScriptEnabled(true);
        webVote.setWebViewClient(new MyWebViewClient());
        webVote.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webVote.setWebChromeClient(new MyWebChromeClient());

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

    }
    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                CustomProgress.hideDialog();
            }
        }


    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            CustomProgress.hideDialog();
        }

    }
    private void initBar(){
        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("投票详情");
        mTitleBar.setActivity(this);
    }
    private Push getPush() throws DbException {
        DbManager db = x.getDb(MyApplication.daoConfig);
        return db.selector(Push.class).where("type", "=", KEY_VOTE).findFirst();
    }
}
