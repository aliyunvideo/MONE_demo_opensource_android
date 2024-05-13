package com.ethanhua.skeleton;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.RecyclerView;

public class SkeletonRecyclerView extends RecyclerView {
    private SkeletonScreen skeletonScreen = null;
    private boolean mSkeletonShow = false;

    public SkeletonRecyclerView(Context context) {
        super(context);
    }

    public SkeletonRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SkeletonRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showSkeleton(int mItemLayoutId, RecyclerView.Adapter<? extends RecyclerView.ViewHolder> mAdapter, int count) {
        skeletonScreen = Skeleton.bind(this)
                .adapter(mAdapter)
                .shimmer(false)
                .angle(20)
                .frozen(false)
                .duration(1200)
                .count(count)
                .load(mItemLayoutId)
                .show();
        mSkeletonShow = true;
    }

    public void hideSkeleton() {
        if (mSkeletonShow) {
            if (skeletonScreen != null) {
                skeletonScreen.hide();
            }
            mSkeletonShow = false;
        }
    }

    // Getters and setters for the skeletonScreen and mSkeletonShow if needed
}