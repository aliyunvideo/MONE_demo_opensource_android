/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget.pagerecyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.aliyun.svideo.base.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <p/>
 * 页码指示器类，获得此类实例后，可通过{@link PageIndicatorView#initIndicator(int)}方法初始化指示器
 * </P>
 */
public class PageIndicatorView extends LinearLayout {

    private Context mContext = null;
    private int dotSize = 0; // 指示器的大小（dp）
    private int margins = 0; // 指示器间距（dp）
    private int mSelectedResID = android.R.drawable.presence_online;
    private int mNormalResID = android.R.drawable.presence_invisible;

    private List<View> indicatorViews = null; // 存放指示器

    public PageIndicatorView(Context context) {
        this(context, null);
    }

    public PageIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView);
        try {
            mSelectedResID = ta.getResourceId(R.styleable.PageIndicatorView_pi_SelectedBackground, android.R.drawable.presence_online);
            mNormalResID = ta.getResourceId(R.styleable.PageIndicatorView_pi_NormalBackground, android.R.drawable.presence_invisible);
            margins = ta.getDimensionPixelSize(
                          R.styleable.PageIndicatorView_pi_Margin, DimensionConvert.dip2px(context, 5));
            dotSize = ta.getDimensionPixelSize(
                          R.styleable.PageIndicatorView_pi_DotSize, DimensionConvert.dip2px(context, 6));
        } finally {
            ta.recycle();
        }
    }

    public PageIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PageIndicatorView);
        try {
            mSelectedResID = ta.getResourceId(R.styleable.PageIndicatorView_pi_SelectedBackground, android.R.drawable.presence_online);
            mNormalResID = ta.getResourceId(R.styleable.PageIndicatorView_pi_NormalBackground, android.R.drawable.presence_invisible);
            margins = ta.getDimensionPixelSize(
                          R.styleable.PageIndicatorView_pi_Margin, DimensionConvert.dip2px(context, 5));
            dotSize = ta.getDimensionPixelSize(
                          R.styleable.PageIndicatorView_pi_DotSize, DimensionConvert.dip2px(context, 4));
        } finally {
            ta.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;

        setGravity(Gravity.CENTER);
        setOrientation(HORIZONTAL);

        dotSize = DimensionConvert.dip2px(context, dotSize);
        margins = DimensionConvert.dip2px(context, margins);
    }

    /**
     * 初始化指示器，默认选中第一页
     *
     * @param count 指示器数量，即页数
     */
    public void initIndicator(int count) {
//        if(count > 1) {
        if (indicatorViews == null) {
            indicatorViews = new ArrayList<>();
        } else {
            indicatorViews.clear();
            removeAllViews();
        }
        View view;
        LayoutParams params = new LayoutParams(dotSize, dotSize);
        params.setMargins(margins, margins, margins, margins);
        for (int i = 0; i < count; i++) {
            view = new View(mContext);
            view.setBackgroundResource(mNormalResID);
            addView(view, params);
            indicatorViews.add(view);
        }
        if (indicatorViews.size() > 0) {
            indicatorViews.get(0).setBackgroundResource(mSelectedResID);
        }
//        }else if(count == 1) {
//            indicatorViews.get(0).setBackgroundColor(Color.TRANSPARENT);
//        }
    }

    /**
     * 设置选中页
     *
     * @param selected 页下标，从0开始
     */
    public void setSelectedPage(int selected) {
        if (indicatorViews != null) {
            for (int i = 0; i < indicatorViews.size(); i++) {
                if (i == selected) {
                    indicatorViews.get(i).setBackgroundResource(mSelectedResID);
                } else {
                    indicatorViews.get(i).setBackgroundResource(mNormalResID);
                }
            }
        }
    }

}