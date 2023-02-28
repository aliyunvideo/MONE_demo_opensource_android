package com.alivc.live.baselive_pull.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.alivc.live.baselive_pull.R;
import com.alivc.live.commonutils.DensityUtil;

/**
 * 设置最大高度的Layout
 */
public class MaxHeightLayout extends LinearLayout {

    private float mMaxRatio = 0.75f;
    private float mMaxHeight;
    private float mMinHeight;

    public MaxHeightLayout(Context context) {
        super(context);
        init();
    }

    public MaxHeightLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtts(context, attrs);
        init();
    }

    public MaxHeightLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtts(context, attrs);
        init();
    }

    private void initAtts(Context context, AttributeSet attrs){
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.mMaxRatio);
        if (attributes != null) {
            mMaxRatio = attributes.getFloat(R.styleable.mMaxRatio_linear_max_ratio, 0.75f);
            attributes.recycle();
        }
    }

    private void init() {
        mMaxHeight = mMaxRatio * DensityUtil.getDisplayMetrics(getContext()).heightPixels;
        mMinHeight = DensityUtil.dip2px(getContext(), 125);
        mMaxHeight = mMaxHeight < mMinHeight ? mMinHeight : mMaxHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = heightSize <= mMaxHeight ? heightSize : (int) mMaxHeight;
        }
        int maxHeightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, heightMode);
        super.onMeasure(widthMeasureSpec, maxHeightMeasureSpec);
    }
}
