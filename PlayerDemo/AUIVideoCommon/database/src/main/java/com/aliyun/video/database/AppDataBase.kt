package com.aliyun.video.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aliyun.video.database.dao.*
import com.aliyun.video.database.entity.*

@Database(
    entities = [ExportSettingInfo::class, VolumeSettingInfo::class, DownloadResourceEntity::class,
        VideoPlayConfig::class, VideoPlayRecordInfo::class],
    version = 15, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun exportSettingInfoDao(): ExportSettingDao
    abstract fun volumeSettingInfoDao(): VolumeSettingDao
    abstract fun transitionDao(): TransitionDao
    abstract fun playConfigDao(): VideoPlayConfigDao
    abstract fun playRecordDao(): VideoPlayRecordDao
}