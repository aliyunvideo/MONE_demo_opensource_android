package com.aliyun.svideo.track.inc;

/**
 * 片段拖拽事件
 */
public interface OnClipDragListener {
    /**
     * 开始拖拽
     *
     * @param pointX
     * @param pointY
     */
    void onDragStart(float pointX, float pointY);

    /**
     * 结束拖拽
     *
     * @param pointX
     * @param pointY
     */
    void onDragEnd(float pointX, float pointY);

    /**
     * 拖拽位置变化
     *
     * @param pointX
     * @param pointY
     */
    void onDragLocationChange(float pointX, float pointY);
}
