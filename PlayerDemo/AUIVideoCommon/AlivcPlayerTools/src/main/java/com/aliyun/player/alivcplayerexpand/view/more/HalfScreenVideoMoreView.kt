package com.aliyun.player.alivcplayerexpand.view.more

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.aliyun.player.alivcplayerexpand.databinding.LayoutHalfScreenVideoMoreBinding

class HalfScreenVideoMoreView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val mViewBinding =
        LayoutHalfScreenVideoMoreBinding.inflate(LayoutInflater.from(context), this, true)
    var onHalfMoreViewClick: OnHalfMoreViewClick? = null

    init {
        mViewBinding.apply {
            videoMoreVideoSpeed.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED)
            }
            videoMoreVideoSpeedTv.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.VIDEO_SPEED)
            }
            videoMoreVideoDefinition.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION)
            }
            videoMoreVideoDefinitionTv.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.VIDEO_DEFINITION)
            }
            videoMoreVideoDamkun.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.DAMKUN_LOCATION)
            }
            videoMoreVideoDamkunTv.setOnClickListener {
                onHalfMoreViewClick?.onItemClick(HalfViewVideoSettingPage.VideoSettingType.DAMKUN_LOCATION)
            }
            videoMoreCancelTv.setOnClickListener {
                onHalfMoreViewClick?.cancelDialog()
            }
        }
    }

    fun setUpData(listPlayOpen: Boolean, backGroundPlayOpen: Boolean) {
        mViewBinding.apply {
            videoMoreListPlaySettingBtn.isChecked = listPlayOpen
            videoMoreBackgroundPlaySettingBtn.isChecked = backGroundPlayOpen

            videoMoreListPlaySettingBtn.setOnCheckedChangeListener { _, isChecked ->
                onHalfMoreViewClick?.onItemOnOff(
                    isChecked,
                    videoMoreBackgroundPlaySettingBtn.isChecked
                )
            }
            videoMoreBackgroundPlaySettingBtn.setOnCheckedChangeListener { _, isChecked ->
                onHalfMoreViewClick?.onItemOnOff(videoMoreListPlaySettingBtn.isChecked, isChecked)
            }
        }
    }

    interface OnHalfMoreViewClick {
        fun onItemClick(type: HalfViewVideoSettingPage.VideoSettingType)
        fun onItemOnOff(listPlayOpen: Boolean, backGroundPlayOpen: Boolean)
        fun cancelDialog()
    }
}