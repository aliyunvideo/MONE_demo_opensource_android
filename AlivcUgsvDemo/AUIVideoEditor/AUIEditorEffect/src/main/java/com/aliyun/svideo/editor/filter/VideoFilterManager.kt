package com.aliyun.svideo.editor.filter

class VideoFilterManager {
    var mVideoFilterItemViewModel:VideoFilterItemViewModel ?= null

    open fun setVideoFilterItem(viewModel: VideoFilterItemViewModel) {
        mVideoFilterItemViewModel = viewModel
    }
}