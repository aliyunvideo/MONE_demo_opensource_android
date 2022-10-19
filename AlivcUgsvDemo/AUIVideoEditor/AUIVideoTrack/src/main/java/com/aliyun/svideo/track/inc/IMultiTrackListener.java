package com.aliyun.svideo.track.inc;

import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;

public interface IMultiTrackListener extends ScrollHandler {
    /**
     * 获取当前X轴滚动距离
     *
     * @return X轴滚动距离
     */
    int getParentScrollX();

    /**
     * x轴滚动最大距离
     *
     * @return
     */
    int getParentMaxScrollX();

    /**
     * 获取当前Y轴滚动距离
     *
     * @return Y轴滚动距离
     */
    int getParentScrollY();

    /**
     * 调用父控件偏移
     * @param translationX
     */
    void onParentTranslationX(float translationX);

    /**
     * 点击转场
     *
     * @param index
     */
    void onTransitionClick(int index);

    /**
     * 点击二级轨道片段
     *
     * @param clipInfo
     * @param isFocus
     */
    void onSubTrackClick(BaseClipInfo clipInfo, boolean isFocus);

    /**
     * 二级片段焦点变化回调
     *
     * @param clipInfo
     * @param isFocus
     */
    void onSubClipFocusChanged(BaseClipInfo clipInfo, boolean isFocus);

    /**
     * 点击主轨道片段
     * @param id
     * @param isFocus
     */
    void onVideoTrackClick(int id, boolean isFocus);

    /**
     * 更新主轨片段位置
     *
     * @param oldIndex
     * @param newIndex
     */
    void onUpdateMainClipIndex(int oldIndex, int newIndex);

    /**
     * 更新片段时间信息
     *
     * @param type
     * @param id 片段ID
     * @param timeIn
     * @param timeOut
     * @param timelineIn
     * @param timelineOut
     */
    void onUpdateClipTime(ClipType type, int id, long timeIn, long timeOut, long timelineIn, long timelineOut);

    /**
     * 当前触摸移动点位置
     *
     * @param time 时间轴上时间位置
     */
    void onClipTouchPosition(long time);
}
