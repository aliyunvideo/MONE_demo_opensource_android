package com.aliyun.video.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ExportSettingInfo(
    @PrimaryKey()
    val uid: Long,
    @ColumnInfo(name = "definition_progress")
    var definitionProgress: Int,
    @ColumnInfo(name = "coderate_progress")
    var codeRateProgress: Int,
    @ColumnInfo(name = "fps_progress")
    var fpsProgress: Int
)