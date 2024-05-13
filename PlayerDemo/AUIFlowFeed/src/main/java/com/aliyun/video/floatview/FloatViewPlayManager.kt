package com.aliyun.video.floatview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.aliyun.auiplayerserver.bean.VideoInfo
import com.aliyun.player.alivcplayerexpand.listplay.IListPlayManager
import com.aliyun.player.alivcplayerexpand.listplay.IPlayManagerScene
import com.aliyun.player.alivcplayerexpand.playlist.OnListPlayCallback
import com.aliyun.player.alivcplayerexpand.view.gesture.GestureView
import com.aliyun.player.alivcplayerexpand.view.gesture.SimplyTapGestureListener
import com.aliyun.video.R
import com.aliyun.video.play.AliyunPlayerSkinFragment
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.enums.SidePattern
import com.lzf.easyfloat.interfaces.OnInvokeView
import com.lzf.easyfloat.interfaces.OnPermissionResult
import com.lzf.easyfloat.interfaces.OnTouchRangeListener
import com.lzf.easyfloat.permission.FloatViewPermissionUtils
import com.lzf.easyfloat.utils.DragUtils
import com.lzf.easyfloat.widget.BaseSwitchView

/**
 * 显示悬浮
 * 1.抠下 SurfaceView
 * 2.退出 Fragment
 * 3.显示悬浮小窗
 * 4.将SurfaceView 加入悬浮小窗
 *
 * 点击关闭悬浮小窗-关闭悬浮小窗
 *
 * 点击悬浮小窗-进入半屏播放 Fragment
 */
object FloatViewPlayManager {
    const val FROM_FLOAT_PLAY = "from_float_play"
    private const val TAG = "FloatViewPlayManager"
    private const val FULL_SCREEN = "full_screen"
    private const val VIDEO_INFO = "video_info"
    private var mPlayer: IListPlayManager? = null
    var mVideoInfo: VideoInfo? = null
    private var vibrator: Vibrator? = null
    private var vibrating = false
    private var mShowFloatFunctionView = false
    private var mPlaying = false
    private var mApplicationContext: Context? = null
    var mFloatViewShowing = false
    private var mScreenType = 0
    private var mTabIndex = 0
    private var mListPlayCallback: OnListPlayCallback? = null
    private var mRootView: View? = null

    private fun showFloatPlayer(
        context: Context,
        player: IListPlayManager,
        playView: View,
        playing: Boolean,
        videoInfo: VideoInfo?,
        from: Int
    ): FloatViewPlayManager {
        mVideoInfo = videoInfo
        mApplicationContext = context.applicationContext
        mPlayer = player
        mPlayer?.setPlayerScene(IPlayManagerScene.SCENE_FLOAT_PLAY)
        showFloatPlayVideoView(context, playView, player.isPlaying(), from)
        Log.i(TAG, "showFloatPlayer player:$player playView:$playView")
        return this
    }

