package com.aliyun.video.play

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.video.common.ui.BaseFragment
import com.aliyun.video.common.ui.play.FragmentHiddenCallback
import com.aliyun.video.common.ui.play.IContinuePlayFragment
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.CONTINUE_PLAY
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.FROM_LIST
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.PLAY_STATE
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.STATE_FEED_TO_FULL_FORWARD_SCREEN
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.STATE_FEED_TO_FULL_REVERSE_SCREEN
import com.aliyun.video.play.AliyunPlayerSkinFragment.Companion.STATE_FEED_TO_HALF

/**
 * 依附在 Activity 上用于管理横竖屏切换，总共有三种切换状态
 * 1.列表播放 <---->半屏播放页
 * 2.半屏播放页 <----> 全屏播放页
 * 3.列表播放 <---> 全屏播放页面
 *
 */

private const val HALF_SCREEN_FRAGMENT = "half_screen_fragment"

class VideoOrientationManager {

    /**
     * 切换到半屏播放页
     */
    fun switchToHalfScreenVideo(
        activity: FragmentActivity,
        iContinuePlayFragment: IContinuePlayFragment?,
        containerId: Int,
        continuePlay: Boolean,
        videoInfo: VideoInfo?,
        playState: Int
    ) {
        val bundle = Bundle().apply {
            putInt(AliyunPlayerSkinFragment.FULL_SCREEN, STATE_FEED_TO_HALF)
            putBoolean(CONTINUE_PLAY, continuePlay)
            putParcelable(AliyunPlayerSkinFragment.VIDEO_INFO, videoInfo)
            putInt(PLAY_STATE, playState)
        }
        showVideoPlayFragment(activity, iContinuePlayFragment, containerId, bundle)
    }

    fun switchToFullScreenVideo(
        activity: FragmentActivity,
        iContinuePlayFragment: IContinuePlayFragment?,
        videoInfo: VideoInfo?,
        containerId: Int,
        reverseScreen: Boolean,
        playState: Int
    ) {
        val bundle = Bundle().apply {
            putInt(
                AliyunPlayerSkinFragment.FULL_SCREEN,
                if (reverseScreen) STATE_FEED_TO_FULL_REVERSE_SCREEN else STATE_FEED_TO_FULL_FORWARD_SCREEN
            )
            putBoolean(CONTINUE_PLAY, true)
            putParcelable(AliyunPlayerSkinFragment.VIDEO_INFO, videoInfo)
            putInt(PLAY_STATE, playState)
        }
        showVideoPlayFragment(activity, iContinuePlayFragment, containerId, bundle)
    }

    public fun showVideoPlayFragment(
        activity: FragmentActivity,
        iContinuePlayFragment: IContinuePlayFragment?,
        containerId: Int,
        bundle: Bundle
    ) {
        var fragment: BaseFragment? =
            activity.supportFragmentManager.findFragmentByTag(HALF_SCREEN_FRAGMENT) as BaseFragment?
        if (fragment == null) {
            fragment = AliyunPlayerSkinFragment()
        }
        bundle.apply {
            putBoolean(FROM_LIST, true)
            putInt(
                AliyunPlayerSkinFragment.FROM_SOURCE,
                AliyunPlayerSkinFragment.FROM_RECOMMEND_LIST
            )
        }
        fragment.arguments = bundle
        (fragment as AliyunPlayerSkinFragment).mHiddenCallback = object :
            FragmentHiddenCallback {
            override fun onFragmentHidden(fragment: BaseFragment, hidden: Boolean) {
                if (hidden) {
                    iContinuePlayFragment?.onReShow()
                }
            }

        }
        iContinuePlayFragment?.onBeforeHidden()
        showFragment(activity, fragment, containerId, HALF_SCREEN_FRAGMENT)
    }

    private fun showFragment(
        activity: FragmentActivity,
        fragment: BaseFragment,
        containerId: Int,
        fragmentTag: String
    ) {
        val fragmentTransaction = activity.supportFragmentManager.beginTransaction()
        if (!fragment.isAdded) {
            fragmentTransaction.add(containerId, fragment, fragmentTag)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        } else if (fragment.isHidden) {
            fragmentTransaction.show(fragment)
                .addToBackStack(null)
                .commitAllowingStateLoss()
        }
    }
}