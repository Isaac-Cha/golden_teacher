package com.yl.teacher.view;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.CircleAddGridAdapter;
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

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * 班级圈添加界面
 */
public class ClassCircleAddActivity extends BaseActivity implements View.OnClickListener {

    private final int REQUEST_CODE_ALBUM = 1000;
    private final int REQUEST_CODE_CAMERA = 1001;

    private CustomTitleBar custom_titlebar;
    private PopupWindow popupWindow;
    private GridView gv_circle;
    private List<PhotoInfo> photoInfoList;
    private List<PhotoInfo> albumPhotoList = new ArrayList<>();
    private List<PhotoInfo> cameraPhotoList = new ArrayList<>();
    private ImageOptions imageOptions;
    private CircleAddGridAdapter adapter;
    //    private FunctionConfig functionConfig;
    private ImageLoader imageLoader;
    private EditText et_share;
    private List<String> images;
    List<String> mImageTypes;
    private String title;

    @Override
    protected int loadLayout() {
        return R.layout.activity_classcircleadd;
    }

    @Override
    protected void initViews() {
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        gv_circle = (GridView) findViewById(R.id.gv_circle_add);
        et_share = (EditText) findViewById(R.id.et_circle_add);
    }

    @Override
    protected void initData() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initFalleryFinal();
        custom_titlebar.setCenterTitle("班级空间");
        custom_titlebar.displayBackBtn(true);
        custom_titlebar.setRightTitle("发送");
        custom_titlebar.displayRightItem(true);
        photoInfoList = new ArrayList<>();
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
        adapter = new CircleAddGridAdapter(this, photoInfoList, imageOptions);
        adapter.clear();
        gv_circle.setAdapter(adapter);
    }

    private void initFalleryFinal() {
        ThemeConfig theme = new ThemeConfig.Builder()
                .setTitleBarBgColor(Color.rgb(0x4b, 0x45, 0x50))
                .build();
        ThemeConfig themeConfig = theme;
        imageLoader = new FrescoImageLoader(MyApplication.getInstance());
        /*FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setEnableEdit(false);
        functionConfigBuilder.setEnableRotate(false);
        functionConfigBuilder.setEnableCrop(false);
        functionConfigBuilder.setEnableCamera(false);
        functionConfigBuilder.setEnablePreview(false);
        functionConfigBuilder.setMutiSelectMaxSize(Static.MAX_IMAGE_SIZE);
        functionConfigBuilder.setSelected(photoInfoList);
        FunctionConfig functionConfig = functionConfigBuilder.build();*/
        CoreConfig coreConfig = new CoreConfig.Builder(MyApplication.getInstance(), imageLoader, themeConfig)
                .setFunctionConfig(getFunctionConfig())
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);
    }

    private FunctionConfig getFunctionConfig() {
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setEnableEdit(false);
        functionConfigBuilder.setEnableRotate(false);
        functionConfigBuilder.setEnableCrop(false);
        functionConfigBuilder.setEnableCamera(false);
        functionConfigBuilder.setEnablePreview(false);
        functionConfigBuilder.setMutiSelectMaxSize(Static.MAX_IMAGE_SIZE - cameraPhotoList.size());
        functionConfigBuilder.setSelected(photoInfoList);
        return functionConfigBuilder.build();
    }

    @Override
    protected void initListener() {
        // 返回键
        custom_titlebar.setActivity(this);
        custom_titlebar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 发送班级圈，关闭本页
                title = et_share.getText().toString().trim();
                if (StringUtils.isEmpty(title)) {
                    UiUtils.showToast("请分享您的心情");
                    return;
                }

                if (CommonUtil.getStrLength(title) > 250) {
                    UiUtils.showToast("字数超出限制");
                    return;
                }

                /*if (photoInfoList == null || photoInfoList.size() == 0) {
                    UiUtils.showToast("请添加照片");
                    return;
                }*/
//                CustomProgress.show(ClassCircleAddActivity.this, "上传中...", true, null);
                doPhoto();

            }
        });
        gv_circle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopupWindow();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_one: // 拍照
                getImageFromCamera();
                break;

            case R.id.tv_two: // 从相册中选取
                getImageFromAlbum();
                break;

            case R.id.tv_cancel: // 取消
                hidePopupWindow();
                break;

            default:
                break;

        }
    }

    private void getImageFromCamera() {
        GalleryFinal.openCamera(REQUEST_CODE_CAMERA, getFunctionConfig(), mOnHanlderResultCallback);
    }

    private void getImageFromAlbum() {
        GalleryFinal.openGalleryMuti(REQUEST_CODE_ALBUM, getFunctionConfig(), mOnHanlderResultCallback);
    }

    private void hidePopupWindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            /*if (resultList != null) {
                photoInfoList.clear();
                photoInfoList.addAll(resultList);
                // x.image().bind(imgVote, new File(resultList.get(0).getPhotoPath()).toURI().toString(), imageOptions);
            }*/
            List<PhotoInfo> mList = new ArrayList<>();
            if (resultList != null) {

                for (PhotoInfo photoInfo : resultList) {
                    for (PhotoInfo info : photoInfoList) {
                        if (photoInfo.getPhotoPath().equals(info.getPhotoPath())) {
                            mList.add(photoInfo);
                        }
                    }
                }
                resultList.removeAll(mList);

                photoInfoList.addAll(resultList);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            UiUtils.showToast(errorMsg);
        }
    };

    /**
     * 显示popupwindow
     */
    private void showPopupWindow() {

        UiUtils.closeKeyboard(this);

        View view = View.inflate(this, R.layout.popup_three, null);
        // 只有点击事件，没有其他操作
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_one);
        TextView tvPhoto = (TextView) view.findViewById(R.id.tv_two);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);

        tvCamera.setText("拍照");
        tvPhoto.setText("从相册中选取");
        tvCancel.setText("取消");

        tvCamera.setOnClickListener(this);
        tvPhoto.setOnClickListener(this);
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

    /**
     * 发送班级圈
     */
    private void sendClassCircle() {
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String classId = MyApplication.getInstance().getClassId();
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classZone/add");
//        params.addQueryStringParameter("token", token);
//        params.addQueryStringParameter("Class_id", classId);
//        params.addQueryStringParameter("title", title);
//        params.addBodyParameter("images", mGson.toJson(images));
//        params.addBodyParameter("imageType", mGson.toJson(mImageTypes));

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("title", title);
        mParams.put("images", GsonUtils.toJson(images));
        mParams.put("imageType", GsonUtils.toJson(mImageTypes));

        Map<String, String> mParamsBody = new HashMap<>();
        mParamsBody.put("images", GsonUtils.toJson(images));
        mParamsBody.put("imageType", GsonUtils.toJson(mImageTypes));

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/classZone/add", mParams, mParamsBody), new Callback.CommonCallback<String>() {
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


        new SweetAlertDialog(ClassCircleAddActivity.this, SweetAlertDialog.NORMAL_TYPE, true)
                .setTitleText("确认发送吗？")
                .setCancelText("取消")
                .setConfirmText("确定")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        CustomProgress.show(ClassCircleAddActivity.this, "发送中...", true, null);

                        new Thread() {
                            @Override
                            public void run() {
                                images = new ArrayList<>();
                                mImageTypes = new ArrayList<>();
                                for (int i = 0; i < photoInfoList.size(); i++) {
                                    String path = photoInfoList.get(i).getPhotoPath();
                                    String photoPath = CommonUtil.bitmapToString(path);
                                    images.add(photoPath);
                                    String mimeType = StringUtils.getMimeType(path);
                                    mImageTypes.add(mimeType);
                                }

                                mHandler.sendMessage(new Message());
                            }
                        }.start();
                    }
                })
                .show();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            sendClassCircle();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ClassCircleAddActivity");
        MobclickAgent.onResume(this);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ClassCircleAddActivity");
        MobclickAgent.onPause(this);
        hidePopupWindow();
    }

}
