package com.aliyun.svideo.editor.clip.trasition

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.contains
import com.aliyun.svideo.editor.clip.base.BaseClipController
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.effect.TransitionBase
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import java.util.concurrent.TimeUnit

class TransitionPanelController(contentLayout: FrameLayout, editor:AliyunIEditor): BaseClipController(editor),OnItemClickListener,TransitionClickListener {

    private var mContentLayout:FrameLayout = contentLayout
    private var mTransitionPanelView: TransitionPanelView = TransitionPanelView(mContentLayout.context)
    private var mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
    private var mViewModel: TransitionPanelViewModel = TransitionPanelViewModel(contentLayout.context)
    private lateinit var mOnTransitionChangedListener:OnTransitionChangedListener
    private var mCurrentIndex = -1
    init {
        mTransitionPanelView.setViewModel(mViewModel)
        mViewModel.setOnItemClickListener(this)
        mViewModel.actionBarViewModel.setOnItemClickListener(this)
        mViewModel.setOnItemClickListener(this)
        mTransitionPanelView.setItemClickListener(this)
    }

    open fun onBackPress() : Boolean {
        if (mContentLayout.contains(mTransitionPanelView)) {
            hidePanel()
            return true
        }
        return false
    }

    open fun showPanel(index:Int) {
        mCurrentIndex = index - 1
        mContentLayout.addView(mTransitionPanelView, mLayoutParams)
    }

    open fun hidePanel() {
        mContentLayout.removeView(mTransitionPanelView)
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        if (id == PanelItemId.ITEM_ID_CANCEL) {
            hidePanel()
        }
    }

    override fun onEffectSelected(effectType: TransitionBase?) {
        var isPlaying = mEditor.isPlaying
        mEditor.pause()
        if (mViewModel.mIsCheckAll) {
            onAllStreamEffectSelected(effectType)
        } else {
            onSingleEffectSelected(effectType)
        }

        mViewModel.setSelectView(effectType)
        mTransitionPanelView.notifyDataChanged()
        seekToTarget(effectType)
        if (isPlaying) {
            mEditor.play()
        }
    }

    private fun seekToTarget(effectType: TransitionBase?) {
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        if (trackList == null || trackList.size < 2) {
            return
        }
        var trackClip:VideoTrackClip? = null
        for (i in 0 until trackList.size) {
            if (i == mCurrentIndex) {
                trackClip = trackList.get(i)
                break
            }
        }
        if (trackClip == null) {
            mEditor.seek(0)
        } else {
            var duration = 0L
            if (effectType != null) {
                duration = effectType.overlapDuration
            }
            var position = (trackClip.timelineOut * 1000000 - duration).toLong()
            mEditor.seek(position)
        }
    }

    private fun onAllStreamEffectSelected(effectType: TransitionBase?) {
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        if (trackList == null || trackList.size < 2) {
            return
        }
        for (i in 0 until trackList.size) {
            effectType?.setOverlapDuration(1, TimeUnit.SECONDS)
            mEditor.setTransition(i, effectType)
            mOnTransitionChangedListener?.onUpdateTransition(i,effectType)

        }
        return
    }

    private fun onSingleEffectSelected(effectType: TransitionBase?) {
        effectType?.setOverlapDuration(1, TimeUnit.SECONDS)
        mEditor.setTransition(mCurrentIndex, effectType)
        mOnTransitionChangedListener?.onUpdateTransition(mCurrentIndex,effectType)
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip==null) {
            return
        }
        mViewModel.onCurrentClipChanged(streamId, trackClip)
        mTransitionPanelView.notifyDataChanged()
    }

    open fun setOnTransitionChangedListener(listener: OnTransitionChangedListener) {
        mOnTransitionChangedListener = listener;
    }
}