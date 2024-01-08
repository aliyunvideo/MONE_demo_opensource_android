package com.alivc.live.baselive_common;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alivc.live.baselive_common.databinding.PushSettingViewLayoutBinding;
import com.alivc.live.pusher.AlivcAudioAACProfileEnum;
import com.alivc.live.pusher.AlivcAudioChannelEnum;
import com.alivc.live.pusher.AlivcAudioSampleRateEnum;
import com.alivc.live.pusher.AlivcAudioSceneModeEnum;
import com.alivc.live.pusher.AlivcEncodeModeEnum;
import com.alivc.live.pusher.AlivcEncodeType;
import com.alivc.live.pusher.AlivcFpsEnum;
import com.alivc.live.pusher.AlivcLivePushCameraTypeEnum;
import com.alivc.live.pusher.AlivcLivePushConfig;
import com.alivc.live.pusher.AlivcLivePushConstants;
import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcPreviewOrientationEnum;
import com.alivc.live.pusher.AlivcQualityModeEnum;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.alivc.live.pusher.AlivcVideoEncodeGopEnum;

import java.io.File;
import java.util.Locale;

public class LivePushSettingView extends LinearLayout {

    private PushSettingViewLayoutBinding mViewBinding;
    private PushConfigDialogImpl mPushConfigDialog = new PushConfigDialogImpl();

    //码率控制
    private final MutableLiveData<Boolean> mBitrateControlLiveData = new MutableLiveData<>();
    public LiveData<Boolean> bitrateControl = mBitrateControlLiveData;

    //可变分辨率
    private final MutableLiveData<Boolean> mVariableResolutionLiveData = new MutableLiveData<>();
    public LiveData<Boolean> variableResolution = mVariableResolutionLiveData;

    //分辨率
    private final MutableLiveData<AlivcResolutionEnum> mResolutionLiveData = new MutableLiveData<>();
    public LiveData<AlivcResolutionEnum> resolution = mResolutionLiveData;

    //最小帧率
    private final MutableLiveData<AlivcFpsEnum> mMinFpsLiveData = new MutableLiveData<>();
    public LiveData<AlivcFpsEnum> minFps = mMinFpsLiveData;

    //音频采样率
    private final MutableLiveData<AlivcAudioSampleRateEnum> mAudioSampleRateLiveData = new MutableLiveData<>();
    public LiveData<AlivcAudioSampleRateEnum> audioSampleRate = mAudioSampleRateLiveData;

    //GOP
    private final MutableLiveData<AlivcVideoEncodeGopEnum> mGopLiveData = new MutableLiveData<>();
    public LiveData<AlivcVideoEncodeGopEnum> gop = mGopLiveData;

    //FPS
    private final MutableLiveData<AlivcFpsEnum> mFpsLiveData = new MutableLiveData<>();
    public LiveData<AlivcFpsEnum> fps = mFpsLiveData;

    //视频硬编码
    private final MutableLiveData<Boolean> mVideoHardwareDecodeLiveData = new MutableLiveData<>();
    public LiveData<Boolean> videoHardwareDecode = mVideoHardwareDecodeLiveData;
    //音频硬编码
    private final MutableLiveData<Boolean> mAudioHardwareDecodeLiveData = new MutableLiveData<>();
    public LiveData<Boolean> audioHardwareDecode = mAudioHardwareDecodeLiveData;

    //推流镜像
    private final MutableLiveData<Boolean> mPushMirrorLiveData = new MutableLiveData<>();
    public LiveData<Boolean> pushMirror = mPushMirrorLiveData;

    //预览镜像
    private final MutableLiveData<Boolean> mPreviewMirrorLiveData = new MutableLiveData<>();
    public LiveData<Boolean> previewMirror = mPreviewMirrorLiveData;

    //前置摄像头
    private final MutableLiveData<Boolean> mEnableFrontCamera = new MutableLiveData<>();
    public LiveData<Boolean> enableFrontCamera = mEnableFrontCamera;

    //美颜
    private final MutableLiveData<Boolean> mEnableBeauty = new MutableLiveData<>();
    public LiveData<Boolean> enableBeauty = mEnableBeauty;

    //暂停图片
    private final MutableLiveData<Boolean> mPauseImageLiveData = new MutableLiveData<>();
    public LiveData<Boolean> pauseImage = mPauseImageLiveData;

    //网络差图片
    private final MutableLiveData<Boolean> mNetWorkImageLiveData = new MutableLiveData<>();
    public LiveData<Boolean> netWorkImage = mNetWorkImageLiveData;

    //自动对焦
    private final MutableLiveData<Boolean> mAutoFocusLiveData = new MutableLiveData<>();
    public LiveData<Boolean> autoFocus = mAutoFocusLiveData;

    //异步接口
    private final MutableLiveData<Boolean> mAsyncLiveData = new MutableLiveData<>();
    public LiveData<Boolean> async = mAutoFocusLiveData;

    //音乐模式
    private final MutableLiveData<Boolean> mMusicModeLiveData = new MutableLiveData<>();
    public LiveData<Boolean> musicMode = mMusicModeLiveData;

    //外部音视频
    private final MutableLiveData<Boolean> mExternLiveData = new MutableLiveData<>();
    public LiveData<Boolean> extern = mExternLiveData;

    //本地日志
    private final MutableLiveData<Boolean> mLocalLogLiveData = new MutableLiveData<>();
    public LiveData<Boolean> localLog = mLocalLogLiveData;

    //仅视频
    private final MutableLiveData<Boolean> mVideoOnlyLiveData = new MutableLiveData<>();
    public LiveData<Boolean> videoOnly = mVideoOnlyLiveData;

