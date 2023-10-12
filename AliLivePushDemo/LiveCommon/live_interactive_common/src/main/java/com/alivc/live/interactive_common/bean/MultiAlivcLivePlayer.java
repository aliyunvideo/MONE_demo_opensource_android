package com.alivc.live.interactive_common.bean;

import android.content.Context;
import android.util.Log;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.annotations.AlivcLiveNetworkQuality;
import com.alivc.live.interactive_common.listener.MultiInteractLivePushPullListener;
import com.alivc.live.player.AlivcLivePlayConfig;
import com.alivc.live.player.AlivcLivePlayInfoListener;
import com.alivc.live.player.AlivcLivePlayerImpl;
import com.alivc.live.player.AlivcLivePlayerStatsInfo;
import com.alivc.live.player.annotations.AlivcLivePlayError;

import java.nio.charset.StandardCharsets;

/**
 * AlivcLivePlayer 封装类，用于多人连麦互动
 */
public class MultiAlivcLivePlayer extends AlivcLivePlayerImpl implements AlivcLivePlayInfoListener{
    private static final String TAG = "MultiAlivcLivePlayer";

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
    public void onNetworkQualityChanged(AlivcLiveNetworkQuality upQuality, AlivcLiveNetworkQuality downQuality) {

    }

    public void onNetworkQualityChanged(AlivcLiveNetworkQuality quality) {
        Log.w(TAG, "onNetworkQualityChanged: "  + quality);
    }

    @Override
    public void onReceiveSEIMessage(int payload, byte[] data) {
        Log.d(TAG, "onReceiveSEIMessage: " + payload + ", " + new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void onPlayoutVolumeUpdate(int volume, boolean isSpeaking) {
        // 音频音量(仅互动模式下生效，需设置AlivcLivePusher#enableAudioVolumeIndication接口)
        // Log.d(TAG, "onPlayoutVolumeUpdate: " + volume + ", " + isSpeaking);
    }

    @Override
    public void onError(AlivcLivePlayError alivcLivePlayError, String s) {
        mIsPlaying = false;
        if(mListener != null){
            mListener.onPullError(mAudienceId,alivcLivePlayError,s);
        }
    }

    @Override
    public void onPlayerStatistics(AlivcLivePlayerStatsInfo statsInfo) {
        Log.i(TAG, "onPlayerStatistics: " + statsInfo);
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
