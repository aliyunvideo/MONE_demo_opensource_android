package com.aliyun.video.database.dao

import androidx.room.*
import com.aliyun.video.database.entity.VideoPlayRecordInfo

@Dao
interface VideoPlayRecordDao {

//    @Query("SELECT * FROM VideoPlayRecordInfo WHERE videoRecordKey =:videoRecordKey ")
//    fun getAll(videoRecordKey: VideoRecordKey): List<VideoPlayRecordInfo>

    @Query("SELECT * FROM VideoPlayRecordInfo WHERE uid =:uid AND video_id=:vid")
    fun findByID(uid: Long, vid: String): VideoPlayRecordInfo

    @Query("SELECT * from VideoPlayRecordInfo WHERE uid=:uid AND  video_id IN(:vidList)")
    fun findList(uid: Long, vidList: List<String>): List<VideoPlayRecordInfo?>

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg info: VideoPlayRecordInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(info: VideoPlayRecordInfo)

    @Delete
    fun delete(uid: VideoPlayRecordInfo)

    @Update(entity = VideoPlayRecordInfo::class)
    fun updateInfo(vararg info: VideoPlayRecordInfo)
}