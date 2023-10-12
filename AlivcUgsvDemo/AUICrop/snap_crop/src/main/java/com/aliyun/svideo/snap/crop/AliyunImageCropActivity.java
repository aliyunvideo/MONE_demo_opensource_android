/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */

package com.aliyun.svideo.snap.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aliyun.svideo.base.AliyunSnapVideoParam;
import com.aliyun.ugsv.common.utils.BitmapUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.aliyun.ugsv.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideo.base.ActionInfo;
import com.aliyun.svideo.base.AlivcSvideoEditParam;
import com.aliyun.common.global.Version;
import com.aliyun.ugsv.common.utils.DensityUtil;
import com.aliyun.ugsv.common.utils.FileUtils;
import com.aliyun.ugsv.common.utils.ToastUtil;
import com.aliyun.svideosdk.crop.impl.AliyunCropCreator;
import com.aliyun.svideosdk.crop.CropParam;
import com.aliyun.svideosdk.crop.AliyunICrop;
import com.aliyun.svideosdk.crop.CropCallback;
import com.aliyun.svideosdk.common.AliyunErrorCode;
import com.aliyun.svideo.base.AliyunSvideoActionConfig;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.widget.FanProgressBar;
import com.aliyun.svideo.base.widget.HorizontalListView;
import com.aliyun.svideo.base.widget.SizeChangedNotifier;
import com.aliyun.svideo.base.widget.VideoTrimFrameLayout;
import com.aliyun.svideo.media.MediaInfo;
import com.aliyun.svideosdk.common.struct.common.MediaType;
import com.aliyun.svideosdk.common.struct.common.CropKey;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.ugsv.common.utils.DateTimeUtils;
import com.aliyun.ugsv.common.utils.image.ImageLoaderImpl;

import java.lang.ref.WeakReference;

/**
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 * <p>
 * Created by Administrator on 2017/1/16.
 */

