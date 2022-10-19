package com.aliyun.auivideolist.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLayoutManager extends LinearLayoutManager {

    private PagerSnapHelper mPagerSnapHelper;
    private OnViewPagerListener mOnViewPagerListener;
    private int mOldPosition = -1;
    private OrientationHelper mOrientationHelper;

    public CustomLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        init();
    }

    private void init() {
        mPagerSnapHelper = new PagerSnapHelper();
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, getOrientation());
    }

    public void setOnViewPagerListener(OnViewPagerListener listener){
        this.mOnViewPagerListener = listener;
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        if(recyclerView != null){
            mPagerSnapHelper.attachToRecyclerView(recyclerView);

            recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
                @Override
                public void onChildViewAttachedToWindow(@NonNull View view) {
                    int childCount = recyclerView.getChildCount();
                    if(mOnViewPagerListener != null && childCount == 1){
                        mOnViewPagerListener.onPageSelected(0);
                    }else{
                        int position = getPosition(view);
                        mOnViewPagerListener.onPageShow(position);
                    }
                }

                @Override
                public void onChildViewDetachedFromWindow(@NonNull View view) {
                    if(mOnViewPagerListener != null){
                        mOnViewPagerListener.onPageRelease();
                    }
                }
            });
        }
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if(state == RecyclerView.SCROLL_STATE_IDLE){
            if(mOnViewPagerListener != null){
                View snapView = mPagerSnapHelper.findSnapView(CustomLayoutManager.this);
                if(snapView != null){
                    int position = getPosition(snapView);
                    if(mOldPosition != position){
                        mOnViewPagerListener.onPageSelected(position);
                        mOldPosition = position;
                    }
                }
            }
        }
    }

    /**
     * As {@link LinearLayoutManager#collectAdjacentPrefetchPositions} will prefetch one view for us,
     * we only need to prefetch additional ones.
     */
    private int mAdditionalAdjacentPrefetchItemCount = 0;

    public void setPreloadItemCount(int preloadItemCount) {
        if (preloadItemCount < 1) {
            throw new IllegalArgumentException("adjacentPrefetchItemCount must not smaller than 1!");
        }
        mAdditionalAdjacentPrefetchItemCount = preloadItemCount - 1;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        return 200;
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state,
                                                 LayoutPrefetchRegistry layoutPrefetchRegistry) {
        super.collectAdjacentPrefetchPositions(dx, dy, state, layoutPrefetchRegistry);
        /* We make the simple assumption that the list scrolls down to load more data,
         * so here we ignore the `mShouldReverseLayout` param.
         * Additionally, as we can not access mLayoutState, we have to get related info by ourselves.
         * See LinearLayoutManager#updateLayoutState
         */
        int delta = (getOrientation() == HORIZONTAL) ? dx : dy;
        if (getChildCount() == 0 || delta == 0) {
            // can't support this scroll, so don't bother prefetching
            return;
        }
        final int layoutDirection = delta > 0 ? 1 : -1;
        final View child = getChildClosest(layoutDirection);
        final int currentPosition = getPosition(child) + layoutDirection;
        int scrollingOffset;
        /* Our aim is to pre-load, so we just handle layoutDirection=1 situation.
         * If we handle layoutDirection=-1 situation, each scroll with slightly toggle directions
         * will cause huge numbers of bindings.
         */
        if (layoutDirection == 1) {
            scrollingOffset = mOrientationHelper.getDecoratedEnd(child)
                    - mOrientationHelper.getEndAfterPadding();
            for (int i = currentPosition + 1; i < currentPosition + mAdditionalAdjacentPrefetchItemCount + 1; i++) {
                if (i >= 0 && i < state.getItemCount()) {
                    layoutPrefetchRegistry.addPosition(i, Math.max(0, scrollingOffset));
                }
            }
        }
    }

    private View getChildClosest(int layoutDirection) {
        return getChildAt(layoutDirection == -1 ? 0 : getChildCount() - 1);
    }

    public interface OnViewPagerListener{
        void onPageShow(int position);
        void onPageSelected(int position);
        void onPageRelease();
    }
}
