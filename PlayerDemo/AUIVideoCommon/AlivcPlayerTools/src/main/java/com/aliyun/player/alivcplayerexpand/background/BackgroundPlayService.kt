package com.aliyun.player.alivcplayerexpand.background

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.listplay.IListPlayManager
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager
import com.aliyun.player.alivcplayerexpand.playlist.OnListPlayCallback
import com.aliyun.player.alivcplayerexpand.util.DensityUtil
import com.aliyun.player.alivcplayerexpand.util.ImageLoader
import com.aliyun.player.alivcplayerexpand.util.NotificationUtils
import com.aliyun.player.nativeclass.MediaInfo
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Method

private const val TAG = "BackgroundPlayService"

class BackgroundPlayService : Service(), IForegroundService {
    private var mIsStartForeground = false
    private var mPlaying = false
    private val notifyId = 4
    private lateinit var mPlayManager: IListPlayManager
    private lateinit var mNotificationUtils: NotificationUtils
    private var mAuthorName = "默认昵称"
    private lateinit var remoteViews: RemoteViews
    private lateinit var notification: Notification
    private var mVid = ""
    private var mSeriesPosition = -1
    private var mForceStartNotification = false
    private var mPlayComplete = false
    private val mListCallback = object : OnListPlayCallback {
        override fun onPrepare() {
            Log.i(
                TAG,
                "onPrepare mVid:$mVid currentvid:${mPlayManager.getCurrentVideo().videoId}"
            )
            //更新当前标题和icon
            if (mVid != mPlayManager.getCurrentVideo().videoId) {
                updateRemoteView()
                loadIcon()
            }
        }

        override fun onPlaying() {
            mPlayComplete = false
        }

        override fun onPause() {
        }

        override fun onPlayComplete() {
            Log.i(TAG, "onPlayComplete")
            mPlayComplete = true
            if (mIsStartForeground) {
                mPlaying = false
                remoteViews.setImageViewResource(
                    R.id.btn_start,
                    if (mPlaying) R.drawable.notification_pause_icon else R.drawable.notification_play_icon
                )
                mNotificationUtils.manager.notify(notifyId, notification)
            }

        }

        override fun onPlayProgress(
            playProgress: Float,
            currentPlayMillis: Int,
            durationMillis: Int
        ) {
        }

        override fun onPlayError(errorCode: Int, msg: String) {
        }

        override fun onContrastPlay(durationMillis: Int) {
        }
    }

    override fun startForeground() {
        try {
            Log.i(TAG, "startForeground mIsStartForeground:$mIsStartForeground")
            if (!mIsStartForeground) {
                mPlayManager = ListPlayManager.getCurrentListPlayManager()
                mSeriesPosition = mPlayManager.getSeriesPosition()
                mPlayComplete = mPlayManager.isPlayComplete()
                mPlayManager?.addPlayCallback(mListCallback)
                mNotificationUtils = NotificationUtils(this)
                mNotificationUtils.apply {
                    setContent(getRemoteViews())
                    setFlags(Notification.FLAG_ONGOING_EVENT)
                }
                notification =
                    mNotificationUtils.getNotification(this, "标题1", "内容化", R.mipmap.ic_launcher)
                startForeground(notifyId, notification)
                mIsStartForeground = true
                PlayServiceHelper.mServiceHasForeground = true
                Log.i(TAG, "startForeground start end")
//                mNotificationUtils.manager.notify(notifyId, notification)
                loadIcon()
            }
        } catch (e: Exception) {
            Log.i(TAG, "startForeground Exception:$e")
        }
    }

    private fun forceStartNotification() {
        Log.i(
            TAG,
            "forceStartNotification mIsStartForeground:$mIsStartForeground mForceStartNotification:$mForceStartNotification"
        )
        if (!mForceStartNotification) {
            notification =
                mNotificationUtils.getNotification(this, "标题1", "内容化", R.mipmap.ic_launcher)
            startForeground(notifyId, notification)
            mForceStartNotification = true
        }
    }

    private fun getRemoteViews(): RemoteViews {
        remoteViews =
            RemoteViews(packageName, R.layout.notification_mobile_play)
        updateRemoteView()
        return remoteViews
    }

