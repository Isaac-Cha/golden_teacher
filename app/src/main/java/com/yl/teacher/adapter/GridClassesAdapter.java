package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.MenuList;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.ViewHolder;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * 班级管理item
 * Created by GA_PC_Sample on 2016/5/12.
 */
public class GridClassesAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mInflater;
    private List<MenuList> data;
    private List<String> moduleIds;

    public GridClassesAdapter(Context ctx, List<MenuList> tmp, List<String> moduleIds) {
        this.moduleIds = moduleIds;
        this.context = ctx;
        this.mInflater = LayoutInflater.from(context);
        this.data = tmp;
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = mInflater.inflate(R.layout.item_grid_classes, parent, false);
            AutoUtils.autoSize(convertView);
        }
        ImageView imgIcon = ViewHolder.get(convertView, R.id.imgIcon);
        ImageView iv_dot = ViewHolder.get(convertView, R.id.iv_dot);
        TextView tvName = ViewHolder.get(convertView, R.id.tvName);
        tvName.setText(data.get(position).getName());

        switch (data.get(position).getTag()) {

            case Static.TAG_SCHEDULE:
                imgIcon.setImageResource(R.drawable.img_detaile_scedul);
                showRed(Static.KEY_CLASSSCHEDULE + "", iv_dot);
                break;

            case Static.TAG_STUDENT:
                showRed(Static.KEY_STUDENTLOG + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_camera);
                break;

            case Static.TAG_QUEST:
                showRed(Static.KEY_CLASSMEMEBERREQUEST + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_quest);
                break;

            case Static.TAG_ANNOUNCE:
                showRed(Static.KEY_ANNOUNCEMENT + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_broad);
                break;

            case Static.TAG_VOTE:
                showRed(Static.KEY_VOTE + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_vote);
                break;

            case Static.TAG_HOMEWORK:
                showRed(Static.KEY_HOMEWORK + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_hwork);
                break;

            case Static.TAG_ZONE:
                showRed(Static.KEY_CLASSZONE + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_detaile_qzone);
                break;

            case Static.TAG_IM:
                showRed(Static.KEY_IM + "", iv_dot);
                imgIcon.setImageResource(R.drawable.img_chat);
                break;

            default:
                break;

        }

        return convertView;
    }

    private void showRed(String moduleId, ImageView iv_dot) {
        if (moduleIds != null && moduleIds.size() > 0) {
            if (moduleIds.contains(moduleId)) {
                iv_dot.setVisibility(View.VISIBLE);
            } else {
                iv_dot.setVisibility(View.GONE);
            }
        } else {
            iv_dot.setVisibility(View.GONE);
        }
    }

}
