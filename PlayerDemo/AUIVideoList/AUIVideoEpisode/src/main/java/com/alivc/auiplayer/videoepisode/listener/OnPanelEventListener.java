package com.alivc.auiplayer.videoepisode.listener;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;

public interface OnPanelEventListener {
    /**
     * 点击收起
     */
    void onClickRetract();

    /**
     * 点击剧集
     *
     * @param episodeVideoInfo 短剧单集视频数据
     */
    void onItemClicked(AUIEpisodeVideoInfo episodeVideoInfo);
}
