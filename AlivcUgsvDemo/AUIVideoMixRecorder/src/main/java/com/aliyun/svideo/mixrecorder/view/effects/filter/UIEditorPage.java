/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter;
/**
 * NOTE: item order must match
 * @author xlx
 */
public enum UIEditorPage {
    /**
     * 滤镜特效
     */
    FILTER_EFFECT,
    /**
     * 动图
     */
    OVERLAY,
    /**
     * 字幕
     */
    CAPTION,
    /**
     * MV
     */
    MV,
    /**
     * 背景音乐
     */
    AUDIO_MIX,
    /**
     * 画笔特效
     */
    PAINT,
    /**
     * 时间特效
     */
    TIME,
    /**
     * 字体特效
     */
    FONT
    ;

    public static UIEditorPage get(int index) {
        return values()[index];
    }

    public int index() {
        return ordinal();
    }
}
