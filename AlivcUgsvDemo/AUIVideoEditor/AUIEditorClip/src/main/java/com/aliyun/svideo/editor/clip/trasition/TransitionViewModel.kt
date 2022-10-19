package com.aliyun.svideo.editor.clip.trasition

import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideosdk.common.struct.effect.TransitionBase

class TransitionViewModel(transition: TransitionBase?, imageResId: Int,
                          textResId: Int, select: Boolean = false) : BaseViewModel(){
    var mEffectType = transition
    var mImageResId = imageResId
    var mTextResId = textResId
    var mSelect = select
}