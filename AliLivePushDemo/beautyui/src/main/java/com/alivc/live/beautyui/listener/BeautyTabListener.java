package com.alivc.live.beautyui.listener;


import androidx.annotation.Nullable;

import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beautyui.bean.BeautyTabBean;

/**
 * 美颜Tab事件回调
 */
public interface BeautyTabListener {

    public abstract void onTabItemDataChanged(@Nullable BeautyTabBean tabBean, @Nullable BeautyItemBean beautyItemBean);

    public abstract void onTabPutAway(@Nullable BeautyTabBean tabBean);

    public abstract void onTabReset(@Nullable BeautyTabBean tabBean);

}
