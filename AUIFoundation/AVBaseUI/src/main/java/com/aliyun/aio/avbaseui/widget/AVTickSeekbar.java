package com.aliyun.aio.avbaseui.widget;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import androidx.annotation.Nullable;
import com.aliyun.aio.avbaseui.R;

/**
 * @author geekeraven
 */
public class AVTickSeekbar extends View {

    public static class Item {
        public String tickTitle;
        public float progress;
        public Item(String tickTitle, float progress) {
            this.tickTitle = tickTitle;
            this.progress = progress;
        }
    }

    private int mThumbTickCount = 5;
    private int mTrackTickCount = 10;
    private int mTrackColor;
    private Drawable mThumbDrawable;
    private int mMeasureWidth;
    private int mMeasureHeight;
    private Paint mTrackTickPaint;
    private Paint mThumbTickPaint;
    private Paint mThumbPaint;
    private TextPaint mTextPaint;
    private TextPaint mEdgeTextPaint;
    private float mDensity;
    private int mThumbTickWidth;
    private int mTrackTickWidth;
    private int mThumbRadius;
    private List<Rect> mThumbTickRects = new ArrayList<>();
    private List<Rect> mTrackTickRects = new ArrayList<>();
    private int mThumbPosition = 0;
    private List<Item> mItemList = new ArrayList<>();
    private int mTextTopMargin;
    private int mTextHeight;
    private int mLastPosition;

    private OnTickChangedListener mOnTickChangedListener;

    public AVTickSeekbar(Context context) {
        this(context, null);
    }

