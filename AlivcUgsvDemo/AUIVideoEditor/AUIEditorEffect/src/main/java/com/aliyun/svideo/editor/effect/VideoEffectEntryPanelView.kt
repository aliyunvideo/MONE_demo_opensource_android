package com.aliyun.svideo.editor.effect

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideo.editor.effect.databinding.UgsvEditorEffectEntryPanelBinding
import com.aliyun.svideo.track.bean.EffectClipInfo


open class VideoEffectEntryPanelView(context: Context) : BasePanelView<UgsvEditorEffectEntryPanelBinding>(context) {
    private lateinit var mVideoEffectEntryVM : VideoEffectEntryViewModel
    private var speed = 1.0f

    override val layoutId: Int
        get() = R.layout.ugsv_editor_effect_entry_panel


    fun setViewModel(videoEffectEntryVM: VideoEffectEntryViewModel) {
        mDataBinding.viewModel = videoEffectEntryVM
        val lifeOwner = context as LifecycleOwner
        mDataBinding.lifecycleOwner = lifeOwner
        mVideoEffectEntryVM = videoEffectEntryVM
        mDataBinding.multiTrackContainer.setTrackListener(videoEffectEntryVM)
        this.mVideoEffectEntryVM.mMultiTrackViewModel.trackList.observe(lifeOwner) {
            this.mDataBinding.multiTrackContainer.setVideoData(it)
        }
        this.mVideoEffectEntryVM.mVideoEffectManager.getEffectList().forEach {
            onAddVideoEffect(it)
        }
    }

    fun onAddVideoEffect(videoEffectTrack: VideoEffectTrack, needScrollTo: Boolean = false) {
        var effectInfo = EffectClipInfo(videoEffectTrack.hashCode())
        effectInfo.path = videoEffectTrack.mTrackEffectFilter.source.path
        effectInfo.text = videoEffectTrack.mName
        effectInfo.`in` = 0
        effectInfo.timelineIn = videoEffectTrack.mTrackEffectFilter.startTime
        effectInfo.timelineOut = videoEffectTrack.mTrackEffectFilter.endTime
        mDataBinding.multiTrackContainer.addSubClip(effectInfo)
        if (needScrollTo) {
            mDataBinding.multiTrackContainer.scrollToSubClip(effectInfo.clipId)
        }
    }

    fun onRemoveVideoEffect(videoEffectTrack: VideoEffectTrack) {
        mDataBinding.multiTrackContainer.removeSubClip(videoEffectTrack.hashCode())
    }

    fun updatePlayProgress(currentPlayTime : Long) {
        this.mDataBinding.multiTrackContainer.updatePlayProgress((currentPlayTime * speed / 1000).toLong())
    }

    fun updateSpeed(speed: Float) {
        this.speed = speed
    }
}