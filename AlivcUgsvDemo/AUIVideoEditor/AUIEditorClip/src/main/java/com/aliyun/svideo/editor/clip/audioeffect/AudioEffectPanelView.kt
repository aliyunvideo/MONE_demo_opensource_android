package com.aliyun.svideo.editor.clip.audioeffect

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.databinding.UgsvEditorClipAudioEffectPanelBinding
import com.aliyun.svideo.editor.common.panel.BasePanelView


class AudioEffectPanelView(context: Context) : BasePanelView<UgsvEditorClipAudioEffectPanelBinding>(context) {

    override val layoutId: Int
        get() = R.layout.ugsv_editor_clip_audio_effect_panel

    lateinit var mRecyclerView:RecyclerView
    lateinit var mAudioEffectAdapter:AudioEffectAdapter

    override fun initView(context: Context) {
        super.initView(context)
        mRecyclerView = findViewById(R.id.audio_effect_recyclerview)
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager
        mAudioEffectAdapter = AudioEffectAdapter()
        mRecyclerView.adapter = mAudioEffectAdapter
    }

    open fun setViewModel(viewModel: AudioEffectPanelViewModel) {
        mDataBinding.viewModel = viewModel
        mAudioEffectAdapter.setData(viewModel.listViewModel)
    }

    open fun setItemClickListener(listener: AudioEffectClickListener) {
        mAudioEffectAdapter.setAudioSelectListener(listener)
    }

    open fun notifyDataChanged() {
        mAudioEffectAdapter.notifyDataSetChanged()
    }


}