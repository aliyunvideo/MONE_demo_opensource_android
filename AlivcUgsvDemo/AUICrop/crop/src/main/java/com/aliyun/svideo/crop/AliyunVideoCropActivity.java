/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */

package com.aliyun.svideo.crop;

import android.Manifest;
import android.app.Activity;
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
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.aio.avbaseui.widget.AVCircleProgressView;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.common.global.Version;
import com.aliyun.common.utils.FileUtils;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.svideo.base.AlivcSvideoEditParam;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.utils.VideoInfoUtils;
import com.aliyun.svideo.base.widget.HorizontalListView;
import com.aliyun.ugsv.common.utils.DateTimeUtils;
import com.aliyun.ugsv.common.utils.DensityUtils;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.aliyun.svideo.crop.bean.AlivcCropInputParam;
import com.aliyun.svideo.crop.bean.AlivcCropOutputParam;
import com.aliyun.svideo.track.CropTrackContainer;
import com.aliyun.svideo.track.bean.MainVideoClipInfo;
import com.aliyun.svideo.track.inc.CropTrackListener;
import com.aliyun.svideosdk.common.AliyunErrorCode;
import com.aliyun.svideosdk.common.AliyunIThumbnailFetcher;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.CropKey;
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
import com.aliyun.svideosdk.transcode.NativeParser;

import java.util.concurrent.TimeUnit;

