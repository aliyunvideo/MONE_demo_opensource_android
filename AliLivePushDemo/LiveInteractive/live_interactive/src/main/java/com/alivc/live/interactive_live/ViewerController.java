package com.alivc.live.interactive_live;

import android.content.Context;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.alivc.live.commonbiz.ReadFileData;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;

import java.io.File;

/**
 * 以观众身份进入连麦互动界面的 Controller
 */
public class ViewerController {

    private final InteractLiveManager mInteractLiveManager;
    private final Context mContext;
    private final ReadFileData mReadFileData;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //观众连麦预览 View
    private FrameLayout mViewerRenderView;
    //观众连麦推流地址
    private String mPushUrl;
    //主播连麦拉流地址
    private String mPullUrl;
    private final String mRoomId;
    private String mCDNPullUrl;
    private String mAnchorId;
    private boolean mEnableSpeakerPhone = false;

    public ViewerController(Context context, String roomId, String viewId) {
        this.mRoomId = roomId;
        this.mContext = context;
        mReadFileData = new ReadFileData();
        mPushUrl = URLUtils.generateInteractivePushUrl(roomId, viewId);
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context);
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
            }
        });
    }

    /**
     * 观看直播
     *
     * @param anchorId 观看的主播 id
     */
    public void watchLive(String anchorId) {
        this.mAnchorId = anchorId;
        // 旁路观看主播拉流地址（CDN拉流地址）
        mCDNPullUrl = URLUtils.generateCDNPullUrl(mRoomId, anchorId, LivePushGlobalConfig.IS_AUDIO_ONLY);
        // 连麦观看主播拉流地址（RTC拉流地址）
        mPullUrl = URLUtils.generateInteractivePullUrl(mRoomId, anchorId);
        mInteractLiveManager.startPull(mCDNPullUrl);
    }

    /**
     * 观众连麦主播
     */
    public void startConnect() {
        externAV();
        //停止 cdn 拉流
        mInteractLiveManager.stopCDNPull();
        //连麦拉流
        mInteractLiveManager.setPullView(mAnchorRenderView, true);
        mInteractLiveManager.startPull(mPullUrl);
        //观众连麦推流
        mInteractLiveManager.startPreviewAndPush(mViewerRenderView, mPushUrl, false);
    }

    private void externAV() {
        if (LivePushGlobalConfig.ENABLE_EXTERN_AV) {
            File yuvFile = ResourcesDownload.localCaptureYUVFilePath(mContext);
            mReadFileData.readYUVData(yuvFile, (buffer, pts) -> mInteractLiveManager.inputStreamVideoData(buffer, 720, 1280, 720, 1280 * 720 * 3 / 2, pts, 0));
            File pcmFile = new File(mContext.getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
            mReadFileData.readPCMData(pcmFile, (buffer, length, pts) -> mInteractLiveManager.inputStreamAudioData(buffer, length, 44100, 1, pts));
        }
    }

    /**
     * 结束连麦
     */
    public void stopConnect() {
        //观众停止推流
        mInteractLiveManager.stopPush();
        //大屏重新 CDN 流
        mInteractLiveManager.stopPull();
        mInteractLiveManager.startPull(mCDNPullUrl);
    }

    public boolean isPushing() {
        return mInteractLiveManager.isPushing();
    }

    public void resume() {
        mInteractLiveManager.resume();
    }

    public void pause() {
        mInteractLiveManager.pause();
    }

    public void switchCamera() {
        mInteractLiveManager.switchCamera();
    }

    /**
     * 是否有主播 id
     */
    public boolean hasAnchorId() {
        return !TextUtils.isEmpty(mAnchorId);
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener) {
        mInteractLiveManager.setInteractLivePushPullListener(listener);
    }

    public void release() {
        mInteractLiveManager.release();
        mInteractLiveManager.setInteractLivePushPullListener(null);
        mReadFileData.stopYUV();
        mReadFileData.stopPcm();
    }

    public void setMute(boolean b) {
        mInteractLiveManager.setMute(b);
    }

    public void changeSpeakerPhone() {
        mEnableSpeakerPhone = !mEnableSpeakerPhone;
        mInteractLiveManager.enableSpeakerPhone(mEnableSpeakerPhone);
    }
}
