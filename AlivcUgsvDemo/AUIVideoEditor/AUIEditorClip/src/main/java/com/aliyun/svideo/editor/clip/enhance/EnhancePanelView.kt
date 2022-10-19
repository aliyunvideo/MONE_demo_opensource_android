package com.aliyun.svideo.editor.clip.enhance

import android.content.Context
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.databinding.UgsvEditorClipEnhancePanelBinding
import com.aliyun.svideo.editor.common.panel.BasePanelView


class EnhancePanelView(context: Context) : BasePanelView<UgsvEditorClipEnhancePanelBinding>(context) {

    override val layoutId: Int
        get() = R.layout.ugsv_editor_clip_enhance_panel

    override fun initView(context: Context) {
        super.initView(context)
    }


    open fun setViewModel(viewModel: EnhancePanelViewModel) {
        mDataBinding.viewModel = viewModel
    }


}