package com.aliyun.svideo.editor.publish

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aliyun.common.utils.StringUtils
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId

class PublishViewModel : BaseViewModel(){

    val maxTextCount = 50

    private var mContext:Context? = null

    var mDesText:MutableLiveData<String> = MutableLiveData("")
    var mDesTextCount:MutableLiveData<String> = MutableLiveData("0/$maxTextCount")

    fun onBind(context: Context) {
        mContext = context
    }

    override fun onCleared() {
        super.onCleared()
        mContext = null
    }

    fun onChooseCover(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_SELECT_COVER, null)
    }

    fun onPublish(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_PUBLISH ,null)
    }

    fun getDesText() : MutableLiveData<String>{
        var len = StringUtils.length(mDesText.value)
        mDesTextCount.value = "$len/$maxTextCount"
        return mDesText
    }

    fun getTextCount():MutableLiveData<String> {
        return mDesTextCount
    }


}