    public AVTickSeekbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AVTickSeekbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mDensity = context.getResources().getDisplayMetrics().density;
        mThumbTickWidth = (int)(mDensity * 2);
        mTrackTickWidth = (int)(mDensity * 1);
        mThumbRadius = (int)(mDensity * 6) + 1;
        mTextTopMargin = (int)(mDensity * 8);
        mTextHeight = (int)(mDensity * 18);
        initTrackTickPaint(context);
        initThumbTickPaint(context);
        initThumbPaint(context);
        initTextPaint(context);
    }

    private void initTrackTickPaint(Context context) {
        mTrackTickPaint = new Paint();
        mTrackTickPaint.setAntiAlias(true);
        mTrackTickPaint.setStyle(Style.FILL);
        mTrackTickPaint.setColor(context.getResources().getColor(R.color.border_medium));
    }

    private void initThumbTickPaint(Context context) {
        mThumbTickPaint = new Paint();
        mThumbTickPaint.setAntiAlias(true);
        mThumbTickPaint.setStyle(Style.FILL);
        mThumbTickPaint.setColor(context.getResources().getColor(R.color.fill_infrared));
    }

    private void initThumbPaint(Context context) {
        mThumbPaint = new Paint();
        mThumbPaint.setAntiAlias(true);
        mThumbPaint.setStyle(Style.FILL);
        mThumbPaint.setColor(context.getResources().getColor(R.color.fill_infrared));
    }

    private void initTextPaint(Context context) {
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Style.FILL);
        mTextPaint.setTextAlign(Align.LEFT);
        mTextPaint.setTextSize(mDensity * 12);
        mTextPaint.setColor(context.getResources().getColor(R.color.text_strong));

        mEdgeTextPaint = new TextPaint();
        mEdgeTextPaint.setAntiAlias(true);
        mEdgeTextPaint.setStyle(Style.FILL);
        mEdgeTextPaint.setTextAlign(Align.RIGHT);
        mEdgeTextPaint.setTextSize(mDensity * 12);
        mEdgeTextPaint.setColor(context.getResources().getColor(R.color.text_strong));
    }

    public void setDatas(List<Item> items) {
        mItemList.clear();
        mItemList.addAll(items);
        mThumbTickCount = mItemList.size();
        requestLayout();
        postInvalidate();
    }

    public void setOnTickChangedListener(
        OnTickChangedListener onTickChangedListener) {
        mOnTickChangedListener = onTickChangedListener;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureHeight = Math.round(mDensity * 14 + getPaddingTop() + getPaddingBottom() + mTextTopMargin + mTextHeight);
        mMeasureWidth = resolveSize((int)(mDensity * 200), widthMeasureSpec);
        setMeasuredDimension(mMeasureWidth, mMeasureHeight);
        initTickData();
    }

    private void initTickData() {
        mThumbTickRects.clear();
        mTrackTickRects.clear();
        int tickCount = (mThumbTickCount - 1)* mTrackTickCount;
        int width = mMeasureWidth - mThumbRadius * 2 - getPaddingLeft() - getPaddingRight();
        int gap = width / tickCount;
        Rect rect = new Rect();
        int trackTickTop = (int)(mMeasureHeight / 2 - mDensity * 6 / 2);
        int thumbTickTop = (int)(mMeasureHeight / 2 - mDensity * 12 / 2);
        int drawLeft = getPaddingLeft() + mThumbRadius;
        for (int i = 0; i <= tickCount; i++) {
            if (i % mTrackTickCount == 0) {
                rect.left = i * gap + drawLeft;
                rect.right = (int)(rect.left + mThumbTickWidth);
                rect.top = thumbTickTop;
                rect.bottom = (int)(rect.top + mDensity * 12);
                mTrackTickRects.add(new Rect(rect));
                mThumbTickRects.add(new Rect(rect));
            } else {
                rect.left = i * gap + drawLeft;
                rect.right = (int)(rect.left + mTrackTickWidth);
                rect.top = trackTickTop;
                rect.bottom = (int)(rect.top + mDensity * 6);
                mTrackTickRects.add(new Rect(rect));
            }
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        ViewParent parent = getParent();
        if (parent == null) {
            return super.dispatchTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                parent.requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                parent.requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                float mX = event.getX();
                refreshTickBar(event);
                return true;
                //break;
            case MotionEvent.ACTION_MOVE:
                refreshTickBar(event);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                refreshTickBar(event);
                if (mOnTickChangedListener != null && mLastPosition != mThumbPosition) {
                    mLastPosition = mThumbPosition;
                    mOnTickChangedListener.onTickChanged(mThumbPosition, mItemList.get(mThumbPosition));
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void refreshTickBar(MotionEvent event) {
        if (mThumbTickCount < 2) {
            //不够两个tick，不需要刷新了
            return;
        }
        int radius = (mThumbTickRects.get(1).left - mThumbTickRects.get(0).left) / 2;
        float x = event.getX();
        for(int i = 0 ; i < mThumbTickRects.size(); i++) {
            Rect rect = mThumbTickRects.get(i);
            int center = rect.left + (rect.right - rect.left) / 2;
            int left = center - radius;
            int right = center + radius;
            if (x > left && x <= right) {
                mThumbPosition = i;
                break;
            }
        }
        invalidate();
    }

    public synchronized void setThumbPosition(int position) {
        mThumbPosition = position;
        mLastPosition = position;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTrack(canvas);
        drawThumb(canvas);
        drawText(canvas);
    }

    private void drawTrack(Canvas canvas) {
        for (int i = 0; i < mTrackTickRects.size(); i++) {
            if (i % mTrackTickCount == 0) {
                canvas.drawRect(mTrackTickRects.get(i), mThumbTickPaint);
            } else {
                canvas.drawRect(mTrackTickRects.get(i), mTrackTickPaint);
            }
        }
    }

    private void drawThumb(Canvas canvas) {
        Rect rect = mThumbTickRects.get(mThumbPosition);
        int centerX = rect.left + (rect.right - rect.left) / 2;
        int centerY = rect.top + (rect.bottom - rect.top) / 2;
        canvas.drawCircle(centerX, centerY, mThumbRadius, mThumbPaint);
    }

    private void drawText(Canvas canvas) {
        for(int i = 0; i < mItemList.size(); i++) {
            Rect rect = mThumbTickRects.get(i);
            Item item = mItemList.get(i);
            int left =  rect.left - mThumbRadius;
            Paint.FontMetrics fontMetrics=mTextPaint.getFontMetrics();
            float distance=(fontMetrics.bottom - fontMetrics.top)/2 - fontMetrics.bottom;
            int top = (int)(rect.bottom + mTextTopMargin + distance);
            if (i < mItemList.size() - 1) {
                canvas.drawText(item.tickTitle, left, top, mTextPaint);
            } else {
                int right = rect.right + mThumbRadius;
                canvas.drawText(item.tickTitle, right , top, mEdgeTextPaint);
            }
        }
    }

    public interface OnTickChangedListener {
        void onTickChanged(int index, Item item);
    }
}
