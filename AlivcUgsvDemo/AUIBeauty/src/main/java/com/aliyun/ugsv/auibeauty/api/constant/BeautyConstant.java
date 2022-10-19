package com.aliyun.ugsv.auibeauty.api.constant;

import java.util.HashMap;
import java.util.Map;

public class BeautyConstant {
    public final static int BEAUTY_INIT_SUCCEED = 0;
    public final static int BEAUTY_INIT_ERROR = -1;
    public static Map<BeautySDKType, String> beautyManagerImplMap = new HashMap<BeautySDKType, String>() {
        {
            put(BeautySDKType.RACE, "com.aliyun.svideo.beauty.race.manager.RaceManager");
            put(BeautySDKType.FACEUNITY, "com.aliyun.svideo.beauty.faceunity.FaceUnityManager");
            //put(BeautySDKType.QUEEN, "com.aliyun.svideo.beauty.queen.manager.QueenManager");
            put(BeautySDKType.QUEEN, "com.aliyun.svideo.beautyeffect.queen.QueenManager");
        }
    };

}
