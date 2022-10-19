package com.aliyun.video.play

import com.alibaba.sdk.android.oss.common.utils.DateUtil
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.aio.avbaseui.widget.AVToast
import com.aliyun.aio.utils.DensityUtil
import com.aliyun.auiplayerserver.GetAuthInformation
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayer.OnVerifyTimeExpireCallback
import com.aliyun.player.IPlayer.*
import com.aliyun.player.VidPlayerConfigGen
import com.aliyun.player.alivcplayerexpand.background.BackGroundPlayChangeEvent
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig.PLAYTYPE
import com.aliyun.player.alivcplayerexpand.listplay.IPlayManagerScene
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager.GlobalPlayer.mGlobalPlayEnable
import com.aliyun.player.alivcplayerexpand.theme.Theme
import com.aliyun.player.alivcplayerexpand.util.*
import com.aliyun.player.alivcplayerexpand.view.choice.AlivcShowMoreDialog
import com.aliyun.player.alivcplayerexpand.view.control.ControlView
import com.aliyun.player.alivcplayerexpand.view.control.ControlView.OnShowMoreClickListener
import com.aliyun.player.alivcplayerexpand.view.control.ControlView.OnTrackInfoClickListener
import com.aliyun.player.alivcplayerexpand.view.gesturedialog.BrightnessDialog
import com.aliyun.player.alivcplayerexpand.view.more.*
import com.aliyun.player.alivcplayerexpand.view.more.TrackInfoView.OnSubtitleChangedListener
import com.aliyun.player.alivcplayerexpand.view.quality.QualityLanguage
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView.*
import com.aliyun.player.aliyunplayerbase.bean.AliyunPlayAuth
import com.aliyun.player.aliyunplayerbase.bean.AliyunSts
import com.aliyun.player.aliyunplayerbase.util.*
import com.aliyun.player.aliyunplayerbase.view.tipsview.ErrorInfo
import com.aliyun.player.aliyunplayerbase.view.tipsview.OnTipsViewBackClickListener
import com.aliyun.player.aliyunplayerbase.view.tipsview.TipsView.OnTipClickListener
import com.aliyun.player.bean.ErrorCode
import com.aliyun.player.bean.InfoBean
import com.aliyun.player.bean.InfoCode
import com.aliyun.player.nativeclass.CacheConfig
import com.aliyun.player.nativeclass.PlayerConfig
import com.aliyun.player.nativeclass.TrackInfo
import com.aliyun.player.source.Definition
import com.aliyun.player.source.StsInfo
import com.aliyun.player.source.VidAuth
import com.aliyun.player.source.VidSts
import com.aliyun.video.R
import com.aliyun.video.common.ui.BaseFragment
import com.aliyun.video.common.ui.FragmentBackHandler
import com.aliyun.video.common.ui.adapter.ItemClickDelegate
import com.aliyun.video.common.ui.bindView
import com.aliyun.video.common.ui.play.FragmentHiddenCallback
import com.aliyun.video.databinding.AlivcPlayerLayoutSkinBinding
import com.aliyun.video.floatview.FloatViewPlayManager
import com.aliyun.video.homepage.recommend.RecommendViewModel
import com.aliyun.video.play.adapter.RecommendVideoItemDelegate
import com.drakeet.multitype.MultiTypeAdapter
import com.ethanhua.skeleton.SkeletonManager
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.ref.WeakReference

/**
 * 全屏和半屏播放界面
 */

const val NORMAL_HALF_SCREEN_FRAGMENT = "normal_half_screen_fragment"
private const val FEED_LONG_PRESS_KEY = "enable_long_press_feed"

