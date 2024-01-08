package com.alivc.live.interactive_live;

import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mAlivcLivePushConfig;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;

import com.alivc.live.commonbiz.seidelay.SEISourceType;
import com.alivc.live.interactive_common.InteractLiveBaseManager;
import com.alivc.live.interactive_common.InteractiveBaseUtil;
import com.alivc.live.interactive_common.InteractiveMode;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class InteractLiveManager extends InteractLiveBaseManager {

    // 连麦场景下，普通观众拉取主播的旁路CDN直播流
    private AliPlayer mAliPlayer;

    @Override
    public void init(Context context, InteractiveMode interactiveMode) {
        super.init(context, interactiveMode);
        initCDNPlayer();
    }

    @Override
    public void release() {
        super.release();
        stopPullCDNStream();
        releaseCDNPlayer();
    }

    /**
     * 连麦场景下设置混流
     */
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

    /**
     * 连麦场景添加混流
     */
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

    /**
     * 多人连麦场景添加混流
     */
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
     * 多人连麦场景移除混流
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

    public void muteLocalCamera(boolean muteLocalCamera) {
        mAlivcLivePusher.muteLocalCamera(muteLocalCamera);
    }

    /**
     * 初始化基础直播播放器（用于播放CDN基础直播流，如：rtmp/http-flv）
     */
    private void initCDNPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(mContext);

        PlayerConfig playerConfig = mAliPlayer.getConfig();
        // 纯音频 或 纯视频 的flv 需要设置 以加快起播
        // TODO How to enable flv_strict_header
        // 起播缓存，越大起播越稳定，但会影响起播时间，可酌情设置
        playerConfig.mStartBufferDuration = 1000;
        // 卡顿恢复需要的缓存，网络不好的情况可以设置大一些，当前纯音频设置500还好，视频的话建议用默认值3000.
        playerConfig.mHighBufferDuration = 500;
        // 需要开启SEI监听
        playerConfig.mEnableSEI = true;
        mAliPlayer.setConfig(playerConfig);

        mAliPlayer.setAutoPlay(true);

        mAliPlayer.setOnErrorListener(errorInfo -> {
            mAliPlayer.prepare();
        });

        mAliPlayer.setOnSeiDataListener(new IPlayer.OnSeiDataListener() {
            @Override
            public void onSeiData(int i, byte[] bytes) {
                String sei = new String(bytes, StandardCharsets.UTF_8);
                mSEIDelayManager.receiveSEI(SEISourceType.CDN, sei);
//                Log.d(TAG, "onSeiData: " + i + ", " + sei);

                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPlayerSei(i, bytes);
                }
            }
        });
    }

    /**
     * 销毁基础直播播放器（用于播放CDN基础直播流，如：rtmp/http-flv）
     */
    private void releaseCDNPlayer() {
        if (mAliPlayer != null) {
            mAliPlayer.release();
            mAliPlayer = null;
        }
    }

    /**
     * 连麦场景下，普通观众设置 CDN 拉流时，渲染的 Surface
     *
     * @param surfaceHolder 播放器渲染画面的 Surface
     */
    public void setPullView(SurfaceHolder surfaceHolder) {
        if (mAliPlayer != null) {
            mAliPlayer.setDisplay(surfaceHolder);
        }
    }

    /**
     * 连麦场景下，普通观众开始播放主播的旁路CDN直播流
     */
    public void startPullCDNStream(String pullUrl) {
        if (TextUtils.isEmpty(pullUrl)) {
            return;
        }
        if (mAliPlayer != null) {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(pullUrl);
            mAliPlayer.setDataSource(urlSource);
            mAliPlayer.prepare();
        }
    }

    /**
     * 连麦场景下，普通观众停止播放主播的旁路CDN直播流
     */
    public void stopPullCDNStream() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
        }
    }
}
