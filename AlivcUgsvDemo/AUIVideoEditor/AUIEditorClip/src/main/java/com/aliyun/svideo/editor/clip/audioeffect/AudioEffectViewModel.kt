package com.aliyun.svideo.editor.clip.audioeffect

import com.aliyun.svideo.editor.common.panel.viewmodel.BaseViewModel
import com.aliyun.svideosdk.editor.AudioEffectType

class AudioEffectViewModel(type: AudioEffectType, imageResId: Int,
                           textResId: Int, select: Boolean = false) : BaseViewModel(){
    var mEffectType = type
    var mImageResId = imageResId
    var mTextResId = textResId
    var mSelect = select
}