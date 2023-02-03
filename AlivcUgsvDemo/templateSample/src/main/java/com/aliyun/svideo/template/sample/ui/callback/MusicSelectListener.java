package com.aliyun.svideo.template.sample.ui.callback;

import com.aliyun.svideo.music.music.MusicFileBean;

public interface MusicSelectListener {
    void onMusicSelect(MusicFileBean musicFileBean);
    void onMusicPanelClose(boolean confirm);
}
