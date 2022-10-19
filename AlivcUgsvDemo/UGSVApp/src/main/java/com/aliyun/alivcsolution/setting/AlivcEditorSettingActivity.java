/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution.setting;

import android.app.Activity;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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
public class AlivcEditorSettingActivity extends Activity implements View.OnClickListener {

    private static final int EDITOR_REQUEST_CODE_CHOOSE = 102;
    private static final int EDITOR_PERMISSION_REQUEST_CODE = 1001;
    /**
     *  判断是编辑模块进入还是通过社区模块的编辑功能进入
     */
    private static final String INTENT_PARAM_KEY_ENTRANCE = "entrance";
    /**
     * 默认帧率
     */
    private static final int DEFAULT_FRAME_RATE = 30;
    private static final int DEFAULT_GOP = 250;


    private ImageView back;

    /**
     * 视频比例 9:16,3:4,1:1,原比例
     */
    private float mRatio = EditorConfig.RATIO_MODE_ORIGIN;
    /**
     * 分辨率设置 360P、480P、540P、720P
     */

    private int mResolutionMode = EditorConfig.RESOLUTION_1080P;
    /**
     * 视频质量 SSD:HD:SD:LD
     */
    private VideoQuality videoQuality;

    /**
     * 视频/图片的显示风格 CROP（裁剪）、FILL（填充）
     */
    private VideoDisplayMode scaleMode = VideoDisplayMode.SCALE;

    /**
     * 视频编码模式，默认硬编
     */
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;

    //视频质量选择按钮
    private Button mQualitySuperBtn, mQualityHighBtn, mQualityNormalBtn, mQualityLowBtn;
    //视频比例选择按钮
    private Button mRatio9P16Btn, mRatio3P4Btn, mRatio1P1Btn, mRatioOriginalBtn;
    //视频分辨率选择按钮
    private Button mResolutionP360Btn, mResolutionP540Btn, mResolutionP1080Btn,
            mResolutionP720Btn;

    /**
     * 视频编码方式选择按钮
     */
    private Button mEncorderHardwareBtn, mEncorderOpenh264Btn;
    /**
     * 帧率 default {@link #DEFAULT_FRAME_RATE}
     */
    private EditText mFrameRateEdit;

    /**
     * 关键帧间隔 default {@link #DEFAULT_GOP}
     */
    private EditText mGopEdit;

    private Button mRadioFill;//填充
    private Button mRadioCrop;//裁剪
    private Button mStartImport;

