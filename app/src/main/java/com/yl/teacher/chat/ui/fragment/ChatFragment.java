package com.yl.teacher.chat.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.ui.EaseChatFragment.EaseChatFragmentHelper;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.R;
import com.yl.teacher.chat.ChatHelper;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.view.ImageDetailsActivity;
import com.yl.teacher.widget.SystemBarTintManager;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.yl.teacher.global.MyApplication.emmGroup;

/**
 * Created by $USER_NAME on 2016/9/18.
 * 聊天界面
 */
public class ChatFragment extends EaseChatFragment implements EaseChatFragmentHelper {

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("ChatFragment");
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("ChatFragment");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        CommonUtil.systemBarTint(new SystemBarTintManager(getActivity()), R.color.navi_user);

        fragmentArgs = getArguments();
        // check if single chat or group chat
        chatType = fragmentArgs.getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        toChatUsername = fragmentArgs.getString(EaseConstant.EXTRA_USER_ID);

        if (emmGroup == null) {
            UiUtils.showToast("网络异常，请稍候访问");
            getActivity().finish();
        }

        // 扩展属性的一些监听
        setChatFragmentListener(this);

        super.onActivityCreated(savedInstanceState);

        // 设置标题
        titleBar.setTitle(MyApplication.getInstance().classesName);

    }

    /**
     * 消息扩展属性
     *
     * @param message
     */
    @Override
    public void onSetMessageAttributes(EMMessage message) {

        // 通过扩展属性，将userPic和nickname发送出去。
        String userPic = emmGroup.avatar;
        if (!StringUtils.isEmpty(userPic)) {
            message.setAttribute("userPic", userPic);
            LogUtil.i("userPic: " + userPic);
        }

        String nickname = emmGroup.nickname;
        if (!StringUtils.isEmpty(nickname)) {
            message.setAttribute("nickname", nickname);
            LogUtil.i("nickname: " + nickname);
        }

    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {
        // 头像被点击
        EaseUser userInfo = ChatHelper.getInstance().getUserInfo(username);

        LogUtil.e("hxUsername: " + username);
        LogUtil.e("avatar: " + userInfo.getAvatar());

        ArrayList<String> imageUrls = new ArrayList<>();
        imageUrls.add(userInfo.getAvatar());

        Intent mIntent = new Intent(getActivity(), ImageDetailsActivity.class);
        mIntent.putExtra(ImageDetailsActivity.INTENT_IMAGE_URLS, imageUrls);
        mIntent.putExtra(ImageDetailsActivity.INTENT_IMAGE_FROM, ImageDetailsActivity.INTENT_IMAGE_FROM);

        startActivity(mIntent);
        getActivity().overridePendingTransition(R.anim.activity_in_scale, R.anim.activity_out_scale);
    }

    @Override
    public void onAvatarLongClick(String username) {

    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return null;
    }


    @Override
    public void addIMRedDot() {
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String classId = MyApplication.getInstance().classId;
        LogUtil.i("token: " + token);
        LogUtil.i("classId: " + classId);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        x.http().post(HttpUtils.getRequestParams("/common/v1/easemobGroup/group-add-dot", mParams), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                LogUtil.i(s + "IM小红点添加事件已发出");
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.e(throwable.toString());
            }

            @Override
            public void onCancelled(CancelledException e) {
                LogUtil.e(e.toString());
            }

            @Override
            public void onFinished() {

            }
        });

    }

}
