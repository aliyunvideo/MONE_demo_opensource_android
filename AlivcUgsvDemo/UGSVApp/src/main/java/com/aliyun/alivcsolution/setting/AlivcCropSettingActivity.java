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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.aio.avbaseui.avdialog.AVCommonPickerDialog;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.svideo.crop.CropConfig;
import com.aliyun.svideo.crop.util.AUICropHelper;
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

public class AlivcCropSettingActivity extends AVBaseThemeActivity implements View.OnClickListener, AVCommonPickerDialog.OnPickListener {

    private static final int CROP_REQUEST_CODE_CHOOSE = 103;
    private static final int CROP_PERMISSION_REQUEST_CODE = 1002;

    private Button startImport;

    private ImageView back;

    private RelativeLayout mVideoRatioLayout;
    private TextView mTvVideoRatio;
    private AVCommonPickerDialog.ArgsSelector mVideoRatio;
    private static final int REQUEST_CODE_RATIO = 0x11;

    private RelativeLayout mVideoResolutionLayout;
    private TextView mTvVideoResolution;
    private AVCommonPickerDialog.ArgsSelector mVideoResolution;
    private static final int REQUEST_CODE_RESOLUTION = 0x12;

    private RelativeLayout mVideoCropModeLayout;
    private TextView mTvVideoCropMode;
    private AVCommonPickerDialog.ArgsSelector mVideoCropMode;
    private static final int REQUEST_CODE_CROP_MODE = 0x13;

    private RelativeLayout mVideoCodecModeLayout;
    private TextView mTvVideoCodecMode;
    private AVCommonPickerDialog.ArgsSelector mVideoCodecMode;
    private static final int REQUEST_CODE_CODEC_MODE = 0x14;

    private RelativeLayout mVideoQualityLayout;
    private TextView mTvVideoQuality;
    private AVCommonPickerDialog.ArgsSelector mVideoQuality;
    private static final int REQUEST_CODE_QUALITY = 0x15;

    private RelativeLayout mVideoFrameRateLayout;
    private EditText mFrameRateEdit;

    private RelativeLayout mVideoGopLayout;
    private EditText mGopEdit;

    private RelativeLayout mVideoCodeRateLayout;
    private EditText mBitRateEdit;

