package com.aliyun.video.common.ui

import android.view.KeyEvent

interface OnKeyEventListener {
    fun onKeyDown(keyCode: Int, event: KeyEvent?)
}