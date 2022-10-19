package com.aliyun.player.alivcplayerexpand.listplay

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.aliyun.player.*
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.common.ContrastPlayManager
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.player.alivcplayerexpand.playlist.OnListPlayCallback
import com.aliyun.player.alivcplayerexpand.widget.IRenderView
import com.aliyun.player.aliyunplayerbase.util.NetWatchdog
import com.aliyun.player.aliyunplayerbase.util.removeSelf
import com.aliyun.player.bean.ErrorInfo
import com.aliyun.player.bean.InfoBean
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.nativeclass.MediaInfo
import com.aliyun.player.nativeclass.TrackInfo
import com.aliyun.player.source.*
import com.aliyun.thumbnail.ThumbnailBitmapInfo
import com.cicada.player.utils.Logger
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.collections.set


private const val TAG = "ListPlayManager"

class ListPlayManager(var lifecycle: Lifecycle?) : IListPlayManager {
    private lateinit var mListPlayer: AliListPlayer
    private lateinit var mContrastPlayManager: ContrastPlayManager
    var mHasNext = true
    private var mListPlayerContainer: FrameLayout? = null
    private var mLastSurfaceViewContainer: ViewGroup? = null
    private var mListPlayerTSurfaceView: SurfaceView? = null
    private var mSurfaceDestroy = false
    private val mOnPreparedListeners = mutableSetOf<IPlayer.OnPreparedListener>()
    private val mOnCompletionListeners = mutableSetOf<IPlayer.OnCompletionListener>()
    private val mOnInfoListeners = mutableSetOf<IPlayer.OnInfoListener>()
    private val mOnRenderingStartListeners = mutableSetOf<IPlayer.OnRenderingStartListener>()
    private val mOnLoadingStatusListeners = mutableSetOf<IPlayer.OnLoadingStatusListener>()
    private val mOnSeiDataListeners = mutableSetOf<IPlayer.OnSeiDataListener>()
    private val mOnStateChangedListeners = mutableSetOf<IPlayer.OnStateChangedListener>()
    private val mOnTrackChangedListeners = mutableSetOf<IPlayer.OnTrackChangedListener>()
    private val mOnTrackReadyListeners = mutableSetOf<IPlayer.OnTrackReadyListener>()
    private val mOnVideoSizeChangedListeners = mutableSetOf<IPlayer.OnVideoSizeChangedListener>()
    private val mOnSeekCompleteListeners = mutableSetOf<IPlayer.OnSeekCompleteListener>()
    private val mOnErrorListeners = mutableSetOf<IPlayer.OnErrorListener>()
    private val mOnSnapShotListeners = mutableSetOf<IPlayer.OnSnapShotListener>()
    private val mOnVideoRenderedListeners = mutableSetOf<IPlayer.OnVideoRenderedListener>()
    private val mOnSubtitleDisplayListeners = mutableSetOf<IPlayer.OnSubtitleDisplayListener>()

    private lateinit var mContext: Context
    private var mCurrentPosition: Int = 0
    private var mCurrentPlayDuration: Int = 0
    private var mListPlayCallback = mutableSetOf<OnListPlayCallback?>()
    private val mPlayInfo = mutableListOf<Pair<String, String>>()
    private val mSeriesList = mutableListOf<Pair<String, String>>()
    private var mLifeCyclePause = false
    private var mBeforePauseIsPlaying = false
    private var mPlayList = false
    private var mPlaying = false
    private var mPlayComplete = false
    private var mNetWatchdog: NetWatchdog? = null
    private var mSeriesPosition = -1
    private var mCurrentVid = ""
    private var mSeekDuration = 0L


    private val mCompletionListener = object : IPlayer.OnCompletionListener {
        override fun onCompletion() {
            mOnCompletionListeners.forEach {
                it.onCompletion()
            }
            onPlayComplete()
        }
    }
    private val mOnErrorListener = object : IPlayer.OnErrorListener {
        override fun onError(error: ErrorInfo?) {
        }
    }

    //prepare 成功，调用 start，开始播放视频
    private val mOnPrepareListener = object : IPlayer.OnPreparedListener {
        override fun onPrepared() {
            mPlayComplete = false
            mListPlayCallback.forEach {
                it?.onPrepare()
            }
            if (!mLifeCyclePause || mGlobalPlayEnable) {
                mOnPreparedListeners.forEach {
                    it.onPrepared()
                }
                if (mContrastPlayEnable) {
                    mListPlayer.seekTo(mSeekDuration, IPlayer.SeekMode.Accurate)
                }
                mListPlayer.start()
                resetPlayConfig()
            }
        }
    }

