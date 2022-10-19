package com.aliyun.svideo.editor.sticker.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter
import com.aliyun.svideo.editor.sticker.model.StickerResource

class StickerResourceItemViewModel() : ViewModel()  {
    var resource = MutableLiveData<StickerResource>()
    var position : Int = 0
    var checked = resource.map {
        it.isChecked
    }
    private var onItemClickListener: RecyclerAdapter.OnItemClickListener<StickerResource>? = null

    open fun setOnItemClickListener(onItemClickListener: RecyclerAdapter.OnItemClickListener<StickerResource>) {
        this.onItemClickListener = onItemClickListener
    }

    fun bind(resource: StickerResource, position : Int) {
        this.resource.value = resource
        this.position = position
    }

    fun onClick(view : View?) {
        this.onItemClickListener?.onItemClick(resource.value!!,position)
    }

}