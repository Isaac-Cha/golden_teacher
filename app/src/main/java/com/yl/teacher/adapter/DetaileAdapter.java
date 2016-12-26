package com.yl.teacher.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;
import com.yl.teacher.R;
import com.yl.teacher.chat.ui.activity.ChatActivity;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.CampusDelDot;
import com.yl.teacher.model.Manager;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.view.BulletinActivity;
import com.yl.teacher.view.ClassCircleActivity;
import com.yl.teacher.view.HomeworkClassesListActivity;
import com.yl.teacher.view.InviteActivity;
import com.yl.teacher.view.JoinActivity;
import com.yl.teacher.view.ParentActivity;
import com.yl.teacher.view.QuestActivity;
import com.yl.teacher.view.StudentsActivity;
import com.yl.teacher.view.SyllabusActivity;
import com.yl.teacher.view.VoteActivity;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.NoScrollGridView;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

import static com.yl.teacher.global.MyApplication.emmGroup;

/**
 * 班级管理
 * Created by yiban on 2016/5/5.
 */
public class DetaileAdapter extends BaseAdapter {

    private final static int TYPE_HEAD = 0;

    private final static int TYPE_BODY = 1;

    private Context mContext;

    private Manager manager;

    private AlertDialog mUploadDialog;

    private View dialogView;

    private EditText etGrade;

    private TextView tvGrade;

    private Activity activity;
    private final DbManager db;
    private Socket mSocket;

    public DetaileAdapter(Context context, Manager tmp, Activity act, Socket socket) {
        this.activity = act;
        this.mContext = context;
        this.manager = tmp;
        db = x.getDb(MyApplication.daoConfig);
        mSocket = socket;
    }

    @Override
    public int getItemViewType(int position) {

        return position == 0 ? TYPE_HEAD : TYPE_BODY;
    }


    @Override
    public int getViewTypeCount() {

        return TYPE_BODY + 1;
    }

