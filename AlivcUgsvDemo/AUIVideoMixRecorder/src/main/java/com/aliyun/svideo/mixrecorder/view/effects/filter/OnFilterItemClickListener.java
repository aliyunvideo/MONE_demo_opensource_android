/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter;

/**
 * 滤镜item点击事件接口
 * @author xlx
 */
public interface OnFilterItemClickListener {
    /**
     * 滤镜item点击
     * @param effectInfo 特效对象
     * @param index 下标
     * @return
     */
    void onItemClick(AUIEffectInfo effectInfo, int index);
}
