package com.aliyun.auivideolist.player;

import android.util.Log;

import com.aliyun.auivideolist.bean.ListVideoBean;
import com.aliyun.auivideolist.utils.GlobalSettings;
import com.aliyun.loader.MediaLoader;
import com.aliyun.player.AliPlayerGlobalSettings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class AliPlayerPreload {

    private static final String TAG = "AliPlayerPreload";

    private final AtomicInteger mOldPosition = new AtomicInteger(-1);
    private static final int DURATION = 5 * 1000;
    private static final int COUNT = 5;
    private static AliPlayerPreload mInstance = null;
    private final MediaLoader mMediaLoader;
    private LinkedList<ListVideoBean> mUrlLinkedList;
    private final ExecutorService mExecutorService;

    private AliPlayerPreload() {
        mExecutorService = Executors.newSingleThreadExecutor();
        AliPlayerGlobalSettings.enableLocalCache(true, GlobalSettings.CACHE_MEMORY_SIZE * 1024, GlobalSettings.CACHE_DIR);
        AliPlayerGlobalSettings.setCacheFileClearConfig(GlobalSettings.CACHE_EXPIRED_TIME * 24 * 60, GlobalSettings.CACHE_SIZE, GlobalSettings.CACHE_FREE_STORAGE_SIZE);

        Log.e(TAG, "AliPlayerPreload: " + GlobalSettings.CACHE_DIR);

        mMediaLoader = MediaLoader.getInstance();
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
        mMediaLoader.load(url, DURATION);
    }

    private void cancel(String url) {
        mMediaLoader.cancel(url);
    }

    public void setUrls(LinkedList<ListVideoBean> data) {
        this.mUrlLinkedList = data;
    }

    public void moveTo(int position) {
        mExecutorService.execute(() -> {
            if(position % COUNT == 0 && mOldPosition.get() < position){
                preloadVerticalDirection(position);
            }
            preloadHorizontalDirection(mOldPosition.get(),false);
            preloadHorizontalDirection(position,true);
            mOldPosition.set(position);
        });
    }

    private void preloadVerticalDirection(int position){
        int count = 0;
        int size = mUrlLinkedList.size();
        while(count < COUNT && (count + position) < size){
            load(mUrlLinkedList.get(count + position).getUrl());
            count++;
        }
    }

    private void preloadHorizontalDirection(int position,boolean isLoad){
        if(position >= 0 && position < mUrlLinkedList.size()){
            ArrayList<ListVideoBean.HListVideoBean> horizontalVideoData = mUrlLinkedList.get(position).getHorizontalVideoData();
            for (ListVideoBean.HListVideoBean horizontalVideoDatum : horizontalVideoData) {
                if(isLoad){
                    load(horizontalVideoDatum.getUrl());
                }else{
                    cancel(horizontalVideoDatum.getUrl());
                }
            }
        }
    }
}
