package com.aliyun.player.alivcplayerexpand.background;

public interface IForegroundService {

    void stopForeground();

    void startForeground();

    boolean isForeground();
}
