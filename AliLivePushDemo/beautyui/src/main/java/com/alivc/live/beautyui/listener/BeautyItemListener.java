package com.alivc.live.beautyui.listener;

import androidx.annotation.Nullable;

import com.alivc.live.beautyui.bean.BeautyItemBean;

/**
 * 美颜Item事件回调
 */
public interface BeautyItemListener {

    public abstract void onItemClicked(@Nullable BeautyItemBean beautyItemBean);

    public abstract void onItemDataChanged(@Nullable BeautyItemBean beautyItemBean);

}
