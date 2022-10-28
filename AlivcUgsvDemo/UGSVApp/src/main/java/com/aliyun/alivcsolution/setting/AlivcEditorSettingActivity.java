/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.aliyun.aio.avbaseui.avdialog.AVCommonPickerDialog;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.alivcsolution.R;
import com.aliyun.svideo.base.utils.FastClickUtil;
import com.aliyun.svideo.editor.AUIVideoEditor;
import com.aliyun.svideo.editor.EditorConfig;
import com.aliyun.ugsv.common.utils.PermissionUtils;
import com.aliyun.ugsv.common.utils.ToastUtils;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.zhihu.matisse.AVMatisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

/**
 * 视频编辑模块, 参数设置界面
 */
public class AlivcEditorSettingActivity extends AVBaseThemeActivity implements View.OnClickListener, AVCommonPickerDialog.OnPickListener {

    private static final int EDITOR_REQUEST_CODE_CHOOSE = 102;
    private static final int EDITOR_PERMISSION_REQUEST_CODE = 1001;
    /**
     *  判断是编辑模块进入还是通过社区模块的编辑功能进入
     */
    private static final String INTENT_PARAM_KEY_ENTRANCE = "entrance";

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

    private Button mStartImport;

    private Switch mPublish;

    private AVCommonPickerDialog mPickerDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aliyun_svideo_editor_setting_layout);
        initView();
        initData();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);

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

        mVideoFrameRateLayout= findViewById(R.id.video_frame_rate_layout);
        mVideoFrameRateLayout.setOnClickListener(this);
        mFrameRateEdit = findViewById(R.id.ed_video_frame_rate);

        mVideoGopLayout = findViewById(R.id.video_gop_layout);
        mVideoGopLayout.setOnClickListener(this);
        mGopEdit = (EditText) findViewById(R.id.ed_video_gop);

        mVideoCodeRateLayout = findViewById(R.id.video_code_rate_layout);
        mVideoCodeRateLayout.setOnClickListener(this);
        mBitRateEdit = findViewById(R.id.ed_video_code_rate);

        mStartImport = (Button) findViewById(R.id.start_import);
        mStartImport.setOnClickListener(this);

        mPublish = findViewById(R.id.ugsv_publish_after_compose);
    }

    private void initData() {
        EditorConfig.Companion.getInstance().reset();

        mVideoRatio = new AVCommonPickerDialog.ArgsSelector();
        mVideoRatio.mRequestCode = REQUEST_CODE_RATIO;
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_original), EditorConfig.RATIO_MODE_ORIGIN));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_9_16), EditorConfig.RATIO_MODE_9_16));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_3_4), EditorConfig.RATIO_MODE_3_4));
        mVideoRatio.mItemList.add(new AVCommonPickerDialog.PickerItem(getResources().getString(R.string.alivc_crop_setting_ratio_1_1), EditorConfig.RATIO_MODE_1_1));
        mVideoRatio.mSelectedIndex = 3;
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
    }

    @Override
    public void onClick(View v) {
        if (v == mStartImport) {
            if (FastClickUtil.isFastClick()) {
                return;
            }

            String inputFrame = mFrameRateEdit.getText().toString();
            if (!TextUtils.isEmpty(inputFrame)) {
                try {
                    int fps = Integer.parseInt(inputFrame);
                    EditorConfig.Companion.getInstance().setFps(fps);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputGop = mGopEdit.getText().toString();
            if (!TextUtils.isEmpty(inputGop)) {
                try {
                    int gop = Integer.parseInt(inputGop);
                    EditorConfig.Companion.getInstance().setGop(gop);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputBitRate = mBitRateEdit.getText().toString();
            if (!TextUtils.isEmpty(inputBitRate)) {
                try {
                    int bitRate = Integer.parseInt(inputBitRate);
                    EditorConfig.Companion.getInstance().setBitRate(bitRate * 1000);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            EditorConfig.Companion.getInstance().setPublish(mPublish.isChecked());

            if (!PermissionUtils.checkPermissionsGroup(this, PermissionUtils.PERMISSION_STORAGE)) {
                ToastUtils.show(this, PermissionUtils.NO_PERMISSION_TIP[4]);
                PermissionUtils.requestPermissions(this, PermissionUtils.PERMISSION_STORAGE, EDITOR_PERMISSION_REQUEST_CODE);
                return;
            }
            AVMatisse.from(this)
                .choose(MimeType.ofAll(), false)
                .countable(true)
                .maxSelectable(20)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                //                    .showSingleMediaType(true)
                .imageEngine(new GlideEngine())
                .showPreview(false)
                .forResult(EDITOR_REQUEST_CODE_CHOOSE);


        } else if (v == back) {
            finish();
        } else if (v == mVideoRatioLayout) {
            mVideoRatio.mSelectedIndex = mVideoRatio.findSelectedIndexByText(EditorConfig.Companion.getInstance().getRatio());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoRatio).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoRatio.mTitle);
        } else if (v == mVideoResolutionLayout) {
            mVideoResolution.mSelectedIndex = mVideoResolution.findSelectedIndexByText(EditorConfig.Companion.getInstance().getResolution());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoResolution).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoResolution.mTitle);
        } else if (v == mVideoCropModeLayout) {
            mVideoCropMode.mSelectedIndex = mVideoCropMode.findSelectedIndexByText(EditorConfig.Companion.getInstance().getVideoDisplayMode());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoCropMode).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoCropMode.mTitle);
        } else if (v == mVideoCodecModeLayout) {
            mVideoCodecMode.mSelectedIndex = mVideoCodecMode.findSelectedIndexByText(EditorConfig.Companion.getInstance().getCodec());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoCodecMode).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoCodecMode.mTitle);
        } else if (v == mVideoQualityLayout) {
            mVideoQuality.mSelectedIndex = mVideoQuality.findSelectedIndexByText(EditorConfig.Companion.getInstance().getVideoQuality());
            mPickerDialog = new AVCommonPickerDialog.Builder(mVideoQuality).setListener(this).build();
            mPickerDialog.show(getSupportFragmentManager(), mVideoQuality.mTitle);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == EDITOR_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了
                AVMatisse.from(this)
                    .choose( MimeType.ofAll() , false)
                    .countable(true)
                    .maxSelectable( 20)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    //                    .showSingleMediaType(true)
                    .imageEngine(new GlideEngine())
                    .showPreview(false)
                    .forResult(EDITOR_REQUEST_CODE_CHOOSE);
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                showPermissionDialog();
            }
        }
    }
    //系统授权设置的弹框
    AlertDialog openAppDetDialog = null;
    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.ugc_app_name) + getResources().getString(R.string.alivc_recorder_record_dialog_permission_remind));
        builder.setPositiveButton(getResources().getString(R.string.alivc_record_request_permission_positive_btn_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                startActivity(intent);
            }
        });
        builder.setCancelable(false);
        builder.setNegativeButton(getResources().getString(R.string.alivc_recorder_record_dialog_not_setting), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });
        if (null == openAppDetDialog) {
            openAppDetDialog = builder.create();
        }
        if (null != openAppDetDialog && !openAppDetDialog.isShowing()) {
            openAppDetDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (EDITOR_REQUEST_CODE_CHOOSE == requestCode && resultCode == RESULT_OK) {
            AUIVideoEditor.startEditor(this, AVMatisse.obtainPathResult(data));
        }
    }

    @Override
    public void onSubmit(int requestCode, AVCommonPickerDialog.PickerItem pickerItem) {
        if (requestCode == REQUEST_CODE_RATIO) {
            EditorConfig.Companion.getInstance().setRatio((Float) pickerItem.mAttachValue);
            mTvVideoRatio.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_RESOLUTION) {
            EditorConfig.Companion.getInstance().setResolution((Integer) pickerItem.mAttachValue);
            mTvVideoResolution.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CODEC_MODE) {
            EditorConfig.Companion.getInstance().setCodec((VideoCodecs) pickerItem.mAttachValue);
            mTvVideoCodecMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_CROP_MODE) {
            EditorConfig.Companion.getInstance().setVideoDisplayMode((VideoDisplayMode) pickerItem.mAttachValue);
            mTvVideoCropMode.setText(pickerItem.mItemName);
        } else if (requestCode == REQUEST_CODE_QUALITY) {
            EditorConfig.Companion.getInstance().setVideoQuality((VideoQuality)pickerItem.mAttachValue);
            mTvVideoQuality.setText(pickerItem.mItemName);
        }
    }
}
