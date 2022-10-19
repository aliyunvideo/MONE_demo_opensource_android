package com.aliyun.svideo.editor.filter

import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.aliyun.svideo.editor.common.panel.OnItemClickListener
import com.aliyun.svideo.editor.common.panel.viewmodel.ActionBarViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId
import com.aliyun.svideo.editor.effect.R
import com.aliyun.svideo.track.bean.BaseClipInfo
import com.aliyun.svideo.track.bean.ClipType
import com.aliyun.svideo.track.inc.MultiTrackListener
import com.aliyun.svideosdk.common.struct.effect.TrackEffectFilter
import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.impl.AliyunPasterControllerCompoundCaption

class VideoFilterEntryViewModel() : BaseViewModel() {

    val actionBarViewModel = ActionBarViewModel(
        cancelVisible = false,
        confirmVisible = false,
        titleResId = R.string.ugsv_editor_filter_title
    )

    private lateinit var mEditor: AliyunIEditor

    fun bind(lifecycleOwner: LifecycleOwner, editor : AliyunIEditor) {
        this.mEditor = editor
    }

    fun unBind() {
        onCleared()
    }

    fun addVideoEffect(view: View) {
        mOnItemClickListener?.onItemClick(view, PanelItemId.ITEM_ID_ADD)
    }

    fun deleteVideoEffect(view: View) {

    }

    override fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        super.setOnItemClickListener(onItemClickListener)
        actionBarViewModel.setOnItemClickListener(onItemClickListener)
    }

}