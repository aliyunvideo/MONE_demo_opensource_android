
package com.alivc.live.pusher.demo;

import static com.alivc.live.pusher.AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE;
import static com.alivc.live.pusher.AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO;
import static com.alivc.live.pusher.AlivcEncodeType.Encode_TYPE_H264;
import static com.alivc.live.pusher.AlivcEncodeType.Encode_TYPE_H265;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_10;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_12;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_15;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_20;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_25;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_30;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_8;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_AUTO_FOCUS;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_INT_AUDIO_RETRY_COUNT;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_INT_RETRY_INTERVAL;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PREVIEW_MIRROR;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PUSH_MIRROR;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FIVE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FOUR;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_ONE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_THREE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_TWO;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.pusher.AlivcAudioAACProfileEnum;
import com.alivc.live.pusher.AlivcAudioChannelEnum;
import com.alivc.live.pusher.AlivcAudioSampleRateEnum;
import com.alivc.live.pusher.AlivcAudioSceneModeEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcImageFormat;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.alivc.live.pusher.AlivcLivePusher;
import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcQualityModeEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcSoundFormat;
import com.alivc.live.pusher.WaterMarkInfo;
import com.alivc.live.pusher.demo.download.ResourcesDownload;
import com.alivc.live.pusher.demo.test.InformationActivity;
import com.alivc.live.pusher.demo.test.PushDemoTestConstants;
import com.alivc.live.pusher.widget.PushConfigBottomSheet;
import com.alivc.live.commonutils.FastClickUtil;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyun.animoji.AnimojiEngine;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;
import com.aliyunsdk.queen.menu.download.OnDownloadUICallback;

import java.io.File;
import java.util.ArrayList;

public class PushConfigActivity extends AVBaseThemeActivity {

    @Override
    protected int specifiedThemeMode() {
        return AppCompatDelegate.MODE_NIGHT_YES;
    }

    private AlivcResolutionEnum mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int PROGRESS_0 = 0;
    private static final int PROGRESS_16 = 16;
    private static final int PROGRESS_20 = 20;
    private static final int PROGRESS_33 = 33;
    private static final int PROGRESS_40 = 40;
    private static final int PROGRESS_50 = 50;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_66 = 66;
    private static final int PROGRESS_75 = 75;
    private static final int PROGRESS_80 = 80;
    private static final int PROGRESS_90 = 90;
    private static final int PROGRESS_100 = 100;

    private static final int PROGRESS_AUDIO_160 = 20;
    private static final int PROGRESS_AUDIO_320 = 40;
    private static final int PROGRESS_AUDIO_441 = 60;
    private static final int PROGRESS_AUDIO_480 = 80;
    private InputMethodManager manager;

    private View mPublish;
    private SeekBar mResolution;
    private SeekBar mAudioRate;
    private SeekBar mFps;
    private SeekBar mMinFps;
    private SeekBar mGop;
    private TextView mResolutionText;
    private TextView mAudioRateText;
    private TextView mWaterPosition;
    private TextView mFpsText;
    private TextView mMinFpsText;
    private TextView mGOPText;
    private TextView mQualityMode;
    private TextView mAudioProfiles;
    private TextView mAudioRadio;
    private TextView mVideoEncoder;
    private TextView mBFrames;
    private TextView mOrientation;
    private TextView mDisplayMode;
    private TextView mPushMode;
    private TextView mLocalLogTv;


    private EditText mUrl;
    private EditText mTargetRate;
    private EditText mMinRate;
    private EditText mInitRate;
    private EditText mAudioBitRate;
    private EditText mRetryInterval;
    private EditText mRetryCount;
    private EditText mAuthTime;
    private EditText mPrivacyKey;

    private Switch mWaterMark;
    private Switch mPushMirror;
    private Switch mPreviewMirror;
    private Switch mHardCode;
    private Switch mAudioHardCode;
    private Switch mCamera;
    //    private Switch mAudioOnly;
//    private Switch mVideoOnly;
    private Switch mAutoFocus;
    private Switch mBeautyOn;
    private Switch mAnimojiOn;
    private Switch mAsync;
    private Switch mFlash;
    private Switch mLog;
    private Switch mMusicMode;
    private Switch mBitrate;
    private Switch mVariableResolution;
    private Switch mExtern;
    private Switch mPauseImage;
    private Switch mNetworkImage;
    private Switch mInteractionControlSwitch;
    private ImageView mQr;
    private ImageView mBack;
    private TextView mPushTex;

    private AlivcLivePushConfig mAlivcLivePushConfig;
    private boolean mAsyncValue = true;
    private boolean mAudioOnlyPush = false;
    private boolean mVideoOnlyPush = false;
    private AlivcPreviewOrientationEnum mOrientationEnum = ORIENTATION_PORTRAIT;
    private AlivcQualityModeEnum mQualityModeEnum = AlivcQualityModeEnum.QM_RESOLUTION_FIRST;

    private ArrayList<WaterMarkInfo> mWaterMarkInfos = new ArrayList<>();

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isFlash = false;

    private String mAuthTimeStr = "";
    private String mPrivacyKeyStr = "";
    private boolean mMixStream = false;

    private AlivcLivePusher mAlivcLivePusher = null;

    private CharSequence mCustomTargetBitrateValue = "";
    private CharSequence mCustomMinBitrateValue = "";
    private CharSequence mCustomInitBitrateValue = "";
    private CharSequence mCustomAudioBitrateValue = "";

    private View mTabArgsLayout;
    private View mTabActionLayout;
    private View mTabArgsView;
    private View mTabActionView;
    private LinearLayout mTabArgsContentLayout;
    private LinearLayout mTabActionContentLayout;
    private Switch mAdvanceConfig;
    private LinearLayout mAdvanceLayout;
    private int mFpsConfig = 25;//默认帧率
    private PushConfigDialogImpl mPushConfigDialog = new PushConfigDialogImpl();
    private long mCaptureDownloadId;

