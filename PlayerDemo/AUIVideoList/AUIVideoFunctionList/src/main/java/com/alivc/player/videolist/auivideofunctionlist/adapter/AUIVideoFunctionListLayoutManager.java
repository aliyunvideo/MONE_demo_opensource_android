package com.alivc.player.videolist.auivideofunctionlist.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListLayoutManager;

public class AUIVideoFunctionListLayoutManager extends AUIVideoListLayoutManager {
    // 用于跟踪上一个位置
    private int mOldPosition = -1;

    public AUIVideoFunctionListLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onAttachedToWindow(RecyclerView recyclerView) {
        super.onAttachedToWindow(recyclerView);
        // 如果RecyclerView为null，直接返回
        if (recyclerView == null) {
            return;
        }

        // 负责对齐页面
        mPagerSnapHelper.attachToRecyclerView(recyclerView);

        // 监听子视图的附加和分离
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                int position = getPosition(view);
                if (mOnViewPagerListener != null) {
                    if (recyclerView.getChildCount() == 1) {
                        mOnViewPagerListener.onPageSelected(0);
                    } else {
                        mOnViewPagerListener.onPageShow(position);
                    }
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                int position = getPosition(view);
                if (mOnViewPagerListener != null) {
                    mOnViewPagerListener.onPageRelease(position);
                }
            }
        });
    }

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            View snapView = mPagerSnapHelper.findSnapView(this);
            if (snapView != null && mOnViewPagerListener != null) {
                int position = getPosition(snapView);
                if (mOldPosition != position) {
                    mOnViewPagerListener.onPageSelected(position);
                    mOldPosition = position;
                }
            }
        }
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        // 增加额外的布局空间
        return 200;
    }
}
