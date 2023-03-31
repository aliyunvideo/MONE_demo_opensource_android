package com.alivc.live.interactive_live;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.ReadFileData;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.listener.MultiInteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;

import java.io.File;

/**
 * 以主播身份进入多人连麦互动界面的 Controller
 */
public class MultiAnchorController {

    private final InteractLiveManager mInteractLiveManager;
    private final Context mContext;
    private final ReadFileData mReadFileData;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //主播推流地址
    private final String mPushUrl;
    //观众连麦拉流地址
    private String mPullUrl;
    private final String mRoomId;
    private final String mAnchorId;
    private boolean mEnableSpeakerPhone = false;

    public MultiAnchorController(Context context, String roomId, String anchorId) {
        this.mRoomId = roomId;
        this.mAnchorId = anchorId;
        this.mContext = context;
        mReadFileData = new ReadFileData();
        mPushUrl = URLUtils.generateInteractivePushUrl(roomId, anchorId);
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context);
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
     * 开始直播
     */
    public void startPush() {
        externAV();
        mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl, true);
        mInteractLiveManager.addAnchorMixTranscodingConfig(mAnchorId);
    }

    /**
     * 开始连麦
     *
     * @param viewerId 要连麦的观众 id
     */
    public void startConnect(String viewerId, FrameLayout frameLayout) {
        setViewerId(viewerId);
        mInteractLiveManager.setPullView(viewerId, frameLayout, false);
        mInteractLiveManager.startPull(viewerId, mPullUrl);
        mInteractLiveManager.addAudienceMixTranscodingConfig(viewerId, frameLayout);
    }

    public boolean isOnConnected(String key) {
        return mInteractLiveManager.isPulling(key);
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
    public void stopConnect(String key) {
        mInteractLiveManager.stopPull(key);
        mInteractLiveManager.removeAudienceLiveMixTranscodingConfig(key, mAnchorId);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mInteractLiveManager.switchCamera();
    }

    /**
     * 创建 AlivcLivePlayer
     */
    public boolean createAlivcLivePlayer(String audienceId) {
        return mInteractLiveManager.createAlivcLivePlayer(audienceId);
    }

    public void resume() {
        mInteractLiveManager.resume();
    }

    public void pause() {
        mInteractLiveManager.pause();
    }

    public void release() {
        mInteractLiveManager.release();
        mReadFileData.stopYUV();
        mReadFileData.stopPcm();
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
