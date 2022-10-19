package com.aliyun.svideo.editor.effect


interface VideoEffectClickListener {
    fun onEffectSelected(effectPath: String, effectName:String)
    fun onEffectDelete(track: VideoEffectTrack)
}