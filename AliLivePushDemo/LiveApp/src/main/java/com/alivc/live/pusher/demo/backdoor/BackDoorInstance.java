package com.alivc.live.pusher.demo.backdoor;

import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.alivc.live.commonutils.ContextUtils;

/**
 * @author keria
 * @date 2023/10/13
 * @brief
 */
public class BackDoorInstance {

    private boolean showBareStream;

    private BackDoorInstance() {
        showBareStream = SharedPreferenceUtils.getBareStream(ContextUtils.getContext());
    }

    public static BackDoorInstance getInstance() {
        return Inner.instance;
    }

    public boolean isShowBareStream() {
        return this.showBareStream;
    }

    public void setShowBareStream(boolean showBareStream) {
        this.showBareStream = showBareStream;
        SharedPreferenceUtils.setBareStream(ContextUtils.getContext(), showBareStream);
    }

    private static class Inner {
        private static final BackDoorInstance instance = new BackDoorInstance();
    }
}