    //仅音频
    private final MutableLiveData<Boolean> mAudioOnlyLiveData = new MutableLiveData<>();
    public LiveData<Boolean> audioOnly = mAudioOnlyLiveData;

    //预览显示模式
    private final MutableLiveData<AlivcPreviewDisplayMode> mPreviewDisplayModeLiveData = new MutableLiveData<>();
    public LiveData<AlivcPreviewDisplayMode> previewDisplayMode = mPreviewDisplayModeLiveData;

    //声道
    private final MutableLiveData<AlivcAudioChannelEnum> mAudioChannelLiveData = new MutableLiveData<>();
    public LiveData<AlivcAudioChannelEnum> audioChannel = mAudioChannelLiveData;

    //音频编码
    private final MutableLiveData<AlivcAudioAACProfileEnum> mAudioProfileLiveData = new MutableLiveData<>();
    public LiveData<AlivcAudioAACProfileEnum> audioProfile = mAudioProfileLiveData;

    //音频编码
    private final MutableLiveData<AlivcEncodeType> mVideoEncodeTypeLiveData = new MutableLiveData<>();
    public LiveData<AlivcEncodeType> videoEncodeType = mVideoEncodeTypeLiveData;

    //B-Frame
    private final MutableLiveData<Integer> mBFrameLiveData = new MutableLiveData<>();
    public LiveData<Integer> bFrame = mBFrameLiveData;

    //目标码率
    private final MutableLiveData<Integer> mTargetVideoBitrate = new MutableLiveData<>();
    public LiveData<Integer> targetVideoBitrate = mTargetVideoBitrate;

    //最小视频码率
    private final MutableLiveData<Integer> mMinVideoBitrate = new MutableLiveData<>();
    public LiveData<Integer> minVideoBitrate = mMinVideoBitrate;

    //暂停图片路径
    private final MutableLiveData<String> mPauseImgPathLiveData = new MutableLiveData<>();
    public LiveData<String> pauseImagePath = mPauseImgPathLiveData;

    //网络差图片路径
    private final MutableLiveData<String> mNetWorkImgPathLiveData = new MutableLiveData<>();
    public LiveData<String> netWorkImagePath = mNetWorkImgPathLiveData;

    //屏幕方向
    private final MutableLiveData<AlivcPreviewOrientationEnum> mPreviewOrientationLiveData = new MutableLiveData<>();
    public LiveData<AlivcPreviewOrientationEnum> previewOrientation = mPreviewOrientationLiveData;

    //视频质量
    private final MutableLiveData<AlivcQualityModeEnum> mQualityModeLiveData = new MutableLiveData<>();
    public LiveData<AlivcQualityModeEnum> qualityMode = mQualityModeLiveData;

    //水印 Dialog
    private final MutableLiveData<Boolean> mShowWaterMarkDialog = new MutableLiveData<>();
    public LiveData<Boolean> showWaterMark = mShowWaterMarkDialog;

    private AlivcQualityModeEnum mCurrentQualityMode = AlivcQualityModeEnum.QM_RESOLUTION_FIRST;
    private AlivcResolutionEnum mCurrentResolution = AlivcResolutionEnum.RESOLUTION_540P;

    private int mPushModeDefaultIndex = 0;
    private int mDisplayModeDefaultIndex = 0;
    private int mAudioChannelDefaultIndex = 1;
    private int mAudioProfileDefaultIndex = 0;
    private int mQualityModeDefaultIndex = 0;
    private int mVideoEncoderTypeDefaultIndex = 0;
    private int mBFrameDefaultIndex = 0;
    private int mPreviewOrientationDefaultIndex = 0;

    public LivePushSettingView(Context context) {
        this(context, null);
    }

