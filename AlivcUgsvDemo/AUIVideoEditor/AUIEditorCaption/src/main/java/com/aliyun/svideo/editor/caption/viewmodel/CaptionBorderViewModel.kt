package com.aliyun.svideo.editor.caption.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.caption.CaptionManager
import com.aliyun.svideo.editor.caption.model.CaptionBorderState
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption
import java.util.concurrent.TimeUnit

class CaptionBorderViewModel(val captionEditViewModel: CaptionEditViewModel, val aliyunEditor : AliyunIEditor) : BaseViewModel(), CaptionEditViewModel.OnCaptionChangeListener {
    val currentCaptionBorderState = MutableLiveData<CaptionBorderState?>()
    var isFullScreen = false


    fun bind() {
        this.captionEditViewModel.addOnCaptionChangeListener(this)
    }

    fun unbind() {
        super.onCleared()
        hideCaptionBorder()
        this.captionEditViewModel.removeOnCaptionChangeListener(this)
    }

    fun onDeleteClick(view: View?) {
        this.captionEditViewModel.currentCaption.value?.let { caption ->
            this.captionEditViewModel.onDeleteCaption(caption)
            mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_DELETE, caption)
        }
    }

    fun onEditClick(view: View?) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_EDIT, this.captionEditViewModel.currentCaption.value!!)
    }


    private fun refreshCaptionBorder() {
        val curCaption = this.captionEditViewModel.currentCaption.value
        if(curCaption != null) {
            this.currentCaptionBorderState.postValue(CaptionBorderState(true, curCaption.hashCode(), CaptionManager.getCaptionSize(
                curCaption
            )))
        } else {
            this.currentCaptionBorderState.postValue(CaptionBorderState(false, 0, null))
        }
    }

    private fun hideCaptionBorder() {
        this.currentCaptionBorderState.postValue(CaptionBorderState(false, 0, null))
    }

    fun onPlayerTimeChange(playerTime : Long) {
        if(isFullScreen) {
            return
        }

        checkPlayerTimeVisible(playerTime)
    }

    private fun checkPlayerTimeVisible(playerTime: Long) {
        this.captionEditViewModel.currentCaption.value?.apply{
            val visible = playerTime >= startTime && playerTime <= (startTime + duration)
            currentCaptionBorderState.value?.let {
                if(it.visible != visible) {
                    currentCaptionBorderState.postValue(CaptionBorderState(visible, this.hashCode(), CaptionManager.getCaptionSize(
                        this
                    )))
                }
            }
        }
    }

    override fun onCurrentCaptionChange(caption: AliyunPasterControllerCompoundCaption?) {
        refreshCaptionBorder()
    }

    override fun onCaptionAdded(caption: AliyunPasterControllerCompoundCaption) {
    }

    override fun onCaptionDeleted(caption: AliyunPasterControllerCompoundCaption) {
        refreshCaptionBorder()
    }

    override fun onTextChange(caption: AliyunPasterControllerCompoundCaption, text: String) {
        refreshCaptionBorder()
    }

    override fun onBubbleChange(
        caption: AliyunPasterControllerCompoundCaption,
        resource: CaptionResource
    ) {
        refreshCaptionBorder()
    }

    fun onFullScreenChange(fullscreen : Boolean) {
        isFullScreen = fullscreen
        if(fullscreen) {
            hideCaptionBorder()
        } else {
            checkPlayerTimeVisible(TimeUnit.MILLISECONDS.toMicros(aliyunEditor.playerController.currentStreamPosition))
        }
    }
}