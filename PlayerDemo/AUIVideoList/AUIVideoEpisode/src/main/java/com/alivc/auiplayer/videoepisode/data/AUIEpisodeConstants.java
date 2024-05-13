package com.alivc.auiplayer.videoepisode.data;

/**
 * @author keria
 * @date 2023/9/26
 * @brief 短剧常量管理
 */
public class AUIEpisodeConstants {
    /**
     * 私有加密视频源
     *
     * @note 该视频源采用了MP4私有加密功能，仅能在当前APP license下进行播放
     * @note 更多信息请查看模块文档里面，私有加密相关的介绍
     * @see <a href="https://help.aliyun.com/zh/vod/user-guide/alibaba-cloud-proprietary-cryptography">阿里云视频加密（私有加密）</a>
     */
    private static final String ENCRYPTED_EPISODE_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode_encrypt.json";

    /**
     * 普通视频源
     */
    private static final String CUSTOM_EPISODE_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/player/aui_episode.json";

    /**
     * 短剧剧集json文本链接
     *
     * @attention 此变量用于控制剧集页播放的剧集地址！！！
     * @note 当前使用`私有加密视频源`，由于MP4私有加密特性，集成到您项目工程中将会播放失效
     * @note 如果您想体验集成后的效果，请注意替换视频源地址为`普通视频源`
     */
    public static final String EPISODE_JSON_URL = ENCRYPTED_EPISODE_URL;
}