    //首帧开始渲染
    private val mFirstFrameListener = object : IPlayer.OnRenderingStartListener {
        override fun onRenderingStart() {
            mOnRenderingStartListeners.forEach {
                it.onRenderingStart()
            }
            mListPlayCallback.forEach {
                it?.onPlaying()
            }
            if (mContrastPlayEnable && mSeekDuration > 0) {
                mListPlayCallback.forEach {
                    it?.onContrastPlay(mSeekDuration.toInt())
                }
                mSeekDuration = 0
            }
        }
    }

    private val mOnSizeChangeListener =
        IPlayer.OnVideoSizeChangedListener { width, height ->
            mOnVideoSizeChangedListeners.forEach {
                it.onVideoSizeChanged(width, height)
            }
        }

    private val mPlayInfoListener =
        IPlayer.OnInfoListener { info -> //
            if (info != null) {
                mOnInfoListeners.forEach {
                    it.onInfo(info)
                }
                handlePlayInfo(info)
            }
        }

    private val mOnLoadingStatusListener = object : IPlayer.OnLoadingStatusListener {
        override fun onLoadingEnd() {
            mOnLoadingStatusListeners.forEach {
                it.onLoadingEnd()
            }
        }

        override fun onLoadingBegin() {
            mOnLoadingStatusListeners.forEach {
                it.onLoadingBegin()
            }
        }

        override fun onLoadingProgress(percent: Int, netSpeed: Float) {
            mOnLoadingStatusListeners.forEach {
                it.onLoadingProgress(percent, netSpeed)
            }
        }

    }

    private val mOnSeiDataListener = IPlayer.OnSeiDataListener { type, data ->
        mOnSeiDataListeners.forEach {
            it.onSeiData(type, data)
        }
    }

    private val mOnStateChangedListener = IPlayer.OnStateChangedListener { state ->
        mOnStateChangedListeners.forEach {
            it.onStateChanged(state)
        }
    }

    private val mOnTrackChangedListener = object : IPlayer.OnTrackChangedListener {
        override fun onChangedSuccess(trackInfo: TrackInfo?) {
            mOnTrackChangedListeners.forEach {
                it.onChangedSuccess(trackInfo)
            }
        }

        override fun onChangedFail(trackInfo: TrackInfo?, errorInfo: ErrorInfo?) {
            mOnTrackChangedListeners.forEach {
                it.onChangedFail(trackInfo, errorInfo)
            }
        }
    }

    private val mOnTrackReadyListener = object : IPlayer.OnTrackReadyListener {
        override fun onTrackReady(mediaInfo: MediaInfo?) {
            mOnTrackReadyListeners.forEach {
                it.onTrackReady(mediaInfo)
            }
        }
    }

    private val mOnVideoSizeChangedListener = object : IPlayer.OnVideoSizeChangedListener {
        override fun onVideoSizeChanged(width: Int, height: Int) {
            mOnVideoSizeChangedListeners.forEach {
                it.onVideoSizeChanged(width, height)
            }
        }
    }

    private val mOnSeekCompleteListener = object : IPlayer.OnSeekCompleteListener {
        override fun onSeekComplete() {
            mOnSeekCompleteListeners.forEach {
                it.onSeekComplete()
            }
        }
    }

    private val mOnSnapShotListener = object : IPlayer.OnSnapShotListener {
        override fun onSnapShot(bitmap: Bitmap?, with: Int, height: Int) {
            mOnSnapShotListeners.forEach {
                it.onSnapShot(bitmap, with, height)
            }
        }
    }

    private val mOnSubtitleDisplayListener = object : IPlayer.OnSubtitleDisplayListener {
        override fun onSubtitleExtAdded(p0: Int, p1: String?) {
            mOnSubtitleDisplayListeners.forEach {
                it.onSubtitleExtAdded(p0, p1)
            }
        }

        override fun onSubtitleShow(p0: Int, p1: Long, p2: String?) {
            mOnSubtitleDisplayListeners.forEach {
                it.onSubtitleShow(p0, p1, p2)
            }
        }

        override fun onSubtitleHide(p0: Int, p1: Long) {
            mOnSubtitleDisplayListeners.forEach {
                it.onSubtitleHide(p0, p1)
            }
        }

        override fun onSubtitleHeader(p0: Int, p1: String?) {
        }
    }


