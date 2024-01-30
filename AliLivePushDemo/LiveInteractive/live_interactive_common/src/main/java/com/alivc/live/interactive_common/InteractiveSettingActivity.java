package com.alivc.live.interactive_common;

import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mAlivcLivePushConfig;
import static com.alivc.live.interactive_common.utils.LivePushGlobalConfig.mWaterMarkInfos;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.baselive_common.Common;
import com.alivc.live.baselive_common.LivePushSettingView;
import com.alivc.live.baselive_common.PushWaterMarkDialog;
import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcAudioChannelEnum;
import com.alivc.live.pusher.AlivcAudioSampleRateEnum;
import com.alivc.live.pusher.AlivcAudioSceneModeEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcImageFormat;
import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcSoundFormat;
import com.alivc.live.pusher.WaterMarkInfo;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;

import java.io.File;

public class InteractiveSettingActivity extends AppCompatActivity {

    private View mTabArgsLayout;
    private View mTabActionLayout;
    private View mTabArgsView;
    private View mTabActionView;
    private ImageView mBackImageView;
    private Switch mMultiInteractSwitch;
    private Switch mH5CompatibleSwitch;
    private Switch mDataChannelSwitch;
    private Button mCommitButton;
    private AVLoadingDialog mLoadingDialog;

    private LivePushSettingView mLivePushSettingView;
    private AlivcResolutionEnum mCurrentResolution;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_setting);

        addWaterMarkInfo();
        initView();
        initData();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mCommitButton = findViewById(R.id.btn_commit);
        mMultiInteractSwitch = findViewById(R.id.multi_interaction_control);
        mMultiInteractSwitch.setChecked(LivePushGlobalConfig.IS_MULTI_INTERACT);
        mH5CompatibleSwitch = findViewById(R.id.h5_compatible_control);
        mH5CompatibleSwitch.setChecked(LivePushGlobalConfig.IS_H5_COMPATIBLE);
        mDataChannelSwitch = findViewById(R.id.data_channel_control);
        mDataChannelSwitch.setChecked(LivePushGlobalConfig.IS_DATA_CHANNEL_MESSAGE_ENABLE);

        mTabArgsLayout = findViewById(R.id.tab_args_layout);
        mTabActionLayout = findViewById(R.id.tab_action_layout);
        mTabArgsView = findViewById(R.id.tab_args_view);
        mTabActionView = findViewById(R.id.tab_action_view);
        mLivePushSettingView = findViewById(R.id.setting_view);
    }

    private void initData() {
        mLivePushSettingView.setDefaultConfig(mAlivcLivePushConfig, LivePushGlobalConfig.ENABLE_BEAUTY, LivePushGlobalConfig.ENABLE_LOCAL_LOG, LivePushGlobalConfig.ENABLE_WATER_MARK);
    }

    private void initListener() {
        mLivePushSettingView.bitrateControl.observe(this, isChecked -> {
            mAlivcLivePushConfig.setEnableBitrateControl(isChecked);
        });
        mLivePushSettingView.targetVideoBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintTargetBit(getApplicationContext(), bitrate);
            mAlivcLivePushConfig.setTargetVideoBitrate(bitrate);
        });
        mLivePushSettingView.minVideoBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintMinBit(getApplicationContext(), bitrate);
            mAlivcLivePushConfig.setMinVideoBitrate(bitrate);
        });
        mLivePushSettingView.initialVideoBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintMinBit(getApplicationContext(), bitrate);
            mAlivcLivePushConfig.setInitialVideoBitrate(bitrate);
        });
        mLivePushSettingView.audioBitrate.observe(this, bitrate -> {
            SharedPreferenceUtils.setHintMinBit(getApplicationContext(), bitrate);
            mAlivcLivePushConfig.setAudioBitRate(bitrate);
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
        });
        mLivePushSettingView.previewMirror.observe(this, isChecked -> {
            mAlivcLivePushConfig.setPreviewMirror(isChecked);
        });
        mLivePushSettingView.enableFrontCamera.observe(this, isChecked -> {
            mAlivcLivePushConfig.setCameraType(isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK);
//            mCameraId = (isChecked ? AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.getCameraId() : AlivcLivePushCameraTypeEnum.CAMERA_TYPE_BACK.getCameraId());
        });
        mLivePushSettingView.autoFocus.observe(this, isChecked -> {
            mAlivcLivePushConfig.setAutoFocus(isChecked);
        });
        mLivePushSettingView.enableBeauty.observe(this, isChecked -> {
            LivePushGlobalConfig.ENABLE_BEAUTY = isChecked;
        });
        mLivePushSettingView.videoOnly.observe(this, isChecked -> {
            mAlivcLivePushConfig.setVideoOnly(isChecked);
        });
        mLivePushSettingView.audioOnly.observe(this, isChecked -> {
            mAlivcLivePushConfig.setAudioOnly(isChecked);
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
            LivePushGlobalConfig.ENABLE_LOCAL_LOG = isChecked;
        });

        mLivePushSettingView.previewDisplayMode.observe(this, previewDisplayMode -> {
            mAlivcLivePushConfig.setPreviewDisplayMode(previewDisplayMode);
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
            LivePushGlobalConfig.ENABLE_WATER_MARK = isShown;
            if (isShown) {
                PushWaterMarkDialog pushWaterMarkDialog = new PushWaterMarkDialog();
                pushWaterMarkDialog.setWaterMarkInfo(mWaterMarkInfos);
                pushWaterMarkDialog.show(getSupportFragmentManager(), "waterDialog");
            }
        });
        mLivePushSettingView.resolution.observe(this, resolution -> {
            this.mCurrentResolution = resolution;
            if (resolution != AlivcResolutionEnum.RESOLUTION_SELF_DEFINE) {
                mAlivcLivePushConfig.setResolution(resolution);
            }
        });


        mTabArgsLayout.setOnClickListener(view -> {
            mTabArgsView.setVisibility(View.VISIBLE);
            mTabActionView.setVisibility(View.INVISIBLE);
            mLivePushSettingView.showArgsContent(true);
        });

        mTabActionLayout.setOnClickListener(view -> {
            mTabActionView.setVisibility(View.VISIBLE);
            mTabArgsView.setVisibility(View.INVISIBLE);
            mLivePushSettingView.showArgsContent(false);
        });

        mBackImageView.setOnClickListener(view -> {
            finish();
        });
        mCommitButton.setOnClickListener(view -> {
            if (mCurrentResolution == AlivcResolutionEnum.RESOLUTION_SELF_DEFINE) {
                AlivcResolutionEnum.RESOLUTION_SELF_DEFINE.setSelfDefineResolution(mLivePushSettingView.getSelfDefineResolutionWidth(), mLivePushSettingView.getSelfDefineResolutionHeight());
                mAlivcLivePushConfig.setResolution(AlivcResolutionEnum.RESOLUTION_SELF_DEFINE);
            }
            mAlivcLivePushConfig.setConnectRetryCount(mLivePushSettingView.getRetryCount());
            mAlivcLivePushConfig.setConnectRetryInterval(mLivePushSettingView.getRetryInterval());
            finish();
        });
        mMultiInteractSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LivePushGlobalConfig.IS_MULTI_INTERACT = isChecked;
            }
        });
        mH5CompatibleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LivePushGlobalConfig.IS_H5_COMPATIBLE = isChecked;
            }
        });
        mDataChannelSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                LivePushGlobalConfig.IS_DATA_CHANNEL_MESSAGE_ENABLE = isChecked;
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

    private void startDownloadYUV() {
        long mCaptureDownloadId = ResourcesDownload.downloadCaptureYUV(this, new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(long downloadId) {
                hideProgressDialog();
                AVToast.show(InteractiveSettingActivity.this, true, "Download Success");
            }

            @Override
            public void onDownloadProgress(long downloadId, double percent) {
            }

            @Override
            public void onDownloadError(long downloadId, int errorCode, String errorMsg) {
                hideProgressDialog();
                AVToast.show(InteractiveSettingActivity.this, true, errorMsg);
                if (errorCode != DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                    mLivePushSettingView.externDownloadError();
                }
            }
        });
        showProgressDialog(R.string.waiting_download_video_resources);
    }
}