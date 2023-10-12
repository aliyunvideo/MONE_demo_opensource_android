package com.alivc.live.interactive_live;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

/**
 * 以主播身份进入连麦互动界面的 Controller
 */
public class AnchorController {

    private final InteractLiveManager mInteractLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //观众连麦预览 View
    private FrameLayout mViewerRenderView;
    //主播推流地址
    private final String mPushUrl;
    //观众连麦拉流地址
    private String mPullUrl;
    private String mRoomId;
    private String mAnchorId;
    private boolean mEnableSpeakerPhone = false;

    public AnchorController(Context context, String roomId, String anchorId) {
        this.mRoomId = roomId;
        this.mAnchorId = anchorId;
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
        mPushUrl = URLUtils.generateInteractivePushUrl(roomId, anchorId);
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context);
    }

    public AnchorController(Context context, String interactiveURL) {
        this.mContext = context;
        mLocalStreamReader = new LocalStreamReader();
        mPushUrl = interactiveURL;
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context, true);
    }

    /**
     * 设置观众 id
     *
     * @param viewerId 观众 id
     */
    public void setViewerId(String viewerId) {
        mPullUrl = URLUtils.generateInteractivePullUrl(mRoomId, viewerId);
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
        externAV();
        mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl, true);
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
     *
     * @param viewerInfo 要连麦的观众信息
     * @param isUrl      如果 isUrl 是 false，viewerInfo 表示观众的Id，否则 viewerInfo 表示观众端拉流的 URL
     */
    public void startConnect(String viewerInfo, boolean isUrl) {
        if (isUrl) {
            mInteractLiveManager.setPullView(mViewerRenderView, false);
            mInteractLiveManager.startPull(viewerInfo);
        } else {
            setViewerId(viewerInfo);
            mInteractLiveManager.setPullView(mViewerRenderView, false);
            mInteractLiveManager.startPull(mPullUrl);
            mInteractLiveManager.setLiveMixTranscodingConfig(mAnchorId, viewerInfo);
        }
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
        mInteractLiveManager.clearLiveMixTranscodingConfig();
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

    public void muteLocalCamera(boolean muteLocalCamera) {
        mInteractLiveManager.muteLocalCamera(muteLocalCamera);
    }
}
