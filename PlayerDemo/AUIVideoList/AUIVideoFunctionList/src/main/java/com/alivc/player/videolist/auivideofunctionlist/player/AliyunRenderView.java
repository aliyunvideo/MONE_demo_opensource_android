package com.alivc.player.videolist.auivideofunctionlist.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

/**
 * bind AliyunPlayer And TextureView
 */
public class AliyunRenderView {

    private final AliPlayer mAliPlayer;
    private final TextureView mTextureView;

    private int mPlayerState;
    private boolean mHasPrepared = false;
    private boolean mHasCreateSurface = false;
    private PlayerListener mOnPlayerListener;

    public AliyunRenderView(Context context) {
        //create AliPlayer and set AliPlayer config
        mAliPlayer = AliPlayerFactory.createAliPlayer(context);
        PlayerConfig config = mAliPlayer.getConfig();
        config.mClearFrameWhenStop = true;
        mAliPlayer.setConfig(config);
        mAliPlayer.setLoop(true);

        //create TextureView
        mTextureView = new TextureView(context);

        initListener();
    }

    private void initListener() {
        mAliPlayer.setOnPreparedListener(() -> {
            mHasPrepared = true;
            invokeSeekTo();
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onPrepared(-1);
            }
        });

        mAliPlayer.setOnInfoListener(infoBean -> {
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onInfo(-1, infoBean);
            }
        });

        mAliPlayer.setOnStateChangedListener(i -> {
            mPlayerState = i;
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onPlayStateChanged(-1, mPlayerState == IPlayer.paused);
            }
        });

        mAliPlayer.setOnRenderingStartListener(() -> {
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onRenderingStart(-1,mAliPlayer.getDuration());
            }
        });

        mAliPlayer.setOnCompletionListener(() -> {
            if (mOnPlayerListener != null) {
                mOnPlayerListener.onCompletion(-1);
            }
        });

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                mHasCreateSurface = true;
                invokeSeekTo();
                mAliPlayer.setSurface(new Surface(surfaceTexture));
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                mAliPlayer.surfaceChanged();
            }

            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                mAliPlayer.setSurface(null);
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {

            }
        });
    }

    /**
     * TextureView add to FrameLayout
     */
    public TextureView initTextureView() {
        //remove
        removeTextureView();
        return mTextureView;
    }

    public void removeTextureView() {
        if (mTextureView.getParent() != null) {
            ((ViewGroup) mTextureView.getParent()).removeView(mTextureView);
        }
    }

    /**
     * pre-render: Player seekTo(0) after setSurface and prepared
     */
    private void invokeSeekTo() {
        if (mHasCreateSurface && mHasPrepared) {
            mAliPlayer.seekTo(0);
            mHasCreateSurface = false;
            mHasPrepared = false;
        }
    }

    /**
     * Player set data source and prepare
     */
    public void bindUrl(String url) {
        UrlSource urlSource = new UrlSource();
        urlSource.setUri(url);
        if (url.startsWith("artc://")) {
            PlayerConfig config = mAliPlayer.getConfig();
            config.mMaxDelayTime = 1000;
            config.mHighBufferDuration = 10;
            config.mStartBufferDuration = 10;
            mAliPlayer.setConfig(config);
        }
        mAliPlayer.setDataSource(urlSource);
        mAliPlayer.prepare();
    }

    public AliPlayer getAliPlayer() {
        return mAliPlayer;
    }

    public boolean isPlaying() {
        return mPlayerState == IPlayer.started;
    }

    public TextureView getTextureView() {
        initTextureView();
        return mTextureView;
    }

    public void release() {
        mAliPlayer.stop();
        mAliPlayer.release();
    }

    public void setOnPlayerListener(PlayerListener playerListener) {
        this.mOnPlayerListener = playerListener;
    }

    public boolean playerListenerIsNull() {
        return mOnPlayerListener == null;
    }

    public void start() {
        mAliPlayer.start();
    }

    public void pause() {
        mAliPlayer.pause();
    }

    public void stop() {
        mAliPlayer.stop();
    }

    public void openLoopPlay(boolean openLoopPlay) {
        mAliPlayer.setLoop(openLoopPlay);
    }
}
