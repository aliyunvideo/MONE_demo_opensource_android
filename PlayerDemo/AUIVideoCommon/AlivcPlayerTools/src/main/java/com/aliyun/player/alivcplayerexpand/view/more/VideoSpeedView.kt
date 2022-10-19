package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.alivcplayerexpand.R
import com.aliyun.player.alivcplayerexpand.databinding.VideoSpeedBinding

class VideoSpeedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, val currentSpeed: Float
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding = VideoSpeedBinding.inflate(LayoutInflater.from(context), this, true)
    var mOnVideoSpeedSelected: OnVideoSpeedSelected? = null

    init {
        mViewBinding.apply {
            videoSpeedHalf.isChecked = currentSpeed <= 0.5f
            videoSpeedThreeFourths.isChecked = currentSpeed <= 0.75f && currentSpeed > 0.5f
            videoSpeedOriginal.isChecked = currentSpeed <= 1f && currentSpeed > 0.75f
            videoSpeedPointTwoFive.isChecked = currentSpeed <= 1.25f && currentSpeed > 1f
            videoSpeedPointFive.isChecked = currentSpeed <= 1.5f && currentSpeed > 1.25f
            videoSpeedTwo.isChecked = currentSpeed >= 2f
        }

        mViewBinding.videoSpeed.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.video_speed_half -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(0.5f)
                }
                R.id.video_speed_three_fourths -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(0.75f)
                }
                R.id.video_speed_original -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(1f)
                }
                R.id.video_speed_point_two_five -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(1.25f)
                }

                R.id.video_speed_point_five -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(1.5f)
                }
                R.id.video_speed_two -> {
                    mOnVideoSpeedSelected?.onVideoSpeedSelected(2.0f)
                }
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    interface OnVideoSpeedSelected {
        fun onVideoSpeedSelected(speed: Float)
    }
}