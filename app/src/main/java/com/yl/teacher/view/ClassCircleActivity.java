package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.adapter.ClassCircleAdapter;
import com.yl.teacher.db.Push;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.ClassCircle;
import com.yl.teacher.model.Response;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 班级圈
 */
public class ClassCircleActivity extends BaseActivity {

    private static final int KEY_CLASSZONE = 7; // 班级空间

    private CustomTitleBar custom_titlebar;
    private int mPage = 1;
    private final int LIMIT = 10;
    private String token;
    private String classId;
    private PullToRefreshListView lv_classcircle;
    private int refresh_mode;
    private List<ClassCircle.DataBean.ListBean> mTempDatas;
    private List<ClassCircle.DataBean.ListBean> mDatas;
    private int maxPage;
    private LinearLayout ll_nodata;
    private ClassCircleAdapter mAdapter;
    private boolean isFirst;

    @Override
    protected int loadLayout() {
        return R.layout.activity_classcircle;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        lv_classcircle = (PullToRefreshListView) findViewById(R.id.lv_classcircle);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
    }

    @Override
    protected void initData() {
        token = MyApplication.getInstance().getShareUser().getString("token", "");
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }

        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setCenterTitle("班级空间");
        custom_titlebar.setRightTitle("添加");
        custom_titlebar.displayRightItem(true);
        classId = MyApplication.getInstance().getClassId();

        mDatas = new ArrayList<>();

    }

    @Override
    protected void initListener() {

        // 返回按钮
        custom_titlebar.setActivity(this);
        custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ClassCircleActivity.this, ClassCircleAddActivity.class));
            }
        });

        lv_classcircle.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh_mode = Static.LIST_REFRESH;
                mPage = 1;
                getClassCircleData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage++;
                if (mPage <= maxPage) {
                    refresh_mode = Static.LIST_MORE;
                    getClassCircleData();
                } else {
                    UiUtils.showToast("没有更多数据");
                    lv_classcircle.onRefreshComplete();
                }
            }
        });

    }

    @Override
    protected void onResume() {

        try {
            Push push = getPush();
            if (push != null) {
                if (!classId.equals(push.classId + "")) {
                    classId = push.classId + "";
                }
            }
        } catch (DbException e) {

        }

        getClassCircleData();

        super.onResume();
        MobclickAgent.onPageStart("ClassCircleActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ClassCircleActivity");
        MobclickAgent.onPause(this);
    }

    private Push getPush() throws DbException {
        DbManager db = x.getDb(MyApplication.daoConfig);
        return db.selector(Push.class).where("type", "=", KEY_CLASSZONE).findFirst();
    }

    /**
     * 获取班级圈数据
     */
    private void getClassCircleData() {

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "获取中...", true, null);
        }

        // $host/teacher/v1/classZone/list?token=**&Class_id=**&offset=**&limit=**&requestType (any)
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("requestType", "json");
        mParams.put("offset", mPage + "");
        mParams.put("limit", LIMIT + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classZone/list", mParams),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("getClassCircleData: " + result);

                        CustomProgress.hideDialog();
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {

                            ClassCircle classCircle = GsonUtils.fromJson(result, ClassCircle.class);
                            mTempDatas = classCircle.data.list;

                            int count = classCircle.data.count;
                            maxPage = CommonUtil.getMaxPage(count, LIMIT);

                            // 去除重复数据
                            List<ClassCircle.DataBean.ListBean> moreDatas = new ArrayList<>();
                            if (mDatas.size() > 0 && mTempDatas.size() > 0) {
                                for (ClassCircle.DataBean.ListBean t : mTempDatas) {
                                    for (ClassCircle.DataBean.ListBean m : mDatas) {
                                        if (m.id == t.id) {
                                            moreDatas.add(m);
                                        }
                                    }
                                }
                            }
                            mDatas.removeAll(moreDatas);

                            if (refresh_mode == Static.LIST_MORE) {
                                mDatas.addAll(mTempDatas);
                            } else {
                                mDatas.addAll(0, mTempDatas);
                            }

                            // 设置数据
                            if (mAdapter == null) {
                                mAdapter = new ClassCircleAdapter(ClassCircleActivity.this, mDatas, ll_nodata);
                                lv_classcircle.setAdapter(mAdapter);
                            } else {
                                mAdapter.notifyDataSetChanged();
                            }

                            if (mDatas.size() <= 0) {
                                ll_nodata.setVisibility(View.VISIBLE);
                            } else {
                                ll_nodata.setVisibility(View.GONE);
                            }

                            mTempDatas.clear();

                        } else {
                            UiUtils.showToast(response.getData().optString("message"));
                        }

                        lv_classcircle.onRefreshComplete();
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        CustomProgress.hideDialog();
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
                        lv_classcircle.onRefreshComplete();
                        CustomProgress.hideDialog();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                        CustomProgress.hideDialog();
                        lv_classcircle.onRefreshComplete();
                    }

                    @Override
                    public void onFinished() {
                        CustomProgress.hideDialog();
                        lv_classcircle.onRefreshComplete();
                    }
                });

    }

}
