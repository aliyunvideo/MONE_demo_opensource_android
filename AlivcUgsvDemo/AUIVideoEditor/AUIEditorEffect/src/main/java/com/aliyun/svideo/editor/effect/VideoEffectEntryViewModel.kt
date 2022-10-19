package com.aliyun.svideo.editor.effect

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.editor.AliyunIEditor
import java.util.concurrent.TimeUnit

class VideoEffectEntryViewModel() : BaseViewModel(), MultiTrackListener {

    lateinit var mVideoEffectManager:VideoEffectManager
    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = false,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_effect_title
    )
    val mMultiTrackViewModel = VideoEffectEntryMultiTrackViewModel()
    var mBaseClipInfo:MutableLiveData<BaseClipInfo?> = MutableLiveData()

    var currentEffectValid = mBaseClipInfo.map { it != null }
    var mVideoEffectClickListener:VideoEffectClickListener? = null

    private lateinit var mEditor: AliyunIEditor

    fun bind(lifecycleOwner: LifecycleOwner, editor : AliyunIEditor, videoEffectManager: VideoEffectManager) {
        this.mEditor = editor
        this.mVideoEffectManager = videoEffectManager
        this.mMultiTrackViewModel.bind(editor)
    }

    fun unBind() {
        onCleared()
        mBaseClipInfo.value = null
        mVideoEffectClickListener = null
        this.mMultiTrackViewModel.unBind()
    }

    fun setVideoEffectClickListener(videoEffectClickListener: VideoEffectClickListener) {
        mVideoEffectClickListener = videoEffectClickListener
    }

    fun addVideoEffect(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_ADD)
    }

    fun deleteVideoEffect(view: View) {
        mVideoEffectClickListener?.let {
            if (mBaseClipInfo.value == null) {
                return
            }
            for (videoTrack in mVideoEffectManager.mAddEffectList) {
                if (mBaseClipInfo.value!!.clipId == videoTrack.hashCode()) {
                    it.onEffectDelete(videoTrack)
                    return
                }
            }
        }
    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        super.setOnItemClickListener(onItemClickListener)
        actionBarViewModel.setOnItemClickListener(onItemClickListener)
    }

    override fun onClipClick(clipInfo: BaseClipInfo) {
        this.mMultiTrackViewModel.onClipClick(clipInfo)
    }

    override fun onFocusChanged(clipInfo: BaseClipInfo, isFocus: Boolean) {
        this.mMultiTrackViewModel.onFocusChanged(clipInfo, isFocus)
        if (isFocus) {
            mBaseClipInfo.value = clipInfo
        } else {
            mBaseClipInfo.value = null
        }

    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo?) {
        this.mMultiTrackViewModel.onUpdateClipTime(clipInfo)
        if (clipInfo == null) {
            return
        }
        for (videoTrack in mVideoEffectManager.mAddEffectList) {
            if (clipInfo.clipId == videoTrack.hashCode()) {
                videoTrack.mTrackEffectFilter.setStartTime(clipInfo.timelineIn, TimeUnit.MILLISECONDS)
                videoTrack.mTrackEffectFilter.setDuration(clipInfo.clipDuration, TimeUnit.MILLISECONDS)
                mEditor.updateAnimationFilter(videoTrack.mTrackEffectFilter)
                return
            }
        }
    }

    override fun onClipTouchPosition(time: Long) {
        this.mMultiTrackViewModel.onClipTouchPosition(time)
    }

    override fun onScrollChangedTime(time: Long) {
        this.mMultiTrackViewModel.onScrollChangedTime(time)
    }

}