    fun requestFloatPermissionAndShow(
        activity: Activity,
        player: IListPlayManager,
        playView: View,
        playing: Boolean,
        videoInfo: VideoInfo?,
        screenType: Int,
        from: Int,
        tabIndex: Int,
        onOpenFloatView: OnOpenFloatView
    ) {

        mTabIndex = tabIndex
        mScreenType = screenType
        player.setSeriesPlayEnable(false)
        if (FloatViewPermissionUtils.checkPermission(activity.applicationContext)) {
            onOpenFloatView.onBeforeShowFloatView()
            showFloatPlayer(activity, player, playView, playing, videoInfo, from)
        } else {
            val sApplication = activity.application
            AlertDialog.Builder(activity)
                .setMessage("使用浮窗功能，需要您授权悬浮窗权限。")
                .setPositiveButton("去开启") { _, _ ->
                    FloatViewPermissionUtils.requestPermission(
                        activity,
                        object : OnPermissionResult {
                            override fun permissionResult(isOpen: Boolean) {
                                if (isOpen) {
                                    onOpenFloatView.onBeforeShowFloatView()
                                    showFloatPlayer(
                                        activity,
                                        player,
                                        playView,
                                        playing,
                                        videoInfo,
                                        from
                                    )
                                } else {
                                    Toast.makeText(
                                        sApplication,
                                        "开启小窗播放，需要系统权限",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        })
                }
                .setNegativeButton("取消") { _, _ ->
                    Toast.makeText(
                        sApplication,
                        "开启小窗播放，需要系统权限",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .show()
        }
    }

    private fun showFloatPlayVideoView(
        context: Context,
        playView: View,
        playing: Boolean,
        from: Int
    ) {
        if (vibrator == null) {
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        mPlaying = playing
        mFloatViewShowing = true
        EasyFloat.with(context.applicationContext)
            .setShowPattern(ShowPattern.ALL_TIME)
            .setSidePattern(SidePattern.RESULT_SIDE)
            .setImmersionStatusBar(true)
            .setGravity(Gravity.END, -20, 100)
            .setLayout(R.layout.layout_float_play, OnInvokeView {
                it.apply {
                    mRootView = this
                    val closeView =
                        findViewById<View>(R.id.float_video_close_icon)
                    closeView.setOnClickListener {
                        rootView.visibility = View.GONE
                        closeFloatPlayView(true)
                    }
                    val progressBar =
                        findViewById<ProgressBar>(R.id.folat_video_progress_play_duration)
                    val playStateView = findViewById<ImageView>(R.id.float_play_state_icon)
                    if (mListPlayCallback == null) {
                        mListPlayCallback = object : OnListPlayCallback {
                            override fun onPrepare() {
                            }

                            override fun onPlaying() {
                                mPlaying = true
                                playStateView.setImageResource(
                                    if (mPlaying) R.drawable.float_play_pause else R.drawable.float_play_play
                                )
                            }

                            override fun onPause() {
                            }

                            override fun onPlayComplete() {
                                mPlaying = false
                                playStateView.setImageResource(
                                    if (mPlaying) R.drawable.float_play_pause else R.drawable.float_play_play
                                )
                            }

                            override fun onPlayProgress(
                                playProgress: Float,
                                currentPlayMillis: Int,
                                durationMillis: Int
                            ) {
//                                Log.i(TAG, "onPlayProgress  playProgress:$playProgress")
                                progressBar.progress = (playProgress * 100).toInt()
                            }

                            override fun onPlayError(errorCode: Int, msg: String) {
                            }

                            override fun onContrastPlay(durationMillis: Int) {
                            }

                        }
                    }
                    mPlayer?.addPlayCallback(mListPlayCallback!!)

                    playStateView.setImageResource(
                        if (playing) R.drawable.float_play_pause else R.drawable.float_play_play
                    )
                    playStateView.setOnClickListener {
                        mPlaying = !mPlaying
                        if (mPlaying) {
                            mPlayer?.resumePlay()
                        } else {
                            mPlayer?.pause()
                        }
                        playStateView.setImageResource(
                            if (mPlaying) R.drawable.float_play_pause else R.drawable.float_play_play
                        )
                    }
                    val fullScreenView = findViewById<View>(R.id.float_video_full_screen)
                    fullScreenView.setOnClickListener {
                        if (mApplicationContext != null) {
                            //暂停回到续播
                            if (from != AliyunPlayerSkinFragment.FROM_RECOMMEND_LIST) {
                                mPlayer?.pause()
                            }
                            mPlayer?.setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
                            val intent = Intent("com.jjvideo.action.ENTER")
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                putExtra(FROM_FLOAT_PLAY, true)
                                putExtra(FULL_SCREEN, mScreenType)
                                putExtra(AliyunPlayerSkinFragment.FROM_SOURCE, from)
                                putExtra(AliyunPlayerSkinFragment.FROM_LIST, true)
                                putExtra(VIDEO_INFO, mVideoInfo)
                            }
                            context.startActivity(intent)
                            closeFloatPlayView(false)
                        }
                    }
                    val playContainer = findViewById<FrameLayout>(R.id.float_video_container)
                    val lp = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    (playView?.parent as ViewGroup?)?.removeView(playView)
                    playContainer.addView(playView, lp)
                    val gestureView = findViewById<GestureView>(R.id.float_video_gesture)
                    val hideRunnable = Runnable {
                        mShowFloatFunctionView = false
                        playStateView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                        closeView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                        fullScreenView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                    }
                    gestureView.setOnGestureListener(object : SimplyTapGestureListener() {
                        override fun onSingleTapClick() {
                            mShowFloatFunctionView = !mShowFloatFunctionView
                            playStateView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                            closeView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                            fullScreenView.visibility = if (mShowFloatFunctionView) View.VISIBLE else View.GONE
                            gestureView.removeCallbacks(hideRunnable)
                            if (mShowFloatFunctionView) {
                                gestureView.postDelayed(hideRunnable, 3000)
                            }
                        }
                    })
                }
            })
            .registerCallback {
                drag { _, motionEvent ->
                    DragUtils.registerDragClose(motionEvent, object : OnTouchRangeListener {
                        override fun touchInRange(inRange: Boolean, view: BaseSwitchView) {
                            setVibrator(inRange)
                            view.findViewById<TextView>(com.lzf.easyfloat.R.id.tv_delete).text =
                                if (inRange) "松手删除" else "删除浮窗"

                            view.findViewById<ImageView>(com.lzf.easyfloat.R.id.iv_delete)
                                .setImageResource(
                                    if (inRange) com.lzf.easyfloat.R.drawable.icon_delete_selected
                                    else com.lzf.easyfloat.R.drawable.icon_delete_normal
                                )
                        }

                        override fun touchUpInRange() {
                            closeFloatPlayView(true)
                        }
                    }, showPattern = ShowPattern.ALL_TIME)
                }
            }
            .show()
    }

    fun closeFloatPlayView(stopPlay: Boolean) {
        Log.i(TAG, "closeFloatPlayView stopPlay:$stopPlay")
        if (stopPlay) {
            mPlayer?.apply {
                setSeriesPlayEnable(true)
                setPlayerScene(IPlayManagerScene.SCENE_NORMAL)
                savePlayRecord()
                pause()
                //如果 release 调的话，则后面其它场景无法继续播放了
//            getListPlayer().release()
            }
        }
        mFloatViewShowing = false
        if (mListPlayCallback != null) {
            mPlayer?.removePlayCallback(mListPlayCallback!!)
        }
        mPlayer = null
        mListPlayCallback = null
        mRootView?.visibility = View.GONE
        EasyFloat.dismiss()
    }

    fun setVibrator(inRange: Boolean) {
        if (!vibrator!!.hasVibrator() || (inRange && vibrating)) return
        vibrating = inRange
        if (inRange) if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator!!.vibrate(
                VibrationEffect.createOneShot(
                    100,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else vibrator!!.vibrate(100)
        else vibrator!!.cancel()
    }

    interface OnOpenFloatView {
        fun onBeforeShowFloatView()
    }
}