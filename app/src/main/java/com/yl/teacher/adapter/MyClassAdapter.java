package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.MyClass;
import com.yl.teacher.util.UiUtils;
import com.zhy.autolayout.AutoRelativeLayout;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.List;

/**
 * Created by yiban on 2016/5/5.
 */
public class MyClassAdapter extends BaseAdapter {

    private Context mContext;

    private List<MyClass> mList;

    public void setData(List<MyClass> tmp) {
        mList = tmp;
        notifyDataSetChanged();
    }

    public MyClassAdapter(Context context, List<MyClass> tmp) {
        this.mContext = context;
        this.mList = tmp;

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

        ViewHolderBody mHolderBody = null;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_navi_body, null);

            mHolderBody = new ViewHolderBody();
            mHolderBody.relaJoin = (AutoRelativeLayout) convertView.findViewById(R.id.relaJoin);
            mHolderBody.root_rl = (AutoRelativeLayout) convertView.findViewById(R.id.root_rl);
            mHolderBody.tvClasses = (TextView) convertView.findViewById(R.id.tvClasses);
            mHolderBody.tvClasseNo = (TextView) convertView.findViewById(R.id.tvClasseNo);
            mHolderBody.tvTeacher = (TextView) convertView.findViewById(R.id.tvTeacher);
            mHolderBody.tvMember = (TextView) convertView.findViewById(R.id.tvMember);
            mHolderBody.tvJoin = (TextView) convertView.findViewById(R.id.tvJoin);
            mHolderBody.iv_dot = (ImageView) convertView.findViewById(R.id.iv_dot);
            mHolderBody.iv_people = (ImageView) convertView.findViewById(R.id.iv_people);

            convertView.setTag(mHolderBody);
            AutoUtils.autoSize(convertView);
        } else {


            mHolderBody = (ViewHolderBody) convertView.getTag();
        }

        mHolderBody.tvClasses.setText(mList.get(position).getName());
        mHolderBody.tvClasseNo.setText(mList.get(position).getClassCode());
        mHolderBody.tvTeacher.setText(mList.get(position).getRealName());
        mHolderBody.tvMember.setText(mList.get(position).getMemberCount());
        if ("".equals(mList.get(position).getJoinInfo())) {
            mHolderBody.relaJoin.setVisibility(View.GONE);
        } else {
            mHolderBody.relaJoin.setVisibility(View.VISIBLE);
            mHolderBody.tvJoin.setText(mList.get(position).getJoinInfo());
        }

        if (mList.get(position).isRed) {
            mHolderBody.iv_dot.setVisibility(View.VISIBLE);
        } else {
            mHolderBody.iv_dot.setVisibility(View.GONE);
        }

        if (position % 3 == 0) {
            mHolderBody.iv_people.setBackgroundResource(R.drawable.img_class_orange);
            mHolderBody.root_rl.setBackgroundResource(R.drawable.img_class_bg_orange);
            mHolderBody.tvClasses.setTextColor(UiUtils.getColor(R.color.color_class_name_orange));
        } else if (position % 3 == 1) {
            mHolderBody.iv_people.setBackgroundResource(R.drawable.img_class_blue);
            mHolderBody.tvClasses.setTextColor(UiUtils.getColor(R.color.color_class_name_blue));
            mHolderBody.root_rl.setBackgroundResource(R.drawable.img_class_bg_bule);
        } else {
            mHolderBody.iv_people.setBackgroundResource(R.drawable.img_class_purple);
            mHolderBody.tvClasses.setTextColor(UiUtils.getColor(R.color.color_class_name_purple));
            mHolderBody.root_rl.setBackgroundResource(R.drawable.img_class_bg_purple);
        }

        return convertView;
    }

    private class ViewHolderBody {

        TextView tvClasses;

        TextView tvClasseNo;

        TextView tvTeacher;

        TextView tvMember;

        TextView tvJoin;

        AutoRelativeLayout relaJoin;

        AutoRelativeLayout root_rl;

        ImageView iv_dot;

        ImageView iv_people;

    }

}
