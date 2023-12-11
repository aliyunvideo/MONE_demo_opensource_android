package com.aliyun.svideo.mixrecorder.view.music;

import com.aliyun.svideo.music.music.MusicFileBean;

public interface MusicSelectListener {
    void onMusicSelect(MusicFileBean musicFileBean, long startTime);
}
