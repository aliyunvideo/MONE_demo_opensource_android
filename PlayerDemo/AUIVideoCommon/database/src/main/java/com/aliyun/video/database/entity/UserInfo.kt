package com.aliyun.video.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @PrimaryKey
    val uid: Long, val name: String, val sex: String
)