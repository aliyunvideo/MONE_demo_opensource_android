/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.aliyun.svideo.base.R;

public class FanProgressBar extends View {
    private static final float DEFAULT_MAX_PROGRESS = 100f;
    private static final int DEFAULT_INTERNAL_RADIUS = 10;
    private static final int DEFAULT_OUT_RADIUS = 10;
    private static final float DEFAULT_START_ANGLE = -90f;
    private static final float DEFAULT_INTERNAL_ALPHA = 1f;
    private static final float DEFAULT_OUT_ALPHA = 1f;
    private static final int MATCH_PARENT = -1;
    private static final int DIRECTION_CLOCKWISE = 1;
    private static final int DIRECTION_COUNTERCLOCKWISE = -1;
    private static final int EMPTY_INIT = 0;
    private static final int FILL_INIT = 1;


    private float mMaxProgress = DEFAULT_MAX_PROGRESS;
    private float mProgress;
    private int mInternalRadius = DEFAULT_INTERNAL_RADIUS;
    private int mOutRadius = DEFAULT_OUT_RADIUS;
    private int mInternalBackgroundColor = Color.WHITE;
    private int mOutBackgroundColor = Color.WHITE;
    private float mStartAngle = DEFAULT_START_ANGLE;
    private int mInternalAlpha = (int) (255 * DEFAULT_INTERNAL_ALPHA);
    private int mOutAlpha = (int) (255 * DEFAULT_OUT_ALPHA);
    private Paint mInternalPaint;
    private Paint mOutPaint;
    private float mCenterX = mOutRadius;
    private float mCenterY = mOutRadius;
    private int mOutDirection = DIRECTION_CLOCKWISE;
    private int mInternalDirection = DIRECTION_CLOCKWISE;
    private int mInitStyle;
    private int offsetX, offsetY;


    public FanProgressBar(Context context) {
        super(context);
        initPaint();
    }

