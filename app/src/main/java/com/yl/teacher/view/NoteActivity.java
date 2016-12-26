package com.yl.teacher.view;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Quest;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 请求意见页面
 */
public class NoteActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private EditText etComment;

    private TextView tvConfirm;

    private Quest quest;

    private int position;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("NoteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("NoteActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_note;
    }

    @Override
    protected void initViews() {
        quest = (Quest) getIntent().getSerializableExtra("quest");
        position = getIntent().getIntExtra("pos",0);

        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        etComment = (EditText)findViewById(R.id.etComment);
        tvConfirm = (TextView) findViewById(R.id.tvConfirm);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doReply();
            }
        });
    }

    private void doReply(){
        CustomProgress.show(this, "提交中...", true, null);
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String content = etComment.getText().toString();
//        String url = Static.URL_SERVER + "/teacher/v1/classMemberRequest/reply";
//        RequestParams params = new RequestParams(url);
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("request_id", quest.getId());
        mParams.put("content", content);
        mParams.put("status", "1");
        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMemberRequest/reply", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            Action action = new Action();
                            action.setId(Static.EVENTBUS_TYPE_QUEST);
                            action.setPos(position);
                            EventBus.getDefault().post(action);
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
        mTitleBar.setCenterTitle("请求意见");
        mTitleBar.setActivity(this);
    }

}
