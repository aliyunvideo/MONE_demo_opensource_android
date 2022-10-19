package com.aliyun.svideo.editor.caption.viewmodel

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import com.aliyun.svideo.editor.caption.CaptionConfig
import com.aliyun.svideo.editor.caption.CaptionManager
import com.aliyun.svideo.editor.caption.R
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.caption.model.CaptionTrack
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption

class CaptionEntryViewModel(var captionEditViewModel : CaptionEditViewModel) : BaseViewModel(),
    MultiTrackListener, CaptionEditViewModel.OnCaptionChangeListener {
    var currentCaptionValid = captionEditViewModel.currentCaption.map {
        it != null
    }
    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = false,
        confirmVisible = false,
        titleResId = R.string.ugsv_add_caption
    )

    val captionTrackViewModel = CaptionEntryMultiTrackViewModel()
    val captionList : MutableList<AliyunPasterControllerCompoundCaption>
        get() = CaptionManager.getAllCaption(this.aliyunEditor.pasterManager)


    private lateinit var aliyunEditor: AliyunIEditor
    private var onCaptionChangeListener : CaptionEditViewModel.OnCaptionChangeListener? = null

    fun bind(lifecycleOwner: LifecycleOwner, editor : AliyunIEditor) {
        this.aliyunEditor = editor
        this.captionEditViewModel.init(editor)
        this.captionTrackViewModel.bind(editor)
        this.captionEditViewModel.addOnCaptionChangeListener(this)
    }

    fun unBind() {
        onCleared()
        this.captionEditViewModel.unBind()
        this.captionTrackViewModel.unBind()
    }

    override fun onCleared() {
        super.onCleared()
        this.onCaptionChangeListener = null
        this.captionEditViewModel.removeOnCaptionChangeListener(this)
        captionEditViewModel.unBind()
        captionTrackViewModel.unBind()
    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        super.setOnItemClickListener(onItemClickListener)
        actionBarViewModel.setOnItemClickListener(onItemClickListener)
    }

    fun onAddCaptionClick(view: View) {
        val startTime = this.aliyunEditor.playerController.currentStreamPosition
        val duration = CaptionManager.captionDurationBoundJudge(this.aliyunEditor, CaptionConfig.DEFAULT_DURATION)
        val newCaption = CaptionManager.addCaptionWithStartTime(
            view.context,
            this.aliyunEditor.pasterManager,
            null,
            null,
            startTime,
            duration
        )
        newCaption?.let {
            onCaptionChangeListener?.onCaptionAdded(it)
            captionEditViewModel.bind(it)
            mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_ADD, it)
        }

    }

    fun onDeleteCaptionClick(view : View) {
        captionEditViewModel.currentCaption.value?.let { caption ->
            captionEditViewModel.onDeleteCaption(caption)
            mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_DELETE, caption)
        }
    }


    private fun onSelectCaption(caption : AliyunPasterControllerCompoundCaption) {
        captionEditViewModel.bind(caption)
    }

    fun setOnCaptionChangeListener(listener: CaptionEditViewModel.OnCaptionChangeListener) {
        this.onCaptionChangeListener = listener
    }

    override fun onClipClick(clipInfo: BaseClipInfo) {

    }

    override fun onFocusChanged(clipInfo: BaseClipInfo, isFocus: Boolean) {
        val captionTrack = clipInfo as CaptionTrack
        if(isFocus){
            onSelectCaption(captionTrack.captionController)
        } else {
            if(captionTrack.captionController == this.captionEditViewModel.currentCaption.value) {
                this.captionEditViewModel.bind(null)
            }
        }
        this.captionTrackViewModel.onFocusChanged(clipInfo, isFocus)
    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo) {
        this.captionTrackViewModel.onUpdateClipTime(clipInfo)
    }

    override fun onClipTouchPosition(time: Long) {
        this.captionTrackViewModel.onClipTouchPosition(time)
    }


    override fun onScrollChangedTime(time: Long) {
        this.captionTrackViewModel.onScrollChangedTime(time)
    }

    override fun onCurrentCaptionChange(caption: AliyunPasterControllerCompoundCaption?) {
        onCaptionChangeListener?.onCurrentCaptionChange(caption)
    }

    override fun onCaptionAdded(caption: AliyunPasterControllerCompoundCaption) {
        onCaptionChangeListener?.onCaptionAdded(caption)
    }

    override fun onCaptionDeleted(caption: AliyunPasterControllerCompoundCaption) {
        onCaptionChangeListener?.onCaptionDeleted(caption)
    }

    override fun onTextChange(caption: AliyunPasterControllerCompoundCaption, text: String) {
        onCaptionChangeListener?.onTextChange(caption, text)
    }

    override fun onBubbleChange(
        caption: AliyunPasterControllerCompoundCaption,
        resource: CaptionResource
    ) {
        onCaptionChangeListener?.onBubbleChange(caption, resource)
    }

}