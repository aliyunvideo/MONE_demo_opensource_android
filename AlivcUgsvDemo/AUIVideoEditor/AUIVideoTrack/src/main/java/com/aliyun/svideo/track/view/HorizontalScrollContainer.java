package com.aliyun.svideo.track.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.svideo.track.api.ScrollState;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.inc.OnScrollStateChangeListener;
import com.aliyun.svideo.track.inc.ScrollHandler;
import com.aliyun.svideo.track.thumbnail.ThumbnailFetcherManger;


public abstract class HorizontalScrollContainer extends ConstraintLayout implements ScrollHandler {
    private static String TAG = "HorizontalScrollContain";

    private OverScroller mOverScroller;
    private int mScaledTouchSlop;
    private int mScaledMinimumFlingVelocity;
    private int mScaledMaximumFlingVelocity;
    private ScrollState mScrollState;
    private boolean isCallbackScrollDx;
    private boolean mStopListenerEvent;
    private float lastInterceptDownX;
    private float interceptDownXX;
    private float interceptDownX;
    private float interceptDownY;
    private OnScrollStateChangeListener mOnScrollStateChangeListener;
    private VelocityTracker mVelocityTracker;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean isTouchDown;
    private OnClickListener mOnBlankClickListener;
    /**
     * 当前时间刻度缩放值
     */
    protected float mTimelineScale = 1.0f;
    private ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener =  new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        private float previousScaleFactor = 1.0f;
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = mTimelineScale * detector.getScaleFactor() / previousScaleFactor;
            previousScaleFactor = detector.getScaleFactor();
            if (scale < 0.1f) {
                scale = 0.1f;
            }
            if (scale > 10f) {
                scale = 10f;
            }
            onTimelineScaleChange(scale);
            return super.onScale(detector);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            previousScaleFactor = 1.0f;
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
            previousScaleFactor = 1.0f;
        }
    };

    public HorizontalScrollContainer(@NonNull Context context) {
        this(context, null);
    }

    public HorizontalScrollContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        setVerticalScrollBarEnabled(false);
        setVerticalFadingEdgeEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setHorizontalFadingEdgeEnabled(false);
        mOverScroller = new OverScroller(context);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        mScaledMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mScaledMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mScrollState = ScrollState.IDLE;
        isCallbackScrollDx = true;
        lastInterceptDownX = -1;
        mScaleGestureDetector = new ScaleGestureDetector(context, mSimpleOnScaleGestureListener);
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return true;
    }

    public boolean getStopListenerEvent() {
        return mStopListenerEvent;
    }

    public void setStopListenerEvent(boolean z) {
        mStopListenerEvent = z;
    }

    @Override
    public void computeScroll() {
        if (!mOverScroller.isFinished() && mOverScroller.computeScrollOffset()) {
            if (mOverScroller.getCurrX() != mOverScroller.getFinalX()) {
                scrollChildToX(mOverScroller.getCurrX(), isCallbackScrollDx);
                setScrollState(mScrollState);
            } else {
                scrollChildToX(mOverScroller.getCurrX(), isCallbackScrollDx);
                mOverScroller.forceFinished(true);
            }
            postInvalidateOnAnimation();
        } else if (mScrollState == ScrollState.SETTLING) {
            setScrollState(ScrollState.IDLE);
        }
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        if (disallowIntercept) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
            }
            mVelocityTracker = null;
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent == null) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        if (mStopListenerEvent) {
            return true;
        }
        if (motionEvent.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            return true;
        }

        if (motionEvent.getPointerCount() >= 2) {
            return true;
        }

        if ((motionEvent.getAction() == MotionEvent.ACTION_MOVE && mScrollState == ScrollState.DRAGGING) || super.onInterceptTouchEvent(motionEvent)) {
            return true;
        }
        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            interceptDownX = motionEvent.getX();
            interceptDownY = motionEvent.getY();
            interceptDownXX = motionEvent.getX();
            abortAnimation();
        } else if (action == MotionEvent.ACTION_MOVE) {
            float x = motionEvent.getX() - interceptDownX;
            if (Math.abs(x) >= Math.abs(motionEvent.getY() - interceptDownY) && mScaledTouchSlop <= Math.abs(x)) {
                setScrollState(ScrollState.DRAGGING);
            }
        }
        return this.mScrollState == ScrollState.DRAGGING;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (mStopListenerEvent) {
            return true;
        }
        if (motionEvent.getPointerCount() >= 2) {
            if (mScaleGestureDetector != null) {
                mScaleGestureDetector.onTouchEvent(motionEvent);
            }
            return true;
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction();
        ScrollState scrollState;
        if (action == MotionEvent.ACTION_DOWN) {
            isTouchDown = true;
        } else if (action == MotionEvent.ACTION_UP) {
            super.requestDisallowInterceptTouchEvent(false);
            if (mScrollState == ScrollState.DRAGGING) {
                mVelocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                float xVelocity = -getXVel(mVelocityTracker.getXVelocity());
                if (xVelocity != 0.0f) {
                    flingX((int) xVelocity);
                    scrollState = ScrollState.SETTLING;
                } else {
                    scrollState = ScrollState.IDLE;
                }
                setScrollState(scrollState);
            } else if (isTouchDown) {
                if (mOnBlankClickListener != null) {
                    mOnBlankClickListener.onClick(this);
                }
                setScrollState(ScrollState.IDLE);
            }

            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
            }
            mVelocityTracker = null;
        } else if (action == MotionEvent.ACTION_MOVE) {
            super.requestDisallowInterceptTouchEvent(true);
            if (mScrollState == ScrollState.DRAGGING) {
                onScrollByX((int) (interceptDownXX - motionEvent.getX()), true);
                setScrollState(ScrollState.DRAGGING);
            } else {
                float x = motionEvent.getX() - interceptDownX;
                if (Math.abs(x) >= Math.abs(motionEvent.getY() - interceptDownY) && mScaledTouchSlop <= Math.abs(x)) {
                    setScrollState(ScrollState.DRAGGING);
                }
            }
            this.interceptDownXX = motionEvent.getX();
            if (Math.abs(lastInterceptDownX - interceptDownXX) >= 1.0f) {
                lastInterceptDownX = interceptDownXX;
            }
            isTouchDown = this.mScrollState != ScrollState.DRAGGING;
        } else if (action == MotionEvent.ACTION_CANCEL) {
            super.requestDisallowInterceptTouchEvent(false);
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
            }
            mVelocityTracker = null;
            setScrollState(ScrollState.IDLE);
        }
        return true;
    }

    public void onTimelineScaleChange(float scale){

    }

    /**
     * 获取当前时间刻度缩放值
     *
     * @return 缩放值
     */
    public float getTimelineScale() {
        return mTimelineScale;
    }

    /**
     * 滚动到指定位置
     * @param x x轴位置
     * @param invokeChangeListener 是否回调变化
     */
    public void scrollChildToX(int x, boolean invokeChangeListener) {
        this.isCallbackScrollDx = invokeChangeListener;
        EditScroller editScroller = getEditScroller();
        if (editScroller != null) {
            editScroller.startScrollToX(x, invokeChangeListener);
        }
    }

    private void setScrollState(ScrollState state) {
        mScrollState = state;
        int childScrollX = getChildScrollX();
        if (mOnScrollStateChangeListener != null) {
            mOnScrollStateChangeListener.onScrollStateChanged(state, childScrollX, 0);
        }
    }

    /**
     * 滚动到指定时间
     *
     * @param playTime 滚动到达时间 单位:毫秒
     */
    public void scrollChildToPlayTime(long playTime) {
        ThumbnailFetcherManger.getInstance().onUpdatePlayTime(playTime);
        int x = Math.round(playTime * TrackConfig.getPxUnit(mTimelineScale));
        scrollChildToX(x, false);
    }

    protected abstract EditScroller getEditScroller();

    protected int getChildScrollX() {
        int dx = 0;
        EditScroller editScroller = getEditScroller();
        if (editScroller != null) {
            dx = Math.max(dx, editScroller.getScrollX());
        }
        return dx;
    }

    public int getChildMaxScrollX() {
        int dx = 0;
        EditScroller editScroller = getEditScroller();
        if (editScroller != null) {
            dx = Math.max(dx, editScroller.getMaxScrollX());
        }
        return dx;
    }

    private float getXVel(float xVelocity) {
        if (Math.abs(xVelocity) < mScaledMinimumFlingVelocity) {
            return 0.0f;
        }
        float abs = Math.abs(xVelocity);
        if (abs <= mScaledMaximumFlingVelocity) {
            return xVelocity;
        }
        if (xVelocity > 0) {
            return mScaledMaximumFlingVelocity;
        }
        return -mScaledMaximumFlingVelocity;
    }

    private void flingX(int xVelocity) {
        int startX = 0;
        int maxX = 0;
        EditScroller editScroller = getEditScroller();
        if (editScroller != null) {
            int max = Math.max(maxX, editScroller.getMaxScrollX());
            startX = Math.max(startX, editScroller.getScrollX());
            maxX = max;
        }
        mOverScroller.fling(startX, 0, xVelocity, 0, 0, maxX, 0, 0);
        postInvalidateOnAnimation();
    }

    public void abortAnimation() {
        if (mOverScroller != null && !mOverScroller.isFinished()) {
            mOverScroller.abortAnimation();
        }
    }

    public void setOnBlankClickListener(OnClickListener onClickListener) {
        mOnBlankClickListener = onClickListener;
    }

    @Override
    public void onScrollByX(int scrollDx, boolean invokeChangeListener) {
        this.isCallbackScrollDx = invokeChangeListener;
        EditScroller editScroller = getEditScroller();
        if (editScroller != null) {
            editScroller.startScrollDx(scrollDx, invokeChangeListener);
        }
    }

    @Override
    public void onScrollByY(int scrollDy, boolean invokeChangeListener) {
        this.isCallbackScrollDx = invokeChangeListener;
    }

    public void setOnScrollStateChangeListener(OnScrollStateChangeListener onScrollStateChangeListener) {
        mOnScrollStateChangeListener = onScrollStateChangeListener;
    }
}
