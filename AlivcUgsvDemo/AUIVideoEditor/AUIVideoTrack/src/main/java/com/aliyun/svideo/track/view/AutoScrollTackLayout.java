package com.aliyun.svideo.track.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.track.api.HorizontallyState;
import com.aliyun.svideo.track.api.TrackConfig;
import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.inc.OnClipDragListener;
import com.aliyun.svideo.track.util.Util;

import java.util.List;

/**
 * 轨道自动滚动布局
 */
public class AutoScrollTackLayout extends OtherTrackLayout implements OnClipDragListener {
    private int mScreenWidth;
    /**
     * 当前拖动View
     */
    BaseClipLayout mCurDragLayout;
    /**
     * 拖动前时间线进入时间
     */
    private long mOldTimelineIn;
    /**
     * 拖动前时间线出线时间
     */
    private long mOldTimelineOut;
    /**
     * 触发点X轴初始位置
     */
    float mStartPointX;
    /**
     * X轴最小偏移范围
     */
    int mMinTranslationX;
    /**
     * X轴最大偏移范围
     */
    int mMaxTranslationX;
    /**
     * 上次X轴拖动点位置
     */
    float mOldPointX;
    /**
     * X轴自动滚屏动画
     */
    private ValueAnimator mAutoScrollAnim;
    /**
     * 当前横向滚动状态
     */
    private HorizontallyState mAutoScrollState = HorizontallyState.NULL;
    /**
     * 时间轴当前滚动刻度
     */
    private int mCurScrollX = 0;
    /**
     * 时间轴最大滚动范围
     */
    private int mMaxScrollX = 0;
    /**
     * 自动滚动速度
     */
    private float mAutoScrollSpeed;
    /**
     * 当前X轴偏移
     */
    private float mPointTranslationX;
    /**
     * X轴滚动动画监听
     */
    private final ValueAnimator.AnimatorUpdateListener mAnimUpdateListener = new ValueAnimator.AnimatorUpdateListener() {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int scrollX = (int) animation.getAnimatedValue();
            if (mMultiTrackListener != null) {
                int scrollByX = scrollX - mCurScrollX;
                mPointTranslationX = mPointTranslationX + scrollByX;
                onTranslationX(mPointTranslationX);
                mMultiTrackListener.onScrollByX(scrollByX, false);
                mCurScrollX = scrollX;
            }
        }
    };

    /**
     * 当前Y轴滚动状态
     */
    private Runnable mAutoScrollYState;
    /**
     * Y轴自动滚动
     */
    private final Runnable mAutoScrollY = new Runnable() {
        @Override
        public void run() {
            Rect globalRect = new Rect();
            mExpendContentLayout.getGlobalVisibleRect(globalRect);
            Rect draggedRect = new Rect();
            mCurDragLayout.getGlobalVisibleRect(draggedRect);
            int scrollDy = TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START;
            if ((draggedRect.top - globalRect.top) <= TrackConfig.TRACK_LAYOUT_MARGIN_START && mMultiTrackListener.getParentScrollY() > 0) {
                //往上滚
                float newTranslationY = mCurDragLayout.getTranslationY() - scrollDy;
                if (newTranslationY > 0) {
                    mMultiTrackListener.onScrollByY(-scrollDy, false);
                    mCurDragLayout.setTranslationY(newTranslationY);
                    mExpendContentLayout.postDelayed(mAutoScrollY, 500);
                }
            } else if ((globalRect.bottom - draggedRect.bottom) <= TrackConfig.TRACK_LAYOUT_MARGIN_START &&
                    (mMultiTrackListener.getParentScrollY() + globalRect.height()) < mExpendContentLayout.getHeight()) {
                //往下滚
                int oldScrollY = mMultiTrackListener.getParentScrollY();
                float newTranslationY = mCurDragLayout.getTranslationY() + scrollDy;
                mMultiTrackListener.onScrollByY(scrollDy, false);
                if (oldScrollY != mMultiTrackListener.getParentScrollY()) {
                    mCurDragLayout.setTranslationY(newTranslationY);
                    mExpendContentLayout.postDelayed(mAutoScrollY, 500);
                }
            }
        }
    };

    public AutoScrollTackLayout(@NonNull Context context) {
        this(context, null);
    }

    public AutoScrollTackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScrollTackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScreenWidth = Util.getScreenWidth();
    }

    private boolean isNeedAutoScrollX(float pointX, float dx) {
        HorizontallyState horizontallyState;
        if (pointX >= mScreenWidth - TrackConfig.AUTO_SCROLL_WIDTH &&
                mCurScrollX < mMaxScrollX
                && dx > -1) {
            horizontallyState = HorizontallyState.RIGHT;
        } else if (pointX <= TrackConfig.AUTO_SCROLL_WIDTH &&
                mCurScrollX > 0
                && dx < 1) {
            horizontallyState = HorizontallyState.LEFT;
        } else {
            horizontallyState = HorizontallyState.NULL;
        }
        onAutoScrollX(horizontallyState);
        return horizontallyState != HorizontallyState.NULL;
    }

    private void onAutoScrollX(HorizontallyState horizontallyState) {
        if (horizontallyState == mAutoScrollState) {
            return;
        }
        if (mAutoScrollAnim != null) {
            mAutoScrollAnim.removeUpdateListener(mAnimUpdateListener);
            mAutoScrollAnim.cancel();
        }
        mAutoScrollState = horizontallyState;
        //左移
        if (horizontallyState == HorizontallyState.LEFT) {
            //左移，从当前位置移动到最小位置
            int length = mCurScrollX;
            mAutoScrollAnim = ValueAnimator.ofInt(mCurScrollX, 0);
            mAutoScrollAnim.setDuration((long) (length / mAutoScrollSpeed));
            mAutoScrollAnim.addUpdateListener(mAnimUpdateListener);
            mAutoScrollAnim.start();
        } else if (horizontallyState == HorizontallyState.RIGHT) {
            //右移，从当前位置移动到最小位置
            int length = mMaxScrollX - mCurScrollX;
            mAutoScrollAnim = ValueAnimator.ofInt(mCurScrollX, mMaxScrollX);
            mAutoScrollAnim.setDuration((long) (length / mAutoScrollSpeed));
            mAutoScrollAnim.addUpdateListener(mAnimUpdateListener);
            mAutoScrollAnim.start();
        }
    }

    /**
     * 根据根据偏移位置获取排序下标
     * @param dy
     * @return
     */
    private float getIndexByPointY(float dy) {
        if (dy < 0) {
            dy = 0;
        }
        float translationY = mMultiTrackListener.getParentScrollY() + dy;
        int maxNum = mExpendContentLayout.getHeight() / (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START) - 1;
        float num = (translationY / (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START));
        if (num < 0) {
            num = 0;
        } else if (num > maxNum) {
            num = maxNum;
        }
        return num;
    }

    /**
     * 根据下标获取偏移位置
     *
     * @param index
     * @return
     */
    public float getTranslationYByIndex(float index) {
        return index * (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START) + TrackConfig.TRACK_LAYOUT_MARGIN_START;
    }

    private void onDragSort(boolean dragSort, float pointX, float pointY) {
        if (dragSort) {
            this.mCurScrollX = mMultiTrackListener.getParentScrollX();
            this.mMaxScrollX = mMultiTrackListener.getParentMaxScrollX();
            mStartPointX = pointX;
            mOldPointX = pointX;
            mCurDragLayout = getTrackLayoutById(TrackConfig.draggedId);
            mOldTimelineIn = mCurDragLayout.getTrackClipInfo().getTimelineIn();
            mOldTimelineOut = mCurDragLayout.getTrackClipInfo().getTimelineOut();
            LayoutParams layoutParams = (LayoutParams) mCurDragLayout.getLayoutParams();
            layoutParams.topMargin = 0;
            mCurDragLayout.setLayoutParams(layoutParams);
            setSubClipFocus(mCurDragLayout.getTrackClipInfo().getClipId(), false);
            mExpendContentLayout.removeView(mCurDragLayout);
            mExpendContentLayout.addView(mCurDragLayout);
            //其它轨道容器距离顶部距离
            float dy = (pointY - Util.dp2px(54));
            mCurDragLayout.setTranslationY(getTranslationYByIndex((int) Math.floor(getIndexByPointY(dy))));
            mCurDragLayout.setAlpha(0.7f);
            mMinTranslationX = -Math.round(mCurDragLayout.getTrackClipInfo().getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
            mMaxTranslationX = Math.round((mTimelineDuration - mCurDragLayout.getTrackClipInfo().getTimelineOut()) * TrackConfig.getPxUnit(mTimelineScale));
        } else {
            mPointTranslationX = 0;
            onAutoScrollX(HorizontallyState.NULL);
            BaseClipInfo clipInfo = mCurDragLayout.getTrackClipInfo();
            long time = (long) (mCurDragLayout.getTranslationX() / TrackConfig.getPxUnit(mTimelineScale));

            int newIndex = (int) ((mCurDragLayout.getTranslationY() - TrackConfig.TRACK_LAYOUT_MARGIN_START) / (TrackConfig.SUB_CLIP_HEIGHT + TrackConfig.TRACK_LAYOUT_MARGIN_START));
            long newTimelineIn = clipInfo.getTimelineIn() + time;
            long newTimelineOut = clipInfo.getTimelineOut() + time;
            if (newTimelineIn < 0) {
                newTimelineIn = 0;
                newTimelineOut = clipInfo.getOut() - clipInfo.getIn();
            } else if (newTimelineOut > mTimelineDuration) {
                newTimelineOut = mTimelineDuration;
                newTimelineIn = newTimelineOut - (clipInfo.getOut() - clipInfo.getIn());
            }
            int oldIndex = clipInfo.getIndex();
            List<BaseClipLayout> list = getLayoutListByIndex(newIndex);
            boolean isOK = true;
            //遍历是否有交叉
            for (BaseClipLayout item : list) {
                if (item == mCurDragLayout) {
                    continue;
                }
                if (isOverlap(newTimelineIn, newTimelineOut, item.getTrackClipInfo().getTimelineIn(), item.getTrackClipInfo().getTimelineOut())) {
                    isOK = false;
                    break;
                }
            }
            if (isOK) {
                clipInfo.setTimelineIn(newTimelineIn);
                clipInfo.setTimelineOut(newTimelineOut);
                clipInfo.setIndex(newIndex);
            }
            if ((mOldTimelineIn != clipInfo.getTimelineIn()) && (mOldTimelineOut != clipInfo.getTimelineOut())) {
                mMultiTrackListener.onUpdateClipTime(clipInfo.getClipType(), clipInfo.getClipId(), clipInfo.getIn(), clipInfo.getOut(), clipInfo.getTimelineIn(), clipInfo.getTimelineOut());
                updateTimelineLimit(clipInfo.getIndex());
                if (oldIndex != clipInfo.getIndex()) {
                    updateTimelineLimit(oldIndex);
                }
            }
            LayoutParams layoutParams = (LayoutParams) mCurDragLayout.getLayoutParams();
            layoutParams.topMargin = (int) getTranslationYByIndex(clipInfo.getIndex());
            layoutParams.leftMargin = Math.round(clipInfo.getTimelineIn() * TrackConfig.getPxUnit(mTimelineScale));
            mCurDragLayout.setLayoutParams(layoutParams);
            mCurDragLayout.setTranslationX(0);
            mCurDragLayout.setTranslationY(0);
            mCurDragLayout.setAlpha(1);
            onClipSort();
            resizeHeight();
        }
    }

    private boolean isOverlap(long startTime1, long endTime1, long startTime2, long endTime2) {
        return !(endTime1 <= startTime2 || startTime1 >= endTime2);
    }

    private void onTranslationX(float translationX) {
        if (translationX < mMinTranslationX) {
            translationX = mMinTranslationX;
        } else if (translationX > mMaxTranslationX) {
            translationX = mMaxTranslationX;
        }
        mCurDragLayout.setTranslationX(translationX);
    }

    @Override
    public void onDragStart(float pointX, float pointY) {
        onDragSort(true, pointX, pointY);
    }

    @Override
    public void onDragEnd(float pointX, float pointY) {
        onDragSort(false, pointX, pointY);
    }

    @Override
    public void onDragLocationChange(float pointX, float pointY) {
        Rect globalRect = new Rect();
        mExpendContentLayout.getGlobalVisibleRect(globalRect);
        float dy = (pointY - Util.dp2px(54));
        if (dy > globalRect.height()) {
            dy = globalRect.height();
        }
        float translationY = getTranslationYByIndex((int) Math.floor(getIndexByPointY(dy)));
        mCurDragLayout.setTranslationY(translationY);

        if (mCurDragLayout.getTranslationY() < mMultiTrackListener.getParentScrollY() && mMultiTrackListener != null) {
            int scrollDy = (int) (mCurDragLayout.getTranslationY() - mMultiTrackListener.getParentScrollY());
            mMultiTrackListener.onScrollByY(scrollDy, false);
        } else if ((mCurDragLayout.getTranslationY() + mCurDragLayout.getHeight()) > mMultiTrackListener.getParentScrollY() + globalRect.height()) {
            int scrollDy = (int) ((mCurDragLayout.getTranslationY() + mCurDragLayout.getHeight()) - (mMultiTrackListener.getParentScrollY() + globalRect.height()));
            mMultiTrackListener.onScrollByY(scrollDy, false);
        }

        Rect draggedRect = new Rect();
        mCurDragLayout.getGlobalVisibleRect(draggedRect);
        if ((draggedRect.top - globalRect.top) <= TrackConfig.TRACK_LAYOUT_MARGIN_START && mMultiTrackListener.getParentScrollY() > 0) {
            if (mAutoScrollYState == null) {
                mAutoScrollYState = mAutoScrollY;
                mExpendContentLayout.postDelayed(mAutoScrollY, 500);
            }
        } else if ((globalRect.bottom - draggedRect.bottom) <= TrackConfig.TRACK_LAYOUT_MARGIN_START && (mMultiTrackListener.getParentScrollY() + globalRect.height()) < mExpendContentLayout.getHeight()) {
            if (mAutoScrollYState == null) {
                mAutoScrollYState = mAutoScrollY;
                mExpendContentLayout.postDelayed(mAutoScrollY, 500);
            }
        } else {
            if (mAutoScrollYState != null) {
                mExpendContentLayout.removeCallbacks(mAutoScrollY);
                mAutoScrollYState = null;
            }
        }
        mAutoScrollSpeed = TrackConfig.autoScrollSpeed(pointX, mScreenWidth);
        float dx = pointX - mOldPointX;
        mOldPointX = pointX;
        boolean isNeedAutoScrollX = isNeedAutoScrollX(pointX, dx);
        if (!isNeedAutoScrollX) {
            mPointTranslationX = mPointTranslationX + dx;
            onTranslationX(mPointTranslationX);
        }
    }
}
