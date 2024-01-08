package com.alivc.live.commonbiz.seidelay.api;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public interface ISEIDelayEventListener {
    void onEvent(String src, String type, String msg);
}
