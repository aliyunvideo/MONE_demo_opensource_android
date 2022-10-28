package com.aliyun.interactive_pk.bean;

import android.content.Context;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.player.AlivcLivePlayInfoListener;
import com.alivc.live.player.AlivcLivePlayerImpl;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;

/**
 * AlivcLivePlayer 封装类，用于多人 PK
 */
public class MultiAlivcPKLivePlayer extends AlivcLivePlayerImpl implements AlivcLivePlayInfoListener{

    private MultiInteractLivePushPullListener mListener;
    private boolean mIsPlaying = false;
    private String mUserKey;

    public MultiAlivcPKLivePlayer(Context context, AlivcLiveMode mode) {
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
            mListener.onPullSuccess(mUserKey);
        }
    }

    @Override
    public void onPlayStopped() {
        mIsPlaying = false;
        if(mListener != null){
            mListener.onPullStop(mUserKey);
        }
    }

    @Override
    public void onFirstVideoFrameDrawn() {
    }

    @Override
    public void onError(AlivcLivePlayError alivcLivePlayError, String s) {
        mIsPlaying = false;
        if(mListener != null){
            mListener.onPullError(mUserKey,alivcLivePlayError,s);
        }
    }

    public boolean isPulling(){
        return mIsPlaying;
    }

    public void setUserKey(String userKey) {
        this.mUserKey = userKey;
    }

    public String getUserKey(){
        return mUserKey;
    }
}
