package com.alivc.player.videolist.auivideolistcommon.listener;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;

import java.util.List;

public interface OnCompletionListener {
    void onCompletion(boolean success, List<VideoInfo> videoInfoList, String errorMsg,int lastIndex);
}
