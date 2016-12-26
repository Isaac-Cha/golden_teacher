package com.yl.teacher.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yl.teacher.R;


/**
 * 自定义标题栏
 * 
 * @author 
 */
public class CustomTitleBar extends LinearLayout {

	private LayoutInflater mInflater;

	private View m_vTitleView;

	private ImageButton m_vLeftButton2;
	
	private ImageButton m_vLeftButton;

	private ImageButton m_vRightButton;
	
	private ImageButton m_vRightButton2;

	private RelativeLayout m_vLeftItem;

	private RelativeLayout m_vRightItem;

	private TextView m_vLeftTitle;

	private TextView m_vRightTitle;

	private TextView m_vCenterTitle;

    private ImageButton imgSunFlower;
	
	// 页面变量
	private Activity mActivity;
	private Resources mResources;
	private OnTitleBarBackListener mBackListener;

	public CustomTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		init(context);
	}

	public CustomTitleBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		mResources = context.getResources();
		mInflater = LayoutInflater.from(context);
		m_vTitleView = mInflater.inflate(R.layout.widget_custom_titlebar, null);
		m_vLeftItem = (RelativeLayout) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_left_item);
		m_vRightItem = (RelativeLayout) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_right_item);
		m_vLeftButton = (ImageButton) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_left_button);
		m_vLeftButton2 = (ImageButton) m_vTitleView
                .findViewById(R.id.widget_custom_titlebar_left_button_two);
		m_vRightButton = (ImageButton) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_right_button);
		m_vRightButton2 = (ImageButton) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_right_button_two);
		m_vLeftTitle = (TextView) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_left_title);
		m_vRightTitle = (TextView) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_right_title);
		m_vCenterTitle = (TextView) m_vTitleView
				.findViewById(R.id.widget_custom_titlebar_center_title);
        imgSunFlower = (ImageButton) m_vTitleView
                .findViewById(R.id.widget_custom_titlebar_centre_leftimage);
		LayoutParams llp = new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		addView(m_vTitleView, llp);
	}

    /**
     * 显示七色花图片
     */
    public void showSunFlower(){
        imgSunFlower.setVisibility(VISIBLE);
    }

	
	public void setActivityNotClose(Activity activity, OnTitleBarBackListener listener) {
		mActivity = activity;
		mBackListener = listener;
		displayBackBtn(true);
		if (mActivity != null) {
			m_vLeftItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mBackListener != null) {
						mBackListener.onBtnBackPressed();
					}
					
				}
			});
		}
	}
	
	
	public void setActivity(Activity activity, OnTitleBarBackListener listener) {
		mActivity = activity;
		mBackListener = listener;
		displayBackBtn(true);
		if (mActivity != null) {
			m_vLeftItem.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (mBackListener != null) {
						mBackListener.onBtnBackPressed();
					}
					if (mActivity != null) {
						mActivity.finish();
					}
					mActivity = null;
				}
			});
		}
	}

	public void setActivity(Activity activity) {
		setActivity(activity, null);
	}

	/**
	 * 显示左侧控件
	 */
	public void displayLeftItem(boolean b) {
		if (b) {
			m_vLeftItem.setVisibility(View.VISIBLE);
		} else {
			m_vLeftItem.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示右侧控件
	 */
	public void displayRightItem(boolean b) {
		if (b) {
			m_vRightItem.setVisibility(View.VISIBLE);
		} else {
			m_vRightItem.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置主标题
	 */
	public void setCenterTitle(CharSequence text) {
		m_vCenterTitle.setText(text);
	}

	
	/**
	 * 设置左侧标题
	 */
	public void setLeftTitle(CharSequence text) {
		m_vLeftTitle.setText(text);
	}

	/**
	 * 设置右侧标题
	 */
	public void setRightTitle(CharSequence text) {
		m_vRightTitle.setText(text);
	}

	/**
	 * 设置主标题
	 */
	public void setCenterTitle(int res) {
		m_vCenterTitle.setText(mResources.getString(res));
	}

	/**
	 * 设置左侧标题
	 */
	public void setLeftTitle(int res) {
		m_vLeftTitle.setText(mResources.getString(res));
	}

	/**
	 * 设置右侧标题
	 */
	public void setRightTitle(int res) {
		m_vRightTitle.setText(mResources.getString(res));
	}

	/**
	 * 显示返回键
	 */
	public void displayBackBtn(boolean b) {
		if (b) {
			m_vLeftButton.setVisibility(View.VISIBLE);
		} else {
			m_vLeftButton.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示右侧btn
	 */
	public void displayRightBtn(boolean b) {
		if (b) {
			m_vRightButton.setVisibility(View.VISIBLE);
		} else {
			m_vRightButton.setVisibility(View.GONE);
		}
	}
	
	/**
	 * 显示右侧btn2
	 */
	public void displayRightBtnTwo(boolean b) {
		if (b) {
			m_vRightButton2.setVisibility(View.VISIBLE);
		} else {
			m_vRightButton2.setVisibility(View.GONE);
		}
	}
	
	/**
     * 显示左侧btn2
     */
    public void displayLeftBtnTwo(boolean b) {
        if (b) {
            m_vLeftButton2.setVisibility(View.VISIBLE);
        } else {
            m_vLeftButton2.setVisibility(View.GONE);
        }
    }
	

	/**
	 * 设置左侧按钮图片
	 */
	public void setLeftBtnIcon(int res) {
		m_vLeftButton.setImageResource(res);
	}

	/**
     * 设置左侧按钮二图片
     */
    public void setLeftBtnTwoIcon(int res) {
        m_vLeftButton2.setImageResource(res);
    }
	
	/**
	 * 设置右侧按钮图片
	 */
	public void setRightBtnIcon(int res) {
		m_vRightButton.setImageResource(res);
	}
	
	/**
	 * 设置右侧按钮图片
	 */
	public void setRightBtnIconTwo(int res) {
		m_vRightButton2.setImageResource(res);
	}

	/**
	 * 设置右侧按钮1点击事件
	 */
	public void setRightBtnOnClickListener(OnClickListener listener) {
		if (listener == null) {
			return;
		}
		m_vRightButton.setOnClickListener(listener);
	}
	
	/**
	 * 设置右侧按钮2点击事件
	 */
	public void setRightBtnTwoOnClickListener(OnClickListener listener) {
		if (listener == null) {
			return;
		}
		m_vRightButton2.setOnClickListener(listener);
	}
	
	/**
     * 设置左侧按钮点击事件
     */
    public void setLeftBtnOnClickListener(OnClickListener listener) {
        if (listener == null) {
            return;
        }
        m_vLeftItem.setOnClickListener(listener);
    }

	/**
	 * 设置右侧按钮点击事件
	 */
	public void setRightItemOnClickListener(OnClickListener listener) {
		if (listener == null) {
			return;
		}
		m_vRightItem.setOnClickListener(listener);
	}

	public interface OnTitleBarBackListener {
		public void onBtnBackPressed();
	}
}
