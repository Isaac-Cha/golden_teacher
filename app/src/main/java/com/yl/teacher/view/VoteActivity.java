package com.yl.teacher.view;

import android.content.Intent;
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
import com.yl.teacher.adapter.VoteAdapter;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.Vote;
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
 * 投票列表页面
 */
public class VoteActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private boolean flagFirst = true;


    private VoteAdapter adapter;

    private PullToRefreshListView pullToRefreshListView;

    private final static int limit = 10;

    private int page = 1;

    private int maxPage;

    private int modeEvent;

    private List<Vote> mList;

    private LinearLayout linearNodata;

    private TextView tvNodata;

    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_VOTELIST) {
            modeEvent = Static.LIST_REFRESH;
            page = 0;
            CustomProgress.show(this, "加载中...", true, null);
            init();
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
        MobclickAgent.onPageStart("VoteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("VoteActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_vote;
    }

    @Override
    protected void initViews() {
        EventBus.getDefault().register(this);
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        linearNodata = (LinearLayout) findViewById(R.id.linearNodata);
        tvNodata = (TextView) findViewById(R.id.tvNodata);
        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lvVote);

    }

    @Override
    protected void initData() {
        init();
    }

    @Override
    protected void initListener() {

        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

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
                    pullToRefreshListView.onRefreshComplete();
                }
            }
        });

        pullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(VoteActivity.this, VoteDetaileActivity.class);
                intent.putExtra("id", mList.get(position - 1).getId());
                startActivity(intent);

            }
        });
    }

    private void init() {
        if (flagFirst) {
            flagFirst = false;
            CustomProgress.show(this, "加载中...", true, null);
        }

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/vote/list?token="+ MyApplication.getInstance().getShareUser().getString("token", "")+"&Class_id="+MyApplication.getInstance().getClassId()+"&offset="+page+"&limit="+limit);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("Class_id", MyApplication.getInstance().getClassId());
        mParams.put("offset", page + "");
        mParams.put("limit", limit + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/vote/list", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        pullToRefreshListView.onRefreshComplete();
                        LogUtil.e("Vote: " + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            int count = response.getData().optJSONObject("data").optInt("count");
                            maxPage = CommonUtil.getMaxPage(count, limit);
                            List<Vote> tmpData = Vote.getVoteListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));

                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    if (tmpData.size() == 0) {
                                        tvNodata.setText("暂无数据哦！");
                                        linearNodata.setVisibility(View.VISIBLE);
                                        pullToRefreshListView.setVisibility(View.GONE);
                                    } else {
                                        linearNodata.setVisibility(View.GONE);
                                        pullToRefreshListView.setVisibility(View.VISIBLE);
                                        mList = tmpData;
                                        if (null != mList && mList.size() > 0) {
                                            adapter = new VoteAdapter(VoteActivity.this, mList, linearNodata);
                                            pullToRefreshListView.setAdapter(adapter);
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
                                    page = 1;
                                    tvNodata.setText("暂无数据哦！");
                                    linearNodata.setVisibility(View.VISIBLE);
                                    pullToRefreshListView.setVisibility(View.GONE);
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
                        pullToRefreshListView.onRefreshComplete();

                        switch (modeEvent) {
                            case Static.LIST_REFRESH:
                                page = 1;
                                break;

                            case Static.LIST_MORE:
                                page--;
                                break;
                        }

                        if (ex instanceof HttpException) {
                            // 网络错误
                            if (modeEvent == Static.LIST_REFRESH) {
                                linearNodata.setVisibility(View.VISIBLE);
                                tvNodata.setText("网络异常！");
                                pullToRefreshListView.setVisibility(View.GONE);
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

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("投票列表");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("发起");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoteActivity.this, AddVoteActivity.class);
                startActivity(intent);
            }
        });
    }

}
