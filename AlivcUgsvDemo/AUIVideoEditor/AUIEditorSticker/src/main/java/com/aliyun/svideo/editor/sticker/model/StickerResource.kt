package com.aliyun.svideo.editor.sticker.model

import com.aliyun.svideosdk.common.struct.project.Source

data class StickerResource(val id : Int, val source : Source, val cover : String, val name : String = "", val coverResId : Int = 0, var isChecked : Boolean = false) {
}