package com.aliyun.svideo.editor.clip.base

import com.aliyun.svideosdk.common.struct.project.AudioEffect
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AudioEffectType

abstract class BaseClipController(editor: AliyunIEditor) {
    protected var mEditor = editor
    protected var mCurrentStreamId = -1
    var mLastStreamId = -1
    var mTrackClip: VideoTrackClip? = null

    open fun onUpdateProgress() {
        mCurrentStreamId = findCurrentStreamId()
        if (mLastStreamId != mCurrentStreamId) {
            mLastStreamId = mCurrentStreamId
            onCurrentClipChanged(mCurrentStreamId, mTrackClip)
        }
    }

    protected abstract fun onCurrentClipChanged(streamId:Int, trackClip: VideoTrackClip?)

    protected fun findCurrentStreamId() : Int {
        var currentPosition = mEditor.playerController.currentPlayPosition
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        for (track in trackList) {
            if (currentPosition >= track.timelineIn * 1000 && currentPosition < track.timelineOut * 1000) {
                mTrackClip = track
                return track.clipId
            }
        }
        return -1
    }

    protected fun findCurrentStreamAudioEffect() : AudioEffectType {
        var currentPosition = mEditor.playerController.currentPlayPosition
        var trackList = mEditor.editorProject.timeline.primaryTrack.videoTrackClips
        for (track in trackList) {
            if (currentPosition >= track.timelineIn * 1000 && currentPosition < track.timelineOut * 1000) {
                if (track.effects.isEmpty()) {
                    return AudioEffectType.EFFECT_TYPE_DEFAULT
                }
                for(effect in track.effects) {
                    if (effect is AudioEffect) {
                        var audioEffect = effect as AudioEffect
                        return audioEffect.mEffectType
                    }
                }
            }
        }
        return AudioEffectType.EFFECT_TYPE_DEFAULT
    }
}