class AliyunPlayerSkinFragment : BaseFragment(R.layout.alivc_player_layout_skin),
    ControlView.OnFloatPlayViewClickListener, FragmentBackHandler {
    private var currentScreenMode = AliyunScreenMode.Small
    private var mScreenCostView: ScreenCostView? = null
    private var mRecommendViewModel: RecommendViewModel? = null
    private val mVideoDetailViewModel by lazy {
        ViewModelProvider(requireActivity())[VideoDetailViewModel::class.java]
    }
    private val mGetAuthInformation by lazy {
        GetAuthInformation()
    }

    //get StsToken stats
    private var inRequest = false
    //当前点击的视频列表的下标
    private var currentVidItemPosition = 0
    //更多Dialog
    private var showMoreDialog: AlivcShowMoreDialog? = null
    private var halfVideoSettingDialog: AlivcShowMoreDialog? = null
    //是否鉴权过期
    private var mIsTimeExpired = false
    //播放列表资源
    private var mVideoListBean: ArrayList<VideoInfo> = ArrayList()
    //本地视频播放地址
    private val mLocalVideoPath: String? = null
    //用于恢复原本的播放方式，如果跳转到下载界面，播放本地视频，会切换到url播放方式
    private var mCurrentPlayType = GlobalPlayerConfig.mCurrentPlayType
    //当前正在播放的videoId
    private var mCurrentVideoId: String? = null
    private val mNeedOnlyFullScreen = false
    //当前系统屏幕亮度
    private var mIsContinuedPlay = true
    private var mFullScreenType = 0
    private var mIsFromRecommendList = false
    private var mUUID = ""
    private var mPlayState = 0
    private var mInit = false
    private var mVideoInfo: VideoInfo? = null
    private var mFrom = 0
    var mHiddenCallback: FragmentHiddenCallback? = null
    private val mViewBinding by bindView<AlivcPlayerLayoutSkinBinding>()
    private var mFloatPlayClick = false
    private var videoDeveloperModeView: VideoDeveloperModeView? = null
    private var mSkeletonManager = SkeletonManager()

    //相关推荐
    private val mRecommendAdapter = MultiTypeAdapter()
    private val mVideoDescriptionViewModel by lazy {
        ViewModelProvider(requireActivity())[VideoDetailViewModel::class.java]
    }
    private val mRecommendVideoItemClick = object :
        ItemClickDelegate.ItemClickListener<VideoInfo> {
        override fun onItemClick(
            position: Int,
            entity: VideoInfo
        ) {
            mVideoDescriptionViewModel.playWithPosition(position)
            setUpVideoDescription(mVideoListBean[position])
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleIntentData()
        if (mRecommendViewModel == null) {
            mRecommendViewModel =
                ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
        }
        initView()
        initAliyunPlayerView()
        initPlayerConfig()

        mViewBinding.apply {
            //相关推荐
            mRecommendAdapter.register(RecommendVideoItemDelegate(mRecommendVideoItemClick))
            mRecommendRcv.layoutManager =
                LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            mRecommendRcv.adapter = mRecommendAdapter
        }
        initViewModel()

        EventBus.getDefault().unregister(this)
        EventBus.getDefault().register(this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updatePlayerViewMode()
    }

    private fun initData(videoList: MutableList<VideoInfo>) {
        showRecommendList()
        mRecommendAdapter.items = videoList
        mRecommendAdapter.notifyDataSetChanged()
        mViewBinding.mVideoInfoTip.text = CREATE_TIME_MSG
    }

    private fun showRecommendList() {
        mViewBinding.apply {
            mRecommendTitle.setVisible(true)
            mRecommendRcv.setVisible(true)
        }
    }

    private fun initViewModel() {
        mVideoDescriptionViewModel.apply {
            mVideoDetailInfo.observe(viewLifecycleOwner, Observer {
                //更新详情信息
                setUpVideoDescription(it)
                initData(mVideoListBean)
            })
        }
    }

    private fun setUpVideoDescription(bean: VideoInfo) {
        mViewBinding.apply {
            if (bean.user != null) {
                //头像
                ImageLoader.loadCircleImg(
                    bean.user?.avatarUrl,
                    mAuthorPortrait,
                    R.drawable.default_portrait_icon
                )
                //昵称
                mAuthorName.text = bean.user?.userName ?: "polo"
            } else {
                mAuthorPortrait.setImageResource(R.drawable.default_portrait_icon)
                mAuthorName.text = "polo"
            }
            if (bean.title?.isNotEmpty() == true) {
                mVideoTitle.text = bean.title
                mVideoDescription.text = bean.title
            }
        }
    }

    private fun handleIntentData() {
        arguments?.apply {
            lifecycle
            mIsContinuedPlay = getBoolean(CONTINUE_PLAY, false)
            mFullScreenType = getInt(FULL_SCREEN, 0)
            mIsFromRecommendList = getBoolean(FROM_LIST, false)
            mVideoInfo = getParcelable(VIDEO_INFO)
            mUUID = mVideoInfo?.randomUUID ?: ""
            mPlayState = getInt(PLAY_STATE, 0)
            mFrom = getInt(FROM_SOURCE, 0)
        }
    }

    private fun initView() {
        mViewBinding.videoView.apply {
            mSkeletonManager.show(
                mViewBinding.skeletonView,
                R.layout.layout_video_detail_page_loading_layout
            )
            mVideoDetailViewModel.mLoadingState.observe(viewLifecycleOwner, Observer {
                if (it != 0) {
                    mSkeletonManager.hide()
                }
            })
            mVideoDetailViewModel.mSpeedLiveData.observe(viewLifecycleOwner, Observer {
                if (it > 0) {
                    mViewBinding.videoView.changeSpeed(it)
                }
            })
            mVideoDetailViewModel.mIsFromRecommendList = mIsFromRecommendList
            setIsContinuedPlay(mIsContinuedPlay, mIsFromRecommendList)
            val listPlayManager: ListPlayManager = if (mIsContinuedPlay || mIsFromRecommendList) {
                mVideoDetailViewModel.mListPlayManager = ListPlayManager.getCurrentListPlayManager()
                ListPlayManager.getCurrentListPlayManager()
            } else {
                mVideoDetailViewModel.initListPlayManager(lifecycle, requireContext())
                mVideoDetailViewModel.getListPlayer()
            }
            initVideoView(
                listPlayManager.getIRenderView(),
                listPlayManager,
                mFullScreenType
            )
            if (listPlayManager.isPlayComplete() && listPlayManager.getPlayerScene() == IPlayManagerScene.SCENE_NORMAL && mIsContinuedPlay) {
                showReplay()
            }
        }

        mViewBinding.ivBack.setOnClickListener {
            finishSelf()
        }
    }

    private fun initAliyunPlayerView() {
        //保持屏幕敞亮
        mViewBinding.videoView.apply {
            if (mIsFromRecommendList) {
                updateListPosition(mUUID)
            }
            mVideoDetailViewModel.mAuthorName.observe(viewLifecycleOwner, Observer {
                setAuthorName(it)
            })
            mVideoDetailViewModel.mDotListLiveData.observe(viewLifecycleOwner, Observer {
                if (it.isNotEmpty()) {
                    mViewBinding.videoView.setDotInfo(it)
                }
            })
            updateContinuePlay(mIsContinuedPlay, mIsFromRecommendList)
            keepScreenOn = true
            setTheme(Theme.Blue)
            setAutoPlay(true)
            needOnlyFullScreenPlay(mNeedOnlyFullScreen)
            setOnPreparedListener(MyPrepareListener(this@AliyunPlayerSkinFragment))
            setOnCompletionListener(MyCompletionListener(this@AliyunPlayerSkinFragment))
            setOnFirstFrameStartListener(MyFrameInfoListener(this@AliyunPlayerSkinFragment))
            setOnTrackChangedListener(MyOnTrackChangedListener(this@AliyunPlayerSkinFragment))
            setOrientationChangeListener(MyOrientationChangeListener(this@AliyunPlayerSkinFragment))
            setOnTimeExpiredErrorListener(MyOnTimeExpiredErrorListener(this@AliyunPlayerSkinFragment))
            setOnShowMoreClickListener(MyShowMoreClickLisener(this@AliyunPlayerSkinFragment))
            setOnVideoSpeedClickListener(MyVideoSpeedClickListener(this@AliyunPlayerSkinFragment))
            setOnFinishListener(MyOnFinishListener(this@AliyunPlayerSkinFragment))
            setOnScreenBrightness(MyOnScreenBrightnessListener(this@AliyunPlayerSkinFragment))
            setOnErrorListener(MyOnErrorListener(this@AliyunPlayerSkinFragment))
            screenBrightness = BrightnessDialog.getActivityBrightness(requireActivity())
            setOnTrackInfoClickListener(MyOnTrackInfoClickListener(this@AliyunPlayerSkinFragment))
            setOnInfoListener(MyOnInfoListener(this@AliyunPlayerSkinFragment))
            setOutOnSeiDataListener(MyOnSeiDataListener(this@AliyunPlayerSkinFragment))
            setOnTipClickListener(MyOnTipClickListener(this@AliyunPlayerSkinFragment))
            setOnFloatPlayViewClickListener(this@AliyunPlayerSkinFragment)
            setOnTipsViewBackClickListener(MyOnTipsViewBackClickListener(this@AliyunPlayerSkinFragment))
            setOutOnVerifyTimeExpireCallback(MyOnVerifyStsCallback(this@AliyunPlayerSkinFragment))
            enableNativeLog()
            screenBrightness = currentBrightValue
            startNetWatch()
            showLongPressView(FEED_LONG_PRESS_KEY)
        }
    }

    /**
     * 获取播放列表数据
     */
    private fun loadPlayList() {
        mVideoListBean.clear()
        mVideoListBean.addAll(mRecommendViewModel!!.getVideoList())
        if (!mIsFromRecommendList) {
            //普通非续播，则直接添加播放源头
            if (mVideoInfo != null) {
                val vidSts = getVidSts(mVideoInfo!!.videoId)
                mViewBinding.videoView.setVidSts(vidSts, true)
            }
        }
    }

    /**
     * 播放方式
     */
    private fun initDataSource() {
        currentVidItemPosition = 0
        loadPlayList()
        if (mVideoInfo != null) {
            mVideoDetailViewModel.mPlayConfigLiveData.observe(viewLifecycleOwner, Observer {
                it?.apply {
                    mViewBinding.videoView.setUpConfig(danmakuOpen, danmakuLocation, listPlayOpen)
                }
            })
            mVideoDetailViewModel.initData(requireContext())
            mVideoInfo?.vodId?.let {
                mVideoDetailViewModel.requestSeriesList(
                    it,
                    mVideoInfo?.videoId
                )
            }
        }
    }

    /**
     * 初始化播放配置
     */
    private fun initPlayerConfig() {
        PlayConfigManager.init(requireContext())
        //界面设置
        mViewBinding.videoView.setEnableHardwareDecoder(GlobalPlayerConfig.mEnableHardDecodeType)
        mViewBinding.videoView.setRenderMirrorMode(GlobalPlayerConfig.mMirrorMode)
        mViewBinding.videoView.setRenderRotate(GlobalPlayerConfig.mRotateMode)
        //播放配置设置
        val playerConfig: PlayerConfig? = mViewBinding.videoView.playerConfig
        playerConfig?.apply {
            mStartBufferDuration = GlobalPlayerConfig.PlayConfig.mStartBufferDuration
            mHighBufferDuration = GlobalPlayerConfig.PlayConfig.mHighBufferDuration
            mMaxBufferDuration = GlobalPlayerConfig.PlayConfig.mMaxBufferDuration
            mMaxDelayTime = GlobalPlayerConfig.PlayConfig.mMaxDelayTime
            mNetworkTimeout = GlobalPlayerConfig.PlayConfig.mNetworkTimeout
            mMaxProbeSize = GlobalPlayerConfig.PlayConfig.mMaxProbeSize
            mReferrer = GlobalPlayerConfig.PlayConfig.mReferrer
            mHttpProxy = GlobalPlayerConfig.PlayConfig.mHttpProxy
            mNetworkRetryCount = GlobalPlayerConfig.PlayConfig.mNetworkRetryCount
            mEnableSEI = GlobalPlayerConfig.PlayConfig.mEnableSei
            mClearFrameWhenStop = GlobalPlayerConfig.PlayConfig.mEnableClearWhenStop
            mViewBinding.videoView.playerConfig = playerConfig
        }

        //缓存设置
        initCacheConfig()
    }

    private fun initCacheConfig() {
        val cacheConfig = CacheConfig()
        GlobalPlayerConfig.PlayCacheConfig.mDir =
            FileUtils.getDir(requireContext()) + GlobalPlayerConfig.CACHE_DIR_PATH
        cacheConfig.mEnable = GlobalPlayerConfig.PlayCacheConfig.mEnableCache
        cacheConfig.mDir = GlobalPlayerConfig.PlayCacheConfig.mDir
        cacheConfig.mMaxDurationS = GlobalPlayerConfig.PlayCacheConfig.mMaxDurationS.toLong()
        cacheConfig.mMaxSizeMB = GlobalPlayerConfig.PlayCacheConfig.mMaxSizeMB
        mViewBinding.videoView.setCacheConfig(cacheConfig)
    }

    /**
     * 获取VidSts
     *
     * @param vid videoId
     */
    private fun getVidSts(vid: String?): VidSts {
        val vidSts = VidSts()
        vidSts.vid = vid
        vidSts.region = GlobalPlayerConfig.mRegion
        vidSts.accessKeyId = ListPlayManager.mStsInfo?.accessKeyId
        vidSts.securityToken = ListPlayManager.mStsInfo?.securityToken
        vidSts.accessKeySecret = ListPlayManager.mStsInfo?.accessKeySecret
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

    private class MyPrepareListener(fragment: AliyunPlayerSkinFragment) :
        OnPreparedListener {
        private val activityWeakReference = WeakReference(fragment)
        override fun onPrepared() {
            val activity = activityWeakReference.get()
            activity?.onPrepared()
        }
    }

    private fun onPrepared() {
        val mediaInfo =
            mViewBinding.videoView.mediaInfo
        if (mediaInfo != null) {
            mCurrentVideoId = mediaInfo.videoId
        }
        mViewBinding.videoView.changeSpeed(1.0f)
    }

    private class MyCompletionListener(skinActivity: AliyunPlayerSkinFragment) :
        OnCompletionListener {
        private val activityWeakReference = WeakReference(skinActivity)
        override fun onCompletion() {
            activityWeakReference.get()?.onCompletion()
        }
    }

    private fun onCompletion() {
        hideAllDialog()
        currentVidItemPosition = 0
        showReplayTip()
    }

    /**
     * 隐藏所有Dialog
     */
    private fun hideAllDialog() {
        showMoreDialog?.closeDialog()
        halfVideoSettingDialog?.closeDialog()
    }

    private fun AlivcShowMoreDialog.closeDialog() {
        if (isShowing) {
            dismiss()
        }
    }

    private fun showReplayTip() {
        mViewBinding.videoView.showReplay()
    }

    private class MyFrameInfoListener(skinActivity: AliyunPlayerSkinFragment) :
        OnRenderingStartListener {
        private val activityWeakReference = WeakReference(skinActivity)
        override fun onRenderingStart() {
            val activity = activityWeakReference.get()
            activity?.onFirstFrameStart()
        }
    }

    private fun onFirstFrameStart() {}

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                AVToast.show(
                    requireContext(),
                    true,
                    resources.getString(R.string.alivc_sd_card_permission)
                )
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (mActivityResume) {
            mHiddenCallback?.onFragmentHidden(this, hidden)
        }
    }

    private class MyOnTrackChangedListener(activity: AliyunPlayerSkinFragment) :
        OnTrackChangedListener {
        private val weakReference: WeakReference<AliyunPlayerSkinFragment> = WeakReference(activity)
        override fun onChangedSuccess(trackInfo: TrackInfo) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.changeTrackSuccess(trackInfo)
        }

        override fun onChangedFail(
            trackInfo: TrackInfo,
            errorInfo: com.aliyun.player.bean.ErrorInfo
        ) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.changeTrackFail(trackInfo, errorInfo)
        }
    }

    private fun changeTrackFail(
        trackInfo: TrackInfo,
        errorInfo: com.aliyun.player.bean.ErrorInfo
    ) {
        if (showMoreDialog != null && showMoreDialog!!.isShowing) {
            showMoreDialog!!.dismiss()
        }
        AVToast.show(
            requireContext(), true, getString(
                R.string.alivc_player_track_change_error,
                errorInfo.code,
                errorInfo.msg
            )
        )
    }

    private fun changeTrackSuccess(trackInfo: TrackInfo?) {
        if (trackInfo == null) {
            return
        }
        if (showMoreDialog != null && showMoreDialog!!.isShowing) {
            showMoreDialog!!.dismiss()
        }
        when (trackInfo.type) {
            TrackInfo.Type.TYPE_VIDEO -> {
                //码率
                AVToast.show(
                    requireContext(),
                    true,
                    getString(
                        R.string.alivc_player_track_bitrate_change_success,
                        trackInfo.getVideoBitrate().toString()
                    )
                )
            }
            TrackInfo.Type.TYPE_VOD -> {
                AVToast.show(
                    requireContext(), true, getString(
                        R.string.alivc_player_track_definition_change_success,
                        trackInfo.getVodDefinition()
                    )
                )
            }
            else -> {
                AVToast.show(
                    requireContext(), true, getString(
                        R.string.alivc_player_track_change_success,
                        trackInfo.getDescription()
                    )
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if ((mFullScreenType == STATE_FEED_TO_FULL_FORWARD_SCREEN || mFullScreenType == STATE_FEED_TO_FULL_REVERSE_SCREEN) && !mFloatPlayClick) {
            requestBaseActivity()?.fullScreen(true)
            mViewBinding.videoView.monitorFullScreenClick(mFullScreenType == STATE_FEED_TO_FULL_REVERSE_SCREEN)
        }
        if (!GlobalPlayerConfig.PlayConfig.mEnablePlayBackground) {
            mViewBinding.videoView.setAutoPlay(true)
            mViewBinding.videoView.onResume(mActivityResume)
            GlobalPlayerConfig.mCurrentPlayType = mCurrentPlayType
        }
        if (!mInit) {
            initDataSource()
            mInit = true
        }
    }

    override fun onStop() {
        super.onStop()
        if (mActivityResume) {
            mHiddenCallback?.onFragmentHidden(this, true)
        }
        if (!GlobalPlayerConfig.PlayConfig.mEnablePlayBackground) {
            if (shouldStopVideo()) {
                mViewBinding.videoView.apply {
                    setAutoPlay(false)
                    onStop()
                }
            }
            mCurrentPlayType = GlobalPlayerConfig.mCurrentPlayType
        }
    }

    override fun onPause() {
        super.onPause()
        mViewBinding.videoView.onPause(mActivityPaused)
    }

    private fun shouldStopVideo(): Boolean {
        if (mFloatPlayClick) return false
        if (mActivityPaused) return !mGlobalPlayEnable
        return !mIsFromRecommendList
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updatePlayerViewMode()
    }

    private fun updatePlayerViewMode() {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            mViewBinding.ivBack.visibility = View.VISIBLE
            mViewBinding.tvTitle.visibility = View.VISIBLE
            val aliVcVideoViewLayoutParams = mViewBinding.videoView
                .layoutParams as RelativeLayout.LayoutParams
            val margin = DensityUtil.dip2px(requireContext(), 12f)
            aliVcVideoViewLayoutParams.height =
                ((ScreenUtils.getWidth(requireContext()) - margin * 2) * 9.0f / 16).toInt()
            aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            aliVcVideoViewLayoutParams.marginStart = margin
            aliVcVideoViewLayoutParams.marginEnd = margin
            aliVcVideoViewLayoutParams.leftMargin = margin
            aliVcVideoViewLayoutParams.rightMargin = margin
            aliVcVideoViewLayoutParams.topMargin = 0
            mViewBinding.videoView.layoutParams = aliVcVideoViewLayoutParams
            (requestBaseActivity())?.fullScreen(false)
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mViewBinding.ivBack.visibility = View.GONE
            mViewBinding.tvTitle.visibility = View.GONE
            //设置view的布局，宽高
            val videoViewLp = mViewBinding.videoView
                .layoutParams as RelativeLayout.LayoutParams
            videoViewLp.height = ViewGroup.LayoutParams.MATCH_PARENT
            videoViewLp.width = ViewGroup.LayoutParams.MATCH_PARENT
            videoViewLp.marginStart = 0
            videoViewLp.marginEnd = 0
            videoViewLp.leftMargin = 0
            videoViewLp.rightMargin = 0
            videoViewLp.topMargin = 0
            mViewBinding.videoView.layoutParams = videoViewLp
            (requestBaseActivity())?.fullScreen(true)
        }
    }

    override fun onDestroyView() {
        mViewBinding.videoView.clearAllListener()
        mViewBinding.videoView.onDestroy()
        mScreenCostView?.destroy()
        mHiddenCallback = null
        mVideoDetailViewModel.resetLoadingState()
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackgroundPlayChangeEvent(event: BackGroundPlayChangeEvent) {
        when (event.action) {
            BackGroundPlayChangeEvent.ACTION_PAUSE -> {
                mViewBinding.videoView.updatePlayStateIcon(false)
            }

            BackGroundPlayChangeEvent.ACTION_RESUME -> {
                mViewBinding.videoView.updatePlayStateIcon(true)
            }
        }
    }

    class MyOrientationChangeListener(fragment: AliyunPlayerSkinFragment) :
        OnOrientationChangeListener {
        private val weakReference = WeakReference(fragment)
        override fun orientationChange(
            from: Boolean,
            currentMode: AliyunScreenMode
        ) {
            weakReference.get()?.apply {
                if (currentMode == AliyunScreenMode.Small && GlobalPlayerConfig.mCurrentPlayType == PLAYTYPE.URL && !TextUtils.isEmpty(
                        mLocalVideoPath)) {
                    //如果播放本地视频，切换到小屏后，直接关闭
                    finishSelf()
                } else {
                    hideShowMoreDialog(from, currentMode)
                    hideScreenSostDialog(from, currentMode)
                }
            }
        }
    }

    private fun hideShowMoreDialog(
        from: Boolean,
        currentMode: AliyunScreenMode
    ) {
        showMoreDialog?.dismiss()
        currentScreenMode = currentMode
    }

    private fun hideScreenSostDialog(
        fromUser: Boolean,
        currentMode: AliyunScreenMode
    ) {
        currentScreenMode = currentMode
    }

    private class RetryExpiredSts(fragment: AliyunPlayerSkinFragment) :
        GetAuthInformation.OnGetStsInfoListener {
        private val weakReference = WeakReference(fragment)

        override fun onGetStsError(errorMsg: String?) {}

        override fun onGetStsSuccess(dataBean: AliyunSts.StsBean?) {
            dataBean?.let {
                weakReference.get()?.onStsRetrySuccess(
                    dataBean.videoId ?: GlobalPlayerConfig.mVid,
                    dataBean.accessKeyId,
                    dataBean.accessKeySecret,
                    dataBean.securityToken
                )
            }
        }
    }

    private fun onStsRetrySuccess(
        mVid: String,
        akid: String,
        akSecret: String,
        token: String
    ) {
        GlobalPlayerConfig.mVid = mVid
        GlobalPlayerConfig.mStsAccessKeyId = akid
        GlobalPlayerConfig.mStsAccessKeySecret = akSecret
        GlobalPlayerConfig.mStsSecurityToken = token
        inRequest = false
        mIsTimeExpired = false
        val vidSts = getVidSts(mVid)
        mViewBinding.videoView.setVidSts(vidSts, true)
    }

    class MyOnTimeExpiredErrorListener(fragment: AliyunPlayerSkinFragment) :
        OnTimeExpiredErrorListener {
        var weakReference: WeakReference<AliyunPlayerSkinFragment> = WeakReference(fragment)
        override fun onTimeExpiredError() {
            weakReference.get()?.onTimExpiredError()
        }
    }

    /**
     * 鉴权过期
     */
    private fun onTimExpiredError() {
        mGetAuthInformation.getVideoPlayStsDemoInfo(RetryExpiredSts(this))
    }

    private class MyShowMoreClickLisener internal constructor(fragment: AliyunPlayerSkinFragment) :
        OnShowMoreClickListener {
        var weakReference = WeakReference(fragment)
        override fun showMore() {
            if (FastClickUtil.isFastClick()) return
            weakReference.get()?.showMoreDialog()
        }
    }

    private fun showMoreDialog() {
        if (mViewBinding.videoView.screenMode == AliyunScreenMode.Full) {
            showFullMoreDialog()
        }
    }

    private fun onHalfViewMoreItemClick(type: HalfViewVideoSettingPage.VideoSettingType) {
        halfVideoSettingDialog = AlivcShowMoreDialog(requireContext(), AliyunScreenMode.Small)
        val title = when (type) {
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED -> getString(R.string.video_speed_title)
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION -> getString(R.string.definition)
            else -> getString(R.string.damkun_location)
        }
        val selectedPosition = when (type) {
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED -> {
                val videoSpeed = mViewBinding.videoView.currentSpeed
                when {
                    videoSpeed >= 2f -> 0
                    videoSpeed >= 1.5f -> 1
                    videoSpeed >= 1.25f -> 2
                    videoSpeed >= 1.0f -> 3
                    videoSpeed >= 0.75f -> 4
                    else -> 5
                }
            }
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION -> 1
            else -> mVideoDetailViewModel.mPlayConfig?.danmakuLocation ?: 2
        }
        halfVideoSettingDialog = AlivcShowMoreDialog(requireContext(), AliyunScreenMode.Small)
        val itemViewMoreView = HalfViewVideoSettingPage(requireContext())
        halfVideoSettingDialog?.apply {
            setContentView(itemViewMoreView)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
        itemViewMoreView.mItemClick = object : HalfViewVideoSettingPage.OnItemClick {
            override fun onItemClick(
                type: HalfViewVideoSettingPage.VideoSettingType,
                position: Int
            ) {
                handleVideoSettingPageItemCLick(type, position)
            }

            override fun onBack() {
                halfVideoSettingDialog?.dismiss()
            }

            override fun cancelAll() {
                halfVideoSettingDialog?.dismiss()
                showMoreDialog?.dismiss()
            }
        }

        val contentList = when (type) {
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED -> {
                val videoSpeedList = mutableListOf<String>()
                videoSpeedList.add("2.0x")
                videoSpeedList.add("1.5x")
                videoSpeedList.add("1.25x")
                videoSpeedList.add("1.0x")
                videoSpeedList.add("0.75x")
                videoSpeedList.add("0.5x")
                videoSpeedList
            }
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION -> {
                val definitionTrackInfoList = TrackUtils.getTrackInfoListWithTrackInfoType(
                    TrackInfo.Type.TYPE_VOD,
                    mViewBinding.videoView.currentMediaInfo
                )
                val definitionList = mutableListOf<String>()
                for (definition in definitionTrackInfoList) {
                    definitionList.add(
                        QualityLanguage.getSaasLanguage(
                            requireContext(),
                            definition.vodDefinition
                        )
                    )
                }
                definitionList
            }
            else -> mutableListOf<String>()
        }
        itemViewMoreView.setUpData(type, contentList, title, selectedPosition)
    }

    private fun handleVideoSettingPageItemCLick(
        type: HalfViewVideoSettingPage.VideoSettingType,
        position: Int
    ) {

        when (type) {
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED -> {
                val videoSpeed = if (position == 0) 2.0f else 1.5f - (position - 1) * 0.25f
                onChooseVideoSpeed(videoSpeed)
            }
            HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION -> mViewBinding.videoView.selectTrackIndex(
                position
            )
            else -> {}
        }
        hideAllDialog()
    }

    private fun showFullMoreDialog() {
        showFunctionShadowView(true)
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        val moreValue = AliyunShowMoreValue()
        moreValue.apply {
            scaleMode = mViewBinding.videoView.scaleMode
            isEnableHardDecodeType = GlobalPlayerConfig.mEnableHardDecodeType
        }

        videoDeveloperModeView = VideoDeveloperModeView(requireContext(), moreValue = moreValue)
        showMoreDialog?.apply {
            setContentView(videoDeveloperModeView!!)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
    }

    private class MyVideoSpeedClickListener internal constructor(fragment: AliyunPlayerSkinFragment) :
        ControlView.OnVideoSpeedClickListener {
        var weakReference = WeakReference(fragment)
        override fun onVideoSpeedClick() {
            weakReference.get()?.showVideoSpeedView()
        }
    }

    private fun showVideoSpeedView() {
        showFunctionShadowView(true)
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        val videoSpeed = mViewBinding.videoView.currentSpeed
        val showMoreView = VideoSpeedView(requireContext(), currentSpeed = videoSpeed)
        showMoreDialog?.apply {
            setContentView(showMoreView)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
        showMoreView.mOnVideoSpeedSelected = object : VideoSpeedView.OnVideoSpeedSelected {
            override fun onVideoSpeedSelected(speed: Float) {
                onChooseVideoSpeed(speed)
            }
        }
    }

    private fun onChooseVideoSpeed(speed: Float) {
        mViewBinding.videoView.changeSpeed(speed)
        AVToast.show(requireContext(), true, "已切换至${speed}倍速")
        closeFunctionDialog()
    }

    private fun closeFunctionDialog() {
        if (showMoreDialog != null && showMoreDialog!!.isShowing) {
            showMoreDialog?.dismiss()
        }
    }

    private class MyOnFinishListener(fragment: AliyunPlayerSkinFragment) :
        OnFinishListener {
        var weakReference = WeakReference(fragment)
        override fun onFinishClick() {
            weakReference.get()?.apply {
                if (mFullScreenType == STATE_FEED_TO_FULL_FORWARD_SCREEN || mFullScreenType == STATE_FEED_TO_FULL_REVERSE_SCREEN) {
                    mViewBinding.videoView.changedToPortrait(true)
                }
                finishSelf()
            }
        }
    }

    private class MyOnScreenBrightnessListener(fragment: AliyunPlayerSkinFragment) :
        OnScreenBrightnessListener {
        private val weakReference: WeakReference<AliyunPlayerSkinFragment> = WeakReference(fragment)
        override fun onScreenBrightness(brightness: Int) {
            weakReference.get()?.apply {
                setWindowBrightness(brightness)
                mViewBinding.videoView.screenBrightness = brightness
            }
        }
    }

    /**
     * 播放器出错监听
     */
    private class MyOnErrorListener(activity: AliyunPlayerSkinFragment) :
        OnErrorListener {
        private val weakReference = WeakReference(activity)
        override fun onError(errorInfo: com.aliyun.player.bean.ErrorInfo) {
            weakReference.get()?.onError(errorInfo)
        }
    }

    private fun onError(errorInfo: com.aliyun.player.bean.ErrorInfo) {
        //鉴权过期
        if (errorInfo.code.value == ErrorCode.ERROR_SERVER_POP_UNKNOWN.value) {
            mIsTimeExpired = true
        }
    }

    /**
     * 字幕、清晰度、码率、音轨点击事件
     */
    private class MyOnTrackInfoClickListener(fragment: AliyunPlayerSkinFragment) :
        OnTrackInfoClickListener {
        private val weakReference = WeakReference(fragment)

        //字幕
        override fun onSubtitleClick(subtitleTrackInfoList: List<TrackInfo>) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onSubtitleClick(subtitleTrackInfoList)
        }

        //音轨
        override fun onAudioClick(audioTrackInfoList: List<TrackInfo>) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onAudioClick(audioTrackInfoList)
        }

        //码率
        override fun onBitrateClick(bitrateTrackInfoList: List<TrackInfo>) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onBitrateClick(bitrateTrackInfoList)
        }

        //清晰度
        override fun onDefinitionClick(definitionTrackInfoList: List<TrackInfo>) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onDefinitionClick(definitionTrackInfoList)
        }
    }

    /**
     * 字幕改变事件
     */
    private fun onSubtitleClick(subtitleTrackInfoList: List<TrackInfo>) {
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        showFunctionShadowView(true)
        val mTrackInfoView = TrackInfoView(requireContext())
        mTrackInfoView.setTrackInfoLists(subtitleTrackInfoList)
        mTrackInfoView.setCurrentTrackInfo(mViewBinding.videoView.currentTrack(TrackInfo.Type.TYPE_SUBTITLE))
        showMoreDialog?.apply {
            setContentView(mTrackInfoView)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
        mTrackInfoView.setOnSubtitleChangedListener(object : OnSubtitleChangedListener {
            override fun onSubtitleChanged(selectTrackInfo: TrackInfo) {
                mViewBinding.videoView.selectTrack(selectTrackInfo)
            }

            override fun onSubtitleCancel() {
                AVToast.show(requireContext(), true, R.string.alivc_player_cancel_subtitle)
            }
        })
    }

    /**
     * 音轨改变事件
     */
    private fun onAudioClick(audioTrackInfoList: List<TrackInfo>) {
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        showFunctionShadowView(true)
        val mTrackInfoView = TrackInfoView(requireContext())
        mTrackInfoView.setTrackInfoLists(audioTrackInfoList)
        mTrackInfoView.setCurrentTrackInfo(mViewBinding.videoView.currentTrack(TrackInfo.Type.TYPE_AUDIO))
        showMoreDialog?.apply {
            setContentView(mTrackInfoView)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
        mTrackInfoView.setOnAudioChangedListener { selectTrackInfo ->
            mViewBinding.videoView.selectTrack(selectTrackInfo)
        }
    }

    /**
     * 码率改变事件
     */
    private fun onBitrateClick(bitrateTrackInfoList: List<TrackInfo>) {
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        showFunctionShadowView(true)
        val mTrackInfoView = TrackInfoView(requireContext())
        mTrackInfoView.setTrackInfoLists(bitrateTrackInfoList)
        mTrackInfoView.setCurrentTrackInfo(mViewBinding.videoView!!.currentTrack(TrackInfo.Type.TYPE_VIDEO))
        showMoreDialog?.apply {
            setContentView(mTrackInfoView)
            setOnDismissListener {
                showFunctionShadowView(false)
            }
            show()
        }
        mTrackInfoView.setOnBitrateChangedListener { selectTrackInfo, checkedId ->
            if (checkedId == R.id.auto_bitrate) {
                mViewBinding.videoView.selectAutoBitrateTrack()
            } else {
                mViewBinding.videoView.selectTrack(selectTrackInfo)
            }
            closeFunctionDialog()
        }
    }

    /**
     * 清晰度改变事件
     */
    private fun onDefinitionClick(definitionTrackInfoList: List<TrackInfo>) {
        showMoreDialog = AlivcShowMoreDialog(requireContext())
        showFunctionShadowView(true)
        val mTrackInfoView = TrackInfoView(requireContext())
        mTrackInfoView.setTrackInfoLists(definitionTrackInfoList)
        mTrackInfoView.setCurrentTrackInfo(mViewBinding.videoView.currentTrack(TrackInfo.Type.TYPE_VOD))
        showMoreDialog?.apply {
            setContentView(mTrackInfoView)
            setOnDismissListener {
                showFunctionShadowView(false)

            }
            show()
        }
        mTrackInfoView.setOnDefinitionChangedListener { selectTrackInfo ->
            mViewBinding.videoView.selectTrack(selectTrackInfo)
        }
    }

    private class MyOnInfoListener(aliyunPlayerSkinFragment: AliyunPlayerSkinFragment) :
        OnInfoListener {
        private val weakReference: WeakReference<AliyunPlayerSkinFragment> =
            WeakReference(aliyunPlayerSkinFragment)

        override fun onInfo(infoBean: InfoBean) {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onInfo(infoBean)
        }
    }

    private fun onInfo(infoBean: InfoBean) {
        videoDeveloperModeView?.apply {
            val option: Float = mViewBinding.videoView.getOption(Option.RenderFPS)
            setUpData(option)
        }

        when (infoBean.code) {
            InfoCode.CacheSuccess -> AVToast.show(
                requireContext(),
                true,
                R.string.alivc_player_cache_success
            )
            InfoCode.CacheError -> AVToast.show(requireContext(), true, infoBean.extraMsg)
            InfoCode.SwitchToSoftwareVideoDecoder -> AVToast.show(
                requireContext(),
                true,
                R.string.alivc_player_switch_to_software_video_decoder
            )
        }
    }

    /**
     * sei监听事件
     */
    private class MyOnSeiDataListener(aliyunPlayerSkinFragment: AliyunPlayerSkinFragment) :
        OnSeiDataListener {
        private val weakReference = WeakReference(aliyunPlayerSkinFragment)
        override fun onSeiData(type: Int, bytes: ByteArray) {
            weakReference.get()?.onSeiData(type, bytes)
        }
    }

    private fun onSeiData(type: Int, bytes: ByteArray) {
        Log.e(TAG, "onSeiData: type = " + type + " data = " + String(bytes))
    }

    /**
     * TipsView点击监听事件
     */
    private class MyOnTipClickListener(aliyunPlayerSkinFragment: AliyunPlayerSkinFragment) :
        OnTipClickListener {
        private val weakReference = WeakReference(aliyunPlayerSkinFragment)
        override fun onContinuePlay() {}
        override fun onStopPlay() {}
        override fun onRetryPlay(errorCode: Int) {
            val aliyunPlayerSkinFragment = weakReference.get()
            if (aliyunPlayerSkinFragment != null) {
                if (errorCode == ErrorCode.ERROR_LOADING_TIMEOUT.value) {
                    aliyunPlayerSkinFragment.mViewBinding.videoView.reTry()
                } else {
                    aliyunPlayerSkinFragment.refresh()
                }
            }
        }

        override fun onReplay() {
            weakReference.get()?.onReplay()
        }

        override fun onRefreshSts() {}
        override fun onWait() {}
        override fun onExit() {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.finishSelf()
        }
    }

    private fun onReplay() {
        mViewBinding.videoView.changeSpeed(1.0f)
        mVideoDetailViewModel.playSeries(mVideoDetailViewModel.mPosition)
    }

    /**
     * 重试
     */
    private fun refresh() {
        mViewBinding.videoView.reTry()
    }

    /**
     * TipsView返回按钮点击事件
     */
    private class MyOnTipsViewBackClickListener(aliyunPlayerSkinFragment: AliyunPlayerSkinFragment) :
        OnTipsViewBackClickListener {
        private val weakReference = WeakReference(aliyunPlayerSkinFragment)
        override fun onBackClick() {
            val aliyunPlayerSkinFragment = weakReference.get()
            aliyunPlayerSkinFragment?.onTipsViewClick()
        }
    }

    private fun onTipsViewClick() {
        onBackPressed()
    }

    private class MyOnVerifyStsCallback(aliyunPlayerSkinFragment: AliyunPlayerSkinFragment) :
        OnVerifyTimeExpireCallback {
        private val weakReference = WeakReference(aliyunPlayerSkinFragment)
        override fun onVerifySts(stsInfo: StsInfo): AliPlayer.Status {
            val aliyunPlayerSkinFragment = weakReference.get()
            return aliyunPlayerSkinFragment?.onVerifySts(stsInfo) ?: AliPlayer.Status.Valid
        }

        override fun onVerifyAuth(vidAuth: VidAuth): AliPlayer.Status {
            val aliyunPlayerSkinFragment = weakReference.get()
            return aliyunPlayerSkinFragment?.onVerifyAuth(vidAuth) ?: AliPlayer.Status.Valid
        }
    }

    private fun onVerifyAuth(vidAuth: VidAuth): AliPlayer.Status {
        val mLiveExpiration = GlobalPlayerConfig.mLiveExpiration
        val expirationInGMTFormat =
            TimeFormater.getExpirationInGMTFormat(mLiveExpiration)
        //判断鉴权信息是否过期
        return if (TextUtils.isEmpty(mLiveExpiration) || DateUtil.getFixedSkewedTimeMillis() / 1000 > expirationInGMTFormat - 5 * 60) {
            val getAuthInformation = GetAuthInformation()
            getAuthInformation.getVideoPlayAuthInfo(object :
                GetAuthInformation.OnGetPlayAuthInfoListener {
                override fun onGetPlayAuthError(msg: String) {
                    mViewBinding.videoView.onStop()
                    AVToast.show(
                        requireActivity(), true,
                        "Get Auth Info error : $msg"
                    )
                }

                override fun onGetPlayAuthSuccess(dataBean: AliyunPlayAuth.PlayAuthBean) {
                    GlobalPlayerConfig.mLivePlayAuth = dataBean.playAuth
                    vidAuth.playAuth = GlobalPlayerConfig.mLivePlayAuth
                    mViewBinding.videoView.updateAuthInfo(vidAuth)
                }
            })
            AliPlayer.Status.Pending
        } else {
            AliPlayer.Status.Valid
        }
    }

    private fun onVerifySts(stsInfo: StsInfo): AliPlayer.Status {
        val mLiveExpiration = GlobalPlayerConfig.mLiveExpiration
        val expirationInGMTFormat =
            TimeFormater.getExpirationInGMTFormat(mLiveExpiration)
        //判断鉴权信息是否过期
        return if (TextUtils.isEmpty(mLiveExpiration) || DateUtil.getFixedSkewedTimeMillis() / 1000 > expirationInGMTFormat - 5 * 60) {
            val getAuthInformation = GetAuthInformation()
            getAuthInformation.getVideoPlayLiveStsInfo(object :
                GetAuthInformation.OnGetStsInfoListener {
                override fun onGetStsError(msg: String) {
                    mViewBinding.videoView.onStop()
                    AVToast.show(requireContext(), true, "Get Auth Info error : $msg")
                }

                override fun onGetStsSuccess(dataBean: AliyunSts.StsBean) {
                    GlobalPlayerConfig.mLiveStsAccessKeyId = dataBean.accessKeyId
                    GlobalPlayerConfig.mLiveStsSecurityToken = dataBean.securityToken
                    GlobalPlayerConfig.mLiveStsAccessKeySecret = dataBean.accessKeySecret
                    GlobalPlayerConfig.mLiveExpiration = dataBean.expiration
                    stsInfo.accessKeyId = GlobalPlayerConfig.mLiveStsAccessKeyId
                    stsInfo.accessKeySecret = GlobalPlayerConfig.mLiveStsAccessKeySecret
                    stsInfo.securityToken = GlobalPlayerConfig.mLiveStsSecurityToken
                    mViewBinding.videoView.updateStsInfo(stsInfo)
                }
            })
            AliPlayer.Status.Pending
        } else {
            AliPlayer.Status.Valid
        }
    }

    /**
     * 设置屏幕亮度
     */
    private fun setWindowBrightness(brightness: Int) {
        val window = requireActivity().window
        val lp = window.attributes
        lp.screenBrightness = brightness / 100.00f
        window.attributes = lp
    }

    /**
     * 仅当系统的亮度模式是非自动模式的情况下，获取当前屏幕亮度值[0, 255].
     * 如果是自动模式，那么该方法获得的值不正确。
     */
    private val currentBrightValue: Int
        get() {
            var nowBrightnessValue = 0
            val resolver = requireActivity().contentResolver
            try {
                nowBrightnessValue = Settings.System.getInt(
                    resolver,
                    Settings.System.SCREEN_BRIGHTNESS, 255
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return nowBrightnessValue
        }

    override fun onFloatViewPlayClick() {
        mFloatPlayClick = true
        mViewBinding.videoView.apply {
            val currentScreenType =
                when {
                    screenMode == AliyunScreenMode.Small -> STATE_FLOAT_TO_HALF
                    requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE -> STATE_FLOAT_TO_FULL_FORWARD_SCREEN
                    else -> STATE_FLOAT_TO_FULL_REVERSE_SCREEN
                }
            if (screenMode == AliyunScreenMode.Full) {
                changeScreenMode(AliyunScreenMode.Small, false)
            }
            //post 不可以去掉
            post {
                if (mVideoInfo?.videoId != listPlayer.getCurrentVideo().videoId) {
                    mVideoInfo = mVideoDetailViewModel.getCurrentSeriesVideoInfo()
                }
                FloatViewPlayManager.requestFloatPermissionAndShow(
                    requireActivity(),
                    listPlayer,
                    playerView,
                    isPlaying,
                    mVideoInfo,
                    currentScreenType,
                    mFrom,
                    0,
                    object : FloatViewPlayManager.OnOpenFloatView {
                        override fun onBeforeShowFloatView() {
                            mIsContinuedPlay = true
                            mViewBinding.videoView.setIsContinuedPlay(true, false)
                            finishSelf()
                        }
                    }
                )
            }
        }
    }

    private fun showFunctionShadowView(show: Boolean) {
        mViewBinding.functionClickShadow.setVisible(show)
    }

    override fun onBackPressed(): Boolean {
        if (mViewBinding.videoView.screenMode == AliyunScreenMode.Small) {
            finishSelf()
        } else {
            if (mFullScreenType == STATE_FEED_TO_FULL_FORWARD_SCREEN || mFullScreenType == STATE_FEED_TO_FULL_REVERSE_SCREEN) {
                mViewBinding.videoView.changedToPortrait(true)
                finishSelf()
            } else {
                mViewBinding.videoView.changedToPortrait(true)
            }
        }
        return true
    }

    companion object {
        private const val CREATE_TIME_MSG = "原创·138万次观看·06-01发布"
        private const val TAG = "PlayerSkinFragment"
        private const val REQUEST_EXTERNAL_STORAGE = 1

        const val FULL_SCREEN = "full_screen"
        const val CONTINUE_PLAY = "continue_play"
        const val FROM_LIST = "from_list"
        const val VIDEO_INFO = "video_info"
        const val FROM_SOURCE = "from"
        const val STATE_FEED_TO_HALF = 0// 从信息流到半屏页面
        const val STATE_FEED_TO_FULL_FORWARD_SCREEN = 1 // 从信息流到翻转横屏
        const val STATE_FEED_TO_FULL_REVERSE_SCREEN = 2 // 从信息流到翻转横屏
        const val STATE_FLOAT_TO_HALF = 0// 从信息流到半屏页面
        const val STATE_FLOAT_TO_FULL_FORWARD_SCREEN = 1// 从信息流到翻转横屏
        const val STATE_FLOAT_TO_FULL_REVERSE_SCREEN = 2 // 从信息流到翻转横屏

        const val FROM_RECOMMEND_LIST = 0
        const val FROM_ENTERTAINMENT_LIST = 1

        const val PLAY_STATE = "play_state"
        const val PLAY_STATE_INIT = 0
        const val PLAY_STATE_PLAYING = 1
        const val PLAY_STATE_PAUSE = 2
        const val PLAY_STATE_COMPLETE = 3
    }
}