package com.aliyun.interactive_live;

import android.widget.FrameLayout;

import com.aliyun.interactive_common.utils.URLUtils;

/**
 * 以主播身份进入多人连麦互动界面的 Controller
 */
public class MultiAnchorController {

    private final InteractLiveManager mInteractLiveManager;
    //主播预览 View
    private FrameLayout mAnchorRenderView;
    //主播推流地址
    private final String mPushUrl;
    //观众连麦拉流地址
    private String mPullUrl;
    private final String mRoomId;
    private final String mAnchorId;

    public MultiAnchorController(String roomId, String anchorId) {
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
     * 开始直播
     */
    public void startPush() {
        mInteractLiveManager.startPreviewAndPush(mAnchorRenderView, mPushUrl,true);
        mInteractLiveManager.addAnchorMixTranscodingConfig(mAnchorId);
    }

    /**
     * 开始连麦
     *
     * @param viewerId 要连麦的观众 id
     */
    public void startConnect(String viewerId,FrameLayout frameLayout) {
        setViewerId(viewerId);
        mInteractLiveManager.setPullView(viewerId,frameLayout,false);
        mInteractLiveManager.startPull(viewerId,mPullUrl);
        mInteractLiveManager.addAudienceMixTranscodingConfig(viewerId,frameLayout);
    }

    public boolean isOnConnected(String key) {
        return mInteractLiveManager.isPulling(key);
    }

    /**
     * 结束连麦
     */
    public void stopConnect(String key) {
        mInteractLiveManager.stopPull(key);
        mInteractLiveManager.removeAudienceLiveMixTranscodingConfig(key,mAnchorId);
    }
}
