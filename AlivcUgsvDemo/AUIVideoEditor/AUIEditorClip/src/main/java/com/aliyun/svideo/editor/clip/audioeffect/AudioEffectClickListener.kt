package com.aliyun.svideo.editor.clip.audioeffect

import com.aliyun.svideosdk.editor.AudioEffectType


interface AudioEffectClickListener {
    fun onEffectSelected(effectType: AudioEffectType)
}