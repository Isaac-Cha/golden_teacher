package com.yl.teacher.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.umeng.analytics.MobclickAgent;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.R;
import com.yl.teacher.adapter.AddVoteAdapter;
import com.yl.teacher.model.Action;
import com.yl.teacher.model.Response;
import com.yl.teacher.model.VoteOption;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.GsonUtils;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.StringUtils;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.widget.CustomTitleBar;
import com.yl.teacher.widget.NoScrollListView;
import com.yl.teacher.widget.SystemBarTintManager;
import com.yl.teacher.xalertdialog.SweetAlertDialog;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 发起投票页面
 */
public class AddVoteActivity extends BaseActivity {

    private final String VOTINGTYPE_SINGLESELECTION = "单选";
    private final String VOTINGTYPE_MULTISELECTTION = "多选";
    private final String VOTEPUBLIC_YES = "公开";
    private final String VOTEPUBLIC_NO = "不公开";

    private CustomTitleBar mTitleBar;
    private boolean flagSub;
    private long mCurrentTimeL;
    private long mChoosedTimeL;
    private long mStartTimeL;
    private long mEndTimeL;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    private NoScrollListView lvAddVote;

    private AddVoteAdapter adapter;

    private TextView tvAdd;

    private List<VoteOption> mData;

    private List<VoteOption> voteList;

    private EditText etName;
    private TextView tvStart;
    private TextView tvEnd;
    private LinearLayout linearStart;
    private LinearLayout linearEnd;
    private LinearLayout linear_votingtype;
    private TextView tv_votingtype;
    private LayoutInflater mInflater;
    private View popupView;
    private PopupWindow popupWindow;
    private LinearLayout linear_anonymous;
    private TextView tv_anonymous;
    private String votingType;
    private String anonymous;


    public void onEventMainThread(Action action) {
        if (action.getId() == Static.EVENTBUS_TYPE_VOTE) {

            VoteOption voteOption = action.getVote();
            LogUtil.i(voteOption.getImage());
            if (mData.size() > 0) {

                int index = checkData(mData, voteOption);
                if (index == -1) {
                    mData.add(voteOption);
                } else {
                    mData.remove(mData.get(index));
                    mData.add(index, voteOption);
                }

            } else {
                mData.add(voteOption);
            }

            if (adapter == null) {
                adapter = new AddVoteAdapter(this, mData);
                lvAddVote.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }
        }
    }

    private int checkData(List<VoteOption> mData, VoteOption voteOption) {
        for (VoteOption vote : mData) {
            if (vote.id.equals(voteOption.id)) {
                int index = mData.indexOf(vote);
                return index;
            }
        }
        return -1;
    }

