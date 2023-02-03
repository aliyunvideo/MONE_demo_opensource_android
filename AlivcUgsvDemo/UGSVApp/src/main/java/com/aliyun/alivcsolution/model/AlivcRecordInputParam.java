package com.aliyun.alivcsolution.model;

import android.graphics.Color;

import com.aliyun.svideo.base.AliyunSnapVideoParam;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.mixrecorder.MixAudioSourceType;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

/**
 * @author zsy_18 data:2019/5/16
 * 录制参数配置
 */
public class AlivcRecordInputParam {
    /**
     * 传参时作为intent extra param 的key值
     */
    public static final String INTENT_KEY_RESOLUTION_MODE = "mResolutionMode";
    public static final String INTENT_KEY_MAX_DURATION = "mMaxDuration";
    public static final String INTENT_KEY_MIN_DURATION = "mMinDuration";
    public static final String INTENT_KEY_RATION_MODE = "mRatioMode";
    public static final String INTENT_KEY_GOP = "mGop";
    public static final String INTENT_KEY_FRAME = "mFrame";
    public static final String INTENT_KEY_NEED_TRANSCODE = "mNeedTranscode";
    public static final String INTENT_KEY_QUALITY = "mVideoQuality";
    public static final String INTENT_KEY_CODEC = "mVideoCodec";
    public static final String INTENT_KEY_VIDEO_OUTPUT_PATH = "videoOutputPath";
    public static final String INTENT_KEY_VIDEO_RENDERING_MODE = "mRenderingMode";
    public static final String INTENT_KEY_RECORD_FLIP = "mRecordFlip";
    public static final String INTENT_KEY_IS_AUTO_CLEAR = "mIsAutoClear";
    public static final String INTENT_KEY_IS_SVIDEO_QUEEN = "mIsSvideoQueen";
    public static final String INTENT_KEY_MIX_AUDIO_SOURCE_TYPE = "mMixAudioSourceType";
    public static final String INTENT_KEY_MIX_BACKGROUND_COLOR = "mMixBackgroundColor";
    public static final String INTENT_KEY_MIX_BACKGROUND_IMAGE_PATH = "mMixBackgroundImagePath";
    public static final String INTENT_KEY_MIX_BACKGROUND_IMAGE_MODE = "mMixBackgroundImageMode";
    public static final String INTENT_KEY_MIX_VIDEO_PATH = "mMixVideoPath";
    public static final String INTENT_KEY_DISPLAY_PARAM_PLAY = "mPlayDisplayParam";
    public static final String INTENT_KEY_DISPLAY_PARAM_RECORD = "mRecordDisplayParam";
    public static final String INTENT_KEY_MIX_BORDER_PARAM_RECORD = "mMixBorderParam";
    public static final String INTENT_KEY_WATER_MARK = "mWaterMark";

    public static final int RESOLUTION_360P = 0;
    public static final int RESOLUTION_480P = 1;
    public static final int RESOLUTION_540P = 2;
    public static final int RESOLUTION_720P = 3;

    public static final int RATIO_MODE_3_4 = 0;
    public static final int RATIO_MODE_1_1 = 1;
    public static final int RATIO_MODE_9_16 = 2;
    /**
     * 默认配置参数
     */
    public static final int DEFAULT_VALUE_MAX_DURATION = 15 * 1000;
    public static final int DEFAULT_VALUE_MIN_DURATION = 2 * 1000;
    public static final int DEFAULT_VALUE_GOP = 250;
    public static final int DEFAULT_VALUE_FRAME = 30;

