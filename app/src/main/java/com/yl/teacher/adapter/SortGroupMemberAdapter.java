package com.yl.teacher.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yl.teacher.R;
import com.yl.teacher.global.MyApplication;
import com.yl.teacher.model.Contact;
import com.yl.teacher.model.GroupMemberBean;
import com.yl.teacher.model.Response;
import com.yl.teacher.util.CommonUtil;
import com.yl.teacher.util.HttpUtils;
import com.yl.teacher.util.Static;
import com.yl.teacher.util.UiUtils;
import com.yl.teacher.util.ViewHolder;
import com.yl.teacher.view.ParentChildInfoActivity;
import com.yl.teacher.widget.CustomProgress;
import com.yl.teacher.xalertdialog.SweetAlertDialog;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.utils.AutoUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.LogUtil;
import org.xutils.ex.HttpException;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SortGroupMemberAdapter extends BaseAdapter implements SectionIndexer {
	private List<GroupMemberBean> list = null;
	private Context context;
	private LayoutInflater mInflater;
	private List<Contact> mData;
	private LinearLayout linearNodata;
	private LinearLayout titleLayout;
	private ListView mListView;

	public SortGroupMemberAdapter(Context mContext, List<GroupMemberBean> list, List<Contact> data, LinearLayout linearNodata, LinearLayout titleLayout, ListView mListView) {
		this.context = mContext;
		this.list = list;
		this.mInflater = LayoutInflater.from(context);
		mData = data;
		this.linearNodata = linearNodata;
		this.titleLayout = titleLayout;
		this.mListView = mListView;
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.activity_group_member_item, parent, false);
			AutoUtils.autoSize(convertView);
		}
		Uri uri = Uri.parse(Static.IMAGE_IP + "/avatar/" + list.get(position).getUserId() + "/84_84");
		SimpleDraweeView imgIcon = ViewHolder.get(convertView, R.id.imgIcon);
		imgIcon.setImageURI(uri);

		TextView tvCatalog = ViewHolder.get(convertView, R.id.tvCatalog);
		TextView tvName = ViewHolder.get(convertView, R.id.tvName);
		TextView tvPhone = ViewHolder.get(convertView, R.id.tvPhone);
		AutoLinearLayout linearPhone = ViewHolder.get(convertView, R.id.linearPhone);
		linearPhone.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("tel:" +list.get(position).getPhone() );
				Intent intent = new Intent(Intent.ACTION_DIAL, uri);
				context.startActivity(intent);
			}
		});
		GroupMemberBean mContent = list.get(position);

		AutoLinearLayout all_main = ViewHolder.get(convertView, R.id.all_main);
		all_main.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent mIntent = new Intent(context, ParentChildInfoActivity.class);
				String userId = list.get(position).getUserId();
				for (Contact contact : mData) {
					if (contact.getUserId().equals(userId)) {
						Bundle mBundle = new Bundle();
						mBundle.putSerializable("contact", contact);
						mIntent.putExtras(mBundle);
						context.startActivity(mIntent);
						break;
					}
				}
			}
		});

		all_main.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				showDeleteDialog(position);
				return true;
			}
		});

		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);

		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			tvCatalog.setVisibility(View.VISIBLE);
			tvCatalog.setText(mContent.getSortLetters());
		} else {
			tvCatalog.setVisibility(View.GONE);
		}

		tvName.setText(list.get(position).getName());
		tvPhone.setText(list.get(position).getPhone());
		return convertView;

	}


	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	private void showDeleteDialog(final int position) {

		new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE, true)
				.setTitleText("确认要删除该联系人吗？")
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
						deleteParent(position);
					}
				})
				.show();

	}

	/**
	 * 删除一名家长
	 */
	private void deleteParent(final int postion) {

		CustomProgress.show(context, "删除中...", true, null);
		String id = list.get(postion).id;
		String token = MyApplication.getInstance().getShareUser().getString("token", "");
//		RequestParams params = new RequestParams(Static.URL_SERVER + "/teacher/v1/classMember/del?token=" + token + "&Member_id=" + id);

        Map<String, String> mParams = new HashMap<>();
        mParams.put("token", token);
        mParams.put("Member_id", id);

		x.http().get(HttpUtils.getRequestParams("/teacher/v1/classMember/del", mParams),

				new Callback.CommonCallback<String>() {
					@Override
					public void onSuccess(String result) {
						CustomProgress.hideDialog();
						LogUtil.d("" + result);
						Response response = CommonUtil.checkResponse(result);
						if (response.isStatus()) {
							UiUtils.showToast("删除成功");
							list.remove(postion);
							notifyDataSetChanged();

							if (list.size() <= 0) {
								linearNodata.setVisibility(View.VISIBLE);
								mListView.setVisibility(View.GONE);
								titleLayout.setVisibility(View.GONE);
							}

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

}