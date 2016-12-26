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
import com.yl.teacher.model.BulletinModel;
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

public class BulletinListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BulletinModel.List> mData;
    private LinearLayout ll_nodata;

    public BulletinListAdapter(Context context, List<BulletinModel.List> data, LinearLayout ll_nodata) {
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
        if (mData == null)
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
            convertView = View.inflate(UiUtils.getContext(), R.layout.item_syllabus_list, null);
            holder = new ViewHolder();
            holder.image = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_image);
            holder.delete = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_delete);
            holder.title = (TextView) convertView.findViewById(R.id.tv_syllabus_item_title);
            holder.time = (TextView) convertView.findViewById(R.id.tv_syllabus_item_time);
            holder.round = (ImageView) convertView.findViewById(R.id.iv_syllabus_item_round);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int type = mData.get(position).type;
        int readStatus = mData.get(position).readStatus;
        holder.delete.setImageResource(R.drawable.dustbin);
        holder.title.setText(mData.get(position).title + "");
        holder.time.setText(mData.get(position).createTime + "");
        if (type == 1) {
            // 通知
            holder.image.setImageResource(R.drawable.notic);
        } else if (type == 2) {
            // 活动
            holder.image.setImageResource(R.drawable.action);
        } else {
            holder.image.setImageResource(R.drawable.notic);
        }

        /*if (readStatus == 0) {
            // 未读
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
                .setTitleText("确认要删除该公告吗？")
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
                        deleteBulletin(itemPos);
                    }
                })
                .show();

    }

    /**
     * 删除公告
     */
    private void deleteBulletin(final int itemPos) {
        final int announId = mData.get(itemPos).id;
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        ;
//        String url = Static.URL_SERVER + "/teacher/v1/announcement/del-announ?token=" + token + "&Announ_id=" + announId;
//        RequestParams params = new RequestParams(url);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Announ_id", announId + "");

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/announcement/del-announ", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    UiUtils.showToast("删除成功");
                    LogUtil.i("announId" + announId + "，已删除");
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
                UiUtils.showToast("已取消");
            }

            @Override
            public void onFinished() {

            }
        });

    }
}
