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
import com.yl.teacher.model.Reply;
import com.yl.teacher.util.Static;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by yiban on 2016/5/5.
 */
public class ReplyAdapter extends BaseAdapter{

    private ImageOptions imageOptions;

    private final static int TYPE_HEAD = 0;

    private final static int TYPE_BODY = 1;

    private Context mContext;

    private List<Reply> mList;

    private Quest quest;

    public void setData(List<Reply> tmp){
        mList = tmp;
        notifyDataSetChanged();
    }
    public ReplyAdapter(Context context, List<Reply> tmp,Quest tmpQuest){
        imageOptions = new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setCircular(true)
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.img_comment_df)
                .setFailureDrawableId(R.drawable.img_comment_df)
                .build();
        this.mContext = context;
        this.mList = tmp;
        this.quest = tmpQuest;
    }

    @Override
    public int getItemViewType(int position) {

        return position==0 ? TYPE_HEAD : TYPE_BODY;
    }

    @Override
    public int getViewTypeCount() {

        return TYPE_BODY+1;
    }

    @Override
    public int getCount() {
        return mList.size()+1;
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
        ViewHolderHead mHolderHead = null;
        ViewHolderBody mHolderBody = null;
        int type = getItemViewType(position);
        if(convertView==null){

            if(type==TYPE_HEAD){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reply_head, null);
                mHolderHead = new ViewHolderHead();
                mHolderHead.imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
                mHolderHead.imgReviewed = (ImageView) convertView.findViewById(R.id.imgReviewed);

                mHolderHead.tvParent = (TextView) convertView.findViewById(R.id.tvParent);
                mHolderHead.tvTag = (TextView) convertView.findViewById(R.id.tvTag);
                mHolderHead.tvNo = (TextView) convertView.findViewById(R.id.tvNo);
                mHolderHead.tvClass = (TextView) convertView.findViewById(R.id.tvClass);
                mHolderHead.tvRest = (TextView) convertView.findViewById(R.id.tvRest);
                mHolderHead.tvStart = (TextView) convertView.findViewById(R.id.tvStart);

                convertView.setTag(mHolderHead);
            }else if(type==TYPE_BODY){
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reply_body, null);
                mHolderBody = new ViewHolderBody();
                mHolderBody.imgAuthor = (ImageView) convertView.findViewById(R.id.imgAuthor);
                mHolderBody.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
                mHolderBody.tvDate = (TextView) convertView.findViewById(R.id.tvDate);

                convertView.setTag(mHolderBody);
            }
            AutoUtils.autoSize(convertView);
        }else {

            if(type==TYPE_HEAD){
                mHolderHead = (ViewHolderHead)convertView.getTag();
            }else if(type==TYPE_BODY){
                mHolderBody = (ViewHolderBody)convertView.getTag();
            }

        }
        if(type==TYPE_HEAD){
            x.image().bind(mHolderHead.imgIcon, Static.IMAGE_IP + "/avatar/" + quest.getUserId(), imageOptions);
            if("1".equals(quest.getReplyStatus())){
                mHolderHead.imgReviewed.setVisibility(View.VISIBLE);
                mHolderHead.tvTag.setTextColor(mContext.getResources().getColor(R.color.note_yes));
                mHolderHead.tvTag.setText("通过审核");
            }else{
                mHolderHead.imgReviewed.setVisibility(View.GONE);
                mHolderHead.tvTag.setTextColor(mContext.getResources().getColor(R.color.note_no));
                mHolderHead.tvTag.setText("等待我的审核");
            }
            mHolderHead.tvParent.setText(quest.getStudentName()+"的家长");
            mHolderHead.tvNo.setText("编　　号："+quest.getRequestCode());
            mHolderHead.tvClass.setText("所在班级："+quest.getClassName());
            mHolderHead.tvRest.setText("请假事由："+quest.getContent());
            mHolderHead.tvStart.setText("开始时间："+quest.getCreateTime());
        }else if(type==TYPE_BODY){
            x.image().bind(mHolderBody.imgAuthor, Static.IMAGE_IP + "/avatar/" + mList.get(position-1).getUserId(), imageOptions);
            mHolderBody.tvContent.setText(mList.get(position-1).getContent());
            mHolderBody.tvDate.setText(mList.get(position-1).getCreateTime());

        }

        return convertView;
    }

    private class ViewHolderHead{
        ImageView imgIcon;
        ImageView imgReviewed;
        TextView tvParent;

        TextView tvTag;

        TextView tvNo;

        TextView tvClass;

        TextView tvRest;

        TextView tvStart;

    }

    private class ViewHolderBody{

        ImageView imgAuthor;

        TextView tvContent;

        TextView tvDate;

    }

}
