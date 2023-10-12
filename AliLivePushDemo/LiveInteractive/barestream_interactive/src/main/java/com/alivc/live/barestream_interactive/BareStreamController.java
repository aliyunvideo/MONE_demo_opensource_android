package com.alivc.live.barestream_interactive;

import android.content.Context;
import android.text.TextUtils;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

class BareStreamController {

    private final InteractiveBareStreamManager mInteractLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //观众连麦预览 View
    private FrameLayout mViewerRenderView;
    //主播推流地址
    private String mPushUrl;
    //观众连麦拉流地址
    private String mPullUrl;
    private boolean mEnableSpeakerPhone = false;

    public BareStreamController(Context context) {
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

    public void setPullUrl(String url) {
        this.mPullUrl = url;
    }

    /**
     * 设置主播预览 View
     *
     * @param frameLayout 主播预览 View
     */
    public void setAnchorRenderView(FrameLayout frameLayout) {
        this.mAnchorRenderView = frameLayout;
    }

    /**
     * 设置观众预览 View
     *
     * @param frameLayout 观众预览 View
     */
    public void setViewerRenderView(FrameLayout frameLayout) {
        this.mViewerRenderView = frameLayout;
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

    public void stopPreview() {
        mInteractLiveManager.stopPreview();
    }

    public void stopPush() {
        mInteractLiveManager.stopPush();
    }

    public void stopCamera() {
        mInteractLiveManager.stopCamera();
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
    public void startConnect() {
        mInteractLiveManager.setPullView(mViewerRenderView, false);
        mInteractLiveManager.startPull(mPullUrl);
    }

    /**
     * 主播是否正在连麦
     *
     * @return true:正在连麦  false:没有连麦
     */
    public boolean isOnConnected() {
        return mInteractLiveManager.isPulling();
    }

    public void switchCamera() {
        mInteractLiveManager.switchCamera();
    }

    public void resume() {
        mInteractLiveManager.resume();
    }

    public void pause() {
        mInteractLiveManager.pause();
    }

    public void release() {
        mInteractLiveManager.release();
        mLocalStreamReader.stopYUV();
        mLocalStreamReader.stopPcm();
    }

    /**
     * 结束连麦
     */
    public void stopConnect() {
        mInteractLiveManager.stopPull();
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener) {
        mInteractLiveManager.setInteractLivePushPullListener(listener);
    }

    public void setMute(boolean b) {
        mInteractLiveManager.setMute(b);
    }

    public void changeSpeakerPhone() {
        mEnableSpeakerPhone = !mEnableSpeakerPhone;
        mInteractLiveManager.enableSpeakerPhone(mEnableSpeakerPhone);
    }
}
