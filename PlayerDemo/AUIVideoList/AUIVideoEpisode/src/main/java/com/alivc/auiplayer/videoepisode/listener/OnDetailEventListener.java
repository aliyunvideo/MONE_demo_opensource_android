package com.alivc.auiplayer.videoepisode.listener;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;

public interface OnDetailEventListener {
    /**
     * 点击用户昵称
     *
     * @param episodeVideoInfo 短剧数据
     */
    void onClickAuthor(AUIEpisodeVideoInfo episodeVideoInfo);
}
