package com.alivc.live.pusher.demo.interactlive;

import android.widget.FrameLayout;

import com.alivc.live.pusher.util.URLUtils;

/**
 * 以主播身份进入连麦互动界面的 Controller
 */
public class AnchorController {

    private final InteractLiveManager mInteractLiveManager;
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

    public AnchorController(String roomId, String anchorId) {
        this.mRoomId = roomId;
        this.mAnchorId = anchorId;
        mPushUrl = URLUtils.generatePushUrl(roomId, anchorId, 1);
        mInteractLiveManager = InteractLiveManager.getInstance();
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
        mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl,true);
    }

    /**
     * 开始连麦
     *
     * @param viewerId 要连麦的观众 id
     */
    public void startConnect(String viewerId) {
        setViewerId(viewerId);
        mInteractLiveManager.setPullView(mViewerRenderView,false);
        mInteractLiveManager.startPull(mPullUrl);
        mInteractLiveManager.setLiveMixTranscodingConfig(mAnchorId,viewerId);
    }

    /**
     * 主播是否正在连麦
     *
     * @return true:正在连麦  false:没有连麦
     */
    public boolean isOnConnected() {
        return mInteractLiveManager.isPulling();
    }

    /**
     * 结束连麦
     */
    public void stopConnect() {
        mInteractLiveManager.stopPull();
        mInteractLiveManager.clearLiveMixTranscodingConfig();
    }
}
