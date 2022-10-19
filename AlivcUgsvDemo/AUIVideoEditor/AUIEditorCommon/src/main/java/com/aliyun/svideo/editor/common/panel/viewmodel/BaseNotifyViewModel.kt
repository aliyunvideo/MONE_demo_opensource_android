package com.aliyun.svideo.editor.common.panel.viewmodel

import androidx.databinding.BaseObservable
import androidx.lifecycle.ViewModel
import com.aliyun.svideo.editor.common.panel.OnItemClickListener

open class BaseNotifyViewModel : BaseObservable() {
    protected var mOnItemClickListener: OnItemClickListener? = null

    open fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    open fun removeItemClickListener() {
        mOnItemClickListener = null
    }
}