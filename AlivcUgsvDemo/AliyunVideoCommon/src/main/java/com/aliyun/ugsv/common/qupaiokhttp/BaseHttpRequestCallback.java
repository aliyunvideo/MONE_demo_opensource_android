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

import java.lang.reflect.Type;

import okhttp3.Headers;
import okhttp3.Response;

public class BaseHttpRequestCallback<T> {

//    public static final int ERROR_RESPONSE_NULL = 1001;
    public static final int ERROR_RESPONSE_DATA_PARSE_EXCEPTION = 1002;
    public static final int ERROR_RESPONSE_UNKNOWN = 1003;
//    public static final int ERROR_RESPONSE_TIMEOUT = 1004;

    protected Type type;
    protected Headers headers;

    public BaseHttpRequestCallback() {
        type = ClassTypeReflect.getModelClazz(getClass());
    }

    public void onStart() {
    }

    public void onResponse(Response httpResponse, String response, Headers headers) {

    }

    public void onResponse(String response, Headers headers) {
    }

    public void onFinish() {
    }

    protected void onSuccess(Headers headers, T t) {
    }
    protected void onSuccess(T t) {

    }

    /**
     * 上传文件进度
     * @param progress
     * @param networkSpeed 网速
     * @param done
     */
    public void onProgress(int progress, long networkSpeed, boolean done) {
    }

    public void onFailure(int errorCode, String msg) {
    }

    public Headers getHeaders() {
        return headers;
    }

    protected void setResponseHeaders(Headers headers) {
        this.headers = headers;
    }


}
