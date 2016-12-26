package com.yl.teacher.chat.ui.activity;

import android.os.Bundle;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.chat.ui.fragment.ChatFragment;
import com.yl.teacher.util.AppUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.view.BaseActivity;

import org.xutils.common.util.LogUtil;

import static com.yl.teacher.global.MyApplication.avatarIsChanged;
import static com.yl.teacher.global.MyApplication.emmGroup;

/**
 * Created by $USER_NAME on 2016/9/6.
 * 讨论组界面
 */
public class ChatActivity extends BaseActivity {

    private ChatFragment chatFragment;

    @Override
    protected void onResume() {

        super.onResume();
        MobclickAgent.onPageStart("ChatActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChatActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_chatroom;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {

        if (avatarIsChanged) {
            avatarIsChanged = false;
            AppUtils.clearGlideCache(this);
        }

        // 获取环信群组emmGroup
        if (emmGroup == null) {
            UiUtils.showToast("网络异常，请稍候访问");
            finish();
        }

        // 教师端不接收群组消息推送
        try {
            EMClient.getInstance().groupManager().blockGroupMessage(emmGroup.emGroupId);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }

        LogUtil.i("Chat emGroupId: " + emmGroup.emGroupId);
        //new出EaseChatFragment或其子类的实例
        chatFragment = new ChatFragment();

        //传入参数
        Bundle args = new Bundle();
        args.putInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP);
        args.putString(EaseConstant.EXTRA_USER_ID, emmGroup.emGroupId);
        chatFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();
    }

    @Override
    protected void initListener() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
