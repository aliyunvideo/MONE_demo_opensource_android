package com.alivc.live.barestream_interactive;


import com.alivc.live.interactive_common.InteractLiveBaseManager;

public class InteractiveBareStreamManager extends InteractLiveBaseManager {
    public void release() {
        stopPullCDNStream();
        super.release();
    }
}
