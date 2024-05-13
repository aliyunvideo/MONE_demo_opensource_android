package com.aliyun.player.alivcplayerexpand.util

import android.content.Context
import android.util.Log
import com.aliyun.player.alivcplayerexpand.bean.AccountInfo
import com.aliyun.video.database.AppDataBase
import com.aliyun.video.database.DataBase
import com.aliyun.video.database.entity.VideoPlayConfig
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val TAG = "PlayConfigManager"

object PlayConfigManager {
    private var mInit = false
    private var mConfigIsDefault = true
    private lateinit var mAppDataBase: AppDataBase
    private var mPlayConfig: VideoPlayConfig = VideoPlayConfig(
        AccountInfo.uid,
        listPlayOpen = true,
        listPlayMute = true,
        contrastPlay = true,
        danmakuOpen = true,
        danmakuLocation = 2,
        backgroundPlayOpen = true
    )

    fun init(context: Context): PlayConfigManager {
        Log.i(TAG, "init start")
        if (!mInit) {
            mInit = true
            mAppDataBase = DataBase.getDb(context)
        }
        Log.i(TAG, "init end")
        if (mConfigIsDefault) {
            GlobalScope.launch {
                val playConfigDao = mAppDataBase.playConfigDao()
                val playConfig: VideoPlayConfig? = playConfigDao.findByID(AccountInfo.uid)
                if (playConfig != null) {
                    mConfigIsDefault = false
                    mPlayConfig = playConfig
                    mPlayConfig.listPlayMute = true
                    Log.i(TAG, "init data end! mPlayConfig:$mPlayConfig")
                }
            }
        }
        return this
    }

    fun getPlayConfig(): VideoPlayConfig {
        return mPlayConfig
    }

    fun update() {
        GlobalScope.launch {
            try {
                val playConfigDao = mAppDataBase.playConfigDao()
                if (mConfigIsDefault) {
                    playConfigDao.insertAll(mPlayConfig)
                } else {
                    playConfigDao.updateInfo(mPlayConfig)
                }
            } catch (e: Exception) {

            }
        }
    }
}