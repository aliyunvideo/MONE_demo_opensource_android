package com.alivc.player.videolist.auivideofunctionlist.player;

import android.util.Log;

import com.alivc.player.videolist.auivideofunctionlist.utils.GlobalSettings;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.loader.MediaLoader;
import com.aliyun.player.AliPlayerGlobalSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * use AliPlayer SDK {@link MediaLoader}
 * auto load next video after receive MediaLoader onCompleted/onError
 */
public class AliPlayerPreload {

    private static final String TAG = "AliPlayerPreload";

    private final AtomicInteger mOldPosition = new AtomicInteger(-1);
    private static volatile AliPlayerPreload mInstance = null;
    private final MediaLoader mMediaLoader;
    private final List<VideoInfo> mUrlLinkedList = new ArrayList<>();
    private final ExecutorService mExecutorService;
    private boolean mIsLive = true;

    private AliPlayerPreload() {
        mExecutorService = Executors.newSingleThreadExecutor();
        //LocalCache settings(MediaLoader must enable LocalCache)
        AliPlayerGlobalSettings.enableLocalCache(GlobalSettings.CACHE_IS_ENABLE, GlobalSettings.CACHE_MEMORY_SIZE * 1024, GlobalSettings.CACHE_DIR);
        AliPlayerGlobalSettings.setCacheFileClearConfig(GlobalSettings.CACHE_EXPIRED_TIME * 24 * 60, GlobalSettings.CACHE_SIZE, GlobalSettings.CACHE_FREE_STORAGE_SIZE);

        Log.e(TAG, "AliPlayerPreload: " + GlobalSettings.CACHE_DIR);

        mMediaLoader = MediaLoader.getInstance();
        mMediaLoader.setOnLoadStatusListener(new MediaLoader.OnLoadStatusListener() {
            @Override
            public void onError(String s, int i, String s1) {
                loadNext();
            }

            @Override
            public void onCompleted(String s) {
                loadNext();
            }

            @Override
            public void onCanceled(String s) {
            }
        });
    }

    public static AliPlayerPreload getInstance() {
        if (mInstance == null) {
            synchronized (AliPlayerPreload.class) {
                if (mInstance == null) {
                    mInstance = new AliPlayerPreload();
                }
            }
        }
        return mInstance;
    }

    private void load(String url) {
        if(!mIsLive){
            mMediaLoader.load(url, GlobalSettings.DURATION);
        }
    }

    private void cancel(String url) {
        if(!mIsLive){
            mMediaLoader.cancel(url);
        }
    }

    /**
     * cancel MediaLoad with position
     */
    public void cancel(int position){
        cancel(mUrlLinkedList.get(position).getUrl());
    }

    /**
     * Load Next Video
     */
    private void loadNext() {
        if (hasNext(mOldPosition.get())) {
            int next = mOldPosition.incrementAndGet();
            load(mUrlLinkedList.get(next).getUrl());
        }
    }

    /**
     * set DataSource
     */
    public void setUrls(List<VideoInfo> data) {
        this.mUrlLinkedList.clear();
        this.mUrlLinkedList.addAll(data);
    }

    public void addUrls(List<VideoInfo> videoBeanList) {
        this.mUrlLinkedList.addAll(videoBeanList);
    }

    /**
     * start load
     */
    public void moveToSerial(int position) {
        mExecutorService.execute(() -> {
            //cancel first
            int i = mOldPosition.get();
            if (i >= 0 && i < mUrlLinkedList.size()) {
                cancel(mUrlLinkedList.get(mOldPosition.get()).getUrl());
            }
            //load
            if (hasNext(position)) {
                load(mUrlLinkedList.get(position + 1).getUrl());
            }
            mOldPosition.set(position + 1);
        });
    }

    private boolean hasNext(int currentPosition) {
        return currentPosition + 1 < mUrlLinkedList.size();
    }

    public void isLive(boolean isLive){
        this.mIsLive = isLive;
    }
}
