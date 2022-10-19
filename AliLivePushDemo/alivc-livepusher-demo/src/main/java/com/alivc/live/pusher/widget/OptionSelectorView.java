package com.alivc.live.pusher.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Scroller;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import java.util.List;

public class OptionSelectorView extends View implements Runnable {
    private static final String TAG = "OptionSelectorView";
    private final float DEFAULT_FRICTION = 0.02f;
    private final int DEFAULT_ITEM_HEIGHT = (int) dip2px(39);
    private final int DEFAULT_DIVIDER_COLOR = Color.parseColor("#3A3D48");
    private final int DEFAULT_VISIBLE_ITEM_NUM = 6;
    private final float mHalfShowHeight = DEFAULT_VISIBLE_ITEM_NUM * DEFAULT_ITEM_HEIGHT / 2.0f;
    private final int DEFAULT_START_ITEM_COLOR = Color.parseColor("#FCFCFD");
    private final int DEFAULT_END_TEXT_COLOR = Color.parseColor("#747A8C");
    private final float mMaxTextSize = sp2px(21);
    private final float mMinTextSize = sp2px(13);

    private List<String> mData;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private long mClickTimeout;
    private VelocityTracker mVelocityTracker;
    private Scroller mScroller;
    private int mMinScrollY;
    private int mMaxScrollY;
    private int mScrollOffsetY;
    private boolean mIsDragging = false;
    private boolean mIsFlingScroll = false;
    private float mTouchY;
    private long mTouchDownTime;
    private int mCurrentScrollPosition;
    private int mSelectedItemPosition = 0;
    private OnItemSelectedListener mOnItemSelectedListener;
    private Rect mContentRect;
    protected Rect mPanelRect;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public OptionSelectorView(Context context) {
        super(context);
        initView(context);
    }

