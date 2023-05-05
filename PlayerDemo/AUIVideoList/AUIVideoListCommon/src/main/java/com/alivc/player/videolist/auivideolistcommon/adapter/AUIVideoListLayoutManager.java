package com.alivc.player.videolist.auivideolistcommon.adapter;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.listener.OnViewPagerListener;

public class AUIVideoListLayoutManager extends LinearLayoutManager {

    protected PagerSnapHelper mPagerSnapHelper;
    protected OnViewPagerListener mOnViewPagerListener;
    protected OrientationHelper mOrientationHelper;

    public AUIVideoListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
    }

    public void setOnViewPagerListener(OnViewPagerListener listener) {
        this.mOnViewPagerListener = listener;
    }

    public void setPreloadItemCount(int preloadItemCount) {
        if (preloadItemCount < 1) {
            throw new IllegalArgumentException("adjacentPrefetchItemCount must not smaller than 1!");
        }
        /**
         * As {@link LinearLayoutManager#collectAdjacentPrefetchPositions} will prefetch one view for us,
         * we only need to prefetch additional ones.
         */
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return 200;
    }

    private View getChildClosest(int layoutDirection) {
        return getChildAt(layoutDirection == -1 ? 0 : getChildCount() - 1);
    }
}
