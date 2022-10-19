/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */
package com.aliyun.svideo.editor.sticker.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.aliyun.svideo.editor.common.widget.BaseAliyunPasterView
import com.aliyun.svideo.editor.sticker.R

class StickerBorderControllerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : BaseAliyunPasterView(context, attrs, defStyle) {
    private var contentWidth = 0
    private var contentHeight = 0
    override fun setContentWidth(contentWidth: Int) {
        this.contentWidth = contentWidth
    }

    override fun setContentHeight(contentHeight: Int) {
        this.contentHeight = contentHeight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        validateTransform()
        val width: Int
        val height: Int
        mMatrixUtil.decomposeTSR(mTransform)
        width = (mMatrixUtil.scaleX * contentWidth).toInt()
        height = (mMatrixUtil.scaleY * contentHeight).toInt()
        Log.d(
            "EDIT", "Measure width : " + width + "scaleX : "
                    + " screen width : " + getWidth() + " 1/8 width : " + getWidth() / 8
        )
        val w = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
        val h = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(w, h)
    }

    private var mContentView: View? = null
    override fun onFinishInflate() {
        super.onFinishInflate()
        mContentView = findViewById(R.id.stickerBorder)
    }

    override fun getContentWidth(): Int {
        //Auto-generated method stub
        return contentWidth
    }

    override fun getContentHeight(): Int {
        //Auto-generated method stub
        return contentHeight
    }

    override fun getContentView(): View {
        //Auto-generated method stub
        return mContentView!!
    }
}