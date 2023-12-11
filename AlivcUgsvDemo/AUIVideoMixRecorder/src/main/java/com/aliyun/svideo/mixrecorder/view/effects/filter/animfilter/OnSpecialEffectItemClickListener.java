/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.mixrecorder.view.effects.filter.animfilter;

import com.aliyun.svideosdk.common.struct.effect.EffectFilter;

/**
 * 特效滤镜item点击事件接口
 */
public interface OnSpecialEffectItemClickListener {
    /**
     * 特效滤镜item点击
     * @param effectInfo 特效对象
     * @param index 下标
     */
    void onItemClick(EffectFilter effectInfo, int index);

    /**
     * 更新特效滤镜参数
     * @param effectInfo 特效对象
     */
    void onItemUpdate(EffectFilter effectInfo);
}
