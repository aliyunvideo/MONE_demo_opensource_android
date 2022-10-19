package com.aliyun.alivcsolution.model;

import com.aliyun.Visible;

import java.io.Serializable;

@Visible
public class AlivcMixBorderParam implements Serializable {

    /**
     * 视频流id
     */
    /**
     * Stream id of the video
     */
    private int mTrackId;

    /**
     * 视频边框宽度
     */
    /**
     * The border width of the video
     */
    private int mBorderWidth;

    /**
     * 视频边框颜色
     */
    /**
     * The border color of the video
     */
    private int mBorderColor;

    /**
     * 视频圆角半径
     */
    /**
     * The radius of the video's corner
     */
    private float mCornerRadius;

    public AlivcMixBorderParam(Builder builder){
       mTrackId = builder.mTrackId;
       mBorderWidth = builder.mBorderWidth;
       mBorderColor = builder.mBorderColor;
       mCornerRadius = builder.mCornerRadius;
    }

    public int getTrackId() {
        return mTrackId;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public float getCornerRadius() {
        return mCornerRadius;
    }

    /**
     * {@code AliyunMixBorderParam} builder static inner class.
     */
    /****
     * {@code AliyunMixBorderParam} builder static inner class.
     */
    @Visible
    public static final class Builder {
        private int mTrackId;
        private int mBorderWidth;
        private int mBorderColor;
        private float mCornerRadius;
        public Builder() {
        }

        /**
         * 设置视频流id
         * @param id 视频流id
         * @return a reference to this Builder
         */
        /****
         * Sets the video stream id
         * @param id video stream id
         * @return A reference to this Builder.
         */
        public Builder trackId(int id){
            mTrackId = id;
            return this;
        }

        /**
         * 设置视频边框宽度
         * @param width 边框宽度
         * @return a reference to this Builder
         */
        /****
         * Sets the border width of the video
         * @param width border width
         * @return A reference to this Builder.
         */
        public Builder borderWidth(int width){
            mBorderWidth = width;
            return this;
        }

        /**
         * 设置视频边框颜色
         * @param color 边框颜色
         * @return a reference to this Builder
         */
        /****
         * Sets the border color of the video
         * @param color border color
         * @return A reference to this Builder.
         */
        public Builder borderColor(int color){
            mBorderColor = color;
            return this;
        }

        /**
         * 设置视频圆角半径
         * @param radius 圆角半径
         * @return a reference to this Builder
         */
        /****
         * Sets the video corner radius
         * @param radius corner radius1
         * @return A reference to this Builder.
         */
        public Builder cornerRadius(float radius){
            mCornerRadius = radius;
            return this;
        }

        /**
         * Returns a {@code AliyunMixBorderParam} built from the parameters previously set.
         *
         * @return a {@code AliyunMixBorderParam} built with parameters of this {@code AliyunMixBorderParam.Builder}
         */
        /****
         * Returns a {@code AliyunMixBorderParam} built from the parameters previously set.
         *
         * @return A {@code AliyunMixBorderParam} built with parameters of {@code AliyunMixBorderParam.Builder}.
         */
        public AlivcMixBorderParam build(){
            return new AlivcMixBorderParam(this);
        }
    }
}
