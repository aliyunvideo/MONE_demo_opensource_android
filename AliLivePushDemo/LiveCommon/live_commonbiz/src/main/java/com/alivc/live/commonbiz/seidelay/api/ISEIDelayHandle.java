package com.alivc.live.commonbiz.seidelay.api;

import java.util.HashMap;
import java.util.Map;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public abstract class ISEIDelayHandle {
    public final Map<String, ISEIDelayEventListener> listenerHashMap = new HashMap<>();

    public void addListener(String src, ISEIDelayEventListener listener) {
        listenerHashMap.put(src, listener);
    }

    public void removeListener(String src) {
        listenerHashMap.remove(src);
    }

    public void destroy() {
        listenerHashMap.clear();
    }
}
