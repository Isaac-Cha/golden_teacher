package com.yl.teacher.view;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

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

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by $USER_NAME on 2016/9/6.
 * 课程表添加界面
 */
public class SyllabusAddActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CODE_ALBUM = 0;
    private final int REQUEST_CODE_CAMERA = 1;

    private FunctionConfig functionConfig;
    private String path;
    private String photo;
    private List<String> mImageTypes;

    private CustomTitleBar mTitleBar;
    private TextView tv_title;
    private ImageView iv_image;
    private ImageView iv_remove;
    private PopupWindow popupWindow;
    private ImageOptions imageOptions;

    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SyllabusAddActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SyllabusAddActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_syllabusadd;
    }

    @Override
    protected void initViews() {
        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        iv_remove = (ImageView) findViewById(R.id.iv_remove);
    }

    @Override
    protected void initData() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        mTitleBar.setCenterTitle("添加课程表");
        mTitleBar.displayBackBtn(true);
        mTitleBar.setRightTitle("添加");
        mTitleBar.displayRightItem(true);
        initGallery();
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
    }

    @Override
    protected void initListener() {

        mTitleBar.setActivity(this);

        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        iv_image.setOnClickListener(this);
        iv_remove.setOnClickListener(this);
    }

    private void showDialog() {
        UiUtils.closeKeyboard(this);
        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE, true)
                .setTitleText("确定发布本课程表吗？")
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
                        doPhoto();
                    }
                }).show();
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

    private void sendSyllabus() {

        String title = tv_title.getText().toString().trim();
        if (StringUtils.isEmpty(title)) {
            UiUtils.showToast("请输入标题");
            return;
        }

        if (StringUtils.isEmpty(photo)) {
            UiUtils.showToast("请添加照片");
            return;
        }
//        CustomProgress.hideDialog();
        CustomProgress.show(this, "上传中...", true, null);
        // 添加课程表
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String classId = MyApplication.getInstance().getClassId();
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classSchedule/add");
//        params.addQueryStringParameter("token", token);
//        params.addQueryStringParameter("Class_id", classId);
        List<String> list = new ArrayList<>();
        list.add(photo.trim());

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("title", title);
        mParams.put("image", GsonUtils.toJson(list));
        mParams.put("imageType", GsonUtils.toJson(mImageTypes));
        mParams.put("image", GsonUtils.toJson(list));
        mParams.put("imageType", GsonUtils.toJson(mImageTypes));

        Map<String, String> mParamsBody = new HashMap<>();
        mParamsBody.put("image", GsonUtils.toJson(list));
        mParamsBody.put("imageType", GsonUtils.toJson(mImageTypes));

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/classSchedule/add", mParams, mParamsBody), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                CustomProgress.hideDialog();
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    UiUtils.showToast("上传成功");
                    finish();
                } else {
                    UiUtils.showToast(response.getData().optString("message"));
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                CustomProgress.hideDialog();

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

    private void doPhoto() {
        new Thread() {
            @Override
            public void run() {
                photo = CommonUtil.bitmapToString(path);
                mImageTypes = new ArrayList<>();
                String mImageType = StringUtils.getMimeType(path);
                mImageTypes.add(mImageType);
                mHandler.sendMessage(new Message());
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            sendSyllabus();
        }
    };

    private void getImageFromCamera() {
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
        popupWindow.dismiss();
    }

    private void getImageFromAlbum() {
        GalleryFinal.openGallerySingle(REQUEST_CODE_ALBUM, functionConfig, mOnHanlderResultCallback);
        popupWindow.dismiss();
    }

    private void showPopupWindow() {
        UiUtils.closeKeyboard(this);
        View popupView = View.inflate(this, R.layout.popup_three, null);
        TextView tvCamera = (TextView) popupView.findViewById(R.id.tv_one);
        TextView tvAlbum = (TextView) popupView.findViewById(R.id.tv_two);
        TextView tvCancel = (TextView) popupView.findViewById(R.id.tv_cancel);

        tvCamera.setText("拍照");
        tvAlbum.setText("从相册中选取");
        tvCancel.setText("取消");

        tvCamera.setOnClickListener(this);
        tvAlbum.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        if (popupWindow == null) {
            popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT,
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

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                path = resultList.get(0).getPhotoPath();
                if (!StringUtils.isEmpty(path))
                    x.image().bind(iv_image, new File(path).toURI().toString(), imageOptions);
                    iv_remove.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
        }
    };

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.iv_image:
                showPopupWindow();
                break;

            case R.id.iv_remove:
                removeImage();
                break;

            case R.id.tv_one:
                getImageFromCamera();
                break;

            case R.id.tv_two:
                getImageFromAlbum();
                break;

            case R.id.tv_cancel:
                popupWindow.dismiss();
                break;

            default:
                break;

        }

    }

    private void removeImage() {
        iv_remove.setVisibility(View.GONE);
        path = "";
        iv_image.setImageResource(R.drawable.selector_btn_add_pic);
    }

}