    public LivePushSettingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LivePushSettingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mViewBinding = PushSettingViewLayoutBinding.inflate(LayoutInflater.from(context), this, true);
        initData();
        initListener();
    }

    private void initData() {
        mViewBinding.pushArgsSetting.targetRateEdit.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
        mViewBinding.pushArgsSetting.minRateEdit.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate()));
        mViewBinding.pushArgsSetting.initRateEdit.setHint(String.valueOf(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate()));
        turnOnBitRateFps(false);
    }

    private void initListener() {
        //切换分辨率
        mViewBinding.pushArgsSetting.resolutionView.setOnResolutionChangedListener(resolutionEnum -> {
            mCurrentResolution = resolutionEnum;
            mResolutionLiveData.setValue(resolutionEnum);
            if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_180P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_240P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_360P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_480P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_540P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_720P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else if (resolutionEnum == AlivcResolutionEnum.RESOLUTION_1080P) {
                if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_RESOLUTION_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else if (mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_FLUENCY_FIRST)) {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                } else {
                    changeVideoBitrateWithResolution(AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_1080P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate());
                }
            } else {
                //自定义
                changeVideoBitrateWithResolution(AlivcLivePushConstants.DEFAULT_VALUE_INT_TARGET_BITRATE,
                        AlivcLivePushConstants.DEFAULT_VALUE_INT_MIN_BITRATE,
                        AlivcLivePushConstants.DEFAULT_VALUE_INT_INIT_BITRATE);
            }
        });
        //最小帧率
        mViewBinding.pushArgsSetting.minFpsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= LivePushProgressStep.PROGRESS_0) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_8);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_8.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_16) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_10);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_10.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_33) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_12);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_12.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_50) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_15);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_15.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_66) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_20);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_20.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_80) {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_25);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_25.getFps()));
                } else {
                    mMinFpsLiveData.setValue(AlivcFpsEnum.FPS_30);
                    mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(AlivcFpsEnum.FPS_30.getFps()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //fps
        mViewBinding.pushArgsSetting.fpsSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!mCurrentQualityMode.equals(AlivcQualityModeEnum.QM_CUSTOM)) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_25);
                    mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(83);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_25.getFps()));
                    return;
                }
                if (progress <= LivePushProgressStep.PROGRESS_0) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_8);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_8.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_16) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_10);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_10.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_33) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_12);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_12.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_50) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_15);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_15.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_66) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_20);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_20.getFps()));
                } else if (progress <= LivePushProgressStep.PROGRESS_80) {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_25);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_25.getFps()));
                } else {
                    mFpsLiveData.setValue(AlivcFpsEnum.FPS_30);
                    mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_30.getFps()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= LivePushProgressStep.PROGRESS_0) {
                    seekBar.setProgress(0);
                } else if (progress <= LivePushProgressStep.PROGRESS_16) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_16);
                } else if (progress <= LivePushProgressStep.PROGRESS_33) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_33);
                } else if (progress <= LivePushProgressStep.PROGRESS_50) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_50);
                } else if (progress <= LivePushProgressStep.PROGRESS_66) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_66);
                } else if (progress <= LivePushProgressStep.PROGRESS_80) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_80);
                } else {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_100);
                }
            }
        });

        //音频采样率
        mViewBinding.pushArgsSetting.audioRateSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= LivePushProgressStep.PROGRESS_AUDIO_160) {
                    mAudioSampleRateLiveData.setValue(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_16000);
                    mViewBinding.pushArgsSetting.audioRateText.setText(getContext().getString(R.string.setting_audio_160));
                } else if (progress <= LivePushProgressStep.PROGRESS_AUDIO_320) {
                    mAudioSampleRateLiveData.setValue(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000);
                    mViewBinding.pushArgsSetting.audioRateText.setText(getContext().getString(R.string.setting_audio_320));
                } else if (progress <= LivePushProgressStep.PROGRESS_AUDIO_441) {
                    mAudioSampleRateLiveData.setValue(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100);
                    mViewBinding.pushArgsSetting.audioRateText.setText(getContext().getString(R.string.setting_audio_441));
                } else {
                    mAudioSampleRateLiveData.setValue(AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_48000);
                    mViewBinding.pushArgsSetting.audioRateText.setText(getContext().getString(R.string.setting_audio_480));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= LivePushProgressStep.PROGRESS_AUDIO_160) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_160);
                } else if (progress <= LivePushProgressStep.PROGRESS_AUDIO_320) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_320);
                } else if (progress <= LivePushProgressStep.PROGRESS_AUDIO_441) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_441);
                } else {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_480);
                }
            }
        });

        //GOP
        mViewBinding.pushArgsSetting.gopSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= LivePushProgressStep.PROGRESS_20) {
                    mGopLiveData.setValue(AlivcVideoEncodeGopEnum.GOP_ONE);
                    mViewBinding.pushArgsSetting.gopText.setText("1/s");
                } else if (progress <= LivePushProgressStep.PROGRESS_40) {
                    mGopLiveData.setValue(AlivcVideoEncodeGopEnum.GOP_TWO);
                    mViewBinding.pushArgsSetting.gopText.setText("2/s");
                } else if (progress <= LivePushProgressStep.PROGRESS_60) {
                    mGopLiveData.setValue(AlivcVideoEncodeGopEnum.GOP_THREE);
                    mViewBinding.pushArgsSetting.gopText.setText("3/s");
                } else if (progress <= LivePushProgressStep.PROGRESS_80) {
                    mGopLiveData.setValue(AlivcVideoEncodeGopEnum.GOP_FOUR);
                    mViewBinding.pushArgsSetting.gopText.setText("4/s");
                } else if (progress <= LivePushProgressStep.PROGRESS_100) {
                    mGopLiveData.setValue(AlivcVideoEncodeGopEnum.GOP_FIVE);
                    mViewBinding.pushArgsSetting.gopText.setText("5/s");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= 20) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_20);
                } else if (progress <= 40) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_40);
                } else if (progress <= 60) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_60);
                } else if (progress <= 80) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_80);
                } else if (progress <= 100) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_100);
                }
            }
        });

        //码率控制
        mViewBinding.pushArgsSetting.bitrateControl.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mBitrateControlLiveData.setValue(isChecked);
        });
        //可变分辨率
        mViewBinding.pushArgsSetting.variableResolution.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mVariableResolutionLiveData.setValue(isChecked);
        });
        //高级设置
        mViewBinding.pushArgsSetting.advanceConfig.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mViewBinding.pushArgsSetting.advanceLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
        //显示模式
        mViewBinding.pushArgsSetting.qualityModes.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushArgsSetting.qualityModes, mQualityListener, mQualityModeDefaultIndex);
        });
        //音频编码
        mViewBinding.pushArgsSetting.audioProfiles.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushArgsSetting.audioProfiles, mAudioProfilesListener, mAudioProfileDefaultIndex);
        });
        //声道
        mViewBinding.pushArgsSetting.audioChannel.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushArgsSetting.audioChannel, mAudioChannelListener, mAudioChannelDefaultIndex);
        });
        //视频编码
        mViewBinding.pushArgsSetting.videoEncoderType.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushArgsSetting.videoEncoderType, mEncoderTypeListener, mVideoEncoderTypeDefaultIndex);
        });
        //B帧
        mViewBinding.pushArgsSetting.bFrameNum.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushArgsSetting.bFrameNum, mFrameNumListener, mBFrameDefaultIndex);
        });
        //视频硬编码
        mViewBinding.pushArgsSetting.hardSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mVideoHardwareDecodeLiveData.setValue(isChecked);
        });
        //音频硬编码
        mViewBinding.pushArgsSetting.audioHardenc.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mAudioHardwareDecodeLiveData.setValue(isChecked);
        });

        //推流方向
        mViewBinding.pushFunctionSetting.mainOrientation.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushFunctionSetting.mainOrientation, mOrientationListener, mPreviewOrientationDefaultIndex);
        });
        //显示模式
        mViewBinding.pushFunctionSetting.settingDisplayMode.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushFunctionSetting.settingDisplayMode, mDisplayModeListener, mDisplayModeDefaultIndex);
        });
        //水印
        mViewBinding.pushFunctionSetting.watermarkSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mViewBinding.pushFunctionSetting.waterPosition.setClickable(isChecked);
            mViewBinding.pushFunctionSetting.waterPosition.setTextColor(isChecked ? getResources().getColor(R.color.colourful_text_strong) : getResources().getColor(R.color.text_ultraweak));
        });
        //水印位置
        mViewBinding.pushFunctionSetting.waterPosition.setOnClickListener(view -> {
            mShowWaterMarkDialog.setValue(mViewBinding.pushFunctionSetting.watermarkSwitch.isChecked());
        });
        //推流镜像
        mViewBinding.pushFunctionSetting.pushMirrorSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mPushMirrorLiveData.setValue(isChecked);
        });
        //预览镜像
        mViewBinding.pushFunctionSetting.previewMirrorSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mPreviewMirrorLiveData.setValue(isChecked);
        });
        //摄像头
        mViewBinding.pushFunctionSetting.cameraSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mEnableFrontCamera.setValue(isChecked);
        });
        //自动对焦
        mViewBinding.pushFunctionSetting.autofocusSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mAutoFocusLiveData.setValue(isChecked);
        });
        //美颜
        mViewBinding.pushFunctionSetting.beautyOnSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mEnableBeauty.setValue(isChecked);
        });
        //暂停推图片
        mViewBinding.pushFunctionSetting.pauseImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mPauseImageLiveData.setValue(isChecked);
        });
        //网络差图片
        mViewBinding.pushFunctionSetting.networkImage.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mNetWorkImageLiveData.setValue(isChecked);
        });
        //推流模式
        mViewBinding.pushFunctionSetting.pushMode.setOnClickListener(view -> {
            mPushConfigDialog.showConfigDialog(mViewBinding.pushFunctionSetting.pushMode, mPushModeListener, mPushModeDefaultIndex);
        });
        //有效时长,鉴权 key
        //异步接口
        mViewBinding.pushFunctionSetting.asyncSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mAsyncLiveData.setValue(isChecked);
        });
        //音乐模式
        mViewBinding.pushFunctionSetting.musicModeSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mMusicModeLiveData.setValue(isChecked);
        });
        //外部音视频
        mViewBinding.pushFunctionSetting.externVideo.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mExternLiveData.setValue(isChecked);
            // 当前外部音视频的资源是720P，因此推流分辨率对应调整为720P
            mViewBinding.pushArgsSetting.resolutionView.setResolution(AlivcResolutionEnum.RESOLUTION_720P);
        });
        //本地日志
        mViewBinding.pushFunctionSetting.logSwitch.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            mLocalLogLiveData.setValue(isChecked);
        });
    }

    public void showArgsContent(boolean isShow) {
        mViewBinding.pushArgsSetting.getRoot().setVisibility(isShow ? View.VISIBLE : View.GONE);
        mViewBinding.pushFunctionSetting.getRoot().setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mQualityListener = new PushConfigBottomSheet.OnPushConfigSelectorListener() {
        @Override
        public void confirm(String tips, int i) {
            mViewBinding.pushArgsSetting.qualityModes.setText(tips);
            if (AlivcQualityModeEnum.QM_CUSTOM.equals(mCurrentQualityMode)) {
                mViewBinding.pushArgsSetting.targetRateEdit.setText("");
                mViewBinding.pushArgsSetting.minRateEdit.setText("");
                mViewBinding.pushArgsSetting.initRateEdit.setText("");
                mViewBinding.pushArgsSetting.audioBitrate.setText("");
            }
            if (i == 0) {
                mCurrentQualityMode = AlivcQualityModeEnum.QM_RESOLUTION_FIRST;
                mQualityModeLiveData.setValue(AlivcQualityModeEnum.QM_RESOLUTION_FIRST);
                if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_RESOLUTION_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                }
                turnOnBitRateFps(false);
            } else if (i == 1) {
                mCurrentQualityMode = AlivcQualityModeEnum.QM_FLUENCY_FIRST;
                mQualityModeLiveData.setValue(AlivcQualityModeEnum.QM_FLUENCY_FIRST);
                if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P_FLUENCY_FIRST.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                }
                turnOnBitRateFps(false);
            } else if (i == 2) {
                mCurrentQualityMode = AlivcQualityModeEnum.QM_CUSTOM;
                mQualityModeLiveData.setValue(AlivcQualityModeEnum.QM_CUSTOM);
                if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_180P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_180P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_240P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_240P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_360P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_360P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_480P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_480P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_540P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_540P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                } else if (mCurrentResolution.equals(AlivcResolutionEnum.RESOLUTION_720P)) {
                    changeVideoBitrateWithResolution(
                            AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_TARGET_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_MIN_BITRATE.getBitrate(),
                            AlivcLivePushConstants.BITRATE_720P.DEFAULT_VALUE_INT_INIT_BITRATE.getBitrate()
                    );
                }
                turnOnBitRateFps(true);
            }
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mAudioProfilesListener = (data, index) -> {
        if (index == 0) {
            mAudioProfileLiveData.setValue(AlivcAudioAACProfileEnum.AAC_LC);
        } else if (index == 1) {
            mAudioProfileLiveData.setValue(AlivcAudioAACProfileEnum.HE_AAC);
        } else if (index == 2) {
            mAudioProfileLiveData.setValue(AlivcAudioAACProfileEnum.HE_AAC_v2);
        } else if (index == 3) {
            mAudioProfileLiveData.setValue(AlivcAudioAACProfileEnum.AAC_LD);
        }
    };

    private void turnOnBitRateFps(boolean on) {
        if (!on) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(83);
            mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(AlivcFpsEnum.FPS_25.getFps()));
            mViewBinding.pushArgsSetting.targetRateEdit.setFocusable(false);
            mViewBinding.pushArgsSetting.minRateEdit.setFocusable(false);
            mViewBinding.pushArgsSetting.initRateEdit.setFocusable(false);
            mViewBinding.pushArgsSetting.fpsSeekbar.setFocusable(false);
            mViewBinding.pushArgsSetting.targetRateEdit.setFocusableInTouchMode(false);
            mViewBinding.pushArgsSetting.minRateEdit.setFocusableInTouchMode(false);
            mViewBinding.pushArgsSetting.initRateEdit.setFocusableInTouchMode(false);
            mViewBinding.pushArgsSetting.fpsSeekbar.setFocusableInTouchMode(false);
        } else {
            mViewBinding.pushArgsSetting.targetRateEdit.setFocusable(true);
            mViewBinding.pushArgsSetting.minRateEdit.setFocusable(true);
            mViewBinding.pushArgsSetting.initRateEdit.setFocusable(true);
            mViewBinding.pushArgsSetting.targetRateEdit.setFocusableInTouchMode(true);
            mViewBinding.pushArgsSetting.minRateEdit.setFocusableInTouchMode(true);
            mViewBinding.pushArgsSetting.initRateEdit.setFocusableInTouchMode(true);
            mViewBinding.pushArgsSetting.targetRateEdit.requestFocus();
            mViewBinding.pushArgsSetting.initRateEdit.requestFocus();
            mViewBinding.pushArgsSetting.minRateEdit.requestFocus();
        }
    }

    private void changeVideoBitrateWithResolution(int targetBitrate, int minBitrate, int initBitrate) {
        mViewBinding.pushArgsSetting.targetRateEdit.setHint(String.valueOf(targetBitrate));
        mViewBinding.pushArgsSetting.minRateEdit.setHint(String.valueOf(minBitrate));
        mViewBinding.pushArgsSetting.initRateEdit.setHint(String.valueOf(initBitrate));

        mTargetVideoBitrate.setValue(targetBitrate);
        mMinVideoBitrate.setValue(minBitrate);
    }

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mAudioChannelListener = (data, index) -> {
        if (index == 0) {
            mAudioChannelLiveData.setValue(AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE);
        } else if (index == 1) {
            mAudioChannelLiveData.setValue(AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO);
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mEncoderTypeListener = (data, index) -> {
        if (index == 0) {
            mVideoEncodeTypeLiveData.setValue(AlivcEncodeType.Encode_TYPE_H264);
        } else if (index == 1) {
            mVideoEncodeTypeLiveData.setValue(AlivcEncodeType.Encode_TYPE_H265);
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mFrameNumListener = (data, index) -> {
        if (index == 0) {
            mBFrameLiveData.setValue(0);
        } else if (index == 1) {
            mBFrameLiveData.setValue(1);
        } else if (index == 2) {
            mBFrameLiveData.setValue(3);
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mOrientationListener = (data, index) -> {
        if (index == 0) {
            mPreviewOrientationLiveData.setValue(AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT);
        } else if (index == 1) {
            mPreviewOrientationLiveData.setValue(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT);
        } else if (index == 2) {
            mPreviewOrientationLiveData.setValue(AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT);
        }
        mPauseImgPathLiveData.setValue(getContext().getFilesDir().getPath() + File.separator + "alivc_resource/background_push.png");
        mNetWorkImgPathLiveData.setValue(getContext().getFilesDir().getPath() + File.separator + "alivc_resource/poor_network.png");
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mDisplayModeListener = (data, index) -> {
        if (index == 0) {
            mPreviewDisplayModeLiveData.setValue(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL);
        } else if (index == 1) {
            mPreviewDisplayModeLiveData.setValue(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT);
        } else if (index == 2) {
            mPreviewDisplayModeLiveData.setValue(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL);
        }
    };

    private final PushConfigBottomSheet.OnPushConfigSelectorListener mPushModeListener = (data, index) -> {
        boolean mAudioOnlyPush = (index == 1);
        boolean mVideoOnlyPush = (index == 2);
        mAudioOnlyLiveData.setValue(mAudioOnlyPush);
        mVideoOnlyLiveData.setValue(mVideoOnlyPush);
    };

    public String getInitVideoBitrate() {
        String initVideoBitrate = mViewBinding.pushArgsSetting.initRateEdit.getText().toString();
        if (TextUtils.isEmpty(initVideoBitrate)) {
            return mViewBinding.pushArgsSetting.initRateEdit.getHint().toString();
        }
        return initVideoBitrate;
    }

    public String getAudioBitrate() {
        String audioBitrate = mViewBinding.pushArgsSetting.audioBitrate.getText().toString();
        if (TextUtils.isEmpty(audioBitrate)) {
            return mViewBinding.pushArgsSetting.audioBitrate.getHint().toString();
        }
        return audioBitrate;
    }

    public String getMinVideoBitrate() {
        String minVideoBitrate = mViewBinding.pushArgsSetting.minRateEdit.getText().toString();
        if (TextUtils.isEmpty(minVideoBitrate)) {
            return mViewBinding.pushArgsSetting.minRateEdit.getHint().toString();
        }
        return minVideoBitrate;
    }

    public String getMinVideoBitrateOnlyEditText() {
        return mViewBinding.pushArgsSetting.minRateEdit.getText().toString();
    }

    public String getMinVideoBitrateOnlyHint() {
        return mViewBinding.pushArgsSetting.minRateEdit.getHint().toString();
    }

    public void setTargetVideoBitrateText(String bitrate) {
        mViewBinding.pushArgsSetting.targetRateEdit.setText(bitrate);
    }

    public void setMinVideoBitrateText(String bitrate) {
        mViewBinding.pushArgsSetting.minRateEdit.setText(bitrate);
    }

    public String getTargetVideoBitrate() {
        String targetVideoBitrate = mViewBinding.pushArgsSetting.targetRateEdit.getText().toString();
        if (TextUtils.isEmpty(targetVideoBitrate)) {
            return mViewBinding.pushArgsSetting.targetRateEdit.getHint().toString();
        }
        return targetVideoBitrate;
    }

    public String getTargetVideoBitrateOnlyEditText() {
        return mViewBinding.pushArgsSetting.targetRateEdit.getText().toString();
    }

    public String getTargetVideoBitrateOnlyHint() {
        return mViewBinding.pushArgsSetting.targetRateEdit.getHint().toString();
    }

    public int getRetryCount() {
        String retryCount = mViewBinding.pushFunctionSetting.retryCount.getText().toString();
        if (TextUtils.isEmpty(retryCount)) {
            return AlivcLivePushConstants.DEFAULT_VALUE_INT_AUDIO_RETRY_COUNT;
        }
        return Integer.parseInt(retryCount);
    }

    public int getRetryInterval() {
        String retryInterval = mViewBinding.pushFunctionSetting.retryInterval.getText().toString();
        if (TextUtils.isEmpty(retryInterval)) {
            return AlivcLivePushConstants.DEFAULT_VALUE_INT_RETRY_INTERVAL;
        }
        return Integer.parseInt(retryInterval);
    }

    public String getAuthTime() {
        return mViewBinding.pushFunctionSetting.authTime.getText().toString();
    }

    public String getPrivacyKey() {
        return mViewBinding.pushFunctionSetting.privacyKey.getText().toString();
    }

    public void externDownloadError() {
        mViewBinding.pushFunctionSetting.externVideo.setChecked(false);
    }

    public boolean enableWaterMark() {
        return mViewBinding.pushFunctionSetting.watermarkSwitch.isChecked();
    }

    public void setPushMirror(boolean isChecked) {
        mViewBinding.pushFunctionSetting.pushMirrorSwitch.setChecked(isChecked);
    }

    public void setPreviewMirror(boolean isChecked) {
        mViewBinding.pushFunctionSetting.previewMirrorSwitch.setChecked(isChecked);
    }

    public void setAutoFocus(boolean isChecked) {
        mViewBinding.pushFunctionSetting.autofocusSwitch.setChecked(isChecked);
    }

    public void setBeautyOn(boolean isChecked) {
        mViewBinding.pushFunctionSetting.beautyOnSwitch.setChecked(isChecked);
    }

    public boolean enableBeauty() {
        return mViewBinding.pushFunctionSetting.beautyOnSwitch.isChecked();
    }

    public int getSelfDefineResolutionWidth() {
        return mViewBinding.pushArgsSetting.resolutionView.getSelfDefineWidth();
    }

    public int getSelfDefineResolutionHeight() {
        return mViewBinding.pushArgsSetting.resolutionView.getSelfDefineHeight();
    }

    public void destroy() {
        mPushConfigDialog.destroy();
    }

    public void setDefaultConfig(AlivcLivePushConfig alivcLivePushConfig,boolean isEnableBeauty,boolean isEnableLocalLog,boolean isEnableWaterMark) {
        mViewBinding.pushArgsSetting.bitrateControl.setChecked(alivcLivePushConfig.isEnableBitrateControl());
        mViewBinding.pushArgsSetting.targetRateEdit.setText(String.valueOf(alivcLivePushConfig.getTargetVideoBitrate()));
        mViewBinding.pushArgsSetting.minRateEdit.setText(String.valueOf(alivcLivePushConfig.getMinVideoBitrate()));
        mViewBinding.pushArgsSetting.variableResolution.setChecked(alivcLivePushConfig.isEnableAutoResolution());
        AlivcResolutionEnum configResolution = alivcLivePushConfig.getResolution();
        mViewBinding.pushArgsSetting.resolutionView.setResolution(configResolution);
        int audioChannels = alivcLivePushConfig.getAudioChannels();
        if (audioChannels == AlivcAudioChannelEnum.AUDIO_CHANNEL_ONE.getChannelCount()) {
            mAudioChannelDefaultIndex = 0;
            mViewBinding.pushArgsSetting.audioChannel.setText(getResources().getString(R.string.single_track));
        } else if ((audioChannels == AlivcAudioChannelEnum.AUDIO_CHANNEL_TWO.getChannelCount())) {
            mAudioChannelDefaultIndex = 1;
            mViewBinding.pushArgsSetting.audioChannel.setText(getResources().getString(R.string.dual_track));
        }
        AlivcAudioAACProfileEnum configAudioProfile = alivcLivePushConfig.getAudioProfile();
        if (configAudioProfile == AlivcAudioAACProfileEnum.AAC_LC) {
            mAudioProfileDefaultIndex = 0;
            mViewBinding.pushArgsSetting.audioProfiles.setText(getResources().getString(R.string.setting_audio_aac_lc));
        } else if (configAudioProfile == AlivcAudioAACProfileEnum.HE_AAC) {
            mAudioProfileDefaultIndex = 1;
            mViewBinding.pushArgsSetting.audioProfiles.setText(getResources().getString(R.string.setting_audio_aac_he));
        } else if (configAudioProfile == AlivcAudioAACProfileEnum.HE_AAC_v2) {
            mAudioProfileDefaultIndex = 2;
            mViewBinding.pushArgsSetting.audioProfiles.setText(getResources().getString(R.string.setting_audio_aac_hev2));
        } else if (configAudioProfile == AlivcAudioAACProfileEnum.AAC_LD) {
            mAudioProfileDefaultIndex = 3;
            mViewBinding.pushArgsSetting.audioProfiles.setText(getResources().getString(R.string.setting_audio_aac_ld));
        }
        mViewBinding.pushArgsSetting.minFpsText.setText(String.valueOf(alivcLivePushConfig.getMinFps()));
        int configMinFps = alivcLivePushConfig.getMinFps();
        if (configMinFps == AlivcFpsEnum.FPS_8.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_0);
        } else if (configMinFps == AlivcFpsEnum.FPS_10.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_16);
        } else if (configMinFps == AlivcFpsEnum.FPS_12.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_33);
        } else if (configMinFps == AlivcFpsEnum.FPS_15.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_50);
        } else if (configMinFps == AlivcFpsEnum.FPS_20.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_66);
        } else if (configMinFps == AlivcFpsEnum.FPS_25.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_80);
        } else if (configMinFps == AlivcFpsEnum.FPS_30.getFps()) {
            mViewBinding.pushArgsSetting.minFpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_100);
        }
        AlivcAudioSampleRateEnum configAudioSampleRate = alivcLivePushConfig.getAudioSampleRate();
        if (configAudioSampleRate == AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_16000) {
            mViewBinding.pushArgsSetting.audioRateSeekbar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_160);
            mViewBinding.pushArgsSetting.audioRateText.setText(getResources().getString(R.string.setting_audio_160));
        } else if (configAudioSampleRate == AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_32000) {
            mViewBinding.pushArgsSetting.audioRateSeekbar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_320);
            mViewBinding.pushArgsSetting.audioRateText.setText(getResources().getString(R.string.setting_audio_320));
        } else if (configAudioSampleRate == AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_44100) {
            mViewBinding.pushArgsSetting.audioRateSeekbar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_441);
            mViewBinding.pushArgsSetting.audioRateText.setText(getResources().getString(R.string.setting_audio_441));
        } else if (configAudioSampleRate == AlivcAudioSampleRateEnum.AUDIO_SAMPLE_RATE_48000) {
            mViewBinding.pushArgsSetting.audioRateSeekbar.setProgress(LivePushProgressStep.PROGRESS_AUDIO_480);
            mViewBinding.pushArgsSetting.audioRateText.setText(getResources().getString(R.string.setting_audio_480));
        }
        mViewBinding.pushArgsSetting.gopText.setText(String.format(Locale.getDefault(), "%d/s", alivcLivePushConfig.getVideoEncodeGop()));
        int videoEncodeGop = alivcLivePushConfig.getVideoEncodeGop();
        if (videoEncodeGop == AlivcVideoEncodeGopEnum.GOP_ONE.getGop()) {
            mViewBinding.pushArgsSetting.gopSeekbar.setProgress(LivePushProgressStep.PROGRESS_20);
        } else if (videoEncodeGop == AlivcVideoEncodeGopEnum.GOP_TWO.getGop()) {
            mViewBinding.pushArgsSetting.gopSeekbar.setProgress(LivePushProgressStep.PROGRESS_40);
        } else if (videoEncodeGop == AlivcVideoEncodeGopEnum.GOP_THREE.getGop()) {
            mViewBinding.pushArgsSetting.gopSeekbar.setProgress(LivePushProgressStep.PROGRESS_60);
        } else if (videoEncodeGop == AlivcVideoEncodeGopEnum.GOP_FOUR.getGop()) {
            mViewBinding.pushArgsSetting.gopSeekbar.setProgress(LivePushProgressStep.PROGRESS_80);
        } else if (videoEncodeGop == AlivcVideoEncodeGopEnum.GOP_FIVE.getGop()) {
            mViewBinding.pushArgsSetting.gopSeekbar.setProgress(LivePushProgressStep.PROGRESS_100);
        }
        mViewBinding.pushArgsSetting.fpsText.setText(String.valueOf(alivcLivePushConfig.getFps()));
        int configFps = alivcLivePushConfig.getFps();
        if (configFps == AlivcFpsEnum.FPS_8.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_0);
        } else if (configFps == AlivcFpsEnum.FPS_10.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_16);
        } else if (configFps == AlivcFpsEnum.FPS_12.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_33);
        } else if (configFps == AlivcFpsEnum.FPS_15.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_50);
        } else if (configFps == AlivcFpsEnum.FPS_20.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_66);
        } else if (configFps == AlivcFpsEnum.FPS_25.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_80);
        } else if (configFps == AlivcFpsEnum.FPS_30.getFps()) {
            mViewBinding.pushArgsSetting.fpsSeekbar.setProgress(LivePushProgressStep.PROGRESS_100);
        }
        AlivcQualityModeEnum configQualityMode = alivcLivePushConfig.getQualityMode();
        if (configQualityMode == AlivcQualityModeEnum.QM_RESOLUTION_FIRST) {
            mQualityModeDefaultIndex = 0;
            mViewBinding.pushArgsSetting.qualityModes.setText(getResources().getString(R.string.quality_resolution_first));
        } else if (configQualityMode == AlivcQualityModeEnum.QM_FLUENCY_FIRST) {
            mQualityModeDefaultIndex = 1;
            mViewBinding.pushArgsSetting.qualityModes.setText(getResources().getString(R.string.quality_fluency_first));
        } else if (configQualityMode == AlivcQualityModeEnum.QM_CUSTOM) {
            mQualityModeDefaultIndex = 2;
            mViewBinding.pushArgsSetting.qualityModes.setText(getResources().getString(R.string.quality_custom));
        }
        AlivcEncodeType configVideoEncodeType = alivcLivePushConfig.getVideoEncodeType();
        if (configVideoEncodeType == AlivcEncodeType.Encode_TYPE_H264) {
            mVideoEncoderTypeDefaultIndex = 0;
            mViewBinding.pushArgsSetting.videoEncoderType.setText(getResources().getString(R.string.h264));
        } else if (configVideoEncodeType == AlivcEncodeType.Encode_TYPE_H265) {
            mVideoEncoderTypeDefaultIndex = 1;
            mViewBinding.pushArgsSetting.videoEncoderType.setText(getResources().getString(R.string.h265));
        }
        int bFrames = alivcLivePushConfig.getBFrames();
        if (bFrames == 0) {
            mBFrameDefaultIndex = 0;
            mViewBinding.pushArgsSetting.bFrameNum.setText("0");
        } else if (bFrames == 1) {
            mBFrameDefaultIndex = 1;
            mViewBinding.pushArgsSetting.bFrameNum.setText("1");
        } else if (bFrames == 3) {
            mBFrameDefaultIndex = 2;
            mViewBinding.pushArgsSetting.bFrameNum.setText("3");
        }
        mViewBinding.pushArgsSetting.hardSwitch.setChecked(alivcLivePushConfig.getVideoEncodeMode() == AlivcEncodeModeEnum.Encode_MODE_HARD);
        mViewBinding.pushArgsSetting.audioHardenc.setChecked(alivcLivePushConfig.getAudioEncodeMode() == AlivcEncodeModeEnum.Encode_MODE_HARD);

        mViewBinding.pushFunctionSetting.beautyOnSwitch.setChecked(isEnableBeauty);
        mViewBinding.pushFunctionSetting.logSwitch.setChecked(isEnableLocalLog);
        mViewBinding.pushFunctionSetting.watermarkSwitch.setChecked(isEnableWaterMark);
        mViewBinding.pushFunctionSetting.pushMirrorSwitch.setChecked(alivcLivePushConfig.isPushMirror());
        mViewBinding.pushFunctionSetting.previewMirrorSwitch.setChecked(alivcLivePushConfig.isPreviewMirror());
        mViewBinding.pushFunctionSetting.cameraSwitch.setChecked(alivcLivePushConfig.getCameraType() == AlivcLivePushCameraTypeEnum.CAMERA_TYPE_FRONT.getCameraId());
        mViewBinding.pushFunctionSetting.autofocusSwitch.setChecked(alivcLivePushConfig.isAutoFocus());
        mViewBinding.pushFunctionSetting.pauseImage.setChecked(!TextUtils.isEmpty(alivcLivePushConfig.getPausePushImage()));
        mViewBinding.pushFunctionSetting.networkImage.setChecked(!TextUtils.isEmpty(alivcLivePushConfig.getNetworkPoorPushImage()));
        mViewBinding.pushFunctionSetting.musicModeSwitch.setChecked(alivcLivePushConfig.getAudioSceneMode() == AlivcAudioSceneModeEnum.AUDIO_SCENE_MUSIC_MODE);
        mViewBinding.pushFunctionSetting.externVideo.setChecked(alivcLivePushConfig.isExternMainStream());

        boolean configAudioOnly = alivcLivePushConfig.isAudioOnly();
        boolean configVideoOnly = alivcLivePushConfig.isVideoOnly();
        if (configAudioOnly && configVideoOnly) {
            mPushModeDefaultIndex = 0;
            mViewBinding.pushFunctionSetting.pushMode.setText(getResources().getString(R.string.video_push_streaming));
        } else {
            if (configAudioOnly) {
                mPushModeDefaultIndex = 1;
                mViewBinding.pushFunctionSetting.pushMode.setText(getResources().getString(R.string.audio_only_push_streaming));
            } else {
                mPushModeDefaultIndex = 2;
                mViewBinding.pushFunctionSetting.pushMode.setText(getResources().getString(R.string.video_only_push_streaming));
            }
        }

        AlivcPreviewDisplayMode configPreviewDisplayMode = alivcLivePushConfig.getPreviewDisplayMode();
        if (configPreviewDisplayMode == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL) {
            mDisplayModeDefaultIndex = 0;
            mViewBinding.pushFunctionSetting.settingDisplayMode.setText(getResources().getString(R.string.display_mode_full));
        } else if (configPreviewDisplayMode == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT) {
            mDisplayModeDefaultIndex = 1;
            mViewBinding.pushFunctionSetting.settingDisplayMode.setText(getResources().getString(R.string.display_mode_fit));
        } else if (configPreviewDisplayMode == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL) {
            mDisplayModeDefaultIndex = 2;
            mViewBinding.pushFunctionSetting.settingDisplayMode.setText(getResources().getString(R.string.display_mode_cut));
        }


        int configPreviewOrientation = alivcLivePushConfig.getPreviewOrientation();
        if (configPreviewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_PORTRAIT.getOrientation()) {
            mPreviewOrientationDefaultIndex = 0;
            mViewBinding.pushFunctionSetting.mainOrientation.setText(getResources().getString(R.string.portrait));
        } else if (configPreviewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_LEFT.getOrientation()) {
            mPreviewOrientationDefaultIndex = 1;
            mViewBinding.pushFunctionSetting.mainOrientation.setText(getResources().getString(R.string.homeLeft));
        } else if (configPreviewOrientation == AlivcPreviewOrientationEnum.ORIENTATION_LANDSCAPE_HOME_RIGHT.getOrientation()) {
            mPreviewOrientationDefaultIndex = 2;
            mViewBinding.pushFunctionSetting.mainOrientation.setText(getResources().getString(R.string.homeRight));
        }
    }
}