    public FanProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuViewFanProgressBar);
        try {
            mMaxProgress = typedArray.getFloat(R.styleable.QuViewFanProgressBar_circleMaxProgress, DEFAULT_MAX_PROGRESS);
            mProgress = typedArray.getFloat(R.styleable.QuViewFanProgressBar_circleProgress, 0);
            mInternalRadius = typedArray.getDimensionPixelSize(R.styleable.QuViewFanProgressBar_internalRadius, DEFAULT_INTERNAL_RADIUS);
            mOutRadius = typedArray.getDimensionPixelSize(R.styleable.QuViewFanProgressBar_outRadius, DEFAULT_OUT_RADIUS);
            mInternalBackgroundColor = typedArray.getColor(R.styleable.QuViewFanProgressBar_internalBackgroundColor, Color.WHITE);
            mOutBackgroundColor = typedArray.getColor(R.styleable.QuViewFanProgressBar_outBackgroundColor, Color.WHITE);
            mStartAngle = typedArray.getFloat(R.styleable.QuViewFanProgressBar_startAngle, DEFAULT_START_ANGLE);
            mInternalAlpha = (int) (255 * typedArray.getFloat(R.styleable.QuViewFanProgressBar_internalAlpha, DEFAULT_INTERNAL_ALPHA));
            mOutAlpha = (int) (255 * typedArray.getFloat(R.styleable.QuViewFanProgressBar_outAlpha, DEFAULT_OUT_ALPHA));
            mCenterX = typedArray.getFloat(R.styleable.QuViewFanProgressBar_centerX, Integer.MAX_VALUE);
            mCenterY = typedArray.getFloat(R.styleable.QuViewFanProgressBar_centerY, Integer.MAX_VALUE);
            mOutDirection = typedArray.getInt(R.styleable.QuViewFanProgressBar_outDirection, DIRECTION_CLOCKWISE);
            mInternalDirection = typedArray.getInt(R.styleable.QuViewFanProgressBar_internalDirection, DIRECTION_CLOCKWISE);
            mInitStyle = typedArray.getInt(R.styleable.QuViewFanProgressBar_initStyle, EMPTY_INIT);
        } finally {
            typedArray.recycle();
        }
        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mInternalRadius == MATCH_PARENT) {
            mInternalRadius = getMeasuredWidth() / 2;
        }
        if (mOutRadius == MATCH_PARENT) {
            mOutRadius = getMeasuredWidth() / 2;
        }
        if (mCenterX == Integer.MAX_VALUE) {
            mCenterX = mOutRadius;
        }
        if (mCenterY == Integer.MAX_VALUE) {
            mCenterY = mOutRadius;
        }
    }

    public FanProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.QuViewFanProgressBar);
        try {
            mMaxProgress = typedArray.getFloat(R.styleable.QuViewFanProgressBar_circleMaxProgress, DEFAULT_MAX_PROGRESS);
            mProgress = typedArray.getFloat(R.styleable.QuViewFanProgressBar_circleProgress, 0);
            mInternalRadius = typedArray.getDimensionPixelSize(R.styleable.QuViewFanProgressBar_internalRadius, DEFAULT_INTERNAL_RADIUS);
            mOutRadius = typedArray.getDimensionPixelSize(R.styleable.QuViewFanProgressBar_outRadius, DEFAULT_OUT_RADIUS);
            mInternalBackgroundColor = typedArray.getColor(R.styleable.QuViewFanProgressBar_internalBackgroundColor, Color.WHITE);
            mOutBackgroundColor = typedArray.getColor(R.styleable.QuViewFanProgressBar_outBackgroundColor, Color.WHITE);
            mStartAngle = typedArray.getFloat(R.styleable.QuViewFanProgressBar_startAngle, DEFAULT_START_ANGLE);
            mInternalAlpha = (int) (255 * typedArray.getFloat(R.styleable.QuViewFanProgressBar_internalAlpha, DEFAULT_INTERNAL_ALPHA));
            mOutAlpha = (int) (255 * typedArray.getFloat(R.styleable.QuViewFanProgressBar_outAlpha, DEFAULT_OUT_ALPHA));
            mCenterX = typedArray.getFloat(R.styleable.QuViewFanProgressBar_centerX, Integer.MAX_VALUE);
            mCenterY = typedArray.getFloat(R.styleable.QuViewFanProgressBar_centerY, Integer.MAX_VALUE);
            mOutDirection = typedArray.getInt(R.styleable.QuViewFanProgressBar_outDirection, DIRECTION_CLOCKWISE);
            mInternalDirection = typedArray.getInt(R.styleable.QuViewFanProgressBar_internalDirection, DIRECTION_CLOCKWISE);
        } finally {
            typedArray.recycle();
        }
        initPaint();
    }

    public void setProgress(float progress) {
        if (mProgress != progress) {
            this.mProgress = progress;
            if (mProgress > mMaxProgress) {
                mProgress = mMaxProgress;
            } else if (mProgress == 0) {
                mProgress = 0;
            }
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float progress = mInitStyle == EMPTY_INIT ? mProgress : (mMaxProgress - mProgress);
        if (progress == mMaxProgress) {
            if (mOutRadius > 0) {
                canvas.drawCircle(mCenterX, mCenterY, mOutRadius, mOutPaint);
            }
            if (mInternalRadius > 0) {
                canvas.drawCircle(mCenterX, mCenterY, mInternalRadius, mInternalPaint);
            }
        } else if (progress == 0) {
            canvas.drawColor(Color.TRANSPARENT);
        } else {

            float sweepAngle = -(360.0f * progress / mMaxProgress);

            if (sweepAngle != 0) {
                float outSweepAngle = (mOutDirection == DIRECTION_CLOCKWISE ? sweepAngle : -sweepAngle);
                if (mOutRadius > 0) {
                    RectF ovalOut = new RectF();
                    ovalOut.left = mCenterX + offsetX - mOutRadius ;
                    ovalOut.right = mCenterX + offsetX + mOutRadius ;
                    ovalOut.top = mCenterY + offsetY - mOutRadius ;
                    ovalOut.bottom = mCenterY + offsetY + mOutRadius ;
                    canvas.drawArc(ovalOut, mStartAngle, outSweepAngle, false, mOutPaint);
                }

                float internalSweepAngle = (mInternalDirection == DIRECTION_CLOCKWISE ? sweepAngle : -sweepAngle);
                if (mInternalRadius > 0) {
                    RectF ovalInternal = new RectF();
                    ovalInternal.left = mCenterX - mInternalRadius;
                    ovalInternal.right = mCenterX + mInternalRadius;
                    ovalInternal.top = mCenterY - mInternalRadius;
                    ovalInternal.bottom = mCenterY + mInternalRadius;
                    canvas.drawArc(ovalInternal, mStartAngle, internalSweepAngle, true, mInternalPaint);
                }
            }
        }
    }
    public void setOffset(int x, int y) {
        offsetX = x;
        offsetY = y;
    }

    public void setOutStrokeWidth(int outStrokeWidth) {
        if (mOutPaint != null) {
            mOutPaint.setStrokeWidth(outStrokeWidth);
        }
    }
    private void initPaint() {
        mInternalPaint = new Paint();
        mInternalPaint.setAlpha(mInternalAlpha);
        mInternalPaint.setColor(mInternalBackgroundColor);
        mInternalPaint.setStyle(Paint.Style.FILL);
        mInternalPaint.setAntiAlias(true);

        mOutPaint = new Paint();
        mOutPaint.setColor(mOutBackgroundColor);
        mOutPaint.setStyle(Paint.Style.STROKE);
        mOutPaint.setAntiAlias(true);
        mOutPaint.setAlpha(mOutAlpha);
    }

//    private float calculateSweepAngle() {
//        float sweepAngle = 0;
//        if(mInitStyle == EMPTY_INIT) {
//
//        }else {
//            sweepAngle = -(360.0f * (mMaxProgress - mProgress) / mMaxProgress);
//        }
//        return sweepAngle;
//    }

    public void setInternalAlpha(float alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 1) {
            alpha = 1;
        }
        int tmp = (int) (255 * alpha);
        if (mInternalAlpha != tmp) {
            mInternalAlpha = tmp;
            invalidate();
        }
    }

    public void setOutAlpha(float alpha) {
        if (alpha < 0) {
            alpha = 0;
        } else if (alpha > 1) {
            alpha = 1;
        }
        int tmp = (int) (255 * alpha);
        if (tmp != mOutAlpha) {
            mOutAlpha = tmp;
            invalidate();
        }
    }

    public void setInternalRadius(int radius) {
        if (mInternalRadius != radius) {
            mInternalRadius = radius;
            invalidate();
        }
    }

    public void setOutRadius(int radius) {
        if (mOutRadius != radius) {
            mOutRadius = radius;
            invalidate();
        }
    }
}
