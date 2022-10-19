package com.aliyun.svideo.editor.caption.model

import com.aliyun.svideo.track.bean.CaptionClipInfo
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption

class CaptionTrack(val captionController : AliyunPasterControllerCompoundCaption) : CaptionClipInfo(captionController.hashCode()) {

    init {
        val startTime = captionController.startTime
        val duration = captionController.duration
        this.`in` = 0L;
        this.out = duration / 1000
        this.timelineIn = startTime / 1000
        this.timelineOut = this.timelineIn + duration / 1000

    }

    override fun getText(): String {
        return captionController.text
    }

    override fun setText(text: String) {
        captionController.text = text
    }

}