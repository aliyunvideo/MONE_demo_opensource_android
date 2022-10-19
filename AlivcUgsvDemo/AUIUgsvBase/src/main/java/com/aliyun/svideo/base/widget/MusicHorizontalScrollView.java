package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MusicHorizontalScrollView extends HorizontalScrollView {
    private ScrollViewListener scrollViewListener = null;

    public MusicHorizontalScrollView(Context context) {
        super(context);
    }

    public MusicHorizontalScrollView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
    }

    public MusicHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {
        void onScrollChanged(HorizontalScrollView scrollView, int x, int y, int oldx, int oldy);
        void onScrollStop();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL){
            if(scrollViewListener != null){
                scrollViewListener.onScrollStop();
            }
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void fling(int velocityX) {
        super.fling(0);
    }
}
