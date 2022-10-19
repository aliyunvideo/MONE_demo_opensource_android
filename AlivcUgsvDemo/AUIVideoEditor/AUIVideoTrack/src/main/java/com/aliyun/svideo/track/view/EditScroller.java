package com.aliyun.svideo.track.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.track.R;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.inc.IScrollChangeListener;
import com.aliyun.svideo.track.util.Util;


public class EditScroller extends FrameLayout {
    /**
     * 是否需要回调滚动通知
     */
    private boolean isNoticeScrollDx = true;
    /**
     * 滚动事件回调
     */
    private IScrollChangeListener mScrollChangeListener;
    /**
     * 时间轴最大滚动距离
     */
    private int mTimelineMaxScrollX;

    /**
     * 时间轴时长
     */
    private long mTimelineDuration;
    /**
     * 当前时间刻度缩放值
     */
    private float mTimelineScale = 1.0f;

    public EditScroller(@NonNull Context context) {
        this(context, null);
    }

    public EditScroller(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EditScroller(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        int marginStart = getResources().getDimensionPixelSize(R.dimen.video_track_margin_start);
        int leftBtnWidth = getResources().getDimensionPixelSize(R.dimen.video_track_left_btn_width);
        //左右两边间距
        int paddingLeft = ((Util.getScreenWidth() / 2) - TrackConfig.MOVE_BTN_WIDTH) - marginStart - leftBtnWidth;
        setPadding(paddingLeft, getPaddingTop(), paddingLeft, getPaddingBottom());
    }

    public void setTimelineDuration(long timelineDuration) {
        mTimelineDuration = timelineDuration;
        resizeMaterialsMaxScrollX();
    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
    }

    private void resizeMaterialsMaxScrollX() {
        mTimelineMaxScrollX = Math.round(mTimelineDuration * TrackConfig.getPxUnit(mTimelineScale));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (isNoticeScrollDx && mScrollChangeListener != null) {
            mScrollChangeListener.onScrollChanged(getScrollX(), l - oldl);
        }
        if (mScrollChangeListener != null) {
            mScrollChangeListener.onScrollChanged(getScrollX());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        resizeMaterialsMaxScrollX();
        if (getChildCount() != 0) {
            int padding = getPaddingTop() + getPaddingBottom();
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (child.getVisibility() != GONE) {
                    ViewGroup.LayoutParams layoutParams = child.getLayoutParams();
                    int height = heightMeasureSpec - padding;
                    if (layoutParams != null) {
                        MarginLayoutParams marginLayoutParams = (MarginLayoutParams) layoutParams;
                        height = height - marginLayoutParams.topMargin - marginLayoutParams.bottomMargin;
                    }
                    measureChild(child, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), height);
                }
            }
            setMeasuredDimension(getSize(widthMeasureSpec), heightMeasureSpec);
        } else if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            setMeasuredDimension(getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        } else {
            setMeasuredDimension(0, 0);
        }
    }

    /**
     * 获取最大滚动距离
     *
     * @return 最大滚动距离
     */
    public final int getMaxScrollX() {
        return mTimelineMaxScrollX;
    }

    private int getSize(int widthMeasureSpec) {
        int size = MeasureSpec.getSize(widthMeasureSpec);
        if (size == 0) {
            return Util.getScreenWidth();
        }
        return size;
    }

    /**
     * 滚动指定距离
     * @param scrollDx 滚动距离
     * @param invokeChangeListener 是否回调变化
     */
    public void startScrollDx(int scrollDx, boolean invokeChangeListener) {
        isNoticeScrollDx = invokeChangeListener;
        startScrollToX(getScrollX() + scrollDx, invokeChangeListener);
    }

    /**
     * X轴滚动到指定位置
     * @param scrollToX 指定位置
     * @param invokeChangeListener 是否回调变化
     */
    public void startScrollToX(int scrollToX, boolean invokeChangeListener) {
        isNoticeScrollDx = invokeChangeListener;
        if(scrollToX < 0){
            scrollToX = 0;
        }
        scrollToX = Math.min(scrollToX, getMaxScrollX());
        scrollTo(scrollToX, 0);
    }

    public void setScrollChangeListener(IScrollChangeListener scrollChangeListener) {
        mScrollChangeListener = scrollChangeListener;
    }
}
