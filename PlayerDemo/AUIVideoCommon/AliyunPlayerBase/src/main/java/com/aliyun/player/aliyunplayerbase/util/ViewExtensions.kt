package com.aliyun.player.aliyunplayerbase.util

import android.view.View
import android.view.ViewGroup

fun View.setVisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.isVisible(): Boolean {
    return View.VISIBLE == visibility
}

fun View.removeSelf() {
    (parent as ViewGroup?)?.removeView(this)
}