    public OptionSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public OptionSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        mClickTimeout = ViewConfiguration.getTapTimeout();
        mContentRect = new Rect(0, 0, 0, 0);
        mPanelRect = new Rect(0, 0, 0, 0);
        mScroller = new Scroller(context);
        mScroller.setFriction(DEFAULT_FRICTION);
        mMinScrollY = 0;
    }

    public void setData(List<String> data, int selectPosition) {
        mScroller.forceFinished(true);
        mData = data;
        mCurrentScrollPosition = mSelectedItemPosition = selectPosition;
        mScrollOffsetY = DEFAULT_ITEM_HEIGHT * selectPosition;
        mMaxScrollY = (mData.size() - 1) * DEFAULT_ITEM_HEIGHT;
        invalidateAndCheckItemChange();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        reSetRect();
    }

    private void reSetRect() {
        mContentRect.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        mPanelRect.set(0, 0, getWidth(), getHeight());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTracker(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                mIsDragging = true;
                mTouchY = event.getY();
                mTouchDownTime = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                float currentY = event.getY();
                float dy = currentY - mTouchY;
                if (Math.abs(dy) < 1) {
                    break;
                }
                scroll((int) -dy);
                mTouchY = currentY;
                break;
            case MotionEvent.ACTION_UP:
                mIsDragging = false;
                mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                float velocityY = mVelocityTracker.getYVelocity();
                if (Math.abs(velocityY) > mMinimumVelocity) {
                    mScroller.forceFinished(true);
                    mIsFlingScroll = true;
                    mScroller.fling(0, mScrollOffsetY, 0, (int) -velocityY, 0, 0,
                            mMinScrollY, mMaxScrollY);
                    fixBounceEffect();
                } else {
                    //点击位置和中心点的y轴距离
                    int clickOffset = 0;
                    if (System.currentTimeMillis() - mTouchDownTime <= mClickTimeout) {
                        clickOffset = (int) (event.getY() - getHeight() / 2);
                    }
                    int scrollDistance = clickOffset + calculateDistanceNeedToScroll((mScrollOffsetY + clickOffset) % DEFAULT_ITEM_HEIGHT);

                    if (scrollDistance <= 0) {
                        scrollDistance = Math.max(scrollDistance, -mScrollOffsetY);
                    } else {
                        scrollDistance = Math.min(scrollDistance, mMaxScrollY - mScrollOffsetY);
                    }
                    mScroller.startScroll(0, mScrollOffsetY, 0, scrollDistance);

                }
                invalidateAndCheckItemChange();
                ViewCompat.postOnAnimation(this, this);
                recycleVelocityTracker();
                break;
            case MotionEvent.ACTION_CANCEL:
                recycleVelocityTracker();
                break;
            default:
                break;

        }

        return true;
    }


    @Override
    public void run() {
        //停止滚动后更新状态
        if (mScroller.isFinished() && !mIsDragging && !mIsFlingScroll) {
            int currentItemPosition = getCurrentPosition();
            if (currentItemPosition == mSelectedItemPosition) {
                return;
            }

            mCurrentScrollPosition = mSelectedItemPosition = currentItemPosition;

            //选中监听回调
            if (mOnItemSelectedListener != null && mData != null && mData.size() > mSelectedItemPosition) {
                mOnItemSelectedListener.onItemSelected(mData.get(mSelectedItemPosition), mSelectedItemPosition);
            }
        }

        if (mScroller.computeScrollOffset()) {
            mScrollOffsetY = mScroller.getCurrY();
            invalidateAndCheckItemChange();
            ViewCompat.postOnAnimation(this, this);
        } else if (mIsFlingScroll) {
            //快速滚动后调整选中位置到中心
            mIsFlingScroll = false;
            mScroller.startScroll(0, mScrollOffsetY, 0, calculateDistanceNeedToScroll(mScrollOffsetY % DEFAULT_ITEM_HEIGHT));
            invalidateAndCheckItemChange();
            ViewCompat.postOnAnimation(this, this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mContentRect.width() == 0) {
            reSetRect();
        }
        drawDivider(canvas);
        //已滚动item数
        int scrolledItem;
        //没有滚动完的item偏移值
        int scrolledOffset;
        scrolledItem = mScrollOffsetY / DEFAULT_ITEM_HEIGHT;
        scrolledOffset = mScrollOffsetY % DEFAULT_ITEM_HEIGHT;

        int half = (DEFAULT_VISIBLE_ITEM_NUM + 1) / 2;
        //绘制的第一个选项下标
        int firstItemIndex = scrolledItem - half + (mScrollOffsetY > 0 ? 1 : 0);
        //绘制的最后一个选项下标
        int lastItemIndex = scrolledItem + half;

        for (int i = firstItemIndex; i <= lastItemIndex; i++) {
            drawItem(canvas, i, scrolledOffset);
        }

    }

    protected void drawItem(Canvas canvas, int index, int scrolledOffset) {
        String text = getTextByIndex(index);
        if (text == null) {
            return;
        }
        //item 与中间项的偏移
        int item2CenterOffsetY;
        item2CenterOffsetY = (index - mScrollOffsetY / DEFAULT_ITEM_HEIGHT) * DEFAULT_ITEM_HEIGHT - scrolledOffset;
        float textSize = mMaxTextSize - ((mMaxTextSize - mMinTextSize) / mHalfShowHeight * Math.abs(item2CenterOffsetY));
        mPaint.setTextSize((float) Math.floor(textSize));
        int textColor = evaluate(Math.abs(item2CenterOffsetY) * 1.0f / mHalfShowHeight, DEFAULT_START_ITEM_COLOR, DEFAULT_END_TEXT_COLOR);
        mPaint.setColor(textColor);
        int textHeightHalf = (int) ((mPaint.getFontMetrics().descent + mPaint.getFontMetrics().ascent) / 2);

        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.clipRect(mContentRect);
        canvas.drawText(text, mContentRect.centerX(), mContentRect.centerY() + item2CenterOffsetY - textHeightHalf, mPaint);
    }


    private void drawDivider(Canvas canvas) {
        //没有设置分割线颜色时跳过绘制
        if (DEFAULT_DIVIDER_COLOR == Color.TRANSPARENT) {
            return;
        }
        mPaint.setColor(DEFAULT_DIVIDER_COLOR);
        mPaint.setStrokeWidth(1);
        canvas.drawLine(mContentRect.left, mContentRect.centerY() - (DEFAULT_ITEM_HEIGHT >> 1), mContentRect.right, mContentRect.centerY() - (DEFAULT_ITEM_HEIGHT >> 1), mPaint);
        canvas.drawLine(mContentRect.left, mContentRect.centerY() + (DEFAULT_ITEM_HEIGHT >> 1), mContentRect.right, mContentRect.centerY() + (DEFAULT_ITEM_HEIGHT >> 1), mPaint);
    }

    public int evaluate(float fraction, int startValue, int endValue) {
        float startA = ((startValue >> 24) & 0xff) / 255.0f;
        float startR = ((startValue >> 16) & 0xff) / 255.0f;
        float startG = ((startValue >> 8) & 0xff) / 255.0f;
        float startB = (startValue & 0xff) / 255.0f;

        float endA = ((endValue >> 24) & 0xff) / 255.0f;
        float endR = ((endValue >> 16) & 0xff) / 255.0f;
        float endG = ((endValue >> 8) & 0xff) / 255.0f;
        float endB = (endValue & 0xff) / 255.0f;

        // convert from sRGB to linear
        startR = (float) Math.pow(startR, 2.2);
        startG = (float) Math.pow(startG, 2.2);
        startB = (float) Math.pow(startB, 2.2);

        endR = (float) Math.pow(endR, 2.2);
        endG = (float) Math.pow(endG, 2.2);
        endB = (float) Math.pow(endB, 2.2);

        // compute the interpolated color in linear space
        float a = startA + fraction * (endA - startA);
        float r = startR + fraction * (endR - startR);
        float g = startG + fraction * (endG - startG);
        float b = startB + fraction * (endB - startB);

        // convert back to sRGB in the [0..255] range
        a = a * 255.0f;
        r = (float) Math.pow(r, 1.0 / 2.2) * 255.0f;
        g = (float) Math.pow(g, 1.0 / 2.2) * 255.0f;
        b = (float) Math.pow(b, 1.0 / 2.2) * 255.0f;

        return Math.round(a) << 24 | Math.round(r) << 16 | Math.round(g) << 8 | Math.round(b);
    }

    private String getTextByIndex(int index) {


        if (mData != null && index >= 0 && index < mData.size()) {
            return mData.get(index);
        }
        return null;
    }


    private void initVelocityTracker(MotionEvent event) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    private void recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    private void scroll(int distance) {
        mScrollOffsetY += distance;
        mScrollOffsetY = Math.min(mMaxScrollY, Math.max(mMinScrollY, mScrollOffsetY));
        invalidateAndCheckItemChange();
    }

    private void invalidateAndCheckItemChange() {
        invalidate();
        int currentPosition = getCurrentPosition();
        if (mCurrentScrollPosition != currentPosition) {
            mCurrentScrollPosition = currentPosition;
        }
    }

    private int getCurrentPosition() {
        int itemPosition;
        if (mScrollOffsetY < 0) {
            itemPosition = (mScrollOffsetY - DEFAULT_ITEM_HEIGHT / 2) / DEFAULT_ITEM_HEIGHT;
        } else {
            itemPosition = (mScrollOffsetY + DEFAULT_ITEM_HEIGHT / 2) / DEFAULT_ITEM_HEIGHT;
        }
        int currentPosition = itemPosition % mData.size();
        if (currentPosition < 0) {
            currentPosition += mData.size();
        }

        return currentPosition;
    }

    private int calculateDistanceNeedToScroll(int offset) {
        //超过item高度一半，需要滚动一个item
        if (Math.abs(offset) > DEFAULT_ITEM_HEIGHT / 2) {
            if (mScrollOffsetY < 0) {
                return -DEFAULT_ITEM_HEIGHT - offset;
            } else {
                return DEFAULT_ITEM_HEIGHT - offset;
            }
        }
        //当前item回到中心距离
        else {
            return -offset;
        }
    }

    private void fixBounceEffect() {
        //修正快速滑动最后停止位置，没有回弹效果
        int stopOffset = mScroller.getFinalY();
        int itemScrollOffset = Math.abs(stopOffset % DEFAULT_ITEM_HEIGHT);

        //如果滚动偏移超过半个item高度，停止位置加一item高度
        if (itemScrollOffset > DEFAULT_ITEM_HEIGHT >> 1) {
            if (stopOffset < 0) {
                stopOffset = stopOffset / DEFAULT_ITEM_HEIGHT * DEFAULT_ITEM_HEIGHT - DEFAULT_ITEM_HEIGHT;
            } else {
                stopOffset = stopOffset / DEFAULT_ITEM_HEIGHT * DEFAULT_ITEM_HEIGHT + DEFAULT_ITEM_HEIGHT;
            }
        } else {
            stopOffset = stopOffset / DEFAULT_ITEM_HEIGHT * DEFAULT_ITEM_HEIGHT;

        }
        mScroller.setFinalY(stopOffset);
    }

    private void draw3DItemText(Canvas canvas, String text, int item2CenterOffsetY, int textHeightHalf) {
        canvas.save();


    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelectedListener = onItemSelectedListener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(String data, int position);
    }


    /**
     * dp宽度转像素值
     */
    public float dip2px(float dpValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * 文字大小转像素值
     */
    public float sp2px(float spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, Resources.getSystem().getDisplayMetrics());
    }
}
