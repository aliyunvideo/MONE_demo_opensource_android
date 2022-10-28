package com.aliyun.svideo.recorder

import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode
import com.aliyun.svideosdk.common.struct.common.VideoQuality
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs

open class RecorderConfig {
    companion object {
        const val RATIO_MODE_3_4 = 3.0f/4
        const val RATIO_MODE_9_16 = 9.0f/16
        const val RATIO_MODE_1_1 = 1.0f

        const val RESOLUTION_360P = 360
        const val RESOLUTION_540P = 540
        const val RESOLUTION_720P = 720
        const val RESOLUTION_1080P = 1080

        const val DEFAULT_GOP = 250
        const val DEFAULT_BITRATE = -1

        val instance : RecorderConfig by lazy {
            RecorderConfig()
        }
    }
    /**关键帧间隔*/
    var gop = DEFAULT_GOP
    /**编码器*/
    var codec: VideoCodecs = VideoCodecs.H264_HARDWARE
    /**视频质量*/
    var videoQuality:VideoQuality = VideoQuality.HD
    /**裁剪模式*/
    var videoDisplayMode: VideoDisplayMode = VideoDisplayMode.FILL
    /**分辨率*/
    var resolution = RESOLUTION_720P
    /**视频比例*/
    var ratio = RATIO_MODE_9_16
    /**码率*/
    var bitRate = DEFAULT_BITRATE

    var fps = 30

    var minDuration = 2 * 1000
    var maxDuration = 15 * 1000

    var needEdit = true
    var isVideoFlip = false
    var isClearCache = false

    fun reset() {
        gop = DEFAULT_GOP
        codec = VideoCodecs.H264_HARDWARE
        videoQuality = VideoQuality.HD
        videoDisplayMode = VideoDisplayMode.FILL
        resolution = RESOLUTION_720P
        ratio = RATIO_MODE_9_16
        bitRate = DEFAULT_BITRATE
        fps = 30

        minDuration = 2 * 1000
        maxDuration = 15 * 1000

        needEdit = true
        isVideoFlip = false
        isClearCache = false
    }
}