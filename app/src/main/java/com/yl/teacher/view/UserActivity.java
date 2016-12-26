package com.yl.teacher.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.User;
import com.yl.teacher.util.AppUtils;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.yl.teacher.global.MyApplication.avatarIsChanged;
import static com.yl.teacher.util.CommonUtil.hasSdcard;

/**
 * 个人中心页面
 */
public class UserActivity extends BaseActivity {

    private LayoutInflater inflater;

    private final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private final static int REQUEST_CODE_CAMERA = 1000;
    private final static int REQUEST_CODE_GALLERY = 1001;
    private final static int REQUEST_CODE_CROP = 1002;
    private File tempFile;

    private CustomTitleBar mTitleBar;

    private AutoRelativeLayout relaSex;

    //private AutoRelativeLayout relaAge;

    private AutoRelativeLayout relaPhone;

    private AlertDialog mValidDialog;

    private View validView;

    private TextView et_OldPhone;

    private Button btnValid;

    private EditText etValid;

    private AlertDialog mUpdateDialog;

    private View updateView;

    private EditText etValidNew;

    private Button btnValidNew;

    private EditText etPhoneNew;

    private SimpleDraweeView imgIcon;

    private User user;

    private String school;

    private TextView tvName;

    private TextView tvSex;

    //private TextView tvAge;

    //private String age;

    private TextView tvPhone;

    private PopupWindow popupPhoto;

    private View popPhoto;

    private PopupWindow popupSex;

    private View popSex;

    private int sex = 1;

    private AutoLinearLayout linearQuit;

    private int second = 60;

    private boolean flagValid;

    private int secondUpdate = 60;

    private boolean flagValidUpdate;

