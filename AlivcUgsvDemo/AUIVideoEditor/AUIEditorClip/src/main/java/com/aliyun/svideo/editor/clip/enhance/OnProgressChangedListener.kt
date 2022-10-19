package com.aliyun.svideo.editor.clip.enhance


interface OnProgressChangedListener {
    fun onProgressChanged(panelItemId: Long, progress:Float, isAll:Boolean)
    fun onResetClick(isAll:Boolean)
}