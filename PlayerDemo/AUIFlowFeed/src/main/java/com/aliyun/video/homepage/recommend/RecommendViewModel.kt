package com.aliyun.video.homepage.recommend

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.player.IPlayer
import com.aliyun.player.alivcplayerexpand.background.PlayServiceHelper
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.player.alivcplayerexpand.listplay.IPlayManagerScene
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.alivcplayerexpand.playlist.OnListPlayCallback
import com.aliyun.player.alivcplayerexpand.util.PlayConfigManager
import com.aliyun.player.alivcplayerexpand.view.voice.AudioModeView
import com.aliyun.player.alivcplayerexpand.widget.IRenderView
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode
import com.aliyun.video.database.entity.VideoPlayConfig
import com.aliyun.video.floatview.FloatViewPlayManager
import com.aliyun.video.homepage.viewmodel.BaseListViewModel
import com.aliyun.apsaravideo.videocommon.list.AutoPlayScrollListener
import com.aliyun.apsaravideo.videocommon.list.ListPlayCallback
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.auiplayerserver.flowfeed.HomePageFetcher
import com.aliyun.video.R
import com.aliyun.video.homepage.HomePageVideoListRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "RecommendViewModel"

const val PLAY_STATE_INIT = 0
const val PLAY_STATE_PLAYING = 1
const val PLAY_STATE_PAUSED = 2
const val PLAY_STATE_COMPLETE = 3


/**
 * 非纯净 ViewModel，需要修改
 */
class RecommendViewModel : BaseListViewModel<VideoInfo>() {
    private val mRepository by lazy {
        HomePageVideoListRepository()
    }
    private lateinit var mListPlayManager: ListPlayManager
    var mPosition = 0
    private var mLastPosition = -1
    val mPlayState = MutableLiveData<Int>()
    val mPlayProgress = MutableLiveData<Float>()
    val mPlayPosition = MutableLiveData<Int>(-1)
    val mPlayerScene = MutableLiveData<Int>(-1)
    var mInFloatPlayState = false
    private var mCurrentScrollPosition = 0
    var mPlayConfig: VideoPlayConfig? = null


    fun initListPlayManager(
        lifecycle: Lifecycle,
        context: Context,
        rcv: RecyclerView,
        playerViewContainerId: Int,
        videoCoverId: Int
    ) {
        mListPlayManager = ListPlayManager.getListPlayManager(lifecycle)
        //lifecycle 生命周期 控制播放
        mListPlayManager.init(context)
        mListPlayManager.addPlayCallback(object : OnListPlayCallback {
            override fun onPrepare() {
            }

            override fun onPlaying() {
                if (FloatViewPlayManager.mFloatViewShowing || mPosition >= mListData.size) return
                GlobalPlayerConfig.mVid = mListData[mPosition].videoId
                hideViewWhenPlaying(rcv)
                mPlayState.value = PLAY_STATE_PLAYING
            }

            override fun onPause() {
                updatePlayIcon(mPosition, rcv, show = true, playing = false)
                mPlayState.value = PLAY_STATE_PAUSED
            }

            override fun onPlayComplete() {
                //显示默认状态
                if (FloatViewPlayManager.mFloatViewShowing) return
                if (mListPlayManager.getPlayerScene() == IPlayManagerScene.SCENE_ONLY_VOICE) {
                    onAudioModePlayComplete(mPosition, rcv)
                } else {
                    showPlayFunctionView(false, mPosition, rcv)
                }
                mPlayState.value = PLAY_STATE_COMPLETE
            }

            override fun onPlayProgress(
                playProgress: Float,
                currentPlayMillis: Int,
                durationMillis: Int
            ) {
                //更新进度
                updatePlayProgress(rcv, playProgress, currentPlayMillis, durationMillis)
            }

            override fun onPlayError(errorCode: Int, msg: String) { }

            override fun onContrastPlay(durationMillis: Int) {
                if (durationMillis > 0) {
                    onShowContrastPlayTip(true, mPosition, rcv)
                }
            }
        })
        rcv.addOnScrollListener(
            AutoPlayScrollListener(
                playerViewContainerId,
                object : ListPlayCallback {
                    override fun play(position: Int) {
                        mCurrentScrollPosition = position
                        if (FloatViewPlayManager.mFloatViewShowing) return
                        val preIsFloatPlay = mInFloatPlayState
                        if (preIsFloatPlay) {
                            mInFloatPlayState = false
                            mListPlayManager.recreateSurfaceView()
                        }
                        if (mPosition != position) {
                            mPlayPosition.postValue(position)
                            updatePlayPosition(position)
                            playPosition(position, rcv, playerViewContainerId, videoCoverId)
                        }
                    }

                    override fun pause() {}
                    override fun getPlayState() = 0
                })
        )
        requestVideoPlayConfig()
    }

