package com.alivc.live.interactive_pk;

import android.content.Context;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.ReadFileData;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.MultiInteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;

import java.io.File;

public class PKController {

    private final PKLiveManager mPKLiveManager;
    private final Context mContext;
    private final ReadFileData mReadFileData;
    //主播房间号和 ID
    private String mOwnerRoomId;
    private String mOwnerUserId;
    //PK对方主播的房间号和 ID
    private String mOtherRoomId;
    private String mOtherUserId;
    //主播推流 URL
    private final String mOwnerPushUrl;
    private String mOtherPullUrl;

    public PKController(Context context, String roomId, String userId) {
        mPKLiveManager = new PKLiveManager();
        mPKLiveManager.init(context.getApplicationContext());
        this.mOwnerRoomId = roomId;
        this.mOwnerUserId = userId;
        mReadFileData = new ReadFileData();
        this.mContext = context;
        mOwnerPushUrl = URLUtils.generatePushUrl(roomId, userId, 1);
    }

    /**
     * 设置 PK 主播的信息
     *
     * @param userId 对方主播的 userId
     * @param roomId 对方主播的 roomId
     */
    public void setPKOtherInfo(String userId, String roomId) {
        mOtherPullUrl = URLUtils.generatePullUrl(roomId, userId, 1);
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
        if (LivePushGlobalConfig.ENABLE_EXTERN_AV) {
            File yuvFile = ResourcesDownload.localCaptureYUVFilePath(mContext);
            mReadFileData.readYUVData(yuvFile, (buffer, pts) -> mPKLiveManager.inputStreamVideoData(buffer, 720, 1280, 720, 1280 * 720 * 3 / 2, pts, 0));
            File pcmFile = new File(mContext.getFilesDir().getPath() + File.separator + "alivc_resource/441.pcm");
            mReadFileData.readPCMData(pcmFile, (buffer, length, pts) -> mPKLiveManager.inputStreamAudioData(buffer, length, 44100, 1, pts));
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
            mPKLiveManager.setLiveMixTranscodingConfig(mOwnerUserId, mOtherUserId);
        } else {
            mPKLiveManager.setLiveMixTranscodingConfig(null, null);
        }
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
        mReadFileData.stopYUV();
        mReadFileData.stopPcm();
    }

    public void setMute(boolean b) {
        mPKLiveManager.setMute(b);
    }
}
