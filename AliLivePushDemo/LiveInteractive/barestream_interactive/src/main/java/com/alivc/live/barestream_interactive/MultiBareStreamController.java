package com.alivc.live.barestream_interactive;

import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.interactive_common.listener.MultiInteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

class MultiBareStreamController {

    private final InteractiveBareStreamManager mInteractLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //主播推流地址
    private String mPushUrl;
    //观众连麦拉流地址
    private boolean mEnableSpeakerPhone = false;

    public MultiBareStreamController(Context context) {
        this.mContext = context;
        AlivcResolutionEnum resolution = LivePushGlobalConfig.mAlivcLivePushConfig.getResolution();
        int width = AlivcResolutionEnum.getResolutionWidth(resolution, LivePushGlobalConfig.mAlivcLivePushConfig.getLivePushMode());
        int height = AlivcResolutionEnum.getResolutionHeight(resolution, LivePushGlobalConfig.mAlivcLivePushConfig.getLivePushMode());
        mLocalStreamReader = new LocalStreamReader.Builder()
                .setVideoWith(width)
                .setVideoHeight(height)
                .setVideoStride(width)
                .setVideoSize(height * width * 3 / 2)
                .setVideoRotation(0)
                .setAudioSampleRate(44100)
                .setAudioChannel(1)
                .setAudioBufferSize(2048)
                .build();
        mInteractLiveManager = new InteractiveBareStreamManager();
        mInteractLiveManager.init(context, true);
    }

    public void setPushUrl(String url) {
        this.mPushUrl = url;
    }

    public void pauseVideoPlaying(String key) {
        mInteractLiveManager.pause(key);
    }

    public void resumeVideoPlaying(String key) {
        mInteractLiveManager.resume(key);
    }

    public void setPullView(String key, FrameLayout frameLayout) {
        mInteractLiveManager.setPullView(key, frameLayout, true);
    }

    /**
     * 设置主播预览 View
     *
     * @param frameLayout 主播预览 View
     */
    public void setAnchorRenderView(FrameLayout frameLayout) {
        this.mAnchorRenderView = frameLayout;
    }

    public void stopPush() {
        mInteractLiveManager.stopPush();
    }

    /**
     * 开始直播
     */
    public void startPush() {
        if (!TextUtils.isEmpty(mPushUrl)) {
            externAV();
            mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl, true);
        }
    }

    private void externAV() {
        if (LivePushGlobalConfig.mAlivcLivePushConfig.isExternMainStream()) {
            File yuvFile = ResourcesConst.localCaptureYUVFilePath(mContext);
            mLocalStreamReader.readYUVData(yuvFile, (buffer, pts, videoWidth, videoHeight, videoStride, videoSize, videoRotation) -> {
                mInteractLiveManager.inputStreamVideoData(buffer, videoWidth, videoHeight, videoStride, videoSize, pts, videoRotation);
            });
            File pcmFile = ResourcesConst.localCapturePCMFilePath(mContext);
            mLocalStreamReader.readPCMData(pcmFile, (buffer, length, pts, audioSampleRate, audioChannel) -> {
                mInteractLiveManager.inputStreamAudioData(buffer, length, audioSampleRate, audioChannel, pts);
            });
        }
    }

    /**
     * 开始连麦
     */
    public void startConnect(String key, String url, FrameLayout mRenderView) {
        mInteractLiveManager.createAlivcLivePlayer(key);
        mInteractLiveManager.setPullView(key, mRenderView, false);
        mInteractLiveManager.startPull(key, url);
    }

    /**
     * 结束连麦
     */
    public void stopConnect(String key) {
        mInteractLiveManager.stopPull(key);
    }

    public void switchCamera() {
        mInteractLiveManager.switchCamera();
    }

    public void release() {
        mInteractLiveManager.release();
        mLocalStreamReader.stopYUV();
        mLocalStreamReader.stopPcm();
    }

    public void setMultiInteractLivePushPullListener(MultiInteractLivePushPullListener listener) {
        mInteractLiveManager.setMultiInteractLivePushPullListener(listener);
    }

    public void setMute(boolean b) {
        mInteractLiveManager.setMute(b);
    }

    public void changeSpeakerPhone() {
        mEnableSpeakerPhone = !mEnableSpeakerPhone;
        mInteractLiveManager.enableSpeakerPhone(mEnableSpeakerPhone);
    }
}
