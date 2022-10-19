package com.aliyun.svideo.editor.sticker.model

import com.aliyun.svideosdk.editor.AliyunPasterController

class StickerBorderState(val visible : Boolean, val controller : AliyunPasterController? = null, val update : Boolean = false, val newlyAdd : Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StickerBorderState

        if (visible != other.visible) return false
        if (controller.hashCode() != other.hashCode() || !controller?.pasterIconPath.equals(other.controller?.pasterIconPath)) return false
        if (update != other.update) return false
        if (newlyAdd != other.newlyAdd) return false

        return true
    }

    override fun hashCode(): Int {
        var result = visible.hashCode()
        result = 31 * result + (controller?.hashCode() ?: 0)
        result = 31 * result + update.hashCode()
        result = 31 * result + newlyAdd.hashCode()
        return result
    }


}
