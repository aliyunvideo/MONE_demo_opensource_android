/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.media;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.aliyun.ugsv.common.utils.DensityUtils;

public class GalleryItemDecoration extends RecyclerView.ItemDecoration {
    private int mLineSpace = -1;
    private int offset = -1;
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        if (mLineSpace == -1) {
            mLineSpace = DensityUtils.dip2px(view.getContext(), 1.0f);
            offset = DensityUtils.dip2px(view.getContext(), 1.0f);
        }

        int position = parent.getChildLayoutPosition(view);


        outRect.bottom = mLineSpace;
        if (position % 4 == 0) {
            outRect.right = offset;
        } else if (position % 4 == 1 || position % 4 == 2) {
            outRect.left = offset / 2;
            outRect.right = offset / 2;
        } else {
            outRect.left = offset;
        }

    }
}
