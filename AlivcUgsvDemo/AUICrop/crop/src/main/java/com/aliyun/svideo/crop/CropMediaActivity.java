/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.aliyun.common.utils.FileUtils;
import com.aliyun.ugsv.common.utils.FastClickUtil;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.svideo.crop.bean.AlivcCropInputParam;
import com.aliyun.svideo.crop.util.AUICropHelper;
import com.aliyun.svideo.media.MediaInfo;
import com.aliyun.svideo.media.MediaStorage;
import com.aliyun.svideo.media.MutiMediaView;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.CropKey;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.common.struct.recorder.CameraType;
import com.aliyun.svideosdk.common.struct.recorder.FlashType;
import com.aliyun.svideosdk.common.utils.AliyunSVideoUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 裁剪模块的media选择Activity
 */
public class CropMediaActivity extends Activity {

    private final String TAG = "CropMediaActivity";

    private int resolutionMode;
    private int ratioMode;
    private VideoDisplayMode cropMode = VideoDisplayMode.FILL;
    private int frameRate;
    private int gop;
    private int minVideoDuration;
    private int maxVideoDuration;
    private int minCropDuration;
    private VideoQuality quality = VideoQuality.SSD;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private static final int CROP_CODE = 3001;
    private static final int RECORD_CODE = 3002;

    public static final String RESULT_TYPE = "result_type";
    public static final int RESULT_TYPE_CROP = 4001;
    public static final int RESULT_TYPE_RECORD = 4002;

    /**
     * 录制参数
     */
    private int recordMode = AliyunSnapVideoParam.RECORD_MODE_AUTO;
    private String[] filterList;
    private int beautyLevel = 80;
    private boolean beautyStatus = true;
    private CameraType cameraType = CameraType.FRONT;
    private FlashType flashType = FlashType.ON;
    private int maxDuration = 30000;
    private int minDuration = 2000;
    private boolean needClip = true;
    private int sortMode = MediaStorage.SORT_MODE_MERGE;

    private MutiMediaView mMutiMediaView;
    private String mTempMediaFilePath = null;

