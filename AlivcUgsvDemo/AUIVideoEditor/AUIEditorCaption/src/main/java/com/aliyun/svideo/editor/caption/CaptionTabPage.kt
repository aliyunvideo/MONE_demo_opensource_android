package com.aliyun.svideo.editor.caption

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel

abstract class CaptionTabPage<T : ViewDataBinding>(val context: Context, var titleRes: Int) : ICaptionTabPage {

    protected lateinit var mDataBinding: T

    protected abstract val layoutId: Int
    protected lateinit var captionEditViewModel : CaptionEditViewModel

    override fun onTabInit() {
        mDataBinding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false)
        mDataBinding.lifecycleOwner = context as LifecycleOwner
    }
    override fun onTabUnInit() {
    }

    override fun onBind(captionEditVM: CaptionEditViewModel) {
        this.captionEditViewModel = captionEditVM
    }

    override fun getTitle(): String {
        return context.getString(titleRes)
    }

    override fun getView(): View {
        return mDataBinding.root
    }

    override fun onTabSelected() {

    }
}