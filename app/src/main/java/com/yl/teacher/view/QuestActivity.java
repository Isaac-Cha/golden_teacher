package com.yl.teacher.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.QuestAdapter;
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
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 请求列表页面
 */
public class QuestActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private boolean flagFirst = true;

    private PullToRefreshListView lvQuest;

    private QuestAdapter adapter;

    private int modeEvent;

    private final static int limit = 10;

    private int page = 1;

    private int maxPage;

    private List<Quest> mList;

    private LinearLayout linearNodata;

    private TextView tvNodata;
    private String token;

    public void onEventMainThread(Action action) {
        if(action.getId()== Static.EVENTBUS_TYPE_QUEST){
            mList.get(action.getPos()).setReplyStatus("1");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("QuestActivity");
        MobclickAgent.onResume(this);
        modeEvent = Static.LIST_REFRESH;
        page = 1;
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("QuestActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_quest;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        linearNodata = (LinearLayout)findViewById(R.id.linearNodata);
        tvNodata = (TextView)findViewById(R.id.tvNodata);
        lvQuest = (PullToRefreshListView) findViewById(R.id.lvQuest);

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {

        lvQuest.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                modeEvent = Static.LIST_REFRESH;
                page = 1;
                init();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

                page = page + 1;

                if (page <= maxPage) {
                    modeEvent = Static.LIST_MORE;
                    init();
                } else {

                    Toast.makeText(x.app(), x.app().getResources().getString(R.string.no_more_data), Toast.LENGTH_SHORT).show();
                    lvQuest.onRefreshComplete();
                }
            }
        });

        lvQuest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(QuestActivity.this,ReviewedActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("quest",mList.get(position - 1));
                intent.putExtra("pos",position - 1);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }

    private void init(){

        if(flagFirst){
            flagFirst = false;
            CustomProgress.show(this, "加载中...", true, null);
        }

        token = MyApplication.getInstance().getShareUser().getString("token", "");

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classMemberRequest/list?token="+ token +"&Class_id="+MyApplication.getInstance().getClassId()+"&offset="+page+"&limit="+limit);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", MyApplication.getInstance().getClassId());
        mParams.put("offset", page + "");
        mParams.put("limit", limit + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMemberRequest/list", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        lvQuest.onRefreshComplete();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            int count = response.getData().optJSONObject("data").optInt("count");
                            maxPage = CommonUtil.getMaxPage(count, limit);
                            List<Quest> tmpData = Quest.getQuestListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));
                            //LogUtil.d(""+tmpData.size());
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    if(tmpData.size()==0){
                                        tvNodata.setText("暂无数据哦！");
                                        linearNodata.setVisibility(View.VISIBLE);
                                        lvQuest.setVisibility(View.GONE);
                                    }else{
                                        linearNodata.setVisibility(View.GONE);
                                        lvQuest.setVisibility(View.VISIBLE);
                                        mList = tmpData;
                                        if(null!=mList && mList.size()>0){
                                            adapter = new QuestAdapter(QuestActivity.this,mList);
                                            lvQuest.setAdapter(adapter);
                                        }
                                    }

                                    break;

                                case Static.LIST_MORE:
                                    mList.addAll(mList.size(), tmpData);
                                    adapter.setData(mList);
                                    break;
                            }


                        } else {

                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    page=1;
                                    tvNodata.setText("暂无数据哦！");
                                    linearNodata.setVisibility(View.VISIBLE);
                                    lvQuest.setVisibility(View.GONE);
                                    break;

                                case Static.LIST_MORE:
                                    page--;
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();
                        lvQuest.onRefreshComplete();

                        switch (modeEvent) {
                            case Static.LIST_REFRESH:
                                page=1;
                                break;

                            case Static.LIST_MORE:
                                page--;
                                break;
                        }

                        if (ex instanceof HttpException) { // 网络错误
                            if(modeEvent==Static.LIST_REFRESH){
                                linearNodata.setVisibility(View.VISIBLE);
                                tvNodata.setText("网络异常！");
                                lvQuest.setVisibility(View.GONE);
                            }
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
        mTitleBar.setCenterTitle("请求列表");
        mTitleBar.setActivity(this);
    }

}
