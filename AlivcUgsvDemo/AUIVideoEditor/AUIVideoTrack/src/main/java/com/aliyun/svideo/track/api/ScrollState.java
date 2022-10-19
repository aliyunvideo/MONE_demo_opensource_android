package com.aliyun.svideo.track.api;

public enum ScrollState {
    /**
     * 滚动停止
     */
    IDLE,
    /**
     * 用户触摸状态下的滚动
     */
    DRAGGING,
    /**
     * 用户释放手指后继续滚动
     */
    SETTLING
}
