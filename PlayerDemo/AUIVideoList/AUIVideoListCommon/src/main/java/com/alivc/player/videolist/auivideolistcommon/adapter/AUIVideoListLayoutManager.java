package com.alivc.player.videolist.auivideolistcommon.adapter;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.listener.OnViewPagerListener;

public class AUIVideoListLayoutManager extends LinearLayoutManager {

    protected final PagerSnapHelper mPagerSnapHelper;
    protected final OrientationHelper mOrientationHelper;

    protected OnViewPagerListener mOnViewPagerListener;

    public AUIVideoListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        mPagerSnapHelper = new PagerSnapHelper();
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, RecyclerView.VERTICAL);
    }

    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return 200;
    }
}
