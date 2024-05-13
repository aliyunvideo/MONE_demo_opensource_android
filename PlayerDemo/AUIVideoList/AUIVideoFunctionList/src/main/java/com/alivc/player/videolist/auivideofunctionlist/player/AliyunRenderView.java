package com.alivc.player.videolist.auivideofunctionlist.player;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.alivc.player.videolist.auivideolistcommon.bean.VideoInfo;
import com.alivc.player.videolist.auivideolistcommon.listener.PlayerListener;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.IPlayer;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;

/**
 * bind AliyunPlayer And TextureView
 */
public class AliyunRenderView {
    private static final String TAG = "[AUI]AliyunRenderView";

    private final TextureView mTextureView;

    private AliPlayer mAliPlayer;
    private int mPlayerState;
    private boolean mHasPrepared = false;
    private boolean mHasCreateSurface = false;
    private PlayerListener mOnPlayerListener;

    // 通过接口设置，可以达到精准seek效果，默认是 IPlayer.SeekMode.Inaccurate
    // 当前SDK默认是 IPlayer.SeekMode.Inaccurate ，即：非精准seek
    // 如果想要精准seek，请设置为 IPlayer.SeekMode.Accurate
    private static final IPlayer.SeekMode DEFAULT_SEEK_MODE = IPlayer.SeekMode.Accurate;

    public AliyunRenderView(Context context) {
        //create TextureView
        mTextureView = new TextureView(context);
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
                mOnPlayerListener.onRenderingStart(-1, mAliPlayer.getDuration());
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
            mAliPlayer.seekTo(0, DEFAULT_SEEK_MODE);
            mHasCreateSurface = false;
            mHasPrepared = false;
        }
    }

    /**
     * Player set data source and prepare
     */
    public void bindVideo(AliPlayer aliPlayer, VideoInfo videoInfo) {
        if (aliPlayer == null) {
            return;
        }

        if (videoInfo == null || TextUtils.isEmpty(videoInfo.getUrl())) {
            return;
        }

        Log.i(TAG, "[BIND] [" + this + "][" + videoInfo.getTitle() + "]");
        mAliPlayer = aliPlayer;
        initListener();

        String url = videoInfo.getUrl();
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

    public void unbind() {
        Log.i(TAG, "[UNBIND] [" + this + "]");
        if (mAliPlayer != null) {
            mAliPlayer.pause();
            mAliPlayer.stop();
        }
    }

    public AliPlayer getAliPlayer() {
        return mAliPlayer;
    }

    public boolean isPlaying() {
        return mPlayerState == IPlayer.started;
    }

    public void setOnPlayerListener(PlayerListener playerListener) {
        this.mOnPlayerListener = playerListener;
    }

    public void start() {
        Log.i(TAG, "[START] [" + this + "]");
        if (mAliPlayer != null) {
            mAliPlayer.start();
        }
    }

    public void pause() {
        Log.i(TAG, "[PAUSE] [" + this + "]");
        if (mAliPlayer != null) {
            mAliPlayer.pause();
        }
    }

    public void seekTo(long progress) {
        if (mAliPlayer != null) {
            mAliPlayer.seekTo(progress, DEFAULT_SEEK_MODE);
        }
    }

    @Override
    public String toString() {
        return "RenderView-" + super.hashCode();
    }
}
