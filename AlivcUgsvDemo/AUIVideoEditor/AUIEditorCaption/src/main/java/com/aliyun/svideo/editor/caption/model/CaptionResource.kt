package com.aliyun.svideo.editor.caption.model

import com.aliyun.svideosdk.common.struct.project.Source

data class CaptionResource(
    val type: CaptionResourceType,
    val id: Int,
    val source: Source,
    val cover: String,
    val name: String = "",
    val coverResId: Int = 0,
    var isChecked: Boolean = false
) {
}