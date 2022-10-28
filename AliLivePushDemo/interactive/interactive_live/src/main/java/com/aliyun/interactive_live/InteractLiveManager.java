package com.aliyun.interactive_live;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
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
import com.aliyun.interactive_common.listener.InteractLivePushPullListener;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;
import com.aliyun.interactive_common.utils.LivePushGlobalConfig;
import com.aliyun.interactive_common.utils.URLUtils;
import com.aliyun.interactive_live.bean.MultiAlivcLivePlayer;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.source.UrlSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InteractLiveManager {

    private static final String TAG = "InteractLiveManager";
    private AlivcLivePushConfig mAlivcLivePushConfig;
    private FrameLayout mAudienceFrameLayout;
    //多人连麦混流
    private final ArrayList<AlivcLiveMixStream> mMultiInteractLiveMixStreamsArray = new ArrayList<>();
    //多人连麦 Config
    private final AlivcLiveTranscodingConfig mMixInteractLiveTranscodingConfig = new AlivcLiveTranscodingConfig();

    private InteractLiveManager(){}

    private static InteractLiveManager mInstance = null;

    public static InteractLiveManager getInstance(){
        if(mInstance == null){
            synchronized (InteractLiveManager.class){
                if(mInstance == null){
                    mInstance = new InteractLiveManager();
                }
            }
        }
        return mInstance;
    }

    private Context mContext;
    private boolean mIsPlaying = false;
    private AliPlayer mAliPlayer;
    private AlivcLivePusher mAlivcLivePusher;
    private MultiAlivcLivePlayer mAlivcLivePlayer;
    private final Map<String, MultiAlivcLivePlayer> mAlivcLivePlayerMap = new HashMap<>();
    private InteractLivePushPullListener mInteractLivePushPullListener;
    private MultiInteractLivePushPullListener mMultiInteractLivePushPullListener;
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
//        mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_540P);
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
                if(mInteractLivePushPullListener != null){
                    mInteractLivePushPullListener.onPushError();
                }
            }

            @Override
            public void onSDKError(AlivcLivePusher alivcLivePusher, AlivcLivePushError alivcLivePushError) {
                Log.d(TAG, "onSDKError: ");
                if(mInteractLivePushPullListener != null){
                    mInteractLivePushPullListener.onPushError();
                }
            }
        });

        mAlivcLivePusher.setLivePushInfoListener(new AlivcLivePushInfoListener() {
            @Override
            public void onPreviewStarted(AlivcLivePusher alivcLivePusher) {
                Log.d(TAG, "onPreviewStarted: ");
                if(mInteractLivePushPullListener != null){
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
                if(mInteractLivePushPullListener != null){
                    mInteractLivePushPullListener.onPullSuccess();
                }
            }

            @Override
            public void onPlayStopped() {
                Log.d(TAG, "onPlayStopped: ");
                mIsPlaying = false;
                if(mInteractLivePushPullListener != null){
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
                if(mInteractLivePushPullListener != null){
                    mInteractLivePushPullListener.onPullError(alivcLivePlayError,s);
                }
            }
        });
    }

    /**
     * 创建 AlivcLivePlayer，用于多人连麦互动
     * @param audienceId 区分 AlivcLivePlayer 的 key
     */
    public boolean createAlivcLivePlayer(String audienceId){
        if(mAlivcLivePlayerMap.containsKey(audienceId)){
            return false;
        }
        MultiAlivcLivePlayer alivcLivePlayer = new MultiAlivcLivePlayer(mContext, AlivcLiveMode.AlivcLiveInteractiveMode);
        alivcLivePlayer.setAudienceId(audienceId);
        alivcLivePlayer.setMultiInteractPlayInfoListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String audienceId) {
                if(mMultiInteractLivePushPullListener != null){
                    mMultiInteractLivePushPullListener.onPullSuccess(audienceId);
                }
            }

            @Override
            public void onPullError(String audienceId, AlivcLivePlayError errorType, String errorMsg) {
                if(mMultiInteractLivePushPullListener != null){
                    mMultiInteractLivePushPullListener.onPullError(audienceId,errorType,errorMsg);
                }
            }

            @Override
            public void onPullStop(String audienceId) {
                if(mMultiInteractLivePushPullListener != null){
                    mMultiInteractLivePushPullListener.onPullStop(audienceId);
                }
            }

            @Override
            public void onPushSuccess() {
                if(mMultiInteractLivePushPullListener != null){
                    mMultiInteractLivePushPullListener.onPushSuccess();
                }
            }

            @Override
            public void onPushError() {
                if(mMultiInteractLivePushPullListener != null){
                    mMultiInteractLivePushPullListener.onPushError();
                }
            }
        });
        mAlivcLivePlayerMap.put(audienceId,alivcLivePlayer);
        return true;
    }

    public boolean isPushing(){
        return mAlivcLivePusher.isPushing();
    }

    public boolean isPulling(){
        return mIsPlaying;
    }

    public boolean isPulling(String key){
        MultiAlivcLivePlayer multiAlivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(multiAlivcLivePlayer != null){
            return multiAlivcLivePlayer.isPulling();
        }
        return false;
    }

    public void startPreviewAndPush(FrameLayout frameLayout,String url,boolean isAnchor){
        this.mPushUrl = url;
        mAlivcLivePusher.startPreview(mContext,frameLayout,isAnchor);
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

    public void setPullView(SurfaceHolder surfaceHolder){
        mAliPlayer.setDisplay(surfaceHolder);
    }

    public void startPull(String url){
        this.mPullUrl = url;
        if(url.startsWith(URLUtils.ARTC)){
            mAlivcLivePlayer.startPlay(url);
        }else{
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(url);
            mAliPlayer.setDataSource(urlSource);
            mAliPlayer.prepare();
        }
    }

    public void startPull(String key,String url){
        this.mPullUrl = url;
        if(url.startsWith(URLUtils.ARTC)){
            AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
            if(alivcLivePlayer != null){
                alivcLivePlayer.startPlay(url);
            }
        }else{
            UrlSource urlSource = new UrlSource();
            urlSource.setUri(url);
            mAliPlayer.setDataSource(urlSource);
            mAliPlayer.prepare();
        }
    }

    public void stopPull() {
        if(isPulling()){
            mAlivcLivePlayer.stopPlay();
        }
        mHasPulled = false;
    }

    public void stopPull(String key){
        AlivcLivePlayer alivcLivePlayer = mAlivcLivePlayerMap.get(key);
        if(alivcLivePlayer != null){
            alivcLivePlayer.stopPlay();
        }
        mAlivcLivePlayerMap.remove(key);
    }

    public void stopCDNPull() {
        mAliPlayer.stop();
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
        if (TextUtils.isEmpty(anchorId) && TextUtils.isEmpty(audience)) {
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
            anchorMixStream.setWidth(mAlivcLivePushConfig.getWidth());
            anchorMixStream.setHeight(mAlivcLivePushConfig.getHeight());
            anchorMixStream.setZOrder(1);
            Log.d(TAG, "AlivcRTC anchorMixStream --- " + anchorMixStream.getUserId() + ", " + anchorMixStream.getWidth() + ", " + anchorMixStream.getHeight()
                    + ", " + anchorMixStream.getX() + ", " + anchorMixStream.getY() + ", " + anchorMixStream.getZOrder());
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        if (mAudienceFrameLayout != null) {
            audienceMixStream.setUserId(audience);
            audienceMixStream.setX((int) mAudienceFrameLayout.getX() / 3);
            audienceMixStream.setY((int) mAudienceFrameLayout.getY() / 3);
            audienceMixStream.setWidth(mAudienceFrameLayout.getWidth() / 2);
            audienceMixStream.setHeight(mAudienceFrameLayout.getHeight() / 2);

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

        mMultiInteractLiveMixStreamsArray.add(anchorMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);

        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    public void addAudienceMixTranscodingConfig(String audience,FrameLayout frameLayout){
        if (TextUtils.isEmpty(audience)) {
            return;
        }
        AlivcLiveMixStream audienceMixStream = new AlivcLiveMixStream();
        audienceMixStream.setUserId(audience);
        audienceMixStream.setX((int) frameLayout.getX() / 2);
        audienceMixStream.setY((int) frameLayout.getY() / 3);
        audienceMixStream.setWidth(frameLayout.getWidth() / 2);
        audienceMixStream.setHeight(frameLayout.getHeight() / 2);
        audienceMixStream.setZOrder(2);
        Log.d(TAG, "AlivcRTC audienceMixStream --- " + audienceMixStream.getUserId() + ", " + audienceMixStream.getWidth() + ", " + audienceMixStream.getHeight()
                + ", " + audienceMixStream.getX() + ", " + audienceMixStream.getY() + ", " + audienceMixStream.getZOrder());
        mMultiInteractLiveMixStreamsArray.add(audienceMixStream);
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
        }
    }

    public void clearLiveMixTranscodingConfig(){
        mAlivcLivePusher.setLiveMixTranscodingConfig(null);
    }

    public void removeAudienceLiveMixTranscodingConfig(String audience,String anchorId){
        if(TextUtils.isEmpty(audience)){
            return ;
        }
        for (AlivcLiveMixStream alivcLiveMixStream : mMultiInteractLiveMixStreamsArray) {
            if(audience.equals(alivcLiveMixStream.getUserId())){
                mMultiInteractLiveMixStreamsArray.remove(alivcLiveMixStream);
                break;
            }
        }
        //Array 中只剩主播 id，说明无人连麦
        if(mMultiInteractLiveMixStreamsArray.size() == 1 && mMultiInteractLiveMixStreamsArray.get(0).getUserId().equals(anchorId)){
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(null);
            }
        }else{
            mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
            if (mAlivcLivePusher != null) {
                mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            }
        }
    }

    public void release(){
        stopCDNPull();
        stopPull();
        stopPush();

        mMultiInteractLiveMixStreamsArray.clear();
        mMixInteractLiveTranscodingConfig.setMixStreams(mMultiInteractLiveMixStreamsArray);
        if (mAlivcLivePusher != null) {
            mAlivcLivePusher.setLiveMixTranscodingConfig(mMixInteractLiveTranscodingConfig);
            mAlivcLivePusher.destroy();
        }

        mAliPlayer.release();
        mAlivcLivePlayer.destroy();

        for (AlivcLivePlayer alivcLivePlayer : mAlivcLivePlayerMap.values()) {
            alivcLivePlayer.destroy();
        }
        mAlivcLivePlayerMap.clear();
    }

    public void setInteractLivePushPullListener(InteractLivePushPullListener listener){
        this.mInteractLivePushPullListener = listener;
    }

    public void setMultiInteractLivePushPullListener(MultiInteractLivePushPullListener listener){
        this.mMultiInteractLivePushPullListener = listener;
    }
}
