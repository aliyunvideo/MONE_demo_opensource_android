package com.aliyun.video.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VolumeSettingInfo(
    @PrimaryKey()
    val videoId: Long,

    @ColumnInfo(name = "volume_progress")
    var volumeProgress: Int,

    @ColumnInfo(name = "fadin_progress")
    var fadInProgress: Int,

    @ColumnInfo(name = "fadout_progress")
    var fadOutProgress: Int,
    /**
     * 视频的index
     */
    @ColumnInfo(name = "video_index")
    var videoIndex: Int
)