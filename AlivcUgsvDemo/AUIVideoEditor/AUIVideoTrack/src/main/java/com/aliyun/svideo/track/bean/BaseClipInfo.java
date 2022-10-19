package com.aliyun.svideo.track.bean;

public class BaseClipInfo {
    /**
     * 片段ID
     */
    private int mClipId = -1;
    /**
     * 片段类型
     */
    private ClipType mClipType;
    /**
     * 素材片段相对于素材的入点
     */
    private long mIn;

    /**
     * 素材片段相对于素材的出点
     */
    private long mOut;

    /**
     * 素材资源的时长
     */
    private long mDuration;

    /**
     * 素材片段相对于时间线的入点
     */
    private long mTimelineIn;

    /**
     * 素材片段相对于时间线的出点
     */
    private long mTimelineOut;

    /**
     * 排序
     */
    private int mIndex;

    /**
     * 片段速率,当前没用到
     */
    private float mSpeed = 1.0f;

    public int getClipId() {
        return mClipId;
    }

    public void setClipId(int clipId) {
        this.mClipId = clipId;
    }

    public ClipType getClipType() {
        return mClipType;
    }

    public void setClipType(ClipType clipType) {
        this.mClipType = clipType;
    }

    public long getIn() {
        return mIn;
    }

    public void setIn(long in) {
        this.mIn = in;
    }

    public long getOut() {
        return mOut;
    }

    public void setOut(long out) {
        this.mOut = out;
    }

    public long getDuration() {
        return mDuration;
    }

    public void setDuration(long duration) {
        this.mDuration = duration;
    }

    public long getTimelineIn() {
        return mTimelineIn;
    }

    public void setTimelineIn(long timelineIn) {
        this.mTimelineIn = timelineIn;
    }

    public long getTimelineOut() {
        return mTimelineOut;
    }

    public void setTimelineOut(long timelineOut) {
        this.mTimelineOut = timelineOut;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        this.mIndex = index;
    }

    public float getSpeed() {
        return mSpeed;
    }

    public void setSpeed(float speed) {
        this.mSpeed = speed;
    }

    public long getClipDuration() {
        return mTimelineOut - mTimelineIn;
    }
}
