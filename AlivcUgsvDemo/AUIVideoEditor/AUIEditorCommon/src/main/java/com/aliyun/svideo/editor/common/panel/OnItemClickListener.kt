package com.aliyun.svideo.editor.common.panel

import android.view.View

interface OnItemClickListener {
    fun onItemClick(view: View?, id: Long, obj : Any? = null)
}