    /**
     * 视频分辨率
     */
    private int mResolutionMode;
    /**
     * 视频录制最大时长
     */
    private int mMaxDuration;
    /**
     * 视频录制最小时长
     */
    private int mMinDuration;
    /**
     * 录制视频比例
     */
    private int mRatioMode;
    /**
     * 录制视频的关键帧间隔
     */
    private int mGop;
    /**
     * 录制视频的帧率
     */
    private int mFrame;
    /**
     * 录制视频的视频质量
     */
    private VideoQuality mVideoQuality;
    /**
     * 录制视频的编码方式
     */
    private VideoCodecs mVideoCodec;
    /**
     * 输出视频路径
     */
    private String mVideoOutputPath;
    /**
     * 渲染方式
     */
    private BeautySDKType mRenderingMode;
    /**
     * 录制是否输出镜像
     */
    private boolean mIsUseFlip;
    /**
     * 是否自动清空录制分段视频
     */
    private boolean mIsAutoClearTemp = false;
    /**
     * 是否有水印
     */
    private boolean mHasWaterMark = true;
    /**
     * 是否是单独的race版本包
     */
    private boolean mIsSvideoRace = false;
    /**
     * 合拍导入视频路径
     */
    private String mMixVideoFilePath;
    /**
     * 合拍音频模式选择
     */
    private MixAudioSourceType mMixAudioSourceType;
    /**
     * v3.19.0 新增
     * 设置合成窗口非填充模式下的背景颜色
     */
    private int mMixBackgroundColor;
    /**
     * 设置合成窗口非填充模式下的背景图片,assets, resource文件可以使用这个接口
     * v3.19.0 新增
     */
    private String mMixBackgroundImagePath;
    /**
     * 设置合成窗口非填充模式下的背景图片路径
     * v3.19.0 新增
     * 0：裁切 1：填充 2：拉伸
     */
    private int mMixBackgroundImageMode;
    /**
     * 主要用于调整合拍功能中录制画面在视频中的布局；普通录制不需要额外设置
     */
    private VideoDisplayParam mRecordDisplayParam;
    /**
     * 主要用于调整合拍功能中导入视频在视频中的布局；普通录制不需要额外设置
     */
    private VideoDisplayParam mPlayDisplayParam;

    /**
     * 是否需要转码
     */
    private boolean isNeedTranscode;

    /**
     * 合拍设置视频边框
     * */
    private AlivcMixBorderParam mMixBorderParam;

    private AlivcRecordInputParam() {
        this.mResolutionMode = RESOLUTION_720P;
        this.mMaxDuration = DEFAULT_VALUE_MAX_DURATION;
        this.mMinDuration = DEFAULT_VALUE_MIN_DURATION;
        this.mRatioMode = RATIO_MODE_9_16;
        this.mGop = DEFAULT_VALUE_GOP;
        this.mFrame = DEFAULT_VALUE_FRAME;
        this.mVideoQuality = VideoQuality.HD;
        this.mVideoCodec = VideoCodecs.H264_HARDWARE;
        this.mRenderingMode = BeautySDKType.FACEUNITY;
        this.mIsUseFlip = false;
        this.mMixAudioSourceType = MixAudioSourceType.Original;
        this.mMixBackgroundColor = Color.BLACK;
    }
    /**
     * 获取拍摄视频宽度
     * @return
     */
    public int getVideoWidth() {
        int width = 0;
        switch (mResolutionMode) {
        case AliyunSnapVideoParam.RESOLUTION_360P:
            width = 360;
            break;
        case AliyunSnapVideoParam.RESOLUTION_480P:
            width = 480;
            break;
        case AliyunSnapVideoParam.RESOLUTION_540P:
            width = 540;
            break;
        case AliyunSnapVideoParam.RESOLUTION_720P:
            width = 720;
            break;
        default:
            width = 540;
            break;
        }

        return width;
    }
    public int getVideoHeight() {
        int width = getVideoWidth();
        int height = 0;
        switch (mRatioMode) {
        case AliyunSnapVideoParam.RATIO_MODE_1_1:
            height = width;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_3_4:
            height = width * 4 / 3;
            break;
        case AliyunSnapVideoParam.RATIO_MODE_9_16:
            height = width * 16 / 9;
            break;
        default:
            height = width;
            break;
        }
        return height;
    }
    public void setResolutionMode(int mResolutionMode) {
        this.mResolutionMode = mResolutionMode;
    }

