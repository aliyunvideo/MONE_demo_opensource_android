package com.alivc.live.pusher.demo.interactlive;

import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.pusher.demo.listener.InteractLivePushPullListener;
import com.alivc.live.pusher.util.URLUtils;

/**
 * 以观众身份进入连麦互动界面的 Controller
 */
public class ViewerController extends InteractLivePushPullListener {

    private final InteractLiveManager mInteractLiveManager;
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

    public ViewerController(String roomId, String viewId) {
        this.mRoomId = roomId;
        mPushUrl = URLUtils.generatePushUrl(roomId, viewId, 1);
        mInteractLiveManager = InteractLiveManager.getInstance();

        mInteractLiveManager.setInteractLivePushPullListener(this);
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
     * 观看直播(CDN URL)
     *
     * @param anchorId 观看的主播 id
     */
    public void watchLive(String anchorId) {
        this.mAnchorId = anchorId;
        //观看主播拉流地址
        mCDNPullUrl = URLUtils.generateCDNUrl(mRoomId, anchorId, 0);
        mPullUrl = URLUtils.generatePullUrl(mRoomId, anchorId, 1);
        mInteractLiveManager.startPull(mCDNPullUrl);
    }

    /**
     * 观众连麦主播
     */
    public void startConnect() {
        //停止 cdn 拉流
        mInteractLiveManager.stopCDNPull();
        //连麦拉流
        mInteractLiveManager.setPullView(mAnchorRenderView,true);
        mInteractLiveManager.startPull(mPullUrl);
        //观众连麦推流
        mInteractLiveManager.startPreviewAndPush(mViewerRenderView, mPushUrl,false);
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

    /**
     * 是否有主播 id
     */
    public boolean hasAnchorId(){
        return !TextUtils.isEmpty(mAnchorId);
    }

    public void release(){
        mInteractLiveManager.setInteractLivePushPullListener(null);
    }


    @Override
    public void onPullSuccess() {
        super.onPullSuccess();
    }

    @Override
    public void onPullError(AlivcLivePlayError errorType, String errorMsg) {
        super.onPullError(errorType, errorMsg);
    }

    @Override
    public void onPushSuccess() {
        super.onPushSuccess();
    }

    @Override
    public void onPushError() {
        super.onPushError();
    }
}
