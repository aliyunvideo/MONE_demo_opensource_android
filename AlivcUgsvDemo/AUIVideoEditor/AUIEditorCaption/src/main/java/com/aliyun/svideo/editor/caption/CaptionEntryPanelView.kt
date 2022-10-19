package com.aliyun.svideo.editor.caption

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.caption.databinding.CaptionEntryPanelBinding
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionTrack
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEntryViewModel
import com.aliyun.svideo.editor.common.panel.BasePanelView
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption


open class CaptionEntryPanelView(context: Context) : BasePanelView<CaptionEntryPanelBinding>(context) {
    private lateinit var captionEntryVM : CaptionEntryViewModel
    private var speed = 1.0f

    override val layoutId: Int
        get() = R.layout.caption_entry_panel

    fun setViewModel(captionEntryVM: CaptionEntryViewModel) {
        this.captionEntryVM = captionEntryVM
        mDataBinding.viewModel = captionEntryVM
        val lifeOwner = context as LifecycleOwner
        mDataBinding.lifecycleOwner = lifeOwner

        this.captionEntryVM.captionList.forEach {
            mDataBinding.multiTrackContainer.addSubClip(CaptionTrack(it))
        }

        mDataBinding.multiTrackContainer.setTrackListener(captionEntryVM)

        this.captionEntryVM.captionTrackViewModel.trackList.observe(lifeOwner) {
            this.mDataBinding.multiTrackContainer.setVideoData(it)
        }

        this.captionEntryVM.setOnCaptionChangeListener(object : CaptionEditViewModel.OnCaptionChangeListener {
            override fun onCurrentCaptionChange(caption: AliyunPasterControllerCompoundCaption?) {
                caption?.let {
                    mDataBinding.multiTrackContainer.setSubClipFocus(it.hashCode())
                }
            }

            override fun onCaptionAdded(caption: AliyunPasterControllerCompoundCaption) {
                val captionTrack = CaptionTrack(caption)
                mDataBinding.multiTrackContainer.addSubClip(captionTrack)
                mDataBinding.multiTrackContainer.scrollToSubClip(captionTrack.clipId)
            }

            override fun onCaptionDeleted(caption: AliyunPasterControllerCompoundCaption) {
                mDataBinding.multiTrackContainer.removeSubClip(caption.hashCode())
            }

            override fun onTextChange(
                caption: AliyunPasterControllerCompoundCaption,
                text: String
            ) {
                mDataBinding.multiTrackContainer.setSubClipText(caption.hashCode(), caption.text)
            }

            override fun onBubbleChange(
                caption: AliyunPasterControllerCompoundCaption,
                resource: CaptionResource
            ) {
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