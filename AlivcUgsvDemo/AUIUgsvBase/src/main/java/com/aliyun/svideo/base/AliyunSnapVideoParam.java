package com.aliyun.svideo.base;/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */


import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.common.struct.recorder.CameraType;
import com.aliyun.svideosdk.common.struct.recorder.FlashType;
public class AliyunSnapVideoParam {

    public static final String CROP_MODE = "crop_mode";
    public static final String VIDEO_FRAMERATE = "video_framerate";
    public static final String VIDEO_GOP = "video_gop";
    public static final String VIDEO_BITRATE = "video_bitrate";
    public static final String NEED_RECORD = "need_record";
    public static final String MAX_VIDEO_DURATION = "max_video_duration";
    public static final String MIN_VIDEO_DURATION = "min_video_duration";
    public static final String MIN_CROP_DURATION = "min_crop_duration";
    public static final String VIDEO_RESOLUTION = "video_resolution";
    public static final String MIN_DURATION = "min_duration";
    public static final String MAX_DURATION = "max_duration";
    public static final String VIDEO_QUALITY = "video_quality";
    public static final String VIDEO_RATIO = "video_ratio";
    public static final String RECORD_MODE = "record_mode";
    public static final String FILTER_LIST = "filter_list";
    public static final String BEAUTY_STATUS = "beauty_status";
    public static final String BEAUTY_LEVEL = "beauty_level";
    public static final String CAMERA_TYPE = "camera_type";
    public static final String FLASH_TYPE = "falsh_type";
    public static final String NEED_CLIP = "need_clip";
    public static final String SORT_MODE = "sort_mode";
    public static final String CROP_USE_GPU = "crop_use_gpu";
    public static final String VIDEO_CODEC = "video_codec";

    public static final VideoDisplayMode SCALE_CROP = VideoDisplayMode.SCALE;
    public static final VideoDisplayMode SCALE_FILL = VideoDisplayMode.FILL;

    /**
     * 只显示视频
     */
    /****
     * Only display video files.
     */
    public static final int SORT_MODE_VIDEO = 0;

    /**
     * 只显示照片
     */
    /****
     * Only display image files.
     */
    public static final int SORT_MODE_PHOTO = 1;

    /**
     * 照片和视频混合显示
     */
    /****
     * Display both video and image files.
     */
    public static final int SORT_MODE_MERGE = 2;

    /**
     * 宽高比为3：4
     */
    /****
     * Set the aspect ratio to 3:4.
     */
    public static final int RATIO_MODE_3_4 = 0;

    /**
     * 宽高比为1：1
     */
    /****
     * Set the aspect ratio to 1:1.
     */
    public static final int RATIO_MODE_1_1 = 1;

    /**
     * 宽高比为9：16
     */
    /****
     * Set the aspect ratio to 9:16.
     */
    public static final int RATIO_MODE_9_16 = 2;


    public static final int RESOLUTION_360P = 0;
    public static final int RESOLUTION_480P = 1;
    public static final int RESOLUTION_540P = 2;
    public static final int RESOLUTION_720P = 3;

    /**
     * 按下录制，再次按下停止录制。
     */
    /****
     * Press once to start recording. Press again to stop recording.
     */
    public static final int RECORD_MODE_TOUCH = 0;

    /**
     * 按压录制，即按住时录制，松手停止录制
     */
    /****
     * Press to record. Release to stop recording.
     */
    public static final int RECORD_MODE_PRESS = 1;

    /**
     * {@link AliyunSnapVideoParam#RECORD_MODE_PRESS}和{@link AliyunSnapVideoParam#RECORD_MODE_TOUCH}两种都支持
     */
    /****
     * Support both {@link AliyunSnapVideoParam#RECORD_MODE_PRESS} and {@link AliyunSnapVideoParam#RECORD_MODE_TOUCH}.
     */
    public static final int RECORD_MODE_AUTO = 2;


