package com.yl.teacher.view;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.MyClass;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

/**
 * 创建班级成功页面
 */
public class CreateSuccessActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private TextView tvClasses;

    private TextView tvNo;

    private TextView tvTeacher;

    private TextView tvLogin;

    private MyClass myClass;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("CreateSuccessActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("CreateSuccessActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_create_success;
    }

    @Override
    protected void initViews() {
        myClass = (MyClass) getIntent().getSerializableExtra("myClass");
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        tvClasses = (TextView)findViewById(R.id.tvClasses);
        tvNo = (TextView)findViewById(R.id.tvNo);
        tvTeacher = (TextView)findViewById(R.id.tvTeacher);
        tvLogin = (TextView)findViewById(R.id.tvLogin);

    }

    @Override
    protected void initData() {
        tvClasses.setText(myClass.getName());
        tvNo.setText(myClass.getClassCode());
        tvTeacher.setText(myClass.getHeadMaster());
    }

    @Override
    protected void initListener() {
        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().setClassId(""+myClass.getId());
                MyApplication.getInstance().setClassesName(myClass.getName());
                MyApplication.getInstance().setClassedNo(myClass.getClassCode());
                Intent intent = new Intent(CreateSuccessActivity.this, DetaileActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initBar(){

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("创建成功");
        mTitleBar.displayLeftItem(false);
    }

}
