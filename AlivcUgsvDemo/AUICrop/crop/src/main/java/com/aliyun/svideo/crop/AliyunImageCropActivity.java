/*
 * Copyright (C) 2010-2013 Alibaba Group Holding Limited
 */

package com.aliyun.svideo.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.aliyun.common.global.Version;
import com.aliyun.common.utils.DensityUtil;
import com.aliyun.common.utils.FileUtils;
import com.aliyun.common.utils.ToastUtil;
import com.aliyun.svideosdk.crop.impl.AliyunCropCreator;
import com.aliyun.svideosdk.crop.CropParam;
import com.aliyun.svideosdk.crop.AliyunICrop;
import com.aliyun.svideosdk.crop.CropCallback;
import com.aliyun.ugsv.common.utils.BitmapUtils;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.UriUtils;
import com.aliyun.ugsv.common.utils.image.ImageLoaderOptions;
import com.aliyun.svideosdk.common.AliyunErrorCode;
import com.aliyun.svideo.base.Constants;
import com.aliyun.svideo.base.widget.FanProgressBar;
import com.aliyun.svideo.base.widget.HorizontalListView;
import com.aliyun.svideo.base.widget.SizeChangedNotifier;
import com.aliyun.svideo.base.widget.VideoTrimFrameLayout;
import com.aliyun.svideosdk.common.struct.common.MediaType;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
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
    public static final int REQUEST_CODE_EDITOR_IMAGE_CROP = 2;

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

    private int screenWidth;
    private int frameWidth;
    private int frameHeight;
    private int mScrollX;
    private int mScrollY;
    private int mImageWidth;
    private int mImageHeight;


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
        new ImageLoaderImpl().loadImage(this, "file://" + path, new ImageLoaderOptions.Builder().skipDiskCacheCache().skipMemoryCache().build()).into(mImageView);
        frame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameWidth = frame.getWidth();
                frameHeight = frame.getHeight();
                if (CropConfig.Companion.getInstance().getVideoDisplayMode() == VideoDisplayMode.SCALE) {
                    scaleCrop(mImageWidth, mImageHeight);
                } else if (CropConfig.Companion.getInstance().getVideoDisplayMode() == VideoDisplayMode.FILL) {
                    scaleFill(mImageWidth, mImageHeight);
                }
                frame.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void getData() {

        path = CropConfig.Companion.getInstance().getInputPath();
        int index = path.lastIndexOf(".");
        mSuffix = index == -1 ? "" : path.substring(index);
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
        layoutParams.width = screenWidth;
        layoutParams.height = (int) (screenWidth / CropConfig.Companion.getInstance().getRatio());
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
        float ratio = 1 / CropConfig.Companion.getInstance().getRatio();
        if (CropConfig.Companion.getInstance().isOriginRatio()) {
            //原比例模式下与填充模式一致

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
        } else {
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
        }
        layoutParams.setMargins(0, 0, 0, 0);
        mImageView.setLayoutParams(layoutParams);
        CropConfig.Companion.getInstance().setVideoDisplayMode(VideoDisplayMode.SCALE);
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
        float ratio = 1 / CropConfig.Companion.getInstance().getRatio();
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
        CropConfig.Companion.getInstance().setVideoDisplayMode(VideoDisplayMode.FILL);
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
            if (CropConfig.Companion.getInstance().getVideoDisplayMode() == VideoDisplayMode.SCALE) {
                scaleCrop(mImageWidth, mImageHeight);
            } else if (CropConfig.Companion.getInstance().getVideoDisplayMode() == VideoDisplayMode.FILL) {
                scaleFill(mImageWidth, mImageHeight);

            }
        } else if (v == nextBtn) {
            startCrop();
        } else if (v == cancelBtn) {
            onBackPressed();
        }
    }

    private void startCrop() {

        if (frameWidth == 0 || frameHeight == 0) {
            ToastUtil.showToast(this, R.string.alivc_crop_video_tip_crop_failed);
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
        float outputRatio = CropConfig.Companion.getInstance().getRatio();
        if (videoRatio > outputRatio) {
            posX = 0;
            posY = ((mImageView.getMeasuredHeight() - frameHeight) / 2 + mScrollY) * mImageWidth / frameWidth;
            outputWidth = CropConfig.Companion.getInstance().getResolution();
            cropWidth = mImageWidth;
            cropHeight = (int) (mImageWidth / CropConfig.Companion.getInstance().getRatio());
            outputHeight = (int) (outputWidth / CropConfig.Companion.getInstance().getRatio());
        } else if (videoRatio < outputRatio) {
            posX = ((mImageView.getMeasuredWidth() - frameWidth) / 2 + mScrollX) * mImageHeight / frameHeight;
            posY = 0;
            outputWidth = CropConfig.Companion.getInstance().getResolution();
            cropHeight = mImageHeight;
            cropWidth = (int) (mImageHeight * CropConfig.Companion.getInstance().getRatio());
            outputHeight = (int) (outputHeight / CropConfig.Companion.getInstance().getRatio());

        } else {
            // 原比例或videoRatio = outputRatio执行else

            posX = 0;
            posY = 0;

            outputWidth = CropConfig.Companion.getInstance().getResolution();
            cropHeight = mImageHeight;
            cropWidth = (int) (mImageHeight * CropConfig.Companion.getInstance().getRatio());
            outputHeight = (int) (outputWidth / CropConfig.Companion.getInstance().getRatio());
        }
        CropParam cropParam = new CropParam();
        cropParam.setOutputPath(outputPath);
        cropParam.setInputPath(path);
        cropParam.setOutputWidth(outputWidth);
        cropParam.setOutputHeight(outputHeight);
        cropParam.setMediaType(MediaType.ANY_IMAGE_TYPE);
        Rect cropRect = new Rect(posX, posY, posX + cropWidth, posY + cropHeight);
        cropParam.setCropRect(cropRect);
        cropParam.setScaleMode(CropConfig.Companion.getInstance().getVideoDisplayMode());
        cropParam.setFrameRate(CropConfig.Companion.getInstance().getFps());
        cropParam.setGop(CropConfig.Companion.getInstance().getGop());
        cropParam.setQuality(CropConfig.Companion.getInstance().getVideoQuality());
        cropParam.setVideoCodec(CropConfig.Companion.getInstance().getCodec());
        if (CropConfig.Companion.getInstance().getBitRate() != CropConfig.DEFAULT_BITRATE) {
            cropParam.setVideoBitrate(CropConfig.Companion.getInstance().getBitRate());
        }
        cropParam.setFillColor(Color.BLACK);

        mCropProgressBg.setVisibility(View.VISIBLE);
        crop.setCropParam(cropParam);
        isCropping = true;
        crop.startCrop();
    }
    /**
     * 裁剪结束
     */
    private void onCropComplete() {
        //裁剪结束
        Intent intent = getIntent();
        setResult(Activity.RESULT_OK, intent);
        finish();
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
                    ToastUtil.showToast(AliyunImageCropActivity.this, R.string.alivc_crop_video_tip_not_supported_video);
                    break;
                case AliyunErrorCode.ALIVC_SVIDEO_ERROR_MEDIA_NOT_SUPPORTED_AUDIO:
                    ToastUtil.showToast(AliyunImageCropActivity.this, R.string.alivc_crop_video_tip_not_supported_audio);
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
                            UriUtils.saveImgToMediaStore(AliyunImageCropActivity.this.getApplicationContext(), outputPath);
                        }
                    });
                } else {
                    scanFile();
                }
                onCropComplete();
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

    public static void startImageCropForResult(Activity context, int requestCode) {
        Intent intent = new Intent(context, AliyunImageCropActivity.class);
        context.startActivityForResult(intent, requestCode);
    }

}
