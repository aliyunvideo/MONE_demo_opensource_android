package com.aliyun.auiplayerserver.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class VideoInfo(
    val cateId: Int,
    val cateName: String?,
    val commentCount: Int?,
    val coverUrl: String?,
    val description: String?,
    val duration: Double,
    val fileUrl: String?,
    val firstFrameUrl: String?,
    val height: Int,
    val likeCount: Int?,
    val publishTime: Long?,
    val size: Double?,
    val title: String?,
    val type: String?,
    val user: User?,
    val videoId: String,
    val viewCount: Int?,
    val vodId: Long?,
    val weight: Long?,
    val width: Int?,
    var randomUUID: String?
) : Parcelable