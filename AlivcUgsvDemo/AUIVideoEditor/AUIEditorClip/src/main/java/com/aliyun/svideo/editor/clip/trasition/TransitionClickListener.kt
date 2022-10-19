package com.aliyun.svideo.editor.clip.trasition

import com.aliyun.svideosdk.common.struct.effect.TransitionBase


interface TransitionClickListener {
    fun onEffectSelected(effectType: TransitionBase?)
}