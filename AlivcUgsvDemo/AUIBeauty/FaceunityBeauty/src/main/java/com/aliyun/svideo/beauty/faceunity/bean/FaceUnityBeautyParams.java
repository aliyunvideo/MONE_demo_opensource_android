package com.aliyun.svideo.beauty.faceunity.bean;

/**
 * Created by Akira on 2018/6/20.
 */
public class FaceUnityBeautyParams implements Cloneable {
    /**
     * 美白
     */
    public float beautyWhite = 60;
    /**
     * 磨皮
     */
    public float beautyBuffing = 60;
    /**
     * 红润
     */
    public float beautyRuddy = 60;
    /**
     * 瘦脸
     */
    public float beautySlimFace = 60;
    /**
     * 大眼
     */
    public float beautyBigEye = 60;
    /**
     * 美颜等级
     */
    public int beautyLevel = 3;

    public FaceUnityBeautyParams() {
    }

    @Override
    public String toString() {
        return "FaceUnityBeautyParams{" +
                "beautyWhite=" + beautyWhite +
                ", beautyBuffing=" + beautyBuffing +
                ", beautyRuddy=" + beautyRuddy +
                ", beautySlimFace=" + beautySlimFace +
                ", beautyBigEye=" + beautyBigEye +
                ", level=" + beautyLevel +
                '}';
    }

    @Override
    public FaceUnityBeautyParams clone() {
        FaceUnityBeautyParams beautyParams = null;
        try {
            beautyParams = (FaceUnityBeautyParams) super.clone();
            beautyParams.beautyLevel = this.beautyLevel;
            beautyParams.beautyWhite = this.beautyWhite;
            beautyParams.beautyBuffing = this.beautyBuffing;
            beautyParams.beautyRuddy = this.beautyRuddy;
            beautyParams.beautySlimFace = this.beautySlimFace;
            beautyParams.beautyBigEye = this.beautyBigEye;
            return beautyParams;

        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
