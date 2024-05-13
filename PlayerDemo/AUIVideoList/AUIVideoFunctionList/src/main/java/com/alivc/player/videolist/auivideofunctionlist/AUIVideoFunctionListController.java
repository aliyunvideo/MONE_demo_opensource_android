package com.alivc.player.videolist.auivideofunctionlist;

import android.content.Context;

import com.alivc.player.videolist.auivideofunctionlist.adapter.AUIVideoFunctionListAdapter;
import com.alivc.player.videolist.auivideofunctionlist.player.AliPlayerPreload;
import com.alivc.player.videolist.auivideofunctionlist.player.AliyunRenderView;
import com.alivc.player.videolist.auivideolistcommon.adapter.AUIVideoListViewHolder;
import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.aliyun.player.AliPlayerGlobalSettings;

import java.io.File;
import java.util.List;

public class AUIVideoFunctionListController {

    private AliPlayerPreload mAliPlayerPreload;
    private int mCurrentPosition;
    //Play the 0 th video for the first time

    public AUIVideoFunctionListController(Context context) {
        mAliPlayerPreload = AliPlayerPreload.getInstance();
        mAliPlayerPreload.init();
        initPlayerConfigs(context);
    }

    /**
     * load Video List
     * 1. set Video List to AliPlayerPreload
     */
    public void loadSources(List<VideoInfo> videoList) {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.setUrls(videoList);
        }
    }

    public void addSource(List<VideoInfo> videoBeanList) {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.addUrls(videoBeanList);
        }
    }

    // 播放器相关配置
    private void initPlayerConfigs(Context context) {
        //开启本地缓存，统一约定在cache路径下的Preload目录
        String cacheDir = context.getExternalCacheDir() + File.separator + "Preload";
        enableLocalCache(true, cacheDir);
        setCacheFileClearConfig(30 * 24 * 60, 20 * 1024, 0);
    }

    public void onPrepared(int position, AUIVideoListViewHolder viewHolder) {
        if (mCurrentPosition == position) {
            if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
                ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayerView().start();
            }
        }
    }

    public void onPageSelected(int position, AUIVideoListViewHolder viewHolder) {
        this.mCurrentPosition = position;
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.moveTo(position);
        }
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            AliyunRenderView aliyunRenderView = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayerView();
            aliyunRenderView.start();
        }
    }

    public void onPageRelease(int position, AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            AliyunRenderView aliPlayerView = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayerView();
            aliPlayerView.pause();
        }
    }

    public void seekTo(long progress, AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayerView().seekTo(progress);
        }
    }

    public void changePlayState(AUIVideoListViewHolder viewHolder) {
        if (viewHolder instanceof AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) {
            AliyunRenderView aliPlayerView = ((AUIVideoFunctionListAdapter.AUIVideoFunctionListViewHolder) viewHolder).getAliPlayerView();
            if (aliPlayerView.isPlaying()) {
                aliPlayerView.pause();
                viewHolder.showPlayIcon(true);
            } else {
                aliPlayerView.start();
                viewHolder.showPlayIcon(false);
            }
        }
    }

    public void destroy() {
        if (mAliPlayerPreload != null) {
            mAliPlayerPreload.release();
            mAliPlayerPreload = null;
        }
    }

    /**
     * 开启本地缓存
     */
    public void enableLocalCache(boolean enable, String path) {
        AliPlayerGlobalSettings.enableLocalCache(enable, 10 * 1024, path);
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
}
