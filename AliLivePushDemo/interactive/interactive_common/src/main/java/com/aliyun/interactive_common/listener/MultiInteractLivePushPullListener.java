package com.aliyun.interactive_common.listener;

import com.alivc.live.player.annotations.AlivcLivePlayError;

public abstract class MultiInteractLivePushPullListener {

    public void onPullSuccess(String userKey){}
    public void onPullError(String userKey,AlivcLivePlayError errorType, String errorMsg){}
    public void onPullStop(String userKey){}
    public void onPushSuccess(){}
    public void onPushError(){}
    public void onConnectionLost(){}
}
