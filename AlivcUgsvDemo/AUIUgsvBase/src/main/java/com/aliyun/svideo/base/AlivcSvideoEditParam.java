package com.aliyun.svideo.base;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.aliyun.common.global.AliyunTag;
import com.aliyun.svideo.media.MediaInfo;
import com.aliyun.svideosdk.common.struct.common.AliyunVideoParam;
import com.aliyun.svideosdk.common.struct.common.CropKey;
import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.common.struct.common.VideoQuality;
import com.aliyun.svideosdk.common.struct.encoder.VideoCodecs;
import com.aliyun.svideosdk.transcode.NativeParser;

/**
 * @author zsy_18 data:2018/10/10
 */
public class AlivcSvideoEditParam {

    /**
     *  判断是编辑模块进入还是通过社区模块的编辑功能进入
     */
    public static final String INTENT_PARAM_KEY_ENTRANCE = "entrance";
    public static final String VIDEO_FRAMERATE = "video_framerate";
    public static final String VIDEO_GOP = "video_gop";
    public static final String VIDEO_RATIO = "video_ratio";
    public static final String VIDEO_QUALITY = "video_quality";
    public static final String VIDEO_RESOLUTION = "video_RESOLUTION";
    public static final String VIDEO_CROP_MODE = "crop_mode";
    public static final String TAIL_ANIMATION = "tail_animation";
    public static final String VIDEO_PATH = "video_path";
    public static final String VIDEO_DURATION = "video_duration";
    public static final String CROP_ACTION = "action";
    public static final String VIDEO_CODEC = "video_codexc";
    /**
     * 是否真裁剪
     */
    public static final int ACTION_TRANSCODE = CropKey.ACTION_TRANSCODE;
    public static final int ACTION_SELECT_TIME = CropKey.ACTION_SELECT_TIME;

    /**
     * 视频比例
     */
    public static final int RATIO_MODE_3_4 = 0;
    public static final int RATIO_MODE_1_1 = 1;
    public static final int RATIO_MODE_9_16 = 2;
    public static final int RATIO_MODE_ORIGINAL = 3;//原比例
    /**
     * 视频分辨率
     */
    public static final int RESOLUTION_360P = 0;
    public static final int RESOLUTION_480P = 1;
    public static final int RESOLUTION_540P = 2;
    public static final int RESOLUTION_720P = 3;

    /**
     * 视频帧率
     */
    private int mFrameRate;
    /**
     * 视频GOP
     */
    private int mGop;

    /**
     * 视频比例
     */
    private int mRatio;
    /**
     * 视频质量
     */
    private VideoQuality mVideoQuality;
    /**
     * 视频分辨率
     */
    private int mResolutionMode;

    /**
     * 编码
     */
    private VideoCodecs mVideoCodec = VideoCodecs.H264_HARDWARE;

    /**
     * 视频裁剪模式
     */
    private VideoDisplayMode mCropMode;
    /**
     * 是否有片尾动画
     */
    private boolean hasTailAnimation;
    /**
     * 仅媒体资源，用于裁剪、原视频的时候获取视频高度
     */
    private MediaInfo mediaInfo;
    /**
     * 是否真裁剪
     */
    private int mCropAction;
    /**
     *  判断是编辑模块进入还是通过社区模块的编辑功能进入
     *  svideo: 短视频
     *  community: 社区
     */
    private String entrance;
    /**
     * 相册页面是否打开裁剪的入口
     */
    private boolean isOpenCrop;

    /**
     * 初始化成员变量
     */
    private AlivcSvideoEditParam() {
        mGop = 250;
        mFrameRate = 30;
        mRatio = RATIO_MODE_9_16;
        mVideoQuality = VideoQuality.HD;
        mResolutionMode = RESOLUTION_720P;
        mCropMode = VideoDisplayMode.FILL;
        entrance = "svideo";
        mCropAction = ACTION_SELECT_TIME;
    }
    public AliyunVideoParam generateVideoParam() {
        AliyunVideoParam param = new AliyunVideoParam.Builder()
        .frameRate(mFrameRate)
        .gop(mGop)
        .crf(0)
        .videoQuality(mVideoQuality)
        .scaleMode(mCropMode)
        .outputWidth(getVideoWidth())
        .outputHeight(getVideoHeight(this.mediaInfo))
        .videoCodec(mVideoCodec)
        .build();
        return param;
    }

    public int getCropAction() {
        return mCropAction;
    }

    public void setCropAction(int mCropAction) {
        this.mCropAction = mCropAction;
    }

    public String getEntrance() {
        return entrance;
    }

    public MediaInfo getMediaInfo() {
        return mediaInfo;
    }

    public void setMediaInfo(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }

    public void setEntrance(String entrance) {
        this.entrance = entrance;
    }

    public int getFrameRate() {
        return mFrameRate;
    }

    public void setFrameRate(int mFrameRate) {
        this.mFrameRate = mFrameRate;
    }

    public int getGop() {
        return mGop;
    }

