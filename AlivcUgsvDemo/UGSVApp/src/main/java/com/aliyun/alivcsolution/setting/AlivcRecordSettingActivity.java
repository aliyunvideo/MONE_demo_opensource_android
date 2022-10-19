/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution.setting;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;

import android.Manifest;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.alivcsolution.model.AlivcMixBorderParam;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.base.widget.ProgressDialog;
import com.aliyun.svideo.recorder.AUIVideoRecorderEntrance;
import com.aliyun.svideo.recorder.bean.AUIRecorderInputParam;
import com.aliyun.svideosdk.common.struct.common.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.mixrecorder.MixAudioSourceType;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

/**
 * 视频录制模块的 录制参数设置界面 原RecorderSettingTest Created by Administrator on 2017/3/2.
 */

public class AlivcRecordSettingActivity extends AVBaseThemeActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final int BACKGROUND_COLOR = Color.BLUE;
    private static final String BACKGROUND_IMAGE_PATH
        = "/sdcard/Android/data/com.aliyun.apsaravideo/files/mytest.jpg";
    private EditText minDurationEt, maxDurationEt, gopEt;
    private ImageView mBackBtn;
    //视频质量选择按钮
    private Button mQualitySuperBtn, mQualityHighBtn, mQualityNomalBtn, mQualityLowBtn;
    //视频比例选择按钮
    private View mSettingRatioGroup;
    private Button mRecordRatio9P16Btn, mRecordRatio3P4Btn, mRecordRatio1P1Btn;
    //视频分辨率选择按钮
    private Button mRecordResolutionP360Btn, mRecordResolutionP480Btn, mRecordResolutionP540Btn,
            mRecordResolutionP720Btn;
    //合拍音频模式选择按钮
    private Button mDuetOriginal, mDuetRecorded, mDuetMute, mDuetMix;
    //合拍背景色
    private Button mDuetBackgroundNo, mDuetBackgroundColor, mDuetBackgroundImage;
    //合拍背景填充模式
    private Button mMixBgFillMode, mMixBgCropMode, mMixBgStretchMode;
    //视频圆角边框
    private Button mDuetCornerBorderNo, mDuetCornerBorderYes;
    /**
     * 视频编码方式选择按钮
     */
    private Button mEncorderHardwareBtn, mEncorderOpenh264Btn;
    private Button mStartRecord;
    private View mMixSourceGroup, mMixBackgroundGroup, mMixBgScaleModeGroup, mMixVideoCornerBorder, mTranscodeGroup;

    /**
     * 拍摄方式选择按钮 普通，合拍，View录制
     */
    private Button mRecordGeneral, mRecordMix, mRecordView;
    /**
     * 渲染方式选择按钮 faceunity，race
     */
    private Button mRecordFaceUnity, mRecordQueen, mRecordDefault;
    private boolean mHasWaterMark = false;
    private int mResolutionMode, mRatioMode;
    private MixAudioSourceType mMixAudioSourceType;
    private VideoQuality mVideoQuality;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_SOFT_OPENH264;
    private BeautySDKType mRenderingMode = BeautySDKType.QUEEN;
    private Switch mSwitchFlip;
    private Switch mSwitchWatermark;
    private Switch mSwitchAutoClearTemp;
    private int mBackgroundColor;
    private String mBackgroundImage;
    private int mMixBgScaleMode = 0; //displayMode 0：crop 1：fill 2：exact fit
    private boolean isNeedTranscode = false;
    private AsyncTask<Void, Void, Void> copyAssetsTask;
    private AlivcMixBorderParam mMixBorderParam;

    /**
     * 转码开关
     */
    private SwitchCompat mVideoTranscodeSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.aliyun_svideo_activity_recorder_setting);
        copyAssets();
        initView();
    }

    private void recordEnable() {
        mStartRecord.setEnabled(true);
    }

    private void initView() {
        minDurationEt = (EditText)findViewById(R.id.aliyun_min_duration_edit);
        maxDurationEt = (EditText)findViewById(R.id.aliyun_max_duration_edit);
        mSwitchFlip = (Switch)findViewById(R.id.alivc_record_switch_flip);
        mSwitchWatermark = (Switch)findViewById(R.id.alivc_record_switch_watermark);
        mSwitchWatermark.setChecked(mHasWaterMark);
        mSwitchAutoClearTemp = findViewById(R.id.alivc_record_switch_auto_clear);
        gopEt = (EditText)findViewById(R.id.aliyun_gop_edit);

        mStartRecord = findViewById(R.id.aliyun_start_record);
        mStartRecord.setOnClickListener(this);
        mBackBtn = (ImageView)findViewById(R.id.aliyun_back);
        mBackBtn.setOnClickListener(this);

        mQualitySuperBtn = findViewById(R.id.alivc_video_quality_super);
        mQualitySuperBtn.setOnClickListener(this);
        mQualityHighBtn = findViewById(R.id.alivc_video_quality_high);
        mQualityHighBtn.setOnClickListener(this);
        mQualityNomalBtn = findViewById(R.id.alivc_video_quality_normal);
        mQualityNomalBtn.setOnClickListener(this);
        mQualityLowBtn = findViewById(R.id.alivc_video_quality_low);
        mQualityLowBtn.setOnClickListener(this);
        //视频分辨率选择相关按钮
        mRecordResolutionP360Btn = findViewById(R.id.alivc_record_resolution_360p);
        mRecordResolutionP480Btn = findViewById(R.id.alivc_record_resolution_480p);
        mRecordResolutionP540Btn = findViewById(R.id.alivc_record_resolution_540p);
        mRecordResolutionP720Btn = findViewById(R.id.alivc_record_resolution_720p);
        mRecordResolutionP360Btn.setOnClickListener(this);
        mRecordResolutionP480Btn.setOnClickListener(this);
        mRecordResolutionP540Btn.setOnClickListener(this);
        mRecordResolutionP720Btn.setOnClickListener(this);
        //视频编码相关按钮
        mEncorderHardwareBtn = findViewById(R.id.alivc_record_encoder_hardware);
        mEncorderOpenh264Btn = findViewById(R.id.alivc_record_encoder_openh264);
        mEncorderHardwareBtn.setOnClickListener(this);
        mEncorderOpenh264Btn.setOnClickListener(this);

        //视频比例相关按钮
        mSettingRatioGroup = findViewById(R.id.alivc_recorder_setting_ratio_group);
        mRecordRatio9P16Btn = findViewById(R.id.alivc_video_ratio_9_16);
        mRecordRatio3P4Btn = findViewById(R.id.alivc_video_ratio_3_4);
        mRecordRatio1P1Btn = findViewById(R.id.alivc_video_ratio_1_1);
        mRecordRatio9P16Btn.setOnClickListener(this);
        mRecordRatio3P4Btn.setOnClickListener(this);
        mRecordRatio1P1Btn.setOnClickListener(this);

        //拍摄方式
        mRecordGeneral = findViewById(R.id.alivc_video_record_general);
        mRecordMix = findViewById(R.id.alivc_video_record_mix);
        mRecordMix.setOnClickListener(this);
        mRecordGeneral.setOnClickListener(this);
        mRecordView = findViewById(R.id.alivc_video_record_view);
        mRecordView.setOnClickListener(this);

        //渲染方式
        mRecordFaceUnity = findViewById(R.id.alivc_video_record_faceunity);
        mRecordQueen = findViewById(R.id.alivc_video_record_queen);
        mRecordDefault = findViewById(R.id.alivc_video_record_default);
        mRecordFaceUnity.setOnClickListener(this);
        mRecordQueen.setOnClickListener(this);
        mRecordDefault.setOnClickListener(this);

        //合拍音频选择模式
        mMixSourceGroup = findViewById(R.id.alivc_mix_recorder_source_group);
        mDuetOriginal = findViewById(R.id.alivc_video_record_duet_original);
        mDuetRecorded = findViewById(R.id.alivc_video_record_duet_recorded);
        mDuetMute = findViewById(R.id.alivc_video_record_duet_mute);
        mDuetMix = findViewById(R.id.alivc_video_record_duet_mix);
        mDuetOriginal.setOnClickListener(this);
        mDuetRecorded.setOnClickListener(this);
        mDuetMute.setOnClickListener(this);
        mDuetMix.setOnClickListener(this);

        //合拍背景色选择
        mMixBackgroundGroup = findViewById(R.id.alivc_mix_recorder_background_group);
        mDuetBackgroundNo = findViewById(R.id.alivc_video_record_duet_background_no);
        mDuetBackgroundColor = findViewById(R.id.alivc_video_record_duet_background_color);
        mDuetBackgroundImage = findViewById(R.id.alivc_video_record_duet_background_image);
        mDuetBackgroundNo.setOnClickListener(this);
        mDuetBackgroundColor.setOnClickListener(this);
        mDuetBackgroundImage.setOnClickListener(this);

        //合拍视频边框
        mMixVideoCornerBorder = findViewById(R.id.alivc_mix_recorder_round_corner);
        mDuetCornerBorderNo = findViewById(R.id.alivc_video_record_duet_round_border_no);
        mDuetCornerBorderYes = findViewById(R.id.alivc_video_record_duet_round_border_yes);
        mDuetCornerBorderNo.setOnClickListener(this);
        mDuetCornerBorderYes.setOnClickListener(this);

        mMixBgScaleModeGroup = findViewById(R.id.alivc_mix_bg_scale_mode_group);
        mMixBgFillMode = findViewById(R.id.alivc_mix_bg_scale_mode_fill);
        mMixBgCropMode = findViewById(R.id.alivc_mix_bg_scale_mode_crop);
        mMixBgStretchMode = findViewById(R.id.alivc_mix_bg_scale_mode_stretch);
        mMixBgFillMode.setOnClickListener(this);
        mMixBgCropMode.setOnClickListener(this);
        mMixBgStretchMode.setOnClickListener(this);

        mTranscodeGroup = findViewById(R.id.alivc_mix_recorder_transcode_group);
        mVideoTranscodeSwitch = findViewById(R.id.video_transcode_switch);
        mVideoTranscodeSwitch.setOnCheckedChangeListener(this);
        mVideoTranscodeSwitch.setChecked(isNeedTranscode);

        //初始化配置
        onRatioSelected(mRecordRatio9P16Btn);
        onEncoderSelected(mEncorderHardwareBtn);
        onResolutionSelected(mRecordResolutionP720Btn);
        onQualitySelected(mQualityHighBtn);
        onRecordSelected(mRecordGeneral);
        onRenderingSelected(mRecordQueen);
        onDuetAudioModeSelected(mDuetOriginal);
        onDuetBackgroundSelected(mDuetBackgroundNo);
        onDuetVideoBorderUnSetting();
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mVideoTranscodeSwitch) {
            isNeedTranscode = isChecked;
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
            int gop = 30;

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

            String gopValue = gopEt.getText().toString().trim();
            if (!TextUtils.isEmpty(gopValue)) {
                try {
                    gop = Integer.parseInt(gopValue);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }
            AUIRecorderInputParam recordParam = new AUIRecorderInputParam.Builder()
                    .setResolutionMode(mResolutionMode)
                    .setRatioMode(mRatioMode)
                    .setMaxDuration(max)
                    .setMinDuration(min)
                    .setVideoQuality(mVideoQuality)
                    .setGop(gop)
                    .setVideoCodec(mVideoCodec)
                    .setIsUseFlip(mSwitchFlip.isChecked())
                    .setIsAutoClearTemp(mSwitchAutoClearTemp.isChecked())
                    .setVideoRenderingMode(mRenderingMode)
                    .setWaterMark(mSwitchWatermark.isChecked())
                    .setMixAudioSourceType(mMixAudioSourceType)
                    .setMixBackgroundColor(mBackgroundColor)
                    .setMixBackgroundImagePath(mBackgroundImage)
                    .setMixBackgroundImageMode(mMixBgScaleMode)
                    .setMixNeedTranscode(isNeedTranscode)
                    .build();
            AUIVideoRecorderEntrance.startRecord(this, recordParam);
        } else if (v == mBackBtn) {
            finish();
        } else if (v == mEncorderHardwareBtn || v == mEncorderOpenh264Btn) {
            onEncoderSelected(v);
        } else if (v == mQualityHighBtn || v == mQualityLowBtn || v == mQualitySuperBtn
                   || v == mQualityNomalBtn) {
            onQualitySelected(v);
        } else if (v == mRecordRatio1P1Btn || v == mRecordRatio3P4Btn || v == mRecordRatio9P16Btn) {
            onRatioSelected(v);
        } else if (v == mRecordResolutionP360Btn || v == mRecordResolutionP480Btn
                   || mRecordResolutionP540Btn == v
                   || v == mRecordResolutionP720Btn) {
            onResolutionSelected(v);
        } else if ((v == mRecordGeneral)) {
            onRecordSelected(v);
        } else if (v == mRecordMix) {
            onRecordSelected(v);
        } else if (v == mRecordView) {
            onRecordSelected(v);
        } else if ((v == mRecordFaceUnity)) {
            onRenderingSelected(v);
        } else if (v == mRecordQueen) {
            onRenderingSelected(v);
        } else if (v == mRecordDefault){
            onRenderingSelected(v);
        } else if (v == mDuetOriginal || v == mDuetRecorded || v == mDuetMute || v == mDuetMix) {
            onDuetAudioModeSelected(v);
        } else if (v == mDuetBackgroundNo || v == mDuetBackgroundColor
                   || v == mDuetBackgroundImage) {
            onDuetBackgroundSelected(v);
        } else if (v == mMixBgFillMode || v == mMixBgCropMode
                   || v == mMixBgStretchMode) {
            onDuetBackgroundScaleMode(v);
        } else if (v == mDuetCornerBorderNo) {
            onDuetVideoBorderUnSetting();
        } else if (v == mDuetCornerBorderYes) {
            onDuetVideoBorderSetting();
        }
    }

    private void onDuetVideoBorderUnSetting() {
        mMixBorderParam = null;
        mDuetCornerBorderYes.setSelected(false);
        mDuetCornerBorderNo.setSelected(true);
    }

    private void onDuetVideoBorderSetting() {
        mMixBorderParam = new AlivcMixBorderParam.Builder()
        .borderWidth(10)
        .cornerRadius(30)
        .borderColor(Color.WHITE)
        .build();
        mDuetCornerBorderYes.setSelected(true);
        mDuetCornerBorderNo.setSelected(false);
    }
    /**
     * 拍摄方式选择
     */
    private void onRecordSelected(View view) {
        mRecordGeneral.setSelected(false);
        mRecordMix.setSelected(false);
        mRecordView.setSelected(false);
        if (view == mRecordView) {
            mStartRecord.setText(getResources().getText(R.string.alivc_recorder_setting_start_record));
            mRecordView.setSelected(true);
            mMixBackgroundGroup.setVisibility(View.VISIBLE);
            mMixSourceGroup.setVisibility(View.GONE);
            mTranscodeGroup.setVisibility(View.GONE);
            mMixVideoCornerBorder.setVisibility(View.VISIBLE);
            if (mDuetBackgroundImage.isSelected()) {
                mMixBgScaleModeGroup.setVisibility(View.VISIBLE);
                onDuetBackgroundScaleMode(null);
            } else {
                mMixBgScaleModeGroup.setVisibility(View.GONE);
            }

            mSettingRatioGroup.setVisibility(View.GONE);
        } else if (view == mRecordMix) {
            mStartRecord.setText(getResources().getText(R.string.aliyun_start_mix_record));
            mRecordMix.setSelected(true);
            mMixBackgroundGroup.setVisibility(View.VISIBLE);
            mMixSourceGroup.setVisibility(View.VISIBLE);
            mTranscodeGroup.setVisibility(View.VISIBLE);
            mMixVideoCornerBorder.setVisibility(View.VISIBLE);
            if (mDuetBackgroundImage.isSelected()) {
                mMixBgScaleModeGroup.setVisibility(View.VISIBLE);
                onDuetBackgroundScaleMode(null);
            } else {
                mMixBgScaleModeGroup.setVisibility(View.GONE);
            }

            mSettingRatioGroup.setVisibility(View.VISIBLE);
        } else {
            mStartRecord.setText(
                getResources().getText(R.string.alivc_recorder_setting_start_record));
            mRecordGeneral.setSelected(true);
            mMixBackgroundGroup.setVisibility(View.GONE);
            mMixSourceGroup.setVisibility(View.GONE);
            mTranscodeGroup.setVisibility(View.GONE);
            mMixBgScaleModeGroup.setVisibility(View.GONE);
            mMixVideoCornerBorder.setVisibility(View.GONE);

            mSettingRatioGroup.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 渲染方式选择
     */
    private void onRenderingSelected(View view) {
        mRecordFaceUnity.setSelected(false);
        mRecordQueen.setSelected(false);
        if (view == mRecordFaceUnity) {
            mRecordFaceUnity.setSelected(true);
            mRecordQueen.setSelected(false);
            mRecordDefault.setSelected(false);
            mRenderingMode = BeautySDKType.FACEUNITY;
        } else if (view == mRecordDefault){
            mRecordFaceUnity.setSelected(false);
            mRecordQueen.setSelected(false);
            mRecordDefault.setSelected(true);
            mRenderingMode = BeautySDKType.DEFAULT;
        }else {
            mRecordFaceUnity.setSelected(false);
            mRecordQueen.setSelected(true);
            mRecordDefault.setSelected(false);
            mRenderingMode = BeautySDKType.QUEEN;
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
        mRecordRatio9P16Btn.setSelected(false);
        mRecordRatio3P4Btn.setSelected(false);
        mRecordRatio1P1Btn.setSelected(false);
        if (view == mRecordRatio1P1Btn) {
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_1_1;
            mRecordRatio1P1Btn.setSelected(true);
        } else if (view == mRecordRatio9P16Btn) {
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_9_16;
            mRecordRatio9P16Btn.setSelected(true);
        } else {
            mRecordRatio3P4Btn.setSelected(true);
            mRatioMode = AliyunSnapVideoParam.RATIO_MODE_3_4;
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

    /**
     * 视频分辨率选择
     *
     * @param view
     */
    private void onResolutionSelected(View view) {
        mRecordResolutionP360Btn.setSelected(false);
        mRecordResolutionP480Btn.setSelected(false);
        mRecordResolutionP540Btn.setSelected(false);
        mRecordResolutionP720Btn.setSelected(false);
        if (view == mRecordResolutionP360Btn) {
            mRecordResolutionP360Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_360P;
        } else if (view == mRecordResolutionP480Btn) {
            mRecordResolutionP480Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_480P;

        } else if (view == mRecordResolutionP540Btn) {
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_540P;
            mRecordResolutionP540Btn.setSelected(true);

        } else {
            mRecordResolutionP720Btn.setSelected(true);
            mResolutionMode = AliyunSnapVideoParam.RESOLUTION_720P;

        }

    }

    private void onDuetAudioModeSelected(View view) {
        mDuetOriginal.setSelected(false);
        mDuetRecorded.setSelected(false);
        mDuetMute.setSelected(false);
        mDuetMix.setSelected(false);
        if (view == mDuetOriginal) {
            mDuetOriginal.setSelected(true);
            mMixAudioSourceType = MixAudioSourceType.Original;
        } else if (view == mDuetRecorded) {
            mDuetRecorded.setSelected(true);
            mMixAudioSourceType = MixAudioSourceType.Recorded;
        } else if (view == mDuetMute) {
            mDuetMute.setSelected(true);
            mMixAudioSourceType = MixAudioSourceType.Mute;
        } else if (view == mDuetMix) {
            mDuetMix.setSelected(true);
            mMixAudioSourceType = MixAudioSourceType.MIX;
        } else {
            mDuetOriginal.setSelected(true);
            mMixAudioSourceType = MixAudioSourceType.Original;
        }
    }

    private void onDuetBackgroundSelected(View view) {
        if (view == mDuetBackgroundNo) {
            mDuetBackgroundNo.setSelected(true);
            mDuetBackgroundColor.setSelected(false);
            mDuetBackgroundImage.setSelected(false);
            mBackgroundImage = "";
            mBackgroundColor = Color.BLACK;
            mMixBgScaleModeGroup.setVisibility(View.GONE);
        } else if (view == mDuetBackgroundColor) {
            mDuetBackgroundNo.setSelected(false);
            mDuetBackgroundColor.setSelected(true);
            mBackgroundColor = Color.RED;
        } else if (view == mDuetBackgroundImage) {
            mDuetBackgroundNo.setSelected(false);
            mDuetBackgroundImage.setSelected(true);
            mBackgroundImage = BACKGROUND_IMAGE_PATH;
            mMixBgScaleModeGroup.setVisibility(View.VISIBLE);
            onDuetBackgroundScaleMode(null);
        } else {
            mDuetBackgroundNo.setSelected(true);
            mDuetBackgroundColor.setSelected(false);
            mDuetBackgroundImage.setSelected(false);
            mBackgroundImage = "";
            mBackgroundColor = Color.BLACK;

        }
    }

    //displayMode 0：crop 1：fill 2：exact fit
    private void onDuetBackgroundScaleMode(View view) {
        if (view == mMixBgFillMode) {
            mMixBgFillMode.setSelected(true);
            mMixBgCropMode.setSelected(false);
            mMixBgStretchMode.setSelected(false);
            mMixBgScaleMode = 1;
        } else if (view == mMixBgCropMode) {
            mMixBgFillMode.setSelected(false);
            mMixBgCropMode.setSelected(true);
            mMixBgStretchMode.setSelected(false);
            mMixBgScaleMode = 0;
        } else if (view == mMixBgStretchMode) {
            mMixBgFillMode.setSelected(false);
            mMixBgCropMode.setSelected(false);
            mMixBgStretchMode.setSelected(true);
            mMixBgScaleMode = 2;
        } else {
            mMixBgScaleMode = 1;
            mMixBgFillMode.setSelected(true);
            mMixBgCropMode.setSelected(false);
            mMixBgStretchMode.setSelected(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
