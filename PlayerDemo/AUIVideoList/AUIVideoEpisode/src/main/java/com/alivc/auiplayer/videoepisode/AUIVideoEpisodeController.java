package com.alivc.auiplayer.videoepisode;


import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.Surface;

import com.alivc.auiplayer.videoepisode.data.AUIEpisodeVideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IListPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PlayerConfig;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class AUIVideoEpisodeController {

    private final AliListPlayer aliListPlayer;
    private int mOldPosition = 0;
    private int mCurrentPlayerState;
    private final SparseArray<String> mIndexWithUUID = new SparseArray<>();
    private PlayerListener mPlayerListener;
    private IPlayer preRenderPlayer;
    private boolean mAutoPlayLoop;

    public AUIVideoEpisodeController(Context context) {
        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
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

        //开启本地缓存
        String cacheDir = context.getFilesDir() + File.separator + "aliyunPlayer" + File.separator + "Preload";
        enableLocalCache(true, cacheDir);
        setCacheFileClearConfig(30 * 24 * 60, 20 * 1024, 0);

        //设置HttpDNS
        enableHTTPDNS(true);

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
        aliListPlayer.setLoop(openLoopPlay);
        mAutoPlayLoop = openLoopPlay;
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    //用于剧集的跳转
    public void onPageSelected(int position) {
        Log.i("CheckFunc", "onPageSelected (int)" + " position " + position);

        aliListPlayer.moveTo(mIndexWithUUID.get(position));
        this.mOldPosition = position;
    }

    //用于短视频的上下滑动
    public void onPageSelected(int position, Surface surface) {
        Log.i("CheckFunc", "onPageSelected (int, surface)" + " position " + position);
        if (position == 0) {
            aliListPlayer.setSurface(surface);
            aliListPlayer.moveTo(mIndexWithUUID.get(position));
        } else {
            int gap = Math.abs(position - mOldPosition);
            if (gap == 1) {
                if (mOldPosition < position) {
                    IPlayer preRenderPlayer = aliListPlayer.getPreRenderPlayer();
                    if (preRenderPlayer != null) {
                        //clearScreen() will refresh the screen hereby cause a black screen issue
                        //preRenderPlayer.clearScreen();
                        preRenderPlayer.setSurface(surface);
                        if (mAutoPlayLoop) {
                            preRenderPlayer.setLoop(true);
                        }
                        preRenderPlayer.start();
                        aliListPlayer.setSurface(null);

                        preRenderPlayer.setOnInfoListener(infoBean -> {
                                    long duration = preRenderPlayer.getDuration();
                                    mPlayerListener.onInfo((int) duration, infoBean);
                                    toRenderingStartOnInfo();
                                }
                        );
                        preRenderPlayer.setOnStateChangedListener(i -> {
                        });

                        preRenderPlayer.setOnPreparedListener(() -> {
                            mPlayerListener.onPrepared(-1);
                        });


                        preRenderPlayer.setOnErrorListener((ErrorInfo errorInfo) -> {
                            mPlayerListener.onError(errorInfo);
                            preRenderPlayer.stop();
                        });

                        aliListPlayer.moveToNextWithPrerendered();
                    } else {
                        aliListPlayer.clearScreen();
                        aliListPlayer.setSurface(surface);
                        aliListPlayer.moveToNext();
                    }
                } else {
                    aliListPlayer.setSurface(surface);
                    aliListPlayer.moveToPrev();
                }
            } else {
                aliListPlayer.setSurface(surface);
                aliListPlayer.moveTo(mIndexWithUUID.get(position));
            }
        }
        this.mOldPosition = position;
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
            preRenderPlayer.setSurface(surface);
            preRenderPlayer.seekTo(0);
        }
    }

    public void setSurface(Surface surface) {
        Log.i("CheckFunc", "setSurface" + " surface " + surface);

        aliListPlayer.setSurface(surface);
    }

    public void surfaceChanged() {
        aliListPlayer.surfaceChanged();
    }

    public void destroy() {
        aliListPlayer.clear();
        aliListPlayer.stop();
        aliListPlayer.release();
    }

    public void onPlayStateChange() {
        Log.i("CheckFunc", "onPlayStateChange" + " mCurrentPlayerState " + mCurrentPlayerState + " moldPosition " + mOldPosition);
        if (mCurrentPlayerState == IPlayer.paused) {
            aliListPlayer.start();
            mCurrentPlayerState = IPlayer.started;
        } else {
            aliListPlayer.pause();
            mCurrentPlayerState = IPlayer.paused;
        }

        mPlayerListener.onPlayStateChanged(-1, mCurrentPlayerState == IPlayer.paused);
    }

    public void seek(long seekPosition) {
        aliListPlayer.seekTo(seekPosition);
    }

    public void pausePlay() {
        aliListPlayer.pause();
    }

    public void resumePlay() {
        aliListPlayer.start();
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
     * 配置网络超时重试时间与次数
     */
    public void setNetworkRetryTimes(int timeoutMs, int retryCount) {
        PlayerConfig config = aliListPlayer.getConfig();
        config.mNetworkRetryCount = retryCount;
        config.mNetworkTimeout = timeoutMs;
        aliListPlayer.setConfig(config);
    }
}
