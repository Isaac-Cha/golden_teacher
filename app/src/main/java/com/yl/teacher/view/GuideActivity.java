package com.yl.teacher.view;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.db.Guide;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.User;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.zhy.autolayout.AutoLinearLayout;

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
 * 引导页
 * Created by yiban on 2016/6/16.
 */
public class GuideActivity extends BaseActivity {

    private DbManager dbManager;

    private ViewPager viewPager;

    private View viewOne;

    private View viewTwo;

    private View viewTrd;

    private AutoLinearLayout linearPassOne;

    private AutoLinearLayout linearPassTwo;

    private AutoLinearLayout linearPassTrd;

    private LayoutInflater inflater;

    private List<View> mList;

    private GuideAdapter adapter;
    private double flaggingWidth;
    private GestureDetector gestureDetector;
    private int currentItem;
    private AutoLinearLayout ll_dot;
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("GuideActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("GuideActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initViews() {
        inflater = LayoutInflater.from(this);
        dbManager = x.getDb(MyApplication.getInstance().daoConfig);
        ll_dot = (AutoLinearLayout) findViewById(R.id.ll_dot);
    }

    @Override
    protected void initData() {
        gestureDetector = new GestureDetector(new GuideViewTouch());
        // 获取分辨率
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        flaggingWidth = dm.widthPixels / 8;

        Guide guide = new Guide();
        guide.setVersion(MyApplication.getInstance().VERSION_CODE);
        guide.setIntial(1);
        try {
            dbManager.saveBindingId(guide);
        } catch (DbException e) {
            e.printStackTrace();
        }
        initViewPager();
        initPoint();
    }

    @Override
    protected void initListener() {

        // ViewPager滑动监听
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                setCurDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initViewPager() {
        mList = new ArrayList<>();
        viewPager = (ViewPager) findViewById(R.id.viewMain);

        viewOne = inflater.inflate(R.layout.guide_one, null);
        viewTwo = inflater.inflate(R.layout.guide_two, null);
        viewTrd = inflater.inflate(R.layout.guide_trd, null);

        viewOne.findViewById(R.id.iv_gohome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterToHome();
            }
        });
        viewTwo.findViewById(R.id.iv_gohome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterToHome();
            }
        });
        viewTrd.findViewById(R.id.iv_gohome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterToHome();
            }
        });

        mList.add(viewOne);
        mList.add(viewTwo);
        mList.add(viewTrd);

        adapter = new GuideAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0, true);

    }

    private class GuideAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mList.get(position));
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mList.get(position), 0);
            return mList.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }


    }

    private void enterToHome() {
        if (!"".equals(MyApplication.getInstance().getShareUser().getString("token", ""))) {
            doLogin();
        } else {
            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 自动登录
     */
    private void doLogin() {
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/autoLogin");
//        params.addQueryStringParameter("token", MyApplication.getInstance().getShareUser().getString("token", ""));

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/autoLogin", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            if (response.getData().optJSONObject("data") != null && response.getData().optJSONObject("data").toString().length() > 0) {

                                User user = User.getUserFromJsonObj(response.getData().optJSONObject("data"));
                                User.setCurrentUser(user);
                                MobclickAgent.onProfileSignIn(user.getNickName());

                                if ("".equals(user.getInviteCode())) {
                                    Intent intent = new Intent(GuideActivity.this, CodeActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    if ("".equals(user.getSchoolId())) {
                                        Intent intent = new Intent(GuideActivity.this, InfoActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(GuideActivity.this, ClassesActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

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
                        Intent intent = new Intent(GuideActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector.onTouchEvent(ev)) {
            ev.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(ev);
    }

    private class GuideViewTouch extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            if (currentItem == mList.size() - 1) {
                if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())
                        && (e1.getX() - e2.getX() <= (-flaggingWidth) || e1.getX() - e2.getX() >= flaggingWidth)) {
                    if (e1.getX() - e2.getX() >= flaggingWidth) {
                        enterToHome();
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {

        points = new ImageView[mList.size()];

        // 循环取得小点图片
        for (int i = 0; i < mList.size(); i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) ll_dot.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }
    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > mList.size() - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }
}