public class AliyunImageCropActivity extends Activity implements HorizontalListView.OnScrollCallBack, SizeChangedNotifier.Listener,
    VideoTrimFrameLayout.OnVideoScrollCallBack, View.OnClickListener, CropCallback {

    public static final String VIDEO_PATH = "video_path";

    public static final VideoDisplayMode SCALE_CROP = VideoDisplayMode.SCALE;
    public static final VideoDisplayMode SCALE_FILL = VideoDisplayMode.FILL;

    public static final String RESULT_KEY_CROP_PATH = "crop_path";

    private static int mOutStrokeWidth;


    private AliyunICrop crop;

    private VideoTrimFrameLayout frame;
    private ImageView mImageView;


    private ImageView cancelBtn, nextBtn, transFormBtn;

    private FanProgressBar mCropProgress;
    private FrameLayout mCropProgressBg;


    private String path;
    private String outputPath;
    private int resolutionMode;
    private int ratioMode;
    private VideoQuality quality = VideoQuality.HD;
    private VideoCodecs mVideoCodecs = VideoCodecs.H264_HARDWARE;
    private int frameRate;
    private int gop;

    private int screenWidth;
    private int frameWidth;
    private int frameHeight;
    private int mScrollX;
    private int mScrollY;
    private int mImageWidth;
    private int mImageHeight;


    private VideoDisplayMode cropMode = VideoDisplayMode.SCALE;

    private boolean isCropping = false;
    private String mSuffix;
    private String mMimeType;
    private AsyncTask<Void, Void, Void> mDeleteTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.alivc_crop_activity_image_crop);
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        crop = AliyunCropCreator.createCropInstance(this);
        crop.setCropCallback(this);
        getData();
        initView();
        initSurface();
        new ImageLoaderImpl().loadImage(this, "file://" + path,
                                        new ImageLoaderOptions.Builder().skipMemoryCache().skipDiskCacheCache().build()
                                       ).into(mImageView);
        frame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameWidth = frame.getWidth();
                frameHeight = frame.getHeight();
                if (cropMode == SCALE_CROP) {
                    scaleCrop(mImageWidth, mImageHeight);

                } else if (cropMode == SCALE_FILL) {
                    scaleFill(mImageWidth, mImageHeight);
                }
                frame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void getData() {
        path = getIntent().getStringExtra(AlivcSvideoEditParam.VIDEO_PATH);
        int index = path.lastIndexOf(".");
        mSuffix = index == -1 ? "" : path.substring(index);
        resolutionMode = getIntent().getIntExtra(AlivcSvideoEditParam.VIDEO_RESOLUTION, AlivcSvideoEditParam.RESOLUTION_540P);
        cropMode = (VideoDisplayMode) getIntent().getSerializableExtra(AlivcSvideoEditParam.VIDEO_CROP_MODE);
        if (cropMode == null) {
            cropMode = VideoDisplayMode.SCALE;
        }
        quality = (VideoQuality) getIntent().getSerializableExtra(AlivcSvideoEditParam.VIDEO_QUALITY);
        if (quality == null) {
            quality = VideoQuality.HD;
        }
        mVideoCodecs = (VideoCodecs) getIntent().getSerializableExtra(AlivcSvideoEditParam.VIDEO_CODEC);
        if (mVideoCodecs == null) {
            mVideoCodecs = VideoCodecs.H264_HARDWARE;
        }
        gop = getIntent().getIntExtra(AlivcSvideoEditParam.VIDEO_GOP, 5);
        frameRate = getIntent().getIntExtra(AlivcSvideoEditParam.VIDEO_FRAMERATE, 25);
        ratioMode = getIntent().getIntExtra(AlivcSvideoEditParam.VIDEO_RATIO, CropKey.RATIO_MODE_3_4);
        BitmapUtils.checkAndAmendImgOrientation(path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        mMimeType = options.outMimeType;
        mImageWidth = options.outWidth;
        mImageHeight = options.outHeight;
    }

    public static final String getVersion() {
        return Version.VERSION;
    }

    private void initView() {
        mOutStrokeWidth = DensityUtil.dip2px(this, 5);
        transFormBtn = (ImageView) findViewById(R.id.aliyun_transform);
        transFormBtn.setOnClickListener(this);
        nextBtn = (ImageView) findViewById(R.id.aliyun_next);
        nextBtn.setOnClickListener(this);
        cancelBtn = (ImageView) findViewById(R.id.aliyun_back);
        cancelBtn.setOnClickListener(this);
        mCropProgressBg = (FrameLayout) findViewById(R.id.aliyun_crop_progress_bg);
        mCropProgressBg.setVisibility(View.GONE);
        mCropProgress = (FanProgressBar) findViewById(R.id.aliyun_crop_progress);
        mCropProgress.setOutRadius(DensityUtil.dip2px(this, 40) / 2 - mOutStrokeWidth / 2);
        mCropProgress.setOffset(mOutStrokeWidth / 2, mOutStrokeWidth / 2);
        mCropProgress.setOutStrokeWidth(mOutStrokeWidth);
    }


    public void initSurface() {
        frame = (VideoTrimFrameLayout) findViewById(R.id.aliyun_video_surfaceLayout);
        frame.setOnSizeChangedListener(this);
        frame.setOnScrollCallBack(this);
        resizeFrame();
        mImageView = (ImageView) findViewById(R.id.aliyun_image_view);
    }

    private void resizeFrame() {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frame.getLayoutParams();
        switch (ratioMode) {
        case AliyunSnapVideoParam.RATIO_MODE_1_1:
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_3_4:
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 4 / 3;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_9_16:
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 16 / 9;
            break;
        default:
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * 16 / 9;
            break;
        }
        frame.setLayoutParams(layoutParams);
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(msc != null) {
//            msc.disconnect();
//            msc = null;
//        }
        if (crop != null) {
            crop.dispose();
            crop = null;
        }

        if (mDeleteTask != null) {
            mDeleteTask.cancel(true);
            mDeleteTask = null;
        }
    }

    private void scaleCrop(int imageWidth, int imageHeight) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
        int s = Math.min(imageWidth, imageHeight);
        int b = Math.max(imageWidth, imageHeight);
        float imageRatio = (float) b / s;
        float ratio = 1f;
        switch (ratioMode) {
        case CropKey.RATIO_MODE_1_1:
            ratio = 1f;
            break;
        case CropKey.RATIO_MODE_3_4:
            ratio = (float) 4 / 3;
            break;
        case CropKey.RATIO_MODE_9_16:
            ratio = (float) 16 / 9;
            break;
        default:
            ratio = (float) 16 / 9;
            break;
        }
        //裁剪模式，以短边为准，长边超出容器
        if (imageWidth >= imageHeight) {
            //图片宽大于高，imageRatio = w/h  (以高为准，宽大于容器宽)
            layoutParams.height = frameHeight;
            layoutParams.width = (int)(frameHeight * imageRatio);
        } else {
            //图片宽小于高（imageRatio = h/w）
            if (imageRatio <= ratio) {
                //如果图片的比率小于所选择的比率时（以高为准，宽度大于容器）
                layoutParams.height = frameHeight;
                layoutParams.width = (int)(frameHeight / imageRatio);
            } else {
                //如果图片的比率大于所选择的比率时（以高为准，宽度填充）
                layoutParams.width = frameWidth;
                layoutParams.height = (int)(frameWidth * imageRatio);
            }
        }
        layoutParams.setMargins(0, 0, 0, 0);
        mImageView.setLayoutParams(layoutParams);
        cropMode = SCALE_CROP;
        transFormBtn.setActivated(false);
        resetScroll();
    }

    /**
     * 展示填充模式
     * @param imageWidth 图片真实的宽
     * @param imageHeight 图片真实的高度
     */
    private void scaleFill(int imageWidth, int imageHeight) {
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
        int s = Math.min(imageWidth, imageHeight);
        int b = Math.max(imageWidth, imageHeight);
        float imageRatio = (float) b / s;
        float ratio = 1f;
        switch (ratioMode) {
        case CropKey.RATIO_MODE_1_1:
            ratio = 1f;
            break;
        case CropKey.RATIO_MODE_3_4:
            ratio = (float) 4 / 3;
            break;
        case CropKey.RATIO_MODE_9_16:
            ratio = (float) 16 / 9;
            break;
        default:
            ratio = (float) 16 / 9;
            break;
        }

        //填充模式，以长边为准，短边填充
        if (imageWidth >= imageHeight) {
            //图片宽大于高
            layoutParams.width = frameWidth;
            layoutParams.height = (int) (frameWidth / imageRatio);
        } else {
            //图片宽小于高（imageRatio = h/w）
            if (imageRatio <= ratio) {
                //如果图片的比率小于所选择的比率时（以宽为准，高度填充）
                layoutParams.width = frameWidth;
                layoutParams.height = (int)(frameWidth * imageRatio);
            } else {
                //如果图片的比率大于所选择的比率时（以高为准，宽度填充）
                layoutParams.height = frameHeight;
                layoutParams.width = (int)(frameHeight / imageRatio);
            }
        }
        layoutParams.setMargins(0, 0, 0, 0);
        mImageView.setLayoutParams(layoutParams);
        cropMode = SCALE_FILL;
        transFormBtn.setActivated(true);
        resetScroll();
    }


    private void scanFile() {
        MediaScannerConnection.scanFile(getApplicationContext(),
                                        new String[] {outputPath},
                                        new String[] {mMimeType}, null);
//        if(msc != null && msc.isConnected()) {
//            msc.scanFile(outputPath, mMimeType);
//        }
    }

    @Override
    public void onBackPressed() {
        if (isCropping) {
            crop.cancel();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSizeChanged(View view, int w, int h, int oldw, int oldh) {

    }

    @Override
    public void onVideoScroll(float distanceX, float distanceY) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mImageView.getLayoutParams();
        int width = lp.width;
        int height = lp.height;

        if (width > frameWidth || height > frameHeight) {
            int maxHorizontalScroll = width - frameWidth;
            int maxVerticalScroll = height - frameHeight;
            if (maxHorizontalScroll > 0) {
                maxHorizontalScroll = maxHorizontalScroll / 2;
                mScrollX += distanceX;
                if (mScrollX > maxHorizontalScroll) {
                    mScrollX = maxHorizontalScroll;
                }
                if (mScrollX < -maxHorizontalScroll) {
                    mScrollX = -maxHorizontalScroll;
                }
            }
            if (maxVerticalScroll > 0) {
                maxVerticalScroll = maxVerticalScroll / 2;
                mScrollY += distanceY;
                if (mScrollY > maxVerticalScroll) {
                    mScrollY = maxVerticalScroll;
                }
                if (mScrollY < -maxVerticalScroll) {
                    mScrollY = -maxVerticalScroll;
                }
            }
            lp.setMargins(0, 0, mScrollX, mScrollY);
        }

        mImageView.setLayoutParams(lp);
    }

    @Override
    public void onVideoSingleTapUp() {
    }

    @Override
    public void onClick(View v) {
        if (v == transFormBtn) {
            if (isCropping) {
                return;
            }
            if (cropMode == SCALE_FILL) {
                scaleCrop(mImageWidth, mImageHeight);
            } else if (cropMode == SCALE_CROP) {
                scaleFill(mImageWidth, mImageHeight);

            }
        } else if (v == nextBtn) {
            startCrop();
        } else if (v == cancelBtn) {
            onBackPressed();
        }
    }

    private void startCrop() {
        if (ratioMode == 3) {
            //原比例时不进行缩放和裁剪
            finish();
            return;
        }
        if (frameWidth == 0 || frameHeight == 0) {
            ToastUtil.showToast(this, R.string.aliyun_video_crop_error);
            isCropping = false;
            return;
        }
        if (isCropping) {
            return;
        }
        int posX;
        int posY;
        int outputWidth = 0;
        int outputHeight = 0;
        int cropWidth;
        int cropHeight;
        outputPath = Constants.SDCardConstants.getDir(this) + DateTimeUtils.getDateTimeFromMillisecond(System.currentTimeMillis()) + "-crop" + mSuffix;
        float videoRatio = (float) mImageHeight / mImageWidth;
        float outputRatio = 1f;
        switch (ratioMode) {
        case CropKey.RATIO_MODE_1_1:
            outputRatio = 1f;
            break;
        case CropKey.RATIO_MODE_3_4:
            outputRatio = (float) 4 / 3;
            break;
        case CropKey.RATIO_MODE_9_16:
            outputRatio = (float) 16 / 9;
            break;
        default:
            outputRatio = (float) 16 / 9;
            break;
        }
        if (videoRatio > outputRatio) {
            posX = 0;
            posY = ((mImageView.getMeasuredHeight() - frameHeight) / 2 + mScrollY) * mImageWidth / frameWidth;
            switch (resolutionMode) {
            case CropKey.RESOLUTION_360P:
                outputWidth = 360;
                break;
            case AliyunSnapVideoParam.RESOLUTION_480P:
                outputWidth = 480;
                break;
            case AliyunSnapVideoParam.RESOLUTION_540P:
                outputWidth = 540;
                break;
            case AliyunSnapVideoParam.RESOLUTION_720P:
                outputWidth = 720;
                break;
            default:
                outputWidth = 720;
                break;
            }
            cropWidth = mImageWidth;
            cropHeight = 0;
            switch (ratioMode) {
            case AliyunSnapVideoParam.RATIO_MODE_1_1:
                cropHeight = mImageWidth;
                outputHeight = outputWidth;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_3_4:
                cropHeight = mImageWidth * 4 / 3;
                outputHeight = outputWidth * 4 / 3;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_9_16:
                cropHeight = mImageWidth * 16 / 9;
                outputHeight = outputWidth * 16 / 9;
                break;
            default:
                cropHeight = mImageWidth * 16 / 9;
                outputHeight = outputWidth * 16 / 9;
                break;
            }
        } else {
            posX = ((mImageView.getMeasuredWidth() - frameWidth) / 2 + mScrollX) * mImageHeight / frameHeight;
            posY = 0;
            switch (resolutionMode) {
            case CropKey.RESOLUTION_360P:
                outputWidth = 360;
                break;
            case AliyunSnapVideoParam.RESOLUTION_480P:
                outputWidth = 480;
                break;
            case AliyunSnapVideoParam.RESOLUTION_540P:
                outputWidth = 540;
                break;
            case AliyunSnapVideoParam.RESOLUTION_720P:
                outputWidth = 720;
                break;
            default:
                outputWidth = 720;
                break;
            }
            cropHeight = mImageHeight;
            switch (ratioMode) {
            case AliyunSnapVideoParam.RATIO_MODE_1_1:
                cropWidth = mImageHeight;
                outputHeight = outputWidth;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_3_4:
                cropWidth = mImageHeight * 3 / 4;
                outputHeight = outputWidth * 4 / 3;
                break;
            case AliyunSnapVideoParam.RATIO_MODE_9_16:
                cropWidth = mImageHeight * 9 / 16;
                outputHeight = outputWidth * 16 / 9;
                break;
            default:
                cropWidth = mImageHeight * 9 / 16;
                outputHeight = outputWidth * 16 / 9;
                break;
            }
        }
        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(outputPath);
        cropParam.setInputPath(path);
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);
        cropParam.setMediaType(MediaType.ANY_IMAGE_TYPE);
        Rect cropRect = new Rect(posX, posY, posX + cropWidth, posY + cropHeight);
        cropParam.setCropRect(cropRect);
        cropParam.setScaleMode(cropMode);
        cropParam.setFrameRate(frameRate);
        cropParam.setGop(gop);
        cropParam.setQuality(quality);
        cropParam.setVideoCodec(mVideoCodecs);
        cropParam.setFillColor(Color.BLACK);

        mCropProgressBg.setVisibility(View.VISIBLE);
        crop.setCropParam(cropParam);
        isCropping = true;
        crop.startCrop();
        mCropProgressBg.setVisibility(View.GONE);
        isCropping = false;


        Intent intent = getIntent();
        intent.putExtra(RESULT_KEY_CROP_PATH, outputPath);
        String tagClassName = AliyunSvideoActionConfig.getInstance().getAction().getTagClassName(ActionInfo.SVideoAction.CROP_TARGET_CLASSNAME);
        if (tagClassName == null) {
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            intent.setClassName(AliyunImageCropActivity.this, tagClassName);
            startActivity(intent);
        }
    }

    private void deleteFile() {
        mDeleteTask = new DeleteFileTask(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private static class DeleteFileTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AliyunImageCropActivity> weakReference;

        DeleteFileTask(AliyunImageCropActivity activity) {
            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (weakReference == null) {
                return null;
            }
            AliyunImageCropActivity activity = weakReference.get();
            if (activity != null) {
                FileUtils.deleteFile(activity.outputPath);
            }

            return null;
        }
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgressBg.setVisibility(View.GONE);
                switch (code) {
                case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_VIDEO:
                    ToastUtil.showToast(AliyunImageCropActivity.this, R.string.aliyun_video_crop_error);
                    break;
                case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_AUDIO:
                    ToastUtil.showToast(AliyunImageCropActivity.this, R.string.aliyun_not_supported_audio);
                    break;
                default:
                    break;
                }
//                progressDialog.dismiss();
                setResult(Activity.RESULT_CANCELED, getIntent());
            }
        });
        isCropping = false;

    }

    @Override
    public void onComplete(long duration) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgress.setVisibility(View.GONE);
                mCropProgressBg.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    //适配android Q
                    ThreadUtils.runOnSubThread(new Runnable() {
                        @Override
                        public void run() {
                            UriUtils.saveVideoToMediaStore(AliyunImageCropActivity.this.getApplicationContext(), outputPath);
                        }
                    });
                } else {
                    scanFile();
                }
                Intent intent = getIntent();
                intent.putExtra(RESULT_KEY_CROP_PATH, outputPath);
                String tagClassName = AliyunSvideoActionConfig.getInstance().getAction().getTagClassName(ActionInfo.SVideoAction.CROP_TARGET_CLASSNAME);
                if (tagClassName == null) {
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                } else {
                    intent.setClassName(AliyunImageCropActivity.this, tagClassName);
                    startActivity(intent);
                }

