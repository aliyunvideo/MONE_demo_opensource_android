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

import android.text.TextUtils;
import android.util.Log;

import com.aliyun.ugsv.common.httpfinal.QupaiHttpFinal;
import com.aliyun.ugsv.common.qupaiokhttp.https.HttpsCerManager;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;

import okhttp3.CookieJar;
import okhttp3.Headers;
import okhttp3.OkHttpClient;

public class OkHttpFinal {
    static {
        QupaiHttpFinal.getInstance().initOkHttpFinal();
    }

    private static OkHttpFinal sOkHttpFinal;


    private OkHttpClient mOkHttpClient;
    private OkHttpFinalConfiguration mConfiguration;

    private OkHttpFinal() {
    }

    public synchronized void init(OkHttpFinalConfiguration configuration) {
        this.mConfiguration = configuration;

        long timeout = configuration.getTimeout();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .connectTimeout(timeout, TimeUnit.MILLISECONDS)
        .writeTimeout(timeout, TimeUnit.MILLISECONDS)
        .readTimeout(timeout, TimeUnit.MILLISECONDS);
        if (configuration.getHostnameVerifier() != null) {
            builder.hostnameVerifier(configuration.getHostnameVerifier());
        }

        List<InputStream> certificateList = configuration.getCertificateList();
        if (certificateList != null && certificateList.size() > 0) {
            HttpsCerManager httpsCerManager = new HttpsCerManager(builder);
            httpsCerManager.setCertificates(certificateList);
        }

        CookieJar cookieJar = configuration.getCookieJar();
        if (cookieJar != null) {
            builder.cookieJar(cookieJar);
        }

        if (configuration.getCache() != null) {
            builder.cache(configuration.getCache());
        }

        if (configuration.getAuthenticator() != null) {
            builder.authenticator(configuration.getAuthenticator());
        }
        if (configuration.getCertificatePinner() != null) {
            builder.certificatePinner(configuration.getCertificatePinner());
        }
        builder.followRedirects(configuration.isFollowRedirects());
        builder.followSslRedirects(configuration.isFollowSslRedirects());
        if (configuration.getSslSocketFactory() != null) {
            builder.sslSocketFactory(configuration.getSslSocketFactory());
        }
        if (configuration.getDispatcher() != null) {
            builder.dispatcher(configuration.getDispatcher());
        }
        builder.retryOnConnectionFailure(configuration.isRetryOnConnectionFailure());
        if (configuration.getNetworkInterceptorList() != null) {
            builder.networkInterceptors().addAll(configuration.getNetworkInterceptorList());
        }
        if (configuration.getInterceptorList() != null) {
            builder.interceptors().addAll(configuration.getInterceptorList());
        }

        if (configuration.getProxy() != null) {
            builder.proxy(configuration.getProxy());
        }
        ILogger.d("OkHttpFinal init...");
        Constants.DEBUG = configuration.isDebug();

        mOkHttpClient = builder.build();
        if (mOkHttpClient == null) {
            Log.e("AliYunLog", "OkhttpFinal init failed, so okhttpClient is null");
        }
        Log.d("AliYunLog", "OkhttpFinal init successful, mOkhttpClient = " + mOkHttpClient + ", okhttpFinal = " + this);
    }

    public static OkHttpFinal getInstance() {
        if (sOkHttpFinal == null) {
            synchronized (OkHttpFinal.class) {
                if (sOkHttpFinal == null) {
                    sOkHttpFinal = new OkHttpFinal();
                }
            }
        }
        return sOkHttpFinal;
    }

    /**
     * 修改公共请求参数信息
     *
     * @param key
     * @param value
     */
    public synchronized void updateCommonParams(String key, String value) {
        boolean add = false;
        List<Part> commonParams = mConfiguration.getCommonParams();
        if (commonParams != null) {
            for (Part param : commonParams) {
                if (param != null && TextUtils.equals(param.getKey(), key)) {
                    param.setValue(value);
                    add = true;
                    break;
                }
            }
        }
        if (!add) {
            commonParams.add(new Part(key, value));
        }
    }

    /**
     * 修改公共header信息
     *
     * @param key
     * @param value
     */
    public synchronized void updateCommonHeader(String key, String value) {
        Headers headers = mConfiguration.getCommonHeaders();
        if (headers == null) {
            headers = new Headers.Builder().build();
        }
        mConfiguration.commonHeaders = headers.newBuilder().set(key, value).build();
    }

    @Deprecated
    public synchronized OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    public synchronized OkHttpClient.Builder getOkHttpClientBuilder() {
        if (mOkHttpClient == null) {
            Log.e("AliYunLog", "mOkhttpClient is null, okhttpFinal = " + this);
        }
        return mOkHttpClient.newBuilder();
    }

    public synchronized List<Part> getCommonParams() {
        return mConfiguration.getCommonParams();
    }

    public synchronized List<InputStream> getCertificateList() {
        return mConfiguration.getCertificateList();
    }

    public synchronized HostnameVerifier getHostnameVerifier() {
        return mConfiguration.getHostnameVerifier();
    }

    public synchronized long getTimeout() {
        return mConfiguration.getTimeout();
    }

    public synchronized Headers getCommonHeaders() {
        return mConfiguration.getCommonHeaders();
    }
}
