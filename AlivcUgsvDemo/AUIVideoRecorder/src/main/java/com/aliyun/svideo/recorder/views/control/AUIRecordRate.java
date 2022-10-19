package com.aliyun.svideo.recorder.views.control;

/**
 * @author zsy_18 data:2018/7/31
 */
public enum AUIRecordRate {
    /**
     * 录制速度，很慢
     */
    VERY_FLOW(0.5f),
    /**
     * 录制速度，较慢
     */
    FLOW(0.75f),
    /**
     * 录制速度，标准
     */
    STANDARD(1f),
    /**
     * 录制速度，快
     */
    FAST(1.5f),
    /**
     * 录制速度，很快
     */
    VERY_FAST(2f);
    private float rate;

    private AUIRecordRate(float rate) {
        this.rate = rate;
    }

    public float getRate() {
        return this.rate;
    }
}

