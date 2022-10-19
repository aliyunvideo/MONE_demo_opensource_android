package com.aliyun.svideo.beauty.faceunity.inteface;

import com.aliyun.svideo.beauty.faceunity.bean.FaceUnityBeautyParams;
/**
 * Created by Akira on 2018/5/31.
 */

public interface OnBeautyParamsChangeListener {

    void onBeautyFaceChange(FaceUnityBeautyParams param);

    void onBeautyShapeChange(FaceUnityBeautyParams param);
}
