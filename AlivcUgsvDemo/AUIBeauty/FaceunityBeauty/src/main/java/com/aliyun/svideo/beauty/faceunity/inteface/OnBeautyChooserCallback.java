package com.aliyun.svideo.beauty.faceunity.inteface;

import com.aliyun.svideo.beauty.faceunity.bean.FaceUnityBeautyParams;


public interface OnBeautyChooserCallback {
    void onShowFaceDetailView(int level);
    void onShowSkinDetailView(int type);
    void onChooserBlankClick();
    void onChooserBackClick();
    void onChooserKeyBackClick();
    void onFaceLevelChanged(int level);
    void onBeautyFaceChange(FaceUnityBeautyParams param);
    void onBeautyShapeChange(FaceUnityBeautyParams param);
    void onShapeTypeChange(int type);

}
