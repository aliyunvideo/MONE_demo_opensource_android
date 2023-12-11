package com.alivc.live.interactive_common.listener;

import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.player.annotations.AlivcLivePlayError;

public abstract class InteractLivePushPullListener {
    public void onPullSuccess(InteractiveUserData userData) {}
    public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {}
    public void onPullStop(InteractiveUserData userData) {}
    public void onPushSuccess(){}
    public void onPushError(){}
    public void onConnectionLost(){}
    public void onReceiveSEIMessage(int payload, byte[] data) {}
    public void onPlayerSei(int i, byte[] bytes) {}
}