    private AVCommonPickerDialog mPickerDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alivc_svideo_activity_crop_setting);
        initView();
        initData();
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

        mVideoRatioLayout = findViewById(R.id.video_ratio_layout);
        mVideoRatioLayout.setOnClickListener(this);
        mTvVideoRatio = findViewById(R.id.tv_video_ratio);

        mVideoResolutionLayout = findViewById(R.id.video_resolution_layout);
        mVideoResolutionLayout.setOnClickListener(this);
        mTvVideoResolution = findViewById(R.id.tv_video_resolution);

        mVideoCropModeLayout = findViewById(R.id.video_crop_mode_layout);
        mVideoCropModeLayout.setOnClickListener(this);
        mTvVideoCropMode = findViewById(R.id.tv_video_crop_mode);

        mVideoCodecModeLayout = findViewById(R.id.video_codec_layout);
        mVideoCodecModeLayout.setOnClickListener(this);
        mTvVideoCodecMode= findViewById(R.id.tv_video_codec);

        mVideoQualityLayout = findViewById(R.id.video_quality_layout);
        mVideoQualityLayout.setOnClickListener(this);
        mTvVideoQuality = findViewById(R.id.tv_video_quality);

        mVideoFrameRateLayout= findViewById(R.id.video_frame_rate_layout);
        mVideoFrameRateLayout.setOnClickListener(this);
        mFrameRateEdit = findViewById(R.id.ed_video_frame_rate);

        mVideoGopLayout = findViewById(R.id.video_gop_layout);
        mVideoGopLayout.setOnClickListener(this);
        mGopEdit = (EditText) findViewById(R.id.ed_video_gop);

        mVideoCodeRateLayout = findViewById(R.id.video_code_rate_layout);
        mVideoCodeRateLayout.setOnClickListener(this);
        mBitRateEdit = findViewById(R.id.ed_video_code_rate);
    }

    private void initData() {
        CropConfig.Companion.getInstance().reset();

        mVideoRatio = new AVCommonPickerDialog.ArgsSelector();
        mVideoRatio.mRequestCode = REQUEST_CODE_RATIO;
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_original), CropConfig.RATIO_MODE_ORIGIN));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_9_16), CropConfig.RATIO_MODE_9_16));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_3_4), CropConfig.RATIO_MODE_3_4));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_1_1), CropConfig.RATIO_MODE_1_1));
        mVideoRatio.mSelectedIndex = 3;
        mVideoRatio.mTitle = getResources().getString(R.string.alivc_crop_setting_ratio);

        mVideoResolution = new AVCommonPickerDialog.ArgsSelector();
        mVideoResolution.mRequestCode = REQUEST_CODE_RESOLUTION;
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_1080p), CropConfig.RESOLUTION_1080P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_720p), CropConfig.RESOLUTION_720P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_540p), CropConfig.RESOLUTION_540P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_360p), CropConfig.RESOLUTION_360P));
        mVideoResolution.mSelectedIndex = 1;
        mVideoResolution.mTitle = getResources().getString(R.string.alivc_crop_setting_resolution);

        mVideoCropMode = new AVCommonPickerDialog.ArgsSelector();
        mVideoCropMode.mRequestCode = REQUEST_CODE_CROP_MODE;
        mVideoCropMode.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_mode_fill), VideoDisplayMode.FILL));
        mVideoCropMode.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_mode_crop),  VideoDisplayMode.SCALE));
        mVideoCropMode.mSelectedIndex = 0;
        mVideoCropMode.mTitle = getResources().getString(R.string.alivc_media_crop_mode);

        mVideoCodecMode = new AVCommonPickerDialog.ArgsSelector();
        mVideoCodecMode.mRequestCode = REQUEST_CODE_CODEC_MODE;
        mVideoCodecMode.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_encoder_hardware), VideoCodecs.H264_HARDWARE));
        mVideoCodecMode.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_encoder_openh264), VideoCodecs.H264_SOFT_OPENH264));
        mVideoCodecMode.mSelectedIndex = 0;
        mVideoCodecMode.mTitle = getResources().getString(R.string.alivc_crop_setting_codec);

        mVideoQuality = new AVCommonPickerDialog.ArgsSelector();
        mVideoQuality.mRequestCode = REQUEST_CODE_QUALITY;
        mVideoQuality.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_quality_super), VideoQuality.SSD));
        mVideoQuality.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_quality_high), VideoQuality.HD));
        mVideoQuality.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_quality_meidan), VideoQuality.SD));
        mVideoQuality.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_quality_low), VideoQuality.LD));
        mVideoQuality.mSelectedIndex = 1;
        mVideoQuality.mTitle = getResources().getString(R.string.alivc_crop_setting_quality);
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

        } else if (v == mVideoRatioLayout) {
            mVideoRatio.mSelectedIndex = mVideoRatio.findSelectedIndexByText(CropConfig.Companion.getInstance().getRatio());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoRatio).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoRatio.mTitle);
        } else if (v == mVideoResolutionLayout) {
            mVideoResolution.mSelectedIndex = mVideoResolution.findSelectedIndexByText(CropConfig.Companion.getInstance().getResolution());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoResolution).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoResolution.mTitle);
        } else if (v == mVideoCropModeLayout) {
            mVideoCropMode.mSelectedIndex = mVideoCropMode.findSelectedIndexByText(CropConfig.Companion.getInstance().getVideoDisplayMode());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoCropMode).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoCropMode.mTitle);
        } else if (v == mVideoCodecModeLayout) {
            mVideoCodecMode.mSelectedIndex = mVideoCodecMode.findSelectedIndexByText(CropConfig.Companion.getInstance().getCodec());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoCodecMode).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoCodecMode.mTitle);
        } else if (v == mVideoQualityLayout) {
            mVideoQuality.mSelectedIndex = mVideoQuality.findSelectedIndexByText(CropConfig.Companion.getInstance().getVideoQuality());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoQuality).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoQuality.mTitle);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CROP_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK) {
            String inputFrame = mFrameRateEdit.getText().toString();
            if (!TextUtils.isEmpty(inputFrame)) {
                try {
                    int fps = Integer.parseInt(inputFrame);
                    CropConfig.Companion.getInstance().setFps(fps);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputGop = mGopEdit.getText().toString();
            if (!TextUtils.isEmpty(inputGop)) {
                try {
                    int gop = Integer.parseInt(inputGop);
                    CropConfig.Companion.getInstance().setGop(gop);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputBitRate = mBitRateEdit.getText().toString();
            if (!TextUtils.isEmpty(inputBitRate)) {
                try {
                    int bitRate = Integer.parseInt(inputBitRate);
                    CropConfig.Companion.getInstance().setBitRate(bitRate * 1000);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }
            List<String> path = AVMatisse.obtainPathResult(data);
            AUICropHelper.startVideoCropForResult(this, path, -1);
        }
    }

    @Override
    public void onSubmit(int requestCode, AVCommonPickerDialog.PickerItem pickerItem) {
        if (requestCode == REQUEST_CODE_RATIO) {
            CropConfig.Companion.getInstance().setRatio((Float) pickerItem.mAttachValue);
            mTvVideoRatio.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_RESOLUTION) {
            CropConfig.Companion.getInstance().setResolution((Integer) pickerItem.mAttachValue);
            mTvVideoResolution.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CODEC_MODE) {
            CropConfig.Companion.getInstance().setCodec((VideoCodecs) pickerItem.mAttachValue);
            mTvVideoCodecMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CROP_MODE) {
            CropConfig.Companion.getInstance().setVideoDisplayMode((VideoDisplayMode) pickerItem.mAttachValue);
            mTvVideoCropMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_QUALITY) {
            CropConfig.Companion.getInstance().setVideoQuality((VideoQuality)pickerItem.mAttachValue);
            mTvVideoQuality.setText(pickerItem.mItemName);
        }
    }
}
