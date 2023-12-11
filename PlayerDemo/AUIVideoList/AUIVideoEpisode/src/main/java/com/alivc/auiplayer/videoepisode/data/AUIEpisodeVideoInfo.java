package com.alivc.auiplayer.videoepisode.data;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.Formatter;
import java.util.Locale;

/**
 * 短剧单集视频数据
 */
public class AUIEpisodeVideoInfo extends VideoInfo {

    /**
     * 视频唯一id
     */
    public int videoId;

    /// --------视频详情信息--------

    /**
     * 视频封面图
     */
    public String coverUrl;

    /// --------视频播放信息--------

    /**
     * 视频播放时长
     */
    public int videoDuration;

    /**
     * 视频播放
     */
    public int videoPlayCount;

    /// --------视频互动信息--------

    /**
     * 点赞状态
     */
    public boolean isLiked;

    /**
     * 点赞数量
     */
    public int likeCount;

    /**
     * 评论数量
     */
    public int commentCount;

    /**
     * 分享数量
     */
    public int shareCount;

    /**
     * 格式化显示数字
     *
     * @param number 原始数字
     * @return 字符串
     */
    public static String formatNumber(int number) {
        if (number < 10000) {
            return String.valueOf(number);
        }
        return String.format("%.1f万", ((float) number / 10000));
    }

    public static String formatTimeDuration(int videoDuration) {
        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        int totalSeconds = videoDuration / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;

        formatBuilder.setLength(0);
        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }
}
