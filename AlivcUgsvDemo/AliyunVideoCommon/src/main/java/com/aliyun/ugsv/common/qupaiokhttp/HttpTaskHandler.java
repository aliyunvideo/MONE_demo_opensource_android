/*
 * Copyright (C) 2015 pengjianbo(pengjianbosoft@gmail.com), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.aliyun.ugsv.common.qupaiokhttp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpTaskHandler {

    /** 正在请求的任务集合 */
    private static Map<String, List<OkHttpTask>> httpTaskMap;
    /** 单例请求处理器 */
    private static HttpTaskHandler httpTaskHandler = null;

    private HttpTaskHandler() {
        httpTaskMap = new ConcurrentHashMap<>();
    }

    /**
     * 获得处理器实例
     */
    public static HttpTaskHandler getInstance() {
        if (httpTaskHandler == null) {
            httpTaskHandler = new HttpTaskHandler();
        }
        return httpTaskHandler;
    }

    /**
     * 移除KEY
     * @param key
     */
    public synchronized void removeTask(String key) {
        if (httpTaskMap.containsKey(key)) {
            //移除对应的Key
            httpTaskMap.remove(key);
        }
    }

    /**
     * 将请求放到Map里面
     * @param key
     * @param task
     */
    synchronized void addTask(String key, OkHttpTask task) {
        if (httpTaskMap.containsKey(key)) {
            List<OkHttpTask> tasks = httpTaskMap.get(key);
            tasks.add(task);
            httpTaskMap.put(key, tasks);
        } else {
            List<OkHttpTask> tasks = new ArrayList<>();
            tasks.add(task);
            httpTaskMap.put(key, tasks);
        }
    }

    /**
     * 判断是否存在
     * @param key
     * @return
     */
    boolean contains(String key) {
        return httpTaskMap.containsKey(key);
    }
}
