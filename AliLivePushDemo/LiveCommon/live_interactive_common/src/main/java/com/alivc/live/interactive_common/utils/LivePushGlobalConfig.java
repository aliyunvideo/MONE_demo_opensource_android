package com.alivc.live.interactive_common.utils;

import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcVideoEncodeGopEnum;

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

    /**
     * 纯音频
     */
    public static boolean IS_AUDIO_ONLY = false;

    /**
     * 主播端 GOP
     */
    public static AlivcVideoEncodeGopEnum GOP = AlivcVideoEncodeGopEnum.GOP_ONE;

    /**
     * 外部音视频
     */
    public static boolean ENABLE_EXTERN_AV = false;
}
