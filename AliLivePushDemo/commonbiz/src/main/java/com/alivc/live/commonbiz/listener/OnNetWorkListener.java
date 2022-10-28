package com.alivc.live.commonbiz.listener;

public interface OnNetWorkListener {
    void onSuccess(Object obj);

    void onFailure(int code, String msg);

    void onError();
}