    private int mResolutionMode = RESOLUTION_540P;
    private int mRatioMode = RATIO_MODE_3_4;
    private boolean mNeedRecord = true;
    private VideoQuality mVideoQuality = VideoQuality.HD;
    private int mGop = 5;
    private int mFrameRate = 25;
    private int mBitrate;
    private int mMinVideoDuration = 2000;
    private int mMaxVideoDuration = 10 * 60 * 1000;
    private int mMinCropDuration = 2000;
    private VideoDisplayMode mScaleMode = VideoDisplayMode.SCALE;

    private int mRecordMode =  RECORD_MODE_AUTO;
    private String[] mFilterList;
    private int mBeautyLevel = 80;
    private boolean mBeautyStatus = true;
    private CameraType mCameraType = CameraType.FRONT;
    private FlashType mFlashType = FlashType.OFF;
    private boolean mNeedClip = true;
    private int mMaxDuration = 30000;
    private int mMinDuration = 2000;
    private int mSortMode = SORT_MODE_MERGE;
    private boolean isCropUseGPU = false;
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;


    public int getSortMode() {
        return mSortMode;
    }

    /**
     * 设置媒体文件选择界面的过滤模式
     *
     * @see AliyunSnapVideoParam#SORT_MODE_VIDEO
     * @see AliyunSnapVideoParam#SORT_MODE_PHOTO
     * @see AliyunSnapVideoParam#SORT_MODE_MERGE
     * @param sortMode
     */
    /****
     * Sets the filter mode on the media file selection page.
     *
     * @see AliyunSnapVideoParam#SORT_MODE_VIDEO
     * @see AliyunSnapVideoParam#SORT_MODE_PHOTO
     * @see AliyunSnapVideoParam#SORT_MODE_MERGE
     * @param sortMode
     */
    public void setSortMode(int sortMode) {
        this.mSortMode = sortMode;
    }

    /**
     * 设置裁剪使用GPU
     * @param isUseGPU true：使用gpu， false：使用cpu
     */
    /****
     * Sets whether to use GPU during cropping.
     * @param isUseGPU true indicates use GPU. false indicates use CPU.
     */
    public void setCropUseGPU(boolean isUseGPU) {
        this.isCropUseGPU = isUseGPU;
    }

    public int getRecordMode() {
        return mRecordMode;
    }

    /**
     * 设置录制模式
     * @see AliyunSnapVideoParam#RECORD_MODE_PRESS
     * @see AliyunSnapVideoParam#RECORD_MODE_TOUCH
     * @see AliyunSnapVideoParam#RECORD_MODE_AUTO
     * @param mRecordMode
     */
    /****
     * Sets the recording mode.
     * @see AliyunSnapVideoParam#RECORD_MODE_PRESS
     * @see AliyunSnapVideoParam#RECORD_MODE_TOUCH
     * @see AliyunSnapVideoParam#RECORD_MODE_AUTO
     * @param mRecordMode
     */
    public void setRecordMode(int mRecordMode) {
        this.mRecordMode = mRecordMode;
    }

    public boolean isNeedClip() {
        return mNeedClip;
    }

    /**
     * 是否多段录制
     * @param mNeedClip true：多段录制，false：单段录制
     */
    /****
     * Sets whether to record multiple clips.
     * @param mNeedClip true indicates record multiple clips. false indicates record one clip only.
     */
    public void setNeedClip(boolean mNeedClip) {
        this.mNeedClip = mNeedClip;
    }

    public boolean getBeautyStatus() {
        return mBeautyStatus;
    }

    /**
     * 设置美颜开关
     * @param mBeautyStatus true：开， false：关
     */
    /****
     * Enables or disables beauty mode.
     * @param mBeautyStatus true indicates enable. false indicates disable.
     */
    public void setBeautyStatus(boolean mBeautyStatus) {
        this.mBeautyStatus = mBeautyStatus;
    }

