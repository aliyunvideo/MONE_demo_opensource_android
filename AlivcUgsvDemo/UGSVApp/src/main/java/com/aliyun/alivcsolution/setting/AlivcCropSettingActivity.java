/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution.setting;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.svideo.crop.bean.AlivcCropInputParam;
import com.aliyun.svideo.crop.util.AUICropHelper;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.ugsv.common.utils.FastClickUtil;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.zhihu.matisse.AVMatisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;

public class AlivcCropSettingActivity extends AVBaseThemeActivity implements View.OnClickListener {

    private static final int CROP_REQUEST_CODE_CHOOSE = 103;
    private static final int CROP_PERMISSION_REQUEST_CODE = 1002;

    String[] effDirs;

    /**
     * 默认帧率
     */
    private static final int DEFAULT_FRAMR_RATE = 30;
    /**
     * 默认Gop参数
     */
    private static final int DEFAULT_GOP = 250;
    private static final int REQUEST_CROP = 2002;

    private Button startImport;


    private EditText frameRateEdit, gopEdit;
    private ImageView back;
    private VideoDisplayMode cropMode = VideoDisplayMode.SCALE;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;
    private boolean ifPaused = false;
    //视频编码方式选择按钮
    private Button mEncorderHardwareBtn, mEncorderOpenh264Btn;
    /**
     * 视频质量button
     */
    private Button mQualitySuperBtn, mQualityHighBtn, mQualityNomalBtn, mQualityLowBtn;

    /**
     * 视频比例button
     */
    private Button mCropRatio9P16Btn, mCropRatio3P4Btn, mCropRatio1P1Btn, mCropRatioOriginalBtn;

    /**
     * 分辨率button
     */
    private Button mCropResolutionP360Btn, mCropResolutionP480Btn, mCropResolutionP540Btn, mCropResolutionP720Btn;
    private VideoQuality mVideoQuality;
    private int mRatioMode;
    private int mResolutionMode;

