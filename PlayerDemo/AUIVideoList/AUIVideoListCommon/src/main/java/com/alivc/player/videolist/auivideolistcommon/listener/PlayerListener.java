package com.alivc.player.videolist.auivideolistcommon.listener;


import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.bean.InfoBean;

public interface PlayerListener {
    void onPrepared(int position);
    void onInfo(int position, InfoBean infoBean);
    void onPlayStateChanged(int position,boolean isPaused);
    void onRenderingStart(int position,long duration);
    void onCompletion(int position);
    void onError(ErrorInfo errorInfo);
}
