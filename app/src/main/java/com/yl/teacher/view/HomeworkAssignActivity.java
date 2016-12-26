package com.yl.teacher.view;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.FrescoImageLoader;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;
import com.zhy.autolayout.AutoRelativeLayout;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 作业布置界面
 */
public class HomeworkAssignActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CODE_ALBUM = 0;
    private final int REQUEST_CODE_CAMERA = 1;

    private CustomTitleBar custom_titlebar;
    private ImageOptions imageOptions;
    private EditText et_classname;
    private TextView tv_date;
    private EditText et_publisher;
    private EditText et_content;
    private AutoRelativeLayout rl_date;
    private ImageView iv_image;
    private String date;
    private String content;
    private String photo;
    private ImageView iv_remove;
    private boolean mFlagSend = true;
//    private DatePickerPopWin pickerPopWin;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private long mChoosedTimeL;
    private long mCurrentTimeL;
    private FunctionConfig functionConfig;
    private PopupWindow popupWindow;
    private List<String> mImageTypes;

    @Override
    protected int loadLayout() {
        return R.layout.activity_homeworkassgin;
    }

    @Override
    protected void initViews() {
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
//        et_classname = (EditText) findViewById(R.id.et_classname);
        tv_date = (TextView) findViewById(R.id.et_homework_date);
//        et_publisher = (EditText) findViewById(R.id.et_publisher);
        et_content = (EditText) findViewById(R.id.et_homework_content);
        rl_date = (AutoRelativeLayout) findViewById(R.id.rl_homework_date);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_remove = (ImageView) findViewById(R.id.iv_remove);
    }

    @Override
    protected void initData() {
        initGallery();
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar.setCenterTitle("作业布置");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setRightTitle("添加");
        custom_titlebar.displayRightItem(true);
//        et_classname.setText(MyApplication.getInstance().getClassesName());

        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.btn_add)
                .setFailureDrawableId(R.drawable.btn_add)
                .build();
    }

    @Override
    protected void initListener() {
        // 返回键
        custom_titlebar.setActivity(this);
        // 右键
        custom_titlebar.setRightItemOnClickListener(this);
        rl_date.setOnClickListener(this);
        iv_image.setOnClickListener(this);
        iv_remove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.widget_custom_titlebar_right_item: // 发送
                sendHomework();
                break;

            case R.id.rl_homework_date:
                showDialog();
                break;

//            case R.id.et_homework_content:
            case R.id.iv_image:
                /*Intent intent = new Intent(HomeworkAssignActivity.this, VoteMsgActivity.class);
                intent.putExtra("pageFrom", Static.EVENTBUS_TYPE_HOMEWORK);
                content = et_content.getText().toString().trim();
                intent.putExtra("homework", content);
                startActivity(intent);*/
                showPopupWindow();
                break;

            case R.id.iv_remove: // 移除
                iv_image.setImageResource(R.drawable.btn_add);
                photo = "";
                iv_remove.setVisibility(View.GONE);
                break;

            case R.id.tv_one: // 拍照
                getImageFromCamera();
                break;

            case R.id.tv_two: // 从相册中选取
                getImageFromAlbum();
                break;

            case R.id.tv_cancel: // 取消
                popupWindow.dismiss();
                break;

            default:
                break;
        }
    }

    private void doPhoto(final String path) {

        /*new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
                .setTitleText("确定添加作业？")
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
                        sweetAlertDialog.dismiss();
                        new Thread() {
                            @Override
                            public void run() {

                                photo = CommonUtil.bitmapToString(path);
                                mHandler.sendMessage(new Message());

                            }
                        }.start();
                    }
                })
                .show();*/

        if (path != null) {

            x.image().bind(iv_image, path, imageOptions);
            iv_remove.setVisibility(View.VISIBLE);
            mImageTypes = new ArrayList<>();
            String mimeType = StringUtils.getMimeType(path);
            mImageTypes.add(mimeType);


            new Thread() {
                @Override
                public void run() {

                    photo = CommonUtil.bitmapToString(path);

                }
            }.start();

        } else {
            UiUtils.showToast("图片选取错误");
        }

    }

    /**
     * 发送作业
     */
    private void sendHomework() {

        // 布置作业
        date = tv_date.getText().toString().trim();
        content = et_content.getText().toString().trim();
        if (StringUtils.isEmpty(date) || StringUtils.isEmpty(content)) {
            UiUtils.showToast("请填写带*号的内容");
            return;
        }

        if (mChoosedTimeL < mCurrentTimeL) {
            UiUtils.showToast("时间不能早于今天");
            return;
        }

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE, true)
                .setTitleText("确定添加作业？")
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
                        sweetAlertDialog.dismiss();

                        CustomProgress.show(HomeworkAssignActivity.this, "上传中...", true, null);

                        String token = MyApplication.getInstance().getShareUser().getString("token", "");
                        String classId = MyApplication.getInstance().getClassId();

                        Map<String, String> mParams = new HashMap<>();
                        mParams.put("token", token);
                        mParams.put("homeworkDate", date);
                        mParams.put("Class_id", classId);
                        mParams.put("content", content);

