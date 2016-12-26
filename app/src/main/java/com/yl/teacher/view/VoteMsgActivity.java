package com.yl.teacher.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.VoteOption;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.FrescoImageLoader;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.util.LogUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.List;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import de.greenrobot.event.EventBus;

/**
 * 添加投票照片和内容页面
 */
public class VoteMsgActivity extends BaseActivity {

    private FunctionConfig functionConfig;

    private ImageOptions imageOptions;

    private final int REQUEST_CODE_CAMERA = 1000;
    private final int REQUEST_CODE_GALLERY = 1001;
    private final int TYPE_NOTIC = 1;
    private final int TYPE_ACTION = 2;

    private CustomTitleBar mTitleBar;

    private EditText etComment;

    private ImageView imgVote;

    private RelativeLayout relaRemove;

    private String path = "";

    private String photo;

    private LayoutInflater inflater;

    private PopupWindow popupWindow;

    private View popView;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };
    private VoteOption voteOption;

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("VoteMsgActivity");
        MobclickAgent.onResume(this);
        closeKeyboard();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("VoteMsgActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_votemsg;
    }

    @Override
    protected void initViews() {

        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
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
        inflater = LayoutInflater.from(this);
        etComment = (EditText) findViewById(R.id.etComment);
        imgVote = (ImageView) findViewById(R.id.imgVote);
        relaRemove = (RelativeLayout) findViewById(R.id.relaRemove);

    }

    @Override
    protected void initData() {
        voteOption = (VoteOption) getIntent().getSerializableExtra("VoteOption");

        if (voteOption != null) {
            etComment.setText(voteOption.getTitle());
            etComment.setSelection(voteOption.getTitle().length());
            x.image().bind(imgVote, voteOption.getImage(), imageOptions);
            path = voteOption.getImage();
        }

        if (!TextUtils.isEmpty(path)) {
            relaRemove.setVisibility(View.VISIBLE);
        }

        initGallery();
        initBar();
    }

    @Override
    protected void initListener() {
        relaRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relaRemove.setVisibility(View.GONE);
                path = "";
                imgVote.setImageResource(R.drawable.btn_add);
            }
        });
        imgVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage();
            }
        });
    }


    private void doPhoto() {
        new Thread() {
            @Override
            public void run() {

                photo = CommonUtil.bitmapToString(path);
                handler.sendMessage(new Message());

            }
        }.start();
    }

    private void showImage() {
        handlerKeyboard(getWindow().peekDecorView(), false);
        if (popupWindow == null) {
            popView = inflater.inflate(R.layout.popup_three, null);
            TextView tvPhoto = (TextView) popView.findViewById(R.id.tv_one);
            TextView tvLocal = (TextView) popView.findViewById(R.id.tv_two);
            TextView tvCancel = (TextView) popView.findViewById(R.id.tv_cancel);

            tvPhoto.setText("拍照");
            tvLocal.setText("从相册中选取");
            tvCancel.setText("取消");

            popupWindow = new PopupWindow(popView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
            popupWindow.setFocusable(true);
            // 实例化一个ColorDrawable颜色为半透明
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            // 设置popWindow的显示和消失动画
            popupWindow.setAnimationStyle(R.style.bottom_dialog);
            popupWindow.setOutsideTouchable(true);

            tvPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    GalleryFinal.openCamera(REQUEST_CODE_CAMERA, functionConfig, mOnHanlderResultCallback);
                    popupWindow.dismiss();

                }
            });
            tvLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GalleryFinal.openGallerySingle(REQUEST_CODE_GALLERY, functionConfig, mOnHanlderResultCallback);
                    popupWindow.dismiss();

                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }

        UiUtils.darkenScreen(this, popupWindow);

        // 在底部显示
        popupWindow.showAtLocation(mTitleBar,
                Gravity.BOTTOM, 0, 0);

    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                relaRemove.setVisibility(View.VISIBLE);
                LogUtil.d("" + resultList.get(0).getPhotoPath());
                path = resultList.get(0).getPhotoPath();
                x.image().bind(imgVote, new File(resultList.get(0).getPhotoPath()).toURI().toString(), imageOptions);
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(VoteMsgActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

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

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("添加投票选项");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("添加");
        mTitleBar.displayRightItem(true);
        etComment.setHint("请输入标题");

        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                String title = etComment.getText().toString();
                if (StringUtils.isEmpty(title)) {
                    UiUtils.showToast("标题不能为空");
                    return;
                }

                if (voteOption == null) {
                    voteOption = new VoteOption();
                    voteOption.id = StringUtils.getUUID();
                    voteOption.setImage(path);
                    voteOption.setTitle(title);
                } else {
                    voteOption.setImage(path);
                    voteOption.setTitle(title);
                }

                Action action = new Action();
                action.setId(Static.EVENTBUS_TYPE_VOTE);
                action.setVote(voteOption);
                EventBus.getDefault().post(action);
                finish();

            }

        });
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

    private void closeKeyboard() {
        if (getCurrentFocus() != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getCurrentFocus()
                                    .getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
