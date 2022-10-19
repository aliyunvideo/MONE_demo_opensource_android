package com.aliyun.svideo.editor.caption.model

import android.graphics.RectF

data class CaptionBorder(var rectF: RectF,var scale: Float,var rotation: Float) {
    override fun toString(): String {
        return "AlivcCaptionBorderBean{" +
                "rectF=" + rectF +
                ", scale=" + scale +
                ", roation=" + rotation +
                '}'
    }
}