package com.aliyun.svideo.editor.sticker

import android.content.Context
import android.graphics.BitmapFactory
import androidx.lifecycle.LifecycleOwner
import com.aliyun.common.utils.BitmapUtil
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideo.editor.sticker.databinding.StickerEntryPanelBinding
import com.aliyun.svideo.editor.sticker.model.StickerTrack
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEditViewModel
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEntryViewModel
import com.aliyun.svideosdk.editor.AliyunPasterController


open class StickerEntryPanelView(context: Context) : BasePanelView<StickerEntryPanelBinding>(context) {
    private lateinit var stickerEntryVM : StickerEntryViewModel
    private var speed = 1.0f

    override val layoutId: Int
        get() = R.layout.sticker_entry_panel

    fun setViewModel(captionEntryVM: StickerEntryViewModel) {
        this.stickerEntryVM = captionEntryVM
        mDataBinding.viewModel = captionEntryVM
        val lifeOwner = context as LifecycleOwner
        mDataBinding.lifecycleOwner = lifeOwner

        this.stickerEntryVM.stickerList.forEach {
            val stickerTrack = StickerTrack(it)
            mDataBinding.multiTrackContainer.addSubClip(stickerTrack)
            val bitmap = BitmapUtil.safeDecodeFile(stickerTrack.path, BitmapFactory.Options())
            bitmap?.let { bitmap ->
                mDataBinding.multiTrackContainer.setSubClipMark(stickerTrack.clipId, bitmap)
            }
        }

        mDataBinding.multiTrackContainer.setTrackListener(stickerEntryVM)

        this.stickerEntryVM.stickerTrackViewModel.trackList.observe(lifeOwner) {
            this.mDataBinding.multiTrackContainer.setVideoData(it)
        }

        this.stickerEntryVM.setOnStickerChangeListener(object : StickerEditViewModel.OnStickerChangeListener{
            override fun onCurrentStickerChange(sticker: AliyunPasterController?) {
                if(sticker != null) {
                    mDataBinding.multiTrackContainer.setSubClipFocus(sticker.hashCode())
                } else {
                    mDataBinding.multiTrackContainer.setSubClipFocus(-1)
                }
            }

            override fun onStickerAdded(sticker: AliyunPasterController) {
                val stickerTrack = StickerTrack(sticker)
                mDataBinding.multiTrackContainer.addSubClip(stickerTrack)
                mDataBinding.multiTrackContainer.scrollToSubClip(stickerTrack.clipId)
                val bitmap = BitmapUtil.safeDecodeFile(stickerTrack.path, BitmapFactory.Options())
                bitmap?.let { bitmap->
                    mDataBinding.multiTrackContainer.setSubClipMark(stickerTrack.clipId, bitmap)
                }

            }

            override fun onStickerDeleted(sticker: AliyunPasterController) {
                mDataBinding.multiTrackContainer.removeSubClip(sticker.hashCode())
            }

        })

    }

    fun updatePlayProgress(currentPlayTime : Long) {
        this.mDataBinding.multiTrackContainer.updatePlayProgress((currentPlayTime * this.speed / 1000).toLong())
    }

    fun updateSpeed(speed: Float) {
        this.speed = speed
    }

}