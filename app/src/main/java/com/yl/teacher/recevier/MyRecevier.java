package com.yl.teacher.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.github.nkzawa.socketio.client.Socket;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.db.Push;
import com.yl.teacher.model.CampusDelDot;
import com.yl.teacher.model.JPushNotifyModel;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.view.BulletinDetailActivity;
import com.yl.teacher.view.ClassCircleActivity;
import com.yl.teacher.view.HomeworkDetailActivity;
import com.yl.teacher.view.JoinActivity;
import com.yl.teacher.view.ReviewedActivity;
import com.yl.teacher.view.StartActivity;
import com.yl.teacher.view.StudentsActivity;
import com.yl.teacher.view.SyllabusActivity;
import com.yl.teacher.view.VoteDetaileActivity;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

public class MyRecevier extends BroadcastReceiver {

    private DbManager db;
    private Push mPush;
    private Socket mSocket;

    @Override
    public void onReceive(Context context, Intent intent) {

        // 获取DbManager
        db = x.getDb(MyApplication.daoConfig);
        mSocket = MyApplication.getInstance().getSocket();

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) { // 接收到通知

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) { // 通过通知栏打开标题

            String sJson = bundle.getString(JPushInterface.EXTRA_EXTRA);
            LogUtil.e("sJson: "+ sJson);
            JPushNotifyModel model = GsonUtils.fromJson(sJson, JPushNotifyModel.class);

            try {
                saveDb(model);
            } catch (DbException e) {
                e.printStackTrace();
            }

            Intent mIntent;
            switch (model.type) {

                case Static.KEY_CLASSSCHEDULE: // 课表
                    campusDelDot(Static.KEY_CLASSSCHEDULE);
                    mIntent = new Intent(context, SyllabusActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_STUDENTLOG: // 学生点滴
                    campusDelDot(Static.KEY_STUDENTLOG);
                    mIntent = new Intent(context, StudentsActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_CLASSMEMEBERREQUEST: // 请求
                    campusDelDot(Static.KEY_CLASSMEMEBERREQUEST);
                    mIntent = new Intent(context, ReviewedActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_ANNOUNCEMENT: // 公告
                    campusDelDot(Static.KEY_ANNOUNCEMENT);
                    mIntent = new Intent(context, BulletinDetailActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_VOTE: // 投票
                    campusDelDot(Static.KEY_VOTE);
                    mIntent = new Intent(context, VoteDetaileActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_HOMEWORK: // 作业
                    campusDelDot(Static.KEY_HOMEWORK);
                    mIntent = new Intent(context, HomeworkDetailActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_CLASSZONE: // 班级空间
                    campusDelDot(Static.KEY_CLASSZONE);
                    mIntent = new Intent(context, ClassCircleActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                case Static.KEY_PERMIT: // 待审批
                    campusDelDot(Static.KEY_PERMIT);
                    mIntent = new Intent(context, JoinActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

                default:
                    mIntent = new Intent(context, StartActivity.class);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(mIntent);
                    break;

            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
//            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }

    }

    private void saveDb(JPushNotifyModel model) throws DbException {

        mPush = new Push();
        mPush.type = model.type;
        mPush.tid = model.tid;
        mPush.classId = model.classId;
        db.delete(Push.class);
        db.saveBindingId(mPush);

    }

    private void campusDelDot(int moduleId) {
        CampusDelDot campusDelDot = new CampusDelDot();
        campusDelDot.classId = MyApplication.getInstance().getClassId();
        campusDelDot.token = MyApplication.getInstance().getShareUser().getString("token", "");
        campusDelDot.moduleId = moduleId;
        mSocket.emit(Static.SOCKET_DEL_DOT, GsonUtils.toJson(campusDelDot));
        LogUtil.e("GsonUtils.toJson(campusDelDot): " + GsonUtils.toJson(campusDelDot));
    }

}
