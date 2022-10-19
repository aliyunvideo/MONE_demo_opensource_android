package com.aliyun.player.alivcplayerexpand.background;

public interface IPlayNotifyEvent {
    String KEY_NOTIFY_ACTION = "key_notify_action";
    int NOTIFY_PLAY_NEXT = 1;
    int NOTIFY_PLAY_LAST = 2;
    int NOTIFY_PLAY_ICON_CLICK = 3;
    int NOTIFY_JUMP_DETAIL_PLAY = 4;
    int NOTIFY_CLOSE_NOTIFY = 5;
}