    /**
     *  判断是编辑模块进入还是通过社区模块的编辑功能进入
     *  svideo: 短视频
     *  community: 社区
     */
    private String entrance;
    private int mFrameRate;
    private int mGop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.aliyun_svideo_editor_setting_layout);
        entrance = getIntent().getStringExtra(INTENT_PARAM_KEY_ENTRANCE);
        initView();
    }

    private void initView() {
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(this);
        mFrameRateEdit = (EditText) findViewById(R.id.frame_rate_edit);
        mGopEdit = (EditText) findViewById(R.id.gop_edit);
        //视频质量按钮初始化
        mQualitySuperBtn = (Button) findViewById(R.id.alivc_video_quality_super);
        mQualityHighBtn = (Button) findViewById(R.id.alivc_video_quality_high);
        mQualityNormalBtn = (Button) findViewById(R.id.alivc_video_quality_normal);
        mQualityLowBtn = (Button) findViewById(R.id.alivc_video_quality_low);
        mQualitySuperBtn.setOnClickListener(this);
        mQualityHighBtn.setOnClickListener(this);
        mQualityNormalBtn.setOnClickListener(this);
        mQualityLowBtn.setOnClickListener(this);

        mRatio9P16Btn = (Button) findViewById(R.id.alivc_video_ratio_9_16);
        mRatio3P4Btn = (Button) findViewById(R.id.alivc_video_ratio_3_4);
        mRatio1P1Btn = (Button) findViewById(R.id.alivc_video_ratio_1_1);
        mRatioOriginalBtn = (Button) findViewById(R.id.alivc_video_ratio_original);
        mRatio9P16Btn.setOnClickListener(this);
        mRatio3P4Btn.setOnClickListener(this);
        mRatio1P1Btn.setOnClickListener(this);
        mRatioOriginalBtn.setOnClickListener(this);


        mResolutionP360Btn = (Button) findViewById(R.id.alivc_resolution_360p);
        mResolutionP540Btn = (Button) findViewById(R.id.alivc_resolution_540p);
        mResolutionP1080Btn = (Button) findViewById(R.id.alivc_resolution_1080p);
        mResolutionP720Btn = (Button) findViewById(R.id.alivc_resolution_720p);
        mResolutionP360Btn.setOnClickListener(this);
        mResolutionP540Btn.setOnClickListener(this);
        mResolutionP1080Btn.setOnClickListener(this);
        mResolutionP720Btn.setOnClickListener(this);

        //视频编码相关按钮
        mEncorderHardwareBtn = findViewById(R.id.alivc_edit_encoder_hardware);
        mEncorderOpenh264Btn = findViewById(R.id.alivc_edit_encoder_openh264);
        mEncorderHardwareBtn.setOnClickListener(this);
        mEncorderOpenh264Btn.setOnClickListener(this);

        mRadioFill = (Button) findViewById(R.id.radio_fill);
        mRadioCrop = (Button) findViewById(R.id.radio_crop);
        mRadioFill.setOnClickListener(this);
        mRadioCrop.setOnClickListener(this);

        mStartImport = (Button) findViewById(R.id.start_import);
        mStartImport.setOnClickListener(this);

        //初始化配置
        onRatioSelected(mRatio9P16Btn);
        onResolutionSelected(mResolutionP720Btn);
        onQualitySelected(mQualityHighBtn);
        onScaleModeSelected(mRadioFill);
        onEncoderSelected(mEncorderHardwareBtn);

    }



    @Override
    public void onClick(View v) {
        if (v == mStartImport) {
            if (FastClickUtil.isFastClick()) {
                return;
            }

            String inputFrame = mFrameRateEdit.getText().toString();
            mFrameRate = DEFAULT_FRAME_RATE;
            if (!TextUtils.isEmpty(inputFrame)) {
                try {
                    mFrameRate = Integer.parseInt(inputFrame);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

            String inputGop = mGopEdit.getText().toString();
            mGop = DEFAULT_GOP;
            if (!TextUtils.isEmpty(inputGop)) {
                try {
                    mGop = Integer.parseInt(inputGop);
                } catch (Exception e) {
                    Log.e("ERROR", "input error");
                }
            }

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
        } else if (v == mRadioCrop || v == mRadioFill ) {
            onScaleModeSelected(v);
        } else if (v == mQualityHighBtn || v == mQualityLowBtn || v == mQualitySuperBtn || v == mQualityNormalBtn) {
            onQualitySelected(v);
        } else if (v == mRatio1P1Btn || v == mRatio3P4Btn || v == mRatio9P16Btn || v == mRatioOriginalBtn) {
            onRatioSelected(v);
        } else if (v == mResolutionP360Btn || v == mResolutionP540Btn || mResolutionP1080Btn == v
                   || v == mResolutionP720Btn) {
            onResolutionSelected(v);
        } else if (v == mEncorderHardwareBtn || v == mEncorderOpenh264Btn) {
            onEncoderSelected(v);
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
        mQualityNormalBtn.setSelected(false);
        mQualityLowBtn.setSelected(false);
        if (view == mQualitySuperBtn) {
            videoQuality = VideoQuality.SSD;
            mQualitySuperBtn.setSelected(true);
        } else if (view == mQualityHighBtn) {
            mQualityHighBtn.setSelected(true);
            videoQuality = VideoQuality.HD;
        } else if (view == mQualityNormalBtn) {
            mQualityNormalBtn.setSelected(true);
            videoQuality = VideoQuality.SD;
        } else {
            mQualityLowBtn.setSelected(true);
            videoQuality = VideoQuality.LD;
        }
    }

    /**
     * 视频比例选择
     *
     * @param view
     */
    private void onRatioSelected(View view) {
        mRatio9P16Btn.setSelected(false);
        mRatio3P4Btn.setSelected(false);
        mRatio1P1Btn.setSelected(false);
        mRatioOriginalBtn.setSelected(false);

        view.setSelected(true);
        if (view == mRatio1P1Btn) {
            mRatio = EditorConfig.RATIO_MODE_1_1;
        } else if (view == mRatio9P16Btn) {
            mRatio = EditorConfig.RATIO_MODE_9_16;
        } else if (view == mRatio3P4Btn) {
            mRatio = EditorConfig.RATIO_MODE_3_4;
        } else if (view == mRatioOriginalBtn) {
            mRatio = EditorConfig.RATIO_MODE_ORIGIN;
        } else {
            mRatio = EditorConfig.RATIO_MODE_9_16;
        }
    }

    /**
     * 选择视频分辨率
     * @param view 选择的分辨率button
     */
    private void onResolutionSelected(View view) {
        mResolutionP360Btn.setSelected(false);
        mResolutionP540Btn.setSelected(false);
        mResolutionP1080Btn.setSelected(false);
        mResolutionP720Btn.setSelected(false);
        view.setSelected(true);
        if (view == mResolutionP360Btn) {
            mResolutionMode = EditorConfig.RESOLUTION_360P;
        } else if (view == mResolutionP540Btn) {
            mResolutionMode = EditorConfig.RESOLUTION_540P;
        } else if (view == mResolutionP720Btn) {
            mResolutionMode = EditorConfig.RESOLUTION_720P;
        } else {
            mResolutionMode = EditorConfig.RESOLUTION_1080P;
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
     * 填充模式选择
     * @param view 选择的button
     */
    private void onScaleModeSelected(View view) {
        mRadioFill.setSelected(false);
        mRadioCrop.setSelected(false);
        view.setSelected(true);
        if (view == mRadioFill) {
            scaleMode = VideoDisplayMode.FILL;
        } else {
            scaleMode = VideoDisplayMode.SCALE;
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
            EditorConfig config = new EditorConfig();
            config.setRatio(mRatio);
            config.setVideoDisplayMode(scaleMode);
            config.setVideoQuality(videoQuality);
            config.setResolution(mResolutionMode);
            config.setFps(mFrameRate);
            config.setGop(mGop);
            config.setCodec(mVideoCodec);
            AUIVideoEditor.startEditor(this, AVMatisse.obtainPathResult(data), config);
        }
    }
}
