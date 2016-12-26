package com.yl.teacher.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.VoteOption;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.List;

/**
 * 添加投票
 * Created by yiban on 2016/5/5.
 */
public class AddVoteAdapter extends BaseAdapter {

    private ImageOptions imageOptions;

    private Context mContext;

    private List<VoteOption> mData;


    public AddVoteAdapter(Context context, List<VoteOption> data) {
        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.btn_add)
                .setFailureDrawableId(R.drawable.btn_add)
                .build();

        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
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

            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_addvote_body, null);
            mHolderBody = new ViewHolderBody();
            mHolderBody.tvHead = (TextView) convertView.findViewById(R.id.tvHead);
            mHolderBody.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            mHolderBody.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
            convertView.setTag(mHolderBody);

            AutoUtils.autoSize(convertView);
        } else {

            mHolderBody = (ViewHolderBody) convertView.getTag();

        }

        mHolderBody.tvHead.setText("标题" + (position + 1) + "：");
        mHolderBody.tvContent.setText(mData.get(position).getTitle());
        String imageUri = new File(mData.get(position).getImage()).toURI().toString();
        x.image().bind(mHolderBody.imgIcon, imageUri, imageOptions);

        return convertView;
    }


    private class ViewHolderBody {

        TextView tvHead;
        TextView tvContent;
        ImageView imgIcon;
    }


}
