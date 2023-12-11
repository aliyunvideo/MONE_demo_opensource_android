package com.alivc.live.interactive_pk;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.InteractiveMode;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcLiveLocalRecordConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class PKController {

    private final PKLiveManager mPKLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;

    // 主播信息
    public InteractiveUserData mOwnerUserData;
    // PK主播信息
    public InteractiveUserData mOtherUserData;

    private boolean mEnableSpeakerPhone = false;

    public PKController(Context context, String roomId, String userId) {
        mPKLiveManager = new PKLiveManager();
        mPKLiveManager.init(context.getApplicationContext(), InteractiveMode.PK);
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
        this.mContext = context;

        InteractiveUserData userData = new InteractiveUserData();
        userData.channelId = roomId;
        userData.userId = userId;
        userData.url = URLUtils.generateInteractivePushUrl(roomId, userId);
        mOwnerUserData = userData;
    }

    /**
     * 设置 PK 主播的信息
     */
    public void setPKOtherInfo(InteractiveUserData userData) {
        if (userData == null) {
            return;
        }
        mOtherUserData = userData;
        mOtherUserData.url = URLUtils.generateInteractivePullUrl(userData.channelId, userData.userId);
    }

    /**
     * 开始推流
     *
     * @param frameLayout 推流画面渲染 View 的 parent ViewGroup
     */
    public void startPush(FrameLayout frameLayout) {
        externAV();
        mPKLiveManager.startPreviewAndPush(mOwnerUserData, frameLayout, true);
    }

    private void externAV() {
        if (LivePushGlobalConfig.mAlivcLivePushConfig.isExternMainStream()) {
            File yuvFile = ResourcesConst.localCaptureYUVFilePath(mContext);
            mLocalStreamReader.readYUVData(yuvFile, (buffer, pts, videoWidth, videoHeight, videoStride, videoSize, videoRotation) -> {
                mPKLiveManager.inputStreamVideoData(buffer, videoWidth, videoHeight, videoStride, videoSize, pts, videoRotation);
            });
            File pcmFile = ResourcesConst.localCapturePCMFilePath(mContext);
            mLocalStreamReader.readPCMData(pcmFile, (buffer, length, pts, audioSampleRate, audioChannel) -> {
                mPKLiveManager.inputStreamAudioData(buffer, length, audioSampleRate, audioChannel, pts);
            });
        }
    }

    /**
     * 开始 PK (拉流)
     */
    public void startPK(InteractiveUserData userData, FrameLayout frameLayout) {
        mPKLiveManager.setPullView(userData, frameLayout, true);
        mPKLiveManager.startPullRTCStream(mOtherUserData);
    }

    /**
     * 停止 PK (拉流)
     */
    public void stopPK() {
        mPKLiveManager.stopPullRTCStream(mOtherUserData);
    }

    public void setPKLiveMixTranscoding(boolean needMix) {
        if (needMix) {
            mPKLiveManager.setLiveMixTranscodingConfig(mOwnerUserData, mOtherUserData, false);
        } else {
            mPKLiveManager.clearLiveMixTranscodingConfig();
        }
    }

    /**
     * 静音对端主播（一般用于PK惩罚环节的业务场景）
     *
     * @param mute 静音
     */
    public void mutePKMixStream(boolean mute) {
        if (!isPKing() || mPKLiveManager == null) {
            return;
        }

        // 是否静音连麦实时流
        if (mute) {
            mPKLiveManager.pauseAudioPlaying(mOtherUserData.getKey());
        } else {
            mPKLiveManager.resumeAudioPlaying(mOtherUserData.getKey());
        }

        // 更新混流静音
        mPKLiveManager.setLiveMixTranscodingConfig(mOwnerUserData, mOtherUserData, mute);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mPKLiveManager.switchCamera();
    }

    public boolean isPKing() {
        return mPKLiveManager.isPulling(mOtherUserData);
    }

    /**
     * 开始 PK (多人 PK 场景)
     */
    public void startMultiPK(InteractiveUserData userData, FrameLayout frameLayout) {
        if (userData == null) {
            return;
        }
        mPKLiveManager.setPullView(userData, frameLayout, true);
        mPKLiveManager.startPullRTCStream(userData);
    }

    public void setPullView(InteractiveUserData userData, FrameLayout frameLayout) {
        mPKLiveManager.setPullView(userData, frameLayout, true);
    }

    /**
     * 停止 PK (多人 PK 场景)
     */
    public void stopMultiPK(InteractiveUserData userData) {
        mPKLiveManager.stopPullRTCStream(userData);
    }

    /**
     * 恢复视频流
     *
     * @param userKey userKey
     */
    public void resumeVideoPlaying(String userKey) {
        mPKLiveManager.resumePlayRTCStream(mPKLiveManager.getUserDataByKey(userKey));
    }

    /**
     * 暂停视频流
     *
     * @param userKey userKey
     */
    public void pauseVideoPlaying(String userKey) {
        mPKLiveManager.pausePlayRTCStream(mPKLiveManager.getUserDataByKey(userKey));
    }

    /**
     * 设置 PK 主播的信息(多人PK场景)
     */
    public void setMultiPKOtherInfo(InteractiveUserData userData) {
        setPKOtherInfo(userData);
    }

    public boolean isPKing(InteractiveUserData userData) {
        return mPKLiveManager.isPulling(userData);
    }

    /**
     * 设置混流 (多人 PK 场景)
     *
     * @param isOwnerUserId 是否是主播自己的 id
     * @param userData      主播 userData (如果 isOwnerUserId 为 true，则 userId 表示主播自己的 id，否则表示 pk 方的主播 id)
     * @param frameLayout   当 isOwnerUserId 为 false 时，需要设置 pk 方主播的混流，需要传递 frameLayout 计算混流位置
     */
    public void addMultiPKLiveMixTranscoding(boolean isOwnerUserId, InteractiveUserData userData, FrameLayout frameLayout) {
        if (isOwnerUserId) {
            mPKLiveManager.addAnchorMixTranscodingConfig(userData);
        } else {
            mPKLiveManager.addAudienceMixTranscodingConfig(userData, frameLayout);
        }
    }

    /**
     * 设置混流 (多人 PK 场景)
     * 当 isOwnerUserId 为 true 时，表示主播自己退出 PK，则删除所有混流设置。否则只删除 PK 方的混流。
     *
     * @param isOwnerUserId 是否是主播自己的 id
     * @param userData      主播 userData (如果 isOwnerUserId 为 true，则 userId 表示主播自己的 id，否则表示 pk 方的主播 id)
     */
    public void removeMultiPKLiveMixTranscoding(boolean isOwnerUserId, InteractiveUserData userData) {
        if (isOwnerUserId) {
            mPKLiveManager.clearLiveMixTranscodingConfig();
        } else {
            mPKLiveManager.removeLiveMixTranscodingConfig(userData);
        }
    }

    public void muteAnchor(String userKey, boolean mute) {
        // 更新连麦静音
        if (mute) {
            mPKLiveManager.pauseAudioPlaying(userKey);
        } else {
            mPKLiveManager.resumeAudioPlaying(userKey);
        }

        // 更新混流静音
        mPKLiveManager.muteAnchorMultiStream(mPKLiveManager.getUserDataByKey(userKey), mute);
    }

    public void setPKLivePushPullListener(InteractLivePushPullListener listener) {
        mPKLiveManager.setInteractLivePushPullListener(listener);
    }

    public void setMultiPKLivePushPullListener(InteractLivePushPullListener listener) {
        mPKLiveManager.setInteractLivePushPullListener(listener);
    }

    public void release() {
        mPKLiveManager.release();
        mLocalStreamReader.stopYUV();
        mLocalStreamReader.stopPcm();
    }

    public void setMute(boolean b) {
        mPKLiveManager.setMute(b);
    }

    public void changeSpeakerPhone() {
        mEnableSpeakerPhone = !mEnableSpeakerPhone;
        mPKLiveManager.enableSpeakerPhone(mEnableSpeakerPhone);
    }

    public void enableAudioCapture(boolean enable) {
        mPKLiveManager.enableAudioCapture(enable);
    }

    public void enableLocalCamera(boolean enable) {
        mPKLiveManager.enableLocalCamera(enable);
    }

    public void sendSEI(String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LivePushPKSEI", text);
            mPKLiveManager.sendSEI(jsonObject.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public void startLocalRecord(AlivcLiveLocalRecordConfig recordConfig) {
        if (mPKLiveManager != null) {
            mPKLiveManager.startLocalRecord(recordConfig);
        }
    }

    public void stopLocalRecord() {
        if (mPKLiveManager != null) {
            mPKLiveManager.stopLocalRecord();
        }
    }
}
