package com.alivc.player.videolist.auivideolistcommon.listener;

public interface OnNetWorkListener {
    void onSuccess(Object obj);

    void onFailure(int code, String msg);

    void onError(String msg);
}
