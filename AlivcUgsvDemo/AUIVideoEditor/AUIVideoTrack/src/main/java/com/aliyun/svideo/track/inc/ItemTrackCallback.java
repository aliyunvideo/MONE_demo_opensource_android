package com.aliyun.svideo.track.inc;


public interface ItemTrackCallback {

    void onClipTouchMove(int id, int dx, int orientation, float clipLength, int scrollByX);

    void onTransitionClick(int index);

    void onTrackTouch(int id, boolean isFocus);

    /**
     * 触摸拖动抬起事件
     * @param id
     * @param orientation
     * @param clipInTime
     * @param clipOutTime
     */
    void onClipTouchUp(int id, int orientation, long clipInTime, long clipOutTime);

    /**
     * 触摸拖动按下事件
     * @param id
     * @param orientation
     * @param clipLength
     * @param contentLeftPosition
     */
    void onClipTouchDown(int id, int orientation, float clipLength, int contentLeftPosition);

    /**
     * 片段时间更新
     *
     * @param effectId
     * @param orientation
     * @param timeIn
     * @param timeOut
     * @param timelineIn
     * @param timelineOut
     */
    void onUpdateClipTime(int effectId, int orientation, long timeIn, long timeOut, long timelineIn, long timelineOut);

}
