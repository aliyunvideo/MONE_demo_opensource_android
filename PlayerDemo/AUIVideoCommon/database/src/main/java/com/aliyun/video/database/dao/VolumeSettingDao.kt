package com.aliyun.video.database.dao

import androidx.room.*
import com.aliyun.video.database.entity.ExportSettingInfo
import com.aliyun.video.database.entity.VolumeSettingInfo

@Dao
interface VolumeSettingDao {

    @Query("SELECT * FROM VOLUMESETTINGINFO WHERE videoId =:videoId ")
    fun findAll(videoId: Long): List<VolumeSettingInfo>

    @Query("SELECT * FROM VOLUMESETTINGINFO WHERE videoId =:videoId AND video_index=:videoIndex")
    fun findByID(videoId: Long, videoIndex: Int): VolumeSettingInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg info: VolumeSettingInfo)

    @Delete
    fun delete(info: VolumeSettingInfo)

    @Update(entity = VolumeSettingInfo::class)
    fun updateInfo(vararg info: VolumeSettingInfo)
}