    public void setMaxDuration(int mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
    }

    public void setMinDuration(int mMinDuration) {
        this.mMinDuration = mMinDuration;
    }
    public void setRatioMode(int mRatioMode) {
        this.mRatioMode = mRatioMode;
    }

    public void setGop(int mGop) {
        this.mGop = mGop;
    }

    public void setFrame(int mFrame) {
        this.mFrame = mFrame;
    }

    public void setVideoQuality(VideoQuality mVideoQuality) {
        this.mVideoQuality = mVideoQuality;
    }

    public void setVideoCodec(VideoCodecs mVideoCodec) {
        this.mVideoCodec = mVideoCodec;
    }

    public void setVideoOutputPath(String videoOutputPath) {
        this.mVideoOutputPath = videoOutputPath;
    }

    public MixAudioSourceType getMixAudioSourceType() {
        return mMixAudioSourceType;
    }

    public void setMixAudioSourceType(
        MixAudioSourceType mMixAudioSourceType) {
        this.mMixAudioSourceType = mMixAudioSourceType;
    }

    public int getMixBackgroundColor() {
        return mMixBackgroundColor;
    }

    public void setMixBackgroundColor(int mMixBackgroundColor) {
        this.mMixBackgroundColor = mMixBackgroundColor;
    }

    public String getMixBackgroundImagePath() {
        return mMixBackgroundImagePath;
    }

    public void setMixBackgroundImagePath(String mMixBackgroundImagePath) {
        this.mMixBackgroundImagePath = mMixBackgroundImagePath;
    }

    public int getMixBackgroundImageMode() {
        return mMixBackgroundImageMode;
    }

    public void setMixBackgroundImageMode(int mMixBackgroundImageMode) {
        this.mMixBackgroundImageMode = mMixBackgroundImageMode;
    }

    public int getResolutionMode() {
        return mResolutionMode;
    }

    public int getMaxDuration() {
        return mMaxDuration;
    }

    public int getMinDuration() {
        return mMinDuration;
    }

    public int getRatioMode() {
        return mRatioMode;
    }

    public int getGop() {
        return mGop;
    }

    public int getFrame() {
        return mFrame;
    }

    public boolean isUseFlip() {
        return mIsUseFlip;
    }

    public boolean isAutoClearTemp() {
        return mIsAutoClearTemp;
    }

    public boolean hasWaterMark() {
        return mHasWaterMark;
    }

    public void setHasWaterMark(boolean waterMark) {
        this.mHasWaterMark = waterMark;
    }

    public VideoQuality getVideoQuality() {
        return mVideoQuality;
    }

    public VideoCodecs getVideoCodec() {
        return mVideoCodec;
    }

    public String getVideoOutputPath() {
        return mVideoOutputPath;
    }

    public BeautySDKType getmRenderingMode() {
        return mRenderingMode;
    }

    public boolean isSvideoRace() {
        return mIsSvideoRace;
    }

    public void setmRenderingMode(BeautySDKType mRenderingMode) {
        this.mRenderingMode = mRenderingMode;
    }

    public boolean isNeedTranscode() {
        return isNeedTranscode;
    }

    public String getMixVideoFilePath() {
        return mMixVideoFilePath;
    }
    public void setMixVideoFilePath(String videoFilePath) {
        mMixVideoFilePath = videoFilePath;
    }
    public VideoDisplayParam getRecordDisplayParam() {
        return mRecordDisplayParam;
    }
    public VideoDisplayParam getPlayDisplayParam() {
        return mPlayDisplayParam;
    }

    public AlivcMixBorderParam getMixBorderParam() {
        return mMixBorderParam;
    }

    public void setRecordDisplayParam(VideoDisplayParam mRecordDisplayParam) {
        this.mRecordDisplayParam = mRecordDisplayParam;
    }

