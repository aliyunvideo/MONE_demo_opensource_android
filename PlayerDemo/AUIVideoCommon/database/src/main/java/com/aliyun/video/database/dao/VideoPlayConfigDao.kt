package com.aliyun.video.database.dao

import androidx.room.*
import com.aliyun.video.database.entity.VideoPlayConfig

@Dao
interface VideoPlayConfigDao {
    @Query("SELECT * FROM VIDEOPLAYCONFIG")
    fun getAll(): List<VideoPlayConfig>

    @Query("SELECT * FROM VIDEOPLAYCONFIG WHERE uid =:uid ")
    fun findByID(uid: Long): VideoPlayConfig?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg info: VideoPlayConfig)

    @Delete
    fun delete(info: VideoPlayConfig)

    @Update
    fun updateInfo(vararg info: VideoPlayConfig)
}