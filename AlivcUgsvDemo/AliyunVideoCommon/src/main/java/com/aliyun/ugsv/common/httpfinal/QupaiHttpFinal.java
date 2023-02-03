/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.ugsv.common.httpfinal;

import com.aliyun.ugsv.common.qupaiokhttp.OkHttpFinal;
import com.aliyun.ugsv.common.qupaiokhttp.OkHttpFinalConfiguration;
import com.aliyun.ugsv.common.qupaiokhttp.Part;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;

public class QupaiHttpFinal implements HttpInterface {
    private static QupaiHttpFinal instance;

    public static QupaiHttpFinal getInstance() {
        if (instance == null) {
            synchronized (QupaiHttpFinal.class) {
                if (instance == null) {
                    instance = new QupaiHttpFinal();
                }
            }
        }
        return instance;
    }

    public void initOkHttpFinal() {
        List<Part> commomParams = new ArrayList<>();
        Headers commonHeaders = new Headers.Builder().build();

        List<Interceptor> interceptorList = new ArrayList<>();
        OkHttpFinalConfiguration.Builder builder = null;
        builder = new OkHttpFinalConfiguration.Builder()
        .setCommenParams(commomParams)
        .setCommenHeaders(commonHeaders)
        .setTimeout(35000)
        .setInterceptors(interceptorList)
//                    .setCookieJar(CookieJar.NO_COOKIES)
//                    .setCertificates(getResources().getAssets().open("rootca.der"))
//                    .setHostnameVerifier(new SkirtHttpsHostnameVerifier())
        .setDebug(true);
//        addHttps(builder);
        OkHttpFinal.getInstance().init(builder.build());
    }

//    private static void addHttps(OkHttpFinalConfiguration.Builder builder) {
//        try {
//            SSLContext sc = SSLContext.getInstance("SSL");
//            sc.init(null, new TrustManager[]{new X509TrustManager() {
//                @Override
//                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                    Log.d(AliyunTag.TAG, "X509TrustManager checkServerTrusted" + "chain" + chain[0] + "authType" + authType);
//                }
//
//
//                @Override
//                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                    Log.d(AliyunTag.TAG, "X509TrustManager checkClientTrusted" + "chain" + chain[0] + "authType" + authType);
//                }
//
//                @Override
//                public X509Certificate[] getAcceptedIssuers() {
//                    return new X509Certificate[]{};
//                }
//            }}, new SecureRandom());
//            builder.setSSLSocketFactory(sc.getSocketFactory());
//            builder.setHostnameVerifier(new HostnameVerifier() {
//                @Override
//                public boolean verify(String hostname, SSLSession session) {
//                    return true;
//                }
//            });
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (KeyManagementException e) {
//            e.printStackTrace();
//        }
//    }

}
