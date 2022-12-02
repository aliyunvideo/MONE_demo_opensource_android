package com.aliyun.interactive_pk;

import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.alivc.live.player.AlivcLivePlayer;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;
import com.aliyun.interactive_common.InteractLiveBaseManager;

import java.util.ArrayList;

public class PKLiveManager extends InteractLiveBaseManager {

    public void resumeVideoPlaying(String key) {
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.resumeVideoPlaying();
        }
    }

    public void pauseVideoPlaying(String key) {
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.pauseVideoPlaying();
        }
    }

    /**
     * 设置混流
     *
     * @param anchorId 主播 id
     * @param audience 观众 id
     */
    public void setLiveMixTranscodingConfig(String anchorId, String audience) {
        if (TextUtils.isEmpty(anchorId) || TextUtils.isEmpty(audience)) {
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
            anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth() / 2);
            anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight() / 2);
            anchorMixStream.setZOrder(1);
            Log.d(TAG, "AlivcRTC anchorMixStream --- " + anchorMixStream.getUserId() + ", " + anchorMixStream.getWidth() + ", " + anchorMixStream.getHeight()
                    + ", " + anchorMixStream.getX() + ", " + anchorMixStream.getY() + ", " + anchorMixStream.getZOrder());
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        if (mAudienceFrameLayout != null) {
            audienceMixStream.setUserId(audience);
            audienceMixStream.setX(mAlivcLivePushConfig.getWidth() / 2);
            audienceMixStream.setY(0);
            audienceMixStream.setWidth(mAlivcLivePushConfig.getWidth() / 2);
            audienceMixStream.setHeight(mAlivcLivePushConfig.getHeight() / 2);

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
        int size = mMultiInteractLiveMixStreamsArray.size() - 1;
        audienceMixStream.setX(size % 3 * frameLayout.getWidth() / 3);
        audienceMixStream.setY(size / 3 * frameLayout.getHeight() / 3);
        audienceMixStream.setWidth(frameLayout.getWidth() / 3);
        audienceMixStream.setHeight(frameLayout.getHeight() / 3);
        audienceMixStream.setZOrder(2);
        mMultiInteractLiveMixStreamsArray.add(audienceMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    /**
     * 移除混流
     */
    public void removeLiveMixTranscodingConfig(String userId) {
        if (TextUtils.isEmpty(userId)) {
            return;
        }

        for (AlivcLiveMixStream alivcLiveMixStream : mMultiInteractLiveMixStreamsArray) {
            if (userId.equals(alivcLiveMixStream.getUserId())) {
                mMultiInteractLiveMixStreamsArray.remove(alivcLiveMixStream);
                break;
            }
        }

        //多人 PK 混流，结束 PK 后，重新排版混流界面，防止出现覆盖现象
        int size = mMultiInteractLiveMixStreamsArray.size() - 1;
        for (int i = 1; i < mMultiInteractLiveMixStreamsArray.size(); i++) {
            AlivcLiveMixStream alivcLiveMixStream = mMultiInteractLiveMixStreamsArray.get(i);
            alivcLiveMixStream.setX(((size - i) % 3) * alivcLiveMixStream.getWidth());
            alivcLiveMixStream.setY((size - i) / 3 * alivcLiveMixStream.getHeight());
            alivcLiveMixStream.setWidth(alivcLiveMixStream.getWidth());
            alivcLiveMixStream.setHeight(alivcLiveMixStream.getHeight());
        }

        //Array 中只剩主播 id，说明无人连麦
        if (mMultiInteractLiveMixStreamsArray.size() == 1 && mMultiInteractLiveMixStreamsArray.get(0).getUserId().equals(userId)) {
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
}
