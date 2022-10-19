package com.aliyun.svideo.editor.clip.volume

import android.content.Context
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.databinding.UgsvEditorClipVolumePanelBinding
import com.aliyun.svideo.editor.common.panel.BasePanelView


class VolumePanelView(context: Context) : BasePanelView<UgsvEditorClipVolumePanelBinding>(context) {

    override val layoutId: Int
        get() = R.layout.ugsv_editor_clip_volume_panel

    override fun initView(context: Context) {
        super.initView(context)
    }

    open fun setViewModel(viewModel: VolumePanelViewModel) {
        mDataBinding.viewModel = viewModel

    }

    open fun updateSeekStatus() {
        mDataBinding.viewModel?.let {
            it.updateMusicProgress()
            if (it.hasOtherMusic()) {
                mDataBinding.ugsvEditorMusicSeekbar.setOnTouchListener(null)
            } else {
                mDataBinding.ugsvEditorMusicSeekbar.setOnTouchListener { v, event -> return@setOnTouchListener true }
            }
        }
    }

}