package com.aliyun.svideo.editor.caption.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.aliyun.svideo.editor.caption.model.CaptionResource
import com.aliyun.svideo.editor.common.widget.RecyclerAdapter

class CaptionResourceItemViewModel() : ViewModel()  {
    var resource = MutableLiveData<CaptionResource>()
    var position : Int = 0
    var checked = resource.map {
        it.isChecked
    }
    private var onItemClickListener: RecyclerAdapter.OnItemClickListener<CaptionResource>? = null

    open fun setOnItemClickListener(onItemClickListener: RecyclerAdapter.OnItemClickListener<CaptionResource>) {
        this.onItemClickListener = onItemClickListener
    }

    fun bind(resource: CaptionResource, position : Int) {
        this.resource.value = resource
        this.position = position
    }

    fun onClick(view : View?) {
        this.onItemClickListener?.onItemClick(resource.value!!,position)
    }

}