    private fun updateRemoteView() {
        Log.i(TAG, "updateRemoteView")
        val hasNext = mPlayManager.hasNextSeries()
        val hasPre = mPlayManager.hasPreviousSeries()
        val mediaInfo: MediaInfo? = mPlayManager.getCurrentVideo()
        if (mediaInfo != null) {
            mVid = mediaInfo.videoId
        }
        mPlaying = mPlayManager.isPlaying()
        // 设置 点击通知栏的上一首按钮时要执行的意图
        remoteViews.setOnClickPendingIntent(
            R.id.btn_pre,
            getClickRemoteIntent(IPlayNotifyEvent.NOTIFY_PLAY_LAST)
        )
        // 设置 点击通知栏的下一首按钮时要执行的意图
        remoteViews.setOnClickPendingIntent(
            R.id.btn_next,
            getClickRemoteIntent(IPlayNotifyEvent.NOTIFY_PLAY_NEXT)
        )
        // 设置 点击通知栏的播放暂停按钮时要执行的意图
        remoteViews.setOnClickPendingIntent(
            R.id.btn_start,
            getClickRemoteIntent(IPlayNotifyEvent.NOTIFY_PLAY_ICON_CLICK)
        )
        // 设置 点击通知栏的根容器时要执行的意图
        remoteViews.setOnClickPendingIntent(
            R.id.ll_root,
            getClickRemoteIntent(IPlayNotifyEvent.NOTIFY_JUMP_DETAIL_PLAY)
        )
        //设置 点击通知栏，关闭按钮
        remoteViews.setOnClickPendingIntent(
            R.id.audio_mode_notification_close_icon,
            getClickRemoteIntent(IPlayNotifyEvent.NOTIFY_CLOSE_NOTIFY)
        )

        remoteViews.setImageViewResource(
            R.id.btn_next,
            if (hasNext) R.drawable.notification_next_series_enable else R.drawable.notification_next_series_disable
        )

        remoteViews.setImageViewResource(
            R.id.btn_pre,
            if (hasPre) R.drawable.notification_pre_series_enable else R.drawable.notification_pre_series_disable
        )
        remoteViews.setImageViewResource(
            R.id.btn_start,
            if (mPlaying) R.drawable.notification_pause_icon else R.drawable.notification_play_icon
        )
        remoteViews.setTextViewText(R.id.tv_title, mediaInfo?.title ?: "") // 设置通知栏上标题
        remoteViews.setTextViewText(R.id.tv_artist, mAuthorName) // 设置通知栏上作者

    }

    private fun loadIcon() {
        try {
            val desWidth = DensityUtil.dip2px(this@BackgroundPlayService, 92f)
            val desHeight = DensityUtil.dip2px(this@BackgroundPlayService, 52f)
            val mediaInfo = mPlayManager.getCurrentVideo()
            ImageLoader.loadAsBitmap(
                this,
                mediaInfo.coverUrl,
                desWidth,
                desHeight,
                object : ImageLoader.OnLoadBitmapCallback {
                    override fun onBitmapBack(bitmap: Bitmap?) {
                        Log.i(
                            TAG,
                            "onBitmapBack bitmap:%$bitmap width:${bitmap?.width} height:${bitmap?.height}"
                        )
                        if (bitmap != null) {
                            remoteViews.setImageViewBitmap(
                                R.id.audio_mode_notification_cover,
                                bitmap
                            )
                            mNotificationUtils.manager.notify(notifyId, notification)
                        }
                    }

                    override fun onError() {
                    }
                })
        } catch (e: Exception) {
            Log.i(TAG, "loadIcon exception:$e")
        }
    }


    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val matrix = Matrix()
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        matrix.postScale((width / originalWidth).toFloat(), (height / originalHeight).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
    }

