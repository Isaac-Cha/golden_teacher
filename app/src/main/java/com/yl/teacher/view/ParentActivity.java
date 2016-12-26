package com.yl.teacher.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.SortGroupMemberAdapter;
import com.yl.teacher.model.Contact;
import com.yl.teacher.model.GroupMemberBean;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.widget.CharacterParser;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.PinyinComparator;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 家长列表页面
 * Created by yiban on 2016/5/5.
 */
public class ParentActivity extends BaseActivity implements SectionIndexer {

    private CustomTitleBar mTitleBar;

    private LinearLayout linearNodata;

    private TextView tvNodata;

    private SortGroupMemberAdapter adapter;

    /**
     * 上次第一个可见元素，用于滚动时记录标识。
     */
    private int lastFirstVisibleItem = -1;
    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private List<GroupMemberBean> SourceDateList;

    private LinearLayout titleLayout;
    private TextView titleMsg;

    /**
     * 根据拼音来排列ListView里面的数据类
     */
    private PinyinComparator pinyinComparator;

    private ListView mListView;

    private String[] mData;

    private List<Contact> mList;

    @Override
    protected int loadLayout() {
        return R.layout.activity_parent;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        linearNodata = (LinearLayout) findViewById(R.id.linearNodata);
        tvNodata = (TextView) findViewById(R.id.tvNodata);
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        titleMsg = (TextView) findViewById(R.id.title_layout_catalog);
        mListView = (ListView) findViewById(R.id.listView);
    }

    @Override
    protected void initData() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        characterParser = CharacterParser.getInstance();
        pinyinComparator = new PinyinComparator();
        getContact();
    }

    @Override
    protected void initListener() {

    }

    private void initBar() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("家长列表");
        mTitleBar.setActivity(this);
    }

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<GroupMemberBean> filledData(String[] date) {
        List<GroupMemberBean> mSortList = new ArrayList<GroupMemberBean>();

        for (int i = 0; i < date.length; i++) {
            GroupMemberBean sortModel = new GroupMemberBean();
            sortModel.setUserId(mList.get(i).getUserId());
            sortModel.id = mList.get(i).getId();
            sortModel.setPhone(mList.get(i).getPhone());
            sortModel.setName(date[i]);
            // 汉字转换成拼音
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();

            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                sortModel.setSortLetters(sortString.toUpperCase());
            } else {
                sortModel.setSortLetters("#");
            }

            mSortList.add(sortModel);
        }
        return mSortList;

    }

    @Override
    public Object[] getSections() {
        return null;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的Char ascii值
     */
    public int getSectionForPosition(int position) {
        return SourceDateList.get(position).getSortLetters().charAt(0);
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < SourceDateList.size(); i++) {
            String sortStr = SourceDateList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 获取通讯录数据
     */
    private void getContact() {
        CustomProgress.show(this, "加载中...", true, null);
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classMember/linkman?token=" + MyApplication.getInstance().getShareUser().getString("token", "") + "&type=1&Class_id=" + MyApplication.getInstance().getClassId());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("type", "1");
        mParams.put("Class_id", MyApplication.getInstance().getClassId());

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMember/linkman", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            mList = Contact.getContactListFromJsonObj(response.getData().optJSONArray("data"));
                            if (mList.size() == 0) {
                                tvNodata.setText("暂无数据哦！");
                                linearNodata.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                                titleLayout.setVisibility(View.GONE);
                            } else {
                                linearNodata.setVisibility(View.GONE);
                                titleLayout.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.VISIBLE);
                                mData = new String[mList.size()];
                                for (int i = 0; i < mList.size(); i++) {

                                    mData[i] = mList.get(i).getTitle();

                                }
                                SourceDateList = filledData(mData);

                                // 根据a-z进行排序源数据
                                Collections.sort(SourceDateList, pinyinComparator);
                                adapter = new SortGroupMemberAdapter(ParentActivity.this, SourceDateList, mList, linearNodata, titleLayout, mListView);
                                mListView.setAdapter(adapter);
                                mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem,
                                                         int visibleItemCount, int totalItemCount) {
                                        if (SourceDateList.size() > 1) {
                                            int section = getSectionForPosition(firstVisibleItem);
                                            int nextSection = getSectionForPosition(firstVisibleItem + 1);
                                            int nextSecPosition = getPositionForSection(+nextSection);
                                            if (firstVisibleItem != lastFirstVisibleItem) {
                                                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                                        .getLayoutParams();
                                                params.topMargin = CommonUtil.dip2px(ParentActivity.this, 50);
                                                titleLayout.setLayoutParams(params);
                                                titleMsg.setText(SourceDateList.get(
                                                        getPositionForSection(section)).getSortLetters());
                                            }
                                            if (nextSecPosition == firstVisibleItem + 1) {
                                                View childView = view.getChildAt(0);
                                                if (childView != null) {
                                                    int titleHeight = titleLayout.getHeight();
                                                    int bottom = childView.getBottom();
                                                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) titleLayout
                                                            .getLayoutParams();
                                                    if (bottom < titleHeight) {
                                                        float pushedDistance = bottom - titleHeight - CommonUtil.dip2px(ParentActivity.this, 50);
                                                        params.topMargin = (int) pushedDistance;
                                                        titleLayout.setLayoutParams(params);
                                                    } else {
                                                        if (params.topMargin != 0) {
                                                            params.topMargin = CommonUtil.dip2px(ParentActivity.this, 50);
                                                            titleLayout.setLayoutParams(params);
                                                        }
                                                    }
                                                }
                                            }
                                            lastFirstVisibleItem = firstVisibleItem;
                                        }

                                    }
                                });
                            }


                        } else {
                            tvNodata.setText("暂无数据哦！");
                            linearNodata.setVisibility(View.VISIBLE);
                            mListView.setVisibility(View.GONE);
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();

                        if (ex instanceof HttpException) { // 网络错误
                            linearNodata.setVisibility(View.VISIBLE);
                            tvNodata.setText("网络异常！");
                            mListView.setVisibility(View.GONE);
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

}
