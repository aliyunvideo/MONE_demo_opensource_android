package com.alivc.live.pusher.demo.pk;

import android.content.Context;
import android.text.TextUtils;
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
import com.alivc.live.pusher.demo.bean.MultiAlivcPKLivePlayer;
import com.alivc.live.pusher.demo.interactlive.LivePushGlobalConfig;
import com.alivc.live.pusher.demo.listener.InteractLivePushPullListener;
import com.alivc.live.pusher.demo.listener.MultiInteractLivePushPullListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PKLiveManager {

    private static final String TAG = "PKLiveManager";
    private AlivcLivePushConfig mAlivcLivePushConfig;
    private FrameLayout mAudienceFrameLayout;
    //多人PK混流
    private final ArrayList<AlivcLiveMixStream> mMultiPKLiveMixStreamsArray = new ArrayList<>();
    //多人PK Config
    private final AlivcLiveTranscodingConfig mMixPKLiveTranscodingConfig = new AlivcLiveTranscodingConfig();

    private Context mContext;
    private boolean mIsPlaying = false;
    private AlivcLivePusher mAlivcLivePusher;
    private MultiAlivcPKLivePlayer mAlivcLivePlayer;
    private final Map<String,MultiAlivcPKLivePlayer> mAlivcLivePlayerMap = new HashMap<>();
    private InteractLivePushPullListener mPKLivePushPullListener;
    private MultiInteractLivePushPullListener mMultiPKLivePushPullListener;
    private String mPushUrl;
    private String mPullUrl;
    private boolean mHasPulled = false;
    private boolean mHasPushed = false;

    public void init(Context context){
        mContext = context.getApplicationContext();
        initLivePusherSDK();
        initPlayer();
    }

    private void initLivePusherSDK(){
        AlivcLiveBase.registerSDK();
        // 初始化推流配置类
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
        // 分辨率540P，最大支持720P
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
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPushError();
                }
                if (mMultiPKLivePushPullListener != null) {
                    mMultiPKLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.d(TAG, "onSDKError: ");
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPushError();
                }
                if (mMultiPKLivePushPullListener != null) {
                    mMultiPKLivePushPullListener.onPushError();
                }
            }
        });

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPreviewStarted: ");
            }

            @Override
            public void onPreviewStoped(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPreviewStoped: ");
            }

            @Override
            public void onPushStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPushStarted: ");
                mHasPushed = false;
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPushSuccess();
                }
                if (mMultiPKLivePushPullListener != null) {
                    mMultiPKLivePushPullListener.onPushSuccess();
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
            }

            @Override
            public void onAdjustFps(AlivcLivePusher alivcLivePusher, int i, int i1) {
            }

            @Override
            public void onPushStatistics(AlivcLivePusher alivcLivePusher, AlivcLivePushStatsInfo alivcLivePushStatsInfo) {
            }

            @Override
            public void onSetLiveMixTranscodingConfig(AlivcLivePusher alivcLivePusher, boolean b, String s) {

            }
        });

        mAlivcLivePusher.setLivePushNetworkListener(new AlivcLivePushNetworkListener() {
            @Override
            public void onNetworkPoor(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onNetworkRecovery(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onReconnectStart(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onConnectionLost(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onReconnectFail(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onReconnectSucceed(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onSendDataTimeout(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onConnectFail(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public String onPushURLAuthenticationOverdue(AlivcLivePusher alivcLivePusher) {
                return null;
            }

            @Override
            public void onSendMessage(AlivcLivePusher alivcLivePusher) {

            }

            @Override
            public void onPacketsLost(AlivcLivePusher alivcLivePusher) {

            }
        });
    }

    private void initPlayer(){
        mAlivcLivePlayer = new MultiAlivcPKLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        mAlivcLivePlayer.setPlayInfoListener(new AlivcLivePlayInfoListener() {
            @Override
            public void onPlayStarted() {
                Log.d(TAG, "onPlayStarted: ");
                mIsPlaying = true;
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPullSuccess();
                }
            }

            @Override
            public void onPlayStopped() {
                Log.d(TAG, "onPlayStopped: ");
                mIsPlaying = false;
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPullStop();
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
                if(mPKLivePushPullListener != null){
                    mPKLivePushPullListener.onPullError(alivcLivePlayError,s);
                }
            }
        });
    }

    /**
     * 创建 AlivcLivePlayer，用于多人 PK
     * @param userKey 区分 AlivcLivePlayer 的 key
     */
    public boolean createAlivcLivePlayer(String userKey){
        if(mAlivcLivePlayerMap.containsKey(userKey)){
            return false;
        }
        MultiAlivcPKLivePlayer alivcLivePlayer = new MultiAlivcPKLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        alivcLivePlayer.setUserKey(userKey);
        alivcLivePlayer.setMultiInteractPlayInfoListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String userKey) {
                if(mMultiPKLivePushPullListener != null){
                    mMultiPKLivePushPullListener.onPullSuccess(userKey);
                }
            }

            @Override
            public void onPullError(String userKey, AlivcLivePlayError errorType, String errorMsg) {
                if(mMultiPKLivePushPullListener != null){
                    mMultiPKLivePushPullListener.onPullError(userKey,errorType,errorMsg);
                }
            }

            @Override
            public void onPullStop(String userKey) {
                if(mMultiPKLivePushPullListener != null){
                    mMultiPKLivePushPullListener.onPullStop(userKey);
                }
            }

            @Override
            public void onPushSuccess() {
                if(mMultiPKLivePushPullListener != null){
                    mMultiPKLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPushError() {
                if(mMultiPKLivePushPullListener != null){
                    mMultiPKLivePushPullListener.onPushError();
                }
            }
        });
        mAlivcLivePlayerMap.put(userKey,alivcLivePlayer);
        return true;
    }

    public boolean isPushing(){
        return mAlivcLivePusher.isPushing();
    }

    public boolean isPulling(){
        return mIsPlaying;
    }

    public boolean isPulling(String key){
        MultiAlivcPKLivePlayer multiAlivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(multiAlivcLivePlayer != null){
            return multiAlivcLivePlayer.isPulling();
        }
        return false;
    }

    public void startPreviewAndPush(FrameLayout frameLayout,String url,boolean isAnchor){
        this.mPushUrl = url;
        mAlivcLivePusher.startPreview(mContext,frameLayout,isAnchor);
        Log.e(TAG, "startPreviewAndPush: " + mPushUrl );
        mAlivcLivePusher.startPushAysnc(mPushUrl);
    }

    public void stopPush(){
        if(mAlivcLivePusher.isPushing()){
            mAlivcLivePusher.stopPush();
        }
    }

    public void setPullView(FrameLayout frameLayout,boolean isAnchor){
        this.mAudienceFrameLayout = frameLayout;
        AlivcLivePlayConfig config = new AlivcLivePlayConfig();
        config.isFullScreen = isAnchor;
        mAlivcLivePlayer.setupWithConfig(config);
        mAlivcLivePlayer.setPlayView(frameLayout);
    }

    public void setPullView(String key,FrameLayout frameLayout,boolean isAnchor){
        AlivcLivePlayConfig config = new AlivcLivePlayConfig();
        config.isFullScreen = isAnchor;
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(alivcLivePlayer != null){
            alivcLivePlayer.setupWithConfig(config);
            alivcLivePlayer.setPlayView(frameLayout);
        }
    }

    public void startPull(String url){
        this.mPullUrl = url;
        Log.e(TAG, "startPull: " + url);
        mAlivcLivePlayer.startPlay(url);
    }

    /**
     * 多人 PK 场景使用
     * @param key  用于区分多人 PK
     * @param url  PK 拉流 URL
     */
    public void startPull(String key,String url){
        this.mPullUrl = url;
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(alivcLivePlayer != null){
            alivcLivePlayer.startPlay(url);
        }
    }

    public void stopPull() {
        mAlivcLivePlayer.stopPlay();
        mHasPulled = false;
    }

    public void stopPull(String key){
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(alivcLivePlayer != null){
            alivcLivePlayer.stopPlay();
        }
        mAlivcLivePlayerMap.remove(key);
    }

    public void pause(){
        if(isPushing()){
            mAlivcLivePusher.pause();
            mHasPushed = true;
        }
        if(isPulling()){
            mAlivcLivePlayer.stopPlay();
            mHasPulled = true;
        }
    }

    public void pause(String key){
        //TODO
    }

    public void resume(){
        if(mHasPushed){
            mAlivcLivePusher.resumeAsync();
        }
        if(mHasPulled){
            startPull(mPullUrl);
        }
    }

    public void resume(String key){
        //TODO
    }

    public void switchCamera() {
        if(mAlivcLivePusher.isPushing()){
            mAlivcLivePusher.switchCamera();
        }
    }

    public void setLiveMixTranscodingConfig(String anchorId,String audience){
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

    public void addAnchorMixTranscodingConfig(String anchorId){
        if(TextUtils.isEmpty(anchorId)){
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
            return ;
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

        mMultiPKLiveMixStreamsArray.add(anchorMixStream);
        mMixPKLiveTranscodingConfig.setMixStreams(mMultiPKLiveMixStreamsArray);

        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixPKLiveTranscodingConfig);
        }
    }

    public void addAudienceMixTranscodingConfig(String audience,FrameLayout frameLayout){
        if (TextUtils.isEmpty(audience)) {
            return;
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        audienceMixStream.setUserId(audience);
        int[] location = new int[2];
        frameLayout.getLocationOnScreen(location);
        audienceMixStream.setX(location[0] / 2);
//        audienceMixStream.setY(location[1] / 3);
        int size = mMultiPKLiveMixStreamsArray.size();
        audienceMixStream.setY(size / 2 * frameLayout.getHeight() / 2);
        audienceMixStream.setWidth(frameLayout.getWidth() / 2);
        audienceMixStream.setHeight(frameLayout.getHeight() / 2);
        audienceMixStream.setZOrder(2);
        Log.d(TAG, "AlivcRTC audienceMixStream --- " + audienceMixStream.getUserId() + ", " + audienceMixStream.getWidth() + ", " + audienceMixStream.getHeight()
                + ", " + location[0] + ", " + location[1] + ", " + audienceMixStream.getZOrder());
        mMultiPKLiveMixStreamsArray.add(audienceMixStream);
        mMixPKLiveTranscodingConfig.setMixStreams(mMultiPKLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixPKLiveTranscodingConfig);
        }
    }

    public void clearLiveMixTranscodingConfig(){
        mAlivcLivePusher.setLiveMixTranscodingConfig(null);
    }

    public void removeLiveMixTranscodingConfig(String userId){
        if(TextUtils.isEmpty(userId)){
            return ;
        }
        for (AlivcLiveMixStream alivcLiveMixStream : mMultiPKLiveMixStreamsArray) {
            if(userId.equals(alivcLiveMixStream.getUserId())){
                mMultiPKLiveMixStreamsArray.remove(alivcLiveMixStream);
                break;
            }
        }
        //Array 中只剩主播 id，说明无人连麦
        if(mMultiPKLiveMixStreamsArray.size() == 1 && mMultiPKLiveMixStreamsArray.get(0).getUserId().equals(userId)){
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
        }else{
            mMixPKLiveTranscodingConfig.setMixStreams(mMultiPKLiveMixStreamsArray);
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(mMixPKLiveTranscodingConfig);
            }
        }
    }

    public void release(){
        stopPull();
        stopPush();

        mAlivcLivePusher.destroy();
        mAlivcLivePlayer.destroy();

        for (AlivcLivePlayer alivcLivePlayer : mAlivcLivePlayerMap.values()) {
            alivcLivePlayer.destroy();
        }
        mAlivcLivePlayerMap.clear();

        mMultiPKLiveMixStreamsArray.clear();
        mMixPKLiveTranscodingConfig.setMixStreams(null);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixPKLiveTranscodingConfig);
        }
    }

    public void setPKLivePushPullListener(InteractLivePushPullListener listener){
        this.mPKLivePushPullListener = listener;
    }

    public void setMultiPKLivePushPullListener(MultiInteractLivePushPullListener listener){
        this.mMultiPKLivePushPullListener = listener;
    }
}
