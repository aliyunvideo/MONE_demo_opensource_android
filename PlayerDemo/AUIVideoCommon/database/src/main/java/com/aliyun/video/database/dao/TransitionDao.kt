package com.aliyun.video.database.dao

import androidx.room.*
import com.aliyun.video.database.entity.DownloadResourceEntity

@Dao
interface TransitionDao {
    @Query("SELECT * FROM DOWNLOADRESOURCEENTITY")
    fun getAll(): List<DownloadResourceEntity>

    @Query("SELECT * FROM DownloadResourceEntity WHERE id =:id ")
    fun findByID(id: Long): DownloadResourceEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg info: DownloadResourceEntity)

    @Delete
    fun delete(info: DownloadResourceEntity)

    @Update
    fun updateInfo(vararg info: DownloadResourceEntity)
}