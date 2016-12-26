package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.Vote;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.util.ViewHolder;
import com.yl.teacher.xalertdialog.SweetAlertDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投票列表
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class VoteAdapter extends BaseAdapter {

    private final String VOTEPUBLIC_YES = "0";
    private final String VOTEPUBLIC_NO = "1";

    private Context context;
    private LayoutInflater mInflater;
    private LinearLayout ll_nodata;
    private List<Vote> mList;

    public VoteAdapter(Context ctx,List<Vote> tmp, LinearLayout ll_nodata){
        this.mList = tmp;
        this.context = ctx;
        this.mInflater = LayoutInflater.from(context);
        this.ll_nodata = ll_nodata;
    }

    public void setData(List<Vote> tmp){
        mList = tmp;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_vote_list, parent, false);
//            AutoUtils.autoSize(convertView);
        }

        ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);
//        ImageView iv_round = ViewHolder.get(convertView, R.id.iv_round);
        ImageView iv_time = ViewHolder.get(convertView, R.id.iv_time); // 时间图标
        ImageView iv_no_public = ViewHolder.get(convertView, R.id.iv_no_public); // 不公开
        ImageView iv_delete = ViewHolder.get(convertView, R.id.iv_delete);
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);
        TextView tv_status = ViewHolder.get(convertView, R.id.tv_status);

        iv_image.setImageResource(R.drawable.img_vote);
        tv_title.setText(mList.get(position).getTitle());
        tv_time.setText(mList.get(position).getCreateTime());
        String status = mList.get(position).getStatus();
        switch (status) {
            case "0": // 未开始
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("未开始");
                tv_status.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_gray));
                iv_time.setBackgroundResource(R.drawable.img_vote_time_end);
                tv_time.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_gray));
                break;

            case "1": // 进行中
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("进行中");
                tv_status.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_pink));
                iv_time.setBackgroundResource(R.drawable.img_vote_time_start);
                tv_time.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_pink));
                break;

            case "2": // 已结束
                tv_status.setVisibility(View.VISIBLE);
                tv_status.setText("已结束");
                tv_status.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_gray));
                iv_time.setBackgroundResource(R.drawable.img_vote_time_end);
                tv_time.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_gray));
                break;

            default:
                tv_status.setVisibility(View.GONE);
                iv_time.setBackgroundResource(R.drawable.img_vote_time_end);
                tv_time.setTextColor(UiUtils.getColor(R.color.color_vote_time_text_gray));
                break;
        }

        iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(position);
            }
        });

        // 投票是否公开
        if (VOTEPUBLIC_NO.equals(mList.get(position).getAnonymous())) {
            // 显示“不公开”
            iv_no_public.setVisibility(View.VISIBLE);
        } else {
            // 隐藏“不公开”
            iv_no_public.setVisibility(View.GONE);
        }

        return convertView;
    }

    private void showDialog(final int position) {

        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE, true)
                .setTitleText("确认要删除该投票吗？")
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
                        deleteVote(position);
                    }
                })
                .show();

    }

    private void deleteVote(final int position) {
        // /teacher/v1/vote/del?token=**&id=**
        String id = mList.get(position).getId();
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
//        String url = Static.URL_SERVER + "/teacher/v1/vote/del";
//        RequestParams mParams = new RequestParams(url);
//        mParams.addQueryStringParameter("token", token);
//        mParams.addQueryStringParameter("id", id);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("id", id);

        x.http().post(HttpUtils.getRequestParams("/teacher/v1/vote/del", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Response response = CommonUtil.checkResponse(s);
                if (response.isStatus()) {
                    UiUtils.showToast("删除成功");
                    mList.remove(mList.get(position));
                    notifyDataSetChanged();

                    if (mList.size() <= 0) {
                        ll_nodata.setVisibility(View.VISIBLE);
                    } else {
                        ll_nodata.setVisibility(View.GONE);
                    }

                } else  {
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