public class AliyunVideoCropActivity extends Activity implements TextureView.SurfaceTextureListener, HorizontalListView.OnScrollCallBack,
     View.OnClickListener, CropCallback, Handler.Callback, CropTrackListener {
    public static final String TAG = AliyunVideoCropActivity.class.getSimpleName();
    private static final int PLAY_VIDEO = 1000;
    private static final int PAUSE_VIDEO = 1001;
    private static final int END_VIDEO = 1003;
    private static final int UPDATE_PLAY_TIMESTAMP = 1004;
    private static final int UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS = 1005;
    private int playState = END_VIDEO;
    private AliyunICrop mCroptor;
    private FrameLayout mFrameContainer;
    private TextureView textureview;
    private Surface mSurface;
    /**
     * sdk提供的播放器，支持非关键帧的实时预览
     */
    private AliyunISVideoPlayer mPlayer;
    private ImageView mCancelBtn, mNextBtn, mNoFullScreenTransform;
    private CropTrackContainer mTrackContainer;
    private AVCircleProgressView mCropProgress;
    private String mInputVideoPath;
    private String mOutputPath;
    private long mOriginalDuration;
    private int mResolutionMode;
    private int mRatioMode;
    private VideoQuality mQuality = VideoQuality.HD;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private int mFrameRate;
    private int mGop;
    private int mScreenWidth;
    private int mFrameContainerWidth;
    private int mFrameContainerHeight;
    private int mScrollX;
    private int mScrollY;
    private int mVideoWidth;
    private int mVideoHeight;
    private VideoDisplayMode cropMode = VideoDisplayMode.SCALE;
    private AliyunIThumbnailFetcher mThumbnailFetcher;
    private long mCropStartTime;
    private long mCropEndTime;
    long mCurSeek = 0;
    private Handler mPlayHandler = new Handler(this);
    private boolean isPause = false;
    private boolean isCropping = false;
    /**
     * 每次修改裁剪结束位置时需要重新播放视频
     */
    private boolean mNeedPlayStart = false;
    private boolean isUseGPU = false;
    private int mAction = CropKey.ACTION_TRANSCODE;
    /**
     * 原比例
     */
    public static final int RATIO_ORIGINAL = 3;
    private VideoDisplayMode mOriginalMode;
    private boolean mFullScreenMode = false;
    private int mNoFullScreenAreaHeight = 0;
    private int mFullScreenAreaHeight = 0;
    private TextView mNoFullScreenPlayTimeStamp;
    private View mNoFullScreenPlayIcon;
    private SeekBar mFullScreenProgresBar;
    private boolean mProgressBarTouching = false;
    private TextView mFullScreenPlayTimeStamp;
    private TextView mFullScreenPlayDuration;
    private View mFullScreenPlayIcon;
    private View mFullScreenTransform;
    private View mFullScreenOperateArea;
    private View mNoFullScreenPlayOperate;
    private View mNoFullScreenNavOperate;
    private View mFullScreenBackBtn;
    private View mBgMask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.alivc_crop_activity_video_crop);
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mNoFullScreenAreaHeight = DensityUtils.dip2px(this, 498);
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        mFullScreenAreaHeight = getResources().getDisplayMetrics().heightPixels;
        mCroptor = AliyunCropCreator.createCropInstance(this);
        mCroptor.setCropCallback(this);
        getData();
        initView();
        initSurface();
    }

    private void getData() {

        Intent intent = getIntent();
        mAction = intent.getIntExtra(AlivcCropInputParam.INTENT_KEY_ACTION, CropKey.ACTION_TRANSCODE);
        mInputVideoPath = intent.getStringExtra(AlivcCropInputParam.INTENT_KEY_PATH);
        try {
            mOriginalDuration = mCroptor.getVideoDuration(mInputVideoPath) / 1000;
            mCropStartTime = 0;
            mCropEndTime = mOriginalDuration;
        } catch (Exception e) {
            ToastUtil.showToast(this, R.string.alivc_crop_video_tip_crop_failed);
        }//获取精确的视频时间
        mResolutionMode = intent.getIntExtra(AlivcCropInputParam.INTENT_KEY_RESOLUTION_MODE, AlivcCropInputParam.RESOLUTION_720P);
        cropMode = (VideoDisplayMode)intent.getSerializableExtra(AlivcCropInputParam.INTENT_KEY_CROP_MODE);
        if (cropMode == null) {
            cropMode = VideoDisplayMode.FILL;
        }
        mOriginalMode = cropMode;
        mQuality =  (VideoQuality)intent.getSerializableExtra(AlivcCropInputParam.INTENT_KEY_QUALITY);
        if (mQuality == null) {
            mQuality = VideoQuality.HD;
        }
        mGop = intent.getIntExtra(AlivcCropInputParam.INTENT_KEY_GOP, 250 );
        mFrameRate = intent.getIntExtra(AlivcCropInputParam.INTENT_KEY_FRAME_RATE, 30);
        mRatioMode = intent.getIntExtra(AlivcCropInputParam.INTENT_KEY_RATIO_MODE, AlivcCropInputParam.RATIO_MODE_9_16);
        isUseGPU = intent.getBooleanExtra(AlivcCropInputParam.INTENT_KEY_USE_GPU, false );
        mVideoCodec = (VideoCodecs)intent.getSerializableExtra(AlivcCropInputParam.INTENT_KEY_CODECS);
        if (mVideoCodec == null) {
            mVideoCodec = VideoCodecs.H264_HARDWARE;
        }
    }


    public static String getVersion() {
        return Version.VERSION;
    }

    private void initView() {
        mNoFullScreenTransform = findViewById(R.id.aliyun_transform);
        mNoFullScreenTransform.setOnClickListener(this);
        mNextBtn = findViewById(R.id.aliyun_next);
        mNextBtn.setOnClickListener(this);
        mCancelBtn = findViewById(R.id.aliyun_back);
        mCancelBtn.setOnClickListener(this);
        mCropProgress = findViewById(R.id.aliyun_crop_progress);
        mCropProgress.setVisibility(View.GONE);

        mFullScreenProgresBar = findViewById(R.id.play_progress);
        mFullScreenProgresBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               if(mPlayer != null && mProgressBarTouching){
                   long duration = mCropEndTime - mCropStartTime;
                   mCurSeek = (long)((float)progress/100 * duration + mCropStartTime);
                   mPlayer.seek(mCurSeek);
                   mPlayHandler.removeMessages(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
                   mPlayHandler.sendEmptyMessage(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
               }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mProgressBarTouching = true;
                pauseVideo();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mProgressBarTouching = false;
                resumeVideo();
            }
        });
        mNoFullScreenPlayTimeStamp = findViewById(R.id.play_timestamp);
        mNoFullScreenPlayIcon = findViewById(R.id.aliyun_play_toggle);
        mNoFullScreenPlayIcon.setOnClickListener(this);
        mFullScreenPlayIcon = findViewById(R.id.aliyun_play_toggle_fullscreen);
        mFullScreenPlayIcon.setOnClickListener(this);
        mFullScreenPlayTimeStamp = findViewById(R.id.current_timestamp_fullscreen);
        mFullScreenPlayDuration = findViewById(R.id.full_duration_fullscreen);
        mFullScreenTransform = findViewById(R.id.aliyun_transform_fullscreen);
        mFullScreenTransform.setOnClickListener(this);
        mFullScreenOperateArea = findViewById(R.id.fullscreen_operate_area);
        mNoFullScreenPlayOperate = findViewById(R.id.play_options_area);
        mNoFullScreenNavOperate = findViewById(R.id.nav_options_area);
        mFullScreenBackBtn = findViewById(R.id.ic_back);
        mFullScreenBackBtn.setOnClickListener(this);
        mTrackContainer = findViewById(R.id.track_container);
        mTrackContainer.setListener(this);
        mBgMask = findViewById(R.id.bg_mask);

        MainVideoClipInfo clipInfo = new MainVideoClipInfo(1, mInputVideoPath, 0,
                mOriginalDuration, mOriginalDuration);
        mTrackContainer.setVideoData(clipInfo);
    }

    public void initSurface() {
        mFrameContainer = findViewById(R.id.aliyun_video_surfaceLayout);
        textureview = (TextureView) findViewById(R.id.aliyun_video_textureview);
        resizeFrame();
        textureview.setSurfaceTextureListener(this);
    }

    private void resizeFrame() {
        NativeParser parser = new NativeParser();
        parser.init(mInputVideoPath);
        try {
            mVideoWidth = Integer.parseInt(parser.getValue(NativeParser.VIDEO_WIDTH));
            mVideoHeight = Integer.parseInt(parser.getValue(NativeParser.VIDEO_HEIGHT));

        } catch (NumberFormatException ex) {
            Log.e(TAG, ex.getMessage());
            return;
        } finally {
            parser.release();
            parser.dispose();
        }

        if (mVideoWidth == 0 || mVideoHeight == 0) {
            Log.e(TAG, "NativeParser parser video width = 0 or height = 0");
            return;
        }
        ViewGroup.LayoutParams containerlp = mFrameContainer.getLayoutParams();
        if(mFullScreenMode)
        {
            containerlp.width = mScreenWidth;
            containerlp.height = mFullScreenAreaHeight;
        }else{
            containerlp.width = mScreenWidth;
            containerlp.height = mNoFullScreenAreaHeight;
        }
        mFrameContainer.setLayoutParams(containerlp);
        mFrameContainerWidth = containerlp.width;
        mFrameContainerHeight = containerlp.height;
        if (cropMode == VideoDisplayMode.SCALE) {
            scaleCrop(mVideoWidth, mVideoHeight);
        } else if (cropMode == VideoDisplayMode.FILL) {
            scaleFill(mVideoWidth, mVideoHeight);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
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
                    mFrameContainerWidth = mFrameContainer.getWidth();
                    mFrameContainerHeight = mFrameContainer.getHeight();
                    mVideoWidth = dataWidth;
                    mVideoHeight = dataHeight;
                    if (mCroptor != null && mCropEndTime == 0) {
                        try {
                            mCropEndTime = (long) (mCroptor.getVideoDuration(mInputVideoPath) * 1.0f / 1000);
                        } catch (Exception e) {
                            ToastUtil.showToast(AliyunVideoCropActivity.this, R.string.alivc_crop_video_tip_error);
                        }
                    }

                    if (cropMode == VideoDisplayMode.SCALE) {
                        scaleCrop(dataWidth, dataHeight);
                    } else if (cropMode == VideoDisplayMode.FILL) {
                        scaleFill(dataWidth, dataHeight);
                    }
                    mPlayer.setDisplaySize(textureview.getLayoutParams().width, textureview.getLayoutParams().height);
                    startPlayVideo();

                }

                @Override
                public void onError(int i) {
                    Log.e(TAG, "错误码 : " + i);
                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showToast(AliyunVideoCropActivity.this, getString(R.string.alivc_crop_video_tip_error));
                        }
                    });

                }
            });
            mPlayer.setDisplay(mSurface);
            mPlayer.setSource(mInputVideoPath);
        }
        Log.i(TAG, "onSurfaceTextureAvailable");
    }


    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.e(TAG, "onSurfaceTextureSizeChanged");
        mPlayer.setDisplaySize(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.e(TAG, "onSurfaceTextureDestroyed");
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            playState = END_VIDEO;
            mPlayer = null;
            mSurface.release();
            mSurface = null;
        }
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    private void resetScroll() {
        mScrollX = 0;
        mScrollY = 0;
    }

    @Override
    public void onScrollDistance(Long count, int distanceX) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isPause) {
            startPlayVideo();
        }
        if (isCropping && mPlayer != null) {
            long currentPosition = mPlayer.getCurrentPosition() / 1000;
            mPlayer.draw(currentPosition);
        }
    }

    @Override
    protected void onPause() {
        if (playState == PLAY_VIDEO) {
            pauseVideo();
        }
        isPause = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCroptor != null) {
            mCroptor.dispose();
            mCroptor = null;
        }
        if (mThumbnailFetcher != null) {
            mThumbnailFetcher.release();
        }
    }

    private void scaleFill(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            Log.e(TAG, "error , videoSize width = 0 or height = 0");
            return;
        }
        LayoutParams layoutParams = (LayoutParams) textureview.getLayoutParams();
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
        textureview.setLayoutParams(layoutParams);
        cropMode = VideoDisplayMode.FILL;
        resetScroll();
    }

    private void scaleCrop(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
            Log.e(TAG, "error , videoSize width = 0 or height = 0");
            return;
        }
        LayoutParams layoutParams = (LayoutParams) textureview.getLayoutParams();
        int s = Math.min(videoWidth, videoHeight);
        int b = Math.max(videoWidth, videoHeight);
        float videoRatio = (float) b / s;
        float ratio;
        switch (mRatioMode) {
        case AliyunSnapVideoParam.RATIO_MODE_1_1:
            ratio = 1f;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_3_4:
            ratio = (float) 4 / 3;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_9_16:
            ratio = (float) 16 / 9;
            break;
        default:
            ratio = (float) 16 / 9;
            break;
        }
        if (mRatioMode == AlivcSvideoEditParam.RATIO_MODE_ORIGINAL) {
            //原比例显示逻辑和填充模式一致
            if (videoWidth > videoHeight) {
                layoutParams.width = mFrameContainerWidth;
                layoutParams.height = mFrameContainerWidth * videoHeight / videoWidth;
            } else {
                if (videoRatio >= ratio) {
                    layoutParams.height = mFrameContainerHeight;
                    layoutParams.width = mFrameContainerHeight * videoWidth / videoHeight;
                } else {
                    layoutParams.width = mFrameContainerWidth;
                    layoutParams.height = mFrameContainerWidth * videoHeight / videoWidth;
                }
            }
        } else {
            if (videoWidth > videoHeight) {
                layoutParams.height = mFrameContainerHeight;
                layoutParams.width = mFrameContainerHeight * videoWidth / videoHeight;
            } else {
                if (videoRatio >= ratio) {
                    layoutParams.width = mFrameContainerWidth;
                    layoutParams.height = mFrameContainerWidth * videoHeight / videoWidth;
                } else {
                    layoutParams.height = mFrameContainerHeight;
                    layoutParams.width = mFrameContainerHeight * videoWidth / videoHeight;

                }
            }

        }

        layoutParams.setMargins(0, 0, 0, 0);
        textureview.setLayoutParams(layoutParams);
        cropMode = VideoDisplayMode.SCALE;
        resetScroll();
    }


    private void scanFile() {
        MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[] {mOutputPath}, new String[] {"video/mp4"}, null);
    }

    private void startPlayVideo() {
        if (isCropping) {
            //裁剪过程中点击无效
            return;
        }
        if (mPlayer == null) {
            return;
        }
        mPlayer.seek((int) mCropStartTime);
        mPlayer.resume();
        playState = PLAY_VIDEO;
        long videoPos = mCropStartTime;
        mPlayHandler.removeMessages(PLAY_VIDEO);
        mPlayHandler.sendEmptyMessage(PLAY_VIDEO);
        //重新播放之后修改为false，防止暂停、播放的时候重新开始播放
        mNeedPlayStart = false;
    }

    private void togglePlay(){
        if (playState == END_VIDEO) {
            startPlayVideo();
        } else if (playState == PLAY_VIDEO) {
            pauseVideo();
        } else if (playState == PAUSE_VIDEO) {
            resumeVideo();
        }
    }

    private void pauseVideo() {
        if (mPlayer == null) {
            return;
        }
        mPlayer.pause();
        playState = PAUSE_VIDEO;
        mPlayHandler.removeMessages(PLAY_VIDEO);
        mNoFullScreenPlayIcon.setActivated(false);
        mFullScreenPlayIcon.setActivated(false);
    }

    private void resumeVideo() {
        if (mPlayer == null) {
            return;
        }
        if (mNeedPlayStart) {
            startPlayVideo();
            mNeedPlayStart = false;
            return;
        }
        mPlayer.resume();
        playState = PLAY_VIDEO;
        mPlayHandler.sendEmptyMessage(PLAY_VIDEO);
    }

    @Override
    public void onBackPressed() {
        if (isCropping) {
            mCroptor.cancel();
        } else {
            super.onBackPressed();
        }
    }

    private void toggleFullScreenMode(boolean fullScreen){
        mFullScreenMode = fullScreen;
        if(mFullScreenMode){
           mFullScreenOperateArea.setVisibility(View.VISIBLE);
           mNoFullScreenPlayOperate.setVisibility(View.GONE);
           mNoFullScreenNavOperate.setVisibility(View.GONE);
            mTrackContainer.setVisibility(View.GONE);
           mFullScreenTransform.setActivated(true);
           mNoFullScreenTransform.setActivated(true);
           mFullScreenBackBtn.setVisibility(View.VISIBLE);
        }else{
            mFullScreenOperateArea.setVisibility(View.GONE);
            mNoFullScreenPlayOperate.setVisibility(View.VISIBLE);
            mNoFullScreenNavOperate.setVisibility(View.VISIBLE);
            mTrackContainer.setVisibility(View.VISIBLE);
            mFullScreenTransform.setActivated(false);
            mNoFullScreenTransform.setActivated(false);
            mFullScreenBackBtn.setVisibility(View.GONE);
        }
        resizeFrame();
        if(playState == PAUSE_VIDEO)
        {
            mPlayHandler.removeMessages(UPDATE_PLAY_TIMESTAMP);
            mPlayHandler.sendEmptyMessage(UPDATE_PLAY_TIMESTAMP);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mNoFullScreenTransform || v == mFullScreenTransform) {
            if (isCropping) {
                return;
            }
            toggleFullScreenMode(!mFullScreenMode);
        } else if (v == mNextBtn) {

            if (mScrollX != 0 || mScrollY != 0 || !cropMode.equals(mOriginalMode)) {
                //需要裁剪画面时或者切换裁剪模式时，走真裁剪
                mAction = CropKey.ACTION_TRANSCODE;
            }
            switch (mAction) {
            case CropKey.ACTION_TRANSCODE:
                startCrop();
                break;
            case CropKey.ACTION_SELECT_TIME:
                long duration = mCropEndTime - mCropStartTime;
                AlivcCropOutputParam cropOutputParam = new AlivcCropOutputParam();
                //由于只是选择时间，所以文件路径和输入路径保持一致
                cropOutputParam.setOutputPath(mInputVideoPath);
                cropOutputParam.setDuration(duration);
                cropOutputParam.setStartTime(mCropStartTime);
                onCropComplete(cropOutputParam);
                break;
            default:
                break;
            }
        } else if (v == mCancelBtn) {
            onBackPressed();
        } else if(v == mNoFullScreenPlayIcon || v == mFullScreenPlayIcon){
            togglePlay();
            if(playState == PLAY_VIDEO){
                mNoFullScreenPlayIcon.setActivated(true);
                mFullScreenPlayIcon.setActivated(true);
            }else{
                mNoFullScreenPlayIcon.setActivated(false);
                mFullScreenPlayIcon.setActivated(false);
            }
        } else if(v == mFullScreenBackBtn){
            toggleFullScreenMode(false);
        }
    }

    /**
     * 裁剪结束
     */
    private void onCropComplete(AlivcCropOutputParam outputParam) {
        AVToast.show(this, true, R.string.alivc_crop_complete);
        //裁剪结束
        Intent intent = getIntent();
        intent.putExtra(AlivcCropOutputParam.RESULT_KEY_OUTPUT_PARAM, outputParam);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    private void startCrop() {
        if (!PermissionUtils.checkPermissionsGroup(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE})) {
            ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
            return;
        }

        if (mFrameContainerWidth == 0 || mFrameContainerHeight == 0) {
            ToastUtil.showToast(this, R.string.alivc_crop_video_tip_crop_failed);
            isCropping = false;
            mBgMask.setVisibility(View.GONE);
            return;
        }
        if (isCropping) {
            return;
        }
        //开始裁剪时，暂停视频的播放,提高裁剪效率
        pauseVideo();
        int posX = 0;
        int posY = 0;
        int outputWidth = 0;
        int outputHeight = 0;
        int cropWidth = mVideoWidth;
        int cropHeight = mVideoHeight;
        mOutputPath = Constants.SDCardConstants.getDir(this) + DateTimeUtils.getDateTimeFromMillisecond(System.currentTimeMillis()) + Constants.SDCardConstants.CROP_SUFFIX;
        switch (mResolutionMode) {
            case AliyunSnapVideoParam.RESOLUTION_360P:
                outputWidth = 360;
                break;
            case AliyunSnapVideoParam.RESOLUTION_480P:
                outputWidth = 480;
                break;
            case AliyunSnapVideoParam.RESOLUTION_540P:
                outputWidth = 540;
                break;
            default:
                outputWidth = 720;
                break;
        }
        float videoRatio = (float) mVideoWidth / (float) mVideoHeight;
        switch (mRatioMode) {
            case AliyunSnapVideoParam.RATIO_MODE_1_1:
                outputHeight = outputWidth;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_3_4:
                outputHeight = outputWidth * 4 / 3;
                break;
            case RATIO_ORIGINAL:
                outputHeight = (int) (outputWidth / videoRatio);
                break;
            default:
                outputHeight = outputWidth * 16 / 9;
                break;
        }
        if(VideoDisplayMode.FILL == cropMode){
           posX = 0;
           posY = 0;
           cropWidth = mVideoWidth;
           cropHeight = mVideoHeight;
        }else{
            //TODO
        }

        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(mOutputPath);
        cropParam.setInputPath(mInputVideoPath);
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);
        Rect cropRect = new Rect(posX, posY, posX + cropWidth, posY + cropHeight);
        cropParam.setCropRect(cropRect);
        cropParam.setStartTime(mCropStartTime, TimeUnit.MILLISECONDS);
        cropParam.setEndTime(mCropEndTime, TimeUnit.MILLISECONDS);
        cropParam.setScaleMode(cropMode);
        cropParam.setFrameRate(mFrameRate);
        cropParam.setGop(mGop);
        cropParam.setQuality(mQuality);
        cropParam.setVideoCodec(mVideoCodec);
        cropParam.setFillColor(Color.BLACK);
        cropParam.setCrf(0);

        mCropProgress.setVisibility(View.VISIBLE);
        cropParam.setUseGPU(isUseGPU);
        mCroptor.setCropParam(cropParam);

        int ret = mCroptor.startCrop();
        if (ret < 0) {
            ToastUtil.showToast(this, getString(R.string.alivc_crop_video_tip_crop_failed) + "  " + ret);
            return;
        }
        startCropTimestamp = System.currentTimeMillis();
        Log.d("CROP_COST", "start : " + startCropTimestamp);
        isCropping = true;
        mBgMask.setVisibility(View.VISIBLE);
    }

    long startCropTimestamp;

    private void deleteFile() {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                FileUtils.deleteFile(mOutputPath);
                return null;
            }
        } .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onProgress(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setProgress(percent);
            }
        });
    }

    @Override
    public void onError(final int code) {
        Log.d(TAG, "crop failed : " + code);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
                switch (code) {
                case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_VIDEO:
                    ToastUtil.showToast(AliyunVideoCropActivity.this, R.string.alivc_crop_video_tip_not_supported_video);
                    break;
                case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_AUDIO:
                    ToastUtil.showToast(AliyunVideoCropActivity.this, R.string.alivc_crop_video_tip_not_supported_audio);
                    break;
                default:
                    ToastUtil.showToast(AliyunVideoCropActivity.this, R.string.alivc_crop_video_tip_crop_failed);
                    break;
                }
//                progressDialog.dismiss();
                setResult(Activity.RESULT_CANCELED, getIntent());
            }
        });
        isCropping = false;
        mBgMask.setVisibility(View.GONE);
    }

    @Override
    public void onComplete(long duration) {
        long time = System.currentTimeMillis();
        Log.d(TAG, "completed : " + (time - startCropTimestamp));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //适配android Q
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            UriUtils.saveVideoToMediaStore(AliyunVideoCropActivity.this.getApplicationContext(), mOutputPath);
                        }
                    });
                } else {
                    scanFile();
                }
                long duration = mCropEndTime - mCropStartTime;
                AlivcCropOutputParam cropOutputParam = new AlivcCropOutputParam();
                cropOutputParam.setOutputPath(mOutputPath);
                cropOutputParam.setDuration(duration);
                onCropComplete(cropOutputParam);
            }
        });
        isCropping = false;
        mBgMask.setVisibility(View.GONE);
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
        deleteFile();
        setResult(Activity.RESULT_CANCELED);
        finish();
        isCropping = false;
        mBgMask.setVisibility(View.GONE);
    }

    private void updateCurrentTimestamp(boolean withProgress){
        if(mPlayer == null){
            return;
        }
        long currentPlayPos = mPlayer.getCurrentPosition() / 1000;
        if(currentPlayPos >= mCropEndTime)
        {
            startPlayVideo();
            return;
        }
        currentPlayPos -= mCropStartTime;
        Log.d(TAG, "currentPlayPos:" + currentPlayPos);
        long duration = mCropEndTime - mCropStartTime;
        float progress = currentPlayPos / (float) duration;
        int durationInSeconds = (int) (duration / 1000);
        int currentTimeInSeconds = (int) (currentPlayPos / 1000);
        if(mFullScreenMode){
            if(withProgress){
                mFullScreenProgresBar.setProgress((int)(progress * 100));
            }
            mFullScreenPlayTimeStamp.setText(String.format("%02d:%02d", currentTimeInSeconds / 60, currentTimeInSeconds % 60));
            mFullScreenPlayDuration.setText(String.format("%02d:%02d", durationInSeconds / 60, durationInSeconds % 60));
        } else {
            mNoFullScreenPlayTimeStamp.setText(String.format("%02d:%02d/%02d:%02d", currentTimeInSeconds / 60, currentTimeInSeconds % 60, durationInSeconds / 60, durationInSeconds % 60));
            if (withProgress) {
                mTrackContainer.updatePlayProgress(currentPlayPos, duration);
            }
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what) {
        case PAUSE_VIDEO:
            pauseVideo();
            break;
        case PLAY_VIDEO:
            if (mPlayer != null) {
                updateCurrentTimestamp(true);
                mNoFullScreenPlayIcon.setActivated(true);
                mFullScreenPlayIcon.setActivated(true);
                mPlayHandler.sendEmptyMessageDelayed(PLAY_VIDEO, 10);
            }
            break;
        case UPDATE_PLAY_TIMESTAMP:
            updateCurrentTimestamp(true);
            break;
        case UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS:
            updateCurrentTimestamp(false);
            break;
        default:
            break;
        }
        return false;
    }

    @Override
    public void onUpdateClipTime(long timeIn, long timeOut) {
        Log.d(TAG,"onUpdateClipTime in:" + timeIn + " , out:" + timeOut);
        mCropStartTime = timeIn;
        mCropEndTime = timeOut;
    }

    @Override
    public void onClipTouchPosition(long time) {
        Log.d(TAG,"onClipTouchPosition: " + time);
        if(playState != PAUSE_VIDEO)
        {
            mPlayHandler.sendEmptyMessage(PAUSE_VIDEO);
        }
        long duration = mCropEndTime - mCropStartTime;
        if (time < 0) {
            time = 0;
        } else if (time > duration) {
            time = duration;
        }
        if (mCurSeek != time) {
            time += mCropStartTime;
            mPlayer.seek(time);
            mCurSeek = time;
        }
        mPlayHandler.removeMessages(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
        mPlayHandler.sendEmptyMessage(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
    }

    @Override
    public void onScrollChangedTime(long time) {
        Log.d(TAG,"onScrollChangedTime: " + time);
        if(playState != PAUSE_VIDEO)
        {
            mPlayHandler.sendEmptyMessage(PAUSE_VIDEO);
        }
        time += mCropStartTime;
        if ((mCurSeek != 0 && time == 0) || (mCurSeek != mOriginalDuration && time == mOriginalDuration)) {
            mPlayer.seek(time);
            mCurSeek = time;
        } else if (Math.abs(time - mCurSeek) > 100) {
            //间隔大于100ms才处理
            mPlayer.seek(time);
            mCurSeek = time;
        }
        mPlayHandler.removeMessages(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
        mPlayHandler.sendEmptyMessage(UPDATE_PLAY_TIMESTAMP_WITHOUT_PROGRESS);
    }
}
