package com.yl.teacher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.ReplyAdapter;
import com.yl.teacher.db.Push;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Quest;
import com.yl.teacher.model.Reply;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.NoScrollListView;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 待请求页面
 */
public class ReviewedActivity extends BaseActivity {

    private static final int KEY_CLASSMEMEBERREQUEST = 3; // 请求

    private CustomTitleBar mTitleBar;

    private int position;

    private Quest quest;

    private List<Reply> replyList;

    private NoScrollListView lvReply;

    private ReplyAdapter adapter;
    private String questId;
    private DbManager db;
    private String token;
    private String lastId;
    private boolean isFirst;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ReviewedActivity");
        MobclickAgent.onResume(this);

        if (quest != null) {
            questId = quest.getId();
        } else {
            try {
                Push push = getPush();
                if (push != null) {
                    questId = push.tid+"";
                    lastId = questId;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        if (!StringUtils.isEmpty(lastId)) {
            try {
                Push push = getPush();
                if (push != null && !lastId.equals(push.tid+"")) {
                    questId = push.tid+"";
                    lastId = questId;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        initDetail();

    }

    private Push getPush() throws DbException {
        return db.selector(Push.class).where("type", "=", KEY_CLASSMEMEBERREQUEST).findFirst();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ReviewedActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_reviewed;
    }

    @Override
    protected void initViews() {

        position = getIntent().getIntExtra("pos",-1);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        lvReply = (NoScrollListView) findViewById(R.id.lvReply);

    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        db = x.getDb(MyApplication.daoConfig);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            quest =  (Quest) bundle.getSerializable("quest");
        }

    }

    @Override
    protected void initListener() {

    }

    /**
     * 添加阅读记录
     */
    private void addRead() {
        // /teacher/v1/classMemberRequest/reply?token=**&request_id=**&status=**&content=**
//        String url = Static.URL_SERVER + "/teacher/v1/classMemberRequest/reply";
//        RequestParams mParams = new RequestParams(url);
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("request_id", questId);
        mParams.put("status", "2");
        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMemberRequest/reply", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {

            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                if (throwable instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) throwable;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    LogUtil.d(responseCode + ":" + responseMsg);
                    // ...
                } else { // 其他错误
                    // ...
                }
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void initDetail(){

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "加载中...", true, null);
        }

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classMemberRequest/info?token="+ token +"&request_id="+questId);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("request_id", questId);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMemberRequest/info", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            quest = Quest.getQuestFromJsonObj(response.getData().optJSONObject("data"));

                            initBar();

                            if("1".equals(quest.getReplyStatus())){
                                initReply();
                            }else{
                                CustomProgress.hideDialog();
                                replyList = new ArrayList<>();
                                adapter= new ReplyAdapter(ReviewedActivity.this,replyList,quest);
                                lvReply.setAdapter(adapter);
                            }

                            // 如果不是已审核，才需要改变阅读状态
                            if (!"1".equals(quest.getStatus())) {
                                addRead();
                            }

                        } else {
                            CustomProgress.hideDialog();
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

    private void initReply(){

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classMemberRequest/reply-list?token="+ MyApplication.getInstance().getShareUser().getString("token", "")+"&request_id="+quest.getId());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("request_id", quest.getId());

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMemberRequest/reply-list", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            replyList = Reply.getReplyListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));
                            adapter= new ReplyAdapter(ReviewedActivity.this,replyList,quest);
                            lvReply.setAdapter(adapter);
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
        mTitleBar.setCenterTitle(""+quest.getTitle());
        mTitleBar.setActivity(this);
        if(!"1".equals(quest.getReplyStatus())){
            mTitleBar.setRightTitle("确认");
            mTitleBar.displayRightItem(true);
            mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReviewedActivity.this,NoteActivity.class);
                    intent.putExtra("pos",position);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("quest",quest);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Action action) {
        LogUtil.e("已回调");
        if(action.getId()== Static.EVENTBUS_TYPE_QUEST){
            LogUtil.e("已回调IF");
            mTitleBar.displayRightItem(false);
        }
    }

}