    @Override
    public int getCount() {
        return 2;
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
        if (convertView == null) {

            if (type == TYPE_HEAD) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_detaile_head, null);
                mHolderHead = new ViewHolderHead();
                mHolderHead.tvGrade = (TextView) convertView.findViewById(R.id.tvGrade);
                tvGrade = mHolderHead.tvGrade;
                mHolderHead.linearEdit = (AutoLinearLayout) convertView.findViewById(R.id.linearEdit);
                //mHolderHead.imgEdit = (ImageView) convertView.findViewById(R.id.imgEdit);
                mHolderHead.linearParent = (AutoLinearLayout) convertView.findViewById(R.id.linearParent);
                mHolderHead.linearPermit = (AutoLinearLayout) convertView.findViewById(R.id.linearPermit);
                mHolderHead.linearInvite = (AutoLinearLayout) convertView.findViewById(R.id.linearInvite);
                mHolderHead.tvParent = (TextView) convertView.findViewById(R.id.tvParent);
                mHolderHead.tvPermit = (TextView) convertView.findViewById(R.id.tvPermit);
                mHolderHead.iv_dot = (ImageView) convertView.findViewById(R.id.iv_dot);
//                mHolderHead.arl_red = (AutoRelativeLayout) convertView.findViewById(R.id.arl_red);
//                mHolderHead.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
                convertView.setTag(mHolderHead);
            } else if (type == TYPE_BODY) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_detaile_body, null);
                mHolderBody = new ViewHolderBody();
                mHolderBody.gridClasses = (NoScrollGridView) convertView.findViewById(R.id.gridClasses);
                convertView.setTag(mHolderBody);
            }
            AutoUtils.autoSize(convertView);
        } else {

            if (type == TYPE_HEAD) {
                mHolderHead = (ViewHolderHead) convertView.getTag();
            } else if (type == TYPE_BODY) {
                mHolderBody = (ViewHolderBody) convertView.getTag();
            }

        }
        if (type == TYPE_HEAD) {
            mHolderHead.tvGrade.setText(manager.getClassName());
            mHolderHead.tvParent.setText("家长:" + manager.getMemberCount());
            mHolderHead.tvPermit.setText("待审批:" + manager.getWaitApply());

            if (manager.isPermit) {
                mHolderHead.iv_dot.setVisibility(View.VISIBLE);
            } else {
                mHolderHead.iv_dot.setVisibility(View.GONE);
            }

            /*if (manager.getWaitApply() > 0) {
                mHolderHead.arl_red.setVisibility(View.VISIBLE);
            } else {
                mHolderHead.arl_red.setVisibility(View.GONE);
            }*/

            mHolderHead.linearEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                }
            });

            mHolderHead.linearParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ParentActivity.class);
                    mContext.startActivity(intent);
                }
            });
            mHolderHead.linearPermit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    campusDelDot(Static.KEY_PERMIT);

                    Intent intent = new Intent(mContext, JoinActivity.class);
                    mContext.startActivity(intent);
                }
            });
            mHolderHead.linearInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, InviteActivity.class);
                    mContext.startActivity(intent);
                }
            });

        } else if (type == TYPE_BODY) {
            if (manager.getMenu() != null && manager.getMenu().size() > 0) {

                GridClassesAdapter gridClassesAdapter = new GridClassesAdapter(mContext, manager.getMenu(), manager.moduleIds);

                mHolderBody.gridClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        switch (manager.getMenu().get(position).getTag()) {

                            case Static.TAG_SCHEDULE:
                                campusDelDot(Static.KEY_CLASSSCHEDULE);
                                Intent intentSchenule = new Intent(mContext, SyllabusActivity.class);
                                mContext.startActivity(intentSchenule);
                                break;

                            case Static.TAG_STUDENT:
                                campusDelDot(Static.KEY_STUDENTLOG);
                                Intent intentStudent = new Intent(mContext, StudentsActivity.class);
                                mContext.startActivity(intentStudent);
                                break;

                            case Static.TAG_QUEST:
                                campusDelDot(Static.KEY_CLASSMEMEBERREQUEST);
                                Intent intentQuest = new Intent(mContext, QuestActivity.class);
                                mContext.startActivity(intentQuest);
                                break;

                            case Static.TAG_ANNOUNCE:
                                campusDelDot(Static.KEY_ANNOUNCEMENT);
                                Intent intentAnnounce = new Intent(mContext, BulletinActivity.class);
                                mContext.startActivity(intentAnnounce);
                                break;

                            case Static.TAG_VOTE:
                                campusDelDot(Static.KEY_VOTE);
                                Intent intentVote = new Intent(mContext, VoteActivity.class);
                                mContext.startActivity(intentVote);
                                break;

                            case Static.TAG_HOMEWORK:
                                campusDelDot(Static.KEY_HOMEWORK);
                                Intent intentHome = new Intent(mContext, HomeworkClassesListActivity.class);
                                mContext.startActivity(intentHome);
                                break;

                            case Static.TAG_ZONE:
                                campusDelDot(Static.KEY_CLASSZONE);
                                Intent intentZone = new Intent(mContext, ClassCircleActivity.class);
                                mContext.startActivity(intentZone);
                                break;

                            case Static.TAG_IM:
                                campusDelDot(Static.KEY_IM);
                                if (emmGroup == null) {
                                    UiUtils.showToast("网络异常，请稍候访问");
                                } else {
                                    Intent intentIM = new Intent(mContext, ChatActivity.class);
                                    mContext.startActivity(intentIM);
                                }
                                break;

                            default:
                                break;

                        }

                    }
                });
                mHolderBody.gridClasses.setAdapter(gridClassesAdapter);
            }

        }

        return convertView;
    }

    private class ViewHolderHead {

        TextView tvGrade;
        AutoLinearLayout linearEdit;
        //ImageView imgEdit;
        AutoLinearLayout linearParent;
        AutoLinearLayout linearPermit;
        AutoLinearLayout linearInvite;
        TextView tvParent;
        TextView tvPermit;
        //        AutoRelativeLayout arl_red;
//        TextView tv_count;
        ImageView iv_dot;
    }

    private class ViewHolderBody {

        NoScrollGridView gridClasses;

    }

    /**
     * isOpen = true  显示
     * isOpen = false 隐藏
     * 当v为空时可以调用getWindow().peekDecorView()获取整个屏幕的View
     */
    public static void handlerKeyboard(View v, boolean isOpen) {
        InputMethodManager imm = (InputMethodManager) v.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (isOpen) {
            imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
        } else {
            if (imm.isActive()) {
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
        }
    }

    private void showDialog() {
        if (null == dialogView) {
            dialogView = LayoutInflater.from(mContext).inflate(
                    R.layout.dialog_edit_classes, null);
            etGrade = (EditText) dialogView.findViewById(R.id.etGrade);

            mUploadDialog = new AlertDialog.Builder(mContext)
                    .setView(dialogView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if ("".equals(etGrade.getText().toString())) {
                                Toast.makeText(x.app(), "班级名字不能为空！", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            handlerKeyboard(activity.getWindow().peekDecorView(), false);
                            updateName();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handlerKeyboard(activity.getWindow().peekDecorView(), false);
                            mUploadDialog.dismiss();
                        }
                    })
                    .create();
        }
        mUploadDialog.show();
    }

    /**
     * 修改班级名字
     */
    private void updateName() {
        CustomProgress.show(mContext, "修改中...", true, null);
        String token = MyApplication.getInstance().getShareUser().getString("token", "");
        String classId = manager.getClassId();
        final String name = etGrade.getText().toString().trim();
//        RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/schoolClass/update?token=" + MyApplication.getInstance().getShareUser().getString("token", "") + "&Class_id=" + manager.getClassId());
//        params.addQueryStringParameter("name", etGrade.getText().toString());
        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Class_id", classId);
        mParams.put("name", name);

        x.http().get(HttpUtils.getRequestParams("/teacher/v1/schoolClass/update", mParams),

                new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        CustomProgress.hideDialog();
                        LogUtil.d("" + result);
                        Response response = CommonUtil.checkResponse(result);
                        if (response.isStatus()) {
                            tvGrade.setText(name);
                            MyApplication.getInstance().classesName = name;
                        } else {
                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        CustomProgress.hideDialog();

                        if (ex instanceof HttpException) { // 网络错误
                            HttpException httpEx = (HttpException) ex;
                            int responseCode = httpEx.getCode();
                            String responseMsg = httpEx.getMessage();
                            String errorResult = httpEx.getResult();
                            LogUtil.d(responseCode + ":" + responseMsg);
                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                        } else { // 其他错误
                            // ...
                        }

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Toast.makeText(x.app(), "cancelled", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }

    /**
     * 删除小红点
     */
    private void campusDelDot(int moduleId) {
        CampusDelDot campusDelDot = new CampusDelDot();
        campusDelDot.classId = MyApplication.getInstance().getClassId();
        campusDelDot.token = MyApplication.getInstance().getShareUser().getString("token", "");
        campusDelDot.moduleId = moduleId;
        mSocket.emit(Static.SOCKET_DEL_DOT, GsonUtils.toJson(campusDelDot));
        LogUtil.e("GsonUtils.toJson(campusDelDot): " + GsonUtils.toJson(campusDelDot));
    }

}
