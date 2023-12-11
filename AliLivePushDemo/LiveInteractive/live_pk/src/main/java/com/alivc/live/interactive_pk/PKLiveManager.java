package com.alivc.live.interactive_pk;

import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mAlivcLivePushConfig;

import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.alivc.live.interactive_common.InteractLiveBaseManager;
import com.alivc.live.interactive_common.InteractiveBaseUtil;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.player.AlivcLivePlayer;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLiveMixStreamType;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;

import java.util.ArrayList;

public class PKLiveManager extends InteractLiveBaseManager {

    public void resumeAudioPlaying(String key) {
        AlivcLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.resumeAudioPlaying();
        }
    }

    public void pauseAudioPlaying(String key) {
        AlivcLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.pauseAudioPlaying();
        }
    }

    // PK场景下设置混流
    public void setLiveMixTranscodingConfig(InteractiveUserData anchorUserData, InteractiveUserData otherUserData, boolean pkMute) {
        if (anchorUserData == null || TextUtils.isEmpty(anchorUserData.channelId) || TextUtils.isEmpty(anchorUserData.userId)) {
            clearLiveMixTranscodingConfig();
            return;
        }

        if (otherUserData == null || TextUtils.isEmpty(otherUserData.channelId) || TextUtils.isEmpty(otherUserData.userId)) {
            clearLiveMixTranscodingConfig();
            return;
        }

        if (mAlivcLivePushConfig == null) {
            return;
        }

        if (mAlivcLivePusher == null) {
            return;
        }

        ArrayList<AlivcLiveMixStream> mixStreams = new ArrayList<>();

        AlivcLiveMixStream anchorMixStream = new AlivcLiveMixStream();
        anchorMixStream.setUserId(anchorUserData.userId);
        anchorMixStream.setX(0);
        anchorMixStream.setY(0);
        anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth() / 2);
        anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight() / 2);
        anchorMixStream.setZOrder(1);

        mixStreams.add(anchorMixStream);
        Log.d(TAG, "anchorMixStream: " + anchorMixStream.getUserId() + ", " + anchorMixStream.getWidth() + ", " + anchorMixStream.getHeight()
                + ", " + anchorMixStream.getX() + ", " + anchorMixStream.getY() + ", " + anchorMixStream.getZOrder());

        if (mAudienceFrameLayout != null) {
            AlivcLiveMixStream otherMixStream = new AlivcLiveMixStream();
            otherMixStream.setUserId(otherUserData.userId);
            otherMixStream.setX(mAlivcLivePushConfig.getWidth() / 2);
            otherMixStream.setY(0);
            otherMixStream.setWidth(mAlivcLivePushConfig.getWidth() / 2);
            otherMixStream.setHeight(mAlivcLivePushConfig.getHeight() / 2);
            otherMixStream.setZOrder(2);
            otherMixStream.setMixStreamType(pkMute ? AlivcLiveMixStreamType.PURE_VIDEO : AlivcLiveMixStreamType.AUDIO_VIDEO);
            otherMixStream.setMixSourceType(InteractiveBaseUtil.covertVideoStreamType2MixSourceType(otherUserData.videoStreamType));

            mixStreams.add(otherMixStream);
            Log.d(TAG, "otherMixStream: " + otherMixStream.getUserId() + ", " + otherMixStream.getWidth() + ", " + otherMixStream.getHeight()
                    + ", " + otherMixStream.getX() + ", " + otherMixStream.getY() + ", " + otherMixStream.getZOrder());
        }

        AlivcLiveTranscodingConfig transcodingConfig = new AlivcLiveTranscodingConfig();
        transcodingConfig.setMixStreams(mixStreams);
        mAlivcLivePusher.setLiveMixTranscodingConfig(transcodingConfig);
    }

    // 多人PK场景下添加混流
    public void addAnchorMixTranscodingConfig(InteractiveUserData anchorUserData) {
        if (anchorUserData == null || TextUtils.isEmpty(anchorUserData.channelId) || TextUtils.isEmpty(anchorUserData.userId)) {
            return;
        }

        if (mAlivcLivePushConfig == null) {
            return;
        }

        if (mAlivcLivePusher == null) {
            return;
        }

        AlivcLiveMixStream anchorMixStream = new AlivcLiveMixStream();
        anchorMixStream.setUserId(anchorUserData.userId);
        anchorMixStream.setX(0);
        anchorMixStream.setY(0);
        anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth());
        anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight());
        anchorMixStream.setZOrder(1);
        anchorMixStream.setMixSourceType(InteractiveBaseUtil.covertVideoStreamType2MixSourceType(anchorUserData.videoStreamType));

        mMultiInteractLiveMixStreamsArray.add(anchorMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
    }

    // 多人连麦混流静音
    public void muteAnchorMultiStream(InteractiveUserData audienceUserData, boolean mute) {
        if (audienceUserData == null || TextUtils.isEmpty(audienceUserData.channelId) || TextUtils.isEmpty(audienceUserData.userId)) {
            return;
        }

        AlivcLiveMixStream mixStream = findMixStreamByUserData(audienceUserData);
        if (mixStream == null) {
            return;
        }

        mixStream.setMixStreamType(mute ? AlivcLiveMixStreamType.PURE_VIDEO : AlivcLiveMixStreamType.AUDIO_VIDEO);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    /**
     * 添加混流配置
     *
     * @param audienceUserData 观众信息
     * @param frameLayout      观众 frameLayout(渲染 View 的 ViewGroup，用于计算混流位置)
     */
    public void addAudienceMixTranscodingConfig(InteractiveUserData audienceUserData, FrameLayout frameLayout) {
        if (audienceUserData == null || TextUtils.isEmpty(audienceUserData.channelId) || TextUtils.isEmpty(audienceUserData.userId)) {
            return;
        }

        if (mAlivcLivePushConfig == null) {
            return;
        }

        if (mAlivcLivePusher == null) {
            return;
        }

        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        audienceMixStream.setUserId(audienceUserData.userId);
        int size = mMultiInteractLiveMixStreamsArray.size() - 1;
        audienceMixStream.setX(size % 3 * frameLayout.getWidth() / 3);
        audienceMixStream.setY(size / 3 * frameLayout.getHeight() / 3);
        audienceMixStream.setWidth(frameLayout.getWidth() / 3);
        audienceMixStream.setHeight(frameLayout.getHeight() / 3);
        audienceMixStream.setZOrder(2);
        audienceMixStream.setMixSourceType(InteractiveBaseUtil.covertVideoStreamType2MixSourceType(audienceUserData.videoStreamType));

        mMultiInteractLiveMixStreamsArray.add(audienceMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
    }

    // 移除混流
    public void removeLiveMixTranscodingConfig(InteractiveUserData userData) {
        if (userData == null || TextUtils.isEmpty(userData.channelId) || TextUtils.isEmpty(userData.userId)) {
            return;
        }

        AlivcLiveMixStream mixStream = findMixStreamByUserData(userData);
        if (mixStream == null) {
            return;
        }

        mMultiInteractLiveMixStreamsArray.remove(mixStream);

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
        if (mMultiInteractLiveMixStreamsArray.size() == 1 && mMultiInteractLiveMixStreamsArray.get(0).getUserId().equals(userData.userId)) {
            clearLiveMixTranscodingConfig();
        } else {
            mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            }
        }
    }
}
