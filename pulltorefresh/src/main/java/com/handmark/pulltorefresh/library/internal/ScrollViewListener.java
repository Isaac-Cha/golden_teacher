package com.handmark.pulltorefresh.library.internal;

import android.widget.ScrollView;

/**
 * Created by yiban on 2015/12/14.
 */
public interface ScrollViewListener {
    void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy);
}
