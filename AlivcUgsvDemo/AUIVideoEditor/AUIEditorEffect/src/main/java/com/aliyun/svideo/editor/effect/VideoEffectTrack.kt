package com.aliyun.svideo.editor.effect

import com.aliyun.svideosdk.common.struct.effect.TrackEffectFilter


class VideoEffectTrack(filter: TrackEffectFilter, name: String) {
    val mTrackEffectFilter = filter
    val mName = name
}