package com.alivc.live.interactive_live;

import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mAlivcLivePushConfig;

import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import com.alivc.live.interactive_common.InteractLiveBaseManager;
import com.alivc.live.interactive_common.InteractiveBaseUtil;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;

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

    // 连麦场景下设置混流
    public void setLiveMixTranscodingConfig(InteractiveUserData anchorUserData, InteractiveUserData audienceUserData) {
        if (anchorUserData == null || TextUtils.isEmpty(anchorUserData.channelId) || TextUtils.isEmpty(anchorUserData.userId)) {
            clearLiveMixTranscodingConfig();
            return;
        }

        if (audienceUserData == null || TextUtils.isEmpty(audienceUserData.channelId) || TextUtils.isEmpty(audienceUserData.userId)) {
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

        // 添加主播混流窗口
        AlivcLiveMixStream anchorMixStream = new AlivcLiveMixStream();
        anchorMixStream.setUserId(anchorUserData.userId);
        anchorMixStream.setX(0);
        anchorMixStream.setY(0);
        anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth());
        anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight());
        anchorMixStream.setZOrder(1);

        mixStreams.add(anchorMixStream);
        Log.d(TAG, "anchorMixStream: " + anchorMixStream);

        // 添加连麦观众混流窗口
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        if (mAudienceFrameLayout != null) {
            audienceMixStream.setUserId(audienceUserData.userId);
            audienceMixStream.setX((int) mAudienceFrameLayout.getX() / 3);
            audienceMixStream.setY((int) mAudienceFrameLayout.getY() / 3);
            audienceMixStream.setWidth(mAudienceFrameLayout.getWidth() / 2);
            audienceMixStream.setHeight(mAudienceFrameLayout.getHeight() / 2);
            audienceMixStream.setZOrder(2);
            audienceMixStream.setMixSourceType(InteractiveBaseUtil.covertVideoStreamType2MixSourceType(audienceUserData.videoStreamType));

            mixStreams.add(audienceMixStream);
            Log.d(TAG, "audienceMixStream: " + audienceMixStream);
        }

        AlivcLiveTranscodingConfig transcodingConfig = new AlivcLiveTranscodingConfig();
        transcodingConfig.setMixStreams(mixStreams);
        mAlivcLivePusher.setLiveMixTranscodingConfig(transcodingConfig);
    }

    // 连麦场景添加混流
    public void addAnchorMixTranscodingConfig(InteractiveUserData anchorUserData) {
        if (anchorUserData == null || TextUtils.isEmpty(anchorUserData.channelId) || TextUtils.isEmpty(anchorUserData.userId)) {
            clearLiveMixTranscodingConfig();
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

    // 多人连麦场景添加混流
    public void addAudienceMixTranscodingConfig(InteractiveUserData audienceUserData, FrameLayout frameLayout) {
        if (audienceUserData == null || TextUtils.isEmpty(audienceUserData.channelId) || TextUtils.isEmpty(audienceUserData.userId)) {
            return;
        }

        if (mAlivcLivePusher == null) {
            return;
        }

        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        audienceMixStream.setUserId(audienceUserData.userId);
        audienceMixStream.setX((int) frameLayout.getX() / 3);
        audienceMixStream.setY((int) frameLayout.getY() / 3);
        audienceMixStream.setWidth(frameLayout.getWidth() / 3);
        audienceMixStream.setHeight(frameLayout.getHeight() / 3);
        audienceMixStream.setZOrder(2);
        audienceMixStream.setMixSourceType(InteractiveBaseUtil.covertVideoStreamType2MixSourceType(audienceUserData.videoStreamType));

        mMultiInteractLiveMixStreamsArray.add(audienceMixStream);
        Log.d(TAG, "audienceMixStream --- " + audienceMixStream.getUserId() + ", " + audienceMixStream.getWidth() + ", " + audienceMixStream.getHeight()
                + ", " + audienceMixStream.getX() + ", " + audienceMixStream.getY() + ", " + audienceMixStream.getZOrder());

        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
    }

    /**
     * 移除混流
     *
     * @param audienceUserData 观众userdata
     * @param anchorId         主播userdata
     */
    public void removeAudienceLiveMixTranscodingConfig(InteractiveUserData audienceUserData, String anchorId) {
        if (audienceUserData == null || TextUtils.isEmpty(audienceUserData.channelId) || TextUtils.isEmpty(audienceUserData.userId)) {
            return;
        }

        AlivcLiveMixStream mixStream = findMixStreamByUserData(audienceUserData);
        if (mixStream == null) {
            return;
        }

        mMultiInteractLiveMixStreamsArray.remove(mixStream);

        //Array 中只剩主播 id，说明无人连麦
        if (mMultiInteractLiveMixStreamsArray.size() == 1 && mMultiInteractLiveMixStreamsArray.get(0).getUserId().equals(anchorId)) {
            clearLiveMixTranscodingConfig();
        } else {
            mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            }
        }
    }

    public void release() {
        stopPullCDNStream();
        super.release();
    }

    public void muteLocalCamera(boolean muteLocalCamera) {
        mAlivcLivePusher.muteLocalCamera(muteLocalCamera);
    }
}
