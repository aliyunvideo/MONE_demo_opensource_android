package com.alivc.auiplayer.videoepisode.data;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

@Keep
public class AUIEpisodeDataEvent {
    public int preIndex;
    public int curIndex;
    public AUIEpisodeVideoInfo episodeVideoInfo;

    public AUIEpisodeDataEvent(int preIndex, int curIndex, @NonNull AUIEpisodeVideoInfo episodeVideoInfo) {
        this.preIndex = preIndex;
        this.curIndex = curIndex;
        this.episodeVideoInfo = episodeVideoInfo;
    }

    @Override
    public String toString() {
        return "AUIEpisodeDataEvent{" +
                "preIndex=" + preIndex +
                ", curIndex=" + curIndex +
                ", episodeVideoInfo=" + episodeVideoInfo +
                '}';
    }
}
