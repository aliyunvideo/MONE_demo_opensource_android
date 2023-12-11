package com.alivc.live.interactive_live;

import android.content.Context;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.InteractiveMode;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import java.io.File;

/**
 * 以观众身份进入连麦互动界面的 Controller
 */
public class ViewerController {

    private final InteractLiveManager mInteractLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;

    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //观众连麦预览 View
    private FrameLayout mViewerRenderView;

    //主播信息
    private InteractiveUserData mAnchorUserData;
    private InteractiveUserData mViewerUserData;

    //主播连麦拉流地址
    private String mPullRTCUrl;
    // 主播CDN拉流地址
    private String mPullCDNUrl;

    private boolean mEnableSpeakerPhone = false;
    private boolean mNeedPullOtherStream = false;

    public ViewerController(Context context, InteractiveUserData anchorUserData, InteractiveUserData viewerUserData) {
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
        mAnchorUserData = anchorUserData;
        mViewerUserData = viewerUserData;
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context, InteractiveMode.INTERACTIVE);
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
     * 设置观看主播预览 View
     *
     * @param surfaceView 观看主播预览的 View
     */
    public void setAnchorCDNRenderView(SurfaceView surfaceView) {
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                mInteractLiveManager.setPullView(surfaceHolder);
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                mInteractLiveManager.setPullView(null);
            }
        });
    }

    /**
     * 观看直播
     *
     * @param anchorId 观看的主播 id
     */
    public void watchLive(String anchorId) {
        mAnchorUserData.userId = anchorId;

        // 旁路观看主播拉流地址（CDN拉流地址）
        mPullCDNUrl = URLUtils.generateCDNPullUrl(mAnchorUserData.channelId, anchorId, LivePushGlobalConfig.mAlivcLivePushConfig.isAudioOnly());
        mInteractLiveManager.startPullCDNStream(mPullCDNUrl);

        // 连麦观看主播拉流地址（RTC拉流地址）
        mPullRTCUrl = URLUtils.generateInteractivePullUrl(mAnchorUserData.channelId, anchorId);
        mAnchorUserData.url = mPullRTCUrl;

        mNeedPullOtherStream = false;
    }

    /**
     * 观众连麦主播
     */
    public void startConnect() {
        externAV();
        //停止 cdn 拉流
        mInteractLiveManager.stopPullCDNStream();

        //观众连麦推流
        mInteractLiveManager.startPreviewAndPush(mViewerUserData, mViewerRenderView, false);

        mNeedPullOtherStream = true;
    }

    // 先推后拉
    public void pullOtherStream() {
        if (mNeedPullOtherStream) {
            //连麦拉流
            mInteractLiveManager.setPullView(mAnchorUserData, mAnchorRenderView, true);
            mInteractLiveManager.startPullRTCStream(mAnchorUserData);
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
     * 结束连麦
     */
    public void stopConnect() {
        //观众停止推流
        mInteractLiveManager.stopPush();
        //大屏重新 CDN 流
        mInteractLiveManager.stopPullRTCStream(mAnchorUserData);
        mInteractLiveManager.startPullCDNStream(mPullCDNUrl);
    }

    public boolean isPushing() {
        return mInteractLiveManager.isPushing();
    }

    public void resume() {
        mInteractLiveManager.resumePush();
        mInteractLiveManager.resumePlayRTCStream(mAnchorUserData);
    }

    public void pause() {
        mInteractLiveManager.pausePush();
        mInteractLiveManager.pausePlayRTCStream(mAnchorUserData);
    }

    public void switchCamera() {
        mInteractLiveManager.switchCamera();
    }

    /**
     * 是否有主播 id
     */
    public boolean hasAnchorId() {
        return mAnchorUserData != null && !TextUtils.isEmpty(mAnchorUserData.userId);
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener) {
        mInteractLiveManager.setInteractLivePushPullListener(listener);
    }

    public void release() {
        mInteractLiveManager.release();
        mInteractLiveManager.setInteractLivePushPullListener(null);
        mLocalStreamReader.stopYUV();
        mLocalStreamReader.stopPcm();
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
