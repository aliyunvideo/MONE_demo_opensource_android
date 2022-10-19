package com.aliyun.svideo.editor.common.util

import com.aliyun.svideosdk.editor.AliyunIEditor
import com.aliyun.svideosdk.editor.TimeEffectType


class SpeedUtil {
    companion object {

        fun getSpeed(editor : AliyunIEditor) : Float{
            for (item in editor.editorProject.allTimeFilters) {
                if (TimeEffectType.TIME_EFFECT_TYPE_RATE.ordinal == item.timeFilterType) {
                    return item.param
                }
            }
            return 1.0f
        }
    }
}