    private fun updatePlayProgress(
        rcv: RecyclerView, progress: Float, currentPlayMillis: Int, durationMillis: Int
    ) {
        val progressBar =
            getRecyclerViewItemChildView<SeekBar>(rcv, mPosition, R.id.alivc_info_small_seekbar)
        progressBar?.progress = (progress * 100).toInt()

        val playProgressTv =
            getRecyclerViewItemChildView<TextView>(rcv, mPosition, R.id.alivc_info_small_position)
        playProgressTv?.text = DateUtils.formatElapsedTime((currentPlayMillis / 1000).toLong())
        val videoDurationTv =
            getRecyclerViewItemChildView<TextView>(rcv, mPosition, R.id.alivc_info_small_duration)
        videoDurationTv?.text = DateUtils.formatElapsedTime((durationMillis / 1000).toLong())

    }

    private fun playPosition(
        position: Int, rcv: RecyclerView,
        playerViewContainerId: Int,
        videoCoverId: Int
    ) {
        refreshGlobalPlayConfig()
        mListPlayManager.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
        //显示上次播放的封面
        if (mLastPosition in -1 until mListData.size) {
            showPlayFunctionView(false, mLastPosition, rcv)
        }
        //设置容器，在开始播放
        val container: ViewGroup? = getRecyclerViewItemChildView(
            rcv,
            position,
            playerViewContainerId
        )
        if (container != null) {
            mListPlayManager.setSurfaceContainer(container)
            mListPlayManager.play(position)
        }
        mListPlayManager.mHasNext = position != mListData.size - 1
        mPlayerScene.value = IPlayManagerScene.SCENE_NORMAL
    }

