package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.JoinAdapter;
import com.yl.teacher.db.Push;
import com.yl.teacher.model.Join;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
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
import java.util.List;
import java.util.Map;

/**
 * 入班申请页面
 */
public class JoinActivity extends BaseActivity {

    private static final int KEY_PERMIT = 8; // 待审批

    private CustomTitleBar mTitleBar;

    private boolean flagFirst = true;

    private PullToRefreshListView lvJoin;

    private final static int limit = 10;

    private int page = 1;

    private int maxPage;

    private int modeEvent;

    private List<Join> mList;

    private JoinAdapter adapter;

    private LinearLayout linearNodata;

    private TextView tvNodata;
    private String classId;
    private String token;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("JoinActivity");
        MobclickAgent.onResume(this);
        modeEvent = Static.LIST_REFRESH;
        page = 1;
        init();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("JoinActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_join;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        linearNodata = (LinearLayout) findViewById(R.id.linearNodata);
        tvNodata = (TextView) findViewById(R.id.tvNodata);
        lvJoin = (PullToRefreshListView) findViewById(R.id.lvJoin);
    }

    @Override
    protected void initData() {

        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        classId = MyApplication.getInstance().getClassId();

        try {
            Push push = getPush();
            if (push != null && StringUtils.isEmpty(classId)) {
                classId = push.classId + "";
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void initListener() {
        lvJoin.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
                    lvJoin.onRefreshComplete();
                }
            }
        });

    }

    /**
     * 获取列表
     */
    private void init() {
        if (flagFirst) {
            flagFirst = false;
            CustomProgress.show(this, "加载中...", true, null);
        }
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/joinApply/join-list?Class_id=" + classId + "&status=0&offset=" + page + "&limit=" + limit);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("Class_id", classId);
        mParams.put("status", "0");
        mParams.put("offset", page + "");
        mParams.put("limit", limit + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/joinApply/join-list", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        lvJoin.onRefreshComplete();
                        CustomProgress.hideDialog();
                        LogUtil.d("result: " + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            if (response.getData().optJSONObject("data") == null) {
                                if (modeEvent == Static.LIST_REFRESH) {
                                    tvNodata.setText("暂无数据哦！");
                                    linearNodata.setVisibility(View.VISIBLE);
                                    lvJoin.setVisibility(View.GONE);
                                }

                            } else {
                                linearNodata.setVisibility(View.GONE);
                                lvJoin.setVisibility(View.VISIBLE);
                                int count = response.getData().optJSONObject("data").optInt("count");
                                maxPage = CommonUtil.getMaxPage(count, limit);
                                List<Join> tmpData = Join.getJoinListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));

                                switch (modeEvent) {
                                    case Static.LIST_REFRESH:

                                        if (tmpData.size() == 0) {
                                            tvNodata.setText("暂无数据哦！");
                                            linearNodata.setVisibility(View.VISIBLE);
                                            lvJoin.setVisibility(View.GONE);
                                        } else {
                                            linearNodata.setVisibility(View.GONE);
                                            lvJoin.setVisibility(View.VISIBLE);
                                            mList = tmpData;
                                            if (null != mList && mList.size() > 0) {
                                                adapter = new JoinAdapter(JoinActivity.this, JoinActivity.this, mList);
                                                lvJoin.setAdapter(adapter);
                                            }
                                        }

                                        break;

                                    case Static.LIST_MORE:
                                        mList.addAll(mList.size(), tmpData);
                                        adapter.setData(mList);
                                        break;
                                }
                            }


                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                            switch (modeEvent) {
                                case Static.LIST_REFRESH:
                                    page = 1;
                                    tvNodata.setText("暂无数据哦！");
                                    linearNodata.setVisibility(View.VISIBLE);
                                    lvJoin.setVisibility(View.GONE);
                                    break;

                                case Static.LIST_MORE:
                                    page--;
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LogUtil.d("ex===" + ex.getMessage());
                        CustomProgress.hideDialog();
                        lvJoin.onRefreshComplete();


                        switch (modeEvent) {
                            case Static.LIST_REFRESH:
                                page = 1;
                                break;

                            case Static.LIST_MORE:
                                page--;
                                break;
                        }
                        if (ex instanceof HttpException) { // 网络错误
                            if (modeEvent == Static.LIST_REFRESH) {
                                linearNodata.setVisibility(View.VISIBLE);
                                tvNodata.setText("网络异常！");
                                lvJoin.setVisibility(View.GONE);
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
        mTitleBar.setCenterTitle("入班申请");
        mTitleBar.setActivity(this);
    }

    private Push getPush() throws DbException {
        DbManager db = x.getDb(MyApplication.daoConfig);
        return db.selector(Push.class).where("type", "=", KEY_PERMIT).findFirst();
    }

}
