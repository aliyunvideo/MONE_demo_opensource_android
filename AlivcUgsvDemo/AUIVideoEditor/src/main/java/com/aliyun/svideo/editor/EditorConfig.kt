package com.aliyun.svideo.editor

import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode
import com.aliyun.svideosdk.common.struct.common.VideoQuality
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs

public class EditorConfig {
    companion object {
        const val RATIO_MODE_3_4 = 3.0f/4
        const val RATIO_MODE_9_16 = 9.0f/16
        const val RATIO_MODE_1_1 = 1.0f
        const val RATIO_MODE_ORIGIN = 0f

        const val RESOLUTION_360P = 360
        const val RESOLUTION_540P = 540
        const val RESOLUTION_720P = 720
        const val RESOLUTION_1080P = 1080
    }
    /**帧率*/
     var fps = 30
    /**关键帧间隔*/
    var gop = 30
    /**编码器*/
    var codec: VideoCodecs = VideoCodecs.H264_HARDWARE
    /**视频质量*/
    var videoQuality:VideoQuality = VideoQuality.HD
    /**裁剪模式*/
    var videoDisplayMode: VideoDisplayMode = VideoDisplayMode.FILL
    /**分辨率*/
    var resolution = RESOLUTION_1080P
    /**视频比例*/
    var ratio = RATIO_MODE_ORIGIN
}