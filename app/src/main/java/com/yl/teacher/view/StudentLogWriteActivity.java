package com.yl.teacher.view;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yl.teacher.R;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.Student;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GlideCircleTransform;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by $USER_NAME on 2016/10/19.
 */
public class StudentLogWriteActivity extends BaseActivity {

    public static final String INTENT_STUDENT = "intent_student";
    private Student.DataBean mStudent;
    private CustomTitleBar mTitleBar;
    private ImageView iv_student_avatar;
    private TextView tv_student_name;
    private EditText et_student_content;
    private String stuentLog;


    @Override
    protected int loadLayout() {
        return R.layout.activity_student_log_write;
    }

    @Override
    protected void initViews() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        iv_student_avatar = (ImageView) findViewById(R.id.iv_student_avatar);
        tv_student_name = (TextView) findViewById(R.id.tv_student_name);
        et_student_content = (EditText) findViewById(R.id.et_student_content);

    }

    @Override
    protected void initData() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        mTitleBar.setCenterTitle("写点滴");
        mTitleBar.displayBackBtn(true);
        mTitleBar.setActivity(this);
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightTitle("发送");

        mStudent = (Student.DataBean) getIntent().getSerializableExtra(INTENT_STUDENT);
        if (mStudent == null) {
            UiUtils.showToast("获取学生信息错误");
            return;
        }

        Glide.with(this).load(mStudent.studentAvatar).transform(new GlideCircleTransform(this)).into(iv_student_avatar);
        tv_student_name.setText(mStudent.studentName);

    }

    @Override
    protected void initListener() {

        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDialog();
            }
        });

    }

    private void showConfirmDialog() {

        stuentLog = et_student_content.getText().toString().trim();
        if (StringUtils.isEmpty(stuentLog)) {
            UiUtils.showToast("点滴内容不能为空~");
            return;
        }

        if (CommonUtil.getStrLength(stuentLog) > 100) {
            UiUtils.showToast("点滴内容超过限制");
            return;
        }

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE, false)
                .setTitleText("确定发送该点滴？")
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sendStudentLog();
                    }
                })
                .show();

    }

    private void sendStudentLog() {

        CustomProgress.show(this, "发送中...", true, null);

        // $host/teacher/v2/studentLog/add-log
        String mToken = MyApplication.getInstance().getShareUser().getString("token", "");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", mToken);
        mParams.put("to_User_id", mStudent.studentId + "");
        mParams.put("SchoolClass_id", mStudent.schoolId + "");
        mParams.put("content", stuentLog);

        x.http().post(HttpUtils.getRequestParams("/teacher/v2/studentLog/add-log", mParams),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        CustomProgress.hideDialog();

                        Response response = CommonUtil.checkResponse(s);
                        if (response.isStatus()) {

                            UiUtils.showToast("发送成功");
                            finish();

                        } else {
                            UiUtils.showToast(response.getData().optString("message"));
                        }

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
                        CustomProgress.hideDialog();
                    }

                    @Override
                    public void onCancelled(CancelledException e) {
                        CustomProgress.hideDialog();
                    }

                    @Override
                    public void onFinished() {
                        CustomProgress.hideDialog();
                    }
                });

    }

}
