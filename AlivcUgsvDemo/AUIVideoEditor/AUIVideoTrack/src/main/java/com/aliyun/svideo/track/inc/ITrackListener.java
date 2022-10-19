package com.aliyun.svideo.track.inc;

import com.aliyun.svideo.track.bean.BaseClipInfo;
import com.aliyun.svideo.track.bean.ClipType;

public interface ITrackListener {
    /**
     * 点击选择封面事件
     */
    void onSelectCoverClick();

    /**
     * 点击转场事件
     *
     * @param index 对应下标
     */
    void onTransitionClick(int index);
    void onPreviewTrackClick(ClipType clipType);
    void onSubTrackClick(BaseClipInfo clipInfo, boolean isFocus);

    /**
     * 主轨片段点击事件
     *
     * @param index 对应下标
     * @param isFocus 当前是否选中
     */
    void onVideoTrackClick(int index, boolean isFocus);

    /**
     * 更新主轨片段位置
     *
     * @param oldIndex 原下标
     * @param newIndex 新下标
     */
    void onUpdateMainClipIndex(int oldIndex, int newIndex);

    /**
     * 更新片时间信息
     *
     * @param type 片段类型
     * @param id 片段id
     * @param timeIn 片段对应素材进入时间
     * @param timeOut 片段对应素材退出时间
     * @param timelineIn 片段对时间线进入时间
     * @param timelineOut 片段对时间线退出时间
     */
    void onUpdateClipTime(ClipType type, int id, long timeIn, long timeOut, long timelineIn, long timelineOut);

    /**
     * 当前触摸移动点位置
     *
     * @param time 时间轴上时间位置
     */
    void onClipTouchPosition(long time);
}
