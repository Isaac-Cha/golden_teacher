package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.HomeworkClassesListAdapter;
import com.yl.teacher.model.HomeworkModel;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业列表
 */
public class HomeworkClassesListActivity extends BaseActivity {

    private PullToRefreshListView lv_classes_list;
    private CustomTitleBar custom_titlebar;
    private HomeworkClassesListAdapter adapter;
    private int offset = 1;
    private final int LIMIT = 10;
    private final int RESET = 3;
    private int refresh_mode = RESET;
    private int maxPage;
    private List<HomeworkModel.List> modelList = new ArrayList<>();
    private List<HomeworkModel.List> tempList;
    private Gson mGson;
    private LinearLayout ll_nodata;
    private boolean isFirst;

    @Override
    protected int loadLayout() {
        return R.layout.activity_homeworklist;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        lv_classes_list = (PullToRefreshListView) findViewById(R.id.lv_classes_list);
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
    }

    @Override
    protected void initData() {
        custom_titlebar.setCenterTitle("作业");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setRightTitle("添加");
        custom_titlebar.displayRightItem(true);
//        lv_classes_list.setEmptyView(ll_nodata);
    }

    @Override
    protected void initListener() {
        // 返回按钮
        custom_titlebar.setActivity(this);
        custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeworkClassesListActivity.this, HomeworkAssignActivity.class));
            }
        });

        lv_classes_list.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh_mode = Static.LIST_REFRESH;
                offset = 1;
                getHomeworkList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                offset++;
                if (offset <= maxPage) {
                    refresh_mode = Static.LIST_MORE;
                    getHomeworkList();
                } else {
                    UiUtils.showToast("没有更多数据");
                    lv_classes_list.onRefreshComplete();
                }
            }
        });

        lv_classes_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(HomeworkClassesListActivity.this, HomeworkDetailActivity.class);
                mIntent.putExtra("homeworkId", modelList.get(position - 1).id);
                startActivity(mIntent);
            }
        });

    }

    /**
     * 获取作业列表
     */
    private void getHomeworkList() {
        mGson = new Gson();

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "获取中...", true, null);
        }

        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String classId = MyApplication.getInstance().getClassId();
//        String url = Static.URL_SERVER + "/teacher/v1/homework/list?token=" + token + "&Class_id=" + classId + "&offset=" + offset + "&limit=" + LIMIT;
//        LogUtil.e(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("offset", offset + "");
        mParams.put("limit", LIMIT + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/homework/list", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtil.e("s: " + s);
                CustomProgress.hideDialog();
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    HomeworkModel model = GsonUtils.fromJson(s, HomeworkModel.class);
                    tempList = model.data.list;
                    int count = model.data.count;
                    maxPage = CommonUtil.getMaxPage(count, LIMIT);

                    // 去除重复数据
                    List<HomeworkModel.List> moreList = new ArrayList<>();
                    if (modelList.size() > 0 && tempList.size() > 0) {
                        for (HomeworkModel.List t : tempList) {
                            for (HomeworkModel.List m : modelList) {
                                if (m.id == t.id) {
                                    moreList.add(m);
                                }
                            }
                        }
                    }
                    modelList.removeAll(moreList);
                    if (refresh_mode == Static.LIST_MORE) {
                        modelList.addAll(tempList);
                    } else {
                        modelList.addAll(0, tempList);
                    }

                    if (modelList.size() <= 0) {
                        ll_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ll_nodata.setVisibility(View.GONE);
                    }

                    if (adapter == null) {
                        adapter = new HomeworkClassesListAdapter(HomeworkClassesListActivity.this, modelList, ll_nodata);
                        lv_classes_list.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    tempList.clear();

                } else {
//                    UiUtils.showToast(response.getData().optString("message"));
                }

                lv_classes_list.onRefreshComplete();
                CustomProgress.hideDialog();
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
                lv_classes_list.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException e) {
                CustomProgress.hideDialog();
                UiUtils.showToast("已取消");
                lv_classes_list.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                CustomProgress.hideDialog();
                lv_classes_list.onRefreshComplete();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh_mode = Static.LIST_REFRESH;
        offset = 1;
        getHomeworkList();
        MobclickAgent.onPageStart("HomeworkClassesListActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh_mode = RESET;
        MobclickAgent.onPageEnd("HomeworkClassesListActivity");
        MobclickAgent.onPause(this);
    }

}
