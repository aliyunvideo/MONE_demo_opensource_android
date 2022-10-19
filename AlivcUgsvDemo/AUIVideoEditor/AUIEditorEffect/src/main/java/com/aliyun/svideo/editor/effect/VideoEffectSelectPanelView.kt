package com.aliyun.svideo.editor.effect

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideo.editor.effect.databinding.UgsvEditorEffectSelectPanelBinding


class VideoEffectSelectPanelView(context: Context) : BasePanelView<UgsvEditorEffectSelectPanelBinding>(context) {

    override val layoutId: Int
        get() = R.layout.ugsv_editor_effect_select_panel

    lateinit var mRecyclerView:RecyclerView
    lateinit var mVideoEffectAdapter:VideoEffectAdapter

    override fun initView(context: Context) {
        super.initView(context)
        mRecyclerView = findViewById(R.id.audio_effect_recyclerview)
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mRecyclerView.layoutManager = layoutManager
        mVideoEffectAdapter = VideoEffectAdapter()
        mRecyclerView.adapter = mVideoEffectAdapter
    }

    open fun setViewModel(viewModel: VideoEffectSelectViewModel) {
        mDataBinding.viewModel = viewModel
        var viewModelList = VideoEffectResourceManager.loadVideoEffects(context)
        mVideoEffectAdapter.setData(viewModelList)
    }

    open fun setItemClickListener(listener: VideoEffectClickListener) {
        mVideoEffectAdapter.setVideoEffectSelectListener(listener)
    }

    open fun notifyDataChanged() {
        mVideoEffectAdapter.notifyDataSetChanged()
    }

    fun onBackPress():Boolean {
        if (parent != null && parent is ViewGroup) {
            var viewGroup:ViewGroup = parent as ViewGroup
            viewGroup.removeView(this)
            return true
        }
        return false
    }

}