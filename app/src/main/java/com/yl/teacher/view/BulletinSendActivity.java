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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.FrescoImageLoader;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
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
import org.xutils.http.RequestParams;
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
import de.greenrobot.event.EventBus;

/**
 * 发送公告界面
 */
public class BulletinSendActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final int REQUEST_CODE_ALBUM = 0;
    private final int REQUEST_CODE_CAMERA = 1;

    private CustomTitleBar custom_titlebar;
    private ImageOptions imageOptions;
    private RadioButton rb_send_notic;
    private RadioButton rb_send_action;
    private RadioGroup rg_radiogroup;
    private EditText et_action_title;
    private TextView et_action_starttime;
    private TextView et_action_endtime;
    private EditText et_action_peoplenumber;
    //    private EditText et_action_publisher;
    private EditText et_action_content;
    private ImageView iv_action_iamge;
    private AutoRelativeLayout rl_statrtime;
    private AutoRelativeLayout rl_endtime;
    private AutoRelativeLayout rl_peoplenumber;
    private ImageView iv_starttime_xing;
    private ImageView iv_endtime_xing;
    //    private ImageView iv_publisher_xing;;
    private final int TYPE_NOTIC = 1;
    private final int TYPE_ACTION = 2;
    private int type = TYPE_NOTIC;
    private boolean isStartTime;
    private long startTimeL;
    private long endTimeL;
    private String photo;
    private ImageView iv_remove;
    private boolean mFlagSend = true;
    //    private DatePickerPopWin pickerPopWin;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private long mChoosedTimeL;
    private long mCurrentTimeL;
    private PopupWindow popPhoto;
    private FunctionConfig functionConfig;
    private List<String> mImageTypes;
    private Map<String, String> mParamsBody;
    private Map<String, String> mParams;

    @Override
    protected int loadLayout() {
        return R.layout.activity_bulletinsend;
    }

    @Override
    protected void initViews() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        rb_send_notic = (RadioButton) findViewById(R.id.rb_sendbulletin_notice);
        rb_send_action = (RadioButton) findViewById(R.id.rb_sendbulletin_action);
        rg_radiogroup = (RadioGroup) findViewById(R.id.rg_sendbulletin_radiogroup);
        et_action_title = (EditText) findViewById(R.id.et_sendbulletin_title);
        et_action_starttime = (TextView) findViewById(R.id.et_sendbulletin_starttime);
        et_action_endtime = (TextView) findViewById(R.id.et_sendbulletin_endtime);
        et_action_peoplenumber = (EditText) findViewById(R.id.et_sendbulletin_peoplenumber);
//        et_action_publisher = (EditText) findViewById(R.id.et_sendbulletin_publisher);
        et_action_content = (EditText) findViewById(R.id.et_sendbulletin_content);
        iv_action_iamge = (ImageView) findViewById(R.id.iv_sendbulletin_image);
        iv_remove = (ImageView) findViewById(R.id.iv_sendbulletin_remove);
        rl_statrtime = (AutoRelativeLayout) findViewById(R.id.rl_statrtime);
        rl_endtime = (AutoRelativeLayout) findViewById(R.id.rl_endtime);
        rl_peoplenumber = (AutoRelativeLayout) findViewById(R.id.rl_peoplenumber);
        iv_starttime_xing = (ImageView) findViewById(R.id.iv_starttime_xing);
        iv_endtime_xing = (ImageView) findViewById(R.id.iv_endtime_xing);
