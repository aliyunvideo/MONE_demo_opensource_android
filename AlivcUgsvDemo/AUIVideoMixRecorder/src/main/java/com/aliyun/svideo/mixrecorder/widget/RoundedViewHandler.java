package com.aliyun.svideo.mixrecorder.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.aliyun.svideo.record.R;

public class RoundedViewHandler {
    private Path mRoundedRectPath;
    private RectF mRectF;

    private float mTopLeftRadius;
    private float mTopRightRadius;
    private float mBottomLeftRadius;
    private float mBottomRightRadius;

    public RoundedViewHandler(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundedView);
        float radius = typedArray.getDimension(R.styleable.RoundedView_cornerRadius, 0f);
        mTopLeftRadius = typedArray.getDimension(R.styleable.RoundedView_topLeftCornerRadius, radius);
        mTopRightRadius = typedArray.getDimension(R.styleable.RoundedView_topRightCornerRadius, radius);
        mBottomLeftRadius = typedArray.getDimension(R.styleable.RoundedView_bottomLeftCornerRadius, radius);
        mBottomRightRadius = typedArray.getDimension(R.styleable.RoundedView_bottomRightCornerRadius, radius);
        typedArray.recycle();
        mRoundedRectPath = new Path();
        mRectF = new RectF();
    }

    public void onSizeChanged(int width, int height) {
        float[] radii = new float[]{
                mTopLeftRadius, mTopLeftRadius,
                mTopRightRadius, mTopRightRadius,
                mBottomRightRadius, mBottomRightRadius,
                mBottomLeftRadius, mBottomLeftRadius
        };
        mRectF.set(0.0f, 0.0f, width, height);
        mRoundedRectPath.reset();
        mRoundedRectPath.addRoundRect(mRectF, radii, Path.Direction.CW);
    }

    public void onDraw(Canvas canvas) {
        canvas.clipPath(mRoundedRectPath);
    }
}
