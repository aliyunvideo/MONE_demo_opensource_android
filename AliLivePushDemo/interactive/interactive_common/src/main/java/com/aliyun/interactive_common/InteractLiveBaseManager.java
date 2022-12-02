package com.aliyun.interactive_common;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.player.AlivcLivePlayConfig;
import com.alivc.live.player.AlivcLivePlayInfoListener;
import com.alivc.live.player.AlivcLivePlayer;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.pusher.AlivcAudioAACProfileEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcFpsEnum;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.aliyun.interactive_common.bean.MultiAlivcLivePlayer;
import com.aliyun.interactive_common.listener.InteractLivePushPullListener;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;
import com.aliyun.interactive_common.utils.LivePushGlobalConfig;
import com.aliyun.interactive_common.utils.URLUtils;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.source.UrlSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InteractLiveBaseManager {

    protected static final String TAG = "InteractLiveManager";
    protected AlivcLivePushConfig mAlivcLivePushConfig;
    protected FrameLayout mAudienceFrameLayout;
    //多人连麦混流
    protected final ArrayList<AlivcLiveMixStream> mMultiInteractLiveMixStreamsArray = new ArrayList<>();
    //多人连麦 Config
    protected final AlivcLiveTranscodingConfig mMixInteractLiveTranscodingConfig = new AlivcLiveTranscodingConfig();

    private Context mContext;
    private boolean mIsPlaying = false;
    protected AliPlayer mAliPlayer;
    protected AlivcLivePusher mAlivcLivePusher;
    protected MultiAlivcLivePlayer mAlivcLivePlayer;
    protected final Map<String, MultiAlivcLivePlayer> mAlivcLivePlayerMap = new HashMap<>();
    protected InteractLivePushPullListener mInteractLivePushPullListener;
    protected MultiInteractLivePushPullListener mMultiInteractLivePushPullListener;
    private String mPushUrl;
    private String mPullUrl;
    private boolean mHasPulled = false;
    private boolean mHasPushed = false;

    public void init(Context context) {
        mContext = context.getApplicationContext();
        initLivePusherSDK();
        initPlayer();
    }

    private void initLivePusherSDK() {
        AlivcLiveBase.registerSDK();
        // 初始化推流配置类
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
        mAlivcLivePushConfig.setResolution(LivePushGlobalConfig.CONFIG_RESOLUTION);
        // 建议用户使用20fps
        mAlivcLivePushConfig.setFps(AlivcFpsEnum.FPS_20);
        // 打开码率自适应，默认为true
        mAlivcLivePushConfig.setEnableBitrateControl(true);
        // 默认为竖屏，可设置home键向左或向右横屏
        mAlivcLivePushConfig.setPreviewOrientation(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT);
        // 设置音频编码模式
        mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.AAC_LC);

        mAlivcLivePushConfig.setVideoEncodeMode(LivePushGlobalConfig.VIDEO_ENCODE_HARD ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);
        mAlivcLivePushConfig.setAudioEncodeMode(LivePushGlobalConfig.AUDIO_ENCODE_HARD ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);

        mAlivcLivePusher = new AlivcLivePusher();
        mAlivcLivePusher.init(mContext, mAlivcLivePushConfig);

        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.d(TAG, "onSystemError: ");
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
                }
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.d(TAG, "onSDKError: ");
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
                }
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPushError();
                }
            }
        });

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPreviewStarted: ");
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPreviewStoped(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPreviewStoped: ");
            }

            @Override
            public void onPushStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushStarted: ");
                mHasPushed = false;
                stopCDNPull();
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushSuccess();
                }
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onFirstAVFramePushed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onFirstAVFramePushed: ");
            }

            @Override
            public void onPushPauesed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushPauesed: ");
            }

            @Override
            public void onPushResumed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushResumed: ");
            }

            @Override
            public void onPushStoped(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushStoped: ");
                alivcLivePusher.stopPreview();
                mHasPushed = false;
            }

            @Override
            public void onPushRestarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushRestarted: ");
            }

            @Override
            public void onFirstFramePreviewed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onFirstFramePreviewed: ");
            }

            @Override
            public void onDropFrame(AlivcLivePusher alivcLivePusher, int i, int i1) {
                Log.d(TAG, "onDropFrame: ");
            }

            @Override
            public void onAdjustBitRate(AlivcLivePusher alivcLivePusher, int i, int i1) {
                Log.i(TAG, "onAdjustBitRate: ");
            }

            @Override
            public void onAdjustFps(AlivcLivePusher alivcLivePusher, int i, int i1) {
                Log.d(TAG, "onAdjustFps: ");
            }

            @Override
            public void onPushStatistics(AlivcLivePusher alivcLivePusher, AlivcLivePushStatsInfo alivcLivePushStatsInfo) {
                Log.i(TAG, "onPushStatistics: ");
            }

            @Override
            public void onSetLiveMixTranscodingConfig(AlivcLivePusher alivcLivePusher, boolean b, String s) {
                Log.d(TAG, "onSetLiveMixTranscodingConfig: ");
            }
        });

        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
            @Override
            public void onNetworkPoor(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onNetworkPoor: ");
            }

            @Override
            public void onNetworkRecovery(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onNetworkRecovery: ");
            }

            @Override
            public void onReconnectStart(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectStart: ");
            }

            @Override
            public void onConnectionLost(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onConnectionLost: ");
            }

            @Override
            public void onReconnectFail(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectFail: ");
            }

            @Override
            public void onReconnectSucceed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onReconnectSucceed: ");
            }

            @Override
            public void onSendDataTimeout(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onSendDataTimeout: ");
            }

            @Override
            public void onConnectFail(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onConnectFail: ");
            }

            @Override
            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushURLAuthenticationOverdue: ");
                return null;
            }

            @Override
            public void onSendMessage(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onSendMessage: ");
            }

            @Override
            public void onPacketsLost(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPacketsLost: ");
            }
        });
    }

    private void initPlayer() {
        mAliPlayer = AliPlayerFactory.createAliPlayer(mContext);
        mAliPlayer.setAutoPlay(true);

        mAliPlayer.setOnErrorListener(errorInfo -> {
            mAliPlayer.prepare();
        });

        mAlivcLivePlayer = new MultiAlivcLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        mAlivcLivePlayer.setPlayInfoListener(new AlivcLivePlayInfoListener() {
            @Override
            public void onPlayStarted() {
                Log.d(TAG, "onPlayStarted: ");
                mIsPlaying = true;
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullSuccess();
                }
            }

            @Override
            public void onPlayStopped() {
                Log.d(TAG, "onPlayStopped: ");
                mIsPlaying = false;
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullStop();
                }
            }

            @Override
            public void onFirstVideoFrameDrawn() {
                Log.d(TAG, "onPlaying: ");
                mHasPulled = true;
            }

            @Override
            public void onError(AlivcLivePlayError alivcLivePlayError, String s) {
                Log.d(TAG, "onError: ");
                mIsPlaying = false;
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullError(alivcLivePlayError, s);
                }
            }
        });
    }

    /**
     * 创建 AlivcLivePlayer，用于多人连麦互动
     *
     * @param audienceId 区分 AlivcLivePlayer 的 key
     */
    public boolean createAlivcLivePlayer(String audienceId) {
        if (mAlivcLivePlayerMap.containsKey(audienceId)) {
            return false;
        }
        MultiAlivcLivePlayer alivcLivePlayer = new MultiAlivcLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        alivcLivePlayer.setAudienceId(audienceId);
        alivcLivePlayer.setMultiInteractPlayInfoListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String audienceId) {
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPullSuccess(audienceId);
                }
            }

            @Override
            public void onPullError(String audienceId, AlivcLivePlayError errorType, String errorMsg) {
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPullError(audienceId, errorType, errorMsg);
                }
            }

            @Override
            public void onPullStop(String audienceId) {
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPullStop(audienceId);
                }
            }

            @Override
            public void onPushSuccess() {
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPushError() {
                if (mMultiInteractLivePushPullListener != null) {
                    mMultiInteractLivePushPullListener.onPushError();
                }
            }
        });
        mAlivcLivePlayerMap.put(audienceId, alivcLivePlayer);
        return true;
    }

    public boolean isPushing() {
        return mAlivcLivePusher.isPushing();
    }

    public boolean isPulling() {
        return mIsPlaying;
    }

    public boolean isPulling(String key) {
        MultiAlivcLivePlayer multiAlivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if (multiAlivcLivePlayer != null) {
            return multiAlivcLivePlayer.isPulling();
        }
        return false;
    }

    public void startPreviewAndPush(FrameLayout frameLayout, String url, boolean isAnchor) {
        this.mPushUrl = url;
        mAlivcLivePusher.startPreview(mContext, frameLayout, isAnchor);
        mAlivcLivePusher.startPushAysnc(mPushUrl);
    }

    public void stopPush() {
        if (mAlivcLivePusher.isPushing()) {
            mAlivcLivePusher.stopPush();
        }
    }

    public void setPullView(FrameLayout frameLayout, boolean isAnchor) {
        this.mAudienceFrameLayout = frameLayout;
        AlivcLivePlayConfig config = new AlivcLivePlayConfig();
        config.isFullScreen = isAnchor;
        mAlivcLivePlayer.setupWithConfig(config);
        mAlivcLivePlayer.setPlayView(frameLayout);
    }

    public void setPullView(String key, FrameLayout frameLayout, boolean isAnchor) {
        AlivcLivePlayConfig config = new AlivcLivePlayConfig();
        config.isFullScreen = isAnchor;
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.setupWithConfig(config);
            alivcLivePlayer.setPlayView(frameLayout);
        }
    }

    public void startPull(String url) {
        this.mPullUrl = url;
        if (url.startsWith(URLUtils.ARTC)) {
            mAlivcLivePlayer.startPlay(url);
        } else {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(url);
            mAliPlayer.setDataSource(urlSource);
            mAliPlayer.prepare();
        }
    }

    public void startPull(String key, String url) {
        this.mPullUrl = url;
        if (url.startsWith(URLUtils.ARTC)) {
            AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
            if (alivcLivePlayer != null) {
                alivcLivePlayer.startPlay(url);
            }
        } else {
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(url);
            mAliPlayer.setDataSource(urlSource);
            mAliPlayer.prepare();
        }
    }

    public void stopPull() {
        if (isPulling()) {
            mAlivcLivePlayer.stopPlay();
        }
        mHasPulled = false;
    }

    public void stopPull(String key) {
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.stopPlay();
        }
        mAlivcLivePlayerMap.remove(key);
    }

    public void pause() {
        if (isPushing()) {
            mAlivcLivePusher.pause();
            mHasPushed = true;
        }
        if (isPulling()) {
            mAlivcLivePlayer.stopPlay();
            mHasPulled = true;
        }
    }

    public void pause(String key) {
        //TODO
    }

    public void resume() {
        if (mHasPushed) {
            mAlivcLivePusher.resumeAsync();
        }
        if (mHasPulled) {
            startPull(mPullUrl);
        }
    }

    public void resume(String key) {
        //TODO
    }

    public void stopCDNPull() {
        mAliPlayer.stop();
    }

    public void switchCamera() {
        if (mAlivcLivePusher.isPushing()) {
            mAlivcLivePusher.switchCamera();
        }
    }

    public void release() {
        stopPull();
        stopPush();

        mMultiInteractLiveMixStreamsArray.clear();
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            mAlivcLivePusher.destroy();
        }

        if (mAliPlayer != null) {
            mAliPlayer.release();
        }
        mAlivcLivePlayer.destroy();

        for (AlivcLivePlayer alivcLivePlayer : mAlivcLivePlayerMap.values()) {
            alivcLivePlayer.stopPlay();
            alivcLivePlayer.destroy();
        }
        mAlivcLivePlayerMap.clear();
    }

    public void clearLiveMixTranscodingConfig() {
        mAlivcLivePusher.setLiveMixTranscodingConfig(null);
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener) {
        this.mInteractLivePushPullListener = listener;
    }

    public void setMultiInteractLivePushPullListener(MultiInteractLivePushPullListener listener) {
        this.mMultiInteractLivePushPullListener = listener;
    }
}