//        iv_publisher_xing = (ImageView) findViewById(R.id.iv_publisher_xing);
    }

    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_SEND_BULLETIN) {
            if (!StringUtils.isEmpty(action.getVote().getTitle())) {
                et_action_content.setText(action.getVote().getTitle());
                et_action_content.setSelection(action.getVote().getTitle().length());
            }
            if (!StringUtils.isEmpty(action.getVote().getImage())) {
                x.image().bind(iv_action_iamge, action.getVote().getImage(), imageOptions);
                iv_remove.setVisibility(View.VISIBLE);
                doPhoto(action.getVote().getImage());
            }
        }
    }

    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        initGallery();
        custom_titlebar.setCenterTitle("发公告");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setRightTitle("发布");
        custom_titlebar.displayRightItem(true);
        switchNotic();

        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.btn_add)
                .setFailureDrawableId(R.drawable.btn_add)
                .build();

        /*if (mNoticeFragment == null) {
            mNoticeFragment = new NoticeFragment();
        }
        transaction = getFragmentManager().beginTransaction(); // 开启事务
        transaction.add(R.id.fragment_container, mNoticeFragment, "notice");
        transaction.commit();
        EventBus.getDefault().register(mNoticeFragment);*/
    }

    @Override
    protected void initListener() {
        custom_titlebar.setActivity(this);
        /*custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                UiUtils.showToast(++i + "次");
                sendBulletin();
            }
        });*/
        custom_titlebar.setRightItemOnClickListener(this);
        rg_radiogroup.setOnCheckedChangeListener(this);
        iv_action_iamge.setOnClickListener(this);
        rl_statrtime.setOnClickListener(this);
        rl_endtime.setOnClickListener(this);
        iv_remove.setOnClickListener(this);
    }

    /**
     * 发公告
     */
    private void sendBulletin() {

        if (mChoosedTimeL < mCurrentTimeL) {
            UiUtils.showToast("开始时间不能早于今天");
            return;
        }

        String title = et_action_title.getText().toString().trim();
        String startTimeS = et_action_starttime.getText().toString().trim();
        String endTimeS = et_action_endtime.getText().toString().trim();
        String peoplenumber = et_action_peoplenumber.getText().toString().trim();
//        String publisher = et_action_publisher.getText().toString().trim();
        String content = et_action_content.getText().toString().trim();

        String classId = MyApplication.getInstance().getClassId();
        String token = MyApplication.getInstance().getShareUser().getString("token", "");

//        String url = Static.URL_SERVER + "/teacher/v1/announcement/add";
//        final RequestParams params = new RequestParams(url);
//        params.addQueryStringParameter("token", token);
//        params.addQueryStringParameter("type", type + "");
//        params.addQueryStringParameter("Class_id", classId);

        mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("type", type + "");
        mParams.put("Class_id", classId);

        if (type == TYPE_NOTIC) {

            if (StringUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
                UiUtils.showToast("请填写带 * 号的内容");
                return;
            }

            if ((!StringUtils.isEmpty(startTimeS) && TextUtils.isEmpty(endTimeS))) {
                UiUtils.showToast("请选择结束时间");
                return;
            }

            if ((StringUtils.isEmpty(startTimeS) && !TextUtils.isEmpty(endTimeS))) {
                UiUtils.showToast("请选择开始时间");
                return;
            }
//        params.addQueryStringParameter("title", title);
//        params.addQueryStringParameter("content", content);
            mParams.put("title", title);
            mParams.put("content", content);

            if (!StringUtils.isEmpty(startTimeS)) {
//                params.addQueryStringParameter("startTime", startTimeS);
                mParams.put("startTime", startTimeS);
            }
            if (!StringUtils.isEmpty(endTimeS)) {
//                params.addQueryStringParameter("endTime", endTimeS);
                mParams.put("endTime", endTimeS);
            }
        }

        if (type == TYPE_ACTION) {
            if (StringUtils.isEmpty(title) || StringUtils.isEmpty(content)
                    || StringUtils.isEmpty(startTimeS) || StringUtils.isEmpty(endTimeS)
                    || StringUtils.isEmpty(peoplenumber)) {
                UiUtils.showToast("请填写带 * 号的内容");
                return;
            }
            mParams.put("title", title);
            mParams.put("content", content);
            mParams.put("type", "" + TYPE_ACTION);
            mParams.put("startTime", startTimeS);
            mParams.put("endTime", endTimeS);
            mParams.put("joinMembers", peoplenumber);
        }

        mParamsBody = new HashMap<>();

        if (!StringUtils.isEmpty(photo.trim())) {
            List<String> list = new ArrayList<>();
            list.add(photo.trim());
            mParamsBody.put("image", GsonUtils.toJson(list));
            mParamsBody.put("imageType", GsonUtils.toJson(mImageTypes));
            mParams.put("image", GsonUtils.toJson(list));
            mParams.put("imageType", GsonUtils.toJson(mImageTypes));
        }

        if (endTimeL < startTimeL) {
            UiUtils.showToast("请检查您输入的时间");
            return;
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE, true);
        if (type == TYPE_ACTION) {
            sweetAlertDialog.setTitleText("确定发布活动吗？");
        } else if (type == TYPE_NOTIC) {
            sweetAlertDialog.setTitleText("确定发布通知吗？");
        } else {
            sweetAlertDialog.setTitleText("确定发布公告吗？");
        }
        sweetAlertDialog
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
                        sDialog.dismiss();
                        if (mFlagSend) {
                            mFlagSend = false;

                            CustomProgress.show(BulletinSendActivity.this, "发送中...", true, null);


                            RequestParams params;
                            if (mParams.size() > 0 && mParamsBody.size() > 0) {
                                params = HttpUtils.getRequestParams("/teacher/v1/announcement/add", mParams, mParamsBody);
                            } else {
                                params = HttpUtils.getRequestParams("/teacher/v1/announcement/add", mParams);
                            }

                            x.http().post(params, new Callback.CommonCallback<String>() {
                                @Override
                                public void onSuccess(String s) {
                                    LogUtil.i(s);
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
                                    mFlagSend = true;
                                    CustomProgress.hideDialog();
                                    findViewById(R.id.widget_custom_titlebar_right_button).setClickable(true);
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
                                    UiUtils.showToast("已取消");
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

    private void doPhoto(final String path) {

        if (path != null) {
            mImageTypes = new ArrayList<>();
            x.image().bind(iv_action_iamge, path, imageOptions);
            iv_remove.setVisibility(View.VISIBLE);
            String mImageType = StringUtils.getMimeType(path);
            mImageTypes.add(mImageType);

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.iv_sendbulletin_image:
                showPhotoDialog();
                break;

            case R.id.rl_statrtime:
                isStartTime = true;
                showDialog(et_action_starttime);
                break;

            case R.id.rl_endtime:
                isStartTime = false;
                showDialog(et_action_endtime);
                break;

            case R.id.iv_sendbulletin_remove: // 移除
                iv_action_iamge.setImageResource(R.drawable.btn_add);
                photo = "";
                iv_remove.setVisibility(View.GONE);
                break;

            case R.id.widget_custom_titlebar_right_item:
                sendBulletin();
                break;

            case R.id.tv_one: // 拍照
                getImageFromCamera();
                break;

            case R.id.tv_two: // 从相册中选取
                getImageFromAlbum();
                break;

            case R.id.tv_cancel: // 取消
                popPhoto.dismiss();
                break;

            default:
                break;
        }
    }

    /**
     * 显示图片选择弹窗
     */
    private void showPhotoDialog() {
        View view = View.inflate(this, R.layout.popup_three, null);
        // 只有点击事件，没有其他操作
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_one);
        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_two);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        tvCamera.setText("拍照");
        tvAlbum.setText("从相册中选取");
        tvCancel.setText("取消");

        tvCamera.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        if (popPhoto == null) {
            popPhoto = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT, true);
        }
        popPhoto.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow的显示和消失动画
        popPhoto.setAnimationStyle(R.style.bottom_dialog);
        popPhoto.setOutsideTouchable(true);

        UiUtils.darkenScreen(this, popPhoto);

        // 在底部显示
        popPhoto.showAtLocation(this.findViewById(R.id.widget_custom_titlebar),
                Gravity.BOTTOM, 0, 0);
    }

    private void getImageFromCamera() {
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
        popPhoto.dismiss();
    }

    private void getImageFromAlbum() {
        GalleryFinal.openGallerySingle(REQUEST_CODE_ALBUM, functionConfig, mOnHanlderResultCallback);
        popPhoto.dismiss();
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
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {

            case R.id.rb_sendbulletin_notice: // 通知
                type = TYPE_NOTIC;
                switchNotic();
                break;

            case R.id.rb_sendbulletin_action: // 活动
                type = TYPE_ACTION;
                switchAction();
                break;

            default:
                break;
        }
    }

    private void switchAction() {
        iv_starttime_xing.setVisibility(View.VISIBLE);
        iv_endtime_xing.setVisibility(View.VISIBLE);
//        iv_publisher_xing.setVisibility(View.VISIBLE);
        rl_peoplenumber.setVisibility(View.VISIBLE);
        rb_send_action.setBackgroundResource(R.drawable.btn_friend_bg);
        rb_send_action.setTextColor(getResources().getColor(R.color.white));
        rb_send_notic.setBackgroundColor(Color.TRANSPARENT);
        rb_send_notic.setTextColor(getResources().getColor(R.color.black));
        et_action_title.setHint("请输入活动标题名");
        et_action_starttime.setHint("请选择活动开始时间");
        et_action_endtime.setHint("请选择活动结束时间");
        et_action_peoplenumber.setHint("请输入参与人数");
        et_action_content.setHint("请输入活动内容");
        switchClear();
    }

    private void switchNotic() {
        iv_starttime_xing.setVisibility(View.INVISIBLE);
        iv_endtime_xing.setVisibility(View.INVISIBLE);
//        iv_publisher_xing.setVisibility(View.INVISIBLE);
        rl_peoplenumber.setVisibility(View.GONE);
        rb_send_notic.setBackgroundResource(R.drawable.btn_friend_bg);
        rb_send_notic.setTextColor(getResources().getColor(R.color.white));
        rb_send_action.setBackgroundColor(Color.TRANSPARENT);
        rb_send_action.setTextColor(getResources().getColor(R.color.black));
        et_action_title.setHint("请输入通知标题名");
        et_action_starttime.setHint("请选择通知开始时间");
        et_action_endtime.setHint("请选择通知结束时间");
        et_action_content.setHint("请输入通知内容");
        switchClear();
    }

    private void switchClear() {
        et_action_title.setText("");
        et_action_starttime.setText("");
        et_action_endtime.setText("");
        et_action_peoplenumber.setText("");
//        et_action_publisher.setText("");
        et_action_content.setText("");
        iv_action_iamge.setImageResource(R.drawable.btn_add);
        photo = "";
    }

    private void showDialog(final TextView time) {

        UiUtils.closeKeyboard(this);

        /*pickerPopWin = new DatePickerPopWin.Builder(this, new DatePickerPopWin.OnDatePickedListener() {
            @Override
            public void onDatePickCompleted(int year, int month, int day, String dateDesc) {
                pickerPopWin.dismiss();
                time.setText(dateDesc);
                if (time.getId() == R.id.et_sendbulletin_starttime) {
                    try {
                        mChoosedTimeL = mDateFormat.parse(dateDesc).getTime();
                        // 获取当前时间
                        mCurrentTimeL = mDateFormat.parse(mDateFormat.format(new Date())).getTime();

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else if (time.getId() == et_sendbulletin_endtime) {
                    try {
                        mEndTimeL = mDateFormat.parse(dateDesc).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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
        String thisTime = time.getText().toString().trim();
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
                time.setText(mDate);

                if (time.getId() == R.id.et_sendbulletin_starttime) {
                    mChoosedTimeL = date.getTime();
                    // 获取当前时间
                    try {
                        mCurrentTimeL = mDateFormat.parse(mDateFormat.format(new Date())).getTime();
                    } catch (ParseException e) {

                    }
                    startTimeL = mChoosedTimeL;
                } else if (time.getId() == R.id.et_sendbulletin_endtime) {
                    endTimeL = date.getTime();
                }

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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("BulletinSendActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("BulletinSendActivity");
        MobclickAgent.onPause(this);
    }

}
