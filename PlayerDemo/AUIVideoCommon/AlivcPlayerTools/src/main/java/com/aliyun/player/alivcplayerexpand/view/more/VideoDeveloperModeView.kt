package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.alivcplayerexpand.databinding.LayoutVideoDeveloperModeMoreItemBinding

class VideoDeveloperModeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    val moreValue: AliyunShowMoreValue
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding =
        LayoutVideoDeveloperModeMoreItemBinding.inflate(LayoutInflater.from(context), this, true)
    init {
    }

    fun setUpData(fps: Float) {
        mViewBinding.apply {
            tvVideoFps.text = "$fps"
        }
    }
}