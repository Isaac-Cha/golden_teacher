package com.yl.teacher.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.HomeworkModel;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.xalertdialog.SweetAlertDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作业班级列表
 * Created by GA_PC_Sample on 2016/5/11.
 */
public class HomeworkClassesListAdapter extends BaseAdapter {

    private int[] classNameBgs = {R.drawable.homework_blue, R.drawable.homework_green, R.drawable.homework_lakeblue};
    private List<HomeworkModel.List> mData;
    private Context mContext;
    private LinearLayout ll_nodata;

    public HomeworkClassesListAdapter(Context context, List<HomeworkModel.List> data, LinearLayout ll_nodata) {
        mContext = context;
        mData = data;
        this.ll_nodata = ll_nodata;
    }

    @Override
    public int getCount() {
        if (mData == null)
            return 0;
        else
            return mData.size();
    }

    @Override
    public Object getItem(int position) {
        if (mData == null || mData.size() == 0)
            return null;
        else
            return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (mData == null)
            return 0;
        else
            return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(UiUtils.getContext(), R.layout.item_homework_classes_list, null);
            holder = new ViewHolder();
            holder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
            holder.iv_round = (ImageView) convertView.findViewById(R.id.iv_round);
            holder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
            holder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 设置班级背景色
        if (position % 3 == 0) {
            holder.iv_image.setImageResource(classNameBgs[0]);
        } else if (position % 3 == 1) {
            holder.iv_image.setImageResource(classNameBgs[1]);
        } else {
            holder.iv_image.setImageResource(classNameBgs[2]);
        }

        HomeworkModel.List data = mData.get(position);
//        holder.tv_title.setText(data.className + "作业" + data.number);
        holder.tv_title.setText(data.title);
        holder.tv_time.setText(data.createTime);
        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });

        return convertView;
    }

    /**
     * 删除作业确认弹窗
     *
     * @param position
     */
    private void showDialog(final int position) {

        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE, true)
                .setTitleText("确认要删除该作业吗？")
                .setCancelText("取消")
                .setConfirmText("删除")
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
                        deleteHomework(position);
                    }
                })
                .show();

    }

    private void deleteHomework(final int position) {
        // $host/teacher/v1/homework/del?token=**&id=**
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        int id = mData.get(position).id;
//        String url = Static.URL_SERVER + "/teacher/v1/homework/del";
//        RequestParams mParams = new RequestParams(url);
//        mParams.addQueryStringParameter("token", token);
//        mParams.addQueryStringParameter("id", id+"");

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("id", id + "");

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/homework/del", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    UiUtils.showToast("删除成功");
                    mData.remove(mData.get(position));
                    notifyDataSetChanged();

                    if (mData.size() <= 0) {
                        ll_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ll_nodata.setVisibility(View.GONE);
                    }

                } else {
                    UiUtils.showToast(response.getData().optString("message"));
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                UiUtils.showToast(x.app().getResources().getString(R.string.net_error));
                if (throwable instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) throwable;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    LogUtil.d(responseCode + ":" + responseMsg);
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

    static class ViewHolder {
        TextView tv_title, tv_time;
        ImageView iv_image, iv_round, iv_delete;
    }

}
