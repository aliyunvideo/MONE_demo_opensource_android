package com.aliyun.auiplayerserver.bean

import androidx.annotation.Keep

@Keep
data class VideoStsInfo(
    val securityToken: String,
    val accessKeySecret: String,
    val accessKeyId: String,
    val expiration: String
)