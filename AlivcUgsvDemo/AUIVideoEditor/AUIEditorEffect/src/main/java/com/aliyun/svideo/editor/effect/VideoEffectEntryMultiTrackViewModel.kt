package com.aliyun.svideo.editor.effect

import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import java.util.concurrent.TimeUnit

class VideoEffectEntryMultiTrackViewModel() : BaseViewModel(), MultiTrackListener {
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
            aliyunEditor.seek(clipInfo.timelineIn, TimeUnit.MILLISECONDS)
        }
    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo?) {

    }

    override fun onClipTouchPosition(time: Long) {
        mTouchPosition = time
        aliyunEditor.draw(time * 1000)
    }

    override fun onScrollChangedTime(time: Long) {
        aliyunEditor.seek(time, TimeUnit.MILLISECONDS)
    }

}