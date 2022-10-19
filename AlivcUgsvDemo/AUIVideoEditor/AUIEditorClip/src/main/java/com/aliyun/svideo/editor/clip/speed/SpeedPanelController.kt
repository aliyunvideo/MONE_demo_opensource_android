package com.aliyun.svideo.editor.clip.speed

import android.view.View
import android.widget.FrameLayout
import androidx.core.view.contains
import com.aliyun.aio.avbaseui.widget.AVTickSeekbar
import com.aliyun.svideo.editor.clip.base.BaseClipController
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor

class SpeedPanelController(contentLayout: FrameLayout, editor:AliyunIEditor): BaseClipController(editor), OnItemClickListener,AVTickSeekbar.OnTickChangedListener {

    object DEFAULT {
        var TICK_INDEX = 2;
    }

    private var mContentLayout:FrameLayout = contentLayout
    private var mSpeedPanelView: SpeedPanelView = SpeedPanelView(mContentLayout.context)
    private var mLayoutParams: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
        FrameLayout.LayoutParams.MATCH_PARENT,
        FrameLayout.LayoutParams.MATCH_PARENT)
    private var mViewModel: SpeedPanelViewModel = SpeedPanelViewModel(contentLayout.context)
    private var mTickIndex = DEFAULT.TICK_INDEX

    var mSpeedEffectListener: SpeedEffectListener?=null

    init {
        mSpeedPanelView.setViewModel(mViewModel)
        mSpeedPanelView.setTickChangeListener(this)
        mViewModel.setOnItemClickListener(this)
        mViewModel.actionBarViewModel.setOnItemClickListener(this)
    }

    open fun onBackPress() : Boolean {
        if (mContentLayout.contains(mSpeedPanelView)) {
            hidePanel()
            return true
        }
        return false
    }

    open fun showPanel() {
        if (mContentLayout.contains(mSpeedPanelView)) {
            hidePanel()
        }
        mSpeedPanelView.setTickPosition(mTickIndex)
        mContentLayout.addView(mSpeedPanelView, mLayoutParams)
    }

    open fun hidePanel() {
        mContentLayout.removeView(mSpeedPanelView)
    }

    open fun setSpeedEffectListener(listener: SpeedEffectListener) {
        mSpeedEffectListener = listener
    }

    override fun onItemClick(view: View?, id: Long, obj: Any?) {
        if (id == PanelItemId.ITEM_ID_CANCEL) {
            hidePanel()
        } else if (id == PanelItemId.ITEM_ID_RESET) {
            if (mTickIndex != DEFAULT.TICK_INDEX) {
                mSpeedEffectListener?.onSpeedChanged(1.0f)
            }
            mTickIndex = DEFAULT.TICK_INDEX
            mSpeedPanelView.setTickPosition(mTickIndex)
        }
    }

    override fun onTickChanged(index: Int, item: AVTickSeekbar.Item?) {
        if (item != null && mTickIndex != index) {
            mSpeedEffectListener?.onSpeedChanged(item.progress)
            mTickIndex = index
        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
    }


}