//                        String url = Static.URL_SERVER + "/teacher/v1/homework/add";
//                        RequestParams params = new RequestParams(url);
//                        // token=" + token + "&homeworkDate=" + date + "&Class_id=" + id + "&content=" + content
//                        params.addQueryStringParameter("token", token);
//                        params.addQueryStringParameter("homeworkDate", date);
//                        params.addQueryStringParameter("Class_id", classId);
//                        params.addQueryStringParameter("content", content);
                        Map<String, String> mParamsBody = new HashMap<>();
                        if (!StringUtils.isEmpty(photo)) {
                            List<String> mList = new ArrayList<>();
                            mList.add(photo);
                            mParamsBody.put("image", GsonUtils.toJson(mList));
                            mParamsBody.put("imageType", GsonUtils.toJson(mImageTypes));
                            mParams.put("image", GsonUtils.toJson(mList));
                            mParams.put("imageType", GsonUtils.toJson(mImageTypes));
                        }

                        if (mFlagSend) {

                            mFlagSend = false;

                            x.http().post(HttpUtils.getRequestParams("/teacher/v1/homework/add", mParams, mParamsBody), new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    Response response = CommonUtil.checkResponse(s);
                                    if (response.isStatus()) {
                                        UiUtils.showToast("发送成功");
                                        finish();
                                    } else {
                                        UiUtils.showToast(response.getData().optString("message"));
                                    }
                                    CustomProgress.hideDialog();
                                }

                                @Override
                                public void onError(Throwable throwable, boolean b) {
                                    CustomProgress.hideDialog();
                                    mFlagSend = true;
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
                                }

                                @Override
                                public void onCancelled(CancelledException e) {
                                    CustomProgress.hideDialog();
                                    mFlagSend = true;
                                }

                                @Override
                                public void onFinished() {
                                    CustomProgress.hideDialog();
                                }
                            });

                        }

                    }
                })
                .show();

    }

    private void showDialog() {
        UiUtils.closeKeyboard(this);
        /*pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                pickerPopWin.dismiss();
                tv_date.setText(dateDesc);
                try {

                    mChoosedTimeL = mDateFormat.parse(dateDesc).getTime();
                    // 获取当前时间
                    mCurrentTimeL = mDateFormat.parse(mDateFormat.format(new Date())).getTime();;

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).textConfirm("确定") //text of confirm button
                .textCancel("取消") //text of cancel button
                .btnTextSize(16) // button text size
                .viewTextSize(25) // pick view text size
                .colorCancel(Color.parseColor("#999999")) //color of cancel button
                .colorConfirm(Color.parseColor("#009900"))//color of confirm button
                .minYear(1900) //min year in loop
                .maxYear(2550) // max year in loop
//                .dateChose() // date chose when init popwindow
                .build();

        pickerPopWin.showPopWin(this);*/

        // 时间选择器
        TimePickerView mTimePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        // 设置时间范围
        Calendar mCalendar = Calendar.getInstance();
        mTimePickerView.setRange(mCalendar.get(Calendar.YEAR) - 100, mCalendar.get(Calendar.YEAR) + 100);
        String thisTime = tv_date.getText().toString().trim();
        if (TextUtils.isEmpty(thisTime)) {
            mTimePickerView.setTime(new Date());
        } else {
            try {
                Date date = mDateFormat.parse(thisTime);
                mTimePickerView.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mTimePickerView.setCyclic(true);
        mTimePickerView.setCancelable(true);
        // 时间选择后回调
        mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                String mDate = mDateFormat.format(date);
                tv_date.setText(mDate);
                mChoosedTimeL = date.getTime();
                // 获取当前时间
                try {
                    mCurrentTimeL = mDateFormat.parse(mDateFormat.format(new Date())).getTime();
                } catch (ParseException e) {

                }
                LogUtil.e("mChoosedTimeL: "+mChoosedTimeL);
                LogUtil.e("mCurrentTimeL: "+mCurrentTimeL);
            }
        });
        mTimePickerView.show();
    }

    private void initGallery() {
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(0x4b, 0x45, 0x50))
                .build();
        ThemeConfig themeConfig = theme;
        cn.finalteam.galleryfinal.ImageLoader imageLoader = new FrescoImageLoader(this);
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setEnableEdit(false);
        functionConfigBuilder.setEnableRotate(false);
        functionConfigBuilder.setEnableCrop(false);
        functionConfigBuilder.setEnableCamera(false);
        functionConfigBuilder.setEnablePreview(false);
        functionConfigBuilder.setMutiSelectMaxSize(9);
        functionConfig = functionConfigBuilder.build();
        CoreConfig coreConfig = new CoreConfig.Builder(this, imageLoader, themeConfig)
                .setFunctionConfig(functionConfig)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);
    }

    private void showPopupWindow() {
        UiUtils.closeKeyboard(this);
        View view = View.inflate(this, R.layout.popup_three, null);
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_one);
        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_two);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        tvCamera.setText("拍照");
        tvAlbum.setText("从相册中选取");
        tvCancel.setText("取消");

        tvCamera.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
        }
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.bottom_dialog);
        popupWindow.setOutsideTouchable(true);

        UiUtils.darkenScreen(this, popupWindow);

        // 在底部显示
        popupWindow.showAtLocation(this.findViewById(R.id.widget_custom_titlebar),
                Gravity.BOTTOM, 0, 0);
    }

    private void getImageFromCamera() {
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
        popupWindow.dismiss();
    }

    private void getImageFromAlbum() {
        GalleryFinal.openGallerySingle(REQUEST_CODE_ALBUM, functionConfig, mOnHanlderResultCallback);
        popupWindow.dismiss();
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                String path = resultList.get(0).getPhotoPath();
//                x.image().bind(imgVote, new File(resultList.get(0).getPhotoPath()).toURI().toString(), imageOptions);
                doPhoto(path);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            UiUtils.showToast(errorMsg);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("HomeworkAssignActivity");
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("HomeworkAssignActivity");
        MobclickAgent.onPause(this);
    }

}
