package com.aliyun.player.alivcplayerexpand.listplay

import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.aliyun.player.AliListPlayer
import com.aliyun.player.AliPlayer.OnVerifyTimeExpireCallback
import com.aliyun.player.IPlayer.*
import com.aliyun.player.alivcplayerexpand.playlist.OnListPlayCallback
import com.aliyun.player.alivcplayerexpand.widget.IRenderView
import com.aliyun.player.nativeclass.MediaInfo
import com.aliyun.player.source.*

interface IListPlayManager {
    fun play(position: Int)
    fun play(uuid: String)
    fun pause()
    fun stop()
    fun release()
    fun continuePlay(pause: Boolean, vid: String, position: Int)
    fun isPlaying(): Boolean
    fun resumeListPlay()
    fun resumeSeriesPlay()
    fun resumePlay()
    fun isPlayComplete(): Boolean
    fun seekVideo(progress: Int)
    fun addPlayCallback(listPlayCallback: OnListPlayCallback)
    fun removePlayCallback(listPlayCallback: OnListPlayCallback)

    fun setSurfaceContainer(container: ViewGroup)
    fun setStsInfo(stsInfo: StsInfo?)
    fun getIRenderView(): IRenderView
    fun setVideoSpeed(speed: Float)

    /**
     * feed 流播放列表
     */
    fun setPlayList(
        list: MutableList<Pair<String, String>>,
        refresh: Boolean
    )

    fun recreateSurfaceView()

    /**
     * 剧集列表播放相关接口
     */
    fun playNextSeries(): Int
    fun playPreviousSeries(): Int
    fun getSeriesPosition(): Int
    fun playSeries(position: Int)
    fun setSeriesPlayList(list: MutableList<Pair<String, String>>, refresh: Boolean)
    fun clearSeriesPlayList()
    fun setSeriesPlayEnable(enablePlay: Boolean)
    fun hasNextSeries(): Boolean
    fun hasPreviousSeries(): Boolean
    fun getCurrentVideo(): MediaInfo
    fun setLifeCycle(lifecycle: Lifecycle?)

    fun savePlayRecord()
    fun getListPlayer(): AliListPlayer

    fun setAutoPlay(autoPlay: Boolean)
    fun set4GEnablePlay(enablePlay: Boolean)
    fun setContrastPlay(contrastPlay: Boolean)

    /**
     * 设置播放是否静音
     * @param mute true 静音 false 开启声音，默认静音
     */
    fun setPlayMute(mute: Boolean)

    /**
     * 设置为全局播放器，不受 LifeCycle 的影响
     */
    fun setGlobalPlayEnable(enable: Boolean)

    /**
     * 设置播放器的场景
     * @param scene 参考 [IPlayManagerScene]
     */
    fun setPlayerScene(scene: Int)
    fun getPlayerScene(): Int


    /** 下面的接口是为了代理播放器，实现一些特定功能 ***/
    open fun setDataSource(urlSource: UrlSource?): Unit

    fun setDataSource(vidSts: VidSts?)

    fun setDataSource(vidAuth: VidAuth?)

    fun setDataSource(vidMps: VidMps?)

    fun setDataSource(liveSts: LiveSts?)

    fun setDataSource(bitStreamSource: BitStreamSource?)
    fun setOnInfoListener(onInfoListener: OnInfoListener?)
    fun removeOnInfoListener(onInfoListener: OnInfoListener?)
    fun setOnCompletionListener(onCompletionListener: OnCompletionListener?)
    fun removeOnCompletionListener(onCompletionListener: OnCompletionListener?)
    fun setOnPreparedListener(onPreparedListener: OnPreparedListener?)
    fun removeOnPreparedListener(onPreparedListener: OnPreparedListener?)


    fun setOnRenderingStartListener(onRenderingStartListener: OnRenderingStartListener?)
    fun removeOnRenderingStartListener(onRenderingStartListener: OnRenderingStartListener?)

    fun setOnStateChangedListener(onStateChangedListener: OnStateChangedListener?)
    fun removeOnStateChangedListener(onStateChangedListener: OnStateChangedListener?)

    fun setOnLoadingStatusListener(onLoadingStatusListener: OnLoadingStatusListener?)
    fun removeOnLoadingStatusListener(onLoadingStatusListener: OnLoadingStatusListener?)

    fun setOnErrorListener(onErrorListener: OnErrorListener?)
    fun removeOnErrorListener(onErrorListener: OnErrorListener?)

    fun setOnChooseTrackIndexListener(onChooseTrackIndexListener: OnChooseTrackIndexListener?)
    fun removeOnChooseTrackIndexListener(onChooseTrackIndexListener: OnChooseTrackIndexListener?)

    fun setOnTrackReadyListener(onTrackReadyListener: OnTrackReadyListener?)
    fun removeOnTrackReadyListener(onTrackReadyListener: OnTrackReadyListener?)


    fun setOnVideoSizeChangedListener(onVideoSizeChangedListener: OnVideoSizeChangedListener?)
    fun removeOnVideoSizeChangedListener(onVideoSizeChangedListener: OnVideoSizeChangedListener?)

    fun setOnSeekCompleteListener(onSeekCompleteListener: OnSeekCompleteListener?)
    fun removeOnSeekCompleteListener(onSeekCompleteListener: OnSeekCompleteListener?)

    fun setOnTrackChangedListener(onTrackChangedListener: OnTrackChangedListener?)
    fun removeOnTrackChangedListener(onTrackChangedListener: OnTrackChangedListener?)

    fun setOnSeiDataListener(onSeiDataListener: OnSeiDataListener?)
    fun removeOnSeiDataListener(onSeiDataListener: OnSeiDataListener?)

    fun setOnSnapShotListener(listener: OnSnapShotListener?)
    fun removeOnSnapShotListener(listener: OnSnapShotListener?)

    fun setOnVerifyTimeExpireCallback(listener: OnVerifyTimeExpireCallback?)
    fun removeOnVerifyTimeExpireCallback(listener: OnVerifyTimeExpireCallback?)

    fun setOnSubtitleDisplayListener(listener: OnSubtitleDisplayListener?)
    fun removeOnSubtitleDisplayListener(listener: OnSubtitleDisplayListener?)
}