    /**
     * 原比例
     */
    private static final int RATIO_ORIGINAL = 3;
    private Button mCropModeFillButton, mCropModeCropButton;
    private VideoDisplayMode mCropMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_svideo_activity_crop_setting);
        initView();

    }



    @Override
    protected void onPause() {
        super.onPause();
        ifPaused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ifPaused) {
            //editor.resume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        startImport = findViewById(R.id.alivc_crop_start_import);
        startImport.setOnClickListener(this);

        back = (ImageView) findViewById(R.id.aliyun_back);
        back.setOnClickListener(this);

        // 录入参数
        frameRateEdit = (EditText) findViewById(R.id.alivc_crop_frame_rate_edit);
        gopEdit = (EditText) findViewById(R.id.alivc_crop_gop_edit);

        // 视频质量
        mQualitySuperBtn = findViewById(R.id.alivc_video_quality_super);
        mQualitySuperBtn.setOnClickListener(this);
        mQualityHighBtn = findViewById(R.id.alivc_video_quality_high);
        mQualityHighBtn.setOnClickListener(this);
        mQualityNomalBtn = findViewById(R.id.alivc_video_quality_normal);
        mQualityNomalBtn.setOnClickListener(this);
        mQualityLowBtn = findViewById(R.id.alivc_video_quality_low);
        mQualityLowBtn.setOnClickListener(this);

        // 视频比例
        mCropRatio9P16Btn = findViewById(R.id.alivc_video_ratio_9_16);
        mCropRatio3P4Btn = findViewById(R.id.alivc_video_ratio_3_4);
        mCropRatio1P1Btn = findViewById(R.id.alivc_video_ratio_1_1);
        mCropRatioOriginalBtn = findViewById(R.id.alivc_video_ratio_original);
        mCropRatio9P16Btn.setOnClickListener(this);
        mCropRatio3P4Btn.setOnClickListener(this);
        mCropRatio1P1Btn.setOnClickListener(this);
        mCropRatioOriginalBtn.setOnClickListener(this);

        // 分辨率
        mCropResolutionP360Btn = findViewById(R.id.alivc_record_resolution_360p);
        mCropResolutionP480Btn = findViewById(R.id.alivc_record_resolution_480p);
        mCropResolutionP540Btn = findViewById(R.id.alivc_record_resolution_540p);
        mCropResolutionP720Btn = findViewById(R.id.alivc_record_resolution_720p);
        mCropResolutionP360Btn.setOnClickListener(this);
        mCropResolutionP480Btn.setOnClickListener(this);
        mCropResolutionP540Btn.setOnClickListener(this);
        mCropResolutionP720Btn.setOnClickListener(this);

        // 裁剪模式
        mCropModeFillButton = findViewById(R.id.radio_fill);
        mCropModeCropButton = findViewById(R.id.radio_crop);
        mCropModeFillButton.setOnClickListener(this);
        mCropModeCropButton.setOnClickListener(this);

        // 视频编码相关按钮
        mEncorderHardwareBtn = findViewById(R.id.alivc_crop_encoder_hardware);
        mEncorderOpenh264Btn = findViewById(R.id.alivc_crop_encoder_openh264);
        mEncorderHardwareBtn.setOnClickListener(this);
        mEncorderOpenh264Btn.setOnClickListener(this);

        //初始化配置
        onRatioSelected(mCropRatio9P16Btn);
        onEncoderSelected(mEncorderHardwareBtn);
        onResolutionSelected(mCropResolutionP720Btn);
        onQualitySelected(mQualityHighBtn);
        onCropMode(mCropModeFillButton);
    }

    @Override
    public void onClick(View v) {
        if (v == startImport) {
            if (FastClickUtil.isFastClickActivity(this.getClass().getSimpleName())) {
                return;
            }
            // 视频裁剪
            if (!PermissionUtils.checkPermissionsGroup(this, PermissionUtils.PERMISSION_STORAGE)) {
                ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
                PermissionUtils.requestPermissions(this, PermissionUtils.PERMISSION_STORAGE, CROP_PERMISSION_REQUEST_CODE);
                return;
            }
            AVMatisse.from(this)
                    .choose(MimeType.ofVideo(), true)
                    .showSingleMediaType(true)
                    .countable(true)
                    .maxSelectable(1)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(false) // Default is `true`
                    .forResult(CROP_REQUEST_CODE_CHOOSE);

        } else if (v == mEncorderHardwareBtn || v == mEncorderOpenh264Btn) {
            onEncoderSelected(v);
        } else if (v == mQualityHighBtn || v == mQualityLowBtn || v == mQualitySuperBtn || v == mQualityNomalBtn) {
            onQualitySelected(v);
        } else if (v == mCropRatio1P1Btn || v == mCropRatio3P4Btn || v == mCropRatio9P16Btn || v == mCropRatioOriginalBtn) {
            onRatioSelected(v);
        } else if (v == mCropResolutionP360Btn || v == mCropResolutionP480Btn || mCropResolutionP540Btn == v
                   || v == mCropResolutionP720Btn) {
            onResolutionSelected(v);
        } else if (v ==  back) {
            finish();
        } else if (v == mCropModeCropButton || v == mCropModeFillButton) {
            onCropMode(v);
        }
    }

    /**
     * 视频质量选择
     *
     * @param view 被点击按钮
     */
    private void onQualitySelected(View view) {
        mQualitySuperBtn.setSelected(false);
        mQualityHighBtn.setSelected(false);
        mQualityNomalBtn.setSelected(false);
        mQualityLowBtn.setSelected(false);
        if (view == mQualitySuperBtn) {
            mVideoQuality = VideoQuality.SSD;
            mQualitySuperBtn.setSelected(true);
        } else if (view == mQualityHighBtn) {
            mQualityHighBtn.setSelected(true);
            mVideoQuality = VideoQuality.HD;
        } else if (view == mQualityNomalBtn) {
            mQualityNomalBtn.setSelected(true);
            mVideoQuality = VideoQuality.SD;
        } else {
            mQualityLowBtn.setSelected(true);
            mVideoQuality = VideoQuality.LD;
        }
    }

    /**
     * 视频比例选择
     *
     * @param view
     */
    private void onRatioSelected(View view) {
        mCropRatioOriginalBtn.setSelected(false);
        mCropRatio9P16Btn.setSelected(false);
        mCropRatio3P4Btn.setSelected(false);
        mCropRatio1P1Btn.setSelected(false);
        if (view == mCropRatio1P1Btn) {
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_1_1;
            mCropRatio1P1Btn.setSelected(true);
        } else if (view == mCropRatio9P16Btn) {
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;
            mCropRatio9P16Btn.setSelected(true);
        } else if (view == mCropRatio3P4Btn) {
            mCropRatio3P4Btn.setSelected(true);
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_3_4;
        } else {
            mRatioMode = RATIO_ORIGINAL;
            mCropRatioOriginalBtn.setSelected(true);
        }
    }

    /**
     * 视频分辨率选择
     *
     * @param view
     */
    private void onResolutionSelected(View view) {
        mCropResolutionP360Btn.setSelected(false);
        mCropResolutionP480Btn.setSelected(false);
        mCropResolutionP540Btn.setSelected(false);
        mCropResolutionP720Btn.setSelected(false);
        if (view == mCropResolutionP360Btn) {
            mCropResolutionP360Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_360P;
        } else if (view == mCropResolutionP480Btn) {
            mCropResolutionP480Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_480P;

        } else if (view == mCropResolutionP540Btn) {
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;
            mCropResolutionP540Btn.setSelected(true);

        } else {
            mCropResolutionP720Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_720P;

        }

    }

    /**
     * 视频裁剪方式选择
     *
     * @param view
     */
    private void onCropMode(View view) {
        mCropModeFillButton.setSelected(false);
        mCropModeCropButton.setSelected(false);

        if (view == mCropModeFillButton) {
            mCropModeFillButton.setSelected(true);
            mCropMode = VideoDisplayMode.FILL;
        } else {
            mCropModeCropButton.setSelected(true);
            mCropMode = VideoDisplayMode.SCALE;
        }
    }

    /**
     * 视频编码方式选择
     *
     * @param view
     */
    private void onEncoderSelected(View view) {
        mEncorderHardwareBtn.setSelected(false);
        mEncorderOpenh264Btn.setSelected(false);

        if (view == mEncorderOpenh264Btn) {
            mEncorderOpenh264Btn.setSelected(true);
            mVideoCodec = VideoCodecs.H264_SOFT_OPENH264;
        } else if (view == mEncorderHardwareBtn) {
            mEncorderHardwareBtn.setSelected(true);
            mVideoCodec = VideoCodecs.H264_HARDWARE;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CROP_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK) {
            String inputGop = gopEdit.getText().toString();
            int gop = DEFAULT_GOP;
            if (!TextUtils.isEmpty(inputGop)) {
                try {
                    gop = Integer.parseInt(inputGop);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputFrameRate = frameRateEdit.getText().toString();
            int frameRate  = DEFAULT_FRAMR_RATE;
            if (!TextUtils.isEmpty(inputFrameRate)) {
                try {
                    frameRate = Integer.parseInt(inputFrameRate);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }
            List<String> path = AVMatisse.obtainPathResult(data);
            AlivcCropInputParam cropParam = new AlivcCropInputParam.Builder()
                    .setFrameRate(frameRate)
                    .setGop(gop)
                    .setPath(path.get(0))
                    .setCropMode(cropMode)
                    .setQuality(mVideoQuality)
                    .setVideoCodecs(mVideoCodec)
                    .setResolutionMode(mResolutionMode)
                    .setRatioMode(mRatioMode)
                    .setCropMode(mCropMode)
                    .setMinCropDuration(3000)
                    .build();
            AUICropHelper.startVideoCropForResult(this, cropParam, -1);
        }
    }
}
