package com.aliyun.interactive_live;

import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;
import com.aliyun.interactive_common.InteractLiveBaseManager;

import java.util.ArrayList;

public class InteractLiveManager extends InteractLiveBaseManager {

    /**
     * 设置 CDN 拉流时，渲染的 Surface
     *
     * @param surfaceHolder 播放器渲染画面的 Surface
     */
    public void setPullView(SurfaceHolder surfaceHolder) {
        mAliPlayer.setDisplay(surfaceHolder);
    }

    /**
     * 设置混流
     *
     * @param anchorId 主播 id
     * @param audience 观众 id
     */
    public void setLiveMixTranscodingConfig(String anchorId, String audience) {
        if (TextUtils.isEmpty(anchorId) && TextUtils.isEmpty(audience)) {
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
            return;
        }
        AlivcLiveTranscodingConfig transcodingConfig = new AlivcLiveTranscodingConfig();
        AlivcLiveMixStream anchorMixStream = new AlivcLiveMixStream();
        if (mAlivcLivePushConfig != null) {
            anchorMixStream.setUserId(anchorId);
            anchorMixStream.setX(0);
            anchorMixStream.setY(0);
            anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth());
            anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight());
            anchorMixStream.setZOrder(1);
            Log.d(TAG, "AlivcRTC anchorMixStream --- " + anchorMixStream.getUserId() + ", " + anchorMixStream.getWidth() + ", " + anchorMixStream.getHeight()
                    + ", " + anchorMixStream.getX() + ", " + anchorMixStream.getY() + ", " + anchorMixStream.getZOrder());
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        if (mAudienceFrameLayout != null) {
            audienceMixStream.setUserId(audience);
            audienceMixStream.setX((int) mAudienceFrameLayout.getX() / 3);
            audienceMixStream.setY((int) mAudienceFrameLayout.getY() / 3);
            audienceMixStream.setWidth(mAudienceFrameLayout.getWidth() / 2);
            audienceMixStream.setHeight(mAudienceFrameLayout.getHeight() / 2);

            audienceMixStream.setZOrder(2);
            Log.d(TAG, "AlivcRTC audienceMixStream --- " + audienceMixStream.getUserId() + ", " + audienceMixStream.getWidth() + ", " + audienceMixStream.getHeight()
                    + ", " + audienceMixStream.getX() + ", " + audienceMixStream.getY() + ", " + audienceMixStream.getZOrder());
        }
        ArrayList<AlivcLiveMixStream> mixStreams = new ArrayList<>();
        mixStreams.add(anchorMixStream);
        mixStreams.add(audienceMixStream);
        transcodingConfig.setMixStreams(mixStreams);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(transcodingConfig);
        }
    }

    /**
     * 添加混流
     *
     * @param anchorId 主播 id
     */
    public void addAnchorMixTranscodingConfig(String anchorId) {
        if (TextUtils.isEmpty(anchorId)) {
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
            return;
        }

        AlivcLiveMixStream anchorMixStream = new AlivcLiveMixStream();
        if (mAlivcLivePushConfig != null) {
            anchorMixStream.setUserId(anchorId);
            anchorMixStream.setX(0);
            anchorMixStream.setY(0);
            anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth());
            anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight());
            anchorMixStream.setZOrder(1);
        }

        mMultiInteractLiveMixStreamsArray.add(anchorMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    /**
     * 添加混流配置
     *
     * @param audience    观众 id
     * @param frameLayout 观众 frameLayout(渲染 View 的 ViewGroup，用于计算混流位置)
     */
    public void addAudienceMixTranscodingConfig(String audience, FrameLayout frameLayout) {
        if (TextUtils.isEmpty(audience)) {
            return;
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        audienceMixStream.setUserId(audience);
        audienceMixStream.setX((int) frameLayout.getX() / 2);
        audienceMixStream.setY((int) frameLayout.getY() / 3);
        audienceMixStream.setWidth(frameLayout.getWidth() / 2);
        audienceMixStream.setHeight(frameLayout.getHeight() / 2);
        audienceMixStream.setZOrder(2);
        Log.d(TAG, "AlivcRTC audienceMixStream --- " + audienceMixStream.getUserId() + ", " + audienceMixStream.getWidth() + ", " + audienceMixStream.getHeight()
                + ", " + audienceMixStream.getX() + ", " + audienceMixStream.getY() + ", " + audienceMixStream.getZOrder());
        mMultiInteractLiveMixStreamsArray.add(audienceMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    /**
     * 移除混流
     *
     * @param audience 观众 id
     * @param anchorId 主播 id
     */
    public void removeAudienceLiveMixTranscodingConfig(String audience, String anchorId) {
        if (TextUtils.isEmpty(audience)) {
            return;
        }
        for (AlivcLiveMixStream alivcLiveMixStream : mMultiInteractLiveMixStreamsArray) {
            if (audience.equals(alivcLiveMixStream.getUserId())) {
                mMultiInteractLiveMixStreamsArray.remove(alivcLiveMixStream);
                break;
            }
        }
        //Array 中只剩主播 id，说明无人连麦
        if (mMultiInteractLiveMixStreamsArray.size() == 1 && mMultiInteractLiveMixStreamsArray.get(0).getUserId().equals(anchorId)) {
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
        } else {
            mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            }
        }
    }

    public void release() {
        stopCDNPull();
        super.release();
    }
}
