package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.Quest;
import com.yl.teacher.util.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 请求列表
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class QuestAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<Quest> mList;

    public QuestAdapter(Context ctx,List<Quest> tmp){
        this.context = ctx;
        this.mInflater = LayoutInflater.from(context);
        this.mList = tmp;
    }
    public void setData(List<Quest> tmp){
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
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_quest, parent, false);
            AutoUtils.autoSize(convertView);
        }

        ImageView iv_image = ViewHolder.get(convertView, R.id.iv_image);
        ImageView iv_round = ViewHolder.get(convertView, R.id.iv_round);
        TextView tv_title = ViewHolder.get(convertView, R.id.tv_title);
        TextView tv_time = ViewHolder.get(convertView, R.id.tv_time);

        String status = mList.get(position).getStatus();
        if (status.equals("0")) {
            iv_round.setVisibility(View.VISIBLE);
        } else {
            iv_round.setVisibility(View.GONE);
        }

        if(position%3==0){
            iv_image.setImageResource(R.drawable.quest_green);
        }else if(position%3==1){
            iv_image.setImageResource(R.drawable.quest_blue);
        }else {
            iv_image.setImageResource(R.drawable.quest_lakeblue);
        }

        tv_title.setText(mList.get(position).getTitle());
        tv_time.setText(mList.get(position).getCreateTime());

        /*ImageView imgNew = ViewHolder.get(convertView, R.id.imgNew);
        ImageView imgReviewed = ViewHolder.get(convertView, R.id.imgReviewed);
        if("1".equals(mList.get(position).getReplyStatus())){
            imgReviewed.setVisibility(View.VISIBLE);
        }else{
            imgReviewed.setVisibility(View.GONE);
        }
        if("0".equals(mList.get(position).getStatus())){
            imgNew.setVisibility(View.VISIBLE);
        }else{
            imgNew.setVisibility(View.INVISIBLE);
        }
        AutoLinearLayout linearTitle = ViewHolder.get(convertView, R.id.linearTitle);
        TextView tvTitle = ViewHolder.get(convertView, R.id.tvTitle);
        if(position%3==0){
            linearTitle.setBackgroundResource(R.drawable.homework_blue);
        }else if(position%3==1){
            linearTitle.setBackgroundResource(R.drawable.homework_green);
        }else {
            linearTitle.setBackgroundResource(R.drawable.homework_lakeblue);
        }

        TextView tvClass = ViewHolder.get(convertView, R.id.tvClass);
        TextView tvRest = ViewHolder.get(convertView, R.id.tvRest);
        TextView tvExtra = ViewHolder.get(convertView, R.id.tvExtra);
        TextView tvStart = ViewHolder.get(convertView, R.id.tvStart);
        tvTitle.setText(mList.get(position).getTitle());
        tvClass.setText("所在班级："+mList.get(position).getClassName());
        tvRest.setText("请假事由："+mList.get(position).getContent());
        tvExtra.setText("备        注："+mList.get(position).getExtra());
        tvStart.setText("创建时间："+mList.get(position).getCreateTime());*/

        return convertView;
    }


}
