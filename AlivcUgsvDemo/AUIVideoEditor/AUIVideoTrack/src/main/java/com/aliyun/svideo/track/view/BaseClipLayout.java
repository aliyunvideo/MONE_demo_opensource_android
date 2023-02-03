package com.aliyun.svideo.track.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.RelativeLayout;

import com.aliyun.svideo.track.R;
import com.aliyun.svideo.track.api.HorizontallyState;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;
import com.aliyun.svideo.track.bean.MultiClipInfo;
import com.aliyun.svideo.track.inc.IMoveViewListener;
import com.aliyun.svideo.track.inc.ItemTrackCallback;
import com.aliyun.svideo.track.util.Util;

/**
 * 片段基类
 */
public abstract class BaseClipLayout extends RelativeLayout {
    protected ItemTrackCallback mItemTrackCallback;
    private int mScreenWidth;
    /**
     * 自动滚动速度
     */
    private float mAutoScrollerSpeed;
    /**
     * 控件左边位置
     */
    private int mContentLeftPosition;
    /**
     * 控件右边位置
     */
    private int mContentRightPosition;
    /**
     * 当前拖动按钮
     */
    protected MoveView mFocusMoveView;
    /**
     * 当前滚动方向
     */
    private HorizontallyState mScrollState;
    /**
     * 片段信息
     */
    private BaseClipInfo mClipInfo;
    /**
     * 当前时间刻度缩放值
     */
    protected float mTimelineScale = 1.0f;
    /**
     * 当前长度
     */
    private float mContentLength;
    protected View mContentView;
    private MoveView mLeftMoveView;
    private MoveView mRightMoveView;
    private boolean mEnable = true;
    /**
     * 是否可长按拖动
     */
    private boolean mDragEnable = true;

    protected boolean mDragHorizontallyEnable = true;

    /**
     * 自动滚屏动画
     */
    private ValueAnimator mAutoScrollAnim;

    protected ClipTrackStyle mStyle = ClipTrackStyle.DYNAMIC;

    public BaseClipLayout(Context context) {
        this(context, null);
    }

    public BaseClipLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseClipLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    protected void initView() {
        mAutoScrollerSpeed = 1.0f;
        mScrollState = HorizontallyState.NULL;
        mFocusMoveView = null;
        mScreenWidth = Util.getScreenWidth();
        inflateContentView();
        mContentView = findContentView();
        mLeftMoveView = (MoveView) findViewById(R.id.leftMoveBar);
        mRightMoveView = (MoveView) findViewById(R.id.rightMoveBar);
    }

    public abstract void inflateContentView();

    public abstract View findContentView();

    public void setMarkImage(Bitmap bitmap){

    }

    public void setClipText(String text){

    }

    public boolean isDragEnable() {
        return mDragEnable;
    }

