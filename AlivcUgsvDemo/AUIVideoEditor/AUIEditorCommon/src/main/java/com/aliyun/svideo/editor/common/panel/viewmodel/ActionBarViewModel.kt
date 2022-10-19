package com.aliyun.svideo.editor.common.panel.viewmodel

import android.view.View
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt

class ActionBarViewModel(cancelVisible: Boolean, confirmVisible: Boolean, titleResId: Int) :
    BaseViewModel() {
    var cancelVisible : ObservableBoolean
    var confirmVisible : ObservableBoolean
    var titleResId : ObservableInt

    init {
        this.confirmVisible = ObservableBoolean(confirmVisible)
        this.cancelVisible = ObservableBoolean(cancelVisible)
        this.titleResId = ObservableInt(titleResId)
    }

    fun onCancelClick(view: View?) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL)
    }

    fun onConfirmClick(view: View?) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CONFIRM)
    }
}