    public void setGop(int mGop) {
        this.mGop = mGop;
    }

    public int getRatio() {
        return mRatio;
    }

    public void setRatio(int mRatio) {
        this.mRatio = mRatio;
    }

    public VideoQuality getVideoQuality() {
        return mVideoQuality;
    }

    public void setVideoQuality(VideoQuality mVideoQuality) {
        this.mVideoQuality = mVideoQuality;
    }

    public int getResolutionMode() {
        return mResolutionMode;
    }

    public boolean isOpenCrop() {
        return isOpenCrop;
    }

    public void setResolutionMode(int mResolutionMode) {
        this.mResolutionMode = mResolutionMode;
    }

    public VideoDisplayMode getCropMode() {
        return mCropMode;
    }

    public void setCropMode(VideoDisplayMode mScaleMode) {
        this.mCropMode = mScaleMode;
    }

    public boolean isHasTailAnimation() {
        return hasTailAnimation;
    }

    public void setHasTailAnimation(boolean hasTailAnimation) {
        this.hasTailAnimation = hasTailAnimation;
    }

    public VideoCodecs getVideoCodec() {
        return mVideoCodec;
    }

    public static class Build {
        private AlivcSvideoEditParam param = new AlivcSvideoEditParam();
        public Build setFrameRate(int mFrameRate) {
            this.param.mFrameRate = mFrameRate;
            return this;
        }
        public Build setGop(int mGop) {
            this.param.mGop = mGop;
            return this;
        }

        public Build setRatio(int mRatio) {
            this.param.mRatio = mRatio;
            return this;
        }
        public Build setVideoQuality(VideoQuality mVideoQuality) {
            this.param.mVideoQuality = mVideoQuality;
            return this;
        }
        public Build setResolutionMode(int mResolutionMode) {
            this.param.mResolutionMode = mResolutionMode;
            return this;
        }
        public Build setCropMode(VideoDisplayMode mScaleMode) {
            this.param.mCropMode = mScaleMode;
            return this;
        }
        public Build setMediaInfo(MediaInfo info) {
            this.param.mediaInfo = info;
            return this;
        }
        public Build setHasTailAnimation(boolean hasTailAnimation) {
            this.param.hasTailAnimation = hasTailAnimation;
            return this;
        }
        public Build setEntrance(String entrance) {
            this.param.entrance = entrance;
            return this;
        }
        public Build setIsOpenCrop(boolean isOpenCrop) {
            this.param.isOpenCrop = isOpenCrop;
            return this;
        }

        public Build setVideoCodec(VideoCodecs mVideoCodec) {
            this.param.mVideoCodec = mVideoCodec;
            return this;
        }

        public AlivcSvideoEditParam build() {
            return this.param;
        }


    }
    public int getVideoWidth() {
        int width = 0;
        switch (mResolutionMode) {
        case RESOLUTION_360P:
            width = 360;
            break;
        case RESOLUTION_480P:
            width = 480;
            break;
        case RESOLUTION_540P:
            width = 540;
            break;
        case RESOLUTION_720P:
            width = 720;
            break;
        default:
            width = 540;
            break;
        }
        return width;
    }

    public int getVideoHeight(MediaInfo mediaInfo) {
        int height = 0;
        int width = getVideoWidth();
        switch (mRatio) {
        case RATIO_MODE_1_1:
            height = width;
            break;
        case RATIO_MODE_3_4:
            height = width * 4 / 3;
            break;
        case RATIO_MODE_9_16:
            height = width * 16 / 9;
            break;
        case RATIO_MODE_ORIGINAL:
            if (mediaInfo != null) {
                height = (int)(width / getMediaRatio(mediaInfo));
            } else {
                height = width * 16 / 9;
            }
            break;
        default:
            height = width * 16 / 9;
            break;
        }
        return height;
    }
    private float getMediaRatio(MediaInfo mediaInfo) {
        float videoWidth = 9;
        float videoHeight = 16;
        int videoRotation = 0;
        if (mediaInfo.mimeType.startsWith("video") || mediaInfo.filePath.endsWith("gif") || mediaInfo.filePath.endsWith("GIF")) {
            NativeParser parser = new NativeParser();
            parser.init(mediaInfo.filePath);
            try {
                videoWidth = Float.parseFloat(parser.getValue(NativeParser.VIDEO_WIDTH));
                videoHeight = Integer.parseInt(parser.getValue(NativeParser.VIDEO_HEIGHT));
                videoRotation = Integer.parseInt(parser.getValue(NativeParser.VIDEO_ROTATION));
            }  catch (Exception e) {
                Log.e(AliyunTag.TAG, "parse rotation failed");
            }
            parser.release();
            parser.dispose();

        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mediaInfo.filePath, options);
            videoWidth = options.outWidth;
            videoHeight = options.outHeight;
        }


        float ratio = videoWidth / videoHeight;

        return videoRotation == 90 || videoRotation == 270 ? 1 / ratio : ratio;

    }
}
