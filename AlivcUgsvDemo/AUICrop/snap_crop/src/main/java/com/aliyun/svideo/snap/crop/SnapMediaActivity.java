/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.snap.crop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.aliyun.ugsv.common.utils.FastClickUtil;
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

/**
 * 裁剪模块的media选择Activity
 */
public class SnapMediaActivity extends Activity {
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

    private static final String SNAP_RECORD_CLASS = "com.aliyun.svideo.snap.record.AliyunVideoRecorder";

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
    private final String TAG = getClass().getSimpleName();
    private MutiMediaView mMutiMediaView;

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
        mMutiMediaView.setOnActionListener(new MutiMediaView.OnActionListener() {
            @Override
            public void onNext(boolean isReachedMaxDuration) {

            }

            @Override
            public void onBack() {
                finish();
            }
        });
        mMutiMediaView.setOnMediaClickListener(new MutiMediaView.OnMediaClickListener() {
            @Override
            public void onClick(MediaInfo info) {
                if (FastClickUtil.isFastClickActivity(SnapMediaActivity.class.getSimpleName())) {
                    return;
                }
                if (info == null) {

                    Class recorder = null;
                    try {
                        recorder = Class.forName(SNAP_RECORD_CLASS);
                    } catch (ClassNotFoundException e) {
                        Log.e(TAG, "ClassNotFoundException: " + SNAP_RECORD_CLASS);
                    }
                    if (recorder == null) {
                        return;
                    }
                    Intent intent = new Intent(SnapMediaActivity.this, recorder);
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

                    String mediaPath = info.filePath ;

                    if (info.filePath.endsWith("gif")) {
                        Toast.makeText(SnapMediaActivity.this, R.string.alivc_tip_crop_gif, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (info.mimeType.startsWith("image")) {
                        Intent intent = new Intent(SnapMediaActivity.this, AliyunImageCropActivity.class);
                        intent.putExtra(CropKey.VIDEO_PATH, mediaPath);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, resolutionMode);
                        intent.putExtra(AliyunSnapVideoParam.CROP_MODE, cropMode);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_QUALITY, quality);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_GOP, gop);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_FRAMERATE, frameRate);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_RATIO, ratioMode);
                        intent.putExtra(AliyunSnapVideoParam.MIN_CROP_DURATION, minCropDuration);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_CODEC, mVideoCodec);
                        startActivityForResult(intent, CROP_CODE);
                    } else {
                        Intent intent = new Intent(SnapMediaActivity.this, AliyunVideoCropActivity.class);
                        intent.putExtra(CropKey.VIDEO_PATH, mediaPath);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_RESOLUTION, resolutionMode);
                        intent.putExtra(AliyunSnapVideoParam.CROP_MODE, cropMode);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_QUALITY, quality);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_GOP, gop);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_FRAMERATE, frameRate);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_RATIO, ratioMode);
                        intent.putExtra(AliyunSnapVideoParam.MIN_CROP_DURATION, minCropDuration);
                        intent.putExtra(CropKey.ACTION, CropKey.ACTION_TRANSCODE);
                        intent.putExtra(AliyunSnapVideoParam.VIDEO_CODEC, mVideoCodec);
                        intent.putExtra(AliyunSnapVideoParam.CROP_USE_GPU, getIntent().getBooleanExtra(AliyunSnapVideoParam.CROP_USE_GPU, false));
                        startActivityForResult(intent, CROP_CODE);
                    }
                }
            }
        });
        mMutiMediaView.loadMedia();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode ==  CROP_CODE) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMutiMediaView.onDestroy();
    }

}
