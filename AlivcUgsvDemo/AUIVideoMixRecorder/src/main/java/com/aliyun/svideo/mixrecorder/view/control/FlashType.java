package com.aliyun.svideo.mixrecorder.view.control;

/**
 * @author zsy_18 data:2018/7/31
 */
public enum FlashType {
    /**
     * 关闭闪光灯
     */
    OFF("off"),
    /**
     * 只闪一下
     */
    ON("on"),
    /**
     * 自动
     */
    AUTO("auto"),
    /**
     * 持续亮灯
     */
    TORCH("torch");

    private String type;

    private FlashType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.type;
    }
}
