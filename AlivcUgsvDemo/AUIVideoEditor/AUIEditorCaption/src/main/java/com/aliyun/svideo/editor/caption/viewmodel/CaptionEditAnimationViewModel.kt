package com.aliyun.svideo.editor.caption.viewmodel

import androidx.databinding.ObservableInt
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel

class CaptionEditAnimationViewModel() : BaseViewModel() {
    val progress = ObservableInt()
    val maxProgress = ObservableInt()

    fun bind(progress : Int) {
        this.progress.set(progress)
        this.maxProgress.set(100)
    }

}