package com.aliyun.auiplayerapp.videolist.repository;


import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.List;

public interface AUIVideoListDataSource {

    /**
     * get Data Source
     */
    List<VideoInfo> getVideoList();
}
