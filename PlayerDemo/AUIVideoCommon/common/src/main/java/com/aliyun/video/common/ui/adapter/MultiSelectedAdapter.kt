package com.aliyun.video.common.ui.adapter

import com.drakeet.multitype.MultiTypeAdapter

class MultiSelectedAdapter : MultiTypeAdapter() {
    private var mSelectedPosition = 0

    fun notifySelected(position: Int) {
        val lastPosition = mSelectedPosition
        if (lastPosition > -1 && lastPosition < itemCount) {
            notifyItemChanged(lastPosition)
        }
        mSelectedPosition = position
        if (mSelectedPosition > -1 && mSelectedPosition < itemCount) {
            notifyItemChanged(mSelectedPosition)
        }
    }

    fun getSelectedPosition(): Int {
        return mSelectedPosition
    }
}