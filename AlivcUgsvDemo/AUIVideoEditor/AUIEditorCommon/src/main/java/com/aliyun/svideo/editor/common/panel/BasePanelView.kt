package com.aliyun.svideo.editor.common.panel

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

abstract class BasePanelView<T : ViewDataBinding>(context: Context) : FrameLayout(context) {
    protected lateinit var mDataBinding: T
    protected open fun initView(context: Context) {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, this, true)
    }

    protected abstract val layoutId: Int

    init {
        initView(context)
    }

}