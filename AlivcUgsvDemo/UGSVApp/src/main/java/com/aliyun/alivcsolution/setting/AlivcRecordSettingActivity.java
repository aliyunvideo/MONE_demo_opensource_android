/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution.setting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.aliyun.aio.avbaseui.avdialog.AVCommonPickerDialog;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.base.widget.ProgressDialog;
import com.aliyun.svideo.crop.CropConfig;
import com.aliyun.svideo.editor.EditorConfig;
import com.aliyun.svideo.recorder.AUIVideoRecorderEntrance;
import com.aliyun.svideo.recorder.RecorderConfig;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

/**
 * 视频录制模块的 录制参数设置界面 原RecorderSettingTest Created by Administrator on 2017/3/2.
 */

public class AlivcRecordSettingActivity extends AVBaseThemeActivity implements View.OnClickListener, AVCommonPickerDialog.OnPickListener {
    private static final int BACKGROUND_COLOR = Color.BLUE;
    private static final String BACKGROUND_IMAGE_PATH
        = "/sdcard/Android/data/com.aliyun.apsaravideo/files/mytest.jpg";
    private EditText minDurationEt, maxDurationEt;
    private ImageView mBackBtn;

    private Button mStartRecord;
    private Switch mSwitchFlip;
    private Switch mSwitchAutoClearTemp;
    private Switch mSwitchJumpEdit;
    private boolean isNeedTranscode = false;
    private AsyncTask<Void, Void, Void> copyAssetsTask;

    /**
     * 转码开关
     */
    private SwitchCompat mVideoTranscodeSwitch;

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

