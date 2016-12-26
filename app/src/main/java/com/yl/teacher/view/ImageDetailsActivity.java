package com.yl.teacher.view;

import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.adapter.ClassCircleImageAdapter;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;

import java.util.ArrayList;
import java.util.List;

import static com.yl.teacher.global.MyApplication.avatarIsChanged;

/**
 * Created by $USER_NAME on 2016/10/13.
 * 图片查看界面
 */
public class ImageDetailsActivity extends BaseActivity {

    public static final String INTENT_IMAGE_URLS = "imageUrls";
    public static final String INTENT_IMAGE_POSITION = "position";
    public static final String INTENT_IMAGE_FROM = "chat_activity";

    private ViewPager viewPager;
    private LinearLayout guide_group;
    private List<View> guideViewList;
    private ArrayList<String> mImageUrls;
    private int mStartPosition;
    private ClassCircleImageAdapter mAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ImageDetailsActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ImageDetailsActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_class_circle_image;
    }

    @Override
    protected void initViews() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        guide_group = (LinearLayout) findViewById(R.id.guide_group);
    }

    @Override
    protected void initData() {
        guideViewList = new ArrayList<>();
        mImageUrls = getIntent().getStringArrayListExtra(INTENT_IMAGE_URLS);
        mStartPosition = getIntent().getIntExtra(INTENT_IMAGE_POSITION, 0);
        String intentFrom = getIntent().getStringExtra(ImageDetailsActivity.INTENT_IMAGE_FROM);
        if (!StringUtils.isEmpty(intentFrom)) {
            avatarIsChanged = true;
        }

        // 关闭预加载，默认一次只加载一个Fragment
        viewPager.setOffscreenPageLimit(1);
        mAdapter = new ClassCircleImageAdapter(this, mImageUrls);
        viewPager.setAdapter(mAdapter);
        viewPager.setCurrentItem(mStartPosition);
        addGuideView(guide_group, mStartPosition, mImageUrls);
    }

    @Override
    protected void initListener() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < guideViewList.size(); i++) {
                    guideViewList.get(i).setSelected(i == position ? true : false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 添加底部小圆点
     */
    private void addGuideView(LinearLayout guideGroup, int startPosition, ArrayList<String> imgUrls) {

        if (imgUrls != null && imgUrls.size() > 0) {
            guideViewList.clear();
            for (int i = 0; i < imgUrls.size(); i++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.selector_guide_bg);
                view.setSelected(i == startPosition ? true : false);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.gudieview_width),
                        getResources().getDimensionPixelSize(R.dimen.gudieview_heigh));
                layoutParams.setMargins(UiUtils.dip2px(6), 0, 0, UiUtils.dip2px(10));
                guideGroup.addView(view, layoutParams);
                guideViewList.add(view);
            }
        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            return super.dispatchTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_in_scale, R.anim.activity_out_scale);
    }
}
