package com.aliyun.svideo.track.bean;

public class MultiClipInfo extends BaseClipInfo {
    /**
     * 片段进入时间线最小值
     */
    private long mMinTimelineIn;
    /**
     * 片段进入时间线最大值
     */
    private long mMaxTimelineOut;

    public long getMinTimelineIn() {
        return mMinTimelineIn;
    }

    public void setMinTimelineIn(long minTimelineIn) {
        this.mMinTimelineIn = minTimelineIn;
    }

    public long getMaxTimelineOut() {
        return mMaxTimelineOut;
    }

    public void setMaxTimelineOut(long maxTimelineOut) {
        this.mMaxTimelineOut = maxTimelineOut;
    }
}