    public String[] getFilterList() {
        return mFilterList;
    }

    /**
     * 设置滤镜资源列表
     * @param mFilterList
     */
    /****
     * Sets a list of filter effects.
     * @param mFilterList
     */
    public void setFilterList(String[] mFilterList) {
        this.mFilterList = mFilterList;
    }

    public int getBeautyLevel() {
        return mBeautyLevel;
    }

    /**
     * 获取摄像头类型
     * @see CameraType
     * @return 摄像头类型
     */
    /****
     * Gets the camera type.
     * @see CameraType
     * @return The camera type.
     */
    public CameraType getCameraType() {
        return mCameraType;
    }

    public void setCameraType(CameraType mCameraType) {
        this.mCameraType = mCameraType;
    }

    /**
     * 获取闪光灯类型
     * @see FlashType
     * @return 闪光灯类型
     */
    /****
     * Gets the flash mode.
     * @see FlashType
     * @return The flash mode.
     */
    public FlashType getFlashType() {
        return mFlashType;
    }

    /**
     * 设置闪光灯类型
     * @param mFlashType
     */
    /****
     * Sets the flash mode.
     * @param mFlashType
     */
    public void setFlashType(FlashType mFlashType) {
        this.mFlashType = mFlashType;
    }

    /**
     * 设置美颜级别
     * @param mBeautyLevel [0,100] 0表示关闭美颜
     */
    /****
    * Sets the beauty level.
    * @param mBeautyLevel level Valid values: [0, 100]. 0 indicates disable beauty mode.
    */
    public void setBeautyLevel(int mBeautyLevel) {
        this.mBeautyLevel = mBeautyLevel;
    }

    public int getMaxDuration() {
        return mMaxDuration;
    }

    /**
     * 设置录制的最大时长
     * @param mMaxDuration
     */
    /****
     * Sets the maximum recording duration.
     * @param mMaxDuration
     */
    public void setMaxDuration(int mMaxDuration) {
        this.mMaxDuration = mMaxDuration;
    }

    public int getMinDuration() {
        return mMinDuration;
    }

    /**
     * 设置录制的最小时长，小于这个时长视频会被丢弃。
     * @param mMinDuration
     */
    /****
     * Sets the minimum recording duration. Video clips whose durations are shorter than this value are discarded.
     * @param mMinDuration
     */
    public void setMinDuration(int mMinDuration) {
        this.mMinDuration = mMinDuration;
    }

    public int getMinVideoDuration() {
        return mMinVideoDuration;
    }

    /**
     * 设置视频选择页面过滤视频的最小时长
     * @param mMinVideoDuration 单位是毫秒
     */
    /****
     * Sets the minimum video duration on the video selection page.
     * @param mMinVideoDuration Unit: milliseconds.
     */
    public void setMinVideoDuration(int mMinVideoDuration) {
        this.mMinVideoDuration = mMinVideoDuration;
    }

    public int getMaxVideoDuration() {
        return mMaxVideoDuration;
    }

    /**
     * 设置视频选择界面过滤视频的最大时长
     * @param mMaxVideoDuration 单位是毫秒
     */
    /****
     * Sets the maximum video duration on the video selection page.
     * @param mMaxVideoDuration Unit: milliseconds.
     */
    public void setMaxVideoDuration(int mMaxVideoDuration) {
        this.mMaxVideoDuration = mMaxVideoDuration;
    }

    public int getMinCropDuration() {
        return mMinCropDuration;
    }

    /**
     * 设置视频裁剪的最小时长，裁剪界面选取裁剪时间段无法比这个时长小
     * @param mMinCropDuration 单位是毫秒
     */
    /****
     * Sets the minimum video duration during cropping. The durations of cropped video clips must not be shorter than this value.
     * @param mMinCropDuration Unit: milliseconds.
     */
    public void setMinCropDuration(int mMinCropDuration) {
        this.mMinCropDuration = mMinCropDuration;
    }

