package com.alivc.live.interactive_common.utils;

import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.WaterMarkInfo;

import java.util.ArrayList;

public class LivePushGlobalConfig {

    /**
     * 多人互动模式/PK
     */
    public static boolean IS_MULTI_INTERACT = false;

    /**
     * H5兼容模式（可与web连麦互通）
     */
    public static boolean IS_H5_COMPATIBLE = false;

    public static AlivcLivePushConfig mAlivcLivePushConfig = new AlivcLivePushConfig();

    /**
     * 美颜
     */
    public static boolean ENABLE_BEAUTY = true;
    /**
     * 本地日志
     */
    public static boolean ENABLE_LOCAL_LOG = true;

    /**
     * 水印
     */
    public static boolean ENABLE_WATER_MARK = false;

    /**
     * 水印
     */
    public static ArrayList<WaterMarkInfo> mWaterMarkInfos = new ArrayList<>();

}
