package com.aliyun.auiplayerserver.bean

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize

@Keep
@Parcelize
data class User(
    var userId: String?,
    var userName: String?,
    var avatarUrl: String?
) : Parcelable