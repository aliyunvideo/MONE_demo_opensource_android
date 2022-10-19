package com.aliyun.svideo.editor.sticker.viewmodel

import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.common.struct.effect.EffectPaster
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AliyunPasterController

class StickerEntryMultiTrackViewModel() : BaseViewModel(), MultiTrackListener {
    private var mTouchPosition: Long = 0
    var trackList = MutableLiveData<List<VideoTrackClip>>()

    private lateinit var aliyunEditor: AliyunIEditor


    fun bind(editor: AliyunIEditor) {
        this.aliyunEditor = editor
        trackList.value = editor.editorProject.timeline.primaryTrack.videoTrackClips
    }


    fun unBind() {
        onCleared()
    }

    override fun onClipClick(clipInfo: BaseClipInfo) {
        val startTime = (clipInfo.timelineIn * 1000 * clipInfo.speed).toLong()
        val duration = ((clipInfo.timelineOut - clipInfo.timelineIn) * 1000 * clipInfo.speed).toLong()
        if(startTime > aliyunEditor.currentStreamPosition || (startTime + duration) < aliyunEditor.currentStreamPosition) {
            aliyunEditor.seek(startTime)
        }
    }

    override fun onFocusChanged(clipInfo: BaseClipInfo, isFocus: Boolean) {
    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo) {
        val stickerController = this.aliyunEditor.pasterManager.findControllersByType(EffectPaster.PASTER_TYPE_GIF)
            .firstOrNull {
                it.hashCode() == clipInfo.clipId
            }
        (stickerController as? AliyunPasterController)?.let {
            it.editStart()
            it.startTime = (clipInfo.timelineIn * 1000 * clipInfo.speed).toLong()
            it.duration = ((clipInfo.timelineOut - clipInfo.timelineIn) * 1000 * clipInfo.speed).toLong()

            if(it.startTime > aliyunEditor.currentStreamPosition || (it.startTime + it.duration) < aliyunEditor.currentStreamPosition) {
                aliyunEditor.seek(it.startTime)
            }

            it.editCompleted()
        }
    }

    override fun onClipTouchPosition(timeMs: Long) {
        val time = timeMs * 1000
        mTouchPosition = time
        aliyunEditor.draw(time)
    }

    override fun onScrollChangedTime(timeMs: Long) {
        aliyunEditor.seek(timeMs * 1000)
    }

}