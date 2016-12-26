package com.yl.teacher.view;

import android.widget.TextView;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

/**
 * 邀请号
 * Created by yiban on 2016/5/20.
 */
public class InviteActivity extends BaseActivity{

    private CustomTitleBar mTitleBar;

    private TextView tvNo;

    @Override
    protected int loadLayout() {
        return R.layout.activity_invite;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        tvNo = (TextView) findViewById(R.id.tvNo);
    }

    @Override
    protected void initData() {
        tvNo.setText(MyApplication.getInstance().getClassedNo());
    }

    @Override
    protected void initListener() {

    }

    private void initBar(){

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("邀请家长");
        mTitleBar.setActivity(this);
    }
}
