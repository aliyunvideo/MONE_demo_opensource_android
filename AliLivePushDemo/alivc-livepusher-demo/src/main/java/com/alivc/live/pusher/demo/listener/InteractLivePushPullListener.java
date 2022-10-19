package com.alivc.live.pusher.demo.listener;

import com.alivc.live.player.annotations.AlivcLivePlayError;

public abstract class InteractLivePushPullListener {

    public void onPullSuccess(){}
    public void onPullError(AlivcLivePlayError errorType, String errorMsg){}
    public void onPullStop(){}
    public void onPushSuccess(){}
    public void onPushError(){}
}
