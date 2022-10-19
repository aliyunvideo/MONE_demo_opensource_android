package com.aliyun.svideo.track.inc;

/**
 * X轴滚动事件
 */
public interface IScrollChangeListener {
    /**
     * 滚动事件回调
     *
     * @param scrollX 滚动完成位置
     * @param dx      滚动距离
     */
    void onScrollChanged(int scrollX, int dx);

    /**
     * 滚动事件回调
     *
     * @param scrollX 滚动完成位置
     */
    void onScrollChanged(int scrollX);
}
