package com.aliyun.svideo.editor.clip.base

import com.aliyun.svideo.editor.common.panel.viewmodel.BaseNotifyViewModel
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip

open abstract class BaseClipViewModel : BaseNotifyViewModel(){
    open abstract fun onCurrentClipChanged(streamId:Int, trackClip: VideoTrackClip?)
}