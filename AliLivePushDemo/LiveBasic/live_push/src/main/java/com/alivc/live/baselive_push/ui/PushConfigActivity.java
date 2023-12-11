
package com.alivc.live.baselive_push.ui;

import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_AUTO_FOCUS;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PREVIEW_MIRROR;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PUSH_MIRROR;
import static com.alivc.live.pusher.AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.annotations.AlivcLiveMode;
import com.alivc.live.baselive_common.Common;
import com.alivc.live.baselive_common.LivePushSettingView;
import com.alivc.live.baselive_common.PushWaterMarkDialog;
import com.alivc.live.baselive_push.R;
import com.alivc.live.baselive_push.test.InformationActivity;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.commonbiz.test.PushDemoTestConstants;
import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.commonutils.LogcatHelper;
import com.alivc.live.pusher.AlivcAudioChannelEnum;
import com.alivc.live.pusher.AlivcAudioSampleRateEnum;
import com.alivc.live.pusher.AlivcAudioSceneModeEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcImageFormat;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcSoundFormat;
import com.alivc.live.pusher.WaterMarkInfo;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;
import com.aliyunsdk.queen.menu.download.OnDownloadUICallback;

import java.io.File;
import java.util.ArrayList;

public class PushConfigActivity extends AVBaseThemeActivity {

    private RelativeLayout mInteractionRelativeLayout;
    private RadioGroup mLivePushModeRadioGroup;
    private LivePushSettingView mLivePushSettingView;
    private AlivcResolutionEnum mCurrentResolution = AlivcResolutionEnum.RESOLUTION_540P;

    @Override
    protected int specifiedThemeMode() {
        return AppCompatDelegate.MODE_NIGHT_YES;
    }

    private AlivcResolutionEnum mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
    private static final int REQ_CODE_PERMISSION = 0x1111;
    private InputMethodManager manager;

    private View mPublish;
    private TextView mLocalLogTv;
    private EditText mUrl;
    private ImageView mQr;
    private ImageView mBack;

    private AlivcLivePushConfig mAlivcLivePushConfig;
    private boolean mAsyncValue = true;
    private boolean mAudioOnlyPush = false;
    private boolean mVideoOnlyPush = false;
    private AlivcPreviewOrientationEnum mOrientationEnum = ORIENTATION_PORTRAIT;
    private ArrayList<WaterMarkInfo> mWaterMarkInfos = new ArrayList<>();

    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private boolean isFlash = false;

    private String mAuthTimeStr = "";
    private String mPrivacyKeyStr = "";
    private boolean mMixStream = false;

    private View mTabArgsLayout;
    private View mTabActionLayout;
    private View mTabArgsView;
    private View mTabActionView;
    private int mFpsConfig = 25;//默认帧率

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

    private void initView() {
        mUrl = (EditText) findViewById(R.id.url_editor);
        mPublish = findViewById(R.id.beginPublish);
        mQr = (ImageView) findViewById(R.id.qr_code);
        mBack = (ImageView) findViewById(R.id.iv_back);

        SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate());
        SharedPreferenceUtils.setHintMinBit(getApplicationContext(), AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate());
        mTabArgsLayout = findViewById(R.id.tab_args_layout);
        mTabActionLayout = findViewById(R.id.tab_action_layout);
        mTabArgsView = (View) findViewById(R.id.tab_args_view);
        mTabActionView = (View) findViewById(R.id.tab_action_view);
        mLocalLogTv = findViewById(R.id.local_log);
//        mVideoEncoder.check(R.id.h264_encoder);
//        mBFrames.check(R.id.b_frame_no);
        mInteractionRelativeLayout = findViewById(R.id.rl_interaction);
        mLivePushModeRadioGroup = findViewById(R.id.live_push_mode);
        mLivePushSettingView = findViewById(R.id.push_setting_view);

        mInteractionRelativeLayout.setVisibility(AlivcLiveBase.isSupportLiveMode(AlivcLiveMode.AlivcLiveInteractiveMode) ? View.VISIBLE : View.GONE);

        String initUrl = PushDemoTestConstants.getTestPushUrl();
        if (!initUrl.isEmpty()) {
            mUrl.setText(initUrl);
        }

        mLivePushSettingView.showArgsContent(true);

