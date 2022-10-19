package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.alivcplayerexpand.databinding.VideoResolutionBinding

class VideoResolutionView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding = VideoResolutionBinding.inflate(LayoutInflater.from(context), this, true)
}