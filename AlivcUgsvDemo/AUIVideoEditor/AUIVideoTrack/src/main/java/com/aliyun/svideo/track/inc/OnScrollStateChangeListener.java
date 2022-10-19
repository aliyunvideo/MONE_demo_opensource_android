package com.aliyun.svideo.track.inc;


import com.aliyun.svideo.track.api.ScrollState;

public interface OnScrollStateChangeListener {
    void onScrollStateChanged(ScrollState scrollState, int scrollX, int scrollY);
}
