package com.aliyun.svideo.editor.clip.enhance

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.contains
import com.aliyun.svideo.editor.clip.base.BaseClipController
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.internal.videoaugment.VideoAugmentationType
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import java.util.*

class EnhancePanelController(contentLayout: FrameLayout, editor:AliyunIEditor): BaseClipController(editor), OnItemClickListener,OnProgressChangedListener {

    private var mContentLayout:FrameLayout = contentLayout
    private var mEnhancePanelView: EnhancePanelView = EnhancePanelView(mContentLayout.context)
    private var mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
    private var mViewModel: EnhancePanelViewModel = EnhancePanelViewModel(contentLayout.context)

    init {
        mEnhancePanelView.setViewModel(mViewModel)
        mViewModel.setOnItemClickListener(this)
        mViewModel.actionBarViewModel.setOnItemClickListener(this)
        mViewModel.setOnItemClickListener(this)
        mViewModel.setProgressChangedListener(this)
    }

    open fun onBackPress() : Boolean {
        if (mContentLayout.contains(mEnhancePanelView)) {
            hidePanel()
            return true
        }
        return false
    }

    open fun showPanel() {
        mContentLayout.addView(mEnhancePanelView, mLayoutParams)
    }

    open fun hidePanel() {
        mContentLayout.removeView(mEnhancePanelView)
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        if (id == PanelItemId.ITEM_ID_CANCEL) {
            hidePanel()
        } else if (id == PanelItemId.ITEM_ID_RESET) {

        }
    }

    override fun onProgressChanged(panelItemId: Long, progress: Float, isAll: Boolean) {
        if (isAll) {
            adjustAllEnhance(panelItemId, progress)
        } else {
            var currentStreamId = findCurrentStreamId()
            if (currentStreamId == -1) {
                return
            }
            adjustEnhance(panelItemId, currentStreamId, progress)
        }
    }

    override fun onResetClick(isAll: Boolean) {
        if (isAll) {
            adjustAllEnhance(PanelItemId.ITEM_ID_BRIGHTNESS, 0f, true)
            adjustAllEnhance(PanelItemId.ITEM_ID_CONTRAST, 0f, true)
            adjustAllEnhance(PanelItemId.ITEM_ID_SATURATION, 0f, true)
            adjustAllEnhance(PanelItemId.ITEM_ID_VIGNETTING, 0f, true)
            adjustAllEnhance(PanelItemId.ITEM_ID_SHARPNESS, 0f, true)
        } else {
            var currentStreamId = findCurrentStreamId()
            if (currentStreamId == -1) {
                return
            }
            adjustEnhance(PanelItemId.ITEM_ID_BRIGHTNESS, currentStreamId, 0f, true)
            adjustEnhance(PanelItemId.ITEM_ID_CONTRAST, currentStreamId, 0f, true)
            adjustEnhance(PanelItemId.ITEM_ID_SATURATION, currentStreamId, 0f, true)
            adjustEnhance(PanelItemId.ITEM_ID_VIGNETTING, currentStreamId, 0f, true)
            adjustEnhance(PanelItemId.ITEM_ID_SHARPNESS, currentStreamId, 0f, true)
        }
    }

    private fun adjustAllEnhance(panelItemId: Long, progress: Float, reset: Boolean = false) {
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        for (track in trackList) {
            adjustEnhance(panelItemId, track.clipId, progress, reset)
        }
    }

    private fun adjustEnhance(panelItemId: Long, currentStreamId: Int, progress: Float, reset: Boolean = false) {
        var augmentationType:VideoAugmentationType = VideoAugmentationType.CONTRAST
        when (panelItemId) {
            PanelItemId.ITEM_ID_BRIGHTNESS -> augmentationType = VideoAugmentationType.BRIGHTNESS
            PanelItemId.ITEM_ID_CONTRAST -> augmentationType = VideoAugmentationType.CONTRAST
            PanelItemId.ITEM_ID_SATURATION -> augmentationType = VideoAugmentationType.SATURATION
            PanelItemId.ITEM_ID_VIGNETTING -> augmentationType = VideoAugmentationType.VIGNETTE
            PanelItemId.ITEM_ID_SHARPNESS -> augmentationType = VideoAugmentationType.SHARPNESS
            else -> return
        }
        if (reset) {
            mEditor.resetVideoAugmentation(currentStreamId, augmentationType)
        } else {
            mEditor.setVideoAugmentation(currentStreamId, augmentationType, progress)
        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip==null) {
            return
        }
        mViewModel.onCurrentClipChanged(streamId, trackClip)
    }


}