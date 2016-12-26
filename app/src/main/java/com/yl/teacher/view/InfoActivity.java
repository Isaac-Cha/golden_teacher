package com.yl.teacher.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.School;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 完善个人信息页面
 */
public class InfoActivity extends BaseActivity {

    private CustomTitleBar mTitleBar;

    private AlertDialog mUploadDialog;

    private View dialogView;

    private PopupWindow popupWindow;

    private View popView;

    private ListView lvSchool;

    private int sex = 1;

    private String[] dataSchool;

    private AutoRelativeLayout relaSex;

    private TextView tvSex;

    private AutoRelativeLayout relaSchool;

    private TextView tvSchool;

    private EditText etName;

    private int schoolId = -1;

    private List<School> list;
    private String realName;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("InfoActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("InfoActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_fullmsg;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        relaSex = (AutoRelativeLayout) findViewById(R.id.relaSex);
        tvSex = (TextView) findViewById(R.id.tvSex);
        relaSchool = (AutoRelativeLayout) findViewById(R.id.relaSchool);
        tvSchool = (TextView) findViewById(R.id.tvSchool);
        etName = (EditText) findViewById(R.id.etName);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initListener() {
        relaSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindowSex();
            }
        });
        relaSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSchoolData();
            }
        });
    }

    /**
     * 获取学校数据
     */
    private void getSchoolData() {
        CustomProgress.show(this, "加载中...", true, null);

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/school/list");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/school/list", new HashMap<String, String>()),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        Response response = CommonUtil.checkResponse(result);

                        if (response.isStatus()) {
                            list = School.getSchoolListFromJsonObj(response.getData().optJSONObject("data").optJSONArray("list"));
                            if (list.size() > 0) {
                                dataSchool = new String[list.size()];
                                for (int i = 0; i < list.size(); i++) {

                                    dataSchool[i] = list.get(i).getName();

                                }
                            }
                            showDialog();
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();
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

    private void showDialog() {
        UiUtils.closeKeyboard(this);

        if (null == dialogView) {
            dialogView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_school, null);
            lvSchool = (ListView) dialogView.findViewById(R.id.lvSchool);
            lvSchool.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, dataSchool));
            lvSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    tvSchool.setText(dataSchool[position]);
                    schoolId = list.get(position).getId();
                    mUploadDialog.dismiss();
                }
            });
            mUploadDialog = new AlertDialog.Builder(this)
                    .setView(dialogView)
                    .create();
        }
        mUploadDialog.show();
    }

    private void updateMsg() {

        if (schoolId == -1) {
            UiUtils.showToast("请选择学校");
            return;
        }

        String realName =  etName.getText().toString().trim();
        if (CommonUtil.getStrLength(realName) < 2) {
            UiUtils.showToast("姓名不能少于2个字");
            return;
        }

        CustomProgress.show(this, "提交中...", true, null);
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/update");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("School_id", "" + schoolId);
        mParams.put("sex", "" + sex);
        mParams.put("realName", realName);
        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/update", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            Intent intent = new Intent(InfoActivity.this, ClassesActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();
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

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("完善信息");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("确认");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMsg();
            }
        });
    }

    private void showPopwindowSex() {
        UiUtils.closeKeyboard(this);
        if (popupWindow == null) {
            popView = LayoutInflater.from(this).inflate(R.layout.popup_sex, null);
            TextView tvMale = (TextView) popView.findViewById(R.id.tv_male);
            TextView tvFemale = (TextView) popView.findViewById(R.id.tv_female);
            TextView tvCancel = (TextView) popView.findViewById(R.id.tvCancelSex);


            popupWindow = new PopupWindow(popView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);

            // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
            popupWindow.setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
            ColorDrawable dw = new ColorDrawable(0x7d000000);
            popupWindow.setBackgroundDrawable(dw);
            // 设置popWindow的显示和消失动画
            popupWindow.setAnimationStyle(R.style.bottom_dialog);

            tvMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sex = 1;
                    popupWindow.dismiss();
                    tvSex.setText("男");
                }
            });
            tvFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sex = 2;
                    popupWindow.dismiss();
                    tvSex.setText("女");
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
        // 在底部显示
        popupWindow.showAtLocation(this.findViewById(R.id.widget_custom_titlebar),
                Gravity.BOTTOM, 0, 0);

    }

}
