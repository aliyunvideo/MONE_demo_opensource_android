package com.aliyun.svideo.editor.clip.volume

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.base.BaseClipViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip
import com.aliyun.svideosdk.editor.AliyunIEditor

class VolumePanelViewModel(context: Context, aliyunIEditor: AliyunIEditor) : BaseClipViewModel() {

    var mIsCheckAll = false

    var mOriginProgress = 100
    var mMusicProgress = 0

    var mCurrentClip:VideoTrackClip ? = null

    val mEditor = aliyunIEditor

    init {
        if (mEditor.editorProject.timeline.primaryTrack.videoTrackClips.size > 0) {
            var originVolume = mEditor.editorProject.timeline.primaryTrack.videoTrackClips.get(0).mixWeight
            if (originVolume >= 0) {
                mOriginProgress = originVolume
            } else {
                mOriginProgress = 100
            }
        }
        updateMusicProgress()
    }

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = true,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_volume_title
    )

    fun hasOtherMusic() : Boolean{
        if (mEditor.editorProject.timeline.audioTracks.size > 0 && mEditor.editorProject.timeline.audioTracks.get(0).audioTrackClips.size > 0) {
            return true
        }
        return false
    }

    fun onSpaceClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL, null)
    }

    fun onCheckAllClick(view: View) {
        var imageView: ImageView = view as ImageView
        mIsCheckAll = !mIsCheckAll
        if (mIsCheckAll) {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_check)
        } else {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_uncheck)
        }
    }

    fun setOriginProgress(originProgress:Int) {
        mOriginProgress = originProgress
        if (mIsCheckAll) {
            for(trackClip in mEditor.editorProject.timeline.primaryTrack.videoTrackClips) {
                mEditor.applyMusicWeight(trackClip.clipId, mOriginProgress)
            }
        } else {
            mCurrentClip?.let {
                mEditor.applyMusicWeight(it.clipId, mOriginProgress)
            }
        }
        notifyChange()
    }

    fun getOriginProgress():Int {
        return mOriginProgress
    }

    fun setMusicProgress(musicProgress:Int) {
        mMusicProgress = musicProgress
        if (mEditor.editorProject.timeline.audioTracks.size > 0 && mEditor.editorProject.timeline.audioTracks.get(0).audioTrackClips.size > 0) {
            for (audioClip in mEditor.editorProject.timeline.audioTracks.get(0).audioTrackClips) {
                mEditor.applyMusicWeight(audioClip.clipId, mMusicProgress)
            }
        }
        notifyChange()
    }

    fun getMusicProgress():Int {
        return mMusicProgress
    }

    fun getOriginProgressText(): String {
        return "$mOriginProgress%"
    }

    fun getMusicProgressText(): String {
        return "$mMusicProgress%"
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip == null) {
            return
        }
        mCurrentClip = trackClip
        var originVolume = trackClip.mixWeight
        if (originVolume >= 0) {
            mOriginProgress = originVolume
        } else {
            mOriginProgress = 100
        }
        updateMusicProgress()
        notifyChange()
    }

    fun updateMusicProgress() {
        if (mEditor.editorProject.timeline.audioTracks.size > 0 && mEditor.editorProject.timeline.audioTracks.get(0).audioTrackClips.size > 0) {
            var mix = mEditor.editorProject.timeline.audioTracks.get(0).audioTrackClips.get(0).mixWeight
            if (mix >= 0) {
                mMusicProgress = mix
            } else {
                mMusicProgress = 100
            }
        } else {
            mMusicProgress = 0;
        }
    }

}