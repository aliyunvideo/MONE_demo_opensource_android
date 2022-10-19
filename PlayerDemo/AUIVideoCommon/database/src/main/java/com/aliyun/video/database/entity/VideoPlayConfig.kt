package com.aliyun.video.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 播放配置，包含的配置
 *  1.feed 自动播放开关
 *  2.feed 流播放，视频开启音频
 *  3.是否支持续播
 *  4.弹幕开关
 *  5.弹幕位置
 *  6.后台播放，开关
 */
@Entity
data class VideoPlayConfig(
    @PrimaryKey()
    val uid: Long,
    @ColumnInfo(name = "list_play")
    var listPlayOpen: Boolean,
    @ColumnInfo(name = "list_play_mute")
    var listPlayMute: Boolean,
    @ColumnInfo(name = "contrast_play")
    var contrastPlay: Boolean,
    @ColumnInfo(name = "danmaku_open")
    var danmakuOpen: Boolean,
    @ColumnInfo(name = "danmaku_location")
    var danmakuLocation: Int,
    @ColumnInfo(name = "backgroud_play")
    var backgroundPlayOpen: Boolean
)