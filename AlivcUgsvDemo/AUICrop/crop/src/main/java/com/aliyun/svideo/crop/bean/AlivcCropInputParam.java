package com.aliyun.svideo.crop.bean;

import com.aliyun.svideosdk.common.struct.common.CropKey;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;

/**
 * data:2019/5/22
 */
public class AlivcCropInputParam {
    /**
     * 传参时作为intent extra param 的key值
     */
    public static final String INTENT_KEY_PATH = "mPath";
    public static final String INTENT_KEY_RESOLUTION_MODE = "mResolutionMode";
    public static final String INTENT_KEY_CROP_MODE = "mCropMode";
    public static final String INTENT_KEY_QUALITY = "mQuality";
    public static final String INTENT_KEY_CODECS = "mVideoCodecs";
    public static final String INTENT_KEY_GOP = "mGop";
    public static final String INTENT_KEY_FRAME_RATE = "mFrameRate";
    public static final String INTENT_KEY_RATIO_MODE = "mRatioMode";
    public static final String INTENT_KEY_ACTION = "mAction";
    public static final String INTENT_KEY_MIN_DURATION = "mMinCropDuration";
    public static final String INTENT_KEY_USE_GPU = "isUseGPU";
    /**
     * 视频分辨率
     */
    public static final int RESOLUTION_360P = 0;
    public static final int RESOLUTION_480P = 1;
    public static final int RESOLUTION_540P = 2;
    public static final int RESOLUTION_720P = 3;
    /**
     * 视频比例
     */
    public static final int RATIO_MODE_3_4 = 0;
    public static final int RATIO_MODE_1_1 = 1;
    public static final int RATIO_MODE_9_16 = 2;
    public static final int RATIO_MODE_ORIGINAL = 3;//原比例
    /**
     * 需要裁剪内容的路径
     */
    private String mPath;
    /**
     * 分辨率
     */
    private int mResolutionMode;
    /**
     * 裁剪模式
     */
    private VideoDisplayMode mCropMode;
    /**
     * 视频质量
     */
    private VideoQuality mQuality;
    /**
     * 编解码方式
     */
    private VideoCodecs mVideoCodecs;
    /**
     * 关键帧间隔
     */
    private int mGop;
    /**
     * 帧率
     */
    private int mFrameRate;
    /**
     * 视频比例
     */
    private int mRatioMode;
    /**
     * 只是选择时间，不进行转码裁剪操作,{@link CropKey#ACTION_SELECT_TIME}
     * 进行转码操作，{@link CropKey#ACTION_TRANSCODE}
     */
    private int mAction;
    /**
     * 视频最小裁剪时长
     */
    private int mMinCropDuration;
    /**
     * 是否使用GPU
     */
    private boolean isUseGPU;


    public AlivcCropInputParam() {
        mResolutionMode = RESOLUTION_720P;
        mCropMode = VideoDisplayMode.SCALE;
        mQuality = VideoQuality.HD;
        mVideoCodecs = VideoCodecs.H264_HARDWARE;
        mGop = 250;
        mFrameRate = 30;
        mRatioMode = RATIO_MODE_9_16;
        mAction = CropKey.ACTION_TRANSCODE;
        mMinCropDuration = 2000;
        isUseGPU = false;
    }


    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public void setResolutionMode(int mResolutionMode) {
        this.mResolutionMode = mResolutionMode;
    }

    public void setCropMode(VideoDisplayMode mCropMode) {
        this.mCropMode = mCropMode;
    }

    public void setQuality(VideoQuality mQuality) {
        this.mQuality = mQuality;
    }

    public void setVideoCodecs(VideoCodecs mVideoCodecs) {
        this.mVideoCodecs = mVideoCodecs;
    }

    public void setGop(int mGop) {
        this.mGop = mGop;
    }

    public void setFrameRate(int mFrameRate) {
        this.mFrameRate = mFrameRate;
    }

    public void setRatioMode(int mRatioMode) {
        this.mRatioMode = mRatioMode;
    }

    public void setAction(int mAction) {
        this.mAction = mAction;
    }

    public void setCropDuration(int mCropDuration) {
        this.mMinCropDuration = mCropDuration;
    }

    public void setUseGPU(boolean useGPU) {
        isUseGPU = useGPU;
    }

    public String getPath() {
        return mPath;
    }

    public int getResolutionMode() {
        return mResolutionMode;
    }

    public VideoDisplayMode getCropMode() {
        return mCropMode;
    }

    public VideoQuality getQuality() {
        return mQuality;
    }

    public VideoCodecs getVideoCodecs() {
        return mVideoCodecs;
    }

    public int getGop() {
        return mGop;
    }

    public int getFrameRate() {
        return mFrameRate;
    }

    public int getRatioMode() {
        return mRatioMode;
    }

    public int getAction() {
        return mAction;
    }

    public int getMinCropDuration() {
        return mMinCropDuration;
    }

    public boolean isUseGPU() {
        return isUseGPU;
    }

    public static class Builder {
        AlivcCropInputParam mParam = new AlivcCropInputParam();
        public Builder setPath(String mPath) {
            mParam.mPath = mPath;
            return this;
        }

        public Builder setResolutionMode(int mResolutionMode) {
            mParam.mResolutionMode = mResolutionMode;
            return this;
        }

        public Builder setCropMode(VideoDisplayMode mCropMode) {
            mParam.mCropMode = mCropMode;
            return this;
        }

        public Builder setQuality(VideoQuality mQuality) {
            mParam.mQuality = mQuality;
            return this;
        }

        public Builder setVideoCodecs(VideoCodecs mVideoCodecs) {
            mParam.mVideoCodecs = mVideoCodecs;
            return this;
        }

        public Builder setGop(int mGop) {
            mParam.mGop = mGop;
            return this;
        }

        public Builder setFrameRate(int mFrameRate) {
            mParam.mFrameRate = mFrameRate;
            return this;
        }

        public Builder setRatioMode(int mRatioMode) {
            mParam.mRatioMode = mRatioMode;
            return this;
        }

        public Builder setAction(int mAction) {
            mParam.mAction = mAction;
            return this;
        }

        public Builder setMinCropDuration(int mCropDuration) {
            mParam.mMinCropDuration = mCropDuration;
            return this;
        }

        public Builder setUseGPU(boolean useGPU) {
            mParam.isUseGPU = useGPU;
            return this;
        }
        public AlivcCropInputParam build() {
            return mParam;
        }
    }
}
