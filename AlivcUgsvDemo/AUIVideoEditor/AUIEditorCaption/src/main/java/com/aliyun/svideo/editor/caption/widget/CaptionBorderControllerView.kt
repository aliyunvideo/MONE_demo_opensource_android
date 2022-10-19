package com.aliyun.svideo.editor.caption.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View

class CaptionBorderControllerView : View {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mOnLayoutChangeListener?.onLayoutChanged(left, top, right, bottom)
    }

    interface OnLayoutChangeListener {
        fun onLayoutChanged(left: Int, top: Int, right: Int, bottom: Int)
    }

    private var mOnLayoutChangeListener: OnLayoutChangeListener? = null
    fun setOnLayoutChangeListener(onLayoutChangeListener: OnLayoutChangeListener?) {
        mOnLayoutChangeListener = onLayoutChangeListener
    }
}