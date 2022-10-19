package com.aliyun.video.common.ui

import android.view.KeyEvent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

object KeyEventDispatcher {
    private val mListener = mutableSetOf<OnKeyEventListener>()

    fun dispatchEvent(keyCode: Int, event: KeyEvent?) {
        mListener.forEach {
            it.onKeyDown(keyCode, event)
        }
    }

    fun addListener(lifecycle: Lifecycle, onKeyEventListener: OnKeyEventListener) {
        mListener.add(onKeyEventListener)
        lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        mListener.remove(onKeyEventListener)
                    }
                }
            }
        })
    }
}