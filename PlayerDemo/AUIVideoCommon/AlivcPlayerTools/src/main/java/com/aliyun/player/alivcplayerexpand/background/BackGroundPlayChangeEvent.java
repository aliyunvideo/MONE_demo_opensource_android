package com.aliyun.player.alivcplayerexpand.background;

import androidx.annotation.Keep;

@Keep
public class BackGroundPlayChangeEvent {
    public static final int ACTION_NEXT_SERIES = 1;
    public static final int ACTION_PRE_SERIES = 2;
    public static final int ACTION_PAUSE = 3;
    public static final int ACTION_RESUME = 4;
    public int action;

    public BackGroundPlayChangeEvent(int action) {
        this.action = action;
    }
}
