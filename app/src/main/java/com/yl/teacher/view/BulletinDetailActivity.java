package com.yl.teacher.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.db.Push;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Action;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import de.greenrobot.event.EventBus;

/**
 * 公告详情界面
 */
public class BulletinDetailActivity extends BaseActivity {

    private static final int KEY_ANNOUNCEMENT = 4; // 公告

    private WebView wv_bulletindetail;
    private String url;
    private DbManager db;
    private int announId;
    private String token;
    private int lastId;
    private TextView tv_title;
    private RelativeLayout rl_back;
    private RelativeLayout rl_gohome;

    @Override
    protected int loadLayout() {
        return R.layout.activity_bulletindetail;
    }

    @Override
    protected void initViews() {
        tv_title = (TextView) findViewById(R.id.tv_title);
        rl_gohome = (RelativeLayout) findViewById(R.id.rl_gohome);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        wv_bulletindetail = (WebView) findViewById(R.id.wv_bulletindetail);
    }

    @SuppressLint("JavascriptInterface")
    @Override
    protected void initData() {
        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        db = x.getDb(MyApplication.daoConfig);
        announId = getIntent().getIntExtra("announId", -1);

        LogUtil.e("announId: " + announId);

        CustomProgress.show(this, "加载中...", true, null);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        WebSettings webSettings = wv_bulletindetail.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv_bulletindetail.setWebViewClient(mWebViewClient);
        wv_bulletindetail.setWebChromeClient(mWebChromeClient);
        wv_bulletindetail.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        wv_bulletindetail.getSettings().setJavaScriptEnabled(true);
        wv_bulletindetail.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        wv_bulletindetail.addJavascriptInterface(this, "jsObj");

    }

    @Override
    protected void initListener() {

        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityFinish();
            }
        });

        rl_gohome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Action action = new Action();
                action.setId(Static.EVENTBUS_TYPE_BULLETIN_GO_HOME);
                EventBus.getDefault().post(action);
                finish();
            }
        });


    }

    private WebViewClient mWebViewClient = new WebViewClient() {

        // 加载完成
        @Override
        public void onPageFinished(WebView view, String url) {
            CustomProgress.hideDialog();
            String title = wv_bulletindetail.getTitle();
            tv_title.setText(title);
        }

        // 加载出错
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            CustomProgress.hideDialog();
            UiUtils.showToast(R.string.net_error);
        }

        // 开始加载
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            String title = wv_bulletindetail.getTitle();
            tv_title.setText(title);
        }
    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
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
        if (wv_bulletindetail != null) {
            wv_bulletindetail.onPause();
            wv_bulletindetail.destroy();
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            activityFinish();
        } else {
            finish();
        }

        return true;
    }

    @Override
    protected void onResume() {
        if (announId == -1) {
            try {
                Push push = getPush();
                if (push != null) {
                    announId = push.tid;
                    lastId = announId;
                }
                LogUtil.e("announId: " + push.tid);
            } catch (DbException e) {
            }
        }

        if (lastId != 0) {
            try {
                Push push = getPush();
                if (push != null && lastId != push.tid) {
                    announId = push.tid;
                    lastId = announId;
                }
            } catch (DbException e) {
            }
        }

        url = Static.URL_SERVER + "/teacher/v1/announcement/info?token=" + token + "&Announ_id=" + announId;
        wv_bulletindetail.loadUrl(url);
        LogUtil.e(url);
        super.onResume();
        MobclickAgent.onPageStart("BulletinDetailActivity");
        MobclickAgent.onResume(this);
    }

    private Push getPush() throws DbException {
        return db.selector(Push.class).where("type", "=", KEY_ANNOUNCEMENT).findFirst();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BulletinDetailActivity");
        MobclickAgent.onPause(this);
    }

    private void activityFinish() {
        if (wv_bulletindetail != null && wv_bulletindetail.canGoBack()) {
            wv_bulletindetail.goBack();
        } else {
            finish();
        }
    }

}
