package com.alivc.live.interactive_common.utils;

import com.alivc.live.pusher.AlivcFpsEnum;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcVideoEncodeGopEnum;

public class LivePushGlobalConfig {

    /**
     * 多人互动模式/PK
     */
    public static boolean IS_MULTI_INTERACT = false;
    /**
     * 音视频编码
     */
    public static boolean VIDEO_ENCODE_HARD = true;
    public static boolean AUDIO_ENCODE_HARD = true;

    /**
     * 视频编码H265
     */
    public static boolean VIDEO_CODEC_H265 = false;

    /**
     * 美颜
     */
    public static boolean ENABLE_BEAUTY = true;

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

    /**
     * 分辨率
     */
    public static AlivcResolutionEnum RESOLUTION = AlivcResolutionEnum.RESOLUTION_540P;

    /**
     * 目标码率
     */
    public static int TARGET_RATE = AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate();

    /**
     * 最小 FPS
     */
    public static AlivcFpsEnum MIN_FPS = AlivcFpsEnum.FPS_8;

    /**
     * 采集帧率
     */
    public static AlivcFpsEnum FPS = AlivcFpsEnum.FPS_20;
}
