package com.aliyun.svideo.editor.common.widget

import android.os.SystemClock
import android.view.View


open class DebouncedOnClickListener(listener: View.OnClickListener) : View.OnClickListener {

    private val THRESHOLD_MILLIS = 500L
    private var lastClickMillis: Long = 0
    private var proxyListener : View.OnClickListener? = listener

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    override fun onClick(v: View?) {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickMillis > THRESHOLD_MILLIS) {
            proxyListener?.onClick(v)
        }
        lastClickMillis = now
    }
}