    public boolean isCropUseGPU() {
        return isCropUseGPU;
    }

    public int getFrameRate() {
        return mFrameRate;
    }

    /**
     * 设置帧率
     * @param mFrameRate
     */
    /****
     * Sets the frame rate.
     * @param mFrameRate
     */
    public void setFrameRate(int mFrameRate) {
        this.mFrameRate = mFrameRate;
    }

    public VideoDisplayMode getScaleMode() {
        return mScaleMode;
    }

    /**
     * 设置视频裁剪缩放模式
     * @see VideoDisplayMode
     * @param mCropMode
     */
    /****
     * Sets the crop mode.
     * @see VideoDisplayMode
     * @param mCropMode
     */
    public void setScaleMode(VideoDisplayMode mCropMode) {
        this.mScaleMode = mCropMode;
    }

    public int getResolutionMode() {
        return mResolutionMode;
    }

    /**
     * 设置分辨率基数，支持四种分辨率基数，最终的分辨率是由该基数跟{@link AliyunSnapVideoParam#setRatioMode(int)}的值一起计算出来的
     * @see AliyunSnapVideoParam#RESOLUTION_360P
     * @see AliyunSnapVideoParam#RESOLUTION_480P
     * @see AliyunSnapVideoParam#RESOLUTION_540P
     * @see AliyunSnapVideoParam#RESOLUTION_720P
     * @param mResolutionMode
     */
    /****
     * Sets the base for calculating the resolution. Four bases are supported. The resolution is calculated based on the base and {@link AliyunSnapVideoParam#setRatioMode(int)}.
     * @see AliyunSnapVideoParam#RESOLUTION_360P
     * @see AliyunSnapVideoParam#RESOLUTION_480P
     * @see AliyunSnapVideoParam#RESOLUTION_540P
     * @see AliyunSnapVideoParam#RESOLUTION_720P
     * @param mResolutionMode
     */
    public void setResolutionMode(int mResolutionMode) {
        this.mResolutionMode = mResolutionMode;
    }

    public int getRatioMode() {
        return mRatioMode;
    }

    /**
     * 设置分辨率宽高比，最终的分辨率由该值和{@link AliyunSnapVideoParam#setResolutionMode(int)}的值一起计算出。
     * @see AliyunSnapVideoParam#RATIO_MODE_1_1
     * @see AliyunSnapVideoParam#RATIO_MODE_3_4
     * @see AliyunSnapVideoParam#RATIO_MODE_9_16
     * @param mRatioMode
     */
    /****
     * Sets the aspect ratio. The resolution is calculated based on the aspect ratio and {@link AliyunSnapVideoParam#setResolutionMode(int)}.
     * @see AliyunSnapVideoParam#RATIO_MODE_1_1
     * @see AliyunSnapVideoParam#RATIO_MODE_3_4
     * @see AliyunSnapVideoParam#RATIO_MODE_9_16
     * @param mRatioMode
     */
    public void setRatioMode(int mRatioMode) {
        this.mRatioMode = mRatioMode;
    }

    public boolean isNeedRecord() {
        return mNeedRecord;
    }

    /**
     * 视频选择界面是否需要"录制"的入口
     * @param mNeedClip
     */
    /****
     * Sets whether to display the Record option on the video selection page.
     * @param mNeedClip
     */
    public void setNeedRecord(boolean mNeedClip) {
        this.mNeedRecord = mNeedClip;
    }

    public VideoQuality getVideoQuality() {
        return mVideoQuality;
    }

    /**
     * 设置输出视频质量
     * @see VideoQuality
     * @param mVideoQuality
     */
    /****
     * Sets the video quality.
     * @see VideoQuality
     * @param mVideoQuality
     */
    public void setVideoQuality(VideoQuality mVideoQuality) {
        this.mVideoQuality = mVideoQuality;
    }