    // 是否更新用户信息
    private boolean flagUpdateUserInfo = true;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    second--;
                    if (second > 0) {

                        btnValid.setText("" + second + "s");
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    if (second == 0) {
                        flagValid = false;
                        second = 60;
                        btnValid.setBackgroundResource(R.drawable.valid_bg);
                        btnValid.setText("再次获取");
                    }


                    break;
                case 1:

                    secondUpdate--;
                    if (secondUpdate > 0) {

                        btnValidNew.setText("" + secondUpdate + "s");
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                    if (secondUpdate == 0) {
                        flagValidUpdate = false;
                        secondUpdate = 60;
                        btnValidNew.setBackgroundResource(R.drawable.valid_bg);
                        btnValidNew.setText("再次获取");
                    }


                    break;

                default:
                    break;

            }
        }
    };
    private AutoRelativeLayout relaName;
    private PopupWindow mPopupName;
    private TextView tv_change;
    private Bitmap bitmap;
    private TextView tv_version;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("UserActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("UserActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_user;
    }

    @Override
    protected void initViews() {
        school = getIntent().getStringExtra("school");
        initBar();
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        inflater = LayoutInflater.from(this);
        relaPhone = (AutoRelativeLayout) findViewById(R.id.relaPhone);
        linearQuit = (AutoLinearLayout) findViewById(R.id.linearQuit);

        relaSex = (AutoRelativeLayout) findViewById(R.id.relaSex);
        //relaAge = (AutoRelativeLayout)findViewById(R.id.relaAge);

        imgIcon = (SimpleDraweeView) findViewById(R.id.imgIcon);

        tvName = (TextView) findViewById(R.id.tvName);

        tvSex = (TextView) findViewById(R.id.tvSex);

        relaName = (AutoRelativeLayout) findViewById(R.id.relaName);

        //tvAge = (TextView) findViewById(R.id.tvAge);

        tvPhone = (TextView) findViewById(R.id.tvPhone);

        tv_change = (TextView) findViewById(R.id.tv_change);

        tv_version = (TextView) findViewById(R.id.tv_version);

    }

    @Override
    protected void initData() {
        getUserInfo();

        tv_version.setText(StringUtils.getVersionNameFull());
    }

    @Override
    protected void initListener() {
        imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        tv_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });

        linearQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(UserActivity.this, SweetAlertDialog.WARNING_TYPE, true)
                        .setTitleText("是否退出登录？")
                        .setCancelText("取消")
                        .setConfirmText("确定")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();

                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                flagUpdateUserInfo = false;
                                sDialog.dismiss();
                                AppUtils.loginOut(UserActivity.this, new Intent(UserActivity.this, LoginActivity.class));
                            }
                        })
                        .show();
            }
        });
        relaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showValidDialog();
            }
        });
        relaSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindowSex();
            }
        });
        relaName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopwindowName();
            }
        });

        /*relaAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendarDialog();
            }
        });*/
    }

    private void showPopwindowName() {

        View view = View.inflate(this, R.layout.popup_input_name, null);
        final EditText etInputName = (EditText) view.findViewById(R.id.et_pop_name);
        TextView tvNameConfirm = (TextView) view.findViewById(R.id.tv_name_confirm);
        TextView tvNameCancel = (TextView) view.findViewById(R.id.tv_name_cancel);

        if (mPopupName == null) {
            mPopupName = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
        }
        mPopupName.setBackgroundDrawable(new BitmapDrawable());
        mPopupName.setOutsideTouchable(true);

        tvNameCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopupName.dismiss();
            }
        });

        tvNameConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputName = etInputName.getText().toString().trim();
                if (!StringUtils.isEmpty(inputName)) {
                    tvName.setText(inputName);
                    mPopupName.dismiss();
                } else {
                    UiUtils.showToast("请输入姓名");
                }
            }
        });

        // 设置背景色变暗
        UiUtils.darkenScreen(this, mPopupName);

        // 屏幕中间显示
        mPopupName.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void initUi() {

        Uri uri = Uri.parse(Static.IMAGE_IP + "/avatar/" + user.getId() + "/150_150");
        imgIcon.setImageURI(uri);

        tvName.setText(user.getRealName());
        if (user.getSex() == 1) {
            tvSex.setText("男");
        } else {
            tvSex.setText("女");
        }
        tvPhone.setText(user.getPhone());
    }

    private void getUserInfo() {

        CustomProgress.show(this, "加载中...", true, null);
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/info");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/info", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            user = User.getUserFromJsonObj(response.getData().optJSONObject("data"));
                            User.setCurrentUser(user);
                            initUi();
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


    private void showValidDialog() {
        if (null == validView) {
            validView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_valid_phone, null);
            TextView tv_title = (TextView) validView.findViewById(R.id.tv_title);
            tv_title.setText("验证手机号");
            et_OldPhone = (TextView) validView.findViewById(R.id.tvOldPhone);

            btnValid = (Button) validView.findViewById(R.id.btnValid);
            btnValid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!flagValid) {
                        flagValid = true;
                        btnValid.setBackgroundResource(R.drawable.btn_valid_grey);
                        btnValid.setText("" + second + "s");
                        handler.removeMessages(0);
                        //每隔1秒钟发送一次handler消息
                        handler.sendEmptyMessageDelayed(0, 1000);
                        getValidNum();
                    }
                }
            });
            etValid = (EditText) validView.findViewById(R.id.etValid);

            mValidDialog = new AlertDialog.Builder(this)
                    .setView(validView)
                    .setCancelable(true)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mValidDialog.dismiss();
                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkValid();
                        }
                    })
                    .create();
        }
        et_OldPhone.setText(user.getPhone());
        etValid.setText("");

        mValidDialog.show();
    }

    /**
     * 获取验证码
     */
    private void getValidNum() {
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/common/v1/sms/send-verification-code?phone=" + tvOldPhone.getText().toString() + "&kind=4");

        Map<String, String> mParams = new HashMap<>();
        mParams.put("phone", et_OldPhone.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/common/v1/sms/send-verification-code", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {


                        } else {
                            flagValid = false;
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        flagValid = false;
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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    /**
     * 检查验证码是否正确
     */
    private void checkValid() {

        CustomProgress.show(this, "加载中...", true, null);

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/common/v1/sms/check-verification-code?account=" + tvOldPhone.getText().toString() + "&code=" + etValid.getText().toString());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("account", et_OldPhone.getText().toString().trim());
        mParams.put("code", etValid.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/common/v1/sms/check-verification-code", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {

                        CustomProgress.hideDialog();
                        LogUtil.e("checkValid: " + result);
                        Response response = CommonUtil.checkResponse(result);

                        if (response.isStatus()) {
                            UiUtils.showToast("验证成功");
                            showUpdateDialog();

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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    private void showUpdateDialog() {
        if (null == updateView) {
            updateView = LayoutInflater.from(this).inflate(
                    R.layout.dialog_update_phone, null);
            btnValidNew = (Button) updateView.findViewById(R.id.btnValidNew);
            etValidNew = (EditText) updateView.findViewById(R.id.etValidNew);
            etPhoneNew = (EditText) updateView.findViewById(R.id.etPhoneNew);
            btnValidNew.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!flagValidUpdate) {
                        flagValidUpdate = true;
                        btnValidNew.setBackgroundResource(R.drawable.btn_valid_grey);
                        btnValidNew.setText("" + secondUpdate + "s");
                        handler.removeMessages(1);
                        //每隔1秒钟发送一次handler消息
                        handler.sendEmptyMessageDelayed(1, 1000);
                        getValidNum();
                    }
                }
            });
            mUpdateDialog = new AlertDialog.Builder(this)
                    .setView(updateView)
                    .setCancelable(true)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mUpdateDialog.dismiss();
                        }
                    }).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            doUpdatePhone();
                        }
                    })
                    .create();
        }
        etPhoneNew.setText("");
        etValidNew.setText("");
        mUpdateDialog.show();
    }

    /**
     * 修改手机号
     */
    private void doUpdatePhone() {
        CustomProgress.show(this, "提交中...", true, null);
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/modify-phone?token=" + MyApplication.getInstance().getShareUser().getString("token", "") + "&account=" + etPhoneNew.getText().toString() + "&code=" + etValidNew.getText().toString());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("account", etPhoneNew.getText().toString().trim());
        mParams.put("code", etValidNew.getText().toString().trim());

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/modify-phone", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            UiUtils.showToast("修改成功");
                            User user = User.getCurrentUser();
                            user.setPhone(etPhoneNew.getText().toString());
                            User.setCurrentUser(user);

                            tvPhone.setText(user.getPhone());

                            SharedPreferences.Editor editor = MyApplication.getInstance().getShareApp().edit();
                            editor.putString(MyApplication.getInstance().USERNAME, etPhoneNew.getText().toString());
                            editor.commit();

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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    private void doUpdateInfo() {

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/user/update");
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("realName", tvName.getText().toString());
        mParams.put("sex", "" + sex);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/user/update", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
//                            Toast.makeText(x.app(), "修改成功!", Toast.LENGTH_SHORT).show();
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
//                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        } else { // 其他错误
                            // ...
                        }
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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

    private void showPopwindowSex() {

        if (popupSex == null) {
            popSex = LayoutInflater.from(this).inflate(R.layout.popup_sex, null);
            TextView tvMale = (TextView) popSex.findViewById(R.id.tv_male);
            TextView tvFemale = (TextView) popSex.findViewById(R.id.tv_female);
            TextView tvCancel = (TextView) popSex.findViewById(R.id.tvCancelSex);


            popupSex = new PopupWindow(popSex,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);

            // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
//            popupSex.setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
//            ColorDrawable dw = new ColorDrawable(0x7d000000);
            popupSex.setBackgroundDrawable(new BitmapDrawable());
            // 设置popWindow的显示和消失动画
            popupSex.setAnimationStyle(R.style.bottom_dialog);
            popupSex.setOutsideTouchable(true);
            tvMale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sex = 1;
                    popupSex.dismiss();
                    tvSex.setText("男");
                }
            });
            tvFemale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sex = 2;
                    popupSex.dismiss();
                    tvSex.setText("女");
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupSex.dismiss();
                }
            });
        }

        // 设置背景色变暗
        UiUtils.darkenScreen(this, popupSex);

        // 在底部显示
        popupSex.showAtLocation(this.findViewById(R.id.widget_custom_titlebar),
                Gravity.BOTTOM, 0, 0);

    }

    private void showImage() {
        handlerKeyboard(getWindow().peekDecorView(), false);
        if (popupPhoto == null) {
            popPhoto = inflater.inflate(R.layout.popup_three, null);
            TextView tvPhoto = (TextView) popPhoto.findViewById(R.id.tv_one);
            TextView tvLocal = (TextView) popPhoto.findViewById(R.id.tv_two);
            TextView tvCancel = (TextView) popPhoto.findViewById(R.id.tv_cancel);

            tvPhoto.setText("拍照");
            tvLocal.setText("从相册中选取");
            tvCancel.setText("取消");

            popupPhoto = new PopupWindow(popPhoto,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
            popupPhoto.setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
//            ColorDrawable dw = new ColorDrawable(0x7d000000);
            popupPhoto.setBackgroundDrawable(new BitmapDrawable());
            // 设置popWindow的显示和消失动画
            popupPhoto.setAnimationStyle(R.style.bottom_dialog);

            popupPhoto.setOutsideTouchable(true);

            tvPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // 请求相机
                    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                    // 判断存储卡是否可以用，可用进行存储
                    if (hasSdcard()) {
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment
                                        .getExternalStorageDirectory(), PHOTO_FILE_NAME)));
                    }
                    startActivityForResult(intent, REQUEST_CODE_CAMERA);

                    popupPhoto.dismiss();

                }
            });
            tvLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 激活系统图库，选择一张图片
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_CODE_GALLERY);
                    popupPhoto.dismiss();

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupPhoto.dismiss();
                }
            });
        }

        UiUtils.darkenScreen(this, popupPhoto);

        // 在底部显示
        popupPhoto.showAtLocation(mTitleBar,
                Gravity.BOTTOM, 0, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == REQUEST_CODE_CAMERA) {
            if (hasSdcard()) {
                tempFile = new File(Environment.getExternalStorageDirectory(),
                        PHOTO_FILE_NAME);
                crop(Uri.fromFile(tempFile));
            } else {
                UiUtils.showToast("未找到存储卡，无法存储照片！");
            }

        } else if (requestCode == REQUEST_CODE_CROP) {
            try {
                bitmap = data.getParcelableExtra("data");
                doPhoto();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        // 图片格式
        intent.putExtra("outputFormat", "JPEG");
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
        startActivityForResult(intent, REQUEST_CODE_CROP);
    }

    /**
     * isOpen = true  显示
     * isOpen = false 隐藏
     * 当v为空时可以调用getWindow().peekDecorView()获取整个屏幕的View
     */
    private static void handlerKeyboard(View v, boolean isOpen) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isOpen) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        }
    }

    private void doPhoto() {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            byte[] buffer = out.toByteArray();

            String photo = android.util.Base64.encodeToString(buffer, android.util.Base64.DEFAULT).trim();

            uploadIcon(photo);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void uploadIcon(final String photo) {

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", MyApplication.getInstance().getShareUser().getString("token", ""));
        mParams.put("image", photo);

        Map<String, String> mParamsBody = new HashMap<>();
        mParamsBody.put("image", photo);

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/userAvatar/add-headpic", mParams, mParamsBody),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            LogUtil.e("上传成功");

                            // 清除图片缓存
                            Uri uri = Uri.parse(Static.IMAGE_IP + "/avatar/" + user.getId() + "/150_150");
                            ImagePipeline imagePipeline = Fresco.getImagePipeline();
                            imagePipeline.evictFromMemoryCache(uri);
                            imagePipeline.evictFromDiskCache(uri);
                            avatarIsChanged = true;
                            imgIcon.setImageURI(uri);

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
                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
        mTitleBar.setCenterTitle(school);
        mTitleBar.setActivity(this);
        /*mTitleBar.setRightTitle("保存");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doUpdateInfo();
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        if (flagUpdateUserInfo) {
            new Thread() {
                @Override
                public void run() {
                    doUpdateInfo();
                }
            }.start();
        }
        super.onDestroy();
    }
}
