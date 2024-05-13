package com.aliyun.player.alivcplayerexpand.common

import android.content.Context
import com.aliyun.player.alivcplayerexpand.bean.AccountInfo
import com.aliyun.video.database.AppDataBase
import com.aliyun.video.database.DataBase
import com.aliyun.video.database.entity.VideoPlayRecordInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContrastPlayManager(context: Context) {
    private val mDataBase: AppDataBase = DataBase.getDb(context)
    private val mPlayRecordList = mutableMapOf<String, VideoPlayRecordInfo?>()
    private var mMaxExpiredSeconds = 30 * 24 * 3600


    fun setPlayList(vidList: List<String>) {
        GlobalScope.launch {
            val playRecordDao = mDataBase.playRecordDao()
            val playRecordList = playRecordDao.findList(AccountInfo.uid, vidList)
            //初始化一遍
            for (vid in vidList) {
                if (!mPlayRecordList.containsKey(vid)) {
                    mPlayRecordList[vid] =
                        VideoPlayRecordInfo(AccountInfo.uid, vid, 0, System.currentTimeMillis())
                }
            }
            for (record in playRecordList) {
                if (record != null) {
                    mPlayRecordList[record.vid] = record
                }
            }

        }
    }

    fun onPlayEnd(vid: String, playDuration: Int) {
        savePlayRecordInfo(vid, playDuration)
    }

    fun deleteAll() {
        GlobalScope.launch {
            val playRecordDao = mDataBase.playRecordDao()
//            playRecordDao.delete()
        }
    }

    private fun savePlayRecordInfo(vid: String, playDuration: Int) {
        //先更新本地数据
        if (mPlayRecordList[vid] != null) {
            mPlayRecordList[vid]?.apply {
                this.playDuration = playDuration
                this.lastPlayTimeMillis = System.currentTimeMillis()
            }
        }
        GlobalScope.launch {
            val playRecordDao = mDataBase.playRecordDao()
            var playRecordInfo: VideoPlayRecordInfo? =
                playRecordDao.findByID(AccountInfo.uid, vid)
//            val playList = playRecordDao.getAll(AccountInfo.uid)
            val endPlayMillis = System.currentTimeMillis()
            if (playRecordInfo == null) {
                playRecordInfo =
                    VideoPlayRecordInfo(
                        AccountInfo.uid,
                        vid,
                        playDuration,
                        endPlayMillis
                    )
                playRecordDao.insert(playRecordInfo)
            } else {
                playRecordInfo.apply {
                    this.playDuration = playDuration
                    this.lastPlayTimeMillis = endPlayMillis
                }

                playRecordDao.updateInfo(playRecordInfo)
            }
        }
    }

    fun getPlayRecord(vid: String, callback: OnGetPlayRecordInfoBack) {
        if (mPlayRecordList[vid] != null) {
            val recordPlayDuration = mPlayRecordList[vid].getRecordPlayDuration(mMaxExpiredSeconds)
            callback.onGetInfo(recordPlayDuration)
        } else {
            GlobalScope.launch {
                val playRecordDao = mDataBase.playRecordDao()
                val playRecordInfo: VideoPlayRecordInfo? =
                    playRecordDao.findByID(AccountInfo.uid, vid)
                withContext(Dispatchers.Main) {
                    callback.onGetInfo(playRecordInfo.getRecordPlayDuration(mMaxExpiredSeconds))
                }
            }
        }
    }

    private fun VideoPlayRecordInfo?.getRecordPlayDuration(expiredSeconds: Int): Int {
        return if (this == null) 0 else {
            val exceedSecond = (System.currentTimeMillis() - lastPlayTimeMillis) / 1000;
            if (exceedSecond >= expiredSeconds) {
                0
            } else {
                playDuration
            }
        }
    }

    fun setExpiredTime(expiredSeconds: Int) {
        if (expiredSeconds > 0) {
            mMaxExpiredSeconds = expiredSeconds
        }
    }

    companion object {
        private var mInstance: ContrastPlayManager? = null

        fun getInstance(context: Context): ContrastPlayManager {
            if (mInstance == null) {
                mInstance = ContrastPlayManager(context)
            }
            return mInstance!!
        }
    }


    interface OnGetPlayRecordInfoBack {
        fun onGetInfo(duration: Int)
    }
}