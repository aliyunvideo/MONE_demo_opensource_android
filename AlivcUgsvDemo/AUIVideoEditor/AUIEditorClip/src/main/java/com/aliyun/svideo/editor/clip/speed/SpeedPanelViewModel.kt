package com.aliyun.svideo.editor.clip.speed

import android.content.Context
import android.view.View
import com.aliyun.aio.avbaseui.widget.AVTickSeekbar
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.base.BaseClipViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip

class SpeedPanelViewModel(context: Context) : BaseClipViewModel(){


    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = true,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_speed_title
    )

    val mItem = arrayListOf<AVTickSeekbar.Item>(
        AVTickSeekbar.Item("0.5x", 0.5f),
        AVTickSeekbar.Item("0.75x", 0.75f),
        AVTickSeekbar.Item("1x", 1.0f),
        AVTickSeekbar.Item("1.5x", 1.5f),
        AVTickSeekbar.Item("2.0x", 2.0f)
    )

    fun onSpaceClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL, null)
    }

    fun onResetClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_RESET, null)
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
    }

}