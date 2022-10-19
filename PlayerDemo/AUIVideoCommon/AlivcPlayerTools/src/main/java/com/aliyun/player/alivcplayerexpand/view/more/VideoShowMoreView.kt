package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.IPlayer
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.databinding.LayoutVideoShowMoreBinding

class VideoShowMoreView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val moreValue: AliyunShowMoreValue
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding =
        LayoutVideoShowMoreBinding.inflate(LayoutInflater.from(context), this, true)
    var mOnShowMoreSelected: OnShowMoreSelected? = null


    init {
        val scaleMode = moreValue.scaleMode
        mViewBinding.apply {
            when (scaleMode) {
                IPlayer.ScaleMode.SCALE_ASPECT_FIT -> {
                    videoScaleAuto.isChecked = true
                    videoScaleZoom.isChecked = false
                    videoScaleTensile.isChecked = false
                }
                IPlayer.ScaleMode.SCALE_ASPECT_FILL -> {
                    videoScaleAuto.isChecked = false
                    videoScaleZoom.isChecked = false
                    videoScaleTensile.isChecked = true
                }

                IPlayer.ScaleMode.SCALE_TO_FILL -> {
                    videoScaleAuto.isChecked = false
                    videoScaleZoom.isChecked = true
                    videoScaleTensile.isChecked = false
                }
            }
        }

        mViewBinding.videoScale.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.video_scale_auto -> {
                    mOnShowMoreSelected?.onVideoScaleSelected(VideoScale.VIDEO_SCALE_AUTO)
                }
                R.id.video_scale_zoom -> {
                    mOnShowMoreSelected?.onVideoScaleSelected(VideoScale.VIDEO_SCALE_ZOOM)
                }
                R.id.video_scale_tensile -> {
                    mOnShowMoreSelected?.onVideoScaleSelected(VideoScale.VIDEO_SCALE_TENSILE)
                }
            }
        }
        if (moreValue.isEnableHardDecodeType) {
            mViewBinding.videoHardDecoder.isChecked = true
            mViewBinding.videoSoftDecoder.isChecked = false
        } else {
            mViewBinding.videoHardDecoder.isChecked = false
            mViewBinding.videoSoftDecoder.isChecked = true
        }
        mViewBinding.videoDecoder.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.video_hard_decoder -> {
                    mOnShowMoreSelected?.onVideoDecoderSelected(VideoDecoderType.HARD_TYPE)
                }
                R.id.video_soft_decoder -> {
                    mOnShowMoreSelected?.onVideoDecoderSelected(VideoDecoderType.SOFT_TYPE)
                }
            }
        }
        mViewBinding.apply {
            videoMoreListPlaySettingBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                mOnShowMoreSelected?.onItemOnOff(
                    isChecked,
                    videoMoreBackgroundPlaySettingBtn.isChecked
                )
            }
            videoMoreBackgroundPlaySettingBtn.setOnCheckedChangeListener { buttonView, isChecked ->
                mOnShowMoreSelected?.onItemOnOff(videoMoreListPlaySettingBtn.isChecked, isChecked)
            }
            danmakuRg.setOnCheckedChangeListener { group, checkedId ->
                val location = when (checkedId) {
                    R.id.danmaku_top -> {
                        0
                    }
                    R.id.danmaku_bottom -> {
                        1
                    }
                    else -> {
                        2
                    }
                }
                mOnShowMoreSelected?.onDanmukuLocation(location)
            }
        }
    }

    fun setUpData(listPlayOpen: Boolean, backGroundPlayOpen: Boolean, damkuLocation: Int) {
        mViewBinding.apply {
            videoMoreListPlaySettingBtn.isChecked = listPlayOpen
            videoMoreBackgroundPlaySettingBtn.isChecked = backGroundPlayOpen
            danmakuBottom.isChecked = damkuLocation == 1
            danmakuTop.isChecked = damkuLocation == 0
            danmakuUnlimit.isChecked = damkuLocation == 2
        }
    }


    interface OnShowMoreSelected {
        fun onVideoScaleSelected(videoScale: VideoScale)
        fun onVideoDecoderSelected(type: VideoDecoderType)
        fun onItemOnOff(listPlayOpen: Boolean, backGroundPlayOpen: Boolean)
        fun onDanmukuLocation(location: Int)
    }

    enum class VideoScale(scale: Int) {
        VIDEO_SCALE_AUTO(0),
        VIDEO_SCALE_ZOOM(1),
        VIDEO_SCALE_TENSILE(2),
    }

    enum class VideoDecoderType(type: Int) {
        HARD_TYPE(0),
        SOFT_TYPE(1)
    }
}