    private val mNetWorkChangeListener = object : NetWatchdog.NetChangeListener {
        override fun on4GToWifi() {
        }

        override fun onNetDisconnected() {
        }

        override fun onWifiTo4G() {
        }
    }

    private val mNetWorkConnectListener = object : NetWatchdog.NetConnectedListener {
        override fun onReNetConnected(isReconnect: Boolean) {
        }

        override fun onNetUnConnected() {
        }
    }

    private val mThumbnailListener = object : IPlayer.OnThumbnailListener {
        override fun onGetFail(position: Long, errorInfo: ErrorInfo?) {
        }

        override fun onGetSuccess(position: Long, result: ThumbnailBitmapInfo?) {

        }
    }

    private val mLifecycleEventObserver = object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                }
                Lifecycle.Event.ON_START -> {
                }
                Lifecycle.Event.ON_RESUME -> {
                    mLifeCyclePause = false
                    if (!mGlobalPlayEnable) {
                        if (mBeforePauseIsPlaying) {
                            if (getPlayerScene() != IPlayManagerScene.SCENE_FLOAT_PLAY) {
                                resumeListPlay()
                            }
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mLifeCyclePause = true
                    if (!mGlobalPlayEnable) {
                        mBeforePauseIsPlaying = mPlaying
                        if (getPlayerScene() != IPlayManagerScene.SCENE_FLOAT_PLAY) {
                            pause()
                        }
                    }
                    onRecordProgress(mCurrentPlayDuration)
                }
                Lifecycle.Event.ON_STOP -> {
                }
                Lifecycle.Event.ON_DESTROY -> {
                }
            }
        }

    }

    /**
     *
     * int idle = 0; int initalized = 1; int prepared = 2; int started = 3;
     * int paused = 4; int stopped = 5; int completion = 6; int error = 7;
     */
    private val mStateChangedListener = IPlayer.OnStateChangedListener { state ->
        GlobalPlayerConfig.PlayState.playState = state
        mOnStateChangedListeners.forEach {
            it.onStateChanged(state)
        }
    }


    @SuppressLint("InvalidWakeLockTag")
    fun init(context: Context) {
        mContext = context
        mLifeCyclePause = false
        mListPlayer = AliPlayerFactory.createAliListPlayer(context)
        mContrastPlayManager = ContrastPlayManager.getInstance(context)
        lifecycle?.addObserver(mLifecycleEventObserver)
        setUpListener()
        setUpPlayerConfig()
        initListPlayerView()
        initNetWatchdog(context)
        enableNativeLog(context)
    }

    private fun enableNativeLog(context: Context) {
        Logger.getInstance(context).enableConsoleLog(true)
        Logger.getInstance(context).logLevel = Logger.LogLevel.AF_LOG_LEVEL_DEBUG
    }

    private fun setUpListener() {
        //播放完成监听
        mListPlayer.setOnCompletionListener(mCompletionListener)
        //播放错误
        mListPlayer.setOnErrorListener(mOnErrorListener)
        //缓冲成功
        mListPlayer.setOnPreparedListener(mOnPrepareListener)
        //首帧播放成功
        mListPlayer.setOnRenderingStartListener(mFirstFrameListener)
        //分辨率切换
        mListPlayer.setOnVideoSizeChangedListener(mOnSizeChangeListener)
        //播放器事件变化
        mListPlayer.setOnInfoListener(mPlayInfoListener)
        mListPlayer.setOnLoadingStatusListener(mOnLoadingStatusListener)
        mListPlayer.setOnSeiDataListener(mOnSeiDataListener)
        mListPlayer.setOnStateChangedListener(mOnStateChangedListener)
        mListPlayer.setOnStateChangedListener(mStateChangedListener)
        mListPlayer.setOnTrackReadyListener(mOnTrackReadyListener)
        mListPlayer.setOnTrackChangedListener(mOnTrackChangedListener)
        mListPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener)
        mListPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener)
        mListPlayer.setOnSnapShotListener(mOnSnapShotListener)
        mListPlayer.setOnSubtitleDisplayListener(mOnSubtitleDisplayListener)

    }

    private fun setUpPlayerConfig() {
        mListPlayer.apply {
            isLoop = false
            val playConfig = config
            playConfig.mClearFrameWhenStop = false
            //是否清楚最后一帧
            config = playConfig
            setDefinition("HD")
        }
    }

    private fun initNetWatchdog(context: Context) {
        if (mNetWatchdog == null) {
            mNetWatchdog = NetWatchdog(context)
        }
        mNetWatchdog?.setNetChangeListener(mNetWorkChangeListener)
        mNetWatchdog?.setNetConnectedListener(mNetWorkConnectListener)
    }


    override fun play(position: Int) {
        if (mLifeCyclePause && !mGlobalPlayEnable || position >= mPlayInfo.size) return
        mPlayList = true
        onRecordProgress(mCurrentPlayDuration)
        mCurrentPosition = position
        val uuid: String = mPlayInfo[mCurrentPosition].second
        mCurrentVid = mPlayInfo[mCurrentPosition].first
        play(uuid)
    }

    private fun replayCurrent() {
        val vid = mPlayInfo[mCurrentPosition].first
        val vidSts = getVidSts(vid)
        mListPlayer.apply {
            setDataSource(vidSts)
            prepare()
        }
    }


    private fun getVidSts(vid: String?): VidSts {
        val vidSts = VidSts()
        vidSts.vid = vid
        vidSts.region = GlobalPlayerConfig.mRegion
        vidSts.accessKeyId = mStsInfo!!.accessKeyId
        vidSts.securityToken = mStsInfo!!.securityToken
        vidSts.accessKeySecret = mStsInfo!!.accessKeySecret
        //试看
        if (GlobalPlayerConfig.mPreviewTime > 0) {
            val configGen = VidPlayerConfigGen()
            configGen.setPreviewTime(GlobalPlayerConfig.mPreviewTime)
            vidSts.setPlayConfig(configGen)
        }
        if (GlobalPlayerConfig.PlayConfig.mAutoSwitchOpen) {
            val list: MutableList<Definition> = ArrayList()
            list.add(Definition.DEFINITION_AUTO)
            vidSts.setDefinition(list)
        }
        return vidSts
    }

    override fun play(uuid: String) {
        mPlaying = true
        mSeekDuration = 0L
        mCurrentListPlayManager = this
        resetPlayConfig()
        updatePlayVid(uuid)
        if (mContrastPlayEnable) {
            mContrastPlayManager.getPlayRecord(mCurrentVid,
                object : ContrastPlayManager.OnGetPlayRecordInfoBack {
                    override fun onGetInfo(duration: Int) {
                        val ret = mListPlayer.moveTo(uuid, mStsInfo)
                        mSeekDuration = duration.toLong()
                    }
                })
        } else {
            val ret = mListPlayer.moveTo(uuid, mStsInfo)
        }
    }

    private fun updatePlayVid(uuid: String) {
        for (videoinfo in mPlayInfo) {
            if (uuid == videoinfo.second) {
                mCurrentVid = videoinfo.first
            }
        }
    }

    private fun resetPlayConfig() {
        mListPlayer.speed = 1.0f
    }


    override fun pause() {
        mListPlayer.pause()
        mListPlayCallback.forEach {
            it?.onPause()
        }
        mPlaying = false
    }

    override fun resumeListPlay() {
        mCurrentListPlayManager = this
        val playComplete = mPlayComplete
        if (playComplete) {
            play(mCurrentPosition)
        } else {
            mListPlayer.start()
        }
        mPlaying = true
        mListPlayCallback.forEach {
            it?.onPlaying()
        }
    }

    override fun resumePlay() {
        if (mPlayList) {
            resumeListPlay()
        } else {
            resumeSeriesPlay()
        }
    }

    override fun resumeSeriesPlay() {
        mCurrentListPlayManager = this
        val playComplete = mPlayComplete
        if (playComplete) {
            playSeries(mSeriesPosition)
        } else {
            mListPlayer.start()
        }
        mPlaying = true
        mListPlayCallback.forEach {
            it?.onPlaying()
        }
    }

    override fun getListPlayer(): AliListPlayer {
        return mListPlayer
    }

    override fun seekVideo(progress: Int) {
        val seekTimes: Long = (mListPlayer.mediaInfo.duration * (progress.toFloat() / 100)).toLong()
        mListPlayer.seekTo(seekTimes, IPlayer.SeekMode.Accurate)
    }

    override fun addPlayCallback(listPlayCallback: OnListPlayCallback) {
        mListPlayCallback.add(listPlayCallback)
    }

    override fun removePlayCallback(listPlayCallback: OnListPlayCallback) {
        mListPlayCallback.remove(listPlayCallback)
    }

    override fun stop() {
        mListPlayer.stop()
        mListPlayCallback.forEach {
            it?.onPause()
        }
        mPlaying = false
    }

    override fun release() {
        mListPlayer.stop()
        mListPlayer.release()
    }

    override fun recreateSurfaceView() {
        if (mSurfaceDestroy) {
            initListPlayerView()
        }
    }

    override fun setPlayList(
        list: MutableList<Pair<String, String>>,
        refresh: Boolean
    ) {
        if (refresh) {
            mListPlayer.clear()
            mPlayInfo.clear()
        }
        mPlayInfo.addAll(list)
        for (videoInfo in list) {
            mListPlayer.addVid(videoInfo.first, videoInfo.second)
        }
        if (mContrastPlayEnable) {
            getPlayRecordCache(list)
        }
    }

    override fun setSeriesPlayEnable(enablePlay: Boolean) {
        mSeriesPlayEnable = enablePlay
    }

    override fun hasNextSeries(): Boolean {
        return mSeriesList.isNotEmpty() && mSeriesPosition < mSeriesList.size - 1
    }

    override fun hasPreviousSeries(): Boolean {
        return mSeriesList.isNotEmpty() && mSeriesPosition > 0
    }

    override fun getCurrentVideo(): MediaInfo {
        if (mListPlayer.mediaInfo == null)
            return MediaInfo()
        return mListPlayer.mediaInfo
    }

    override fun setLifeCycle(lifecycle: Lifecycle?) {
        this.lifecycle?.removeObserver(mLifecycleEventObserver)
        this.lifecycle = lifecycle
        this.lifecycle?.addObserver(mLifecycleEventObserver)
    }

    override fun setSeriesPlayList(list: MutableList<Pair<String, String>>, refresh: Boolean) {
        if (refresh) {
            mSeriesList.clear()
        }
        mSeriesList.addAll(list)
        for (index in list.indices) {
            if (list[index].first == mCurrentVid) {
                mSeriesPosition = index
            }
            mListPlayer.addVid(list[index].first, list[index].second)
        }
    }

    override fun clearSeriesPlayList() {
        mSeriesList.clear()
        mSeriesPosition = -1
    }

    override fun playNextSeries(): Int {
        if (mSeriesPosition < mSeriesList.size - 1) {
            mSeriesPosition++
            playSeries(mSeriesPosition)
        }
        return mSeriesPosition
    }

    override fun playPreviousSeries(): Int {
        if (mSeriesPosition > 0) {
            mSeriesPosition--
            playSeries(mSeriesPosition)
        }
        return mSeriesPosition
    }

    override fun getSeriesPosition(): Int {
        return mSeriesPosition
    }

    override fun playSeries(position: Int) {
        mPlaying = true
        mPlayList = false
        if (position >= 0 && position < mSeriesList.size) {
            resetPlayConfig()
            onRecordProgress(mCurrentVid, mCurrentPlayDuration)
            mCurrentVid = mSeriesList[position].first
            mSeriesPosition = position
            val uuid = mSeriesList[position].second

            if (mContrastPlayEnable) {
                mContrastPlayManager.getPlayRecord(mCurrentVid,
                    object : ContrastPlayManager.OnGetPlayRecordInfoBack {
                        override fun onGetInfo(duration: Int) {
                            val ret = mListPlayer.moveTo(uuid, mStsInfo)
                            mSeekDuration = duration.toLong()
                        }
                    })
            } else {
                val ret = mListPlayer.moveTo(uuid, mStsInfo)
            }
        }
    }


    override fun savePlayRecord() {
        onRecordProgress(mCurrentPlayDuration)
    }

    private fun getPlayRecordCache(list: MutableList<Pair<String, String>>) {
        val videoList = mutableListOf<String>()
        for (info in list) {
            videoList.add(info.second)
        }
        mContrastPlayManager.setPlayList(videoList)
    }

    override fun continuePlay(pause: Boolean, vid: String, position: Int) {
        if (mListPlayerTSurfaceView?.parent != mListPlayerContainer) {
            mListPlayerTSurfaceView?.removeSelf()
            val lp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            mListPlayerContainer?.addView(mListPlayerTSurfaceView, lp)
        }
        setUpListener()
        if (vid != mCurrentVid) {
            //从头开始播放
            play(position)
        } else {
            mListPlayCallback.forEach {
                it?.onPlaying()
            }
        }
        mPlaying = !pause
    }

    override fun isPlaying() = mPlaying

    override fun isPlayComplete() = mPlayComplete


    override fun setSurfaceContainer(container: ViewGroup) {
        val lp = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mListPlayerContainer?.removeSelf()
        mLastSurfaceViewContainer = container
        container.addView(mListPlayerContainer, lp)
    }

    override fun setStsInfo(stsInfo: StsInfo?) {
        mStsInfo = stsInfo
    }

    override fun getIRenderView(): IRenderView {
        return mListPlayerTSurfaceView as IRenderView
    }

    override fun setVideoSpeed(speed: Float) {
        mListPlayer.speed = speed
    }

    private fun StsInfo.printf(): String {
        return "[ accessKeyId:${this.accessKeyId} accessKeySecret:${this.accessKeySecret} " +
                "mSecurityToken:${securityToken} mRegion:${this.region}  mFormats:${this.formats}"
    }

    private fun InfoBean.printf(): String {
        return "[mCode:$code mExtraValue:$extraValue mExtraMsg:$extraMsg]"
    }

    /**
     * 初始化播放界面
     */
    private fun initListPlayerView() {
        mListPlayerContainer =
            View.inflate(mContext, R.layout.layout_list_player_surface_view, null) as FrameLayout?
        mListPlayerTSurfaceView = mListPlayerContainer?.findViewById(R.id.list_player_surfaceview)

        mListPlayerTSurfaceView?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                mListPlayer.redraw()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                mSurfaceDestroy = true
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                mListPlayer.setDisplay(holder)
                mSurfaceDestroy = false
            }

        })
    }

    override fun setAutoPlay(autoPlay: Boolean) {
    }

    override fun set4GEnablePlay(enablePlay: Boolean) {
    }

    override fun setContrastPlay(contrastPlay: Boolean) {
        mContrastPlayEnable = contrastPlay
    }

    override fun setPlayMute(mute: Boolean) {
        mListPlayer.isMute = mute
        setGlobalPlayEnable(!mute)
    }

    override fun setGlobalPlayEnable(enable: Boolean) {
        mGlobalPlayEnable = enable
    }

    override fun setPlayerScene(scene: Int) {
        mScene = scene
        if (mScene == IPlayManagerScene.SCENE_FLOAT_PLAY
            || mScene == IPlayManagerScene.SCENE_ONLY_VOICE
        ) {
            setGlobalPlayEnable(true)
        }
    }

    override fun getPlayerScene(): Int {
        return mScene
    }

    private fun ErrorInfo.printf(): String {
        return "[code:${code} msg:${msg} ext:${extra}]"
    }

    private fun handlePlayInfo(infoBean: InfoBean) {
        when (infoBean.code) {
            InfoCode.AutoPlayStart -> {
                //自动播放开始,需要设置播放状态
            }
            InfoCode.BufferedPosition -> {
                //更新bufferedPosition
            }
            InfoCode.CurrentPosition -> {
                if (mListPlayer.mediaInfo == null)
                    return
                //更新播放进度
                mCurrentPlayDuration = infoBean.extraValue.toInt()
                val videoDuration = mListPlayer.mediaInfo.duration
                mListPlayCallback.forEach {
                    it?.onPlayProgress(
                        mCurrentPlayDuration.toFloat() / videoDuration,
                        mCurrentPlayDuration,
                        videoDuration
                    )
                }
            }
        }

    }

    /**
     * 记录播放记录，记录时机
     * 1.如果后台之类的，记录当前播放
     * 2.如果用户触发新的播放视频，则记录上一个播放的进度
     */
    private fun onRecordProgress(playDuration: Int) {
        if (mPlayComplete || !mContrastPlayEnable || mCurrentVid.isEmpty()) return
        mContrastPlayManager.onPlayEnd(mCurrentVid, playDuration)
    }

    private fun onRecordProgress(vid: String, playDuration: Int) {
        if (mPlayComplete || !mContrastPlayEnable || mCurrentVid.isEmpty()) return
        mContrastPlayManager.onPlayEnd(vid, playDuration)

    }

    private fun onPlayComplete() {
        if (mCurrentVid.isEmpty())  return
        mCurrentPlayDuration = 0
        mContrastPlayManager.onPlayEnd(mCurrentVid, 0)
        mPlayComplete = true
        mPlaying = false
        mListPlayer.stop()
        mListPlayCallback.forEach {
            it?.onPlayComplete()
        }
    }


    override fun setOnPreparedListener(onPreparedListener: IPlayer.OnPreparedListener?) {
        if (onPreparedListener != null) {
            mOnPreparedListeners.add(onPreparedListener)
        }
    }

    override fun removeOnPreparedListener(onPreparedListener: IPlayer.OnPreparedListener?) {
        mOnPreparedListeners.remove(onPreparedListener)
    }

    override fun setOnRenderingStartListener(onRenderingStartListener: IPlayer.OnRenderingStartListener?) {
        if (onRenderingStartListener != null) {
            mOnRenderingStartListeners.add(onRenderingStartListener)
        }
    }

    override fun removeOnRenderingStartListener(onRenderingStartListener: IPlayer.OnRenderingStartListener?) {
        mOnRenderingStartListeners.remove(onRenderingStartListener)

    }

    override fun setOnStateChangedListener(listener: IPlayer.OnStateChangedListener?) {
        if (listener != null) {
            mOnStateChangedListeners.add(listener)
        }
    }

    override fun removeOnStateChangedListener(onStateChangedListener: IPlayer.OnStateChangedListener?) {
        mOnStateChangedListeners.remove(onStateChangedListener)
    }

    override fun setOnCompletionListener(listener: IPlayer.OnCompletionListener?) {
        if (listener != null) {
            mOnCompletionListeners.add(listener)
        }
    }

    override fun removeOnCompletionListener(onCompletionListener: IPlayer.OnCompletionListener?) {
        mOnCompletionListeners.remove(onCompletionListener)
    }

    override fun setOnLoadingStatusListener(listener: IPlayer.OnLoadingStatusListener?) {
        if (listener != null) {
            mOnLoadingStatusListeners.add(listener)
        }
    }

    override fun removeOnLoadingStatusListener(onLoadingStatusListener: IPlayer.OnLoadingStatusListener?) {
        mOnLoadingStatusListeners.remove(onLoadingStatusListener)
    }

    override fun setOnErrorListener(listener: IPlayer.OnErrorListener?) {
        if (listener != null) {
            mOnErrorListeners.add(listener)
        }
    }

    override fun removeOnErrorListener(onErrorListener: IPlayer.OnErrorListener?) {
        mOnErrorListeners.remove(onErrorListener)
    }

    override fun setOnChooseTrackIndexListener(listener: IPlayer.OnChooseTrackIndexListener?) {
        mListPlayer?.setOnChooseTrackIndexListener(listener)
    }

    override fun removeOnChooseTrackIndexListener(listener: IPlayer.OnChooseTrackIndexListener?) {
        mListPlayer?.setOnChooseTrackIndexListener(null)
    }

    override fun setOnTrackReadyListener(listener: IPlayer.OnTrackReadyListener?) {
        if (listener != null) {
            mOnTrackReadyListeners.add(listener)
        }
    }

    override fun removeOnTrackReadyListener(listener: IPlayer.OnTrackReadyListener?) {
        mOnTrackReadyListeners.remove(listener)
    }

    override fun setOnInfoListener(listener: IPlayer.OnInfoListener?) {
        if (listener != null) {
            mOnInfoListeners.add(listener)
        }
    }

    override fun removeOnInfoListener(onInfoListener: IPlayer.OnInfoListener?) {
        mOnInfoListeners.remove(onInfoListener)
    }

    override fun setOnVideoSizeChangedListener(listener: IPlayer.OnVideoSizeChangedListener?) {
        if (listener != null) {
            mOnVideoSizeChangedListeners.add(listener)
        }
    }

    override fun removeOnVideoSizeChangedListener(onVideoSizeChangedListener: IPlayer.OnVideoSizeChangedListener?) {
        mOnVideoSizeChangedListeners.remove(onVideoSizeChangedListener)
    }

    override fun setOnSeekCompleteListener(listener: IPlayer.OnSeekCompleteListener?) {
        if (listener != null) {
            mOnSeekCompleteListeners.add(listener)
        }
    }

    override fun removeOnSeekCompleteListener(onSeekCompleteListener: IPlayer.OnSeekCompleteListener?) {
        mOnSeekCompleteListeners.remove(onSeekCompleteListener)
    }

    override fun setOnTrackChangedListener(listener: IPlayer.OnTrackChangedListener?) {
        if (listener != null) {
            mOnTrackChangedListeners.add(listener)
        }
    }

    override fun removeOnTrackChangedListener(onTrackChangedListener: IPlayer.OnTrackChangedListener?) {
        mOnTrackChangedListeners.remove(onTrackChangedListener)
    }

    override fun setOnSeiDataListener(listener: IPlayer.OnSeiDataListener?) {
        if (listener != null) {
            mOnSeiDataListeners.add(listener)
        }
    }

    override fun removeOnSeiDataListener(onSeiDataListener: IPlayer.OnSeiDataListener?) {
        mOnSeiDataListeners.remove(onSeiDataListener)
    }


    override fun setOnSnapShotListener(listener: IPlayer.OnSnapShotListener?) {
        if (listener != null) {
            mOnSnapShotListeners.add(listener)
        }
    }

    override fun removeOnSnapShotListener(listener: IPlayer.OnSnapShotListener?) {
        mOnSnapShotListeners.remove(listener)
    }

    override fun setOnVerifyTimeExpireCallback(listener: AliPlayer.OnVerifyTimeExpireCallback?) {
        mListPlayer?.setOnVerifyTimeExpireCallback(listener)
    }

    override fun removeOnVerifyTimeExpireCallback(listener: AliPlayer.OnVerifyTimeExpireCallback?) {
        mListPlayer?.setOnVerifyTimeExpireCallback(null)
    }

    override fun setOnSubtitleDisplayListener(listener: IPlayer.OnSubtitleDisplayListener?) {
        if (listener != null) {
            mOnSubtitleDisplayListeners.add(listener)
        }
    }

    override fun removeOnSubtitleDisplayListener(listener: IPlayer.OnSubtitleDisplayListener?) {
        mOnSubtitleDisplayListeners.remove(listener)
    }

    override fun setDataSource(urlSource: UrlSource?) {
        if (urlSource != null) {
            if (urlSource.uri != null) {
                onRecordProgress(mCurrentPlayDuration)
                mCurrentVid = urlSource.uri
                mListPlayer.setDataSource(urlSource)
            }
        }
    }

    override fun setDataSource(vidSts: VidSts?) {
        if (vidSts != null) {
            if (vidSts.vid != null) {
                onRecordProgress(mCurrentPlayDuration)
                mCurrentVid = vidSts.vid
                mPlaying = true
                mSeekDuration = 0L
                resetPlayConfig()
                if (mContrastPlayEnable) {
                    mContrastPlayManager.getPlayRecord(mCurrentVid,
                        object : ContrastPlayManager.OnGetPlayRecordInfoBack {
                            override fun onGetInfo(duration: Int) {
                                mListPlayer.setDataSource(vidSts)
                                mSeekDuration = duration.toLong()
                            }
                        })
                } else {
                    mListPlayer.setDataSource(vidSts)
                }
            }
        }
    }

    override fun setDataSource(vidAuth: VidAuth?) {
        if (vidAuth != null) {
            if (vidAuth.vid != null) {
                onRecordProgress(mCurrentPlayDuration)
                mCurrentVid = vidAuth.vid
                mPlaying = true
                mSeekDuration = 0L
                resetPlayConfig()
                if (mContrastPlayEnable) {
                    mContrastPlayManager.getPlayRecord(mCurrentVid,
                        object : ContrastPlayManager.OnGetPlayRecordInfoBack {
                            override fun onGetInfo(duration: Int) {
                                mListPlayer.setDataSource(vidAuth)
                                mSeekDuration = duration.toLong()
                            }
                        })
                } else {
                    mListPlayer.setDataSource(vidAuth)
                }
            }
        }
    }


    override fun setDataSource(vidMps: VidMps?) {
        if (vidMps != null) {
            mListPlayer.setDataSource(vidMps)
        }
    }

    override fun setDataSource(liveSts: LiveSts?) {
        if (liveSts != null)
            mListPlayer.setDataSource(liveSts)
    }

    override fun setDataSource(bitStreamSource: BitStreamSource?) {
        if (bitStreamSource != null)
            mListPlayer.setDataSource(bitStreamSource)
    }


    companion object GlobalPlayer {
        private val mListPlayManagerList = LinkedHashMap<Lifecycle, ListPlayManager>()
        var mCurrentListPlayManager: ListPlayManager? = null
        private var mScene = 0
        private var mContrastPlayEnable = true
        var mGlobalPlayEnable = false
        var mSeriesPlayEnable = true

        var mStsInfo: StsInfo? = null

        fun getListPlayManager(lifecycle: Lifecycle): ListPlayManager {
            if (mListPlayManagerList[lifecycle] == null) {
                mListPlayManagerList[lifecycle] = ListPlayManager(lifecycle)
            }
            mCurrentListPlayManager = mListPlayManagerList[lifecycle]!!
            return mCurrentListPlayManager!!
        }

        /**
         * 保存最后一个场景的 ListPlayManager,用于音频播放场景
         */
        fun getCurrentListPlayManager(): ListPlayManager {
            return mCurrentListPlayManager!!
        }
    }
}