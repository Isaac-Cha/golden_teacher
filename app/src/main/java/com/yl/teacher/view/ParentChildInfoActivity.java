package com.yl.teacher.view;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.yl.teacher.R;
import com.yl.teacher.model.Contact;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * 点击联系人列表显示学生信息
 */
public class ParentChildInfoActivity extends BaseActivity {

    private ImageView iv_header;
    private TextView tv_name;
    private TextView tv_sex;
    private TextView tv_age;
    private TextView tv_phone;
    private CustomTitleBar custom_titlebar;
    private Contact contact;

    @Override
    protected int loadLayout() {
        return R.layout.activity_parentchild;
    }

    @Override
    protected void initViews() {
        custom_titlebar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        iv_header = (ImageView) findViewById(R.id.iv_header);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
    }

    @Override
    protected void initData() {
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        Bundle mBundle = getIntent().getExtras();
        contact = (Contact) mBundle.getSerializable("contact");
        if (contact == null) {
            UiUtils.showToast("您访问的联系人不存在");
            finish();
        }
        custom_titlebar.setCenterTitle(contact.getStudentName());
        custom_titlebar.displayBackBtn(true);

        tv_name.setText(contact.getStudentName());

        switch (contact.getSex()) {
            case 1:
                tv_sex.setText("女");
                break;
            case 2:
                tv_sex.setText("男");
                break;
            default:
                tv_sex.setText("");
                break;
        }

        tv_age.setText(contact.getAge());

        tv_phone.setText(contact.getPhone());

        String imageUrl = Static.IMAGE_IP + "/avatar/" + contact.getUserId() + "/140_162";
        x.image().bind(iv_header, imageUrl, new ImageOptions.Builder()
                //.setSize(DensityUtil.dip2px(90), DensityUtil.dip2px(90))
                //.setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                //.setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                .setPlaceholderScaleType(ImageView.ScaleType.CENTER_CROP)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.img_person)
                .setFailureDrawableId(R.drawable.img_person)
                .build());
    }

    @Override
    protected void initListener() {
        custom_titlebar.setActivity(this);
    }

}
