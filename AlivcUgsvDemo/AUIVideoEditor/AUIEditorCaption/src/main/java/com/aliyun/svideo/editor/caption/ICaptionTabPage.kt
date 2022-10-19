package com.aliyun.svideo.editor.caption

import android.view.View
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel

interface ICaptionTabPage {

    fun onTabInit()
    fun onTabUnInit()

    fun onTabSelected()

    fun getTitle() : String
    fun getView() : View
    fun onBind(captionEditVM: CaptionEditViewModel)
}