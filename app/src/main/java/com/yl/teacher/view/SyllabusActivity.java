package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.adapter.SyllabusAdapter;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.SyllabusModel;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
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
 * 课程表界面
 */
public class SyllabusActivity extends BaseActivity {

    private CustomTitleBar custom_titlebar;
    private PullToRefreshListView lv_syllabus;
    // private ImageView iv_add;
    private SyllabusAdapter syllabusAdapter;
    private int offset = 1;
    private final int LIMIT = 10;
    private List<SyllabusModel.List> syllabusModelList = new ArrayList<>();
    private List<SyllabusModel.List> tempList;
    private final int RESET = 3;
    private int refresh_mode = RESET;
    private int maxPage;
    private String token = MyApplication.getInstance().getShareUser().getString("token", "");
    private LinearLayout ll_nodata;
    private boolean isFirst;

    @Override
    protected int loadLayout() {
        return R.layout.activity_syllabus;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        lv_syllabus = (PullToRefreshListView) findViewById(R.id.lv_syllabus_list);
        // iv_add = (ImageView) findViewById(R.id.iv_syllabus_add);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
    }

    @Override
    protected void initData() {
        if (StringUtils.isEmpty(token)) {
            startActivity(new Intent(this, LoginActivity.class));
            UiUtils.showToast("请登录");
            finish();
        }
        custom_titlebar.setCenterTitle("课程表");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.displayRightItem(true);
        custom_titlebar.setRightTitle("添加");
    }

    @Override
    protected void initListener() {
        // 返回键
        custom_titlebar.setActivity(this);
        // iv_add.setOnClickListener(this);

        custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SyllabusActivity.this, SyllabusAddActivity.class));
            }
        });

        // 设置上拉加载，下拉刷新监听
        lv_syllabus.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh_mode = Static.LIST_REFRESH;
                offset = 1;
                getSyllabusList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                offset++;
                if (offset <= maxPage) {
                    refresh_mode = Static.LIST_MORE;
                    getSyllabusList();
                } else {
                    UiUtils.showToast("没有更多数据");
                    lv_syllabus.onRefreshComplete();
                }
            }
        });

        lv_syllabus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(SyllabusActivity.this, ImageDetailsActivity.class);

                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(syllabusModelList.get(position - 1).picUrl);

                mIntent.putExtra(ImageDetailsActivity.INTENT_IMAGE_URLS, imageUrls);
                // 标识为已读
                setRead(syllabusModelList.get(position - 1).id);
                startActivity(mIntent);
                overridePendingTransition(R.anim.activity_in_scale, R.anim.activity_out_scale);
            }
        });

    }

    /**
     * 标记为已读
     */
    private void setRead(int id) {

//        String url = Static.URL_SERVER + "/teacher/v1/classSchedule/read?token=" + token + "&Schedule_id=" + id;
//        RequestParams params = new RequestParams(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Schedule_id", id + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classSchedule/read", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    // 发送成功
                    LogUtil.i("read status send successed");
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    /**
     * 获取课程表
     */
    private void getSyllabusList() {

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "获取中...", true, null);
        }
        String classId = MyApplication.getInstance().getClassId();
//        String url = Static.URL_SERVER + "/teacher/v1/classSchedule/schedule-list?token=" + token + "&offset=" + offset + "&limit=" + LIMIT + "&Class_id=" + classId;
//        RequestParams params = new RequestParams(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId + "");
        mParams.put("offset", offset + "");
        mParams.put("limit", LIMIT + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classSchedule/schedule-list", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                CustomProgress.hideDialog();
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    LogUtil.i(s);
                    SyllabusModel syllabusModel = GsonUtils.fromJson(s, SyllabusModel.class);
                    tempList = syllabusModel.data.list;

                    int count = syllabusModel.data.count;
                    maxPage = CommonUtil.getMaxPage(count, LIMIT);

                    // 去除重复数据
                    List<SyllabusModel.List> moreList = new ArrayList<>();
                    if (syllabusModelList.size() > 0 && tempList.size() > 0) {
                        for (SyllabusModel.List t : tempList) {
                            for (SyllabusModel.List m : syllabusModelList) {
                                if (m.id == t.id) {
                                    moreList.add(m);
                                }
                            }
                        }
                    }
                    syllabusModelList.removeAll(moreList);
                    if (refresh_mode == Static.LIST_MORE) {
                        syllabusModelList.addAll(tempList);
                    } else {
                        syllabusModelList.addAll(0, tempList);
                    }

                    if (syllabusAdapter == null) {
                        syllabusAdapter = new SyllabusAdapter(SyllabusActivity.this, syllabusModelList, ll_nodata);
                        lv_syllabus.setAdapter(syllabusAdapter);
                    } else {
                        syllabusAdapter.notifyDataSetChanged();
                    }

                    tempList.clear();

                    if (syllabusModelList.size() <= 0) {
                        ll_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ll_nodata.setVisibility(View.GONE);
                    }

                } else {
                    UiUtils.showToast(response.getData().optString("message"));
                }
                lv_syllabus.onRefreshComplete();
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
                lv_syllabus.onRefreshComplete();
                CustomProgress.hideDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                CustomProgress.hideDialog();
                lv_syllabus.onRefreshComplete();
            }

            @Override
            public void onFinished() {
                CustomProgress.hideDialog();
                lv_syllabus.onRefreshComplete();
            }
        });

    }

    /*@Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_syllabus_add:
                startActivity(new Intent(this, SyllabusAddActivity.class));
                break;

            default:
                break;
        }

    }*/

    @Override
    protected void onResume() {
        super.onResume();
        refresh_mode = Static.LIST_REFRESH;
        offset = 1;
        getSyllabusList();
        MobclickAgent.onPageStart("SyllabusActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh_mode = RESET;
        MobclickAgent.onPageEnd("SyllabusActivity");
        MobclickAgent.onPause(this);
    }

}