    public void setDragEnable(boolean dragEnable) {
        this.mDragEnable = dragEnable;
        if (isDragEnable()) {
            mContentView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPreStartDrag(v);
                    return true;
                }
            });
        } else {
            mContentView.setOnLongClickListener(null);
        }
    }

    public void setDragHorizontallyEnable(boolean dragHorizontallyEnable) {
        this.mDragHorizontallyEnable = dragHorizontallyEnable;
    }

    public void onMoveFinish() {

    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
    }

    public void setItemTrackCallback(ItemTrackCallback iItemTrackCallback) {
        mItemTrackCallback = iItemTrackCallback;
    }

    public void setData(BaseClipInfo clipInfo) {
        mClipInfo = clipInfo;
        setListener();
        measureLayout();
    }

    protected void measureLayout() {
        onChangeViewParams(mContentLength, true);
        onChangeViewParams(mContentLength, false);
    }

    private int getMaxContentLength(boolean isLeftTap) {
        int maxContentLength = 0;
        if (isLeftTap) {
            int maxClipContentLength = (int) (mClipInfo.getOut() * TrackConfig.getPxUnit(mTimelineScale));
            //主轨则最长边界根据片段长度来计算
            if (mClipInfo.getClipType() == ClipType.MAIN_VIDEO) {
                return maxClipContentLength;
            }
            int maxTimeLineContentLength = (int) (mClipInfo.getTimelineOut() * TrackConfig.getPxUnit(mTimelineScale));
            //花字则根据时间线长度来计算
            if (mClipInfo.getClipType() == ClipType.CAPTION || mClipInfo.getClipType() == ClipType.STICKER ||
                    mClipInfo.getClipType() == ClipType.EFFECT) {
                return maxTimeLineContentLength - (int) (((MultiClipInfo) mClipInfo).getMinTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
            }
            maxContentLength = Math.min(maxTimeLineContentLength, maxClipContentLength);
        } else {
            int maxClipContentLength = (int) ((mClipInfo.getDuration() - mClipInfo.getIn()) * TrackConfig.getPxUnit(mTimelineScale));
            //主轨则最长边界根据片段长度来计算
            if (mClipInfo.getClipType() == ClipType.MAIN_VIDEO) {
                return maxClipContentLength;
            }
            long timelineDuration = getTimelineDuration();
            int maxTimeLineContentLength = 0;
            if (timelineDuration > 0) {
                if ((mClipInfo.getClipType() == ClipType.CAPTION || mClipInfo.getClipType() == ClipType.STICKER ||
                        mClipInfo.getClipType() == ClipType.EFFECT) && ((MultiClipInfo) mClipInfo).getMaxTimelineOut() != 0) {
                    timelineDuration = ((MultiClipInfo) mClipInfo).getMaxTimelineOut();
                }
                maxTimeLineContentLength = (int) ((timelineDuration - mClipInfo.getTimelineIn()) * TrackConfig.getPxUnit(mTimelineScale));
                //花字则根据时间线长度来计算
                if (mClipInfo.getClipType() == ClipType.CAPTION || mClipInfo.getClipType() == ClipType.STICKER ||
                        mClipInfo.getClipType() == ClipType.EFFECT) {
                    return maxTimeLineContentLength;
                }
                maxContentLength = Math.min(maxTimeLineContentLength, maxClipContentLength);
            } else {
                maxContentLength = maxClipContentLength;
            }
        }
        return maxContentLength;
    }

    private long getTimelineDuration() {
        if (getParent() != null && getParent().getParent() != null) {
            ViewGroup viewGroup = (ViewGroup) getParent().getParent();
            if (viewGroup instanceof OtherTrackLayout) {
                return ((OtherTrackLayout) viewGroup).getTimelineDuration();
            }
        }
        return 0;
    }

    private void onChangeViewParams(float newContentLength, boolean isLeftTap) {
        int maxContentLength = getMaxContentLength(isLeftTap);
        if (newContentLength > maxContentLength) {
            newContentLength = maxContentLength;
        }
        if (mContentLength == newContentLength) {
            return;
        }
        if (newContentLength < TrackConfig.getMinPx(mTimelineScale)) {
            newContentLength = TrackConfig.getMinPx(mTimelineScale);
        }
        mContentLength = newContentLength;
        long contentDuration = (long) (mContentLength / TrackConfig.getPxUnit(mTimelineScale));
        if (isLeftTap) {
            mClipInfo.setIn(mClipInfo.getOut() - contentDuration);
            mClipInfo.setTimelineIn(mClipInfo.getTimelineOut() - contentDuration);
        } else {
            mClipInfo.setOut(mClipInfo.getIn() + contentDuration);
            mClipInfo.setTimelineOut(mClipInfo.getTimelineIn() + contentDuration);
        }
        requestLayout();
    }

    protected void setListener() {
        mLeftMoveView.setMoveViewListener(new IMoveViewListener() {
            @Override
            public void onMove(float dx, float moveViewLeftBorderPosition) {
                mAutoScrollerSpeed = TrackConfig.autoScrollSpeed(moveViewLeftBorderPosition, mScreenWidth);
                updateContentWidth();
                updateContentView(HorizontallyState.LEFT, dx);
            }

            @Override
            public void onUp(float dRawX) {
                requestDisallowInterceptTouchEvent(false);
                onAutoScroll(HorizontallyState.NULL);
                onMoveFinish();
                mFocusMoveView = null;
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onClipTouchUp(mClipInfo.getClipId(), 0, getTrackClipInfo().getIn(), getTrackClipInfo().getOut());
                    mItemTrackCallback.onUpdateClipTime(mClipInfo.getClipId(),0, getTrackClipInfo().getIn(), getTrackClipInfo().getOut(),
                            getTrackClipInfo().getTimelineIn(), getTrackClipInfo().getTimelineOut());
                }
            }

            @Override
            public void onDown(float dx) {
                mFocusMoveView = mLeftMoveView;
                requestDisallowInterceptTouchEvent(true);
                updateContentWidth();
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onClipTouchDown(mClipInfo.getClipId(), 0, mContentLength, mContentLeftPosition);
                }
            }
        });
        mRightMoveView.setMoveViewListener(new IMoveViewListener() {
            @Override
            public void onMove(float dx, float moveViewLeftBorderPosition) {
                mAutoScrollerSpeed = TrackConfig.autoScrollSpeed(moveViewLeftBorderPosition, mScreenWidth);
                updateContentWidth();
                updateContentView(HorizontallyState.RIGHT, dx);
            }

            @Override
            public void onUp(float dRawX) {
                requestDisallowInterceptTouchEvent(false);
                updateContentWidth();
                onAutoScroll(HorizontallyState.NULL);
                onMoveFinish();
                mFocusMoveView = null;
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onClipTouchUp(mClipInfo.getClipId(), 1, getTrackClipInfo().getIn(), getTrackClipInfo().getOut());
                    mItemTrackCallback.onUpdateClipTime(mClipInfo.getClipId(), 1,getTrackClipInfo().getIn(), getTrackClipInfo().getOut(),
                            getTrackClipInfo().getTimelineIn(), getTrackClipInfo().getTimelineOut());
                }
            }

            @Override
            public void onDown(float dx) {
                mFocusMoveView = mRightMoveView;
                requestDisallowInterceptTouchEvent(true);
                updateContentWidth();
            }
        });
        mContentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onTrackTouch();
            }
        });
        if (isDragEnable()) {
            mContentView.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onPreStartDrag(v);
                    return true;
                }
            });
        }
    }

    protected void onPreStartDrag(View v){
        TrackConfig.draggedId = getTrackClipInfo().getClipId();
        DragShadowBuilder shadowBuilder = new com.aliyun.svideo.track.view.DragShadowBuilder(v);
        v.startDrag(null, shadowBuilder, getTrackClipInfo().getClipType().ordinal(), 0);
        v.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING);
    }

    public void setClipFocus(boolean isFocus) {
        if (isSelected() == isFocus) {
            return;
        }
        setSelected(isFocus);
        if (isFocus) {
            ViewParent parent = getParent();
            if (parent != null) {
                parent.bringChildToFront(this);
            }
        }
    }


    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (!mEnable || !this.mDragHorizontallyEnable) {
            return;
        }
        mLeftMoveView.setTouchAble(selected);
        mRightMoveView.setTouchAble(selected);
        findViewById(R.id.trackTopLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.trackBottomLine).setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        if(mStyle == ClipTrackStyle.DYNAMIC){
            mLeftMoveView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
            mRightMoveView.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        }else if(mStyle == ClipTrackStyle.FIXED){
            mLeftMoveView.setVisibility(View.GONE);
            mRightMoveView.setVisibility(View.GONE);
        }else{
            Log.e(BaseClipLayout.class.getSimpleName(), "undefined style");
        }
        if (selected) {
            findViewById(R.id.trackTopLine).animate().alpha(1.0f).setDuration(100L).start();
            findViewById(R.id.trackBottomLine).animate().alpha(1.0f).setDuration(100L).start();
            mLeftMoveView.animate().alpha(1.0f).setDuration(100L).start();
            mRightMoveView.animate().alpha(1.0f).setDuration(100L).start();
            ViewParent parent = getParent();
            if (parent != null) {
                parent.bringChildToFront(this);
            }
        }
    }

    private void updateContentWidth() {
        int[] leftPosition = new int[2];
        int[] rightPosition = new int[2];
        mLeftMoveView.getLocationOnScreen(leftPosition);
        mRightMoveView.getLocationOnScreen(rightPosition);
        mContentLeftPosition = leftPosition[0] + TrackConfig.MOVE_BTN_WIDTH;
        mContentRightPosition = rightPosition[0];
        mContentLength = mContentRightPosition - mContentLeftPosition;
    }

    private void updateContentView(HorizontallyState horizontallyState, float dx) {
        if (horizontallyState == HorizontallyState.LEFT) {
            updateLeft(dx);
        } else if (horizontallyState == HorizontallyState.RIGHT) {
            updateRight(dx);
        }
    }

    private void updateRight(float dx) {
        if (dx == 0) {
            return;
        }
        if (dx > 0) {
            int maxLength = getMaxContentLength(false);
            //如果距离大于最大距离则停止滚动
            if (mContentRightPosition - mContentLeftPosition >= maxLength) {
                onAutoScroll(HorizontallyState.NULL);
                return;
            }
        } else {
            //如果距离小于最小距离则停止滚动
            if (mContentRightPosition - mContentLeftPosition <= TrackConfig.getMinPx(mTimelineScale)) {
                onAutoScroll(HorizontallyState.NULL);
                return;
            }
        }
        if (!isNeedAutoScroll(mContentRightPosition, dx)) {
            onChangeViewParams(mContentLength + dx, false);
            if (mItemTrackCallback != null) {
                mItemTrackCallback.onClipTouchMove(mClipInfo.getClipId(), (int) dx, 1, mContentLength, 0);
            }
        }
    }

    private void updateLeft(float dx) {
        if (dx == 0) {
            return;
        }
        if (dx > 0) {
            //从左往右拖动，如果距离小于最小距离则停止滚动
            if (mContentRightPosition - mContentLeftPosition <= TrackConfig.getMinPx(mTimelineScale)) {
                onAutoScroll(HorizontallyState.NULL);
                return;
            }
        } else {
            int maxLength = getMaxContentLength(true);
            //如果距离大于最大距离则停止滚动
            if (mContentRightPosition - mContentLeftPosition >= maxLength) {
                onAutoScroll(HorizontallyState.NULL);
                return;
            }
        }

        if (!isNeedAutoScroll(mContentLeftPosition, dx)) {
            onChangeViewParams(mContentLength - dx, true);
            if (mItemTrackCallback != null) {
                mItemTrackCallback.onClipTouchMove(mClipInfo.getClipId(), Math.round(dx), 0, mContentLength, 0);
            }
        }
    }

    /**
     * 是否需要自动滚动
     *
     * @param rawX
     * @param dx
     * @return
     */
    private boolean isNeedAutoScroll(float rawX, float dx) {
        HorizontallyState horizontallyState;
        if (rawX >= mScreenWidth - TrackConfig.AUTO_SCROLL_WIDTH && dx > -1) {
            horizontallyState = HorizontallyState.RIGHT;
        } else if (rawX <= TrackConfig.AUTO_SCROLL_WIDTH && dx < 1) {
            horizontallyState = HorizontallyState.LEFT;
        } else {
            horizontallyState = HorizontallyState.NULL;
        }
        onAutoScroll(horizontallyState);
        return horizontallyState != HorizontallyState.NULL;
    }

    private final ValueAnimator.AnimatorUpdateListener mAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float length = (Float) animation.getAnimatedValue();
            if (mFocusMoveView == mLeftMoveView) {
                float dX = mContentLength - length;
                onChangeViewParams(length, true);
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onClipTouchMove(mClipInfo.getClipId(), Math.round(dX), 0, mContentLength, Math.round(dX));
                }
            } else if (mFocusMoveView == mRightMoveView) {
                float dX = length - mContentLength;
                onChangeViewParams(length, false);
                if (mItemTrackCallback != null) {
                    mItemTrackCallback.onClipTouchMove(mClipInfo.getClipId(), Math.round(dX), 1, mContentLength, Math.round(dX));
                }
            }
        }
    };

    /**
     * 超过半屏FrameScroller 自动滚动
     *
     * @param horizontallyState
     */
    private void onAutoScroll(HorizontallyState horizontallyState) {
        if (horizontallyState == mScrollState) {
            return;
        }
        if (mAutoScrollAnim != null) {
            mAutoScrollAnim.removeUpdateListener(mAnimUpdateListener);
            mAutoScrollAnim.cancel();
        }
        mScrollState = horizontallyState;
        //左滚
        if (horizontallyState == HorizontallyState.LEFT) {
            //左边按钮
            if (mFocusMoveView == mLeftMoveView) {
                long d = getMaxContentLength(true) - (mContentRightPosition - mContentLeftPosition);
                mAutoScrollAnim = ValueAnimator.ofFloat(mContentLength, getMaxContentLength(true));
                mAutoScrollAnim.setDuration((long) (d / mAutoScrollerSpeed));
                mAutoScrollAnim.start();
            } else if (mFocusMoveView == mRightMoveView) {
                //右边按钮
                mAutoScrollAnim = ValueAnimator.ofFloat(mContentLength, TrackConfig.getMinPx(mTimelineScale));
                mAutoScrollAnim.setDuration((long) (Math.abs(mContentLength - TrackConfig.getMinPx(mTimelineScale)) / mAutoScrollerSpeed));
            }
            if (mAutoScrollAnim != null) {
                mAutoScrollAnim.addUpdateListener(mAnimUpdateListener);
                mAutoScrollAnim.start();
            }
        } else if (horizontallyState == HorizontallyState.RIGHT) {
            //右滚
            //左边按钮
            if (mFocusMoveView == mLeftMoveView) {
                mAutoScrollAnim = ValueAnimator.ofFloat(mContentLength, TrackConfig.getMinPx(mTimelineScale));
                mAutoScrollAnim.setDuration((long) ((mContentLength - TrackConfig.getMinPx(mTimelineScale)) / mAutoScrollerSpeed));
            } else if (mFocusMoveView == mRightMoveView) {
                mAutoScrollAnim = ValueAnimator.ofFloat(mContentLength, getMaxContentLength(false));
                mAutoScrollAnim.setDuration((long) (Math.abs(getMaxContentLength(false) - mContentLength) / mAutoScrollerSpeed));
            }
            if (mAutoScrollAnim != null) {
                mAutoScrollAnim.addUpdateListener(mAnimUpdateListener);
                mAutoScrollAnim.start();
            }
        }
    }

    public void onTrackTouch() {
        if (mItemTrackCallback != null) {
            mItemTrackCallback.onTrackTouch(mClipInfo.getClipId(), isSelected());
        }
    }

    public BaseClipInfo getTrackClipInfo() {
        return mClipInfo;
    }

    public void setClipEnable(boolean enable) {
        mEnable = enable;
        if (!mEnable) {
            setSelected(false);
        }
    }
}