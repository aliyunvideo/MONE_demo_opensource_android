package com.aliyun.svideo.editor.sticker.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideosdk.editor.AliyunPasterController

class StickerTrackItemViewModel() : BaseViewModel()  {
    var sticker = MutableLiveData<AliyunPasterController>()
    var onItemClickListener : RecyclerAdapter.OnItemClickListener<AliyunPasterController>? = null
    var position : Int = 0

    fun bind(caption: AliyunPasterController, position : Int, listener : RecyclerAdapter.OnItemClickListener<AliyunPasterController>?) {
        this.sticker.value = caption
        this.position = position
        this.onItemClickListener = listener
    }

    fun onBubbleClick(view : View?) {
        this.onItemClickListener?.onItemClick(sticker.value!!,position)
    }

}