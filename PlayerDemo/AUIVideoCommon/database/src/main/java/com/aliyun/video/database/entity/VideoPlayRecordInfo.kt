package com.aliyun.video.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["uid", "video_id"])
data class VideoPlayRecordInfo(
    @ColumnInfo(name = "uid")
    val uid: Long,
    @ColumnInfo(name = "video_id")
    val vid: String,
    @ColumnInfo(name = "play_duration")
    var playDuration: Int,
    @ColumnInfo(name = "last_play_time_millis")
    var lastPlayTimeMillis: Long
)