//                progressDialog.dismiss();
            }
        });
        isCropping = false;
    }

    @Override
    public void onCancelComplete() {
        //取消完成
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCropProgressBg.setVisibility(View.GONE);
            }
        });
        deleteFile();
        setResult(Activity.RESULT_CANCELED);
        finish();
        isCropping = false;
    }

    /**
     * 调转到该activity方法
     * @param context
     * @param svideoParam 配置参数，需要包含需要剪切的MediaInfo信息
     * @param requestCode
     */
    public static void startImageCropForResult(Activity context, AlivcSvideoEditParam svideoParam, int requestCode) {
        MediaInfo mediaInfo = svideoParam.getMediaInfo();
        if (mediaInfo == null) {
            return;
        }
        Intent intent = new Intent(context, AliyunImageCropActivity.class);
        intent.putExtra(AlivcSvideoEditParam.VIDEO_PATH, mediaInfo.filePath);
        intent.putExtra(AlivcSvideoEditParam.VIDEO_RATIO, svideoParam.getRatio());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_CROP_MODE, svideoParam.getCropMode());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_QUALITY, svideoParam.getVideoQuality());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_GOP, svideoParam.getGop());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_FRAMERATE, svideoParam.getFrameRate());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_RESOLUTION, svideoParam.getResolutionMode());
        intent.putExtra(AlivcSvideoEditParam.VIDEO_CODEC, svideoParam.getVideoCodec());
        context.startActivityForResult(intent, requestCode);
    }

}
