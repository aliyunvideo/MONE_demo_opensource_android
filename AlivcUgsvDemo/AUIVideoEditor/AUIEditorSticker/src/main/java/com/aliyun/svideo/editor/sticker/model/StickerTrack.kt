package com.aliyun.svideo.editor.sticker.model

import com.aliyun.svideo.track.bean.StickerClipInfo
import com.aliyun.svideosdk.editor.AliyunPasterController

class StickerTrack(val controller : AliyunPasterController) : StickerClipInfo(controller.hashCode()) {

    init {
        val startTime = controller.startTime
        val duration = controller.duration
        this.`in` = 0L
        this.out = duration / 1000
        this.timelineIn = startTime / 1000
        this.timelineOut = this.timelineIn + duration / 1000

    }

    override fun getPath(): String {
        return controller.pasterIconPath
    }

    override fun setPath(path: String) {
//        captionController.text = text
    }
}