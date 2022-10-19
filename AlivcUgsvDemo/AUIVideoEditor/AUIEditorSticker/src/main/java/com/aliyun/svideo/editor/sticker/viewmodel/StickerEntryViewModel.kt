package com.aliyun.svideo.editor.sticker.viewmodel

import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.map
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.editor.sticker.R
import com.aliyun.svideo.editor.sticker.model.StickerTrack
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.common.struct.effect.EffectPaster
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.AliyunPasterController

class StickerEntryViewModel(val stickerEditViewModel: StickerEditViewModel) : BaseViewModel(),
    MultiTrackListener, StickerEditViewModel.OnStickerChangeListener {
    var currentStickerValid = stickerEditViewModel.currentSticker.map {
        it != null
    }
    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = false,
        confirmVisible = false,
        titleResId = R.string.ugsv_add_sticker
    )

    val stickerTrackViewModel = StickerEntryMultiTrackViewModel()
    val stickerList: MutableList<AliyunPasterController>
        get() {
            return this.aliyunEditor.pasterManager.findControllersByType(EffectPaster.PASTER_TYPE_GIF)
                .map { it as AliyunPasterController }.reversed().toMutableList()
        }
    private lateinit var aliyunEditor: AliyunIEditor
    private var onStickerChangeListener: StickerEditViewModel.OnStickerChangeListener? = null

    fun bind(lifecycleOwner: LifecycleOwner, editor: AliyunIEditor) {
        this.aliyunEditor = editor
        this.stickerEditViewModel.init(editor)
        this.stickerTrackViewModel.bind(this.aliyunEditor)
        this.stickerEditViewModel.addOnStickerChangeListener(this)


    }

    fun unBind() {
        onCleared()
        stickerTrackViewModel.unBind()
        stickerEditViewModel.removeOnStickerChangeListener(this)
        stickerEditViewModel.unBind()
        mOnItemClickListener = null
    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        super.setOnItemClickListener(onItemClickListener)
        actionBarViewModel.setOnItemClickListener(onItemClickListener)
    }

    fun onAddStickerClick(view: View) {
        stickerEditViewModel.bind(null)
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_ADD, null)
    }

    fun onDeleteStickerClick(view: View) {

        stickerEditViewModel.currentSticker.value?.let { sticker ->
            stickerEditViewModel.onDeleteSticker(sticker)
            mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_DELETE, sticker)
        }

    }

    override fun onClipClick(clipInfo: BaseClipInfo) {
        this.stickerTrackViewModel.onClipClick(clipInfo)
    }

    fun setOnStickerChangeListener(listener: StickerEditViewModel.OnStickerChangeListener) {
        this.onStickerChangeListener = listener
    }

    override fun onFocusChanged(clipInfo: BaseClipInfo, isFocus: Boolean) {
        this.stickerTrackViewModel.onFocusChanged(clipInfo, isFocus)
        val stickerTrack = clipInfo as StickerTrack
        if(isFocus) {
            stickerEditViewModel.onSelectSticker(stickerTrack.controller)
            mOnItemClickListener?.onItemClick(null, PanelItemId.ITEM_ID_SELECT, stickerTrack.controller)
        } else {
            stickerEditViewModel.onUnSelectSticker(stickerTrack.controller)
        }

    }

    override fun onUpdateClipTime(clipInfo: BaseClipInfo) {
        this.stickerTrackViewModel.onUpdateClipTime(clipInfo)
    }

    override fun onClipTouchPosition(time: Long) {
        this.stickerTrackViewModel.onClipTouchPosition(time)
    }

    override fun onScrollChangedTime(time: Long) {
        this.stickerTrackViewModel.onScrollChangedTime(time)
    }

    override fun onCurrentStickerChange(sticker: AliyunPasterController?) {
        this.onStickerChangeListener?.onCurrentStickerChange(sticker)
    }

    override fun onStickerAdded(sticker: AliyunPasterController) {
        this.onStickerChangeListener?.onStickerAdded(sticker)
    }

    override fun onStickerDeleted(sticker: AliyunPasterController) {
        this.onStickerChangeListener?.onStickerDeleted(sticker)
    }


}