package com.aliyun.svideo.editor.sticker.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.editor.sticker.model.StickerBorderState
import com.aliyun.svideo.editor.sticker.model.StickerResource
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AliyunPasterController
import java.util.concurrent.TimeUnit

class StickerEditViewModel() : BaseViewModel() {
    val currentSticker = MutableLiveData<AliyunPasterController?>()

    val currentStickerBorderState = MutableLiveData<StickerBorderState?>()
    private var isFullScreen : Boolean = false

    private lateinit var aliyunEditor: AliyunIEditor
    private val captionChangeListeners = mutableListOf<OnStickerChangeListener>()

    fun init(editor : AliyunIEditor) {
        this.aliyunEditor = editor
    }

    fun bind(sticker : AliyunPasterController?) {
        this.currentSticker.value = sticker
        captionChangeListeners.forEach { listener-> listener.onCurrentStickerChange(sticker) }
        refreshBorderState()
    }

    fun unBind() {
        onCleared()
        this.captionChangeListeners.clear()
        if(this.currentStickerBorderState.value != null) {
            hideStickerBorder()
        }
        this.currentSticker.value = null
        this.currentStickerBorderState.value = null

    }

    fun onCloseClick(view : View?) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CLOSE, null)
        bind(null)
    }

    fun onDeleteStickerClick(view: View) {
        this.currentSticker.value?.let { sticker ->
            this.onDeleteSticker(sticker)
            mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_DELETE, sticker)
        }

    }

    fun onDeleteSticker(sticker : AliyunPasterController) {
        captionChangeListeners.forEach { listener-> listener.onStickerDeleted(sticker) }
        this.aliyunEditor.pasterManager.remove(sticker)
        this.currentSticker.value = null
        refreshBorderState(false)

    }

    private fun onAddStickerClick(sticker: StickerResource) {

        val defaultDuration = 3000L
        val totalDuration: Long = this.aliyunEditor.playerController.streamDuration
        val currentPlayPosition: Long =
            this.aliyunEditor.playerController.currentStreamPosition
        val rqDuration = currentPlayPosition + defaultDuration
        //判断是否超出时间线
        var stickerDuration = 0L
        stickerDuration = if (rqDuration > totalDuration) {
            totalDuration - currentPlayPosition
        } else {
            defaultDuration
        }


        val stickerController = this.aliyunEditor.pasterManager.addPasterWithStartTime(sticker.source, this.aliyunEditor.playerController.currentStreamPosition, stickerDuration, TimeUnit.MILLISECONDS)
        this.currentSticker.value = stickerController

        refreshBorderState(false, true)
        captionChangeListeners.forEach { it.onStickerAdded(stickerController) }

    }

    private fun onEditStickerClick(sticker : StickerResource) {
        val tempController = this.aliyunEditor.pasterManager.addPasterWithStartTime(sticker.source, 0, 0)
        this.currentSticker.value?.let {
            it.effect = tempController.effect
        }
        tempController.removePaster()
        refreshBorderState(false)
    }

    fun onSelectSticker(sticker : StickerResource) {

        if(this.currentSticker.value != null) {
            onEditStickerClick(sticker)
        } else {
            onAddStickerClick(sticker)
        }
        mOnItemClickListener?.onItemClick(null,PanelItemId.ITEM_ID_SELECT, this.currentSticker.value)
    }

    fun onSelectSticker(sticker: AliyunPasterController) {
        //如果当前选中一个sticker，后面切换到另一个sticker，则先隐藏，再展示
        if(this.currentSticker.value != null && this.currentSticker.value != sticker) {
            hideStickerBorder()
            bind(sticker)
        } else {
            //否则直接展示
            bind(sticker)
        }
    }

    fun onUnSelectSticker(sticker: AliyunPasterController) {
        if(sticker == this.currentSticker.value) {
            this.currentSticker.value = null
            hideStickerBorder()
        }
    }

    private fun refreshBorderState(update: Boolean = true, newAdd : Boolean = false) {
        if(this.currentSticker.value != null) {
            this.currentStickerBorderState.value = StickerBorderState(true, this.currentSticker.value, update, newAdd)
        } else {
            this.currentStickerBorderState.value = StickerBorderState(false, this.currentSticker.value, update, newAdd)
        }
    }

    private fun hideStickerBorder() {
        this.currentStickerBorderState.value = StickerBorderState(false, this.currentSticker.value, true)
    }

    fun onPlayerTimeChange(playerTime: Long) {
        if(isFullScreen) {
            return
        }

        checkPlayerTimeVisible(playerTime)
    }

    private fun checkPlayerTimeVisible(playerTime: Long) {
        this.currentSticker.value?.apply{
            val visible = playerTime >= startTime && playerTime <= (startTime + duration)
            currentStickerBorderState.value?.let {
                if(it.visible != visible) {
                    currentStickerBorderState.value = StickerBorderState(visible, this, !visible)
                }
            }
        }
    }

    fun onFullScreenChange(fullscreen : Boolean) {
        isFullScreen = fullscreen
        if(fullscreen) {
            hideStickerBorder()
        } else {
            checkPlayerTimeVisible(TimeUnit.MILLISECONDS.toMicros(aliyunEditor.playerController.currentStreamPosition))
        }
    }

    fun addOnStickerChangeListener(listener: OnStickerChangeListener) {
        this.captionChangeListeners.add(listener)
    }

    fun removeOnStickerChangeListener(listener: OnStickerChangeListener) {
        this.captionChangeListeners.remove(listener)
    }

    interface OnStickerChangeListener {
        fun onCurrentStickerChange(sticker: AliyunPasterController?)
        fun onStickerAdded(sticker: AliyunPasterController)
        fun onStickerDeleted(sticker: AliyunPasterController)
    }
}