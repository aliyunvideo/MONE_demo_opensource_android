package com.aliyun.video

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import com.aliyun.apsaravideo.videocommon.message.OpenVideoPlayPageEvent
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.apsaravideo.AppHomeWatcher
import com.aliyun.player.alivcplayerexpand.background.PlayServiceHelper
import com.aliyun.video.common.ui.BackHandlerHelper
import com.aliyun.video.common.ui.BaseActivity
import com.aliyun.video.databinding.ActivityMainBinding
import com.aliyun.video.homepage.recommend.RecommendFragment
import com.aliyun.video.play.AliyunPlayerSkinFragment
import com.aliyun.video.play.NORMAL_HALF_SCREEN_FRAGMENT
import org.greenrobot.eventbus.EventBus


private const val TAG = "MainActivity"
const val FROM_FLOAT_PLAY = "from_float_play"
const val VIDEO_INFO = "video_info"

class MainActivity : BaseActivity() {
    private lateinit var mViewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppHomeWatcher.InnerHolder.mInstance.startWatch(application)
        mViewBinding = ActivityMainBinding.inflate(layoutInflater)
        val rootView = mViewBinding.root
        setContentView(rootView)

        if (PlayServiceHelper.mServiceStart) {
            PlayServiceHelper.stopService(this)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        initFragment()
    }

    private fun initFragment(){
        showFragment<RecommendFragment>(R.id.mFragmentContainer,null,RecommendFragment.TAG)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        getIntent()?.apply {
            val fromFloatPlay = getBooleanExtra(FROM_FLOAT_PLAY, false)
            val videoInfo: VideoInfo? =
                getParcelableExtra(
                    VIDEO_INFO
                )
            val screenType = getIntExtra(AliyunPlayerSkinFragment.FULL_SCREEN, 0)
            val from = getIntExtra(AliyunPlayerSkinFragment.FROM_SOURCE, 0)

            if (fromFloatPlay && videoInfo != null) {
                val bundle = Bundle().apply {
                    putParcelable(VIDEO_INFO, videoInfo)
                    putBoolean(AliyunPlayerSkinFragment.CONTINUE_PLAY, true)
                    putBoolean(AliyunPlayerSkinFragment.FROM_LIST, true)
                    putInt(AliyunPlayerSkinFragment.FULL_SCREEN, screenType)
                }
                if (from == AliyunPlayerSkinFragment.FROM_RECOMMEND_LIST) {
                    EventBus.getDefault().post(
                        OpenVideoPlayPageEvent(
                            from, bundle
                        )
                    )
                } else {
                    val bundle = Bundle().apply {
                        putParcelable(VIDEO_INFO, videoInfo)
                        putBoolean(AliyunPlayerSkinFragment.CONTINUE_PLAY, true)
                        putBoolean(AliyunPlayerSkinFragment.FROM_LIST, false)
                        putInt(AliyunPlayerSkinFragment.FULL_SCREEN, screenType)
                    }
                    //进入播放详情页
                    showFragment<AliyunPlayerSkinFragment>(
                        R.id.mFragmentContainer,
                        bundle,
                        NORMAL_HALF_SCREEN_FRAGMENT
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewBinding.root.removeCallbacks(null)
    }

    override fun onBackPressed() {
        if (!BackHandlerHelper.handleBackPress(this)) {
            if (!handleBackSpace()) {
                finish()
            }
        }
    }
}