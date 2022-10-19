package com.aliyun.video.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

private const val BD_NAME = "jjvideo_bd"

object DataBase {
    var instance: AppDataBase? = null
    fun getDb(context: Context): AppDataBase {
        if (instance == null) {
            instance =
                Room.databaseBuilder(context.applicationContext, AppDataBase::class.java, BD_NAME)
                    .fallbackToDestructiveMigration() // 暂时
                    .build()
        }
        return instance!!
    }
}