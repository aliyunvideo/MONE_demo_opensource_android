package com.aliyun.svideo.editor.clip.audioeffect

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.contains
import com.aliyun.svideo.editor.clip.base.BaseClipController
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.internal.videoaugment.VideoAugmentationType
import com.aliyun.svideosdk.common.struct.project.AudioEffect
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AudioEffectType
import java.util.*

class AudioEffectPanelController(contentLayout: FrameLayout, editor:AliyunIEditor): BaseClipController(editor),OnItemClickListener,AudioEffectClickListener {

    private var mContentLayout:FrameLayout = contentLayout
    private var mAudioEffectPanelView: AudioEffectPanelView = AudioEffectPanelView(mContentLayout.context)
    private var mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
    private var mViewModel: AudioEffectPanelViewModel = AudioEffectPanelViewModel(contentLayout.context)

    init {
        mAudioEffectPanelView.setViewModel(mViewModel)
        mViewModel.setOnItemClickListener(this)
        mViewModel.actionBarViewModel.setOnItemClickListener(this)
        mViewModel.setOnItemClickListener(this)
        mAudioEffectPanelView.setItemClickListener(this)
    }

    open fun onBackPress() : Boolean {
        if (mContentLayout.contains(mAudioEffectPanelView)) {
            hidePanel()
            return true
        }
        return false
    }

    open fun showPanel() {
        mContentLayout.addView(mAudioEffectPanelView, mLayoutParams)
    }

    open fun hidePanel() {
        mContentLayout.removeView(mAudioEffectPanelView)
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        if (id == PanelItemId.ITEM_ID_CANCEL) {
            hidePanel()
        }
    }

    override fun onEffectSelected(effectType: AudioEffectType) {
        var isPlaying = mEditor.isPlaying
        mEditor.pause()
        if (mViewModel.mIsCheckAll) {
            onAllStreamEffectSelected(effectType)
        } else {
            onSingleEffectSelected(effectType)
        }

        mViewModel.setSelectView(effectType)
        mAudioEffectPanelView.notifyDataChanged()
        if (isPlaying) {
            mEditor.play()
        }
    }

    private fun onAllStreamEffectSelected(effectType: AudioEffectType) {
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        if (trackList == null || trackList.isEmpty()) {
            return
        }
        for (track in trackList) {
            for (effect in track.effects) {
                if (effect is AudioEffect) {
                    mEditor.removeAudioEffect(track.clipId, effect.mEffectType)
                }
            }
            if (effectType != AudioEffectType.EFFECT_TYPE_DEFAULT) {
                mEditor.applyAudioEffect(track.clipId, effectType, 50)
            }

        }
        return
    }

    private fun onSingleEffectSelected(effectType: AudioEffectType) {
        if (mTrackClip == null) {
            findCurrentStreamId()
        }
        if (mTrackClip == null) return
        for (effect in mTrackClip!!.effects) {
            if (effect is AudioEffect) {
                mEditor.removeAudioEffect(mTrackClip!!.clipId, effect.mEffectType)
            }
        }
        if (effectType != AudioEffectType.EFFECT_TYPE_DEFAULT) {
            mEditor.applyAudioEffect(mTrackClip!!.clipId, effectType, 50)
        }

    }


    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip==null) {
            return
        }
        mViewModel.onCurrentClipChanged(streamId, trackClip)
        mAudioEffectPanelView.notifyDataChanged()
    }

}