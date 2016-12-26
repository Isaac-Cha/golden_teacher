package com.yl.teacher.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.util.UiUtils;

/**
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class HomeworkListAdapter extends BaseAdapter {

    String[] strs = {"1", "2", "3", "你好", "大家好"};

    @Override
    public int getCount() {
        return strs.length;
    }

    @Override
    public Object getItem(int position) {
        return strs[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(UiUtils.getContext(), R.layout.item_homework_list, null);
            holder = new ViewHolder();
            holder.homework = (TextView) convertView.findViewById(R.id.tv_homework);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.homework.setText(strs[position]);

        return convertView;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    static class ViewHolder {
        TextView homework;
    }

}
