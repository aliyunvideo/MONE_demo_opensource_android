package com.aliyun.video.common.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

inline fun <reified VM : ViewModel> Fragment.getViewModel() = lazy {
    ViewModelProvider(this).get(VM::class.java)
}

