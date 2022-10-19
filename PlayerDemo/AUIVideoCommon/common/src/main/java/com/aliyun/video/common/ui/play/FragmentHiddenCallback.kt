package com.aliyun.video.common.ui.play

import com.aliyun.video.common.ui.BaseFragment

interface FragmentHiddenCallback {
    fun onFragmentHidden(fragment: BaseFragment, hidden: Boolean)
}