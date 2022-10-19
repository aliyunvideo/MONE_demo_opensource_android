package com.aliyun.svideo.track.inc;


import com.aliyun.svideo.track.bean.BaseClipInfo;

public interface ClipTrackListener {

    /**
     * 焦点变化事件
     *
     * @param clipInfo
     * @param isFocus 当前焦点
     */
    void onFocusChanged(BaseClipInfo clipInfo, boolean isFocus);

    /**
     * 点击转场事件
     *
     * @param index 转场位置
     */
    void onTransitionClick(int index);

    /**
     * 滚动切换时间
     *
     * @param time 时间轴上时间位置 单位:毫秒
     */
    void onScrollChangedTime(long time);
}
