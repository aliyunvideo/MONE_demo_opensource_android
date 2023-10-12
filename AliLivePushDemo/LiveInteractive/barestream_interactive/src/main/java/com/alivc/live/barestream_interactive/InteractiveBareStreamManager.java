package com.alivc.live.barestream_interactive;


import com.alivc.live.interactive_common.InteractLiveBaseManager;
import com.alivc.live.pusher.AlivcVideoEncodeGopEnum;

public class InteractiveBareStreamManager extends InteractLiveBaseManager {

    public void release() {
        stopCDNPull();
        super.release();
    }
}
