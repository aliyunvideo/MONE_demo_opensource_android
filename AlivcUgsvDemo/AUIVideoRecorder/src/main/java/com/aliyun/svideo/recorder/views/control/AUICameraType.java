package com.aliyun.svideo.recorder.views.control;

/**
 * @author zsy_18 data:2018/7/31
 */
public enum AUICameraType {
    /**
     * 前置摄像头
     */
    FRONT(1),
    /**
     * 后置摄像头
     */
    BACK(0);

    private int type;

    private AUICameraType(int type) {
        this.type = type;
    }

    public int getType() {
        return this.type;
    }
}
