package com.aliyun.svideo.recorder.bean;

import com.aliyun.svideosdk.common.struct.common.VideoDisplayMode;
import com.aliyun.svideosdk.mixrecorder.AliyunMixRecorderDisplayParam;
import com.aliyun.svideosdk.mixrecorder.AliyunMixTrackLayoutParam;

import java.io.Serializable;

/**
 * data:2021/1/28
 * 主要用与控制录制页面SurfaceView的展示，以及合拍功能导入和录制视频的布局。
 */
public class AUIVideoDisplayParam implements Serializable {
    private VideoDisplayMode mDisplayMode;
    /**
     * 布局级别，大的在上面
     */
    private int mLayoutLevel;
    private float mCenterX;
    private float mCenterY;
    private float mWidthRatio;
    private float mHeightRatio;

    private AUIVideoDisplayParam(Builder builder) {
        this.mDisplayMode = builder.mDisplayMode;
        this.mLayoutLevel = builder.mLayoutLevel;
        this.mCenterX = builder.mCenterX;
        this.mCenterY = builder.mCenterY;
        this.mWidthRatio = builder.mWidthRatio;
        this.mHeightRatio = builder.mHeightRatio;
    }
    public AliyunMixRecorderDisplayParam getAliDisplayParam(){
        AliyunMixTrackLayoutParam layoutParam = new AliyunMixTrackLayoutParam.Builder()
            .centerX(mCenterX)
            .centerY(mCenterY)
            .heightRatio(mHeightRatio)
            .widthRatio(mWidthRatio)
            .build();
        AliyunMixRecorderDisplayParam param = new AliyunMixRecorderDisplayParam.Builder()
            .layoutLevel(mLayoutLevel)
            .displayMode(mDisplayMode)
            .layoutParam(layoutParam)
            .build();
        return param;
    }
    public VideoDisplayMode getDisplayMode() {
        return this.mDisplayMode;
    }

    public int getLayoutLevel() {
        return this.mLayoutLevel;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public float getWidthRatio() {
        return mWidthRatio;
    }

    public float getHeightRatio() {
        return mHeightRatio;
    }

    public static final class Builder {
        private VideoDisplayMode mDisplayMode;
        private int mLayoutLevel;
        private float mCenterX;
        private float mCenterY;
        private float mWidthRatio;
        private float mHeightRatio;

        public Builder() {
            this.mDisplayMode = VideoDisplayMode.FILL;
            this.mCenterX = 0.5f;
            this.mCenterY = 0.5f;
            this.mHeightRatio = 1f;
            this.mWidthRatio = 1f;
        }

        public Builder displayMode(VideoDisplayMode val) {
            this.mDisplayMode = val;
            return this;
        }

        public Builder layoutLevel(int val) {
            this.mLayoutLevel = val;
            return this;
        }

        public Builder setmCenterX(float mCenterX) {
            this.mCenterX = mCenterX;
            return this;
        }

        public Builder setmCenterY(float mCenterY) {
            this.mCenterY = mCenterY;
            return this;
        }

        public Builder setmWidthRatio(float mWidthRatio) {
            this.mWidthRatio = mWidthRatio;
            return this;
        }

        public Builder setmHeightRatio(float mHeightRatio) {
            this.mHeightRatio = mHeightRatio;
            return this;
        }
        public AUIVideoDisplayParam build() {
            return new AUIVideoDisplayParam(this);
        }

    }
}
