package com.aliyun.svideo.editor.common.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView

/**
 * ****************************************************************************
 * Copyright (C) 2005-2022 alibaba-inc All rights reserved
 *
 * Description : LinearItemDecoration
 *
 * Creation : 1/6/22
 * ****************************************************************************
 */
class GridItemDecoration : RecyclerView.ItemDecoration {
    private var mPaddingTop: Int
    private var mPaddingLeft: Int
    private var mPaddingRight: Int
    private var mPaddingBottom: Int
    private var paddingTopDrawable: Drawable? = null

    constructor(paddingTop: Int, paddingBottom : Int, paddingLR: Int, @ColorInt paddingTopColor: Int = 0) {
        mPaddingTop = paddingTop
        mPaddingBottom = paddingBottom
        mPaddingLeft = paddingLR
        mPaddingRight = paddingLR
        if (paddingTopColor != 0) {
            paddingTopDrawable = ColorDrawable(paddingTopColor)
        }
    }


    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = mPaddingTop
        outRect.bottom = mPaddingBottom
        outRect.left = mPaddingLeft
        outRect.right = mPaddingRight
    }

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (paddingTopDrawable != null
            && parent.childCount > 1 && mPaddingTop > 0
        ) {
            paddingTopDrawable!!.setBounds(0, 0, parent.right, mPaddingTop)
            paddingTopDrawable!!.draw(canvas)
        }
    }
}