    public int getGop() {
        return mGop;
    }

    /**
     * 设置Gop大小，单位是帧数
     * @param mGop
     */
    /****
     * Sets the GOP size in frames.
     * @param mGop
     */
    public void setGop(int mGop) {
        this.mGop = mGop;
    }

    /**
     * 设置bitrate大小，单位是kps
     * @param bitrate
     */
    /****
     * Sets the bitrate in kbit/s.
     * @param bitrate
     */
    public void setVideoBitrate(int bitrate) {
        this.mBitrate = bitrate;
    }

    public int getVideoBitrate() {
        return mBitrate;
    }

    /**
     * 设置编码方式
     * @param codec 默认是硬编
     * @see VideoCodecs
     */
    /****
     * Sets the encoding mode.
     * @param codec Default is hardware encoding.
     * @see VideoCodecs
     */
    public void setVideoCodec(VideoCodecs codec) {
        this.mVideoCodec = codec;
    }

    public VideoCodecs getVideoCodec() {
        return mVideoCodec;
    }

    public static class Builder {
        private AliyunSnapVideoParam mParam;
        public Builder() {
            mParam = new AliyunSnapVideoParam();
        }

        /**
         * 设置分辨率基数，支持四种分辨率基数，最终的分辨率是由该基数跟{@link AliyunSnapVideoParam#setRatioMode(int)}的值一起计算出来的
         * @see AliyunSnapVideoParam#RESOLUTION_360P
         * @see AliyunSnapVideoParam#RESOLUTION_480P
         * @see AliyunSnapVideoParam#RESOLUTION_540P
         * @see AliyunSnapVideoParam#RESOLUTION_720P
         * @param  resolutionMode
         */
        /****
         * Sets the base for calculating the resolution. Four bases are supported. The resolution is calculated based on the base and {@link AliyunSnapVideoParam#setRatioMode(int)}.
         * @see AliyunSnapVideoParam#RESOLUTION_360P
         * @see AliyunSnapVideoParam#RESOLUTION_480P
         * @see AliyunSnapVideoParam#RESOLUTION_540P
         * @see AliyunSnapVideoParam#RESOLUTION_720P
         * @param  resolutionMode
         */
        public Builder setResolutionMode(int resolutionMode) {
            mParam.setResolutionMode(resolutionMode);
            return this;
        }

        /**
         * @param resolutionMode
         * @deprecated 使用 {@link #setResolutionMode(int)}替代
         */
        /****
         * @param resolutionMode
         * @deprecated Replaced by {@link #setResolutionMode(int)}.
         */
        public Builder setResulutionMode(int resolutionMode) {
            mParam.setResolutionMode(resolutionMode);
            return this;
        }

        /**
         * 设置分辨率宽高比，最终的分辨率由该值和{@link AliyunSnapVideoParam#setResolutionMode(int)}的值一起计算出
         * @see AliyunSnapVideoParam#RATIO_MODE_1_1
         * @see AliyunSnapVideoParam#RATIO_MODE_3_4
         * @see AliyunSnapVideoParam#RATIO_MODE_9_16
         * @param ratioMode
         */
        /****
         * Sets the aspect ratio. The resolution is calculated based on the aspect ratio and {@link AliyunSnapVideoParam#setResolutionMode(int)}.
         * @see AliyunSnapVideoParam#RATIO_MODE_1_1
         * @see AliyunSnapVideoParam#RATIO_MODE_3_4
         * @see AliyunSnapVideoParam#RATIO_MODE_9_16
         * @param ratioMode
         */
        public Builder setRatioMode(int ratioMode) {
            mParam.setRatioMode(ratioMode);
            return this;
        }

        /**
         * 视频选择界面是否需要"录制"的入口。
         * @param needClip
         */
        /****
         * Sets whether to display the Record option on the video selection page.
         * @param needClip
         */
        public Builder setNeedRecord(boolean needClip) {
            mParam.setNeedRecord(needClip);
            return this;
        }

