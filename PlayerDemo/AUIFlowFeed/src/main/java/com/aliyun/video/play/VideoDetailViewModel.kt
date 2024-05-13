package com.aliyun.video.play

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.auiplayerserver.flowfeed.VideoDetailDataFetcher
import com.aliyun.auiplayerserver.flowfeed.http.ResponseStateCode
import com.aliyun.player.alivcplayerexpand.bean.DotBean
import com.aliyun.player.alivcplayerexpand.widget.IRenderView
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.alivcplayerexpand.util.PlayConfigManager
import com.aliyun.auiplayerserver.bean.AliyunVideoListBean
import com.aliyun.video.database.entity.VideoPlayConfig
import com.aliyun.video.homepage.viewmodel.BaseListViewModel
import com.aliyun.player.alivcplayerexpand.listplay.IPlayManagerScene
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "VideoDetailViewModel"

class VideoDetailViewModel :
    BaseListViewModel<AliyunVideoListBean.VideoDataBean.VideoListBean>() {
    val mSeriesPosition = MutableLiveData<Int>()
    var mPosition = 0
    val mSeriesListData = MutableLiveData<List<VideoInfo>>()
    val mVideoDetailInfo = MutableLiveData<VideoInfo>()
    var mVideoDetail: VideoInfo? = null
    val mSeriesList = mutableListOf<VideoInfo>()
    private val mRecommendVideoList =
        mutableListOf<AliyunVideoListBean.VideoDataBean.VideoListBean>()
    var mListPlayManager: ListPlayManager? = null
    var mPlayConfig: VideoPlayConfig? = null
    val mPlayConfigLiveData: MutableLiveData<VideoPlayConfig?> = MutableLiveData(null)
    val mLoadingState = MutableLiveData<Int>(0)

    private val mRemoteDataSource by lazy {
        VideoDetailDataFetcher(this)
    }
    val mAuthorName = MutableLiveData<String>()
    private val mDotList = mutableListOf<DotBean>()
    val mDotListLiveData = MutableLiveData<List<DotBean>>()
    var mIsFromRecommendList = false
    var mSpeedLiveData = MutableLiveData<Float>(-1f)


    fun initListPlayManager(
        lifecycle: Lifecycle,
        context: Context
    ): ListPlayManager {
        if (mListPlayManager == null) {
            mListPlayManager = ListPlayManager.getListPlayManager(lifecycle)
        } else {
            ListPlayManager.mCurrentListPlayManager = mListPlayManager
        }
        mListPlayManager?.init(context)
        return mListPlayManager!!
    }

    fun initData(context: Context) {
        requestVideoPlayConfig(context)
    }

    fun requestSeriesList(vodId: Long, currentVideoId: String?) {
        mRemoteDataSource.enqueue({ getVideoDetailInfo(vodId) }) {
            onSuccess {
                mLoadingState.value = ResponseStateCode.SUCCESS
                Log.i(TAG, "requestSeriesList onSuccess,vodId:$vodId VideoDetailInfo:$it")
                if (it.episodes.isNullOrEmpty() || it.episodes.size == 1) {
                    Log.i(
                        TAG,
                        "requestSeriesList onSuccess,vodId:$vodId episodes is empty or size is 1!"
                    )
                    mListPlayManager?.setSeriesPlayEnable(false)
                } else {
                    mSeriesList.clear()
                    mSeriesList.addAll(it.episodes)
                    //更新选集
                    mPosition = findCurrentSeriesPosition(mSeriesList, currentVideoId)
                    mSeriesPosition.value = mPosition
                    mSeriesListData.value = mSeriesList
                    addSeriesToListPlayManager(mSeriesList)
                }
                mVideoDetail = it.videoDetail
                mVideoDetailInfo.value = it.videoDetail
                if (it.videoDetail.user?.userName != null) {
                    mAuthorName.value = it.videoDetail.user!!.userName!!
                }
                requestDotInfo(mVideoDetail!!.duration)
            }
            onFailed {
                Log.i(TAG, "requestSeriesList onSuccess,vodId:$vodId error:$it")
                mLoadingState.value = ResponseStateCode.SUCCESS
                mSeriesListData.value = emptyList()
            }
        }
    }

    fun playSeries(position: Int) {
        mListPlayManager?.resumePlay()
    }

    public fun playWithPosition(position: Int){
        mPosition = position
        mListPlayManager?.play(position)
    }

    fun updateSeriesPosition(position: Int){
        mPosition = position
        mSeriesPosition.value = position
    }

    fun hasNextSeries(): Boolean {
        return mPosition < mSeriesList.size - 1
    }

    fun enableAutoPlayNextSeries(): Boolean {
        return ListPlayManager.mSeriesPlayEnable
    }

    fun getCurrentSeriesVideoInfo(): VideoInfo? {
        if (mPosition >= 0 && mPosition < mSeriesList.size) {
            val mediaInfo = mSeriesList[mPosition]
            return VideoInfo(
                0,
                "",
                99,
                mediaInfo.coverUrl,
                mediaInfo.description,
                mediaInfo.duration,
                mediaInfo.fileUrl,
                mediaInfo.coverUrl,
                mediaInfo.height,
                300,
                999,
                9999.0,
                mediaInfo.title,
                mediaInfo.type,
                null,
                mediaInfo.videoId!!,
                99,
                mediaInfo.vodId,
                9,
                mediaInfo.width,
                UUID.randomUUID().toString()
            )

        }
        return null
    }

    private fun addSeriesToListPlayManager(list: MutableList<VideoInfo>) {
        val playList = mutableListOf<Pair<String, String>>()
        for (videoInfo in list) {
            videoInfo.randomUUID = UUID.randomUUID().toString()
            playList.add(Pair(videoInfo.videoId, videoInfo.randomUUID!!))
        }
//        mListPlayManager?.setSeriesPlayEnable(true)
        mListPlayManager?.setSeriesPlayList(playList, true)
    }

    private fun findCurrentSeriesPosition(list: MutableList<VideoInfo>, videoId: String?): Int {
        for (index in list.indices) {
            if (videoId == list[index].videoId) {
                return index
            }
        }
        return 0
    }


    /**
     * 通过 ViewModel 共享 AliListPlayer 和 SurfaceView 实现续播
     */
    fun getListPlayer(): ListPlayManager {
        return mListPlayManager!!
    }

    fun getSurfaceView(): IRenderView {
        return mListPlayManager!!.getIRenderView()
    }

    private fun requestVideoPlayConfig(context: Context) {
        viewModelScope.launch {
            withIO {
                mPlayConfig = PlayConfigManager.getPlayConfig()
            }
            withMain {
                mPlayConfigLiveData.value = mPlayConfig
            }
        }
    }

    fun updateVideoPlayConfig(listAutoPlay: Boolean, backgroundPlay: Boolean) {
        PlayConfigManager.getPlayConfig().apply {
            this.backgroundPlayOpen = backgroundPlay
            this.listPlayOpen = listAutoPlay
            mPlayConfig = this
        }
        viewModelScope.launch {
            withIO {
                PlayConfigManager.update()
            }
        }
        mListPlayManager?.apply {
            setGlobalPlayEnable(backgroundPlay)
            setSeriesPlayEnable(listAutoPlay)
        }

    }

    fun updateDanmkuConfig(open: Boolean) {
        PlayConfigManager.getPlayConfig().apply {
            this.danmakuOpen = open
            mPlayConfig = this
        }
        viewModelScope.launch {
            withIO {
                PlayConfigManager.update()
            }
        }
    }

    fun updateDanmkuLocationConfig(location: Int) {
        PlayConfigManager.getPlayConfig().apply {
            this.danmakuLocation = location
            mPlayConfig = this
        }
        viewModelScope.launch {
            withIO {
                PlayConfigManager.update()
            }
        }
    }


    private fun requestDotInfo(duration: Double) {
        Log.i(TAG, "requestDotInfo")
        viewModelScope.launch {
            //模拟
            val requestDots = Math.random() > 0.5f
            if (requestDots) {
                withMain {
                    Log.i(TAG, "requestDotInfo start request ")
                    mDotList.clear()
                    //每10秒打一个点
                    for (index in 1..VideoDotInfoData.dotInfoArray.size) {
                        if (duration < (index) * 20) {
                            break
                        }
                        mDotList.add(
                            DotBean(
                                "${index * 20}",
                                VideoDotInfoData.dotInfoArray[index - 1]
                            )
                        )
                    }
                    Log.i(TAG, "requestDotInfo success mDotList:$mDotList")
                    mDotListLiveData.value = mDotList
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.i(TAG, "onCleared")
    }

    fun resetLoadingState() {
        mLoadingState.value = 0
        mDotListLiveData.value = emptyList()
        mSpeedLiveData.value = -1f
        mSeriesList.clear()
        mListPlayManager?.apply {
            if (getPlayerScene() == IPlayManagerScene.SCENE_FLOAT_PLAY) {
//                mListPlayManager?.setSeriesPlayEnable(true)
            } else {
                mListPlayManager?.clearSeriesPlayList()
            }

        }
    }
}