package com.alivc.live.interactive_common;

import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mAlivcLivePushConfig;
import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mWaterMarkInfos;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.alivc.component.custom.AlivcLivePushCustomFilter;
import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.annotations.AlivcLiveNetworkQuality;
import com.alivc.live.annotations.AlivcLivePushKickedOutType;
import com.alivc.live.annotations.AlivcLiveRecordMediaEvent;
import com.alivc.live.beauty.BeautyFactory;
import com.alivc.live.beauty.BeautyInterface;
import com.alivc.live.beauty.constant.BeautySDKType;
import com.alivc.live.commonbiz.seidelay.SEIDelayManager;
import com.alivc.live.commonbiz.seidelay.SEISourceType;
import com.alivc.live.commonbiz.seidelay.api.ISEIDelayEventListener;
import com.alivc.live.commonbiz.test.URLUtils;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.bean.InteractiveLivePlayer;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.manager.TimestampWatermarkManager;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.player.AlivcLivePlayConfig;
import com.alivc.live.player.AlivcLivePlayer;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.player.annotations.AlivcLivePlayVideoStreamType;
import com.alivc.live.pusher.AlivcAudioChannelEnum;
import com.alivc.live.pusher.AlivcAudioSampleRateEnum;
import com.alivc.live.pusher.AlivcImageFormat;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLiveLocalRecordConfig;
import com.alivc.live.pusher.AlivcLiveMixStream;
import com.alivc.live.pusher.AlivcLivePushError;
import com.alivc.live.pusher.AlivcLivePushErrorListener;
import com.alivc.live.pusher.AlivcLivePushExternalAudioStreamConfig;
import com.alivc.live.pusher.AlivcLivePushInfoListener;
import com.alivc.live.pusher.AlivcLivePushNetworkListener;
import com.alivc.live.pusher.AlivcLivePushStatsInfo;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcLivePusherAudioDataSample;
import com.alivc.live.pusher.AlivcLiveTranscodingConfig;
import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcSoundFormat;
import com.alivc.live.pusher.WaterMarkInfo;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InteractLiveBaseManager {

    protected static final String TAG = InteractLiveBaseManager.class.getSimpleName();

    protected InteractiveMode mInteractiveMode;

    // 这个TM用来做混流的
    protected FrameLayout mAudienceFrameLayout;

    //多人连麦混流
    protected final ArrayList<AlivcLiveMixStream> mMultiInteractLiveMixStreamsArray = new ArrayList<>();
    //多人连麦 Config
    protected final AlivcLiveTranscodingConfig mMixInteractLiveTranscodingConfig = new AlivcLiveTranscodingConfig();

    protected Context mContext;

    protected AlivcLivePusher mAlivcLivePusher;
    private InteractiveUserData mPushUserData;

    protected final Map<String, InteractiveLivePlayer> mInteractiveLivePlayerMap = new HashMap<>();
    protected final Map<String, InteractiveUserData> mInteractiveUserDataMap = new HashMap<>();

    protected static InteractLivePushPullListener mInteractLivePushPullListener;

    // 美颜处理类，需对接Queen美颜SDK，且拥有License拥有美颜能力
    private BeautyInterface mBeautyManager;
    // CameraId，美颜需要使用
    private int mCameraId;

    // 互动模式下的水印处理类
    private TimestampWatermarkManager mTimestampWatermarkManager;

    protected final SEIDelayManager mSEIDelayManager = new SEIDelayManager();

    public void init(Context context, InteractiveMode interactiveMode) {
        mInteractiveMode = interactiveMode;
        mContext = context.getApplicationContext();
        initLivePusher(interactiveMode);
        initWatermarks();
        mSEIDelayManager.registerReceiver(new ISEIDelayEventListener() {
            @Override
            public void onEvent(String src, String type, String msg) {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onReceiveSEIDelay(src, type, msg);
                }
            }
        });
    }

    private void initLivePusher(InteractiveMode interactiveMode) {
        AlivcLiveBase.registerSDK();
        // 初始化推流配置类
        mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
        // 设置H5兼容模式（Web端连麦互通）
        mAlivcLivePushConfig.setH5CompatibleMode(LivePushGlobalConfig.IS_H5_COMPATIBLE);

        // 互动模式下开启RTS推拉裸流(直推&直拉，不同于直播连麦)
        if (InteractiveMode.isBareStream(interactiveMode)) {
            mAlivcLivePushConfig.setEnableRTSForInteractiveMode(true);
        }

        if (mAlivcLivePushConfig.isExternMainStream()) {
            mAlivcLivePushConfig.setExternMainStream(true, AlivcImageFormat.IMAGE_FORMAT_YUVNV12, AlivcSoundFormat.SOUND_FORMAT_S16);
            mAlivcLivePushConfig.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE);
            mAlivcLivePushConfig.setAudioSampleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100);
        }

        // 开启 Data Channel 自定义消息通道
        if (LivePushGlobalConfig.IS_DATA_CHANNEL_MESSAGE_ENABLE) {
            mAlivcLivePushConfig.setEnableDataChannelMessage(true);
        }

        mAlivcLivePusher = new AlivcLivePusher();
        mAlivcLivePusher.init(mContext.getApplicationContext(), mAlivcLivePushConfig);
        mCameraId = mAlivcLivePushConfig.getCameraType();

        // 设置音量回调频率和平滑系数（仅互动模式下生效）
        mAlivcLivePusher.enableAudioVolumeIndication(300, 3, 1);

        mAlivcLivePusher.setLivePushErrorListener(new AlivcLivePushErrorListener() {
            @Override
            public void onSystemError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.e(TAG, "onSystemError: " + alivcLivePushError);
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.e(TAG, "onSDKError: " + alivcLivePushError);
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
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
            public void onPreviewStopped(AlivcLivePusher pusher) {
                Log.d(TAG, "onPreviewStopped: ");
            }

            @Override
            public void onPushStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushStarted: ");
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPushPaused(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushPaused: ");
            }

            @Override
            public void onPushResumed(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushResumed: ");
            }

            @Override
            public void onPushStopped(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushStopped: ");
                pusher.stopPreview();
            }

            @Override
            public void onPushRestarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushRestarted: ");
            }

            @Override
            public void onFirstFramePushed(AlivcLivePusher pusher) {
                Log.d(TAG, "onFirstFramePushed: ");
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
            public void onAdjustBitrate(AlivcLivePusher pusher, int currentBitrate, int targetBitrate) {
                Log.i(TAG, "onAdjustBitrate: " + currentBitrate + "->" + targetBitrate);
            }

            @Override
            public void onAdjustFps(AlivcLivePusher alivcLivePusher, int i, int i1) {
                Log.d(TAG, "onAdjustFps: ");
            }

            @Override
            public void onPushStatistics(AlivcLivePusher alivcLivePusher, AlivcLivePushStatsInfo alivcLivePushStatsInfo) {
                // Log.i(TAG, "onPushStatistics: " + alivcLivePushStatsInfo);
            }

            @Override
            public void onSetLiveMixTranscodingConfig(AlivcLivePusher alivcLivePusher, boolean b, String s) {
                Log.d(TAG, "onSetLiveMixTranscodingConfig: ");
            }

            @Override
            public void onKickedOutByServer(AlivcLivePusher pusher, AlivcLivePushKickedOutType kickedOutType) {
                Log.d(TAG, "onKickedOutByServer: " + kickedOutType);
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onMicrophoneVolumeUpdate(AlivcLivePusher pusher, int volume) {
                // 麦克风音量回调（仅互动模式下生效，需设置AlivcLivePusher#enableAudioVolumeIndication接口）
                // Log.d(TAG, "onMicrophoneVolumeUpdate: " + volume);
            }

            @Override
            public void onLocalRecordEvent(AlivcLiveRecordMediaEvent mediaEvent, String storagePath) {
                ToastUtils.show(mediaEvent + ", " + storagePath);
            }

            @Override
            public void onScreenFramePushState(AlivcLivePusher pusher, boolean isPushing) {
                ToastUtils.show("onScreenFramePushState: " + isPushing);
            }

            @Override
            public void onRemoteUserEnterRoom(AlivcLivePusher pusher, String userId, boolean isOnline) {
                ToastUtils.show("onRemoteUserEnterRoom: " + userId + ", isOnline" + isOnline);
            }

            @Override
            public void onRemoteUserAudioStream(AlivcLivePusher pusher, String userId, boolean isPushing) {
                ToastUtils.show("onRemoteUserAudioStream: " + userId + ", isPushing: " + isPushing);
            }

            @Override
            public void onRemoteUserVideoStream(AlivcLivePusher pusher, String userId, AlivcLivePlayVideoStreamType videoStreamType, boolean isPushing) {
                ToastUtils.show("onRemoteUserVideoStream: " + userId + ", videoStreamType: " + videoStreamType + ", isPushing: " + isPushing);
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
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onConnectionLost();
                }
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
            public void onNetworkQualityChanged(AlivcLiveNetworkQuality upQuality, AlivcLiveNetworkQuality downQuality) {

            }

            @Override
            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushURLAuthenticationOverdue: ");
                return null;
            }

            @Override
            public void onPushURLTokenWillExpire(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushURLTokenWillExpire: ");
            }

            @Override
            public void onPushURLTokenExpired(AlivcLivePusher pusher) {
                Log.d(TAG, "onPushURLTokenExpired: ");
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

        // 【美颜】注册视频前处理回调
        mAlivcLivePusher.setCustomFilter(new AlivcLivePushCustomFilter() {
            @Override
            public void customFilterCreate() {
                initBeautyManager();
            }

            @Override
            public int customFilterProcess(int inputTexture, int textureWidth, int textureHeight, long extra) {
                if (mBeautyManager == null) {
                    return inputTexture;
                }

                return mBeautyManager.onTextureInput(inputTexture, textureWidth, textureHeight);
            }

            @Override
            public void customFilterDestroy() {
                destroyBeautyManager();
            }
        });
    }

    private void destroyLivePusher() {
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.destroy();
        }
    }

    public boolean isPushing() {
        return mAlivcLivePusher != null && mAlivcLivePusher.isPushing();
    }

    public boolean isPulling(InteractiveUserData userData) {
        if (userData == null) {
            return false;
        }
        InteractiveLivePlayer interactiveLivePlayer = mInteractiveLivePlayerMap.get(userData.getKey());
        if (interactiveLivePlayer != null) {
            return interactiveLivePlayer.isPulling();
        }
        return false;
    }

    private void initWatermarks() {
        final float x;
        final float y;
        final float width;

        if (!mWaterMarkInfos.isEmpty()) {
            WaterMarkInfo waterMarkInfo = mWaterMarkInfos.get(0);
            x = waterMarkInfo.mWaterMarkCoordX;
            y = waterMarkInfo.mWaterMarkCoordY;
            width = waterMarkInfo.mWaterMarkWidth;
        } else {
            x = 0f;
            y = 0f;
            width = 1;
        }

        // 互动模式下使用该接口添加水印，最多添加一个，多则替代
        mTimestampWatermarkManager = new TimestampWatermarkManager();
        mTimestampWatermarkManager.init(new TimestampWatermarkManager.OnWatermarkListener() {
            @Override
            public void onWatermarkUpdate(Bitmap bitmap) {
                if (mAlivcLivePusher != null) {
                    mAlivcLivePusher.addWaterMark(bitmap, x, y, width);
                }
            }
        });
    }

    private void destroyWatermarks() {
        if (mTimestampWatermarkManager != null) {
            mTimestampWatermarkManager.destroy();
            mTimestampWatermarkManager = null;
        }
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener) {
        mInteractLivePushPullListener = listener;
    }

    private boolean createAlivcLivePlayer(InteractiveUserData userData) {
        if (userData == null) {
            return false;
        }

        String userDataKey = userData.getKey();
        if (mInteractiveLivePlayerMap.containsKey(userDataKey)) {
            Log.i(TAG, "createAlivcLivePlayer already created " + userDataKey);
            return false;
        }

        Log.d(TAG, "createAlivcLivePlayer: " + userDataKey);
        InteractiveLivePlayer alivcLivePlayer = new InteractiveLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        alivcLivePlayer.setPullUserData(userData);
        alivcLivePlayer.setMultiInteractPlayInfoListener(new InteractLivePushPullListener() {
            @Override
            public void onPullSuccess(InteractiveUserData userData) {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullSuccess(userData);
                }
            }

            @Override
            public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullError(userData, errorType, errorMsg);
                }
            }

            @Override
            public void onPullStop(InteractiveUserData userData) {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPullStop(userData);
                }
            }

            @Override
            public void onPushSuccess() {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPushError() {
                if (mInteractLivePushPullListener != null) {
                    mInteractLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onReceiveSEIMessage(int payload, byte[] data) {
                super.onReceiveSEIMessage(payload, data);
                String sei = new String(data, StandardCharsets.UTF_8);
                mSEIDelayManager.receiveSEI(SEISourceType.RTC, sei);
            }
        });
        mInteractiveLivePlayerMap.put(userDataKey, alivcLivePlayer);
        mInteractiveUserDataMap.put(userDataKey, userData);
        return true;
    }

    public void startPreviewAndPush(InteractiveUserData userData, FrameLayout frameLayout, boolean isAnchor) {
        if (userData == null) {
            Log.e(TAG, "startPreviewAndPush, userData invalid!");
            return;
        }

        mPushUserData = userData;
        mAlivcLivePusher.startPreview(mContext, frameLayout, isAnchor);

        String pushUrl = userData.url;
        if (InteractiveMode.isInteractive(mInteractiveMode)) {
            pushUrl = URLUtils.generateInteractivePushUrl(userData.channelId, userData.userId);
        }

        if (TextUtils.isEmpty(pushUrl)) {
            Log.e(TAG, "startPreviewAndPush, pushUrl invalid!");
            return;
        }

        Log.d(TAG, "startPreviewAndPush: " + isAnchor + ", " + pushUrl);
        mAlivcLivePusher.startPushAsync(pushUrl);

        mSEIDelayManager.registerProvider(userData.userId, new ISEIDelayEventListener() {
            @Override
            public void onEvent(String src, String type, String msg) {
                sendCustomMessage(msg);
            }
        });
    }

    public void stopPreview() {
        mAlivcLivePusher.stopPreview();
    }

    public void stopPush() {
        if (isPushing()) {
            mAlivcLivePusher.stopPush();
        }
    }

    public void stopCamera() {
        mAlivcLivePusher.stopCamera();
    }

    public void setPullView(InteractiveUserData userData, FrameLayout frameLayout, boolean isAnchor) {
        if (userData == null || frameLayout == null) {
            return;
        }

        this.mAudienceFrameLayout = frameLayout;

        createAlivcLivePlayer(userData);

        AlivcLivePlayer livePlayer = mInteractiveLivePlayerMap.get(userData.getKey());
        if (livePlayer == null) {
            Log.e(TAG, "setPullView error: livePlayer is empty");
            return;
        }

        AlivcLivePlayConfig config = new AlivcLivePlayConfig();
        config.isFullScreen = isAnchor;
        config.videoStreamType = userData.videoStreamType;
        livePlayer.setupWithConfig(config);
        livePlayer.setPlayView(frameLayout);
        Log.i(TAG, "setPullView: " + userData);
    }

    public void startPullRTCStream(InteractiveUserData userData) {
        if (userData == null || TextUtils.isEmpty(userData.url)) {
            Log.e(TAG, "startPull error: url is empty");
            return;
        }

        createAlivcLivePlayer(userData);

        AlivcLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(userData.getKey());
        Log.d(TAG, "startPullRTCStream: " + userData.getKey() + ", " + userData.url + ", " + alivcLivePlayer);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.startPlay(userData.url);
        }
    }

    public void stopPullRTCStream(InteractiveUserData userData) {
        if (userData == null) {
            return;
        }
        String userKey = userData.getKey();
        AlivcLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(userKey);
        Log.d(TAG, "stopPullRTCStream: " + userData.getKey() + ", " + userData.url + ", " + alivcLivePlayer);
        if (alivcLivePlayer != null) {
            alivcLivePlayer.stopPlay();
        }
        mInteractiveLivePlayerMap.remove(userKey);
        mInteractiveUserDataMap.remove(userKey);
    }

    // 暂停推流
    public void pausePush() {
        if (isPushing()) {
            mAlivcLivePusher.pause();
        }
    }

    // 恢复推流
    public void resumePush() {
        if (isPushing()) {
            mAlivcLivePusher.resume();
        }
    }

    // 停止播放RTC流
    public void pausePlayRTCStream(InteractiveUserData userData) {
        Log.d(TAG, "pausePlayRTCStream: " + userData);
        if (userData == null) {
            return;
        }

        String userKey = userData.getKey();
        InteractiveLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(userKey);
        if (alivcLivePlayer == null) {
            return;
        }

        alivcLivePlayer.pauseVideoPlaying();
    }

    // 恢复播放RTC流
    public void resumePlayRTCStream(InteractiveUserData userData) {
        Log.d(TAG, "resumePlayRTCStream: " + userData);
        if (userData == null) {
            return;
        }

        String userKey = userData.getKey();
        InteractiveLivePlayer alivcLivePlayer = mInteractiveLivePlayerMap.get(userKey);
        if (alivcLivePlayer == null) {
            return;
        }

        alivcLivePlayer.resumeVideoPlaying();
    }

    // 停止播放所有RTC流
    public void pausePlayRTCStream() {
        for (InteractiveUserData userData : mInteractiveUserDataMap.values()) {
            pausePlayRTCStream(userData);
        }
    }

    // 恢复播放所有RTC流
    public void resumePlayRTCStream() {
        for (InteractiveUserData userData : mInteractiveUserDataMap.values()) {
            resumePlayRTCStream(userData);
        }
    }

    // 发送自定义消息
    public void sendCustomMessage(String text) {
        if (LivePushGlobalConfig.IS_DATA_CHANNEL_MESSAGE_ENABLE) {
            mAlivcLivePusher.sendDataChannelMessage(text);
        } else {
            mAlivcLivePusher.sendMessage(text, 0, 0, false);
        }
    }

    // 开始本地录制
    public void startLocalRecord(AlivcLiveLocalRecordConfig recordConfig) {
        boolean flag = false;
        if (mAlivcLivePusher != null) {
            flag = mAlivcLivePusher.startLocalRecord(recordConfig);
        }
        if (!flag) {
            ToastUtils.show("start local record failed!");
        }
    }

    // 结束本地录制
    public void stopLocalRecord() {
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.stopLocalRecord();
        }
    }

    // 新增外部音频流
    public int addExternalAudioStream(AlivcLivePushExternalAudioStreamConfig externalAudioStreamConfig) {
        return mAlivcLivePusher.addExternalAudioStream(externalAudioStreamConfig);
    }

    // 删除外部音频流
    public int removeExternalAudioStream(int streamId) {
        return mAlivcLivePusher.removeExternalAudioStream(streamId);
    }

    // 输入外部音频流数据
    public int pushExternalAudioStream(int streamId, AlivcLivePusherAudioDataSample audioDataSample) {
        return mAlivcLivePusher.pushExternalAudioStream(streamId, audioDataSample);
    }

    // 设置是否与麦克风采集音频混合
    public int setMixedWithMic(boolean mixed) {
        return mAlivcLivePusher.setMixedWithMic(mixed);
    }

    // 设置外部音频流播放音量
    public int setExternalAudioStreamPlayoutVolume(int streamId, int playoutVolume) {
        return mAlivcLivePusher.setExternalAudioStreamPlayoutVolume(streamId, playoutVolume);
    }

    // 设置外部音频流推流音量
    public int setExternalAudioStreamPublishVolume(int streamId, int publishVolume) {
        return mAlivcLivePusher.setExternalAudioStreamPublishVolume(streamId, publishVolume);
    }

    // 启用外部视频输入源
    public void setExternalVideoSource(boolean enable, boolean useTexture, AlivcLivePlayVideoStreamType videoStreamType, AlivcPreviewDisplayMode previewDisplayMode) {
        mAlivcLivePusher.setExternalVideoSource(enable, useTexture, videoStreamType, previewDisplayMode);
    }

    public void inputStreamVideoData(byte[] data, int width, int height, int stride, int size, long pts, int rotation) {
        mAlivcLivePusher.inputStreamVideoData(data, width, height, stride, size, pts, rotation);
    }

    public void inputStreamAudioData(byte[] data, int size, int sampleRate, int channels, long pts) {
        mAlivcLivePusher.inputStreamAudioData(data, size, sampleRate, channels, pts);
    }

    public void switchCamera() {
        if (isPushing()) {
            mAlivcLivePusher.switchCamera();
            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
            } else {
                mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
            }
        }
    }

    public void setMute(boolean isMute) {
        mAlivcLivePusher.setMute(isMute);
    }

    public void enableSpeakerPhone(boolean enableSpeakerPhone) {
        mAlivcLivePusher.enableSpeakerphone(enableSpeakerPhone);
    }

    public void enableAudioCapture(boolean enable) {
        if (enable) {
            mAlivcLivePusher.startAudioCapture(true);
        } else {
            mAlivcLivePusher.stopAudioCapture();
        }
    }

    public void enableLocalCamera(boolean enable) {
        mAlivcLivePusher.enableLocalCamera(enable);
    }

    public void release() {
        mSEIDelayManager.destroy();

        destroyWatermarks();
        clearLiveMixTranscodingConfig();

        stopPush();
        destroyLivePusher();

        mMultiInteractLiveMixStreamsArray.clear();
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        for (AlivcLivePlayer alivcLivePlayer : mInteractiveLivePlayerMap.values()) {
            alivcLivePlayer.stopPlay();
            alivcLivePlayer.destroy();
        }

        mInteractiveLivePlayerMap.clear();
        mInteractiveUserDataMap.clear();

        mInteractLivePushPullListener = null;
    }

    // 单切混的逻辑在各自场景的manager里面，因为PK和连麦的混流布局不太一样。
    // 更新混流、混切单
    public void clearLiveMixTranscodingConfig() {
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(null);
        }
    }

    // 遍历当前混流子布局信息
    public AlivcLiveMixStream findMixStreamByUserData(InteractiveUserData userData) {
        if (userData == null || TextUtils.isEmpty(userData.userId)) {
            return null;
        }

        for (AlivcLiveMixStream alivcLiveMixStream : mMultiInteractLiveMixStreamsArray) {
            if (userData.userId.equals(alivcLiveMixStream.getUserId())
                    && alivcLiveMixStream.getMixSourceType() == InteractiveBaseUtil.covertVideoStreamType2MixSourceType(userData.videoStreamType)) {
                return alivcLiveMixStream;
            }
        }

        return null;
    }

    public InteractiveUserData getUserDataByKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }

        for (String userKey : mInteractiveUserDataMap.keySet()) {
            if (TextUtils.equals(userKey, key)) {
                return mInteractiveUserDataMap.get(userKey);
            }
        }

        return null;
    }

    // 初始化美颜相关资源
    private void initBeautyManager() {
        if (mBeautyManager == null && LivePushGlobalConfig.ENABLE_BEAUTY) {
            // v4.4.4版本-v6.1.0版本，互动模式下的美颜，处理逻辑参考BeautySDKType.INTERACT_QUEEN，即：InteractQueenBeautyImpl；
            // v6.1.0以后的版本（从v6.2.0开始），基础模式下的美颜，和互动模式下的美颜，处理逻辑保持一致，即：QueenBeautyImpl；
            mBeautyManager = BeautyFactory.createBeauty(BeautySDKType.QUEEN, mContext);
            // initialize in texture thread.
            mBeautyManager.init();
            mBeautyManager.setBeautyEnable(LivePushGlobalConfig.ENABLE_BEAUTY);
            mBeautyManager.switchCameraId(mCameraId);
        }
    }

    // 销毁美颜相关资源
    private void destroyBeautyManager() {
        if (mBeautyManager != null) {
            mBeautyManager.release();
            mBeautyManager = null;
        }
    }
}
