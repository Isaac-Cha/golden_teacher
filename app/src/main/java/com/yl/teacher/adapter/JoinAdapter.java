package com.yl.teacher.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Join;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.ViewHolder;
import com.yl.teacher.widget.CustomProgress;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 入班申请
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class JoinAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private  List<Join> mList;
    private Activity activity;

    public JoinAdapter(Context ctx, Activity act,List<Join> tmp){
        this.activity = act;
        this.context = ctx;
        this.mInflater = LayoutInflater.from(context);
        this.mList = tmp;
    }
    public void setData(List<Join> tmp){
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
            convertView = mInflater.inflate(R.layout.item_join, parent, false);
            AutoUtils.autoSize(convertView);
        }
        TextView tvName = ViewHolder.get(convertView, R.id.tvName);
        tvName.setText(mList.get(position).getName());
        TextView tvSure = ViewHolder.get(convertView, R.id.tvSure);
        TextView tvCancel = ViewHolder.get(convertView, R.id.tvCancel);
        tvSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermit(mList.get(position).getId(),"1", position);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermit(mList.get(position).getId(),"2", position);
            }
        });

        TextView tv_comment = ViewHolder.get(convertView, R.id.tv_comment);
        String comment = mList.get(position).getComment();
        if (!StringUtils.isEmpty(comment)) {
            tv_comment.setText(comment);
        } else {
            tv_comment.setText("申请加入班级");
        }

        return convertView;
    }
    /**
     * 审核家长的请求
     */
    private void checkPermit(String id, String status, final int position){
        CustomProgress.show(context, "加载中...", true, null);
        String token = MyApplication.getInstance().getShareUser().getString("token", "");

//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/joinApply/apply?token="+ token +"&join_id="+id+"&status="+status);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("join_id", id);
        mParams.put("status", status);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/joinApply/apply", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d(""+result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {

                            Action action = new Action();
                            action.setId(Static.EVENTBUS_TYPE_STATUS);
                            EventBus.getDefault().post(action);
                            mList.remove(position);
                            notifyDataSetChanged();
//                            activity.finish();
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();

                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            LogUtil.d(responseCode + ":" + responseMsg);
                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        } else { // 其他错误
                            // ...
                        }

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }

}
