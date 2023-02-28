package com.alivc.live.interactive_live;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.ReadFileData;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;

import java.io.File;

/**
 * 以主播身份进入连麦互动界面的 Controller
 */
public class AnchorController {

    private final InteractLiveManager mInteractLiveManager;
    private final Context mContext;
    private final ReadFileData mReadFileData;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //观众连麦预览 View
    private FrameLayout mViewerRenderView;
    //主播推流地址
    private final String mPushUrl;
    //观众连麦拉流地址
    private String mPullUrl;
    private final String mRoomId;
    private final String mAnchorId;

    public AnchorController(Context context, String roomId, String anchorId) {
        this.mRoomId = roomId;
        this.mAnchorId = anchorId;
        this.mContext = context;
        mReadFileData = new ReadFileData();
        mPushUrl = URLUtils.generatePushUrl(roomId, anchorId, 1);
        mInteractLiveManager = new InteractLiveManager();
        mInteractLiveManager.init(context);
    }

    /**
     * 设置观众 id
     *
     * @param viewerId 观众 id
     */
    public void setViewerId(String viewerId) {
        mPullUrl = URLUtils.generatePullUrl(mRoomId, viewerId, 1);
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
        mInteractLiveManager.setGOP(LivePushGlobalConfig.GOP);
        mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl, true);
    }

    private void externAV() {
        if (LivePushGlobalConfig.ENABLE_EXTERN_AV) {
            File yuvFile = ResourcesDownload.localCaptureYUVFilePath(mContext);
            mReadFileData.readYUVData(yuvFile, (buffer, pts) ->
                    mInteractLiveManager.inputStreamVideoData(buffer, 720, 1280, 720, 1280 * 720 * 3 / 2, pts, 0)
            );
            File pcmFile = new File(mContext.getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
            mReadFileData.readPCMData(pcmFile, (buffer, length, pts) -> mInteractLiveManager.inputStreamAudioData(buffer, length, 44100, 1, pts));
        }
    }

    /**
     * 开始连麦
     *
     * @param viewerId 要连麦的观众 id
     */
    public void startConnect(String viewerId) {
        setViewerId(viewerId);
        mInteractLiveManager.setPullView(mViewerRenderView, false);
        mInteractLiveManager.startPull(mPullUrl);
        mInteractLiveManager.setLiveMixTranscodingConfig(mAnchorId, viewerId);
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
        mReadFileData.stopYUV();
        mReadFileData.stopPcm();
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
}