    public void setPlayDisplayParam(VideoDisplayParam mPlayDisplayParam) {
        this.mPlayDisplayParam = mPlayDisplayParam;
    }

    public static class Builder {
        private AlivcRecordInputParam mParam = new AlivcRecordInputParam();
        public Builder setResolutionMode(int mResolutionMode) {
            this.mParam.mResolutionMode = mResolutionMode;
            return this;
        }

        public Builder setMaxDuration(int mMaxDuration) {
            this.mParam.mMaxDuration = mMaxDuration;
            return this;
        }

        public Builder setMinDuration(int mMinDuration) {
            this.mParam.mMinDuration = mMinDuration;
            return this;
        }

        public Builder setIsUseFlip(boolean isUseFlip) {
            this.mParam.mIsUseFlip = isUseFlip;
            return this;
        }

        public Builder setIsAutoClearTemp(boolean isAutoClearTemp) {
            this.mParam.mIsAutoClearTemp = isAutoClearTemp;
            return this;
        }

        public Builder setRatioMode(int mRatioMode) {
            this.mParam.mRatioMode = mRatioMode;
            return this;
        }

        public Builder setWaterMark(boolean waterMark) {
            this.mParam.mHasWaterMark = waterMark;
            return this;
        }

        public Builder setGop(int mGop) {
            this.mParam.mGop = mGop;
            return this;
        }

        public Builder setFrame(int mFrame) {
            this.mParam.mFrame = mFrame;
            return this;
        }

        public Builder setVideoQuality(VideoQuality mVideoQuality) {
            this.mParam.mVideoQuality = mVideoQuality;
            return this;
        }

        public Builder setVideoCodec(VideoCodecs mVideoCodec) {
            this.mParam.mVideoCodec = mVideoCodec;
            return this;
        }
        public Builder setVideoRenderingMode(BeautySDKType mRenderingMode) {
            this.mParam.mRenderingMode = mRenderingMode;
            return this;
        }

        public Builder setVideoOutputPath(String videoOutputPath) {
            this.mParam.mVideoOutputPath = videoOutputPath;
            return this;
        }
        public Builder setSvideoRace(boolean mIsSvideoRace) {
            this.mParam.mIsSvideoRace = mIsSvideoRace;
            return this;
        }
        public Builder setMixAudioSourceType(MixAudioSourceType mixAudioSourceType) {
            this.mParam.mMixAudioSourceType = mixAudioSourceType;
            return this;
        }
        public Builder setMixBackgroundColor(int mMixBackgroundColor) {
            this.mParam.mMixBackgroundColor = mMixBackgroundColor;
            return this;
        }
        public Builder setMixBackgroundImagePath(String mMixBackgroundImagePath) {
            this.mParam.mMixBackgroundImagePath = mMixBackgroundImagePath;
            return this;
        }
        public Builder setMixBackgroundImageMode(int mMixBackgroundImageMode) {
            this.mParam.mMixBackgroundImageMode = mMixBackgroundImageMode;
            return this;
        }

        public Builder setMixVideoFilePath(String mMixVideoFilePath) {
            this.mParam.mMixVideoFilePath = mMixVideoFilePath;
            return this;
        }
        public Builder setRecordDisplayParam(VideoDisplayParam mRecordDisplayParam) {
            this.mParam.mRecordDisplayParam = mRecordDisplayParam;
            return this;
        }

        public Builder setPlayDisplayParam(VideoDisplayParam mPlayDisplayParam) {
            this.mParam.mPlayDisplayParam = mPlayDisplayParam;
            return this;
        }

        public Builder setMixVideoBorderParam(AlivcMixBorderParam param) {
            this.mParam.mMixBorderParam = param;
            return this;
        }

        public Builder setMixNeedTranscode(boolean needTranscode) {
            this.mParam.isNeedTranscode = needTranscode;
            return this;
        }

        public AlivcRecordInputParam build() {
            return this.mParam;
        }
    }
}
