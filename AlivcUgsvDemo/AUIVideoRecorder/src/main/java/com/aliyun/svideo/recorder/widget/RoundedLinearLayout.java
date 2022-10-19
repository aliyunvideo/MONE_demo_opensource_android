package com.aliyun.svideo.recorder.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class RoundedLinearLayout extends LinearLayout {
    private RoundedViewHandler mHandler;

    public RoundedLinearLayout(Context context) {
        this(context, null);
    }

    public RoundedLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new RoundedViewHandler(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mHandler == null) {
            return;
        }
        mHandler.onSizeChanged(w, h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (mHandler == null) {
            super.dispatchDraw(canvas);
            return;
        }
        canvas.save();
        mHandler.onDraw(canvas);
        super.dispatchDraw(canvas);
        canvas.restore();
    }
}