    private android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            addVote();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("AddVoteActivity");
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("AddVoteActivity");
        MobclickAgent.onPause(this);
    }

    @Override
    protected int loadLayout() {
        return R.layout.activity_addvote;
    }

    @Override
    protected void initViews() {
        voteList = new ArrayList<>();
        EventBus.getDefault().register(this);
        //tmpList = new ArrayList<>();
        CommonUtil.systemBarTint(new SystemBarTintManager(this), R.color.navi_user);
        initBar();
        mData = new ArrayList<>();
        lvAddVote = (NoScrollListView) findViewById(R.id.lvAddVote);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        etName = (EditText) findViewById(R.id.etName);
        tvStart = (TextView) findViewById(R.id.tvStart);
        tvEnd = (TextView) findViewById(R.id.tvEnd);
        linearStart = (LinearLayout) findViewById(R.id.linearStart);
        linearEnd = (LinearLayout) findViewById(R.id.linearEnd);
        // 投票类型
        linear_votingtype = (LinearLayout) findViewById(R.id.linear_votingtype);
        tv_votingtype = (TextView) findViewById(R.id.tv_votingtype);
        // 公开结果
        linear_anonymous = (LinearLayout) findViewById(R.id.linear_anonymous);
        tv_anonymous = (TextView) findViewById(R.id.tv_anonymous);

    }

    @Override
    protected void initData() {
        mInflater = LayoutInflater.from(this);
    }

    @Override
    protected void initListener() {

        linearStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(tvStart);
                UiUtils.closeKeyboard(AddVoteActivity.this);
            }
        });
        linearEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(tvEnd);
                UiUtils.closeKeyboard(AddVoteActivity.this);
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddVoteActivity.this, VoteMsgActivity.class);
                startActivity(intent);
            }
        });

        lvAddVote.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent mIntent = new Intent(AddVoteActivity.this, VoteMsgActivity.class);
                mIntent.putExtra("VoteOption", mData.get(position));
                startActivity(mIntent);
            }
        });

        lvAddVote.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteOption(position);
                return true;
            }
        });

        linear_votingtype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoppupWindow(VOTINGTYPE_SINGLESELECTION, VOTINGTYPE_MULTISELECTTION, tv_votingtype);
            }
        });

        linear_anonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPoppupWindow(VOTEPUBLIC_YES, VOTEPUBLIC_NO, tv_anonymous);
            }
        });

    }

    private void deleteOption(final int position) {
        new SweetAlertDialog(AddVoteActivity.this, SweetAlertDialog.WARNING_TYPE, true)
                .setTitleText("确认要删除该选项吗？")
                .setCancelText("取消")
                .setConfirmText("删除")
                .showCancelButton(true)
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                        mData.remove(mData.get(position));
                        adapter.notifyDataSetChanged();
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initBar() {

        mTitleBar = (CustomTitleBar) findViewById(R.id.widget_custom_titlebar);
        mTitleBar.setCenterTitle("发起投票");
        mTitleBar.setActivity(this);
        mTitleBar.setRightTitle("发送");
        mTitleBar.displayRightItem(true);
        mTitleBar.setRightItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                handlerKeyboard(getWindow().peekDecorView(), false);

                if (!flagSub) {

                    new Thread() {
                        @Override
                        public void run() {
                            voteList.clear();
                            for (int i = 0; i < mData.size(); i++) {
                                VoteOption vote = new VoteOption();
                                vote.setTitle(mData.get(i).getTitle());
                                String image = mData.get(i).getImage();
                                vote.setImage(CommonUtil.bitmapToString(image));
                                String mimeType = StringUtils.getMimeType(image);
                                vote.setImageType(mimeType);
                                voteList.add(vote);
                            }

                            handler.sendMessage(new Message());

                        }
                    }.start();
                }

            }
        });
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

    private void showDialog(final TextView time) {

        // 时间选择器
        TimePickerView mTimePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        // 设置时间范围
        Calendar mCalendar = Calendar.getInstance();
        mTimePickerView.setRange(mCalendar.get(Calendar.YEAR) - 100, mCalendar.get(Calendar.YEAR) + 100);
        String thisTime = time.getText().toString().trim();
        LogUtil.e("thisTime: " + thisTime);
        if (StringUtils.isEmpty(thisTime)) {
            mTimePickerView.setTime(new Date());
        } else {
            try {
                Date date = mDateFormat.parse(thisTime);
                mTimePickerView.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mTimePickerView.setCyclic(true);
        mTimePickerView.setCancelable(true);
        // 时间选择后回调
        mTimePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                String mDate = mDateFormat.format(date);
                time.setText(mDate);

                if (time.getId() == R.id.tvStart) {
                    mStartTimeL = mChoosedTimeL = date.getTime();
                    // 获取当前时间
                    try {
                        mCurrentTimeL = mDateFormat.parse(mDateFormat.format(new Date())).getTime();
                    } catch (ParseException e) {

                    }
                } else if (time.getId() == R.id.tvEnd) {
                    mEndTimeL = date.getTime();
                }

            }
        });
        mTimePickerView.show();

    }

    public void addVote() {

        if ("".equals(etName.getText().toString())) {
            UiUtils.showToast("标题不能为空！");
            return;
        }
        if (0 == mChoosedTimeL) {
            UiUtils.showToast("开始时间不能为空！");
            return;
        }
        if (0 == mEndTimeL) {
            UiUtils.showToast("结束时间不能为空！");
            return;
        }

        if (mStartTimeL < mCurrentTimeL) {
            UiUtils.showToast("开始时间不能早于今天");
            return;
        }

        if (mEndTimeL < mStartTimeL) {
            UiUtils.showToast("结束时间不能早于开始时间");
            return;
        }

        // 投票类型
        votingType = tv_votingtype.getText().toString().trim();
        if (StringUtils.isEmpty(votingType)) {
            UiUtils.showToast("请选择投票类型");
            return;
        }

        // 投票结果公开
        anonymous = tv_anonymous.getText().toString().trim();
        if (StringUtils.isEmpty(anonymous)) {
            UiUtils.showToast("请选择投票结果是否公开");
            return;
        }

        if (voteList.size() < 2) {
            Toast.makeText(x.app(), "投票选项不能少于2个！", Toast.LENGTH_SHORT).show();
            return;
        }

        new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE, true)
                .setTitleText("确定发布投票吗？")
                .setCancelText("取消")
                .setConfirmText("确定")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                })
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        flagSub = true;
                        String token = MyApplication.getInstance().getShareUser().getString("token", "");
                        CustomProgress.show(AddVoteActivity.this, "提交中...", true, null);

                        Map<String, String> mParams = new HashMap<>();
                        mParams.put("token", token);
                        mParams.put("Class_id", MyApplication.getInstance().getClassId());
                        mParams.put("title", etName.getText().toString().trim());
                        mParams.put("startTime", tvStart.getText().toString().trim());
                        mParams.put("endTime", tvEnd.getText().toString().trim());
                        mParams.put("option", GsonUtils.toJson(voteList));

                        if (VOTEPUBLIC_NO.equals(anonymous)) { // 是否公开
                            mParams.put("anonymous", "1");
                        } else {
                            mParams.put("anonymous", "0");
                        }

                        if (VOTINGTYPE_MULTISELECTTION.equals(votingType)) { // 是否多选
                            mParams.put("multiple", "1");
                        } else {
                            mParams.put("multiple", "0");
                        }

                        Map<String, String> mParamsBody = new HashMap<>();
                        mParamsBody.put("option", GsonUtils.toJson(voteList));

                        x.http().post(HttpUtils.getRequestParams("/teacher/v1/vote/add", mParams, mParamsBody),

                                new Callback.CommonCallback<String>() {
                                    @Override
                                    public void onSuccess(String result) {
                                        flagSub = false;
                                        CustomProgress.hideDialog();
                                        LogUtil.d("" + result);
                                        Response response = CommonUtil.checkResponse(result);
                                        if (response.isStatus()) {
                                            Action action = new Action();
                                            action.setId(Static.EVENTBUS_TYPE_VOTELIST);
                                            EventBus.getDefault().post(action);
                                            finish();
                                        } else {

                                            Toast.makeText(x.app(), response.getData().optString("message"), Toast.LENGTH_SHORT).show();

                                        }
                                    }

                                    @Override
                                    public void onError(Throwable ex, boolean isOnCallback) {

                                        CustomProgress.hideDialog();
                                        if (ex instanceof HttpException) { // 网络错误

                                            flagSub = false;
                                            HttpException httpEx = (HttpException) ex;
                                            int responseCode = httpEx.getCode();
                                            String responseMsg = httpEx.getMessage();
                                            String errorResult = httpEx.getResult();
                                            LogUtil.d(responseCode + ":" + responseMsg);
                                            Toast.makeText(x.app(), x.app().getResources().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
                                        } else { // 其他错误
                                            // ...
                                        }
                                        //Toast.makeText(x.app(), ex.getMessage(), Toast.LENGTH_LONG).show();
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
                })
                .show();

    }

    private void showPoppupWindow(final String text1, final String text2, final TextView textView) {
        handlerKeyboard(getWindow().peekDecorView(), false);
        if (popupView == null) {
            popupView = mInflater.inflate(R.layout.popup_three, null);
        }

        final TextView tv_one = (TextView) popupView.findViewById(R.id.tv_one);
        final TextView tv_two = (TextView) popupView.findViewById(R.id.tv_two);
        TextView tvCancel = (TextView) popupView.findViewById(R.id.tv_cancel);

        tv_one.setText(text1);
        tv_two.setText(text2);
        tvCancel.setText("取消");

        popupWindow = new PopupWindow(popupView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        // 设置popWindow弹出窗体可点击，这句话必须添加，并且是true
        popupView.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
//            ColorDrawable dw = new ColorDrawable(0x7d000000);
        popupView.setBackgroundDrawable(new BitmapDrawable());
        // 设置popWindow的显示和消失动画
        popupWindow.setAnimationStyle(R.style.bottom_dialog);

        popupWindow.setOutsideTouchable(true);

        tv_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(text1);
                popupWindow.dismiss();
            }
        });

        tv_two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText(text2);
                popupWindow.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        UiUtils.darkenScreen(this, popupWindow);

        // 在底部显示
        popupWindow.showAtLocation(mTitleBar,
                Gravity.BOTTOM, 0, 0);

    }

}
