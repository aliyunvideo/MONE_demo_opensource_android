package com.aliyun.player.alivcplayerexpand.listplay;

public interface IPlayManagerScene {
    int SCENE_NORMAL = 0; //正常模式，无剧集播放
    int SCENE_SERIES = 1; // 剧集播放
    int SCENE_FLOAT_PLAY = 2; //画中画播放，如果剧集列表不为空，则支持剧集
    int SCENE_ONLY_VOICE = 3;//音频模式，如果剧集列表不为空，则支持剧集
}
