/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */
package com.aliyun.svideo.editor.common.util

import android.graphics.Matrix

open class MatrixUtil {
    var translateX = 0f
    var translateY = 0f
    var scaleX = 0f
    var scaleY = 0f
    var rotation = 0f
    var rotationDeg: Float
        get() = (rotation / Math.PI * 180).toFloat()
        set(value) {
            rotation = (value * Math.PI / 180).toFloat()
        }
    private val _Data = FloatArray(9)
    fun decomposeTSR(m: Matrix) {
        m.getValues(_Data)
        translateX = _Data[Matrix.MTRANS_X]
        translateY = _Data[Matrix.MTRANS_Y]
        val sx = _Data[Matrix.MSCALE_X]
        val sy = _Data[Matrix.MSCALE_Y]
        val skewx = _Data[Matrix.MSKEW_X]
        val skewy = _Data[Matrix.MSKEW_Y]
        scaleX = Math.sqrt((sx * sx + skewx * skewx).toDouble()).toFloat()
        scaleY = Math.sqrt((sy * sy + skewy * skewy).toDouble())
            .toFloat() * Math.signum(sx * sy - skewx * skewy)
        rotation = Math.atan2(-skewx.toDouble(), sx.toDouble()).toFloat()
    }

    fun composeTSR(m: Matrix) {
        m.setRotate(rotationDeg)
        m.postScale(scaleX, scaleY)
        m.postTranslate(translateX, translateY)
    }

    override fun toString(): String {
        return "MatrixUtil{" +
                "translateX=" + translateX +
                ", translateY=" + translateY +
                ", scaleX=" + scaleX +
                ", scaleY=" + scaleY +
                ", rotation=" + rotation +
                '}'
    }

    fun clear() {
        translateX = 0f
        translateY = 0f
        scaleX = 1.0f
        scaleY = 1.0f
        rotation = 0f
    }
}