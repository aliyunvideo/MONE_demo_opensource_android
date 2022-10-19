package com.aliyun.svideo.editor.common.panel.viewmodel

import androidx.lifecycle.ViewModel
import com.aliyun.svideo.editor.common.panel.OnItemClickListener

open class BaseViewModel : ViewModel() {
    protected var mOnItemClickListener: OnItemClickListener? = null

    override fun onCleared() {
        super.onCleared()
        mOnItemClickListener = null
    }

    open fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }
}