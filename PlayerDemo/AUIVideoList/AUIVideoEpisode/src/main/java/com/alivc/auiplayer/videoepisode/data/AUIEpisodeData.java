package com.alivc.auiplayer.videoepisode.data;

import java.io.Serializable;
import java.util.List;

/**
 * @author keria
 * @date 2023/9/26
 * @brief 短剧剧集数据结构
 */
public class AUIEpisodeData implements Serializable {

    /**
     * 短剧剧集唯一ID
     */
    public String id;

    /**
     * 短剧剧集名称
     */
    public String title;

    /**
     * 短剧剧集视频列表
     */
    public List<AUIEpisodeVideoInfo> list;

}
