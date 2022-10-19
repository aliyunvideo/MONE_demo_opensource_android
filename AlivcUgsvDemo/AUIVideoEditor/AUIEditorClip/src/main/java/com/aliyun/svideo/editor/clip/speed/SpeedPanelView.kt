package com.aliyun.svideo.editor.clip.speed

import android.content.Context
import com.aliyun.aio.avbaseui.widget.AVTickSeekbar
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.databinding.UgsvEditorClipSpeedPanelBinding
import com.aliyun.svideo.editor.common.panel.BasePanelView


class SpeedPanelView(context: Context) : BasePanelView<UgsvEditorClipSpeedPanelBinding>(context) {

    lateinit var mTickBar: AVTickSeekbar
    override val layoutId: Int
        get() = R.layout.ugsv_editor_clip_speed_panel

    override fun initView(context: Context) {
        super.initView(context)
        mTickBar = findViewById(R.id.ugsv_eidtor_speed_tickbar)
    }

    open fun setTickPosition(position:Int) {
        mTickBar.setThumbPosition(position)
    }

    open fun setViewModel(viewModel: SpeedPanelViewModel) {
        mDataBinding.viewModel = viewModel
        mTickBar.setDatas(viewModel.mItem)
    }

    open fun setTickChangeListener(listener: AVTickSeekbar.OnTickChangedListener) {
        mTickBar.setOnTickChangedListener(listener)
    }

}