        // 推拉裸流为定制功能，仅针对于有定制需求的客户开放使用；为避免客户理解错乱，故对外演示demo隐藏此入口。
        AppCompatRadioButton radioButton = findViewById(R.id.push_mode_raw_stream);
        radioButton.setVisibility(View.GONE);
    }

    private void setClick() {
        mLivePushSettingView.bitrateControl.observe(this, isChecked -> {
            mAlivcLivePushConfig.setEnableBitrateControl(isChecked);
        });
        mLivePushSettingView.targetVideoBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), bitrate);
        });
        mLivePushSettingView.minVideoBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintMinBit(getApplicationContext(), bitrate);
        });
        mLivePushSettingView.variableResolution.observe(this, isChecked -> {
            mAlivcLivePushConfig.setEnableAutoResolution(isChecked);
        });
        mLivePushSettingView.minFps.observe(this, minFps -> {
            mAlivcLivePushConfig.setMinFps(minFps);
        });
        mLivePushSettingView.audioSampleRate.observe(this, sampleRate -> {
            mAlivcLivePushConfig.setAudioSampleRate(sampleRate);
        });
        mLivePushSettingView.gop.observe(this, gop -> {
            mAlivcLivePushConfig.setVideoEncodeGop(gop);
        });
        mLivePushSettingView.fps.observe(this, fps -> {
            mFpsConfig = fps.getFps();
            mAlivcLivePushConfig.setFps(fps);
        });
        mLivePushSettingView.videoHardwareDecode.observe(this, isChecked -> {
            mAlivcLivePushConfig.setVideoEncodeMode(isChecked ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);
        });
        mLivePushSettingView.audioHardwareDecode.observe(this, isChecked -> {
            mAlivcLivePushConfig.setAudioEncodeMode(isChecked ? AlivcEncodeModeEnum.Encode_MODE_HARD : AlivcEncodeModeEnum.Encode_MODE_SOFT);
        });
        mLivePushSettingView.pushMirror.observe(this, isChecked -> {
            mAlivcLivePushConfig.setPushMirror(isChecked);
            SharedPreferenceUtils.setPushMirror(getApplicationContext(), isChecked);
        });
        mLivePushSettingView.previewMirror.observe(this, isChecked -> {
            mAlivcLivePushConfig.setPreviewMirror(isChecked);
            SharedPreferenceUtils.setPreviewMirror(getApplicationContext(), isChecked);
        });
        mLivePushSettingView.enableFrontCamera.observe(this, isChecked -> {
            mAlivcLivePushConfig.setCameraType(isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK);
            mCameraId = (isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.getCameraId() : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK.getCameraId());
        });
        mLivePushSettingView.resolution.observe(this, resolution -> {
            mCurrentResolution = resolution;
        });
        mLivePushSettingView.autoFocus.observe(this, isChecked -> {
            mAlivcLivePushConfig.setAutoFocus(isChecked);
            SharedPreferenceUtils.setAutofocus(getApplicationContext(), isChecked);
        });
        mLivePushSettingView.enableBeauty.observe(this, isChecked -> {
            SharedPreferenceUtils.setBeautyOn(getApplicationContext(), isChecked);
        });
        mLivePushSettingView.videoOnly.observe(this, isChecked -> {
            mVideoOnlyPush = isChecked;
            mAlivcLivePushConfig.setVideoOnly(mVideoOnlyPush);
        });
        mLivePushSettingView.audioOnly.observe(this, isChecked -> {
            mAudioOnlyPush = isChecked;
            mAlivcLivePushConfig.setAudioOnly(mAudioOnlyPush);
        });
        mLivePushSettingView.pauseImage.observe(this, isChecked -> {
            if (!isChecked) {
                mAlivcLivePushConfig.setPausePushImage("");
            } else {
                if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation()) {
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push_land.png");
                } else {
                    mAlivcLivePushConfig.setPausePushImage(getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
                }
            }
        });
        mLivePushSettingView.netWorkImage.observe(this, isChecked -> {
            if (!isChecked) {
                mAlivcLivePushConfig.setNetworkPoorPushImage("");
            } else {
                if (mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation() || mAlivcLivePushConfig.getPreviewOrientation() == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation()) {
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network_land.png");
                } else {
                    mAlivcLivePushConfig.setNetworkPoorPushImage(getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
                }
            }
        });

        mLivePushSettingView.async.observe(this, isChecked -> {
            mAsyncValue = isChecked;
        });
        mLivePushSettingView.musicMode.observe(this, isChecked -> {
            mAlivcLivePushConfig.setAudioSceneMode(isChecked ? AlivcAudioSceneModeEnum.AUDIO_SCENE_MUSIC_MODE : AlivcAudioSceneModeEnum.AUDIO_SCENE_DEFAULT_MODE);
        });
        mLivePushSettingView.extern.observe(this, isChecked -> {
            mAlivcLivePushConfig.setExternMainStream(isChecked, AlivcImageFormat.IMAGE_FORMAT_YUVNV12, AlivcSoundFormat.SOUND_FORMAT_S16);
            mAlivcLivePushConfig.setAudioChannels(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE);
            mAlivcLivePushConfig.setAudioSampleRate(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100);
            if (isChecked) {
                startDownloadYUV();
            }
        });
        mLivePushSettingView.localLog.observe(this, isChecked -> {
            if (isChecked) {
                LogcatHelper.getInstance(getApplicationContext()).start();
            } else {
                LogcatHelper.getInstance(getApplicationContext()).stop();
            }
        });

        mLivePushSettingView.previewDisplayMode.observe(this, previewDisplayMode -> {
            mAlivcLivePushConfig.setPreviewDisplayMode(previewDisplayMode);
            SharedPreferenceUtils.setDisplayFit(getApplicationContext(), previewDisplayMode.getPreviewDisplayMode());
        });

        mLivePushSettingView.audioChannel.observe(this, audioChannel -> {
            mAlivcLivePushConfig.setAudioChannels(audioChannel);
        });

        mLivePushSettingView.audioProfile.observe(this, audioProfile -> {
            mAlivcLivePushConfig.setAudioProfile(audioProfile);
        });

        mLivePushSettingView.videoEncodeType.observe(this, videoEncodeType -> {
            mAlivcLivePushConfig.setVideoEncodeType(videoEncodeType);
        });

        mLivePushSettingView.bFrame.observe(this, bFrame -> {
            mAlivcLivePushConfig.setBFrames(bFrame);
        });

        mLivePushSettingView.previewOrientation.observe(this, orientation -> {
            mAlivcLivePushConfig.setPreviewOrientation(orientation);
            mOrientationEnum = orientation;
        });

        mLivePushSettingView.pauseImagePath.observe(this, path -> {
            if (mAlivcLivePushConfig.getPausePushImage() == null || mAlivcLivePushConfig.getPausePushImage().equals("")) {
                mAlivcLivePushConfig.setPausePushImage(path);
            }
        });

        mLivePushSettingView.netWorkImagePath.observe(this, path -> {
            if (mAlivcLivePushConfig.getNetworkPoorPushImage() != null && !mAlivcLivePushConfig.getNetworkPoorPushImage().equals("")) {
                mAlivcLivePushConfig.setNetworkPoorPushImage(path);
            }
        });

        mLivePushSettingView.qualityMode.observe(this, quality -> {
            mAlivcLivePushConfig.setQualityMode(quality);
        });

        mLivePushSettingView.showWaterMark.observe(this, isShown -> {
            if (isShown) {
                PushWaterMarkDialog pushWaterMarkDialog = new PushWaterMarkDialog();
                pushWaterMarkDialog.setWaterMarkInfo(mWaterMarkInfos);
                pushWaterMarkDialog.show(getSupportFragmentManager(), "waterDialog");
            }
        });

        mPublish.setOnClickListener(onClickListener);
        mTabArgsLayout.setOnClickListener(onClickListener);
        mTabActionLayout.setOnClickListener(onClickListener);
        mQr.setOnClickListener(onClickListener);
        mBack.setOnClickListener(onClickListener);
        mLocalLogTv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                jump2InformationActivity();
                return true;
            }
        });

        mLivePushModeRadioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.push_mode_interaction) {
                //互动模式
                mAlivcLivePushConfig.setEnableRTSForInteractiveMode(false);
                mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
            } else if (i == R.id.push_mode_raw_stream) {
                //推拉裸流
                mAlivcLivePushConfig.setEnableRTSForInteractiveMode(true);
                mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveInteractiveMode);
            } else {
                //标准模式
                mAlivcLivePushConfig.setEnableRTSForInteractiveMode(false);
                mAlivcLivePushConfig.setLivePushMode(AlivcLiveMode.AlivcLiveBasicMode);
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
                    if (mLivePushSettingView.enableWaterMark()) {
                        waterMarkInfos.addAll(mWaterMarkInfos);
                    }
                    if (mCurrentResolution == AlivcResolutionEnum.RESOLUTION_SELF_DEFINE) {
                        AlivcResolutionEnum.RESOLUTION_SELF_DEFINE.setSelfDefineResolution(mLivePushSettingView.getSelfDefineResolutionWidth(),mLivePushSettingView.getSelfDefineResolutionHeight());
                        mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_SELF_DEFINE);
                    } else {
                        mAlivcLivePushConfig.setResolution(mCurrentResolution);
                    }

                    if (mUrl.getText().toString().contains("rtmp://") || mUrl.getText().toString().contains("artc://")) {
                        checkModelAndRun(new Runnable() {
                            @Override
                            public void run() {
                                LivePushActivity.startActivity(PushConfigActivity.this, mAlivcLivePushConfig, mUrl.getText().toString(),
                                        mAsyncValue, mAudioOnlyPush, mVideoOnlyPush, mOrientationEnum, mCameraId, isFlash, mAuthTimeStr,
                                        mPrivacyKeyStr, mMixStream, mAlivcLivePushConfig.isExternMainStream(), mLivePushSettingView.enableBeauty(), mFpsConfig,
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
            } else if (id == R.id.iv_back) {
                finish();
            } else if (id == R.id.tab_args_layout) {
                mTabArgsView.setVisibility(View.VISIBLE);
                mTabActionView.setVisibility(View.INVISIBLE);
                mLivePushSettingView.showArgsContent(true);
                mInteractionRelativeLayout.setVisibility(AlivcLiveBase.isSupportLiveMode(AlivcLiveMode.AlivcLiveInteractiveMode) ? View.VISIBLE : View.GONE);
            } else if (id == R.id.tab_action_layout) {
                mTabActionView.setVisibility(View.VISIBLE);
                mTabArgsView.setVisibility(View.INVISIBLE);
                mLivePushSettingView.showArgsContent(false);
                mInteractionRelativeLayout.setVisibility(View.GONE);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        mLivePushSettingView.setPushMirror(SharedPreferenceUtils.isPushMirror(getApplicationContext(),DEFAULT_VALUE_PUSH_MIRROR));
        mLivePushSettingView.setPreviewMirror(SharedPreferenceUtils.isPreviewMirror(getApplicationContext(), DEFAULT_VALUE_PREVIEW_MIRROR));
        mLivePushSettingView.setAutoFocus(SharedPreferenceUtils.isAutoFocus(getApplicationContext(), DEFAULT_VALUE_AUTO_FOCUS));
        mLivePushSettingView.setBeautyOn(SharedPreferenceUtils.isBeautyOn(getApplicationContext()));
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
        mAlivcLivePushConfig.setInitialVideoBitrate(Integer.parseInt(mLivePushSettingView.getInitVideoBitrate()));
        mAlivcLivePushConfig.setAudioBitRate(1000 * Integer.parseInt(mLivePushSettingView.getAudioBitrate()));

        mAlivcLivePushConfig.setMinVideoBitrate(Integer.parseInt(mLivePushSettingView.getMinVideoBitrate()));
        SharedPreferenceUtils.setMinBit(getApplicationContext(), Integer.parseInt(mLivePushSettingView.getMinVideoBitrate()));

        mAlivcLivePushConfig.setTargetVideoBitrate(Integer.parseInt(mLivePushSettingView.getTargetVideoBitrate()));
        SharedPreferenceUtils.setTargetBit(getApplicationContext(), Integer.parseInt(mLivePushSettingView.getTargetVideoBitrate()));

        mAlivcLivePushConfig.setConnectRetryCount(mLivePushSettingView.getRetryCount());
        mAlivcLivePushConfig.setConnectRetryInterval(mLivePushSettingView.getRetryInterval());

        mAuthTimeStr = mLivePushSettingView.getAuthTime();
        mPrivacyKeyStr = mLivePushSettingView.getPrivacyKey();

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
                if (!mLivePushSettingView.getTargetVideoBitrateOnlyEditText().isEmpty() || Integer.parseInt(mLivePushSettingView.getTargetVideoBitrateOnlyHint()) != SharedPreferenceUtils.getTargetBit(getApplicationContext())) {
                    mLivePushSettingView.setTargetVideoBitrateText(String.valueOf(SharedPreferenceUtils.getTargetBit(getApplicationContext())));
                }
                if (!mLivePushSettingView.getMinVideoBitrateOnlyEditText().isEmpty() || Integer.parseInt(mLivePushSettingView.getMinVideoBitrateOnlyHint()) != SharedPreferenceUtils.getMinBit(getApplicationContext())) {
                    mLivePushSettingView.setMinVideoBitrateText(String.valueOf(SharedPreferenceUtils.getMinBit(getApplicationContext())));
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
        mLivePushSettingView.destroy();
    }

    private void checkModelAndRun(final Runnable runnable) {
        runnable.run();
//        BeautyMenuMaterial.getInstance().checkModelReady(this, new OnDownloadUICallback() {
//            @Override
//            public void onDownloadStart(@StringRes int tipsResId) {
//                showProgressDialog(tipsResId);
//            }
//
//            @Override
//            public void onDownloadProgress(final float v) {
//            }
//
//            @Override
//            public void onDownloadSuccess() {
//                hideProgressDialog();
//                runnable.run();
//            }
//
//            @Override
//            public void onDownloadError(@StringRes int tipsResId) {
//                hideProgressDialog();
//                showErrorTips(tipsResId);
//            }
//        });
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
        //    private PushConfigDialogImpl mPushConfigDialog = new PushConfigDialogImpl();
        long mCaptureDownloadId = ResourcesDownload.downloadCaptureYUV(this, new OnDownloadListener() {
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
                    mLivePushSettingView.externDownloadError();
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
