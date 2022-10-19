package com.alivc.live.pusher.demo.bean;

/**
 * 显示模式
 */
public enum BeautyItemMode {
    /**
     * 滑动条
     */
    SEEKBAR(0),

    /**
     * 不显示
     */
    NULL(1),

    /**
     * 开关
     */
    SWITCH(2);



    private int value;

    BeautyItemMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
