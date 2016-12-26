package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.BulletinListAdapter;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.BulletinModel;
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

import de.greenrobot.event.EventBus;

/**
 * 公告页面
 */
public class BulletinActivity extends BaseActivity {

    private CustomTitleBar custom_titlebar;
    private PullToRefreshListView lv_bulletin;
    private int offset = 1;
    private final int LIMIT = 10;
    private BulletinListAdapter adapter;
    private List<BulletinModel.List> modelList = new ArrayList<>();
    private List<BulletinModel.List> tempList;
    private final int RESET = 3;
    private int refresh_mode = RESET;
    private int maxPage;
    String token = MyApplication.getInstance().getShareUser().getString("token", "");
    private LinearLayout ll_nodata;
    private boolean isFirst;

    @Override
    protected int loadLayout() {
        return R.layout.activity_bulletin;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        lv_bulletin = (PullToRefreshListView) findViewById(R.id.lv_bulletin_list);
        ll_nodata = (LinearLayout) findViewById(R.id.ll_nodata);
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        custom_titlebar.setCenterTitle("公告");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setRightTitle("发起");
        custom_titlebar.displayRightItem(true);
//        lv_bulletin.setEmptyView(ll_nodata);
    }

    @Override
    protected void initListener() {
        custom_titlebar.setActivity(this);
        custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 发送公告界面
                Intent mIntent = new Intent(BulletinActivity.this, BulletinSendActivity.class);
                startActivity(mIntent);
            }
        });

        // 设置刷新监听
        lv_bulletin.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                refresh_mode = Static.LIST_REFRESH;
                offset = 1;
                getBulletinList();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                offset++;
                if (offset <= maxPage) {
                    refresh_mode = Static.LIST_MORE;
                    getBulletinList();
                } else {
                    UiUtils.showToast("没有更多数据");
                    lv_bulletin.onRefreshComplete();
                }
            }
        });

        // 条目点击事件
        lv_bulletin.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(BulletinActivity.this, BulletinDetailActivity.class);
                mIntent.putExtra("announId", modelList.get(position - 1).id);
                startActivity(mIntent);
                // 添加公告阅读
                setRead(modelList.get(position - 1).id);
            }
        });

    }

    /**
     * 添加公告阅读
     */
    private void setRead(final int announId) {

        int readStatus = 1;
//        String url = Static.URL_SERVER + "/teacher/v1/announcement/read?token=" + token + "&status=" + readStatus + "&Announ_id=" + announId;
//        RequestParams params = new RequestParams(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Announ_id", announId + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/announcement/read", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    LogUtil.i(announId + " is read");
                } else {
//                    UiUtils.showToast(response.getData().optString("message"));
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
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
                UiUtils.showToast("已取消");
            }

            @Override
            public void onFinished() {

            }
        });

    }

    private void getBulletinList() {

        if (!isFirst) {
            isFirst = !isFirst;
            CustomProgress.show(this, "获取中...", true, null);
        }

        String classId = MyApplication.getInstance().getClassId();
//        String url = Static.URL_SERVER + "/teacher/v1/announcement/list?token=" + token + "&Class_id=" + classId + "&offset=" + offset + "&limit=" + LIMIT;
//        RequestParams params = new RequestParams(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("offset", offset + "");
        mParams.put("limit", LIMIT + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/announcement/list", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                LogUtil.i(s);
                if (response.isStatus()) {
                    BulletinModel model = GsonUtils.fromJson(s, BulletinModel.class);
                    tempList = model.data.list;
                    int count = model.data.count;
                    maxPage = CommonUtil.getMaxPage(count, LIMIT);

                    // 去除重复数据
                    List<BulletinModel.List> moreList = new ArrayList<>();
                    if (modelList.size() > 0 && tempList.size() > 0) {
                        for (BulletinModel.List t : tempList) {
                            for (BulletinModel.List m : modelList) {
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

                    if (adapter == null) {
                        adapter = new BulletinListAdapter(BulletinActivity.this, modelList, ll_nodata);
                        lv_bulletin.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }

                    if (modelList.size() <= 0) {
                        ll_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ll_nodata.setVisibility(View.GONE);
                    }

                    tempList.clear();

                } else {
                    UiUtils.showToast(response.getData().optString("message"));
                }
                lv_bulletin.onRefreshComplete();
                CustomProgress.hideDialog();
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
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
                lv_bulletin.onRefreshComplete();
                CustomProgress.hideDialog();
            }

            @Override
            public void onCancelled(CancelledException e) {
                UiUtils.showToast("已取消");
                lv_bulletin.onRefreshComplete();
                CustomProgress.hideDialog();
            }

            @Override
            public void onFinished() {
                lv_bulletin.onRefreshComplete();
                CustomProgress.hideDialog();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BulletinActivity");
        MobclickAgent.onResume(this);
        offset = 1;
        getBulletinList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        refresh_mode = RESET;
        MobclickAgent.onPageEnd("BulletinActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_BULLETIN_GO_HOME) {
            finish();
        }
    }

}
