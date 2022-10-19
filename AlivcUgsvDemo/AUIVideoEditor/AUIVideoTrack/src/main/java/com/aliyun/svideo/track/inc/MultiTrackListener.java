package com.aliyun.svideo.track.inc;


import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;

public interface MultiTrackListener {

    /**
     * 片段点击事件
     *
     * @param clipInfo 片段信息
     */
    void onClipClick(BaseClipInfo clipInfo);

    /**
     * 焦点变化事件
     *
     * @param clipInfo
     * @param isFocus 当前焦点
     */
    void onFocusChanged(BaseClipInfo clipInfo, boolean isFocus);

    /**
     * 片段位置更新
     * @param clipInfo
     */
    void onUpdateClipTime(BaseClipInfo clipInfo);

    /**
     * 当前触摸移动点位置
     *
     * @param time 时间轴上时间位置 单位:毫秒
     */
    void onClipTouchPosition(long time);

    /**
     * 滚动切换时间
     *
     * @param time 时间轴上时间位置 单位:毫秒
     */
    void onScrollChangedTime(long time);
}