        /**
         * 已过时
         * @param videoQuality
         * @deprecated 使用 {@link #setVideoQuality(VideoQuality)}替代
         */
        /****
         * Deprecated.
         * @param videoQuality
         * @deprecated Replaced by {@link #setVideoQuality(VideoQuality)}.
         */
        public Builder setVideQuality(VideoQuality videoQuality) {
            mParam.setVideoQuality(videoQuality);
            return this;
        }

        /**
         * 设置输出视频质量。
         * @see VideoQuality
         * @param videoQuality
         */
        /****
         * Sets the video quality.
         * @see VideoQuality
         * @param videoQuality
         */
        public Builder setVideoQuality(VideoQuality videoQuality) {
            mParam.setVideoQuality(videoQuality);
            return this;
        }

        /**
         * 设置Gop大小，单位是帧数。
         * @param gop
         */
        /****
         * Sets the GOP size in frames.
         * @param gop
         */
        public Builder setGop(int gop) {
            mParam.setGop(gop);
            return this;
        }

        /**
         * 设置bitrate大小，单位是kps
         * @param bitrate
         */
        /****
        * Sets the bitrate in kbit/s.
        * @param bitrate
        */
        public Builder setVideoBitrate(int bitrate) {
            mParam.setVideoBitrate(bitrate);
            return this;
        }

        /**
         * 设置帧率。
         * @param frameRate
         */
        /****
         * Sets the frame rate.
         * @param frameRate
         */
        public Builder setFrameRate(int frameRate) {
            mParam.setFrameRate(frameRate);
            return this;
        }

        /**
         * 设置视频裁剪缩放模式。
         * @see VideoDisplayMode
         * @param scaleMode
         */
        /****
         * Sets the crop mode.
         * @see VideoDisplayMode
         * @param scaleMode
         */
        public Builder setCropMode(VideoDisplayMode scaleMode) {
            mParam.setScaleMode(scaleMode);
            return this;
        }

        /**
         * 设置视频选择页面过滤视频的最小时长。
         * @param duration 单位是毫秒
         */
        /****
         * Sets the minimum video duration on the video selection page.
         * @param duration Unit: milliseconds.
         */
        public Builder setMinVideoDuration(int duration) {
            mParam.setMinVideoDuration(duration);
            return this;
        }

        /**
         * 设置视频选择界面过滤视频的最大时长。
         * @param duration 单位是毫秒
         */
        /****
         * Sets the maximum video duration on the video selection page.
         * @param duration Unit: milliseconds.
         */
        public Builder setMaxVideoDuration(int duration) {
            mParam.setMaxVideoDuration(duration);
            return this;
        }

        /**
         * 设置录制模式
         * @see AliyunSnapVideoParam#RECORD_MODE_PRESS
         * @see AliyunSnapVideoParam#RECORD_MODE_TOUCH
         * @see AliyunSnapVideoParam#RECORD_MODE_AUTO
         * @param recordMode
         */
        /****
         * Sets the recording mode.
         * @see AliyunSnapVideoParam#RECORD_MODE_PRESS
         * @see AliyunSnapVideoParam#RECORD_MODE_TOUCH
         * @see AliyunSnapVideoParam#RECORD_MODE_AUTO
         * @param recordMode
         */
        public Builder setRecordMode(int recordMode) {
            mParam.setRecordMode(recordMode);
            return this;
        }

        /**
         * 设置视频裁剪的最小时长，裁剪界面选取裁剪时间段无法比这个时长小。
         * @param duration 单位是毫秒
         */
        /****
         * Sets the minimum video duration during cropping. The durations of cropped video clips must not be shorter than this value.
         * @param duration Unit: milliseconds.
         */
        public Builder setMinCropDuration(int duration) {
            mParam.setMinCropDuration(duration);
            return this;
        }

