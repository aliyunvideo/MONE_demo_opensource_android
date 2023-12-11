package com.alivc.live.interactive_common.bean;

import android.content.Context;
import android.util.Log;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.annotations.AlivcLiveNetworkQuality;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.player.AlivcLivePlayInfoListener;
import com.alivc.live.player.AlivcLivePlayerImpl;
import com.alivc.live.player.AlivcLivePlayerStatsInfo;
import com.alivc.live.player.annotations.AlivcLivePlayError;

/**
 * AlivcLivePlayer 封装类，用于直播连麦拉流
 */
public class InteractiveLivePlayer extends AlivcLivePlayerImpl implements AlivcLivePlayInfoListener {
    private static final String TAG = InteractiveLivePlayer.class.getSimpleName();

    private InteractLivePushPullListener mListener;
    private boolean mIsPulling = false;

    private InteractiveUserData mPullUserData;

    public InteractiveLivePlayer(Context context, AlivcLiveMode mode) {
        super(context, mode);
        setPlayInfoListener(this);
    }

    public void setMultiInteractPlayInfoListener(InteractLivePushPullListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onPlayStarted() {
        Log.i(TAG, "onPlayStarted: " + mPullUserData);
        mIsPulling = true;
        if (mListener != null) {
            mListener.onPullSuccess(mPullUserData);
        }
    }

    @Override
    public void onPlayStopped() {
        Log.i(TAG, "onPlayStopped: " + mPullUserData);
        mIsPulling = false;
        if (mListener != null) {
            mListener.onPullStop(mPullUserData);
        }
    }

    @Override
    public void onFirstVideoFrameDrawn() {
    }

    @Override
    public void onNetworkQualityChanged(AlivcLiveNetworkQuality upQuality, AlivcLiveNetworkQuality downQuality) {
        Log.w(TAG, "onNetworkQualityChanged: " + upQuality + ", " + downQuality);
    }

    @Override
    public void onReceiveSEIMessage(int payload, byte[] data) {
        //Log.d(TAG, "onReceiveSEIMessage: " + payload + ", " + new String(data, StandardCharsets.UTF_8));
    }

    @Override
    public void onPlayoutVolumeUpdate(int volume, boolean isSpeaking) {
        // 音频音量(仅互动模式下生效，需设置AlivcLivePusher#enableAudioVolumeIndication接口)
        // Log.d(TAG, "onPlayoutVolumeUpdate: " + volume + ", " + isSpeaking);
    }

    @Override
    public void onAudioMuted(boolean mute) {
        ToastUtils.show("onAudioMuted: " + mute);
    }

    @Override
    public void onVideoMuted(boolean mute) {
        ToastUtils.show("onVideoMuted: " + mute);
    }

    @Override
    public void onVideoEnabled(boolean enable) {
        ToastUtils.show("onVideoEnabled: " + enable);
    }

    @Override
    public void onVideoResolutionChanged(int width, int height) {
    }

    @Override
    public void onError(AlivcLivePlayError alivcLivePlayError, String s) {
        Log.e(TAG, "onError: " + mPullUserData + ", " + alivcLivePlayError + ", " + s);
        mIsPulling = false;
        if (mListener != null) {
            mListener.onPullError(mPullUserData, alivcLivePlayError, s);
        }
    }

    @Override
    public void onPlayerStatistics(AlivcLivePlayerStatsInfo statsInfo) {
        //Log.i(TAG, "onPlayerStatistics: " + statsInfo);
    }

    public boolean isPulling() {
        return mIsPulling;
    }

    public void setPullUserData(InteractiveUserData userData) {
        this.mPullUserData = userData;
    }
}
