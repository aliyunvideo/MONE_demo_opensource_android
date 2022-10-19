package com.aliyun.svideo.editor.clip.enhance

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.databinding.ObservableInt
import com.aliyun.svideo.editor.clip.R
import com.aliyun.svideo.editor.clip.base.BaseClipViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseNotifyViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.common.struct.project.VideoTrackClip

class EnhancePanelViewModel(context: Context) : BaseClipViewModel() {

    object DEFAULT {
        val BRIGHTNESS = 0.5f
        val CONTRAST = 0.25F
        val SATURATION = 0.5F
        val VIGNETTE = 0.0F
        val SHARPNESS = 0.0F
    }

    private var mCurrentPanelItem = PanelItemId.ITEM_ID_BRIGHTNESS

    private var mProgress = 0
    var mBrightness = DEFAULT.BRIGHTNESS
    var mContrast = DEFAULT.CONTRAST
    var mSaturation = DEFAULT.SATURATION
    var mVignette = DEFAULT.VIGNETTE
    var mSharpness = DEFAULT.SHARPNESS
    private var mSeekbarEnable = true
    private var mIsCheckAll = false

    private var mOnProgressChangedListener:OnProgressChangedListener? = null

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = true,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_enhance_title
    )

    open fun setProgressChangedListener(onProgressChangedListener:OnProgressChangedListener) {
        mOnProgressChangedListener = onProgressChangedListener
    }


    fun onSpaceClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL, null)
    }

    fun onCheckAllClick(view: View) {
        var imageView:ImageView = view as ImageView
        mIsCheckAll = !mIsCheckAll
        if (mIsCheckAll) {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_check)
        } else {
            imageView.setImageResource(R.drawable.ugsv_editor_clip_uncheck)
        }
    }

    fun onResetClick(view: View) {
        onDataReset()
        mOnProgressChangedListener?.onResetClick(mIsCheckAll)
        mSeekbarEnable = false
        notifyChange()
    }

    fun onBrightnessClick(view: View) {
        mProgress = (mBrightness * 100).toInt()
        mCurrentPanelItem = PanelItemId.ITEM_ID_BRIGHTNESS
        mSeekbarEnable = true
        notifyChange()
    }

    fun onContractClick(view: View) {
        mProgress = (mContrast * 100).toInt()
        mCurrentPanelItem = PanelItemId.ITEM_ID_CONTRAST
        mSeekbarEnable = true
        notifyChange()
    }

    fun onSaturationClick(view: View) {
        mProgress = (mSaturation * 100).toInt()
        mCurrentPanelItem = PanelItemId.ITEM_ID_SATURATION
        mSeekbarEnable = true
        notifyChange()
    }

    fun onVignettingClick(view: View) {
        mProgress = (mVignette * 100).toInt()
        mCurrentPanelItem = PanelItemId.ITEM_ID_VIGNETTING
        mSeekbarEnable = true
        notifyChange()
    }

    fun onSharpnessClick(view: View) {
        mProgress = (mSharpness * 100).toInt()
        mCurrentPanelItem = PanelItemId.ITEM_ID_SHARPNESS
        mSeekbarEnable = true
        notifyChange()
    }

    fun getProgress(): Int{
        return mProgress
    }

    fun setProgress(progress:Int) {
        mProgress = progress
        when(mCurrentPanelItem) {
            PanelItemId.ITEM_ID_RESET -> return
            PanelItemId.ITEM_ID_BRIGHTNESS -> mBrightness = (mProgress / 100f)
            PanelItemId.ITEM_ID_CONTRAST -> mContrast = (mProgress / 100f)
            PanelItemId.ITEM_ID_SATURATION -> mSaturation = (mProgress / 100f)
            PanelItemId.ITEM_ID_VIGNETTING -> mVignette = (mProgress / 100f)
            PanelItemId.ITEM_ID_SHARPNESS -> mSharpness = (mProgress / 100f)
        }
        mOnProgressChangedListener?.onProgressChanged(mCurrentPanelItem, mProgress / 100f, mIsCheckAll)
    }

    fun getSeekBarEnable(): Boolean {
        return mSeekbarEnable
    }

    fun getIsSelected(itemIndex:Int): ObservableInt {
        var item = PanelItemId.ITEM_ID_RESET
        when (itemIndex){
            0 -> item = PanelItemId.ITEM_ID_RESET
            1 -> item = PanelItemId.ITEM_ID_BRIGHTNESS
            2 -> item = PanelItemId.ITEM_ID_CONTRAST
            3 -> item = PanelItemId.ITEM_ID_SATURATION
            4 -> item = PanelItemId.ITEM_ID_VIGNETTING
            5 -> item = PanelItemId.ITEM_ID_SHARPNESS
        }
        if (item == mCurrentPanelItem) return ObservableInt(View.VISIBLE)
        return ObservableInt(View.INVISIBLE)
    }

    fun onDataReset() {
        mBrightness = DEFAULT.BRIGHTNESS
        mContrast = DEFAULT.CONTRAST
        mSaturation = DEFAULT.SATURATION
        mVignette = DEFAULT.VIGNETTE
        mSharpness = DEFAULT.SHARPNESS

        when(mCurrentPanelItem) {
            PanelItemId.ITEM_ID_RESET -> return
            PanelItemId.ITEM_ID_BRIGHTNESS -> mProgress = (mBrightness * 100).toInt()
            PanelItemId.ITEM_ID_CONTRAST -> mProgress = (mContrast * 100).toInt()
            PanelItemId.ITEM_ID_SATURATION -> mProgress = (mSaturation * 100).toInt()
            PanelItemId.ITEM_ID_VIGNETTING -> mProgress = (mVignette * 100).toInt()
            PanelItemId.ITEM_ID_SHARPNESS -> mProgress = (mSharpness * 100).toInt()
        }
    }

    override fun onCurrentClipChanged(streamId: Int, trackClip: VideoTrackClip?) {
        if (trackClip == null) {
            return
        }
        mBrightness = trackClip.brightness
        mContrast = trackClip.contrast
        mSaturation = trackClip.saturation
        mVignette = trackClip.vignette
        mSharpness = trackClip.sharpness

        when(mCurrentPanelItem) {
            PanelItemId.ITEM_ID_RESET -> return
            PanelItemId.ITEM_ID_BRIGHTNESS -> mProgress = (mBrightness * 100).toInt()
            PanelItemId.ITEM_ID_CONTRAST -> mProgress = (mContrast * 100).toInt()
            PanelItemId.ITEM_ID_SATURATION -> mProgress = (mSaturation * 100).toInt()
            PanelItemId.ITEM_ID_VIGNETTING -> mProgress = (mVignette * 100).toInt()
            PanelItemId.ITEM_ID_SHARPNESS -> mProgress = (mSharpness * 100).toInt()
        }
        notifyChange()
    }

}