        /**
         * 设置滤镜资源路径列表。
         * @param filterList
         */
        /****
         * Sets a list of filter effects.
         * @param filterList
         */
        public Builder setFilterList(String[] filterList) {
            mParam.setFilterList(filterList);
            return this;
        }

        /**
         * 设置美颜级别。
         * @param beautyLevel [0,100] 0表示关闭美颜
         */
        /****
         * Sets the beauty level.
         * @param beautyLevel Valid values: [0, 100]. 0 indicates disable beauty mode.
         */
        public Builder setBeautyLevel(int beautyLevel) {
            mParam.setBeautyLevel(beautyLevel);
            return this;
        }

        /**
         * 设置美颜开关。
         * @param beautyStatus true：开，false：关
         */
        /****
         * Enables or disables beauty mode.
         * @param beautyStatus true indicates enable. false indicates disable.
         */
        public Builder setBeautyStatus(boolean beautyStatus) {
            mParam.setBeautyStatus(beautyStatus);
            return this;
        }

        /**
         * 获取摄像头类型。
         * @see CameraType
         * @param cameraType
         */
        /****
         * Gets the camera type.
         * @see CameraType
         * @param cameraType
         */
        public Builder setCameraType(CameraType cameraType) {
            mParam.setCameraType(cameraType);
            return this;
        }

        /**
         * 设置闪光灯类型。
         * @param flashType
         */
        /****
         * Sets the flash mode.
         * @param flashType
         */
        public Builder setFlashType(FlashType flashType) {
            mParam.setFlashType(flashType);
            return this;
        }

        /**
         * 设置录制的最大时长。
         * @param maxDuration
         */
        /****
         * Sets the maximum recording duration.
         * @param maxDuration
         */
        public Builder setMaxDuration(int maxDuration) {
            mParam.setMaxDuration(maxDuration);
            return this;
        }

        /**
         * 设置录制的最小时长，小于这个时长视频会被丢弃。
         * @param minDuration
         */
        /****
         * Sets the minimum recording duration. Video clips whose durations are shorter than this value are discarded.
         * @param minDuration
         */
        public Builder setMinDuration(int minDuration) {
            mParam.setMinDuration(minDuration);
            return this;
        }

        /**
         * 是否多段录制。
         * @param needClip true：多段录制，false：单段录制
         */
        /****
         * Sets whether to record multiple clips.
         * @param needClip true indicates record multiple clips. false indicates record one clip only.
         */
        public Builder setNeedClip(boolean needClip) {
            mParam.setNeedClip(needClip);
            return this;
        }

        /**
         * 设置媒体文件选择界面的过滤模式
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO},
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO},
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}。
         * @param sortMode {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}, {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}, {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}
         */
        /****
         * Sets the filter mode on the media file selection page.
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO},
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}, and
         * {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}.
         * @param sortMode {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}, {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}, {@link AliyunSnapVideoParam#SORT_MODE_PHOTO}
         */
        public Builder setSortMode(int sortMode) {
            mParam.setSortMode(sortMode);
            return this;
        }

        /**
         * 设置裁剪使用GPU
         * @param isUseGPU true：使用gpu， false：使用cpu
         */
        /****
         * Sets whether to use GPU during cropping.
         * @param isUseGPU true indicates use GPU. false indicates use CPU.
         */
        public Builder setCropUseGPU(boolean isUseGPU) {
            mParam.setCropUseGPU(isUseGPU);
            return this;
        }

        /**
         * 设置编码方式
         * @param codec 默认是硬编
         * @see VideoCodecs
         */
        /****
         * Sets the encoding mode.
         * @param codec Default is hardware encoding.
         * @see VideoCodecs
         */
        public Builder setVideoCodec(VideoCodecs codec) {
            mParam.setVideoCodec(codec);
            return this;
        }

        public AliyunSnapVideoParam build() {
            return mParam;
        }
    }
}
