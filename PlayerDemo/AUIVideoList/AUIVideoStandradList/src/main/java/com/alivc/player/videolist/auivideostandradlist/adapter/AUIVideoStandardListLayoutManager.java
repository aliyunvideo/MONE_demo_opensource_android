package com.alivc.player.videolist.auivideostandradlist.adapter;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;

import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

/**
 * ViewPager效果的LayoutManager
 */
public class AUIVideoStandardListLayoutManager extends AUIVideoListLayoutManager implements View.OnTouchListener {

    private int mState;
    private int mdy;

    /**
     * 移动方向
     */
    private int direction;

    private int mCurrentPosition;

    public AUIVideoStandardListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        if(recyclerView.getOnFlingListener() == null){
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
                    case SCROLL_STATE_IDLE:
                        View viewIdle = mPagerSnapHelper.findSnapView(AUIVideoStandardListLayoutManager.this);
                        if (viewIdle == null) {
                            return;
                        }
                        int positionIdle = getPosition(viewIdle);
                        if (mOnViewPagerListener != null && mCurrentPosition != positionIdle) {
                            mOnViewPagerListener.onPageSelected(positionIdle);
                        }
                        mCurrentPosition = positionIdle;
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


    /**
     * 判断是否设置了监听
     */
    public boolean viewPagerListenerIsNull() {
        return mOnViewPagerListener == null;
    }


    /**
     * 监听竖直方向的相对偏移量
     */
    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.direction = dy;
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    /**
     * 监听水平方向的相对偏移量
     */
    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        this.direction = dx;
        return super.scrollHorizontallyBy(dx, recycler, state);
    }

    private RecyclerView.OnChildAttachStateChangeListener mChildAttachStateChangeListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            if (mOnViewPagerListener != null && getChildCount() == 1) {
                mOnViewPagerListener.onInitComplete();
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
            if (direction >= 0) {
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener.onPageRelease(getPosition(view));
                }
            } else {
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener.onPageRelease(getPosition(view));
                }
            }

        }
    };

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
                    View snapView = mPagerSnapHelper.findSnapView(AUIVideoStandardListLayoutManager.this);
                    if (snapView != null) {
                        int position = getPosition(snapView);
                        //如果是第一个视频,并且
                        if (position == 0 && mdy < 0) {
                            if (mOnViewPagerListener != null && getChildCount() == 1) {
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
     *
     * https://stackoverflow.com/questions/31759171/recyclerview-and-java-lang-indexoutofboundsexception-inconsistency-detected-in
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try{
            super.onLayoutChildren(recycler, state);
        }catch (IndexOutOfBoundsException e){
        }

    }
}