    //美颜类型
    private RelativeLayout mBeautyTypeLayout;
    private TextView mTvBeautyType;
    private AVCommonPickerDialog.ArgsSelector mBeautyTypeSelector;
    private static final int REQUEST_CODE_BEAUTY = 0x16;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.aliyun_svideo_activity_recorder_setting);
        copyAssets();
        initView();
        initData();
    }

    private void recordEnable() {
        mStartRecord.setEnabled(true);
    }

    private void initView() {
        minDurationEt = (EditText)findViewById(R.id.ed_min_duration);
        maxDurationEt = (EditText)findViewById(R.id.ed_max_duration);
        mSwitchFlip = (Switch)findViewById(R.id.ugsv_video_flip);
        mSwitchAutoClearTemp = findViewById(R.id.ugsv_video_clear_cache);
        mSwitchJumpEdit = findViewById(R.id.ugsv_edit_after_record);

        mStartRecord = findViewById(R.id.aliyun_start_record);
        mStartRecord.setOnClickListener(this);

        mBackBtn = (ImageView)findViewById(R.id.aliyun_back);
        mBackBtn.setOnClickListener(this);

        //视频质量按钮初始化
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

        mBeautyTypeLayout = findViewById(R.id.beauty_sdk_type);
        mBeautyTypeLayout.setOnClickListener(this);
        mTvBeautyType = findViewById(R.id.tv_beauty_sdk_type);

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
        RecorderConfig.Companion.getInstance().reset();

        mVideoRatio = new AVCommonPickerDialog.ArgsSelector();
        mVideoRatio.mRequestCode = REQUEST_CODE_RATIO;
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_9_16), EditorConfig.RATIO_MODE_9_16));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_3_4), EditorConfig.RATIO_MODE_3_4));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_1_1), EditorConfig.RATIO_MODE_1_1));
        mVideoRatio.mSelectedIndex = 0;
        mVideoRatio.mTitle = getResources().getString(R.string.alivc_crop_setting_ratio);

        mVideoResolution = new AVCommonPickerDialog.ArgsSelector();
        mVideoResolution.mRequestCode = REQUEST_CODE_RESOLUTION;
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_1080p), EditorConfig.RESOLUTION_1080P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_720p), EditorConfig.RESOLUTION_720P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_540p), EditorConfig.RESOLUTION_540P));
        mVideoResolution.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_resolution_360p), EditorConfig.RESOLUTION_360P));
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

        mBeautyTypeSelector = new AVCommonPickerDialog.ArgsSelector();
        mBeautyTypeSelector.mRequestCode = REQUEST_CODE_BEAUTY;
        mBeautyTypeSelector.mItemList.add(new AVCommonPickerDialog.PickerItem(
                getResources().getString(R.string.alivc_setting_rendering_queen),
                BeautySDKType.QUEEN));
        mBeautyTypeSelector.mItemList.add(new AVCommonPickerDialog.PickerItem(
                getResources().getString(R.string.alivc_setting_rendering_default),
                BeautySDKType.DEFAULT));
        mBeautyTypeSelector.mSelectedIndex = 0;
        mBeautyTypeSelector.mTitle =
                getResources().getString(R.string.alivc_recorder_setting_rendering_mode);
    }

    private void copyAssets() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                copyAssetsTask = new CopyAssetsTask(AlivcRecordSettingActivity.this)
                .executeOnExecutor(
                    AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 700);

    }

    @Override
    public void onSubmit(int requestCode, AVCommonPickerDialog.PickerItem pickerItem) {
        if (requestCode == REQUEST_CODE_RATIO) {
            RecorderConfig.Companion.getInstance().setRatio((Float) pickerItem.mAttachValue);
            mTvVideoRatio.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_RESOLUTION) {
            RecorderConfig.Companion.getInstance().setResolution((Integer) pickerItem.mAttachValue);
            mTvVideoResolution.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CODEC_MODE) {
            RecorderConfig.Companion.getInstance().setCodec((VideoCodecs) pickerItem.mAttachValue);
            mTvVideoCodecMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CROP_MODE) {
            RecorderConfig.Companion.getInstance().setVideoDisplayMode((VideoDisplayMode) pickerItem.mAttachValue);
            mTvVideoCropMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_QUALITY) {
            RecorderConfig.Companion.getInstance().setVideoQuality((VideoQuality)pickerItem.mAttachValue);
            mTvVideoQuality.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_BEAUTY) {
            RecorderConfig.Companion.getInstance().setBeautyType((BeautySDKType) pickerItem.mAttachValue);
            mTvBeautyType.setText(pickerItem.mItemName);
        }
    }

    public class CopyAssetsTask extends AsyncTask<Void, Void, Void> {

        private WeakReference<AlivcRecordSettingActivity> weakReference;
        ProgressDialog progressBar;

        CopyAssetsTask(AlivcRecordSettingActivity activity) {

            weakReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AlivcRecordSettingActivity activity = weakReference.get();
            if (activity != null && !activity.isFinishing()) {
                progressBar = new ProgressDialog(activity, R.style.NoBackgroundDlgStyle);
                progressBar.setCanceledOnTouchOutside(false);
                progressBar.setCancelable(false);
                progressBar.setProgressStyle(android.app.ProgressDialog.STYLE_SPINNER);
                progressBar.show();
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            AlivcRecordSettingActivity activity = weakReference.get();
            if (activity != null) {
                try {
                    OutputStream myOutput = new FileOutputStream(BACKGROUND_IMAGE_PATH);
                    InputStream myInput = activity.getAssets().open("test.jpg");
                    byte[] buffer = new byte[1024 * 8];
                    int length = myInput.read(buffer);
                    while (length > 0) {
                        myOutput.write(buffer, 0, length);
                        length = myInput.read(buffer);
                    }

                    myOutput.flush();
                    myInput.close();
                    myOutput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            AlivcRecordSettingActivity activity = weakReference.get();
            if (activity != null && !activity.isFinishing()) {
                progressBar.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mStartRecord) {
            if (FastClickUtil.isFastClick()) {
                return;
            }
            int min = 2000;
            int max = 15000;
            int gop = 250;
            int fps = 30;
            int bitrate = EditorConfig.DEFAULT_BITRATE;

            String minDuration = minDurationEt.getText().toString().trim();
            if (!TextUtils.isEmpty(minDuration)) {
                try {
                    min = Integer.parseInt(minDuration) * 1000;
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }
            if (min <= 0) {
                Toast.makeText(this,
                               getResources().getString(R.string.aliyun_min_record_duration_more_than),
                               Toast.LENGTH_SHORT).show();
                return;
            }
            if (min >= 300000) {
                Toast.makeText(this,
                               getResources().getString(R.string.aliyun_min_record_duration_less_than),
                               Toast.LENGTH_SHORT).show();
                return;
            }
            String maxDuration = maxDurationEt.getText().toString().trim();
            if (!TextUtils.isEmpty(maxDuration)) {
                try {
                    max = Integer.parseInt(maxDuration) * 1000;
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }
            if (max <= 0) {
                Toast.makeText(this,
                               getResources().getString(R.string.aliyun_max_record_duration_more_than),
                               Toast.LENGTH_SHORT).show();
                return;
            }
            if (max > 300000) {
                Toast.makeText(this,
                               getResources().getString(R.string.aliyun_max_record_duration_less_than),
                               Toast.LENGTH_SHORT).show();
                return;
            }

            if (min >= max) {
                Toast.makeText(this,
                               getResources().getString(R.string.alivc_recorder_setting_tip_duration_error),
                               Toast.LENGTH_SHORT).show();
                return;
            }

            String gopValue = mGopEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(gopValue)) {
                try {
                    gop = Integer.parseInt(gopValue);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String fpsValue = mFrameRateEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(fpsValue)) {
                try {
                    fps = Integer.parseInt(fpsValue);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String bitrateValue = mBitRateEdit.getText().toString().trim();
            if (!TextUtils.isEmpty(bitrateValue)) {
                try {
                    bitrate = Integer.parseInt(bitrateValue);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            RecorderConfig.Companion.getInstance().setMinDuration(min);
            RecorderConfig.Companion.getInstance().setMaxDuration(max);
            RecorderConfig.Companion.getInstance().setGop(gop);
            RecorderConfig.Companion.getInstance().setFps(fps);
            if (bitrate != -1) {
                RecorderConfig.Companion.getInstance().setBitRate(bitrate);
            }
            RecorderConfig.Companion.getInstance().setVideoFlip(mSwitchFlip.isChecked());
            RecorderConfig.Companion.getInstance().setNeedEdit(mSwitchJumpEdit.isChecked());
            RecorderConfig.Companion.getInstance().setClearCache(mSwitchAutoClearTemp.isChecked());

            AUIVideoRecorderEntrance.startRecord(this);
        } else if (v == mBackBtn) {
            finish();
        } if (v == mVideoRatioLayout) {
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
        } else if (v == mBeautyTypeLayout) {
            mBeautyTypeSelector.mSelectedIndex = mBeautyTypeSelector.findSelectedIndexByText(
                    RecorderConfig.Companion.getInstance().getBeautyType());
            mPickerDialog = new AVCommonPickerDialog
                    .Builder(mBeautyTypeSelector)
                    .setListener(this)
                    .build();
            mPickerDialog.show(getSupportFragmentManager(), mBeautyTypeSelector.mTitle);
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
