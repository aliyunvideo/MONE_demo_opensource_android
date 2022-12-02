package com.aliyun.interactive_common.bean;

import android.content.Context;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.player.AlivcLivePlayInfoListener;
import com.alivc.live.player.AlivcLivePlayerImpl;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;

/**
 * AlivcLivePlayer 封装类，用于多人连麦互动
 */
public class MultiAlivcLivePlayer extends AlivcLivePlayerImpl implements AlivcLivePlayInfoListener{

    private MultiInteractLivePushPullListener mListener;
    private boolean mIsPlaying = false;
    private String mAudienceId;

    public MultiAlivcLivePlayer(Context context, AlivcLiveMode mode) {
        super(context, mode);
        setPlayInfoListener(this);
    }

    public void setMultiInteractPlayInfoListener(MultiInteractLivePushPullListener listener){
        this.mListener = listener;
    }

    @Override
    public void onPlayStarted() {
        mIsPlaying = true;
        if(mListener != null){
            mListener.onPullSuccess(mAudienceId);
        }
    }

    @Override
    public void onPlayStopped() {
        mIsPlaying = false;
        if(mListener != null){
            mListener.onPullStop(mAudienceId);
        }
    }

    @Override
    public void onFirstVideoFrameDrawn() {
    }

    @Override
    public void onError(AlivcLivePlayError alivcLivePlayError, String s) {
        mIsPlaying = false;
        if(mListener != null){
            mListener.onPullError(mAudienceId,alivcLivePlayError,s);
        }
    }

    public boolean isPulling(){
        return mIsPlaying;
    }

    public void setAudienceId(String audienceId) {
        this.mAudienceId = audienceId;
    }

    public String getAudienceId(){
        return mAudienceId;
    }
}
