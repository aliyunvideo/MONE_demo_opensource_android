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

    // 通过接口设置，可以达到全屏效果，默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT
    // 当前SDK默认是 IPlayer.ScaleMode.SCALE_ASPECT_FIT ，即：图片以自身宽高比为准填充，较短一边未铺满全屏幕，但是会导致剩余空间透明，类似于上下黑边
    // 如果想要全屏效果，请设置为 IPlayer.ScaleMode.SCALE_ASPECT_FILL ，即：图片以自身宽高比为准填充 ，超出部分裁剪，但是会导致图片显示不全，类似于放大并显示一部分
    private static final IPlayer.ScaleMode DEFAULT_VIDEO_SCALE_MODE = IPlayer.ScaleMode.SCALE_ASPECT_FIT;

    // 通过接口设置，可以达到精准seek效果，默认是 IPlayer.SeekMode.Inaccurate
    // 当前SDK默认是 IPlayer.SeekMode.Inaccurate ，即：非精准seek
    // 如果想要精准seek，请设置为 IPlayer.SeekMode.Accurate
    private static final IPlayer.SeekMode DEFAULT_SEEK_MODE = IPlayer.SeekMode.Accurate;

    public AUIVideoStandardListController(Context context) {
        aliListPlayer = AliPlayerFactory.createAliListPlayer(context);
        aliListPlayer.setLoop(true);
        aliListPlayer.setScaleMode(DEFAULT_VIDEO_SCALE_MODE);

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
        aliListPlayer.seekTo(seekPosition, DEFAULT_SEEK_MODE);
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