    private static final String RECORD_CLASS = "com.aliyun.svideo.recorder.activity.AlivcSvideoRecordActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.alivc_crop_activity_media);
        getData();
        init();
    }

    private void getData() {
        resolutionMode = getIntent().getIntExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, CropKey.RESOLUTION_720P);
        cropMode = (VideoDisplayMode) getIntent().getSerializableExtra(AliyunSnapVideoParam.CROP_MODE);
        frameRate = getIntent().getIntExtra(AliyunSnapVideoParam.VIDEO_FRAMERATE, 30);
        gop = getIntent().getIntExtra(AliyunSnapVideoParam.VIDEO_GOP, 250);
        quality = (VideoQuality) getIntent().getSerializableExtra(AliyunSnapVideoParam.VIDEO_QUALITY);
        mVideoCodec = (VideoCodecs) getIntent().getSerializableExtra(AliyunSnapVideoParam.VIDEO_CODEC);
        ratioMode = getIntent().getIntExtra(AliyunSnapVideoParam.VIDEO_RATIO, CropKey.RATIO_MODE_9_16);
        boolean needRecord = getIntent().getBooleanExtra(AliyunSnapVideoParam.NEED_RECORD, true);
        minVideoDuration = getIntent().getIntExtra(AliyunSnapVideoParam.MIN_VIDEO_DURATION, 2000);
        maxVideoDuration = getIntent().getIntExtra(AliyunSnapVideoParam.MAX_VIDEO_DURATION, 10 * 60 * 1000);
        minCropDuration = getIntent().getIntExtra(AliyunSnapVideoParam.MIN_CROP_DURATION, 2000);
        recordMode = getIntent().getIntExtra(AliyunSnapVideoParam.RECORD_MODE, AliyunSnapVideoParam.RECORD_MODE_AUTO);
        filterList = getIntent().getStringArrayExtra(AliyunSnapVideoParam.FILTER_LIST);
        beautyLevel = getIntent().getIntExtra(AliyunSnapVideoParam.BEAUTY_LEVEL, 80);
        beautyStatus = getIntent().getBooleanExtra(AliyunSnapVideoParam.BEAUTY_STATUS, true);
        cameraType = (CameraType) getIntent().getSerializableExtra(AliyunSnapVideoParam.CAMERA_TYPE);
        if (cameraType == null) {
            cameraType = CameraType.FRONT;
        }
        flashType = (FlashType) getIntent().getSerializableExtra(AliyunSnapVideoParam.FLASH_TYPE);
        if (flashType == null) {
            flashType = FlashType.ON;
        }
        minDuration = getIntent().getIntExtra(AliyunSnapVideoParam.MIN_DURATION, 2000);
        maxDuration = getIntent().getIntExtra(AliyunSnapVideoParam.MAX_DURATION, 30000);
        needClip = getIntent().getBooleanExtra(AliyunSnapVideoParam.NEED_CLIP, true);
        sortMode = getIntent().getIntExtra(AliyunSnapVideoParam.SORT_MODE, MediaStorage.SORT_MODE_MERGE);

    }
    private void init() {

        mMutiMediaView = findViewById(R.id.crop_mediaview);
        mMutiMediaView.setMediaSortMode(sortMode);
        mMutiMediaView.setVideoDurationRange(minVideoDuration, maxVideoDuration);
        mMutiMediaView.setOnMediaClickListener(new MutiMediaView.OnMediaClickListener() {
            @Override
            public void onClick(MediaInfo info) {

                if (FastClickUtil.isFastClickActivity(CropMediaActivity.class.getSimpleName())) {
                    return;
                }
                if (info == null) {
                    Class recorder = null;
                    try {
                        recorder = Class.forName(RECORD_CLASS);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "ClassNotFoundException: " + RECORD_CLASS);
                    }
                    if (recorder == null) {
                        return;
                    }
                    Intent intent = new Intent(CropMediaActivity.this, recorder);
                    intent.putExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, resolutionMode);
                    intent.putExtra(AliyunSnapVideoParam.VIDEO_RATIO, ratioMode);
                    intent.putExtra(AliyunSnapVideoParam.RECORD_MODE, recordMode);
                    intent.putExtra(AliyunSnapVideoParam.FILTER_LIST, filterList);
                    intent.putExtra(AliyunSnapVideoParam.BEAUTY_LEVEL, beautyLevel);
                    intent.putExtra(AliyunSnapVideoParam.BEAUTY_STATUS, beautyStatus);
                    intent.putExtra(AliyunSnapVideoParam.CAMERA_TYPE, cameraType);
                    intent.putExtra(AliyunSnapVideoParam.FLASH_TYPE, flashType);
                    intent.putExtra(AliyunSnapVideoParam.NEED_CLIP, needClip);
                    intent.putExtra(AliyunSnapVideoParam.MAX_DURATION, maxDuration);
                    intent.putExtra(AliyunSnapVideoParam.MIN_DURATION, minDuration);
                    intent.putExtra(AliyunSnapVideoParam.VIDEO_QUALITY, quality);
                    intent.putExtra(AliyunSnapVideoParam.VIDEO_GOP, gop);
                    intent.putExtra(AliyunSnapVideoParam.VIDEO_CODEC, mVideoCodec);
                    intent.putExtra(AliyunSnapVideoParam.CROP_USE_GPU, getIntent().getBooleanExtra(AliyunSnapVideoParam.CROP_USE_GPU, false));
                    intent.putExtra("need_gallery", false);
                    startActivityForResult(intent, RECORD_CODE);
                } else {
                    String mediaPath = info.filePath;
                    if (info.filePath.endsWith("gif")) {
                        Toast.makeText(CropMediaActivity.this, R.string.alivc_crop_media_gif_not_support, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (info.mimeType.startsWith("image")) {
                        if (ratioMode == AliyunVideoCropActivity.RATIO_ORIGINAL) {
                            //原比例下android解码器对图片大小有要求，目前支持为单边不大于3840
                            try {
                                ParcelFileDescriptor pfd = CropMediaActivity.this.getContentResolver().openFileDescriptor(Uri.parse(info.fileUri), "r");
                                if (pfd != null) {
                                    Bitmap bitmap = BitmapFactory.decodeFileDescriptor(pfd.getFileDescriptor());
                                    if (bitmap != null && (bitmap.getHeight() > 3840 || bitmap.getWidth() > 3840)) {
                                        ToastUtils.show(CropMediaActivity.this, "原尺寸输出时，图片宽高不能超过3840");
                                        return;
                                    }
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }

                        if (AliyunSVideoUtils.isHEIFImage(info.filePath) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            Log.e(TAG, "HEIF file " + info.filePath);
                            final MediaInfo tempInfo = info;
                            ThreadUtils.runOnSubThread(new Runnable() {
                                @Override
                                public void run() {
                                    final String path = Environment.getExternalStorageDirectory().getPath() + File.separator + "DCIM" + File.separator + "Camera" + File.separator + "crop_temp_" + System.currentTimeMillis() + ".png";
                                    final boolean rs = AliyunSVideoUtils.convertHEIFImage(tempInfo.filePath, path, Bitmap.CompressFormat.PNG, 100);
                                    ThreadUtils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!rs) {
                                                ToastUtils.show(CropMediaActivity.this, "HEIF图片转化失败，请重试!");
                                                return;
                                            }
                                            Log.e(TAG, "HEIF file convert to " + path);
                                            mTempMediaFilePath = path;
                                            AlivcCropInputParam param = new AlivcCropInputParam.Builder()
                                                    .setPath(path)
                                                    .setResolutionMode(resolutionMode)
                                                    .setCropMode(cropMode)
                                                    .setQuality(quality)
                                                    .setGop(gop)
                                                    .setFrameRate(frameRate)
                                                    .setRatioMode(ratioMode)
                                                    .setMinCropDuration(minCropDuration)
                                                    .setVideoCodecs(mVideoCodec)
                                                    .build();
                                            AliyunImageCropActivity.startImageCropForResult(CropMediaActivity.this, param, CROP_CODE);
                                        }
                                    });
                                }
                            });
                            return;
                        }

                        AlivcCropInputParam param = new AlivcCropInputParam.Builder()
                        .setPath(mediaPath)
                        .setResolutionMode(resolutionMode)
                        .setCropMode(cropMode)
                        .setQuality(quality)
                        .setGop(gop)
                        .setFrameRate(frameRate)
                        .setRatioMode(ratioMode)
                        .setMinCropDuration(minCropDuration)
                        .setVideoCodecs(mVideoCodec)
                        .build();
                        AliyunImageCropActivity.startImageCropForResult(CropMediaActivity.this, param, CROP_CODE );
                    } else {
                        AlivcCropInputParam param = new AlivcCropInputParam.Builder()
                        .setPath(mediaPath)
                        .setResolutionMode(resolutionMode)
                        .setCropMode(cropMode)
                        .setQuality(quality)
                        .setGop(gop)
                        .setFrameRate(frameRate)
                        .setRatioMode(ratioMode)
                        .setMinCropDuration(minCropDuration)
                        .setVideoCodecs(mVideoCodec)
                        .setAction(CropKey.ACTION_TRANSCODE)
                        .setUseGPU( getIntent().getBooleanExtra(AliyunSnapVideoParam.CROP_USE_GPU, false))
                        .build();
                        AUICropHelper.startVideoCropForResult(CropMediaActivity.this, param, CROP_CODE );
                    }
                }
            }
        });
        mMutiMediaView.setOnActionListener(new MutiMediaView.OnActionListener() {
            @Override
            public void onNext(boolean isReachedMaxDuration) {

            }

            @Override
            public void onBack() {
                finish();
            }
        });
        mMutiMediaView.loadMedia();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  CROP_CODE) {
            if(!TextUtils.isEmpty(mTempMediaFilePath)){
                boolean rs = FileUtils.deleteFile(mTempMediaFilePath);
                Log.e(TAG, "delete temp file " + mTempMediaFilePath + " | " + rs);
            }
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.putExtra(RESULT_TYPE, RESULT_TYPE_CROP);
                }
                setResult(Activity.RESULT_OK, data);
                finish();
            } else if (resultCode ==  Activity.RESULT_CANCELED) {
                setResult(Activity.RESULT_CANCELED);
            }
        } else if (requestCode == RECORD_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    data.putExtra(RESULT_TYPE, RESULT_TYPE_RECORD);
                }
                setResult(Activity.RESULT_OK, data);
                finish();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                setResult(Activity.RESULT_CANCELED);
            }
        }
    }
    public static void startCropForResult(Activity activity, int requestCode, AliyunSnapVideoParam param) {
        Intent intent = new Intent(activity, CropMediaActivity.class);
        intent.putExtra(AliyunSnapVideoParam.SORT_MODE, param.getSortMode());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, param.getResolutionMode());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_RATIO, param.getRatioMode());
        intent.putExtra(AliyunSnapVideoParam.NEED_RECORD, param.isNeedRecord());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_QUALITY, param.getVideoQuality());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_CODEC, param.getVideoCodec());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_GOP, param.getGop());
        intent.putExtra(AliyunSnapVideoParam.VIDEO_FRAMERATE, param.getFrameRate());
        intent.putExtra(AliyunSnapVideoParam.CROP_MODE, param.getScaleMode());
        intent.putExtra(AliyunSnapVideoParam.MIN_CROP_DURATION, param.getMinCropDuration());
        intent.putExtra(AliyunSnapVideoParam.MIN_VIDEO_DURATION, param.getMinVideoDuration());
        intent.putExtra(AliyunSnapVideoParam.MAX_VIDEO_DURATION, param.getMaxVideoDuration());
        intent.putExtra(AliyunSnapVideoParam.RECORD_MODE, param.getRecordMode());
        intent.putExtra(AliyunSnapVideoParam.FILTER_LIST, param.getFilterList());
        intent.putExtra(AliyunSnapVideoParam.BEAUTY_LEVEL, param.getBeautyLevel());
        intent.putExtra(AliyunSnapVideoParam.BEAUTY_STATUS, param.getBeautyStatus());
        intent.putExtra(AliyunSnapVideoParam.CAMERA_TYPE, param.getCameraType());
        intent.putExtra(AliyunSnapVideoParam.FLASH_TYPE, param.getFlashType());
        intent.putExtra(AliyunSnapVideoParam.NEED_CLIP, param.isNeedClip());
        intent.putExtra(AliyunSnapVideoParam.MAX_DURATION, param.getMaxDuration());
        intent.putExtra(AliyunSnapVideoParam.MIN_DURATION, param.getMinDuration());
        intent.putExtra(AliyunSnapVideoParam.CROP_USE_GPU, param.isCropUseGPU());
        activity.startActivityForResult(intent, requestCode);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMutiMediaView.onDestroy();
    }


}
