package com.handmark.pulltorefresh.library.internal;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by yiban on 2015/12/7.
 */
public class MyScrollview extends ScrollView{

    private ScrollViewListener mListener = null;
    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.mListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mListener != null) {
            mListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }
    public MyScrollview(Context context) {
        super(context);
    }

    public MyScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
