package com.alivc.live.interactive_pk;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.LocalStreamReader;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.bean.MultiAlivcLivePlayer;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.MultiInteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class PKController {

    private final PKLiveManager mPKLiveManager;
    private final Context mContext;
    private final LocalStreamReader mLocalStreamReader;
    //主播房间号和 ID
    private String mOwnerRoomId;
    private String mOwnerUserId;
    //PK对方主播的房间号和 ID
    private String mOtherRoomId;
    private String mOtherUserId;
    //主播推流 URL
    private final String mOwnerPushUrl;
    private String mOtherPullUrl;
    private boolean mEnableSpeakerPhone = false;

    public PKController(Context context, String roomId, String userId) {
        mPKLiveManager = new PKLiveManager();
        mPKLiveManager.init(context.getApplicationContext());
        this.mOwnerRoomId = roomId;
        this.mOwnerUserId = userId;
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
        mOwnerPushUrl = URLUtils.generateInteractivePushUrl(roomId, userId);
    }

    /**
     * 设置 PK 主播的信息
     *
     * @param userId 对方主播的 userId
     * @param roomId 对方主播的 roomId
     */
    public void setPKOtherInfo(String userId, String roomId) {
        mOtherPullUrl = URLUtils.generateInteractivePullUrl(roomId, userId);
        this.mOtherUserId = userId;
        this.mOtherRoomId = roomId;
    }

    /**
     * 开始推流
     *
     * @param frameLayout 推流画面渲染 View 的 parent ViewGroup
     */
    public void startPush(FrameLayout frameLayout) {
        externAV();
        mPKLiveManager.startPreviewAndPush(frameLayout, mOwnerPushUrl, true);
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
    public void startPK(FrameLayout frameLayout) {
        mPKLiveManager.setPullView(frameLayout, true);
        mPKLiveManager.startPull(mOtherPullUrl);
    }

    /**
     * 停止 PK (拉流)
     */
    public void stopPK() {
        mPKLiveManager.stopPull();
    }

    public void setPKLiveMixTranscoding(boolean needMix) {
        if (needMix) {
            mPKLiveManager.setLiveMixTranscodingConfig(mOwnerUserId, mOtherUserId, false);
        } else {
            mPKLiveManager.setLiveMixTranscodingConfig(null, null, false);
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
        MultiAlivcLivePlayer player = mPKLiveManager.getCurrentInteractLivePlayer();
        if (player != null) {
            if (mute) {
                player.pauseAudioPlaying();
            } else {
                player.resumeAudioPlaying();
            }
        }

        // 更新混流静音
        mPKLiveManager.setLiveMixTranscodingConfig(mOwnerUserId, mOtherUserId, mute);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mPKLiveManager.switchCamera();
    }

    public boolean isPKing() {
        return mPKLiveManager.isPulling();
    }

    /**
     * 开始 PK (多人 PK 场景)
     */
    public void startMultiPK(String userId, String roomId, FrameLayout frameLayout) {
        String userKey = generateUserKey(userId, roomId);
        mPKLiveManager.setPullView(userKey, frameLayout, true);
        mPKLiveManager.startPull(userKey, mOtherPullUrl);
    }

    public void setPullView(String userId, String roomId, FrameLayout frameLayout) {
        String userKey = generateUserKey(userId, roomId);
        mPKLiveManager.setPullView(userKey, frameLayout, true);
    }

    /**
     * 停止 PK (多人 PK 场景)
     */
    public void stopMultiPK(String userId, String roomId) {
        String userKey = generateUserKey(userId, roomId);
        mPKLiveManager.stopPull(userKey);
    }

    /**
     * 恢复视频流
     *
     * @param userKey userKey
     */
    public void resumeVideoPlaying(String userKey) {
        mPKLiveManager.resumeVideoPlaying(userKey);
    }

    /**
     * 暂停视频流
     *
     * @param userKey userKey
     */
    public void pauseVideoPlaying(String userKey) {
        mPKLiveManager.pauseVideoPlaying(userKey);
    }

    /**
     * 设置 PK 主播的信息(多人PK场景)
     *
     * @param userId 对方主播的 userId
     * @param roomId 对方主播的 roomId
     */
    public void setMultiPKOtherInfo(String userId, String roomId) {
        setPKOtherInfo(userId, roomId);
        String userKey = generateUserKey(userId, roomId);
        mPKLiveManager.createAlivcLivePlayer(userKey);
    }

    public boolean isPKing(String userId, String roomId) {
        String userKey = generateUserKey(userId, roomId);
        return mPKLiveManager.isPulling(userKey);
    }

    /**
     * 设置混流 (多人 PK 场景)
     *
     * @param isOwnerUserId 是否是主播自己的 id
     * @param userId        主播 id (如果 isOwnerUserId 为 true，则 userId 表示主播自己的 id，否则表示 pk 方的主播 id)
     * @param frameLayout   当 isOwnerUserId 为 false 时，需要设置 pk 方主播的混流，需要传递 frameLayout 计算混流位置
     */
    public void addMultiPKLiveMixTranscoding(boolean isOwnerUserId, String userId, FrameLayout frameLayout) {
        if (isOwnerUserId) {
            mPKLiveManager.addAnchorMixTranscodingConfig(userId);
        } else {
            mPKLiveManager.addAudienceMixTranscodingConfig(userId, frameLayout);
        }

    }

    /**
     * 设置混流 (多人 PK 场景)
     * 当 isOwnerUserId 为 true 时，表示主播自己退出 PK，则删除所有混流设置。否则只删除 PK 方的混流。
     *
     * @param isOwnerUserId 是否是主播自己的 id
     * @param userId        主播 id (如果 isOwnerUserId 为 true，则 userId 表示主播自己的 id，否则表示 pk 方的主播 id)
     */
    public void removeMultiPKLiveMixTranscoding(boolean isOwnerUserId, String userId) {
        if (isOwnerUserId) {
            mPKLiveManager.clearLiveMixTranscodingConfig();
        } else {
            mPKLiveManager.removeLiveMixTranscodingConfig(userId);
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
        mPKLiveManager.muteAnchorMultiStream(userKey.split("=")[0], mute);
    }

    /**
     * 用于多人 PK 场景，根据 RoomId 和 UserId 生成 UserKey
     *
     * @param roomId 房间 ID
     * @param userId 用户 ID
     */
    private String generateUserKey(String userId, String roomId) {
        return userId + "=" + roomId;
    }

    public void setPKLivePushPullListener(InteractLivePushPullListener listener) {
        mPKLiveManager.setInteractLivePushPullListener(listener);
    }

    public void setMultiPKLivePushPullListener(MultiInteractLivePushPullListener listener) {
        mPKLiveManager.setMultiInteractLivePushPullListener(listener);
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

    public void sendSEI(String text) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("LivePushPKSEI", text);
            mPKLiveManager.sendSEI(jsonObject.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
