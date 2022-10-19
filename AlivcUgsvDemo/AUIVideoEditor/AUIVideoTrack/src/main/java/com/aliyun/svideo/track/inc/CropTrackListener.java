package com.aliyun.svideo.track.inc;


public interface CropTrackListener {
    /**
     * 更新片段时间信息
     *
     * @param timeIn 片段对应素材进入时间 单位:毫秒
     * @param timeOut 片段对应素材退出时间 单位:毫秒
     */
    void onUpdateClipTime(long timeIn, long timeOut);

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
