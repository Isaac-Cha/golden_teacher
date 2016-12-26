package com.yl.teacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.yl.teacher.R;
import com.yl.teacher.util.AppUtils;

import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by $USER_NAME on 2016/10/13.
 */
public class ClassCircleImageAdapter extends PagerAdapter {

    private Context mContext;
    private Activity mActivity;
    private List<String> mDatas;
    private LayoutInflater mInflater;
    private View popupView;
    private PopupWindow popupWindow;
    private PhotoView photoView;

    public ClassCircleImageAdapter(Activity activity, List<String> datas) {
        mContext = activity;
        mDatas = datas;
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        final View view = mInflater.inflate(R.layout.item_class_circle_image, container, false);

        if (view != null) {

            photoView = (PhotoView) view.findViewById(R.id.image);

            //loading
            final ProgressBar loading = new ProgressBar(mContext);
            FrameLayout.LayoutParams loadingLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            loadingLayoutParams.gravity = Gravity.CENTER;
            loading.setLayoutParams(loadingLayoutParams);
            ((FrameLayout) view).addView(loading);

            final String imgurl = mDatas.get(position);

            Glide.with(mContext)
                    .load(imgurl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存多个尺寸
                    .thumbnail(0.1f)//先显示缩略图  缩略图为原图的1/10
                    .error(R.mipmap.ic_launcher)
                    .into(new GlideDrawableImageViewTarget(photoView) {
                        @Override
                        public void onLoadStarted(Drawable placeholder) {
                            super.onLoadStarted(placeholder);
                               /* if(smallImageView!=null){
                                    smallImageView.setVisibility(View.VISIBLE);
                                    Glide.with(context).load(imgurl).into(smallImageView);
                                }*/
                            loading.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                            loading.setVisibility(View.GONE);
                        }

                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            loading.setVisibility(View.GONE);
                                /*if(smallImageView!=null){
                                    smallImageView.setVisibility(View.GONE);
                                }*/
                        }
                    });

            container.addView(view, 0);

            photoView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // 弹窗
                    showDownloadImagePopup(view, imgurl);
                    return true;
                }
            });

            photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
                @Override
                public void onPhotoTap(View view, float x, float y) {
                    mActivity.finish();
                }

                @Override
                public void onOutsidePhotoTap() {
                    mActivity.finish();
                }
            });

        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    private void showDownloadImagePopup(View view, final String imgurl) {
        if (popupWindow == null) {
            popupView = mInflater.inflate(R.layout.popup_download_image, null, false);
            TextView tv_save = (TextView) popupView.findViewById(R.id.tv_save);
            TextView tv_cancel = (TextView) popupView.findViewById(R.id.tv_cancel);

            popupWindow = new PopupWindow(popupView,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT, true);

            //实例化一个ColorDrawable
            ColorDrawable dw = new ColorDrawable(0x7d000000);
            //设置SelectPicPopupWindow弹出窗体的背景
            popupWindow.setBackgroundDrawable(dw);

            // 设置popWindow的显示和消失动画
            popupWindow.setAnimationStyle(R.style.bottom_dialog);
            popupWindow.setOutsideTouchable(true);

            // 保存图片
            tv_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AppUtils.downloadImage(imgurl);
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });

            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                    popupWindow = null;
                }
            });

        }

        // 在中间显示
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }
}
