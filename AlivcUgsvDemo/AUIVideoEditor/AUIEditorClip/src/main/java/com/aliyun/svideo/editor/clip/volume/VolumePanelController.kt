package com.aliyun.svideo.editor.clip.volume

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

class VolumePanelController(contentLayout: FrameLayout, editor:AliyunIEditor): BaseClipController(editor), OnItemClickListener {

    private var mContentLayout:FrameLayout = contentLayout
    private var mVolumePanelView: VolumePanelView = VolumePanelView(mContentLayout.context)
    private var mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
    private var mViewModel: VolumePanelViewModel = VolumePanelViewModel(contentLayout.context, editor)

    init {
        mVolumePanelView.setViewModel(mViewModel)
        mViewModel.setOnItemClickListener(this)
        mViewModel.actionBarViewModel.setOnItemClickListener(this)
        mViewModel.setOnItemClickListener(this)
//        mViewModel.setProgressChangedListener(this)
    }

    open fun onBackPress() : Boolean {
        if (mContentLayout.contains(mVolumePanelView)) {
            hidePanel()
            return true
        }
        return false
    }

    open fun showPanel() {
        mVolumePanelView.updateSeekStatus()
        mContentLayout.addView(mVolumePanelView, mLayoutParams)
    }

    open fun hidePanel() {
        mContentLayout.removeView(mVolumePanelView)
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        if (id == PanelItemId.ITEM_ID_CANCEL) {
            hidePanel()
        } else if (id == PanelItemId.ITEM_ID_RESET) {

        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip==null) {
            return
        }
        mViewModel.onCurrentClipChanged(streamId, trackClip)
    }


}