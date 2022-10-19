package com.aliyun.apsaravideo.videocommon.list;

public interface ListPlayCallback {
    int STATE_INIT = 0;
    int STATE_PLAYING = 1;
    int STATE_PAUSED = 2;

    void play(int position);

    void pause();

    int getPlayState();
}
