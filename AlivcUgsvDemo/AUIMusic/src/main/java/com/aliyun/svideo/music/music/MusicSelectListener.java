package com.aliyun.svideo.music.music;

public interface MusicSelectListener {
    void onMusicSelect(MusicFileBean musicFileBean, long startTime);
    void onCancel();
}
