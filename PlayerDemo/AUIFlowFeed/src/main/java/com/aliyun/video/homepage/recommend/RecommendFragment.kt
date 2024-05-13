package com.aliyun.video.homepage.recommend

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aliyun.apsaravideo.AppHomeWatcher
import com.aliyun.apsaravideo.videocommon.message.OpenVideoPlayPageEvent
import com.aliyun.player.alivcplayerexpand.background.BackGroundPlayChangeEvent
import com.aliyun.player.alivcplayerexpand.background.PlayServiceHelper
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.alivcplayerexpand.util.OrientationWatchDog
import com.aliyun.player.alivcplayerexpand.util.PlayConfigManager
import com.aliyun.video.R
import com.aliyun.video.common.ui.*
import com.aliyun.video.common.ui.play.IContinuePlayFragment
import com.aliyun.video.databinding.LayoutListRecommendFragmentBinding
import com.aliyun.video.floatview.FloatViewPlayManager
import com.aliyun.video.homepage.recommend.adapter.RecommendItemDelegate
import com.aliyun.video.play.VideoOrientationManager
import com.drakeet.multitype.MultiTypeAdapter
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.wrapper.RefreshFooterWrapper
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 推荐 Fragment
 */
private const val DEFAULT_FRESH_TIMES = 2000