    private fun showPlayFunctionView(
        playing: Boolean, position: Int, rcv: RecyclerView
    ) {
        if (!playing) {
            //关闭音频模式的 ui
            showAudioMode(false, position, rcv, false)
            updatePlayIcon(position, rcv, show = true, playing = false)
            onShowContrastPlayTip(false, position, rcv)
        }
        val videoCover: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.mVideoCover)
        videoCover?.visibility = if (playing) View.GONE else View.VISIBLE
        val durationTv: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.mVideoDuration)
        durationTv?.visibility = if (playing) View.GONE else View.VISIBLE
        val progressbarLayout: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.alivc_info_small_bar)
        progressbarLayout?.visibility = if (playing) View.VISIBLE else View.GONE
    }

    private fun onVideoPause(
        position: Int, rcv: RecyclerView
    ) {
        val durationTv: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.mVideoDuration)
        durationTv?.visibility = View.GONE
        val progressbarLayout: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.alivc_info_small_bar)
        progressbarLayout?.visibility = View.VISIBLE
        updatePlayIcon(position, rcv, show = true, playing = false)
    }

    private fun onAudioModePlayComplete(
        position: Int,
        rcv: RecyclerView
    ) {
        val audioModeView =
            getRecyclerViewItemChildView<AudioModeView>(rcv, position, R.id.audio_mode_view)
        audioModeView?.onPlayEnd()
    }


    private fun showAudioMode(
        audioMode: Boolean,
        position: Int,
        rcv: RecyclerView,
        pause: Boolean
    ) {
        val audioModeView =
            getRecyclerViewItemChildView<AudioModeView>(rcv, position, R.id.audio_mode_view)
        audioModeView?.apply {
            this.visibility = if (audioMode) View.VISIBLE else View.GONE
            mOnAudioModeListener = if (audioMode) {
                object : AudioModeView.OnAudioModeListener {
                    override fun closeAudioMode() {
                        showAudioMode(false, mPosition, rcv, mListPlayManager.isPlaying())
                        mListPlayManager.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
                        PlayServiceHelper.stopService(rcv.context)
                        hideViewWhenPlaying(rcv)
                    }

                    override fun clickPlayIcon() {
                        if (mListPlayManager.isPlaying()) {
                            mListPlayManager.pause()
                        } else {
                            mListPlayManager.resumeListPlay()
                        }
                        updatePlayIcon(
                            mPosition,
                            rcv,
                            show = false,
                            playing = mListPlayManager.isPlaying()
                        )
                    }

                    override fun onReplay() {
                        mListPlayManager.resumeListPlay()
                    }
                }
            } else {
                null
            }
        }
        val seekBarLayout: View? =
            getRecyclerViewItemChildView<View>(rcv, position, R.id.alivc_info_small_bar)
        seekBarLayout?.visibility = if (audioMode) View.VISIBLE else View.GONE
        if (audioMode) {
            audioModeView?.setUpData(
                mListData[mPosition].coverUrl ?: "",
                AliyunScreenMode.Small,
                mListPlayManager.isPlaying(), true,
                mListPlayManager.isPlayComplete()
            )
        }
        //手势 View
        val gestureView: View? =
            getRecyclerViewItemChildView<View>(rcv, position, R.id.mVideoGestureView)
        gestureView?.setTag(R.id.item_audio_mode, audioMode)
        mPlayerScene.value =
            if (audioMode) IPlayManagerScene.SCENE_ONLY_VOICE else IPlayManagerScene.SCENE_NORMAL
    }

    fun updateAudioViewPlayStateIcon(
        rcv: RecyclerView,
        playing: Boolean
    ) {

        val audioModeView =
            getRecyclerViewItemChildView<AudioModeView>(rcv, mPosition, R.id.audio_mode_view)
        audioModeView?.updatePlayIcon(playing)
        updatePlayIcon(mPosition, rcv, !playing, playing)

    }

    private fun hideViewWhenPlaying(rcv: RecyclerView) {
        val videoCover: View? = getRecyclerViewItemChildView(rcv, mPosition, R.id.mVideoCover)
        videoCover?.visibility = View.GONE
        updatePlayIcon(mPosition, rcv, show = false, playing = true)
    }

    /**
     * 需要考虑当前播放是否，默认集数，如果不是默认集数，不需要续播，直接从头开始播放
     * 回退场景
     * 1.普通退出场景
     * 2.画中画退出场景
     * 3.音频播放退出
     * 其中1 和2 均显示封面
     *
     * 播放状态
     * 1.播放中
     * 2.暂停中
     *
     * 是否当前播放
     * 1.当前播放和列表同一集
     * 2.当前播放，是列表的选集
     */
    fun continuePlay(
        rcv: RecyclerView,
        playerViewContainerId: Int
    ) {
        if (mCurrentScrollPosition != mPosition) {
            //如果当前滑动的位置和之前的播放位置发送变化，则先暂停视频，播放当前视频
            mListPlayManager.pause()
            clickVideo(mCurrentScrollPosition, rcv, R.id.mVideoContainer, R.id.mVideoCover)
            return
        }
        val playerScene = mListPlayManager.getPlayerScene()

        val pause = GlobalPlayerConfig.PlayState.playState == IPlayer.paused
        val playComplete = mListPlayManager.isPlayComplete()

        mPlayerScene.value = playerScene
        if (playerScene == IPlayManagerScene.SCENE_FLOAT_PLAY) {
            //画中画播放，显示封面
            showPlayFunctionView(false, mPosition, rcv)
            //滑动播放失效，除非点击新的视频
            mInFloatPlayState = true
        } else {
            //设置容器，在开始播放
            val container: ViewGroup? = getRecyclerViewItemChildView(
                rcv,
                mPosition,
                playerViewContainerId
            )

            val vid = mListData[mPosition].videoId
            val sameVideo = vid == mListPlayManager.getCurrentVideo().videoId
            if (playComplete) {
                //显示封面
                showPlayFunctionView(false, mPosition, rcv)
            } else {
                hideViewWhenPlaying(rcv)
            }
            container?.apply {
                mListPlayManager.setSurfaceContainer(container)
                mListPlayManager.continuePlay(pause, vid, mPosition)
            }
            val playIcon: ImageView? =
                getRecyclerViewItemChildView(rcv, mPosition, R.id.mVideoPlayIcon)
            if (playComplete) {
                playIcon?.setTag(R.id.item_playing, false)
            } else {
                playIcon?.setTag(R.id.item_playing, !pause)
            }
            if (pause || playComplete) {
                //显示播放按钮
                playIcon?.apply {
                    setImageResource(R.drawable.feed_play_play_icon)
                    this.visibility = View.VISIBLE
                }
            }

            if (playerScene == IPlayManagerScene.SCENE_ONLY_VOICE && sameVideo) {
                //音频模式，瀑布流显示音频模式，常驻进度条
                showAudioMode(true, mPosition, rcv, pause)
            } else {
                mListPlayManager.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
            }
        }
    }

    public fun updatePlayPosition(position: Int) {
        mLastPosition = mPosition
        mPosition = position
        mCurrentScrollPosition = position
    }

    fun refreshGlobalPlayConfig() {
        ListPlayManager.mGlobalPlayEnable = PlayConfigManager.getPlayConfig().backgroundPlayOpen
    }

    override fun requestListData(isLoadMore: Boolean,context: Context, refresh: Boolean) {
        if (refresh) {
            mListData.clear()
        }
        mRepository.requestVideoList(isLoadMore,
            object : HomePageFetcher.VideoListDataBack {
                override fun onResult(
                    list: MutableList<VideoInfo>
                ) {
                    mListData.addAll(list)
                    if(list.isNotEmpty()){
                        updatePlayList(list, refresh)
                    }
                    mListLiveData.postValue(list)
                }

                override fun onError(msg: String?) {}
            }
        )
    }

    private fun <T : View> getRecyclerViewItemChildView(
        rcv: RecyclerView,
        position: Int,
        playerViewContainerId: Int
    ): T? {
        val layoutManager = rcv.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val itemView = layoutManager.findViewByPosition(position)
            return itemView?.findViewById(playerViewContainerId)
        }
        return null
    }


    fun startPlay(rcv: RecyclerView, mPlayerViewContainerId: Int) {
        playPosition(0, rcv, mPlayerViewContainerId, R.id.mVideoCover)
    }

    fun stopPlay(
        rcv: RecyclerView,
        mPlayerViewContainerId: Int,
        position: Int,
        continuePlay: Boolean
    ) {
        if (!continuePlay) {
            mListPlayManager.pause()
            if (mListPlayManager.getPlayerScene() == IPlayManagerScene.SCENE_ONLY_VOICE) {
                mListPlayManager.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
            }
        }
        showPlayFunctionView(false, position, rcv)
        val currentIsFloatPlay = FloatViewPlayManager.mFloatViewShowing
        if (currentIsFloatPlay) {
            FloatViewPlayManager.closeFloatPlayView(ListPlayManager.getCurrentListPlayManager() != mListPlayManager)
        }
    }

    private fun updatePlayList(
        list: MutableList<VideoInfo>,
        refresh: Boolean
    ) {
        if (refresh) {
            mListPlayManager.setStsInfo(mRepository.mStsInfo)
        }
        val playList = mutableListOf<Pair<String, String>>()
        for (video in list) {
            playList.add(Pair(video.videoId, video.randomUUID!!))
        }
        mListPlayManager.setPlayList(playList, refresh)
    }

    fun clickVideo(
        position: Int, rcv: RecyclerView,
        playerViewContainerId: Int,
        videoCoverId: Int
    ) {
        val currentIsFloatPlay = FloatViewPlayManager.mFloatViewShowing
        val previousIsFloatPlay = mInFloatPlayState
        mListPlayManager.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
        mInFloatPlayState = false
        val vid = mListData[mPosition].videoId
        val sameVideo = vid == mListPlayManager.getCurrentVideo().videoId
        if (position == mPosition) {
            if (currentIsFloatPlay || !sameVideo) {
                //如果之前是悬浮播放，直接恢复
                val isPlaying = mListPlayManager.isPlaying()
                continuePlay(rcv, playerViewContainerId)
                if (!isPlaying) {
                    mListPlayManager.resumeListPlay()
                }
                //如果是同一个 ListPlayManager 则不关闭
                FloatViewPlayManager.closeFloatPlayView(ListPlayManager.getCurrentListPlayManager() != mListPlayManager)
            } else if (previousIsFloatPlay && !mListPlayManager.isPlaying()) {
                val container: ViewGroup? = getRecyclerViewItemChildView(
                    rcv,
                    position,
                    playerViewContainerId
                )
                mListPlayManager.recreateSurfaceView()
                mListPlayManager.setSurfaceContainer(container!!)
                mListPlayManager.resumeListPlay()
                mPlayState.value = PLAY_STATE_PLAYING
            } else {
                if (mListPlayManager.isPlaying() && !mListPlayManager.isPlayComplete()) {
                    mListPlayManager.pause()
                    mPlayState.value = PLAY_STATE_PAUSED
                } else {
                    mListPlayManager.resumeListPlay()
                    mPlayState.value = PLAY_STATE_PLAYING
                }
            }
        } else {
            if (currentIsFloatPlay) {
                FloatViewPlayManager.closeFloatPlayView(true)
                rcv.postDelayed(Runnable {
                    mListPlayManager.recreateSurfaceView()
                    mCurrentScrollPosition = position
                    updatePlayPosition(position)
                    playPosition(position, rcv, playerViewContainerId, videoCoverId)
                    mPlayState.value = PLAY_STATE_PLAYING
                }, 1000L)
            } else {
                if (previousIsFloatPlay) {
                    mListPlayManager.recreateSurfaceView()
                }
                //播放
                updatePlayPosition(position)
                playPosition(position, rcv, playerViewContainerId, videoCoverId)
                mPlayState.value = PLAY_STATE_PLAYING
            }
        }
    }


    fun getVideoList(): MutableList<VideoInfo> {
        return mListData
    }

    /**
     * 通过 ViewModel 共享 AliListPlayer 和 SurfaceView 实现续播
     */
    fun getListPlayer(): ListPlayManager {
        return mListPlayManager
    }

    fun getSurfaceView(): IRenderView? {
        return mListPlayManager.getIRenderView()
    }

    fun seekVideo(rcv: RecyclerView, progress: Int) {
        mListPlayManager.seekVideo(progress)
        val seekTipLayout: View? =
            getRecyclerViewItemChildView(rcv, mPosition, R.id.seek_duration_tip_layout)
        seekTipLayout?.visibility = View.GONE
        if (!mListPlayManager.isPlaying()) {
            onVideoPause(mPosition, rcv)
        } else {
            if (mListPlayManager.getPlayerScene() != IPlayManagerScene.SCENE_ONLY_VOICE) {
                viewModelScope.launch {
                    val showPosition = mPosition
                    delay(3000)
                    if (showPosition == mPosition && mListPlayManager.getPlayerScene() != IPlayManagerScene.SCENE_ONLY_VOICE) {
                        val seekLayout: View? =
                            getRecyclerViewItemChildView(rcv, mPosition, R.id.alivc_info_small_bar)
                        seekLayout?.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun changeSpeed(speed: Float) {
        mListPlayManager.setVideoSpeed(speed)
    }

    fun showLongPressTip(rcv: RecyclerView, show: Boolean) {
        val longPressVideoSpeedTipView =
            getRecyclerViewItemChildView<View>(rcv, mPosition, R.id.video_speed_up_tip)
        longPressVideoSpeedTipView?.visibility = if (show) View.VISIBLE else View.GONE
    }

    fun openVoice(open: Boolean) {
        mListPlayManager.setPlayMute(!open)
        updateListMute(!open)
    }

    private fun updateListMute(mute: Boolean) {
        PlayConfigManager.getPlayConfig()?.apply {
            this.listPlayMute = mute
            mPlayConfig = this
        }
    }

    fun changeVoiceState(open: Boolean, rcv: RecyclerView) {
        val voiceCheckBox: CheckBox? =
            getRecyclerViewItemChildView<CheckBox>(rcv, mPosition, R.id.check_box_voice)
        voiceCheckBox?.isChecked = open
    }

    private fun updatePlayIcon(position: Int, rcv: RecyclerView, show: Boolean, playing: Boolean) {
        val playIcon: ImageView? =
            getRecyclerViewItemChildView(rcv, position, R.id.mVideoPlayIcon)
        playIcon?.apply {
            this.visibility = if (show) View.VISIBLE else View.GONE
            setTag(R.id.item_playing, playing)
            setImageResource(if (playing) R.drawable.feed_play_pause_icon else R.drawable.feed_play_play_icon)
        }
    }

    private fun onShowContrastPlayTip(show: Boolean, position: Int, rcv: RecyclerView) {
        val contrastPlayTipLayout: View? =
            getRecyclerViewItemChildView(rcv, position, R.id.layout_contrast_play_tip)
        contrastPlayTipLayout?.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            //延迟三秒
            contrastPlayTipLayout?.postDelayed({
                contrastPlayTipLayout?.visibility = View.GONE
            }, 3000L)
        }
    }

    fun onSeeking(rcv: RecyclerView, progress: Int) {
        if (mListPlayManager.getPlayerScene() != IPlayManagerScene.SCENE_NORMAL)
            return
        val seekTipLayout: View? =
            getRecyclerViewItemChildView(rcv, mPosition, R.id.seek_duration_tip_layout)
        seekTipLayout?.apply {
            this.visibility = View.VISIBLE
            val videoDurationSeconds = mListPlayManager.getListPlayer().mediaInfo.duration / 1000
            val playDurationTv: TextView = findViewById(R.id.seek_play_duration_tv)
            val playDurationText = (videoDurationSeconds * (progress / 100f)).toLong()
            playDurationTv.text =
                DateUtils.formatElapsedTime(playDurationText)
            val videoDurationTv: TextView = findViewById(R.id.seek_video_duration_tv)
            videoDurationTv.text =
                "/${DateUtils.formatElapsedTime(videoDurationSeconds.toLong())}"
            val playProgress: ProgressBar = findViewById(R.id.seek_progress_play_duration)
            playProgress.progress = progress
        }
        //隐藏play 按钮
        val playIcon: ImageView? =
            getRecyclerViewItemChildView(rcv, mPosition, R.id.mVideoPlayIcon)
        playIcon?.visibility = View.GONE
        //显示进度条
        val seekLayout: View? =
            getRecyclerViewItemChildView(rcv, mPosition, R.id.alivc_info_small_bar)
        seekLayout?.visibility = View.VISIBLE
    }

    fun getAuthorName(): String? {
        if (mListData.isEmpty()) return ""
        return mListData[mPosition].user?.userName
    }

    private fun requestVideoPlayConfig() {
        mPlayConfig = PlayConfigManager.getPlayConfig()
    }

    fun sameVideo(videoId: String, position: Int): Boolean {
        if (position >= 0 && position < mListData.size) {
            return mListData[position].videoId == videoId
        }
        return false
    }

    fun releasePlayer(){
        mListPlayManager.release()
    }
}