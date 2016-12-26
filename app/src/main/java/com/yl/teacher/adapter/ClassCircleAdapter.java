package com.yl.teacher.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yl.teacher.R;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.ClassCircle;
import com.yl.teacher.model.ClassCircle.DataBean.ListBean;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GlideCircleTransform;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.util.ViewHolder;
import com.yl.teacher.view.ImageDetailsActivity;
import com.yl.teacher.widget.MultiImageView;
import com.yl.teacher.xalertdialog.SweetAlertDialog;
import com.zhy.autolayout.AutoLinearLayout;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by $USER_NAME on 2016/10/13.
 */
public class ClassCircleAdapter extends BaseAdapter {

    private final int NO_PRAISE = 2;
    private final int IS_PRAISED = 1;

    private Activity mActivity;
    private List<ClassCircle.DataBean.ListBean> mDatas;
    private LinearLayout noDataLayout;
    private LayoutInflater mInflater;
    private final int currentUserId;
    private String token;

    public ClassCircleAdapter(Activity activity, List<ClassCircle.DataBean.ListBean> data, LinearLayout noDataLayout) {
        mActivity = activity;
        mDatas = data;
        this.noDataLayout = noDataLayout;
        mInflater = LayoutInflater.from(activity);
        // 获取当前用户ID
        currentUserId = MyApplication.getInstance().getShareUser().getInt("id", 0);
        token = MyApplication.getInstance().getShareUser().getString("token", "");
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas == null ? null : mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mDatas == null ? 0 : position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_class_circle, parent, false);
        }

        final ListBean data = mDatas.get(position);

        ImageView iv_avatar = ViewHolder.get(convertView, R.id.iv_avatar);
        ImageView iv_praise_status = ViewHolder.get(convertView, R.id.iv_praise_status);
        TextView tv_nickname = ViewHolder.get(convertView, R.id.tv_nickname);
        TextView tv_praise_count = ViewHolder.get(convertView, R.id.tv_praise_count);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_delete = ViewHolder.get(convertView, R.id.tv_delete);
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        AutoLinearLayout ll_praise = ViewHolder.get(convertView, R.id.ll_praise);
        MultiImageView multi_image_view = ViewHolder.get(convertView, R.id.multi_image_view);


        // 头像
        Glide.with(mActivity).load(data.headpicUrl).transform(new GlideCircleTransform(mActivity)).into(iv_avatar);

        // 点赞状态
        switch (data.praiseStatus) {

            case IS_PRAISED:
                iv_praise_status.setImageResource(R.drawable.img_love_focus);
                break;

            case NO_PRAISE:
            default:
                iv_praise_status.setImageResource(R.drawable.img_love_df);
                break;

        }

        tv_nickname.setText(data.realName);
        tv_praise_count.setText(data.praise + "");
        tv_time.setText(data.createTime);
        tv_title.setText(data.title);

        if (currentUserId == data.User_id) {
            tv_delete.setVisibility(View.VISIBLE);
        } else {
            tv_delete.setVisibility(View.GONE);
        }

        if (data.pictureUrls != null && data.pictureUrls.size() > 0) {
            multi_image_view.setVisibility(View.VISIBLE);
            multi_image_view.setList(data.pictureUrls);
        } else {
            multi_image_view.setVisibility(View.GONE);
        }

        // 点赞操作
        ll_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseClassCircle(position);
            }
        });

        // 删除操作
        tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteClassCircleDialog(data);
            }
        });

        multi_image_view.setOnItemClickListener(new MultiImageView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent mIntent = new Intent(mActivity, ImageDetailsActivity.class);

                mIntent.putStringArrayListExtra(ImageDetailsActivity.INTENT_IMAGE_URLS, data.pictureUrls);
                mIntent.putExtra(ImageDetailsActivity.INTENT_IMAGE_POSITION, position);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mActivity.startActivity(mIntent);
                mActivity.overridePendingTransition(R.anim.activity_in_scale, R.anim.activity_out_scale);

            }
        });

        return convertView;

    }

    /**
     * 点赞，取消点赞
     * @param position
     */
    private void praiseClassCircle(final int position) {

        // $host/teacher/v1/classZone/praise?token=**&Zone_id=**
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Zone_id", mDatas.get(position).id + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classZone/praise", mParams),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {

                        Response response = CommonUtil.checkResponse(s);
                        if (response.isStatus()) {

                            if (mDatas.get(position).praiseStatus == IS_PRAISED) {
                                mDatas.get(position).praiseStatus = NO_PRAISE;
                            } else if (mDatas.get(position).praiseStatus == NO_PRAISE) {
                                mDatas.get(position).praiseStatus = IS_PRAISED;
                            }

                            if (mDatas.get(position).praiseStatus == IS_PRAISED) {
                                mDatas.get(position).praise++;
                            } else if (mDatas.get(position).praiseStatus == NO_PRAISE) {
                                mDatas.get(position).praise--;
                            }

                            notifyDataSetChanged();

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
                            UiUtils.showToast(x.app().getResources().getString(R.string.net_error));
                            // ...
                        } else { // 其他错误
                            // ...
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }

    /**
     * 显示删除确认弹窗
     *
     * @param data
     */
    private void showDeleteClassCircleDialog(final ListBean data) {

        new SweetAlertDialog(mActivity, SweetAlertDialog.WARNING_TYPE, false)
                .setTitleText("确定删除该条空间吗？")
                .setConfirmText("确定")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        deleteClassCircle(data);
                        sweetAlertDialog.dismiss();
                    }
                })
                .setCancelText("取消")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                }).show();

    }

    /**
     * 删除班级空间条目
     *
     * @param data
     */
    private void deleteClassCircle(final ListBean data) {

        // $host/teacher/v1/classZone/del
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("id", data.id + "");

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/classZone/del", mParams),
                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String s) {
                        Response response = CommonUtil.checkResponse(s);
                        if (response.isStatus()) {
                            UiUtils.showToast("删除成功");
                            mDatas.remove(data);

                            if (mDatas.size() <= 0) {
                                noDataLayout.setVisibility(View.VISIBLE);
                            } else {
                                noDataLayout.setVisibility(View.GONE);
                            }

                            notifyDataSetChanged();

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
                            UiUtils.showToast(x.app().getResources().getString(R.string.net_error));
                            // ...
                        } else { // 其他错误
                            // ...
                        }
                    }

                    @Override
                    public void onCancelled(CancelledException e) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }

}
