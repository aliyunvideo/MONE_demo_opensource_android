package com.alivc.auiplayer.videoepisode.listener;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;

public interface OnInteractiveEventListener {
    /**
     * 点击点赞
     *
     * @param episodeVideoInfo 短剧数据
     * @param isSelected       点赞 or 取消点赞
     */
    void onClickLike(AUIEpisodeVideoInfo episodeVideoInfo, boolean isSelected);

    /**
     * 点击评论
     *
     * @param episodeVideoInfo 短剧数据
     */
    void onClickComment(AUIEpisodeVideoInfo episodeVideoInfo);

    /**
     * 点击分享
     *
     * @param episodeVideoInfo 短剧数据
     */
    void onClickShare(AUIEpisodeVideoInfo episodeVideoInfo);
}
