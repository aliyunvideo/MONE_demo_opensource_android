package com.alivc.player.videolist.auivideostandradlist;

import android.content.Context;
import android.util.SparseArray;
import android.view.Surface;


import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliListPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.AliPlayerGlobalSettings;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;

import java.util.List;
import java.util.UUID;

public class AUIVideoStandardListController {

    private final AliListPlayer aliListPlayer;
    private int mOldPosition = 0;
    private int mCurrentPlayerState;
    private final SparseArray<String> mIndexWithUUID = new SparseArray<>();
    private PlayerListener mPlayerListener;

    public AUIVideoStandardListController(Context context) {
        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
        aliListPlayer.setLoop(true);

        aliListPlayer.setOnPreparedListener(() -> {
            mPlayerListener.onPrepared(-1);
        });

        aliListPlayer.setOnRenderingStartListener(() -> {
            mPlayerListener.onRenderingStart(-1, aliListPlayer.getDuration());
        });

        aliListPlayer.setOnInfoListener(infoBean -> {
            mPlayerListener.onInfo(-1, infoBean);
        });

        aliListPlayer.setOnCompletionListener(() -> {
            if (mPlayerListener != null) {
                mPlayerListener.onCompletion(-1);
            }
        });

        aliListPlayer.setOnStateChangedListener(i -> {
            mCurrentPlayerState = i;
            mPlayerListener.onPlayStateChanged(-1, i == IPlayer.paused);
        });
    }

    public void loadSource(List<VideoInfo> listVideo) {
        aliListPlayer.clear();
        mIndexWithUUID.clear();
        for (int i = 0; i < listVideo.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i, randomUUID);
            aliListPlayer.addUrl(listVideo.get(i).getUrl(), randomUUID);
        }
    }

    public void addSource(List<VideoInfo> videoBeanList) {
        for (int i = 0; i < videoBeanList.size(); i++) {
            String randomUUID = UUID.randomUUID().toString();
            mIndexWithUUID.put(i + mIndexWithUUID.size(), randomUUID);
            aliListPlayer.addUrl(videoBeanList.get(i).getUrl(), randomUUID);
        }
    }

    public void openLoopPlay(boolean openLoopPlay) {
        aliListPlayer.setLoop(openLoopPlay);
    }

    public void setPlayerListener(PlayerListener listener) {
        this.mPlayerListener = listener;
    }

    public void onPageSelected(int position) {
        if (position == 0) {
            aliListPlayer.moveTo(mIndexWithUUID.get(position));
        } else {
            if (mOldPosition < position) {
                aliListPlayer.moveToNext();
            } else {
                aliListPlayer.moveToPrev();
            }
        }
        this.mOldPosition = position;

    }

    public void setSurface(Surface surface) {
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

    public void onPrepared(int position) {
        long duration = aliListPlayer.getDuration();

    }

    public void onPlayStateChange() {
        if (mCurrentPlayerState == IPlayer.paused) {
            aliListPlayer.start();
        } else {
            aliListPlayer.pause();
        }
    }

    public void seek(long seekPosition) {
        aliListPlayer.seekTo(seekPosition);
    }

    public void setPreloadCount(int preloadCount) {
        aliListPlayer.setPreloadCount(preloadCount);
    }


    public void enableLocalCache(boolean enable, String path) {
        AliPlayerGlobalSettings.enableLocalCache(enable, 10 * 1024, path);
        PlayerConfig config = aliListPlayer.getConfig();
        config.mEnableLocalCache = enable;
        aliListPlayer.setConfig(config);
    }
}
