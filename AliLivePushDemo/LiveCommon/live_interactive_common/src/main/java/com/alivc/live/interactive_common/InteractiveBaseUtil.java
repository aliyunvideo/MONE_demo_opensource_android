package com.alivc.live.interactive_common;

import com.alivc.live.player.annotations.AlivcLivePlayVideoStreamType;
import com.alivc.live.pusher.AlivcLiveMixSourceType;

/**
 * @author keria
 * @date 2023/10/30
 * @brief
 */
public class InteractiveBaseUtil {

    private InteractiveBaseUtil() {
    }

    public static AlivcLiveMixSourceType covertVideoStreamType2MixSourceType(AlivcLivePlayVideoStreamType videoStreamType) {
        if (videoStreamType == AlivcLivePlayVideoStreamType.STREAM_SCREEN) {
            return AlivcLiveMixSourceType.SOURCE_SCREEN;
        }
        return AlivcLiveMixSourceType.SOURCE_CAMERA;
    }

}
