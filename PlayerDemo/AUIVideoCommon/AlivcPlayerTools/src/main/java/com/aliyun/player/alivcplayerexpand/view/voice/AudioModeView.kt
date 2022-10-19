package com.aliyun.player.alivcplayerexpand.view.voice

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.aio.utils.DensityUtil
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.databinding.LayoutAudioModeBinding
import com.aliyun.player.aliyunplayerbase.util.AliyunScreenMode
import com.aliyun.player.aliyunplayerbase.util.ImageLoader
import com.aliyun.player.aliyunplayerbase.util.isVisible
import com.aliyun.player.aliyunplayerbase.util.setVisible

class AudioModeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding =
        LayoutAudioModeBinding.inflate(LayoutInflater.from(context), this, true)
    var mOnAudioModeListener: OnAudioModeListener? = null
    private var mPlaying = false
    private var mPlayComplete = false

    init {
        mViewBinding.apply {
            audioModeBackVideoClick.setOnClickListener {
                mOnAudioModeListener?.closeAudioMode()
                hideSelf()
            }
            audioModePlayStateIcon.setOnClickListener {
                mPlaying = !mPlaying
                updatePlayIcon()
                mOnAudioModeListener?.clickPlayIcon()
            }
            audioModeReplay.setOnClickListener {
                mPlaying = true
                updatePlayIcon()
                audioModeReplay.setVisible(false)
                mOnAudioModeListener?.onReplay()
            }
        }

    }

    private fun hideSelf() {
        setVisible(false)
    }

    private fun updatePlayIcon() {
        mViewBinding.audioModePlayStateIcon.setImageResource(if (mPlaying) R.drawable.video_pause_icon else R.drawable.video_play_icon)
        if (mPlaying) {
            mViewBinding.audioModeReplay.setVisible(false)
        }
    }

    fun updatePlayIcon(playing: Boolean) {
        mPlaying = playing
        updatePlayIcon()
    }

    fun onPlayEnd() {
        mPlayComplete = true
        if (isVisible()) {
            mViewBinding.audioModeReplay.setVisible(true)
            mPlaying = false
            updatePlayIcon()
        }
    }

    fun setUpData(
        coverUrl: String,
        mode: AliyunScreenMode,
        isPlaying: Boolean,
        needBlurBg: Boolean = false,
        playComplete: Boolean
    ) {
        mViewBinding.audioModeBg.setVisible(needBlurBg)
        ImageLoader.loadCircleImg(
            coverUrl,
            mViewBinding.audioModePortrait,
            R.drawable.default_portrait_icon
        )
        setScreenMode(mode)
        mPlaying = isPlaying
        if (playComplete) {
            onPlayEnd()
        } else {
            mViewBinding.audioModeReplay.setVisible(false)
        }
        mPlayComplete = playComplete
        mViewBinding.audioModePlayStateIcon.setImageResource(if (isPlaying) R.drawable.video_pause_icon else R.drawable.video_play_icon)
    }

    fun setScreenMode(mode: AliyunScreenMode) {
        if (mode == AliyunScreenMode.Full) {
            mViewBinding.apply {
                audioModeTitle.textSize = 16f
                audioModeBackTv.textSize = 14f
                audioMoreReplayTv.textSize = 14f

                val backIconLP = audioModeBackIcon.layoutParams
                backIconLP.height = DensityUtil.dip2px(context, 14f)
                backIconLP.width = DensityUtil.dip2px(context, 17f)

//                val backLp = audioModeBackVideo.layoutParams
//                backLp.width = DensityUtils.dip2px(context, 105f)
//                backLp.height = DensityUtils.dip2px(context, 36f)

                val portraitLp: MarginLayoutParams =
                    audioModePortrait.layoutParams as MarginLayoutParams
                portraitLp.height = DensityUtil.dip2px(context, 159f)
                portraitLp.width = DensityUtil.dip2px(context, 159f)
                portraitLp.marginStart = DensityUtil.dip2px(context, 181f)

                val playStateIconLp = audioModePlayStateIcon.layoutParams
                playStateIconLp.width = DensityUtil.dip2px(context, 24f)
                playStateIconLp.height = DensityUtil.dip2px(context, 40f)

                val replayIconLp = audioMoreReplayIcon.layoutParams
                replayIconLp.width = DensityUtil.dip2px(context, 14f)
                replayIconLp.height = DensityUtil.dip2px(context, 14f)

            }
        } else {
            mViewBinding.apply {
                audioModeTitle.textSize = 12f
                audioModeBackTv.textSize = 12f
                audioMoreReplayTv.textSize = 12f

                val backIconLP = audioModeBackIcon.layoutParams
                backIconLP.height = DensityUtil.dip2px(context, 10f)
                backIconLP.width = DensityUtil.dip2px(context, 12f)

//                val backLp = audioModeBackVideo.layoutParams
//                backLp.width = DensityUtils.dip2px(context, 80f)
//                backLp.height = DensityUtils.dip2px(context, 28f)

                val portraitLp: MarginLayoutParams =
                    audioModePortrait.layoutParams as MarginLayoutParams
                portraitLp.height = DensityUtil.dip2px(context, 80f)
                portraitLp.width = DensityUtil.dip2px(context, 80f)
                portraitLp.marginStart = DensityUtil.dip2px(context, 13f)

                val playStateIconLp = audioModePlayStateIcon.layoutParams
                playStateIconLp.width = DensityUtil.dip2px(context, 12f)
                playStateIconLp.height = DensityUtil.dip2px(context, 20f)

                val replayIconLp = audioMoreReplayIcon.layoutParams
                replayIconLp.width = DensityUtil.dip2px(context, 10f)
                replayIconLp.height = DensityUtil.dip2px(context, 10f)
            }

        }
    }

    interface OnAudioModeListener {
        fun closeAudioMode()
        fun clickPlayIcon()
        fun onReplay()
    }
}