package com.alivc.live.interactive_common.listener;

import com.alivc.live.player.annotations.AlivcLivePlayError;

public abstract class InteractLivePushPullListener {

    public void onPullSuccess(){}
    public void onPullError(AlivcLivePlayError errorType, String errorMsg){}
    public void onPullStop(){}
    public void onPushSuccess(){}
    public void onPushError(){}
    public void onConnectionLost(){}
    public void onReceiveSEIMessage(int payload, byte[] data) {}

    public void onPlayerSei(int i, byte[] bytes) {}
}