    private fun getClickRemoteIntent(actionCode: Int): PendingIntent {
        val intent = Intent(this, BackgroundPlayService::class.java)
        intent.putExtra(IPlayNotifyEvent.KEY_NOTIFY_ACTION, actionCode)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getService(
                this,
                actionCode,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            )
        }else{
            PendingIntent.getService(
                this,
                actionCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate")
        startForeground()
    }


    override fun isForeground(): Boolean {
        return mIsStartForeground
    }

    override fun stopForeground() {
        mIsStartForeground = false
        PlayServiceHelper.mServiceHasForeground = false
        PlayServiceHelper.mPendingStopService = false
        Log.i(TAG, "stopForeground")
        stopForeground(true)
        mPlayManager?.removePlayCallback(mListCallback)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand")
        if (!isForeground) {
            startForeground()
        }
//        if (!isForeground) {
//            forceStartNotification()
//        }
        if (intent != null && mIsStartForeground) {
            val action = intent.getIntExtra(IPlayNotifyEvent.KEY_NOTIFY_ACTION, -1)
            Log.i(TAG, "onStartCommand action:$action")
            when (action) {
                IPlayNotifyEvent.NOTIFY_PLAY_NEXT -> {
                    if (mPlayManager.hasNextSeries()) {
                        mSeriesPosition = mPlayManager.playNextSeries()
                        EventBus.getDefault()
                            .post(BackGroundPlayChangeEvent(BackGroundPlayChangeEvent.ACTION_NEXT_SERIES))
                    }
                }
                IPlayNotifyEvent.NOTIFY_PLAY_LAST -> {
                    if (mPlayManager.hasPreviousSeries()) {
                        mSeriesPosition = mPlayManager.playPreviousSeries()
                        EventBus.getDefault()
                            .post(BackGroundPlayChangeEvent(BackGroundPlayChangeEvent.ACTION_PRE_SERIES))
                    }
                }
                IPlayNotifyEvent.NOTIFY_PLAY_ICON_CLICK -> {
                    if (mPlaying) {
                        mPlayManager.pause()
                        mPlaying = false
                        remoteViews.setImageViewResource(
                            R.id.btn_start,
                            R.drawable.notification_play_icon
                        )
                        EventBus.getDefault()
                            .post(BackGroundPlayChangeEvent(BackGroundPlayChangeEvent.ACTION_PAUSE))
                    } else {
                        mPlayManager.resumePlay()
                        mPlaying = true
                        mPlayComplete = false
                        remoteViews.setImageViewResource(
                            R.id.btn_start,
                            R.drawable.notification_pause_icon
                        )
                        EventBus.getDefault()
                            .post(BackGroundPlayChangeEvent(BackGroundPlayChangeEvent.ACTION_RESUME))
                    }
                    mNotificationUtils.manager.notify(notifyId, notification)
                }
                IPlayNotifyEvent.NOTIFY_JUMP_DETAIL_PLAY -> {
                    //回到播放页
                    collapseStatusBar()
                    backToPlayActivity()

                }
                IPlayNotifyEvent.NOTIFY_CLOSE_NOTIFY -> {
                    stopForeground()
                    //停止播放器
                    mNotificationUtils.clearNotification()
                    mPlayManager.pause()
                    PlayServiceHelper.stopService(this)
                }
                -1 -> {
                    mAuthorName = intent.getStringExtra(PlayServiceHelper.KEY_USER_NAME)
                        ?: "默认昵称"
                    remoteViews.setTextViewText(
                        R.id.tv_artist,
                        mAuthorName
                    )
                    mNotificationUtils.manager.notify(notifyId, notification)
                    if (PlayServiceHelper.mPendingStopService) {
                        stopForeground()
                        //停止播放器
                        mNotificationUtils.clearNotification()
                        PlayServiceHelper.stopService(this)
                        PlayServiceHelper.mPendingStopService = false
                    }
                }
            }
        }
        return START_STICKY
    }

    private fun backToPlayActivity() {
        val intent = Intent("com.jjvideo.action.ENTER")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        intent.putExtra(FROM_FLOAT_PLAY, true)
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        stopForeground()
        Log.i(TAG, "onDestroy")
    }

    @SuppressLint("WrongConstant")
    private fun collapseStatusBar() {
        val currentApiVersion = Build.VERSION.SDK_INT
        try {
            val service = getSystemService("statusbar")
            val statusbarManager = Class
                .forName("android.app.StatusBarManager")
            var collapse: Method? = null
            if (service != null) {
                collapse = if (currentApiVersion <= 16) {
                    statusbarManager.getMethod("collapse")
                } else {
                    statusbarManager.getMethod("collapsePanels")
                }
                collapse.setAccessible(true)
                collapse.invoke(service)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}