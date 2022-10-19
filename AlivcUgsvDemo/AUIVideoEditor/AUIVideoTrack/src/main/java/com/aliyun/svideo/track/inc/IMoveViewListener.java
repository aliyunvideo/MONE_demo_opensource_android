package com.aliyun.svideo.track.inc;

/**
 * 左右拖动事件
 */
public interface IMoveViewListener {
    void onMove(float dx, float moveViewLeftBorderPosition);

    void onUp(float dRawX);

    void onDown(float dx);

}
