/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.aio.utils;

import java.util.Map;

public class MapUtil {
    public static <T, K> T parseMapValue(Map<K, T> map, K key, T defValue) {
        if (map != null && map.containsKey(key)) {
            return map.get(key);
        } else {
            return defValue;
        }
    }
}
