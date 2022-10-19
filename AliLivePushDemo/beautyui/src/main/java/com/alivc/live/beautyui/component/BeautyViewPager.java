package com.alivc.live.beautyui.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

/**
 * 美颜Tab对应的ViewPager，禁止ViewPager滑动、禁止滑动动画
 */
class BeautyViewPager extends ViewPager {
    public BeautyViewPager(@NonNull Context context) {
        super(context);
    }

    public BeautyViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // can not scroll by view page
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // can not scroll by view page
        return false;
    }

    @Override
    public void setCurrentItem(int item) {
        // disable scroll view animation
        super.setCurrentItem(item, false);
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        super.setCurrentItem(item, smoothScroll);
    }
}
