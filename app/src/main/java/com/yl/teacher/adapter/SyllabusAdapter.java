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
import com.yl.teacher.model.Response;
import com.yl.teacher.model.SyllabusModel;
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
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class SyllabusAdapter extends BaseAdapter {

    private List<SyllabusModel.List> mData;
    private Context mContext;
    private final int READSTATUS_UNREAD = 0;
    private final int READSTATUS_READ = 1;
    private LinearLayout ll_nodata;

    public SyllabusAdapter(Context context, List<SyllabusModel.List> data, LinearLayout ll_nodata) {
        mContext = context;
        mData = data;
        this.ll_nodata = ll_nodata;
    }

    @Override
    public int getCount() {
        if (mData != null)
            return mData.size();
        else
            return 0;

    }

    @Override
    public Object getItem(int position) {
        if (mData != null)
            return mData.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        if (mData != null)
            return position;
        else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(UiUtils.getContext(), R.layout.item_syllabus_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_image);
            holder.round = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_round);
            holder.delete = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_delete);
            holder.title = (TextView) convertView.findViewById(R.id.tv_syllabus_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.tv_syllabus_item_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SyllabusModel.List data = mData.get(position);
        holder.image.setImageResource(R.drawable.notic);
        holder.delete.setImageResource(R.drawable.dustbin);
        holder.title.setText(data.title);
        holder.time.setText(data.createTime);
        // 阅读状态
        /*if (data.readStatus == READSTATUS_UNREAD) {
            holder.round.setVisibility(View.VISIBLE);
        } else {
            holder.round.setVisibility(View.GONE);
        }*/

        final int itemPos = position;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPayDialog(itemPos);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView image, delete, round;
        TextView title, time;
    }

    private void showPayDialog(final int itemPos) {

        new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE, true)
                .setTitleText("确认要删除该课程表吗？")
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
                        deleteSyllabus(itemPos);
                    }
                })
                .show();

    }

    /**
     * 删除课程表
     */
    private void deleteSyllabus(final int itemPos) {
        int id = mData.get(itemPos).id;
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        // 将删除信息传递给服务器
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classSchedule/del?token=" + token + "&id=" + id);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("id", id + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/classSchedule/del", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    UiUtils.showToast("删除成功");
                    mData.remove(mData.get(itemPos));
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
                UiUtils.showToast("已取消");
            }

            @Override
            public void onFinished() {
            }
        });
    }

}
