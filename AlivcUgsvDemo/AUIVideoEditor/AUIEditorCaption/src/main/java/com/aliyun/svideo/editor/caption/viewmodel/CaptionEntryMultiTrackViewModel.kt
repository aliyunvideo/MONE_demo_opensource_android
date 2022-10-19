package com.aliyun.svideo.editor.caption.viewmodel

import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.caption.CaptionManager
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor

class CaptionEntryMultiTrackViewModel() : BaseViewModel(), MultiTrackListener {
    private var mTouchPosition: Long = 0
    private var mDuration: Long = 0
    var trackList = MutableLiveData<List<VideoTrackClip>>()

    private lateinit var aliyunEditor: AliyunIEditor


    fun bind(editor: AliyunIEditor) {
        this.aliyunEditor = editor
        this.mDuration = editor.duration
        trackList.value = editor.editorProject.timeline.primaryTrack.videoTrackClips
    }


    fun unBind() {
        onCleared()
    }

    override fun onClipClick(clipInfo: BaseClipInfo) {

    }

    override fun onFocusChanged(clipInfo: BaseClipInfo, isFocus: Boolean) {
        if (isFocus) {
            aliyunEditor.seek((clipInfo.timelineIn * 1000 * clipInfo.speed).toLong())
        }
    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo) {
        val captionList = CaptionManager.getAllCaption(aliyunEditor.pasterManager)
        val caption = captionList.firstOrNull {
            it.hashCode() == clipInfo.clipId
        }
        caption?.let {
            //更新时间线
            it.startTime = clipInfo.timelineIn * 1000
            it.duration = (clipInfo.timelineOut - clipInfo.timelineIn) * 1000

            //更新动画的时间线
            val frameAnimationList = it.frameAnimations
            it.clearFrameAnimation()
            frameAnimationList.forEach { anim ->
                anim.startTime = it.startTime * 1000
                anim.duration = it.duration * 1000
                it.addFrameAnimation(anim)
            }
            it.apply()
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