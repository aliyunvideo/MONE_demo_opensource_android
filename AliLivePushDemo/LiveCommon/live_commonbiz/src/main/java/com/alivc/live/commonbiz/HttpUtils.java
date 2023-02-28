package com.alivc.live.commonbiz;

import com.alivc.live.commonbiz.listener.OnNetWorkListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtils {

    private static final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(50000, TimeUnit.MILLISECONDS)
            .readTimeout(50000, TimeUnit.MILLISECONDS)
            .build();

    public static void get(String url, OnNetWorkListener listener) {
        Request okHttpRequest = new Request.Builder()
                .url(url)
                .head()
                .build();

        mOkHttpClient.newCall(okHttpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                if (listener != null) {
                    listener.onError();
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (listener != null) {
                    if (response.isSuccessful()) {
                        listener.onSuccess(response);
                    } else {
                        listener.onFailure(response.code(), response.message());
                    }
                }
            }
        });
    }
}
