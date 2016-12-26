package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.VoteOption;
import com.yl.teacher.util.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 投票列表选项
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class OptionAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<VoteOption> mList;

    public OptionAdapter(Context ctx,List<VoteOption> tmp){
        this.mList = tmp;
        this.context = ctx;
        this.mInflater = LayoutInflater.from(context);
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
            convertView = mInflater.inflate(R.layout.item_vote_option, parent, false);
            AutoUtils.autoSize(convertView);
        }

        TextView tvOptionName = ViewHolder.get(convertView, R.id.tvOptionName);
        tvOptionName.setText((position+1)+"."+mList.get(position).getTitle());

        return convertView;
    }


}
