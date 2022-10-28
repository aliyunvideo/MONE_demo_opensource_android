package com.aliyun.svideo.crop

import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode
import com.aliyun.svideosdk.common.struct.common.VideoQuality
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs

open class CropConfig {
    companion object {
        const val RATIO_MODE_3_4 = 3.0f/4
        const val RATIO_MODE_9_16 = 9.0f/16
        const val RATIO_MODE_1_1 = 1.0f
        const val RATIO_MODE_ORIGIN = 0f

        const val RESOLUTION_360P = 360
        const val RESOLUTION_540P = 540
        const val RESOLUTION_720P = 720
        const val RESOLUTION_1080P = 1080

        const val DEFAULT_FRAME_RATE = 25
        const val DEFAULT_GOP = 250
        const val DEFAULT_BITRATE = -1

        const val DEFAULT_MIN_DURATION = 3 * 1000

        val instance : CropConfig by lazy {
            CropConfig()
        }
    }
    /**帧率*/
     var fps = DEFAULT_FRAME_RATE
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
    var ratio = RATIO_MODE_ORIGIN
    /**码率*/
    var bitRate = DEFAULT_BITRATE

    var minDuration = DEFAULT_MIN_DURATION

    var startTime = 0L

    var duration = 0L

    var inputPath: String? = null
    var outputPath: String? = null

    fun isOriginRatio() : Boolean {
        return ratio == RATIO_MODE_ORIGIN
    }

    fun reset() {
        fps = DEFAULT_FRAME_RATE
        gop = DEFAULT_GOP
        codec = VideoCodecs.H264_HARDWARE
        videoQuality = VideoQuality.HD
        videoDisplayMode = VideoDisplayMode.FILL
        resolution = RESOLUTION_720P
        ratio = RATIO_MODE_ORIGIN
        bitRate = DEFAULT_BITRATE
        minDuration = DEFAULT_MIN_DURATION
        startTime = 0
        duration = 0
        inputPath = null
        outputPath = null
    }
}