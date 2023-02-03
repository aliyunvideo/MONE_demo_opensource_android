package com.aliyun.svideo.template.sample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.aio.avbaseui.widget.AVCircleProgressView;
import com.aliyun.aio.avbaseui.widget.AVToast;

import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.utils.VideoInfoUtils;
import com.aliyun.svideo.template.sample.bean.AlivcCropOutputParam;
import com.aliyun.svideo.template.sample.util.Common;
import com.aliyun.svideo.track.CropTrackContainer;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.inc.CropTrackListener;
import com.aliyun.svideosdk.common.AliyunErrorCode;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.crop.AliyunICrop;
import com.aliyun.svideosdk.crop.CropCallback;
import com.aliyun.svideosdk.crop.CropParam;
import com.aliyun.svideosdk.crop.impl.AliyunCropCreator;
import com.aliyun.svideosdk.player.AliyunISVideoPlayer;
import com.aliyun.svideosdk.player.PlayerCallback;
import com.aliyun.svideosdk.player.impl.AliyunSVideoPlayerCreator;
import com.aliyun.ugsv.common.utils.DateTimeUtils;
import com.aliyun.ugsv.common.utils.DensityUtil;
import com.aliyun.ugsv.common.utils.FileUtils;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ScreenUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtil;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.ugsv.common.utils.UriUtils;

import java.util.concurrent.TimeUnit;

public class VideoClipActivity extends AppCompatActivity implements CropTrackListener, TextureView.SurfaceTextureListener, CropCallback, Handler.Callback, View.OnClickListener {
    public static final String TAG = VideoClipActivity.class.getSimpleName();
    private static final int IDLE_VIDEO = -1;
    private static final int PLAY_VIDEO = 1000;
    private static final int PAUSE_VIDEO = 1001;
    private static final int END_VIDEO = 1003;
    private TextureView mPlayView;
    private String mVideoPath;
    private View mPlayViewContainer, mPlayIcon, mBgMask;
    private AVCircleProgressView mCropProgress;
    private CropTrackContainer mCropTrackContainer;
    private AliyunISVideoPlayer mPlayer;
    private Surface mSurface;
    private int mFrameContainerWidth;
    private int mFrameContainerHeight;
    private AliyunICrop mCroptor;
    private Handler mPlayHandler;
    private int mPlayState = IDLE_VIDEO;
    private long mDuration;
    private long mFixedDuration = 5000L;
    private long mCropStartTime;
    private long mCropEndTime;
    private boolean mIsCropping = false;
    private int mVideoWidth, mVideoHeight;
    private String mOutputPath;
    public static void start(Context ctx, String videoPath, long duration){
        Intent startIntent = new Intent(ctx, VideoClipActivity.class);
        startIntent.putExtra("video_path", videoPath);
        startIntent.putExtra("fixed_duration", duration);
        ((Activity)ctx).startActivityForResult(startIntent, Common.REQUEST_CLIP_VIDEO);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_clip);
        mVideoPath = getIntent().getStringExtra("video_path");
        mFixedDuration = getIntent().getLongExtra("fixed_duration", 5000L);
        ((TextView)findViewById(R.id.fixed_duration)).setText((mFixedDuration/1000.f) + "s");
        mPlayHandler = new Handler(this);
        mPlayView = findViewById(R.id.play_view);
        mPlayViewContainer = findViewById(R.id.playview_container);
        mBgMask = findViewById(R.id.bg_mask);
        mCropProgress = findViewById(R.id.aliyun_crop_progress);
        mPlayIcon = findViewById(R.id.play_indicator);

        mPlayViewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPlayState == PAUSE_VIDEO || mPlayState == IDLE_VIDEO){
                    mPlayIcon.setVisibility(View.GONE);
                    startPlayVideo();
                }else{
                    mPlayIcon.setVisibility(View.VISIBLE);
                    pauseVideo();
                }
            }
        });
        mCropTrackContainer = findViewById(R.id.track_container);
        mCropTrackContainer.setFixedDurationInMs(mFixedDuration);

        View operaContainer = findViewById(R.id.nav_options_area);
        int targetContainerHeight = ScreenUtils.getHeight(this) - operaContainer.getLayoutParams().height - mCropTrackContainer.getLayoutParams().height;
        ViewGroup.LayoutParams containerLp = mPlayViewContainer.getLayoutParams();
        containerLp.height = targetContainerHeight;
        containerLp.width = ScreenUtils.getWidth(this);
        mFrameContainerWidth = containerLp.width;
        mFrameContainerHeight = containerLp.height;
        mPlayViewContainer.setLayoutParams(containerLp);

        findViewById(R.id.start_crop).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);

        DisplayMetrics screenMetric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(screenMetric);
        int height = screenMetric.heightPixels - DensityUtil.dip2px(this, 185);
        int width = (int)(height * 9.0/16.0);
        int videoWidth = width > screenMetric.widthPixels ? screenMetric.widthPixels : width;
        int videoHeight = (int)(videoWidth * 16.0/9.0);

        ViewGroup.LayoutParams playLp = mPlayView.getLayoutParams();
        playLp.width = videoWidth;
        playLp.height = videoHeight;
        mPlayView.setLayoutParams(playLp);

        ViewGroup.LayoutParams lp = mPlayViewContainer.getLayoutParams();
        lp.width = screenMetric.widthPixels;
        lp.height = height;
        mPlayViewContainer.setLayoutParams(lp);

        mPlayView.setSurfaceTextureListener(this);

        mCroptor = AliyunCropCreator.createCropInstance(this);
        mCroptor.setCropCallback(this);
        try {
            mDuration = mCroptor.getVideoDuration(mVideoPath) / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }

        mCropTrackContainer.setListener(this);
        MainVideoClipInfo clipInfo = new MainVideoClipInfo(1, mVideoPath, 0,
                mDuration, mDuration);
        mCropTrackContainer.setVideoData(clipInfo);
    }

    private void initPlayViewLayout(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            Log.e(TAG, "error , videoSize width = 0 or height = 0");
            return;
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mPlayView.getLayoutParams();
        float videoRatio = (float) videoWidth / (float) videoHeight;
        float containerRatio = (float) mFrameContainerWidth / (float) mFrameContainerHeight;

        int targetW = 0;
        int targetH = 0;
        if(videoRatio < containerRatio){
            targetH = mFrameContainerHeight;
            targetW = (int)(targetH * videoRatio);
        }else{
            targetW = mFrameContainerWidth;
            targetH = (int)(targetW / videoRatio);
        }
        layoutParams.width = targetW;
        layoutParams.height = targetH;
        layoutParams.setMargins(0, 0, 0, 0);
        mPlayView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mPlayer != null) pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mPlayer != null) mPlayer.release();
    }

    @Override
    public void onUpdateClipTime(long timeIn, long timeOut) {
        mCropStartTime = timeIn;
        mCropEndTime = timeOut;
    }

    @Override
    public void onClipTouchPosition(long time) {
        if(mPlayer != null && mPlayState != PAUSE_VIDEO){
            mPlayHandler.sendEmptyMessage(PAUSE_VIDEO);
        }
    }

    @Override
    public void onScrollChangedTime(long time) {
        long realTime = (long)(((float)time / mDuration) * (mDuration - mFixedDuration));
        if(mPlayer != null ){
            if(mPlayState != PAUSE_VIDEO) mPlayHandler.sendEmptyMessage(PAUSE_VIDEO);
            mPlayer.seek(realTime);
        }
//        Log.d("czw", "onScrollChangedTime " + realTime);
    }

    @Override
    public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureAvailable");
        if (mPlayer == null) {
            mSurface = new Surface(surface);
            mPlayer = AliyunSVideoPlayerCreator.createPlayer();
            mPlayer.init(this);

            mPlayer.setPlayerCallback(new PlayerCallback() {
                @Override
                public void onPlayComplete() {

                }

                @Override
                public void onDataSize(int dataWidth, int dataHeight) {
                    if (dataWidth == 0 || dataHeight == 0) {
                        Log.e(TAG, "error , video player onDataSize width = 0 or height = 0");
                        return;
                    }
                    mVideoWidth = dataWidth;
                    mVideoHeight = dataHeight;
                    initPlayViewLayout(dataWidth, dataHeight);
                    mPlayer.setDisplaySize(mPlayView.getLayoutParams().width, mPlayView.getLayoutParams().height);
                    startPlayVideo();
                }

                @Override
                public void onError(int i) {
                    Log.e(TAG, "错误码 : " + i);
                }
            });
            mPlayer.setDisplay(mSurface);
            mPlayer.setSource(mVideoPath);
        }
    }

    private void startPlayVideo() {
        if (mPlayer == null) {
            return;
        }
        if(mPlayState == IDLE_VIDEO){
            mPlayer.play();
        }else{
            mPlayer.resume();
        }
        mPlayState = PLAY_VIDEO;
        mPlayHandler.sendEmptyMessage(PLAY_VIDEO);
    }

    private void pauseVideo() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.pause();
        mPlayState = PAUSE_VIDEO;
        mPlayHandler.removeMessages(PLAY_VIDEO);
    }

    @Override
    public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surface) {

    }

    @Override
    public void onProgress(int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setProgress(percent);
            }
        });
    }

    @Override
    public void onError(int code) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
                switch (code) {
                    case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_VIDEO:
                        ToastUtil.showToast(VideoClipActivity.this, "视频格式不支持");
                        break;
                    case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_AUDIO:
                        ToastUtil.showToast(VideoClipActivity.this, "音频格式不支持");
                        break;
                    default:
                        ToastUtil.showToast(VideoClipActivity.this, "裁剪失败");
                        break;
                }
                setResult(Activity.RESULT_CANCELED, getIntent());
            }
        });
        mIsCropping = false;
        mBgMask.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(long duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
                long duration = mCropEndTime - mCropStartTime;
                AlivcCropOutputParam cropOutputParam = new AlivcCropOutputParam();
                cropOutputParam.setOutputPath(mOutputPath);
                cropOutputParam.setDuration(duration);
                AVToast.show(VideoClipActivity.this, true, R.string.alivc_crop_complete);
                //裁剪结束
                Intent intent = getIntent();
                intent.putExtra(AlivcCropOutputParam.RESULT_KEY_OUTPUT_PARAM, cropOutputParam);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        mIsCropping = false;
        mBgMask.setVisibility(View.GONE);
        mCropProgress.setVisibility(View.GONE);
        VideoInfoUtils.printVideoInfo(mOutputPath);
    }

    @Override
    public void onCancelComplete() {
        //取消完成
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
            }
        });
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                FileUtils.deleteFile(mOutputPath);
                return null;
            }
        } .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        setResult(Activity.RESULT_CANCELED);
        finish();
        mIsCropping = false;
        mBgMask.setVisibility(View.GONE);
        mCropProgress.setVisibility(View.GONE);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        switch (msg.what) {
            case PAUSE_VIDEO:
                pauseVideo();
                break;
            case PLAY_VIDEO:
                if (mPlayer != null) {
                    long currentPlayPos = mPlayer.getCurrentPosition() / 1000L;
                    if(currentPlayPos >= mDuration){
                        mPlayer.seek(0);
                        startPlayVideo();
                    }
                    mCropTrackContainer.updatePlayProgress(currentPlayPos, mDuration);
                    mPlayHandler.sendEmptyMessageDelayed(PLAY_VIDEO, 10);
                }
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_crop){
            startCrop();
        }else if(v.getId() == R.id.back){
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (mIsCropping) {
            mCroptor.cancel();
        } else {
            super.onBackPressed();
        }
    }

    private void startCrop() {
        if (!PermissionUtils.checkPermissionsGroup(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE})) {
            ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
            return;
        }

        if (mFrameContainerWidth == 0 || mFrameContainerHeight == 0) {
            ToastUtil.showToast(this, "裁剪失败");
            mIsCropping = false;
            mBgMask.setVisibility(View.GONE);
            mCropProgress.setVisibility(View.GONE);
            return;
        }
        if (mIsCropping) {
            return;
        }
        //开始裁剪时，暂停视频的播放,提高裁剪效率
        pauseVideo();
        mOutputPath = Constants.SDCardConstants.getDir(this) + DateTimeUtils.getDateTimeFromMillisecond(System.currentTimeMillis()) + Constants.SDCardConstants.CROP_SUFFIX;
        int outputWidth = 720;
        float videoRatio = (float) mVideoWidth / (float) mVideoHeight;
        int outputHeight = (int) (outputWidth / videoRatio);
        int posX = 0;
        int posY = 0;
        int cropWidth = mVideoWidth;
        int cropHeight = mVideoHeight;

        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(mOutputPath);
        cropParam.setInputPath(mVideoPath);
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);
        Rect cropRect = new Rect(posX, posY, posX + cropWidth, posY + cropHeight);
        cropParam.setCropRect(cropRect);
        cropParam.setStartTime(mCropStartTime, TimeUnit.MILLISECONDS);
        cropParam.setEndTime(mCropEndTime, TimeUnit.MILLISECONDS);
        cropParam.setScaleMode(VideoDisplayMode.FILL);
        cropParam.setFrameRate(30);
        cropParam.setGop(250);
        cropParam.setQuality(VideoQuality.SSD);
        cropParam.setVideoCodec(VideoCodecs.H264_HARDWARE);
        cropParam.setFillColor(Color.BLACK);
        cropParam.setCrf(0);

        mCropProgress.setVisibility(View.VISIBLE);
        cropParam.setUseGPU(true);
        mCroptor.setCropParam(cropParam);

        int ret = mCroptor.startCrop();
        if (ret < 0) {
            ToastUtil.showToast(this, "裁剪失败  " + ret);
            return;
        }
        mIsCropping = true;
        mBgMask.setVisibility(View.VISIBLE);
        mCropProgress.setVisibility(View.VISIBLE);
    }
}
