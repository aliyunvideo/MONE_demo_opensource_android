/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.aliyun.svideo.base.R;

public class CircleProgressBar extends View {

    private RectF mOval;
    private Paint mPaint;
    private Paint mBgPaint;
    private Paint mFillPaint;

    private int mProgress;
    private int mStrokeWidth;
    private boolean hasBgColor;
    private boolean isFilled;

    private float mBackgroundWidth;
    private float mBackgroundHeight;
    private float mProgressWidth;
    private float mProgressThickness;
    private int mProgressColor;
    private int progressThicknessColor;
    private int mBackgroundColor;

    public CircleProgressBar(Context paramContext) {
        this(paramContext, null);
    }

    public CircleProgressBar(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public CircleProgressBar(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);

        TypedArray array = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.QuViewCircleProgressBar);
        mBackgroundWidth = array.getDimension(R.styleable.QuViewCircleProgressBar_backgroundWidth, mBackgroundWidth);
        mBackgroundHeight = array.getDimension(R.styleable.QuViewCircleProgressBar_backgroundHeight, mBackgroundHeight);
        mProgressWidth = array.getDimension(R.styleable.QuViewCircleProgressBar_progressWidth, mProgressWidth);
        mProgressThickness = array.getDimension(R.styleable.QuViewCircleProgressBar_progressThickness, mProgressThickness);
        mBackgroundColor = array.getColor(R.styleable.QuViewCircleProgressBar_backgroundColor, mBackgroundColor);
        mProgressColor = array.getColor(R.styleable.QuViewCircleProgressBar_progressColor, mProgressColor);
        progressThicknessColor = array.getColor(R.styleable.QuViewCircleProgressBar_progressThicknessColor, progressThicknessColor);
        array.recycle();
        instantiate();
    }

    private void instantiate() {
        this.mProgress = 0;
        this.mStrokeWidth = (int)mProgressThickness;
        float left = (mBackgroundWidth - mProgressWidth) / 2;
        float right = left + mProgressWidth;
        float top = (mBackgroundHeight - mProgressWidth) / 2;
        float bottom = top + mProgressWidth;
        this.mOval = new RectF(left, top, right, bottom);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);

        this.mBgPaint = new Paint();
        this.mBgPaint.setAntiAlias(true);
        this.mBgPaint.setStyle(Paint.Style.FILL);

        this.mFillPaint = new Paint();
        this.mFillPaint.setAntiAlias(true);
        this.mFillPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        if (progressThicknessColor == 0) {
            this.mPaint.setColor(getResources().getColor(android.R.color.transparent));
            this.mFillPaint.setColor(getResources().getColor(android.R.color.transparent));
        } else {
            this.mPaint.setColor(progressThicknessColor);
            this.mFillPaint.setColor(progressThicknessColor);
        }

        if (isFilled) {
            paramCanvas.drawArc(this.mOval, 270.0F, 360.0F, true, this.mFillPaint);
        } else {
            paramCanvas.drawArc(this.mOval, 270.0F, 360.0F, false, this.mPaint);
        }

        if (hasBgColor) {
            if (mBackgroundColor == 0) {
                this.mBgPaint.setColor(getResources().getColor(android.R.color.transparent));
            } else {
                this.mBgPaint.setColor(mBackgroundColor);
            }

            paramCanvas.drawCircle(mBackgroundWidth / 2, mBackgroundWidth / 2, mProgressWidth / 2, mBgPaint);
        }

        if (this.mProgress > 0) {
            this.mPaint.setAlpha(0);
            if (mProgressColor == 0) {
                this.mPaint.setColor(getResources().getColor(android.R.color.white));
                this.mFillPaint.setColor(getResources().getColor(android.R.color.white));
            } else {
                this.mPaint.setColor(mProgressColor);
                this.mFillPaint.setColor(mProgressColor);
            }

            if (isFilled) {
                paramCanvas.drawArc(this.mOval, 270.0F, 360 * this.mProgress / 100, true, this.mFillPaint);
            } else {
                paramCanvas.drawArc(this.mOval, 270.0F, 360 * this.mProgress / 100, false, this.mPaint);
            }
        }
    }

    public void setProgressThicknessColor(int color) {
        progressThicknessColor = color;
    }

    public void setProgress(int paramInt) {
        this.mProgress = paramInt;
        invalidate();
    }

    @Override
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setBackgroundWidth(int width, int height) {
        mBackgroundWidth = width;
        mBackgroundHeight = height;

        instantiate();
    }

    public void setProgressWidth(int width) {
        mProgressWidth = width;

        instantiate();
    }

    public void setProgressThickness(int thickness) {
        mProgressThickness = thickness;

        instantiate();
    }

    public void isProgressBackGround(boolean isBgColor) {
        hasBgColor = isBgColor;
    }

    public void isFilled(boolean isFilled) {
        this.isFilled = isFilled;
    }
}
