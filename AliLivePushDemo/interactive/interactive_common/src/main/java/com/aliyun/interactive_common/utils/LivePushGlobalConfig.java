package com.aliyun.interactive_common.utils;

import com.alivc.live.pusher.AlivcResolutionEnum;

public class LivePushGlobalConfig {

    /**
     * 多人互动模式
     */
    public static boolean IS_MULTI_INTERACT = false;
    /**
     * 多人 PK 模式
     */
    public static boolean IS_MULTI_PK = false;
    /**
     * 分辨率
     */
    public static AlivcResolutionEnum CONFIG_RESOLUTION = AlivcResolutionEnum.RESOLUTION_540P;

    /**
     * 音视频编码
     */
    public static boolean VIDEO_ENCODE_HARD = true;
    public static boolean AUDIO_ENCODE_HARD = true;

}