class RecommendFragment : BaseFragment(R.layout.layout_list_recommend_fragment),
    IContinuePlayFragment, RecommendItemDelegate.OnRecommendItemClick,FragmentBackHandler {

    companion object{
        const val TAG = "RecommendFragment"
    }

    private val mViewBinding by bindView<LayoutListRecommendFragmentBinding>()
    private var mUpdate = false
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity())[RecommendViewModel::class.java]
    }
    private val mVideoOrientationManager = VideoOrientationManager()
    private var mInit = false
    private val mSkeletonItemId: Int
        get() = R.layout.item_skeleton_recommend_list_item
    var mPosition = 0
    private lateinit var mOrientationWatchDog: OrientationWatchDog
    private var mPlaying = false
    private var mPlayState = 0
    private var mPlayScene: Int = 0
    private var mOnTop = true
    private var mOnLongPress = false
    private var mPlayingBeforePause = false

    //refresh
    private val mOrientation: Int = RecyclerView.VERTICAL
    private val mSpanCount = 2
    private val mAdapter = MultiTypeAdapter()
    private val mList = mutableListOf<Any>()
    private val mSkeletonItemCount = 5
    private val mShowSkeletonRunnable = Runnable {
        if (mAdapter.itemCount == 0) {
            mViewBinding.mRcvList.showSkeleton(mSkeletonItemId, mAdapter, mSkeletonItemCount)
        }
    }

    private val mFooterLoadMoreWithNoMoreDataView by lazy {
        LayoutInflater.from(requireContext()).inflate(com.aliyun.video.common.R.layout.layout_refresh_footer_view,null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRefresh()
        if (mInit)
            return
        PlayConfigManager.init(requireContext())
        mViewModel.initListPlayManager(
            lifecycle,
            requireContext(),
            mViewBinding.mRcvList,
            R.id.mVideoContainer,
            R.id.mVideoCover
        )
        mAdapter.register(RecommendItemDelegate(this))
        mViewModel.mListLiveData.observe(
            viewLifecycleOwner, {
                if (it.isNotEmpty()) {
                    updateList(mUpdate, it)
                } else {
                    finishLoadMoreWithNoMoreData()
                }
            })
        mViewModel.mPlayState.observe(viewLifecycleOwner, { handlePlayStateChange(it) })
        mViewModel.mPlayerScene.observe(viewLifecycleOwner, { mPlayScene = it })
        mUpdate = true
        mViewModel.requestListData(false,requireContext(), true)
        initOrientationWatchDog()
        EventBus.getDefault().register(this)
        KeyEventDispatcher.addListener(lifecycle, object : OnKeyEventListener {
            override fun onKeyDown(keyCode: Int, event: KeyEvent?) {
                if (event == null)
                    return
                when (keyCode) {
                    KeyEvent.KEYCODE_VOLUME_UP -> {
                        mViewModel.openVoice(true)
                        mViewModel.changeVoiceState(true, mViewBinding.mRcvList)
                    }
                }
            }
        })
        mViewBinding.ivBack.setOnClickListener {
            requireActivity().finish()
        }
        mViewModel.openVoice(!PlayConfigManager.getPlayConfig().listPlayMute)
        mInit = true
    }

    private fun initRefresh(){
        mViewBinding.apply {
            mRefreshLayout.apply {
                setOnRefreshListener {
                    setRefreshFooter(ClassicsFooter(requireContext()))
                    finishRefresh(DEFAULT_FRESH_TIMES)
                    onRefreshData()
                }
                setOnLoadMoreListener {
                    finishLoadMore(DEFAULT_FRESH_TIMES)
                    onLoadMoreData()
                }
                setRefreshHeader(MaterialHeader(requireContext()))
                setRefreshFooter(ClassicsFooter(requireContext()))
            }

            mRcvList.setItemViewCacheSize(8)
            mRcvList.addItemDecoration(getItemDecoration())
            mRcvList.layoutManager = LinearLayoutManager(requireContext(), mOrientation, false)
            mRcvList.adapter = mAdapter
            mRcvList.postDelayed(mShowSkeletonRunnable, 200L)
        }
    }

    private fun getItemDecoration(): RecyclerView.ItemDecoration {
        return SpaceItemDecoration(mOrientation, dip2px(requireContext(), 9f))
    }

    fun dip2px(paramContext: Context, paramFloat: Float): Int {
        return (0.5f + paramFloat * paramContext.resources.displayMetrics.density).toInt()
    }

    private fun showEmptyView(empty: Boolean) {
        mViewBinding.apply {
            mRcvList.visibility = if (empty) View.GONE else View.VISIBLE
            mEmptyLayout.visibility = if (empty) View.VISIBLE else View.GONE
        }
    }

    override fun onAppStateChange(state: Int, activity: Activity?) {
        if (activity != getActivity() || activity == null) return

        when (state) {
            AppHomeWatcher.AppStatusWatchListener.STATE_RESUME -> {
                mActivityPaused = false
                mActivityResume = true
                onActivityResume()
            }

            AppHomeWatcher.AppStatusWatchListener.STATE_PAUSE -> {
                mActivityPaused = true
                mActivityResume = false
                onActivityPause()
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenVideoPlayPageEvent(event: OpenVideoPlayPageEvent) {
        val tabIndex = event.tabIndex
        val bundle = event.bundle
        mVideoOrientationManager.showVideoPlayFragment(
            requireActivity(), this, R.id.mFragmentContainer, bundle
        )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackgroundPlayChangeEvent(event: BackGroundPlayChangeEvent) {
        when (event.action) {
            BackGroundPlayChangeEvent.ACTION_PAUSE -> {
                mViewModel.updateAudioViewPlayStateIcon(mViewBinding.mRcvList, false)
            }

            BackGroundPlayChangeEvent.ACTION_RESUME -> {
                mViewModel.updateAudioViewPlayStateIcon(mViewBinding.mRcvList, true)

            }
        }
    }

    private fun initOrientationWatchDog() {
        mOrientationWatchDog =
            OrientationWatchDog(
                requireContext()
            )
        mOrientationWatchDog.setOnOrientationListener(object :
            OrientationWatchDog.OnOrientationListener {
            override fun changedToLandReverseScape(fromPort: Boolean) {
                if (mIsHidden || isHostInvalid() || !mPlaying || mIsPause || FloatViewPlayManager.mFloatViewShowing)
                    return
                //切换到横屏
                jumpFullScreen(mViewModel.mPosition, true)
            }

            override fun changedToPortrait(fromLand: Boolean) {
                if (mIsHidden || isHostInvalid() || !mPlaying || mIsPause || FloatViewPlayManager.mFloatViewShowing)
                    return
                //切换到竖屏
            }
            override fun changedToLandForwardScape(fromPort: Boolean) {
                if (mIsHidden || isHostInvalid() || !mPlaying || mIsPause || FloatViewPlayManager.mFloatViewShowing)
                    return
                //切换到横屏
                jumpFullScreen(mViewModel.mPosition, false)
            }

        })
    }

    override fun onResume() {
        super.onResume()
        mOrientationWatchDog.startWatch()
        requestBaseActivity()?.fullScreen(false)
        if (mActivityResume) {
            if (PlayServiceHelper.mServiceStart) {
                PlayServiceHelper.stopService(requireContext())
            }
            if (mPlayingBeforePause && !FloatViewPlayManager.mFloatViewShowing) {
                mViewModel.getListPlayer().resumeListPlay()
                mPlayingBeforePause = false
            }
        }
    }

    fun <T : Any> updateList(refresh: Boolean, list: MutableList<T>) {
        if (refresh) {
            mViewBinding.mRcvList.removeCallbacks(mShowSkeletonRunnable)
            mViewBinding.mRcvList.hideSkeleton()
            showEmptyView(list.isEmpty())
            mList.clear()
            mList.addAll(list)
            mAdapter.items = mList
            mAdapter.notifyDataSetChanged()

            if (!mIsPause) {
                mViewBinding.mRcvList.post {
                    mViewBinding.mRcvList.hideSkeleton()
                    mViewModel.startPlay(mViewBinding.mRcvList, R.id.mVideoContainer)
                }
            }
        }else {
            if (list.isEmpty()) return
            val preSize = mList.size
            mList.addAll(list)
            mAdapter.notifyItemRangeInserted(preSize, list.size)
        }
    }

    private fun handlePlayStateChange(state: Int) {
        mPlayState = state
        mPlaying = when (state) {
            PLAY_STATE_PLAYING ->  true
            else ->  false
        }
    }

    private fun hideViewWhenPlaying(rcv: RecyclerView) {
        val videoCover: View? = getRecyclerViewItemChildView(rcv, mPosition, R.id.mVideoCover)
        videoCover?.visibility = View.GONE
        val playIcon: ImageView? =
            getRecyclerViewItemChildView(rcv, mPosition, R.id.mVideoPlayIcon)
        playIcon?.apply {
            this.visibility = View.GONE
            setTag(R.id.item_playing, true)
            setImageResource(R.drawable.feed_play_pause_icon)
        }
    }

    override fun onPause() {
        super.onPause()
        mViewModel.refreshGlobalPlayConfig()
        mOrientationWatchDog.stopWatch()
        if (mActivityPaused) {
            if (ListPlayManager.mGlobalPlayEnable && mOnTop && !FloatViewPlayManager.mFloatViewShowing && !isActivityTop(
                    requestBaseActivity()!!.javaClass,
                    requireContext()
                )
                && mViewModel.mPlayConfig?.listPlayMute == false
            ) {
                PlayServiceHelper.startPlayService(requireContext(), mViewModel.getAuthorName())
            }
        } else {
            if (!FloatViewPlayManager.mFloatViewShowing) {
                mPlayingBeforePause = mViewModel.getListPlayer().isPlaying()
                mViewModel.getListPlayer().pause()
            }
        }
    }

    private fun isActivityTop(cls: Class<*>, context: Context): Boolean {
        val manager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val name = manager.getRunningTasks(1)[0].topActivity!!.className
        return name == cls.name
    }

    private fun onRefreshData() {
        mUpdate = true
        mViewModel.requestListData(false,requireContext(), true)
    }

    private fun onLoadMoreData() {
        mUpdate = false
        mViewModel.requestListData(true,requireContext(), false)
    }

    override fun onBeforeHidden() {}

    override fun onReShow() {
        mViewBinding.mRcvList.post {
            mViewModel.continuePlay(mViewBinding.mRcvList, R.id.mVideoContainer)
            mOnTop = true
        }
    }

    private fun finishLoadMoreWithNoMoreData(){
        mViewBinding.mRefreshLayout.setRefreshFooter(RefreshFooterWrapper(mFooterLoadMoreWithNoMoreDataView))
        mViewBinding.mRefreshLayout.finishLoadMore(DEFAULT_FRESH_TIMES,true,true)
    }

    override fun jumpHalfScreenPage(position: Int) {
        updateVoiceIcon(true)
        var continuePlay = mViewModel.mPosition == position
        //暂停播放当前，恢复当前封面
        val playState = mPlayState
        if (FloatViewPlayManager.mFloatViewShowing) {
            continuePlay =
                mViewModel.sameVideo(mViewModel.getListPlayer().getCurrentVideo().videoId, position)
            FloatViewPlayManager.closeFloatPlayView(true)
        }
        mViewModel.stopPlay(
            mViewBinding.mRcvList,
            R.id.mVideoContainer,
            mViewModel.mPosition,
            continuePlay
        )
        if (mViewModel.mInFloatPlayState) {
            if (continuePlay && !mViewModel.getListPlayer().isPlaying()) {
                playVideo(mViewModel.mPosition)
            }
        }
        mViewModel.updatePlayPosition(position)
        mVideoOrientationManager.switchToHalfScreenVideo(
            requireActivity(),
            this@RecommendFragment,
            R.id.mFragmentContainer,
            continuePlay,
            mViewModel.getVideoList()[position],
            playState
        )
        mOnTop = false
    }

    override fun jumpFullScreenPage(position: Int) {
        jumpFullScreen(position)
    }

    private fun jumpFullScreen(position: Int, reverseScreen: Boolean = false) {
        updateVoiceIcon(true)
        mViewModel.stopPlay(
            mViewBinding.mRcvList,
            R.id.mVideoContainer,
            position,
            true
        )
        val playState = mPlayState
        mVideoOrientationManager.switchToFullScreenVideo(
            requireActivity(),
            this@RecommendFragment,
            mViewModel.getVideoList()[position],
            R.id.mFragmentContainer,
            reverseScreen,
            playState
        )
        mOnTop = false
    }

    override fun pauseVideo(position: Int) {
        mViewModel.clickVideo(
            position, mViewBinding.mRcvList,
            R.id.mVideoContainer,
            R.id.mVideoCover
        )
    }

    override fun playVideo(position: Int) {
        mViewModel.clickVideo(
            position, mViewBinding.mRcvList,
            R.id.mVideoContainer,
            R.id.mVideoCover
        )
    }

    override fun onSeekFinishVideo(progress: Int) {
        mViewModel.seekVideo(mViewBinding.mRcvList, progress)
    }

    override fun onSeeking(progress: Int) {
        mViewModel.onSeeking(mViewBinding.mRcvList, progress)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mViewBinding.mRcvList.removeCallbacks(mShowSkeletonRunnable)
        mViewModel.releasePlayer()
        mOrientationWatchDog.stopWatch()
        mOrientationWatchDog.destroy()
        EventBus.getDefault().unregister(this)
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

    override fun onLongPress(begin: Boolean) {
        mViewModel.showLongPressTip(mViewBinding.mRcvList, begin)
        if (begin) {
            mViewModel.changeSpeed(2.0f)
            mOnLongPress = true
        } else {
            if (mOnLongPress) {
                mViewModel.changeSpeed(1.0f)
            }
        }
    }

    override fun onVoiceOpen(open: Boolean) {
        mViewModel.openVoice(open)
        updateVoiceIcon(open)
    }

    private fun updateVoiceIcon(open: Boolean) {
        if (mViewBinding.mRcvList.adapter == null) return
        val firstVisibleItem = 0
        val lastVisibleItem = mViewBinding.mRcvList.adapter!!.itemCount
        if (lastVisibleItem > 0) {
            for (index in firstVisibleItem..lastVisibleItem) {
                val checkBox: CheckBox? =
                    getRecyclerViewItemChildView(mViewBinding.mRcvList, index, R.id.check_box_voice)
                checkBox?.isChecked = open
            }
        }
    }

    override fun onBackPressed(): Boolean {
        requireActivity().finish()
        return true
    }

    class SpaceItemDecoration(private val orientation: Int, private val space: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            if (orientation == RecyclerView.VERTICAL) {
                outRect.bottom = space
            } else {
                outRect.left = space
            }
        }
    }
}