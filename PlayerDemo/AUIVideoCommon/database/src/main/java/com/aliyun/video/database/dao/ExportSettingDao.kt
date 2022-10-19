package com.aliyun.video.database.dao

import androidx.room.*
import com.aliyun.video.database.entity.ExportSettingInfo

@Dao
interface ExportSettingDao {
    @Query("SELECT * FROM EXPORTSETTINGINFO")
    fun getAll(): List<ExportSettingInfo>

    @Query("SELECT * FROM EXPORTSETTINGINFO WHERE uid =:uid ")
    fun findByID(uid: Long):ExportSettingInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg info: ExportSettingInfo)

    @Delete
    fun delete(info: ExportSettingInfo)

    @Update
    fun updateInfo(vararg info: ExportSettingInfo)
}