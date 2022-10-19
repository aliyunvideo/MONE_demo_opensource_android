package com.aliyun.svideo.editor.effect

class VideoEffectManager {

    val mAddEffectList : ArrayList<VideoEffectTrack> = ArrayList<VideoEffectTrack>()

    fun addVideoEffect(videoEffectTrack: VideoEffectTrack) {
        mAddEffectList.add(videoEffectTrack)
    }

    fun removeVideoEffect(videoEffectTrack: VideoEffectTrack) {
        mAddEffectList.remove(videoEffectTrack)
    }

    fun getEffectList(): MutableList<VideoEffectTrack>{
        return mAddEffectList.toMutableList()
    }

    fun clearAll() {
        mAddEffectList.clear();
    }
}