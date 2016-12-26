package com.yl.teacher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.yl.teacher.R;
import com.yl.teacher.util.Static;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

public class CircleAddGridAdapter extends BaseAdapter {
    private List<PhotoInfo> mDataList = new ArrayList<>();
    private Context mContext;
    private ImageOptions mImageOptions;
    private int originalSize;
    private int newSize;

    public CircleAddGridAdapter(Context context, List<PhotoInfo> dataList, ImageOptions imageOptions) {
        this.mContext = context;
        this.mDataList = dataList;
        this.mImageOptions = imageOptions;
        this.originalSize = dataList.size();
    }

    public int getCount() {
        // 多返回一个用于展示添加图标
        if (mDataList == null) {
            return 1;
        } else if (mDataList.size() == Static.MAX_IMAGE_SIZE) {
            return Static.MAX_IMAGE_SIZE;
        } else {
            return mDataList.size() + 1;
        }
    }

    public Object getItem(int position) {
        if (mDataList != null
                && mDataList.size() == Static.MAX_IMAGE_SIZE) {
            return mDataList.get(position);
        } else if (mDataList == null || position - 1 < 0
                || position > mDataList.size()) {
            return null;
        } else {
            return mDataList.get(position - 1);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, ViewGroup parent) {
        //所有Item展示不满一页，就不进行ViewHolder重用了，避免了一个拍照以后添加图片按钮被覆盖的奇怪问题
        convertView = View.inflate(mContext, R.layout.item_grid_circle_add, null);
        ImageView imageIv = (ImageView) convertView.findViewById(R.id.iv_item_grid_image);
        ImageView ivRemove = (ImageView) convertView.findViewById(R.id.iv_item_grid_remove);

        if (isShowAddItem(position)) {
            imageIv.setImageResource(R.drawable.btn_add);
//            imageIv.setBackgroundResource(R.color.bg_gray);
            ivRemove.setVisibility(View.GONE);
        } else {
            x.image().bind(imageIv, new File(mDataList.get(position).getPhotoPath()).toURI().toString(), mImageOptions);
            ivRemove.setVisibility(View.VISIBLE);
        }

        ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataList.remove(mDataList.get(position));
                CircleAddGridAdapter.this.notifyDataSetChanged();
            }
        });
        newSize = mDataList.size();

        return convertView;
    }

    private boolean isShowAddItem(int position) {
        int size = mDataList == null ? 0 : mDataList.size();
        return position == size;
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (mDataList != null) {
            mDataList.clear();
        }
    }

}
