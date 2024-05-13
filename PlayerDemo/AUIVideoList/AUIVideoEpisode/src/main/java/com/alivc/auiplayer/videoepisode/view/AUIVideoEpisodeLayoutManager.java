package com.alivc.auiplayer.videoepisode.view;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alivc.auiplayer.videoepisode.adapter.AUIVideoEpisodeAdapter;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;

public class AUIVideoEpisodeLayoutManager extends AUIVideoListLayoutManager implements View.OnTouchListener {
    private int mState;
    private int mdy;

    private int mCurrentPosition = -1;

    private RecyclerView mRecyclerView;

    public AUIVideoEpisodeLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
        // 解决滑动冲突的问题，避免episode panel展开时，当触摸事件发生的时候，list view上下滑的事件被先响应到。
        if (mRecyclerView != null) {
            for (int i = 0; i < mRecyclerView.getChildCount(); ++i) {
                View child = mRecyclerView.getChildAt(i);
                RecyclerView.ViewHolder viewHolder = mRecyclerView.getChildViewHolder(child);
                // 在这里处理ViewHolder的滑动事件
                if (viewHolder instanceof AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) {
                    AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder vh = (AUIVideoEpisodeAdapter.AUIVideoEpisodeViewHolder) viewHolder;
                    if (vh.isPanelShow()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        mRecyclerView = recyclerView;
        if (recyclerView.getOnFlingListener() == null) {
            mPagerSnapHelper.attachToRecyclerView(recyclerView);
        }
        recyclerView.addOnChildAttachStateChangeListener(mChildAttachStateChangeListener);
        recyclerView.setOnTouchListener(this);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                mState = newState;
                switch (mState) {
                    case SCROLL_STATE_DRAGGING:
                        break;
                    case SCROLL_STATE_IDLE:
                        View viewIdle = mPagerSnapHelper.findSnapView(AUIVideoEpisodeLayoutManager.this);
                        if (viewIdle == null) {
                            return;
                        }
                        int positionIdle = getPosition(viewIdle);
                        Log.i("CheckFunc", "onScrollStateChanged " + " positionIdle " + positionIdle + " mCurrentPosition " + mCurrentPosition + " mdy: " + mdy);
                        if (mOnViewPagerListener != null && mCurrentPosition != positionIdle) {
                            mOnViewPagerListener.onPageSelected(positionIdle);
                            mCurrentPosition = positionIdle;
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mdy = dy;
            }
        });
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            int position = getPosition(view);
            Log.i("CheckFunc", "[ATTACH][" + mCurrentPosition + "->" + position + "]");
            if (mOnViewPagerListener != null && position == 0) {
                mOnViewPagerListener.onInitComplete();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            int position = getPosition(view);
            Log.i("CheckFunc", "[DETACH][" + mCurrentPosition + "->" + position + "]");
            if (mOnViewPagerListener != null) {
                mOnViewPagerListener.onPageRelease(position);
            }
        }
    };

    //todo 测试改进

    /**
     * 监听ouTouch事件，因为如果从第二个视频滑动到第一个视频(快速连续滑动),
     * onScrollStateChanged是不会触发SCROLL_STATE_IDLE状态的,会导致
     * 第一个视频不会播放的问题,不会调用onPageSelected监听回调。
     * 经反复测试发现,会回调onTouch的事件,所以在这里进行触发
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mPagerSnapHelper != null) {
                    View snapView = mPagerSnapHelper.findSnapView(AUIVideoEpisodeLayoutManager.this);
                    if (snapView != null) {
                        int position = getPosition(snapView);
                        if (position == 0 && mdy < 0) {
                            if (mOnViewPagerListener != null && getChildCount() == 1) {
                                Log.i("CheckFunc", "onTouch position: " + position);
                                mOnViewPagerListener.onPageSelected(position);
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * bug java.lang.IndexOutOfBoundsException: Inconsistency detected. Invalid view holder adapter positionViewHolder
     * <p>
     * https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
        }
    }
}
