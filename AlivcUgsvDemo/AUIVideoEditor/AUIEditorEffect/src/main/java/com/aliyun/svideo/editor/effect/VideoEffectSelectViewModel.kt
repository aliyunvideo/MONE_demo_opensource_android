package com.aliyun.svideo.editor.effect

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.bean.ClipType
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.editor.AliyunIEditor

class VideoEffectSelectViewModel() : BaseViewModel() {

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = false,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_effect_title
    )
    val mMultiTrackViewModel = VideoEffectEntryMultiTrackViewModel()

    var currentEffectValid:Boolean = false

    private lateinit var mEditor: AliyunIEditor

    fun onSpaceClick(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_CANCEL, null)
    }

    fun bind(lifecycleOwner: LifecycleOwner, editor : AliyunIEditor) {
        this.mEditor = editor
        this.mMultiTrackViewModel.bind(editor)
    }

    fun unBind() {
        onCleared()
        this.mMultiTrackViewModel.unBind()
    }


    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        super.setOnItemClickListener(onItemClickListener)
        actionBarViewModel.setOnItemClickListener(onItemClickListener)
    }

}