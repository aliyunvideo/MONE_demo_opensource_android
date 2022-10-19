package com.aliyun.svideo.editor.filter

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideo.editor.effect.R
import com.aliyun.svideo.editor.effect.VideoEffectAdapter
import com.aliyun.svideo.editor.effect.VideoEffectResourceManager
import com.aliyun.svideo.editor.effect.databinding.UgsvEditorEffectEntryPanelBinding
import com.aliyun.svideo.editor.effect.databinding.UgsvEditorFilterEntryPanelBinding
import com.aliyun.svideo.track.bean.EffectClipInfo


open class VideoFilterEntryPanelView(context: Context) : BasePanelView<UgsvEditorFilterEntryPanelBinding>(context) {
    private lateinit var mVideoEffectEntryVM : VideoFilterEntryViewModel
    lateinit var mVideoFilterAdapter: VideoFilterAdapter

    override val layoutId: Int
        get() = R.layout.ugsv_editor_filter_entry_panel

    override fun initView(context: Context) {
        super.initView(context)
        var layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mDataBinding.videoFilterRecyclerview.layoutManager = layoutManager

    }

    fun setViewModel(videoEffectEntryVM: VideoFilterEntryViewModel, videoFilterManager: VideoFilterManager) {
        mDataBinding.viewModel = videoEffectEntryVM
        val lifeOwner = context as LifecycleOwner
        mVideoEffectEntryVM = videoEffectEntryVM
        mVideoFilterAdapter = VideoFilterAdapter(videoFilterManager)
        mDataBinding.videoFilterRecyclerview.adapter = mVideoFilterAdapter
        var viewModelList = VideoFilterResourceManager.loadVideoFilters(context)
        mVideoFilterAdapter.setData(viewModelList)
    }

    fun setVideoFilterClickListener(videoFilterClickListener: VideoFilterClickListener) {
        mVideoFilterAdapter.setVideoFilterSelectListener(videoFilterClickListener)
    }

    fun updatePlayProgress(currentPlayTime : Long) {
    }


}