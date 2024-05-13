package com.alivc.auiplayer.videoepisode;


import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.dns.DomainProcessor;
import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IListPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.private_service.PrivateService;
import com.cicada.player.utils.Logger;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class AUIVideoEpisodeController {

    private final AliListPlayer aliListPlayer;
    private IPlayer preRenderPlayer;

    private int mOldPosition = 0;

    private int mCurrentPlayerState;
    private int mCurrentPlayerStateCallBack;
    private final SparseArray<String> mIndexWithUUID = new SparseArray<>();
    private PlayerListener mPlayerListener;

    // 通过接口设置，可以达到全屏效果，默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT
    // 当前SDK默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT ，即：图片以自身宽高比为准填充，较短一边未铺满全屏幕，但是会导致剩余空间透明，类似于上下黑边
    // 如果想要全屏效果，请设置为 IPlayer.ScaleMode.SCALE_ASPECT_FILL ，即：图片以自身宽高比为准填充 ，超出部分裁剪，但是会导致图片显示不全，类似于放大并显示一部分
    private static final IPlayer.ScaleMode DEFAULT_VIDEO_SCALE_MODE = IPlayer.ScaleMode.SCALE_ASPECT_FIT;

    // 通过接口设置，可以达到精准seek效果，默认是 IPlayer.SeekMode.Inaccurate
    // 当前SDK默认是 IPlayer.SeekMode.Inaccurate ，即：非精准seek
    // 如果想要精准seek，请设置为 IPlayer.SeekMode.Accurate
    private static final IPlayer.SeekMode DEFAULT_SEEK_MODE = IPlayer.SeekMode.Accurate;

    public AUIVideoEpisodeController(Context context) {
        // AF_LOG_LEVEL_INFO，默认info级别
//        Logger.getInstance(context).enableConsoleLog(true);
//        Logger.getInstance(context).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_TRACE);

        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
        aliListPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);

        preRenderPlayer = aliListPlayer.getPreRenderPlayer();

        initPlayerConfigs(context);
        initPlayerListeners();
    }

    // 播放器相关配置
    private void initPlayerConfigs(Context context) {
        //若采取智能预加载策略，则无需设置preloadCount
        //setPreloadCount(2);

        //设置预加载最大缓存内存大小，这里为500
        String preloadStrategyParam = "{\"algorithm\":\"sub\",\"offset\":\"500\"}";
        setPreloadStrategy(true, preloadStrategyParam);

        //开启本地缓存，统一约定在cache路径下的Preload目录
        String cacheDir = context.getExternalCacheDir() + File.separator + "Preload";
        enableLocalCache(true, cacheDir);
        setCacheFileClearConfig(30 * 24 * 60, 20 * 1024, 0);

        //开启增强型HTTPDNS，需要增强型HTTPDNS的高级License，否则将会失效
        enableEnhancedHTTPDNS(context, true);

        //配置网络超时重试时间与次数
        setNetworkRetryTimes(5000, 2);
    }

    // 播放器相关回调
    private void initPlayerListeners() {
        aliListPlayer.setOnPreparedListener(() -> {
            mPlayerListener.onPrepared(-1);
        });

        aliListPlayer.setOnRenderingStartListener(() -> {
            mPlayerListener.onRenderingStart(-1, aliListPlayer.getDuration());
        });

        aliListPlayer.setOnInfoListener(infoBean -> {
            long duration = aliListPlayer.getDuration();
            mPlayerListener.onInfo((int) duration, infoBean);
            toRenderingStartOnInfo();
        });

        aliListPlayer.setOnStateChangedListener(i -> {
            mCurrentPlayerStateCallBack = i;
            mPlayerListener.onPlayStateChanged(-1, mCurrentPlayerStateCallBack == IPlayer.paused);
        });

        aliListPlayer.setOnErrorListener((ErrorInfo errorInfo) -> {
            mPlayerListener.onError(errorInfo);
            aliListPlayer.stop();
        });
    }

    public void loadSource(List<AUIEpisodeVideoInfo> listVideo) {
        aliListPlayer.clear();
        mIndexWithUUID.clear();
        for (int i = 0; i < listVideo.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i, randomUUID);
            aliListPlayer.addUrl(listVideo.get(i).getUrl(), randomUUID);
        }
    }

    public void addSource(List<AUIEpisodeVideoInfo> videoBeanList) {
        for (int i = 0; i < videoBeanList.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i, randomUUID);
            aliListPlayer.addUrl(videoBeanList.get(i).getUrl(), randomUUID);
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        if (aliListPlayer != null) {
            aliListPlayer.setLoop(openLoopPlay);
        }
        if (preRenderPlayer != null) {
            preRenderPlayer.setLoop(openLoopPlay);
        }
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    // 用于剧集的跳转
    public void onPageSelected(int position) {
        Log.i("CheckFunc", "onPageSelected (int) position " + position);
        moveToPosition(position, null);
        this.mOldPosition = position;
    }

    // 用于短视频的上下滑动
    public void onPageSelected(int position, Surface surface) {
        Log.i("CheckFunc", "onPageSelected (int, surface)" + " position " + position);
        moveToPosition(position, surface);
        this.mOldPosition = position;
    }

    // 移动到指定位置的共用方法
    private void moveToPosition(int position, Surface surface) {
        setSurface(surface);

        // 如果是第一个位置或者跳跃式改变位置
        if (position == 0 || Math.abs(position - mOldPosition) != 1) {
            aliListPlayer.moveTo(mIndexWithUUID.get(position));
            return;
        }

        // 如果是相邻位置的平滑过渡
        if (mOldPosition < position) {
            handleNextWithPreRender(surface);
        } else { // 向前滑动
            aliListPlayer.moveToPrev();
        }
    }

    // 处理预渲染的播放器逻辑
    private void handleNextWithPreRender(Surface surface) {
        if (preRenderPlayer == null) {
            aliListPlayer.clearScreen();
            return;
        }

        // 使用预渲染的播放器播放下一个视频
        preRenderPlayer.setSurface(surface);
        preRenderPlayer.start();
        aliListPlayer.setSurface(null);
        aliListPlayer.moveToNextWithPrerendered();
        setupPreRenderedPlayerListeners(preRenderPlayer);
    }

    // 设置预渲染播放器的监听器
    private void setupPreRenderedPlayerListeners(IPlayer preRenderPlayer) {
        preRenderPlayer.setOnInfoListener(infoBean -> {
            long duration = preRenderPlayer.getDuration();
            mPlayerListener.onInfo((int) duration, infoBean);
            toRenderingStartOnInfo();
        });
        preRenderPlayer.setOnStateChangedListener(i -> {
            mCurrentPlayerStateCallBack = i;
            mPlayerListener.onPlayStateChanged(-1, mCurrentPlayerStateCallBack == IPlayer.paused);
        });
        preRenderPlayer.setOnPreparedListener(() -> mPlayerListener.onPrepared(-1));
        preRenderPlayer.setOnErrorListener((ErrorInfo errorInfo) -> {
            mPlayerListener.onError(errorInfo);
            preRenderPlayer.stop();
        });
    }

    /**
     * 设置智能预渲染
     *
     * @note 当前版本，PreRender Player仅支持预渲染列表下一个视频的画面；指定预渲染上一个视频的画面，有待后续版本支持。
     */
    public void setSurfaceToPreRenderPlayer(Surface surface) {
        Log.i("CheckFunc", "setSurfaceToPreRenderPlayer " + "mOldPosition " + mOldPosition);
        preRenderPlayer = aliListPlayer.getPreRenderPlayer();
        if (preRenderPlayer != null && surface != null) {
            preRenderPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);
            preRenderPlayer.setSurface(surface);
            preRenderPlayer.seekTo(0, DEFAULT_SEEK_MODE);
        }
    }

    public void setSurface(Surface surface) {
        Log.w("CheckFunc", "setSurface: [" + surface + "]");

        aliListPlayer.setSurface(surface);
    }

    public void destroy() {
        aliListPlayer.clear();
        aliListPlayer.stop();
        aliListPlayer.release();
    }

    public void onPlayStateChange() {
        Log.i("CheckFunc", "onPlayStateChange" + " mCurrentPlayerState " + mCurrentPlayerState + " moldPosition " + mOldPosition + " mCurrentPlayerStateCallBack " + mCurrentPlayerStateCallBack);
        if (mCurrentPlayerStateCallBack < IPlayer.prepared) {
            return;
        }
        if (mCurrentPlayerState == IPlayer.paused) {
            aliListPlayer.start();
            mCurrentPlayerState = IPlayer.started;
        } else {
            aliListPlayer.pause();
            mCurrentPlayerState = IPlayer.paused;
        }
    }

    public void seek(long seekPosition) {
        if (seekPosition >= aliListPlayer.getDuration()) {
            // 避免直接跳转到视频尾部，与自动跳转到下一集出现逻辑上的冲突，出现黑屏的的情况。
            seekPosition -= 10;
        }
        if (seekPosition < 0 || seekPosition > aliListPlayer.getDuration()) {
            Log.w("CheckFunc", "seek, seekTo not valid: " + seekPosition);
            return;
        }
        aliListPlayer.seekTo(seekPosition, DEFAULT_SEEK_MODE);
        Log.i("CheckFunc", "seek, seekTo " + seekPosition);

    }

    public void pausePlay() {
        aliListPlayer.pause();
    }

    public void resumePlay() {
        aliListPlayer.start();
    }

    public boolean isCurrentPlayerStateCallBackPaused() {
        return mCurrentPlayerStateCallBack == IPlayer.paused;
    }

    //backUp: in case of preRenderPlayer onRenderingStart being called back ，aliListPlayer cannot get onRenderingStart called back.
    private void toRenderingStartOnInfo() {
        if (aliListPlayer.getDuration() > 0) {
            mPlayerListener.onRenderingStart(-1, aliListPlayer.getDuration());
        }
    }

    /**
     * 设置预加载数量
     */
    public void setPreloadCount(int preloadCount) {
        aliListPlayer.setPreloadCount(preloadCount);
    }

    /**
     * 设置智能预加载策略
     */
    public void setPreloadStrategy(boolean enable, String params) {
        aliListPlayer.setPreloadScene(IListPlayer.SceneType.SCENE_SHORT);
        aliListPlayer.enablePreloadStrategy(IListPlayer.StrategyType.STRATEGY_DYNAMIC_PRELOAD_DURATION, enable);
        if (enable) {
            aliListPlayer.setPreloadStrategy(IListPlayer.StrategyType.STRATEGY_DYNAMIC_PRELOAD_DURATION, params);
        }
    }

    /**
     * 开启本地缓存
     */
    public void enableLocalCache(boolean enable, String path) {
        AliPlayerGlobalSettings.enableLocalCache(enable, 10 * 1024, path);
        PlayerConfig config = aliListPlayer.getConfig();
        config.mEnableLocalCache = enable;
        aliListPlayer.setConfig(config);
    }

    /**
     * 设置缓存清除策略
     */
    public void setCacheFileClearConfig(long expireMin, long maxCapacityMB, long freeStorageMB) {
        AliPlayerGlobalSettings.setCacheFileClearConfig(expireMin, maxCapacityMB, freeStorageMB);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        AliPlayerGlobalSettings.clearCaches();
    }

    /**
     * 开启HTTPDNS
     */
    public void enableHTTPDNS(boolean enable) {
        AliPlayerGlobalSettings.enableHttpDns(enable);
        PlayerConfig config = aliListPlayer.getConfig();
        config.mEnableHttpDns = -1;
        aliListPlayer.setConfig(config);
    }

    /**
     * 开启增强型HTTPDNS
     * <p>
     * 与enableHTTPDNS互斥
     * 如果开启增强型HTTPDNS，需要增强型HTTPDNS的高级License，否则将会失效
     * 如果该功能开启失效，会打印错误日志“enhanced dns license is invalid, open enhanced dns failed”
     * 播放器SDK版本要求：> 6.10.0
     */
    public void enableEnhancedHTTPDNS(Context context, boolean enable) {
        PrivateService.preInitService(context);
        //打开增强型HTTPDNS
        AliPlayerGlobalSettings.enableEnhancedHttpDns(enable);
        //必选，添加增强型HTTPDNS域名，请确保该域名为阿里云CDN域名并完成域名配置可以正常可提供线上服务。
        DomainProcessor.getInstance().addEnhancedHttpDnsDomain("player.***alicdn.com");
        //可选，增加HTTPDNS预解析域名
        DomainProcessor.getInstance().addPreResolveDomain("player.***alicdn.com");
        // AliPlayerGlobalSettings.SET_DNS_PRIORITY_LOCAL_FIRST表示设置HTTPDNS的优先级
        AliPlayerGlobalSettings.setOption(AliPlayerGlobalSettings.SET_DNS_PRIORITY_LOCAL_FIRST, enable ? 1 : 0);
    }

    /**
     * 配置网络超时重试时间与次数
     */
    public void setNetworkRetryTimes(int timeoutMs, int retryCount) {
        PlayerConfig config = aliListPlayer.getConfig();
        config.mNetworkRetryCount = retryCount;
        config.mNetworkTimeout = timeoutMs;
        aliListPlayer.setConfig(config);
    }
}
