package com.aliyun.auiplayerserver.bean

import androidx.annotation.Keep

@Keep
data class VideoDetailInfo(
    var episodes: List<VideoInfo> = emptyList(),
    val videoDetail: VideoInfo
)