    @Nullable
    private AVLoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.push_setting);
        mAlivcLivePushConfig = new AlivcLivePushConfig();
        mAlivcLivePushConfig.setExtraInfo("such_as_user_id");

        //启用内置crash捕获机制，它只会捕获sdk内部造成的crash，而app层依然可以捕获到所有的crash，默认值为true
        //mAlivcLivePushConfig.setEnableSDKCrashMechanism(false);

        //默认有内置播放器，如果有特殊格式需要，可以设置使用阿里云播放器
        //app层需要额外依赖5.4.1+的播放器
        //mAlivcLivePushConfig.setUseAliPlayerForBGM(true);

        //设置音乐模式
        //mAlivcLivePushConfig.setAudioSceneMode(AlivcAudioSceneModeEnum.AUDIO_SCENE_MUSIC_MODE);
        // 设置编码器类型
        // mAlivcLivePushConfig.setVideoEncodeType(Encode_TYPE_H265);
        // 设置b帧个数
        // mAlivcLivePushConfig.setBFrames(1);

        if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation()) {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
        } else {
            mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
            mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
        }
        AlivcLivePushConfig.setMediaProjectionPermissionResultData(null);
        initView();
        setClick();
        Common.copyAsset(this);
        Common.copyAll(this);
        addWaterMarkInfo();
        if (mAlivcLivePushConfig != null) {
            mAlivcLivePushConfig.setPreviewDisplayMode(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL);
            SharedPreferenceUtils.setDisplayFit(getApplicationContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL

                    .getPreviewDisplayMode());
        }
        Intent intent = getIntent();
        String url = intent.getStringExtra("pushUrl");
        if (!TextUtils.isEmpty(url)) {
            mUrl.setText(url);
        }

    }

    private void turnOnBitRateFps(boolean on) {
        if (!on) {
            mFps.setProgress(83);
            mAlivcLivePushConfig.setFps(FPS_25);
            mFpsText.setText(String.valueOf(FPS_25.getFps()));
            mTargetRate.setFocusable(false);
            mMinRate.setFocusable(false);
            mInitRate.setFocusable(false);
            mFps.setFocusable(false);
            mTargetRate.setFocusableInTouchMode(false);
            mMinRate.setFocusableInTouchMode(false);
            mInitRate.setFocusableInTouchMode(false);
            mFps.setFocusableInTouchMode(false);
        } else {
            mTargetRate.setFocusable(true);
            mMinRate.setFocusable(true);
            mInitRate.setFocusable(true);
            mTargetRate.setFocusableInTouchMode(true);
            mMinRate.setFocusableInTouchMode(true);
            mInitRate.setFocusableInTouchMode(true);
            mTargetRate.requestFocus();
            mInitRate.requestFocus();
            mMinRate.requestFocus();
        }
    }

    private void initView() {
        mUrl = (EditText) findViewById(R.id.url_editor);
        mPublish = findViewById(R.id.beginPublish);
        mResolution = (SeekBar) findViewById(R.id.resolution_seekbar);
        mResolutionText = (TextView) findViewById(R.id.resolution_text);
        mFps = (SeekBar) findViewById(R.id.fps_seekbar);
        mFpsText = (TextView) findViewById(R.id.fps_text);
        mTargetRate = (EditText) findViewById(R.id.target_rate_edit);
        mMinRate = (EditText) findViewById(R.id.min_rate_edit);
        mInitRate = (EditText) findViewById(R.id.init_rate_edit);
        mAudioBitRate = (EditText) findViewById(R.id.audio_bitrate);
        mAudioRate = (SeekBar) findViewById(R.id.audio_rate_seekbar);
        mAudioRateText = (TextView) findViewById(R.id.audio_rate_text);
        mRetryInterval = (EditText) findViewById(R.id.retry_interval);
        mRetryCount = (EditText) findViewById(R.id.retry_count);
        mAuthTime = (EditText) findViewById(R.id.auth_time);
        mPrivacyKey = (EditText) findViewById(R.id.privacy_key);
        mMinFps = (SeekBar) findViewById(R.id.min_fps_seekbar);
        mMinFpsText = (TextView) findViewById(R.id.min_fps_text);
        mWaterMark = (Switch) findViewById(R.id.watermark_switch);
        mWaterPosition = (TextView) findViewById(R.id.water_position);
        mPushMirror = (Switch) findViewById(R.id.push_mirror_switch);
        mPreviewMirror = (Switch) findViewById(R.id.preview_mirror_switch);
        mHardCode = (Switch) findViewById(R.id.hard_switch);
        mAudioHardCode = (Switch) findViewById(R.id.audio_hardenc);
        mCamera = (Switch) findViewById(R.id.camera_switch);
//        mAudioOnly = (Switch) findViewById(R.id.audio_only_switch);
//        mVideoOnly = (Switch) findViewById(R.id.video_only_switch);
        mAutoFocus = (Switch) findViewById(R.id.autofocus_switch);
        mBeautyOn = (Switch) findViewById(R.id.beautyOn_switch);
        // Only when device level matches, can use animoji feature.
        mAnimojiOn = (Switch) findViewById(R.id.animoji_switch);
        boolean isDeviceSupportAnimoji = AnimojiEngine.isDeviceSupported(getApplicationContext());
        mAnimojiOn.setVisibility(isDeviceSupportAnimoji ? View.VISIBLE : View.GONE);
        if (!isDeviceSupportAnimoji) {
            SharedPreferenceUtils.setAnimojiOn(getApplicationContext(), false);
        }
        mAsync = (Switch) findViewById(R.id.async_switch);
        mAdvanceConfig = (Switch) findViewById(R.id.advance_config);
        mLog = (Switch) findViewById(R.id.log_switch);
        mMusicMode = (Switch) findViewById(R.id.music_mode_switch);
        mBitrate = (Switch) findViewById(R.id.bitrate_control);
        mVariableResolution = (Switch) findViewById(R.id.variable_resolution);
        mExtern = (Switch) findViewById(R.id.extern_video);
        mPauseImage = (Switch) findViewById(R.id.pause_image);
        mNetworkImage = (Switch) findViewById(R.id.network_image);
        mQr = (ImageView) findViewById(R.id.qr_code);
        mBack = (ImageView) findViewById(R.id.iv_back);
        mAudioRadio = (TextView) findViewById(R.id.audio_channel);
        mVideoEncoder = (TextView) findViewById(R.id.video_encoder_type);
        mBFrames = (TextView) findViewById(R.id.b_frame_num);
        mQualityMode = findViewById(R.id.quality_modes);
        mGop = (SeekBar) findViewById(R.id.gop_seekbar);
        mGOPText = (TextView) findViewById(R.id.gop_text);
        mOrientation = (TextView) findViewById(R.id.main_orientation);
        mDisplayMode = (TextView) findViewById(R.id.setting_display_mode);
        mAudioProfiles = (TextView) findViewById(R.id.audio_profiles);
        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
        turnOnBitRateFps(false);
        mTabArgsLayout = findViewById(R.id.tab_args_layout);
        mTabActionLayout = findViewById(R.id.tab_action_layout);
        mTabArgsView = (View) findViewById(R.id.tab_args_view);
        mTabActionView = (View) findViewById(R.id.tab_action_view);
        mTabArgsContentLayout = (LinearLayout) findViewById(R.id.push_args_setting);
        mTabActionContentLayout = (LinearLayout) findViewById(R.id.push_function_setting);
        mAdvanceLayout = (LinearLayout) findViewById(R.id.advance_layout);
        mPushMode = findViewById(R.id.push_mode);
        mLocalLogTv = findViewById(R.id.local_log);
//        mVideoEncoder.check(R.id.h264_encoder);
//        mBFrames.check(R.id.b_frame_no);
        mInteractionControlSwitch = findViewById(R.id.interaction_control);
        RelativeLayout interactionRelativeLayout = findViewById(R.id.rl_interaction);

        interactionRelativeLayout.setVisibility(AlivcLiveBase.isSupportLiveMode(AlivcLiveMode.AlivcLiveInteractiveMode) ? View.VISIBLE : View.GONE);

        String initUrl = PushDemoTestConstants.getTestPushUrl();
        if (!initUrl.isEmpty()) {
            mUrl.setText(initUrl);
        }
    }

    private void setClick() {
        mPublish.setOnClickListener(onClickListener);
        mWaterPosition.setOnClickListener(onClickListener);
        mTabArgsLayout.setOnClickListener(onClickListener);
        mTabActionLayout.setOnClickListener(onClickListener);
        mWaterMark.setOnCheckedChangeListener(onCheckedChangeListener);
        mPushMirror.setOnCheckedChangeListener(onCheckedChangeListener);
        mPreviewMirror.setOnCheckedChangeListener(onCheckedChangeListener);
        mHardCode.setOnCheckedChangeListener(onCheckedChangeListener);
        mAudioHardCode.setOnCheckedChangeListener(onCheckedChangeListener);
        mCamera.setOnCheckedChangeListener(onCheckedChangeListener);
        mAutoFocus.setOnCheckedChangeListener(onCheckedChangeListener);
        mBeautyOn.setOnCheckedChangeListener(onCheckedChangeListener);
        mAnimojiOn.setOnCheckedChangeListener(onCheckedChangeListener);
        mAdvanceConfig.setOnCheckedChangeListener(onCheckedChangeListener);
        mResolution.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mAudioRate.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mExtern.setOnCheckedChangeListener(onCheckedChangeListener);
        mFps.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mMinFps.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mAsync.setOnCheckedChangeListener(onCheckedChangeListener);
        mLog.setOnCheckedChangeListener(onCheckedChangeListener);
        mMusicMode.setOnCheckedChangeListener(onCheckedChangeListener);
        mBitrate.setOnCheckedChangeListener(onCheckedChangeListener);
        mVariableResolution.setOnCheckedChangeListener(onCheckedChangeListener);
        mPauseImage.setOnCheckedChangeListener(onCheckedChangeListener);
        mNetworkImage.setOnCheckedChangeListener(onCheckedChangeListener);
        mInteractionControlSwitch.setOnCheckedChangeListener(onCheckedChangeListener);
        mQr.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mAudioRadio.setOnClickListener(onClickListener);
        mVideoEncoder.setOnClickListener(onClickListener);
        mBFrames.setOnClickListener(onClickListener);
        mQualityMode.setOnClickListener(onClickListener);
        mGop.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mOrientation.setOnClickListener(onClickListener);
        mDisplayMode.setOnClickListener(onClickListener);
        mAudioProfiles.setOnClickListener(onClickListener);
        mPushMode.setOnClickListener(onClickListener);
        mLocalLogTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                jump2InformationActivity();
                return true;
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            if (id == R.id.beginPublish) {
                if (FastClickUtil.isFastClick()) {
                    return;//点击间隔 至少1秒
                }
                if (getPushConfig() != null) {

                    ArrayList<WaterMarkInfo> waterMarkInfos = new ArrayList<>();
                    if (mWaterMark.isChecked()) {
                        waterMarkInfos.addAll(mWaterMarkInfos);
                    }

                    if (mUrl.getText().toString().contains("rtmp://") || mUrl.getText().toString().contains("artc://")) {
                        checkModelAndRun(new Runnable() {
                            @Override
                            public void run() {
                                LivePushActivity.startActivity(PushConfigActivity.this, mAlivcLivePushConfig, mUrl.getText().toString(),
                                        mAsyncValue, mAudioOnlyPush, mVideoOnlyPush, mOrientationEnum, mCameraId, isFlash, mAuthTimeStr,
                                        mPrivacyKeyStr, mMixStream, mAlivcLivePushConfig.isExternMainStream(), mBeautyOn.isChecked(), mFpsConfig,
                                        waterMarkInfos);
                            }
                        });
                    } else {
                        AVToast.show(PushConfigActivity.this, true, "url format unsupported");
                    }
                }
            } else if (id == R.id.qr_code) {
                if (ContextCompat.checkSelfPermission(PushConfigActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(PushConfigActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            } else if (id == R.id.water_position) {
                if (mWaterMark.isChecked()) {
                    PushWaterMarkDialog pushWaterMarkDialog = new PushWaterMarkDialog();
                    pushWaterMarkDialog.setWaterMarkInfo(mWaterMarkInfos);
                    pushWaterMarkDialog.show(getSupportFragmentManager(), "waterDialog");
                }
            } else if (id == R.id.iv_back) {
                finish();
            } else if (id == R.id.tab_args_layout) {
                mTabArgsView.setVisibility(View.VISIBLE);
                mTabActionView.setVisibility(View.INVISIBLE);
                mTabArgsContentLayout.setVisibility(View.VISIBLE);
                mTabActionContentLayout.setVisibility(View.GONE);
            } else if (id == R.id.tab_action_layout) {
                mTabActionView.setVisibility(View.VISIBLE);
                mTabArgsView.setVisibility(View.INVISIBLE);
                mTabArgsContentLayout.setVisibility(View.GONE);
                mTabActionContentLayout.setVisibility(View.VISIBLE);
            } else if (id == R.id.quality_modes) {
                mPushConfigDialog.showConfigDialog(mQualityMode, mQualityListener, 0);
            } else if (id == R.id.audio_profiles) {
                mPushConfigDialog.showConfigDialog(mAudioProfiles, mAudioProfilesListener, 0);
            } else if (id == R.id.audio_channel) {
                mPushConfigDialog.showConfigDialog(mAudioRadio, mAudioChannelListener, 1);
            } else if (id == R.id.video_encoder_type) {
                mPushConfigDialog.showConfigDialog(mVideoEncoder, mEncoderTypeListener, 0);
            } else if (id == R.id.b_frame_num) {
                mPushConfigDialog.showConfigDialog(mBFrames, mFrameNumListener, 0);
            } else if (id == R.id.main_orientation) {
                mPushConfigDialog.showConfigDialog(mOrientation, mOrientationListener, 0);
            } else if (id == R.id.setting_display_mode) {
                mPushConfigDialog.showConfigDialog(mDisplayMode, mDisplayModenListener, 0);
            } else if (id == R.id.push_mode) {
                mPushConfigDialog.showConfigDialog(mPushMode, mPushModeListener, 0);
            }
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mPushModeListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            mAudioOnlyPush = (index == 1);
            mVideoOnlyPush = (index == 2);
            mAlivcLivePushConfig.setAudioOnly(mAudioOnlyPush);
            mAlivcLivePushConfig.setVideoOnly(mVideoOnlyPush);
        }
    };
    private final PushConfigBottomSheet.OnPushConfigSelectorListener mDisplayModenListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setPreviewDisplayMode(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL);
                SharedPreferenceUtils.setDisplayFit(getApplicationContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL.getPreviewDisplayMode());
            } else if (index == 1) {
                mAlivcLivePushConfig.setPreviewDisplayMode(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT);
                SharedPreferenceUtils.setDisplayFit(getApplicationContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT.getPreviewDisplayMode());
            } else if (index == 2) {
                mAlivcLivePushConfig.setPreviewDisplayMode(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL);
                SharedPreferenceUtils.setDisplayFit(getApplicationContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL.getPreviewDisplayMode());
            }
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mOrientationListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_PORTRAIT);
                mOrientationEnum = ORIENTATION_PORTRAIT;
                if (mAlivcLivePushConfig.getPausePushImage() == null || mAlivcLivePushConfig.getPausePushImage().equals("")) {
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                }
                if (mAlivcLivePushConfig.getNetworkPoorPushImage() != null && !mAlivcLivePushConfig.getNetworkPoorPushImage().equals("")) {
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                }
            } else if (index == 1) {
                mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_LANDSCAPE_HOME_LEFT);
                mOrientationEnum = ORIENTATION_LANDSCAPE_HOME_LEFT;
                if (mAlivcLivePushConfig.getPausePushImage() == null || mAlivcLivePushConfig.getPausePushImage().equals("")) {
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                }
                if (mAlivcLivePushConfig.getNetworkPoorPushImage() != null && !mAlivcLivePushConfig.getNetworkPoorPushImage().equals("")) {
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                }
            } else if (index == 2) {
                mAlivcLivePushConfig.setPreviewOrientation(ORIENTATION_LANDSCAPE_HOME_RIGHT);
                mOrientationEnum = ORIENTATION_LANDSCAPE_HOME_RIGHT;
                if (mAlivcLivePushConfig.getPausePushImage() == null || mAlivcLivePushConfig.getPausePushImage().equals("")) {
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                }
                if (mAlivcLivePushConfig.getNetworkPoorPushImage() != null && !mAlivcLivePushConfig.getNetworkPoorPushImage().equals("")) {
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                }
            }
            updateAnimojiButtonState(getApplicationContext());

        }
    };
    private final PushConfigBottomSheet.OnPushConfigSelectorListener mFrameNumListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setBFrames(0);
            } else if (index == 1) {
                mAlivcLivePushConfig.setBFrames(1);
            } else if (index == 2) {
                mAlivcLivePushConfig.setBFrames(3);
            }

        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mEncoderTypeListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setVideoEncodeType(Encode_TYPE_H264);
            } else if (index == 1) {
                mAlivcLivePushConfig.setVideoEncodeType(Encode_TYPE_H265);
            }

        }
    };
    private final PushConfigBottomSheet.OnPushConfigSelectorListener mAudioChannelListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setAudioChannels(AUDIO_CHANNEL_ONE);
            } else if (index == 1) {
                mAlivcLivePushConfig.setAudioChannels(AUDIO_CHANNEL_TWO);
            }


        }
    };
    private final PushConfigBottomSheet.OnPushConfigSelectorListener mAudioProfilesListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String data, int index) {
            if (index == 0) {
                mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.AAC_LC);
            } else if (index == 1) {
                mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.HE_AAC);
            } else if (index == 2) {
                mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.HE_AAC);
            } else if (index == 3) {
                mAlivcLivePushConfig.setAudioProfile(AlivcAudioAACProfileEnum.AAC_LD);
            }


        }
    };
    private final PushConfigBottomSheet.OnPushConfigSelectorListener mQualityListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String tips, int i) {
            mQualityMode.setText(tips);
            if (i == 0) {
                if (AlivcQualityModeEnum.QM_CUSTOM.equals(mAlivcLivePushConfig.getQualityMode())) {
                    mCustomTargetBitrateValue = mTargetRate.getText();
                    mCustomMinBitrateValue = mMinRate.getText();
                    mCustomInitBitrateValue = mInitRate.getText();
                    mCustomAudioBitrateValue = mAudioBitRate.getText();
                    mTargetRate.setText("");
                    mMinRate.setText("");
                    mInitRate.setText("");
                    mAudioBitRate.setText("");
                }
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_RESOLUTION_FIRST);
                    if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                }
                turnOnBitRateFps(false);
            } else if (i == 1) {
                if (AlivcQualityModeEnum.QM_CUSTOM.equals(mAlivcLivePushConfig.getQualityMode())) {
                    mCustomTargetBitrateValue = mTargetRate.getText();
                    mCustomMinBitrateValue = mMinRate.getText();
                    mCustomInitBitrateValue = mInitRate.getText();
                    mCustomAudioBitrateValue = mAudioBitRate.getText();
                    mTargetRate.setText("");
                    mMinRate.setText("");
                    mInitRate.setText("");
                    mAudioBitRate.setText("");
                }
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_FLUENCY_FIRST);
                    if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                }
                turnOnBitRateFps(false);
            } else if (i == 2) {
                mTargetRate.setText(mCustomTargetBitrateValue);
                mMinRate.setText(mCustomMinBitrateValue);
                mInitRate.setText(mCustomInitBitrateValue);
                mAudioBitRate.setText(mCustomAudioBitrateValue);
                if (mAlivcLivePushConfig != null) {
                    mAlivcLivePushConfig.setQualityMode(AlivcQualityModeEnum.QM_CUSTOM);
                    if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mDefinition.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                }
                turnOnBitRateFps(true);
            }

        }
    };

    private OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int id = buttonView.getId();
            if (id == R.id.watermark_switch) {
                if (mWaterPosition != null) {
                    mWaterPosition.setClickable(isChecked);
                    mWaterPosition.setTextColor(isChecked ? getResources().getColor(R.color.colourful_text_strong) : getResources().getColor(R.color.text_ultraweak));
                }
            } else if (id == R.id.push_mirror_switch) {
                mAlivcLivePushConfig.setPushMirror(isChecked);
                SharedPreferenceUtils.setPushMirror(getApplicationContext(), isChecked);
            } else if (id == R.id.preview_mirror_switch) {
                mAlivcLivePushConfig.setPreviewMirror(isChecked);
                SharedPreferenceUtils.setPreviewMirror(getApplicationContext(), isChecked);
            } else if (id == R.id.hard_switch) {
                mAlivcLivePushConfig.setVideoEncodeMode(isChecked ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);
            } else if (id == R.id.audio_hardenc) {
                mAlivcLivePushConfig.setAudioEncodeMode(isChecked ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);
            } else if (id == R.id.camera_switch) {
                mAlivcLivePushConfig.setCameraType(isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK);
                mCameraId = (isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.getCameraId() : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK.getCameraId());
            } else if (id == R.id.autofocus_switch) {
                mAlivcLivePushConfig.setAutoFocus(isChecked);
                SharedPreferenceUtils.setAutofocus(getApplicationContext(), isChecked);
            } else if (id == R.id.advance_config) {
                if (isChecked) {
                    mAdvanceLayout.setVisibility(View.VISIBLE);
                } else {
                    mAdvanceLayout.setVisibility(View.GONE);
                }
            } else if (id == R.id.beautyOn_switch) {
                SharedPreferenceUtils.setBeautyOn(getApplicationContext(), isChecked);
                updateAnimojiButtonState(getApplicationContext());
            } else if (id == R.id.animoji_switch) {
                SharedPreferenceUtils.setAnimojiOn(getApplicationContext(), isChecked);
            } else if (id == R.id.async_switch) {
                mAsyncValue = isChecked;
            } else if (id == R.id.log_switch) {
                if (isChecked) {
                    LogcatHelper.getInstance(getApplicationContext()).start();
                } else {
                    LogcatHelper.getInstance(getApplicationContext()).stop();
                }
            } else if (id == R.id.music_mode_switch) {
                mAlivcLivePushConfig.setAudioSceneMode(isChecked ? AlivcAudioSceneModeEnum.AUDIO_SCENE_MUSIC_MODE : AlivcAudioSceneModeEnum.AUDIO_SCENE_DEFAULT_MODE);
            } else if (id == R.id.bitrate_control) {
                mAlivcLivePushConfig.setEnableBitrateControl(isChecked);
            } else if (id == R.id.variable_resolution) {
                mAlivcLivePushConfig.setEnableAutoResolution(isChecked);
            } else if (id == R.id.extern_video) {
                mAlivcLivePushConfig.setExternMainStream(isChecked, AlivcImageFormat.IMAGE_FORMAT_YUVNV12, AlivcSoundFormat.SOUND_FORMAT_S16);
                mAlivcLivePushConfig.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE);
                mAlivcLivePushConfig.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100);
                if (isChecked) {
                    startDownloadYUV();
                }
            } else if (id == R.id.pause_image) {
                if (!isChecked) {
                    mAlivcLivePushConfig.setPausePushImage("");
                } else {
                    if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation()) {
                        mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
                    } else {
                        mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                    }
                }
            } else if (id == R.id.network_image) {
                if (!isChecked) {
                    mAlivcLivePushConfig.setNetworkPoorPushImage("");
                } else {
                    if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation()) {
                        mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
                    } else {
                        mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                    }
                }
            } else if (id == R.id.interaction_control) {
                if (isChecked) {
                    mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
                } else {
                    mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveBasicMode);
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int seekBarId = seekBar.getId();
            if (mResolution.getId() == seekBarId) {
                if (progress <= PROGRESS_0) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_180P;
                    mResolutionText.setText(R.string.setting_resolution_180P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_20) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_240P;
                    mResolutionText.setText(R.string.setting_resolution_240P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_20 && progress <= PROGRESS_40) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_360P;
                    mResolutionText.setText(R.string.setting_resolution_360P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_40 && progress <= PROGRESS_60) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_480P;
                    mResolutionText.setText(R.string.setting_resolution_480P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_60 && progress <= PROGRESS_80) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
                    mResolutionText.setText(R.string.setting_resolution_540P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_80 && progress <= PROGRESS_90) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_720P;
                    mResolutionText.setText(R.string.setting_resolution_720P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                } else if (progress > PROGRESS_90) {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_1080P;
                    mResolutionText.setText(R.string.setting_resolution_1080P);
                    if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else if (mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    } else {
                        mTargetRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
                        mMinRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
                        mInitRate.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()));
                        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
                        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
                    }
                }
            } else if (mAudioRate.getId() == seekBarId) {
                if (progress <= PROGRESS_AUDIO_160) {
                    mAlivcLivePushConfig.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_16000);
                    mAudioRateText.setText(getString(R.string.setting_audio_160));
                } else if (progress <= PROGRESS_AUDIO_320) {
                    mAlivcLivePushConfig.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000);
                    mAudioRateText.setText(getString(R.string.setting_audio_320));
                } else if (progress <= PROGRESS_AUDIO_441) {
                    mAlivcLivePushConfig.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100);
                    mAudioRateText.setText(getString(R.string.setting_audio_441));
                } else {
                    mAlivcLivePushConfig.setAudioSamepleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_48000);
                    mAudioRateText.setText(getString(R.string.setting_audio_480));
                }
            } else if (mFps.getId() == seekBarId) {
                if (!mAlivcLivePushConfig.getQualityMode().equals(AlivcQualityModeEnum.QM_CUSTOM)) {
                    mFps.setProgress(83);
                    mAlivcLivePushConfig.setFps(FPS_25);
                    mFpsConfig = 25;
                    mFpsText.setText(String.valueOf(FPS_25.getFps()));
                    return;
                }
                if (progress <= PROGRESS_0) {
                    mAlivcLivePushConfig.setFps(FPS_8);
                    mFpsText.setText(String.valueOf(FPS_8.getFps()));
                    mFpsConfig = 8;
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_16) {
                    mAlivcLivePushConfig.setFps(FPS_10);
                    mFpsText.setText(String.valueOf(FPS_10.getFps()));
                    mFpsConfig = 10;
                } else if (progress > PROGRESS_16 && progress <= PROGRESS_33) {
                    mAlivcLivePushConfig.setFps(FPS_12);
                    mFpsText.setText(String.valueOf(FPS_12.getFps()));
                    mFpsConfig = 12;
                } else if (progress > PROGRESS_33 && progress <= PROGRESS_50) {
                    mAlivcLivePushConfig.setFps(FPS_15);
                    mFpsText.setText(String.valueOf(FPS_15.getFps()));
                    mFpsConfig = 15;
                } else if (progress > PROGRESS_50 && progress <= PROGRESS_66) {
                    mAlivcLivePushConfig.setFps(FPS_20);
                    mFpsText.setText(String.valueOf(FPS_20.getFps()));
                    mFpsConfig = 20;
                } else if (progress > PROGRESS_66 && progress <= PROGRESS_80) {
                    mAlivcLivePushConfig.setFps(FPS_25);
                    mFpsText.setText(String.valueOf(FPS_25.getFps()));
                    mFpsConfig = 25;
                } else if (progress > PROGRESS_80) {
                    mAlivcLivePushConfig.setFps(FPS_30);
                    mFpsText.setText(String.valueOf(FPS_30.getFps()));
                    mFpsConfig = 30;
                }
            } else if (mMinFps.getId() == seekBarId) {
                if (progress <= PROGRESS_0) {
                    mAlivcLivePushConfig.setMinFps(FPS_8);
                    mMinFpsText.setText(String.valueOf(FPS_8.getFps()));
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_16) {
                    mAlivcLivePushConfig.setMinFps(FPS_10);
                    mMinFpsText.setText(String.valueOf(FPS_10.getFps()));
                } else if (progress > PROGRESS_16 && progress <= PROGRESS_33) {
                    mAlivcLivePushConfig.setMinFps(FPS_12);
                    mMinFpsText.setText(String.valueOf(FPS_12.getFps()));
                } else if (progress > PROGRESS_33 && progress <= PROGRESS_50) {
                    mAlivcLivePushConfig.setMinFps(FPS_15);
                    mMinFpsText.setText(String.valueOf(FPS_15.getFps()));
                } else if (progress > PROGRESS_50 && progress <= PROGRESS_66) {
                    mAlivcLivePushConfig.setMinFps(FPS_20);
                    mMinFpsText.setText(String.valueOf(FPS_20.getFps()));
                } else if (progress > PROGRESS_66 && progress <= PROGRESS_80) {
                    mAlivcLivePushConfig.setMinFps(FPS_25);
                    mMinFpsText.setText(String.valueOf(FPS_25.getFps()));
                } else if (progress > PROGRESS_80) {
                    mAlivcLivePushConfig.setMinFps(FPS_30);
                    mMinFpsText.setText(String.valueOf(FPS_30.getFps()));
                }
            } else if (mGop.getId() == seekBarId) {
                if (progress <= PROGRESS_20) {
                    mAlivcLivePushConfig.setVideoEncodeGop(GOP_ONE);
                    mGOPText.setText("1/s");
                } else if (progress > PROGRESS_20 && progress <= PROGRESS_40) {
                    mAlivcLivePushConfig.setVideoEncodeGop(GOP_TWO);
                    mGOPText.setText("2/s");
                } else if (progress > PROGRESS_40 && progress <= PROGRESS_60) {
                    mAlivcLivePushConfig.setVideoEncodeGop(GOP_THREE);
                    mGOPText.setText("3/s");
                } else if (progress > PROGRESS_60 && progress <= PROGRESS_80) {
                    mAlivcLivePushConfig.setVideoEncodeGop(GOP_FOUR);
                    mGOPText.setText("4/s");
                } else if (progress > PROGRESS_80 && progress <= PROGRESS_100) {
                    mAlivcLivePushConfig.setVideoEncodeGop(GOP_FIVE);
                    mGOPText.setText("5/s");
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            int progress = seekBar.getProgress();
            if (mResolution.getId() == seekBar.getId()) {
                if (progress < PROGRESS_0) {
                    seekBar.setProgress(0);
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_20) {
                    seekBar.setProgress(PROGRESS_20);
                } else if (progress > PROGRESS_20 && progress <= PROGRESS_40) {
                    seekBar.setProgress(PROGRESS_40);
                } else if (progress > PROGRESS_40 && progress <= PROGRESS_60) {
                    seekBar.setProgress(PROGRESS_60);
                } else if (progress > PROGRESS_60 && progress <= PROGRESS_80) {
                    seekBar.setProgress(PROGRESS_80);
                } else if (progress > PROGRESS_80 && progress <= PROGRESS_90) {
                    seekBar.setProgress(PROGRESS_90);
                } else if (progress > PROGRESS_90) {
                    seekBar.setProgress(PROGRESS_100);
                }
            } else if (mFps.getId() == seekBar.getId()) {
                if (progress <= PROGRESS_0) {
                    seekBar.setProgress(0);
                } else if (progress > PROGRESS_0 && progress <= PROGRESS_16) {
                    seekBar.setProgress(PROGRESS_16);
                } else if (progress > PROGRESS_16 && progress <= PROGRESS_33) {
                    seekBar.setProgress(PROGRESS_33);
                } else if (progress > PROGRESS_33 && progress <= PROGRESS_50) {
                    seekBar.setProgress(PROGRESS_50);
                } else if (progress > PROGRESS_50 && progress <= PROGRESS_66) {
                    seekBar.setProgress(PROGRESS_66);
                } else if (progress > PROGRESS_66 && progress <= PROGRESS_80) {
                    seekBar.setProgress(PROGRESS_80);
                } else if (progress > PROGRESS_80) {
                    seekBar.setProgress(PROGRESS_100);
                }
            } else if (mAudioRate.getId() == seekBar.getId()) {
                if (progress <= PROGRESS_AUDIO_160) {
                    seekBar.setProgress(PROGRESS_AUDIO_160);
                } else if (progress <= PROGRESS_AUDIO_320) {
                    seekBar.setProgress(PROGRESS_AUDIO_320);
                } else if (progress <= PROGRESS_AUDIO_441) {
                    seekBar.setProgress(PROGRESS_AUDIO_441);
                } else {
                    seekBar.setProgress(PROGRESS_AUDIO_480);
                }
            } else if (mGop.getId() == seekBar.getId()) {
                if (progress <= 20) {
                    seekBar.setProgress(PROGRESS_20);
                } else if (progress <= 40) {
                    seekBar.setProgress(PROGRESS_40);
                } else if (progress <= 60) {
                    seekBar.setProgress(PROGRESS_60);
                } else if (progress <= 80) {
                    seekBar.setProgress(PROGRESS_80);
                } else if (progress <= 100) {
                    seekBar.setProgress(PROGRESS_100);
                }
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mPushMirror.setChecked(SharedPreferenceUtils.isPushMirror(getApplicationContext(), DEFAULT_VALUE_PUSH_MIRROR));
        mPreviewMirror.setChecked(SharedPreferenceUtils.isPreviewMirror(getApplicationContext(), DEFAULT_VALUE_PREVIEW_MIRROR));
        mAutoFocus.setChecked(SharedPreferenceUtils.isAutoFocus(getApplicationContext(), DEFAULT_VALUE_AUTO_FOCUS));
        mBeautyOn.setChecked(SharedPreferenceUtils.isBeautyOn(getApplicationContext()));
        mAnimojiOn.setChecked(SharedPreferenceUtils.isAnimojiOn(getApplicationContext()));
        updateAnimojiButtonState(getApplicationContext());
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(PushConfigActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    AVToast.show(this, false, "You must agree the camera permission request before you use the code scan function");
                }
            }
            break;
            default:
                break;
        }
    }

    private AlivcLivePushConfig getPushConfig() {
        if (mUrl.getText().toString().isEmpty()) {
            AVToast.show(this, true, getString(R.string.url_empty));
            return null;
        }
        mAlivcLivePushConfig.setResolution(mDefinition);
        if (!mInitRate.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setInitialVideoBitrate(Integer.valueOf(mInitRate.getText().toString()));
        } else {
            mAlivcLivePushConfig.setInitialVideoBitrate(Integer.valueOf(mInitRate.getHint().toString()));
        }

        if (!mAudioBitRate.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setAudioBitRate(1000 * Integer.valueOf(mAudioBitRate.getText().toString()));
        } else {
            mAlivcLivePushConfig.setAudioBitRate(1000 * Integer.valueOf(mAudioBitRate.getHint().toString()));
        }

        if (!mMinRate.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setMinVideoBitrate(Integer.valueOf(mMinRate.getText().toString()));
            SharedPreferenceUtils.setMinBit(getApplicationContext(), Integer.valueOf(mMinRate.getText().toString()));
        } else {
            mAlivcLivePushConfig.setMinVideoBitrate(Integer.valueOf(mMinRate.getHint().toString()));
            SharedPreferenceUtils.setMinBit(getApplicationContext(), Integer.valueOf(mMinRate.getHint().toString()));
        }

        if (!mTargetRate.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setTargetVideoBitrate(Integer.valueOf(mTargetRate.getText().toString()));
            SharedPreferenceUtils.setTargetBit(getApplicationContext(), Integer.valueOf(mTargetRate.getText().toString()));
        } else {
            mAlivcLivePushConfig.setTargetVideoBitrate(Integer.valueOf(mTargetRate.getHint().toString()));
            SharedPreferenceUtils.setTargetBit(getApplicationContext(), Integer.valueOf(mTargetRate.getHint().toString()));
        }

        if (!mRetryCount.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setConnectRetryCount(Integer.valueOf(mRetryCount.getText().toString()));
        } else {
            mAlivcLivePushConfig.setConnectRetryCount(DEFAULT_VALUE_INT_AUDIO_RETRY_COUNT);
        }

        if (!mRetryInterval.getText().toString().isEmpty()) {
            mAlivcLivePushConfig.setConnectRetryInterval(Integer.valueOf(mRetryInterval.getText().toString()));
        } else {
            mAlivcLivePushConfig.setConnectRetryInterval(DEFAULT_VALUE_INT_RETRY_INTERVAL);
        }

        mAuthTimeStr = mAuthTime.getText().toString();

        mPrivacyKeyStr = mPrivacyKey.getText().toString();

        return mAlivcLivePushConfig;
    }

    private void addWaterMarkInfo() {
        //添加三个水印，位置坐标不同
        WaterMarkInfo waterMarkInfo = new WaterMarkInfo();
        waterMarkInfo.mWaterMarkPath = Common.waterMark;
        WaterMarkInfo waterMarkInfo1 = new WaterMarkInfo();
        waterMarkInfo1.mWaterMarkPath = Common.waterMark;
        waterMarkInfo.mWaterMarkCoordY += 0.2;
        WaterMarkInfo waterMarkInfo2 = new WaterMarkInfo();
        waterMarkInfo2.mWaterMarkPath = Common.waterMark;
        waterMarkInfo2.mWaterMarkCoordY += 0.4;
        mWaterMarkInfos.add(waterMarkInfo);
        mWaterMarkInfos.add(waterMarkInfo1);
        mWaterMarkInfos.add(waterMarkInfo2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        mUrl.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));  //or do sth
                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            mUrl.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    default:
                        break;
                }
                break;
            case LivePushActivity.REQ_CODE_PUSH: {
                if (mTargetRate != null && mMinRate != null) {

                    if (!mTargetRate.getText().toString().isEmpty() || Integer.valueOf(mTargetRate.getHint().toString()) != SharedPreferenceUtils.getTargetBit(getApplicationContext())) {
                        mTargetRate.setText(String.valueOf(SharedPreferenceUtils.getTargetBit(getApplicationContext())));
                    }

                    if (!mMinRate.getText().toString().isEmpty() || Integer.valueOf(mMinRate.getHint().toString()) != SharedPreferenceUtils.getMinBit(getApplicationContext())) {
                        mMinRate.setText(String.valueOf(SharedPreferenceUtils.getMinBit(getApplicationContext())));
                    }
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                if (manager == null) {
                    manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        SharedPreferenceUtils.clear(getApplicationContext());
        mPushConfigDialog.destroy();
    }

    private void updateAnimojiButtonState(@NonNull Context context) {
        if (mAnimojiOn.getVisibility() != View.VISIBLE) {
            return;
        }
        boolean isSupport = isSupportAnimoji(context);
        mAnimojiOn.setClickable(isSupport);
        // If animoji switch is disabled, we need to set is as unchecked
        if (!mAnimojiOn.isClickable() && mAnimojiOn.isChecked()) {
            mAnimojiOn.setChecked(false);
            SharedPreferenceUtils.setAnimojiOn(getApplicationContext(), false);
        }
    }

    // only high-performance android device supports animoji feature.
    private boolean isSupportAnimoji(@NonNull Context context) {
        boolean isDeviceSupportAnimoji = AnimojiEngine.isDeviceSupported(context);
        // Animoji can only be used when the following conditions are true:
        // 1.Not audio push, we can get the preview;
        // 2.The display orientation is not left or right;
        // 3.Beauty switch is off;
        // 4.The device hardware level matches;
        return !mAudioOnlyPush && mOrientationEnum == AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT && !mBeautyOn.isChecked() && isDeviceSupportAnimoji;
    }

    private void checkModelAndRun(final Runnable runnable) {
        BeautyMenuMaterial.getInstance().checkModelReady(this, new OnDownloadUICallback() {
            @Override
            public void onDownloadStart(@StringRes int tipsResId) {
                showProgressDialog(tipsResId);
            }

            @Override
            public void onDownloadProgress(final float v) {
            }

            @Override
            public void onDownloadSuccess() {
                hideProgressDialog();
                runnable.run();
            }

            @Override
            public void onDownloadError(@StringRes int tipsResId) {
                hideProgressDialog();
                showErrorTips(tipsResId);
            }
        });
    }

    private void showProgressDialog(@StringRes int tipsResId) {
        if (null == mLoadingDialog) {
            mLoadingDialog = new AVLoadingDialog(this).tip(getString(tipsResId));
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public void showErrorTips(@StringRes int tipsResId) {
        Toast.makeText(this, tipsResId, Toast.LENGTH_SHORT).show();
    }

    private void startDownloadYUV() {
        mCaptureDownloadId = ResourcesDownload.downloadCaptureYUV(this, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(long downloadId) {
                hideProgressDialog();
                AVToast.show(PushConfigActivity.this, true, "Download Success");
            }

            @Override
            public void onDownloadProgress(long downloadId, double percent) {
            }

            @Override
            public void onDownloadError(long downloadId, int errorCode, String errorMsg) {
                hideProgressDialog();
                AVToast.show(PushConfigActivity.this, true, errorMsg);
                if (errorCode != DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                    mExtern.setChecked(false);
                }
            }
        });
        showProgressDialog(R.string.waiting_download_video_resources);
    }

    private void jump2InformationActivity() {
        Intent intent = new Intent(this, InformationActivity.class);
        startActivity(intent);
    }

}
