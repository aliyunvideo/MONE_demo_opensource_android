package com.aliyun.ugsv.auibeauty;


import android.content.Context;
import android.hardware.Camera;

import androidx.fragment.app.FragmentManager;

import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;

/**
 * 美颜基础接口类
 * <p>
 * 实现：不同厂商的美颜，须继承自该类进行扩展实现；
 */
public interface BeautyInterface {

    ////////////    1、美颜生命周期相关接口    ////////////

    /**
     * 初始化美颜
     */
    void init(Context context,  IAliyunBeautyInitCallback iAliyunBeautyInitCallback);

    /**
     * 销毁、释放美颜
     */
    void release();


    /**
     * 处理、加载数据
     */
    void initParams();

    /**
     * 显示编辑视图
     */
    void showControllerView(FragmentManager fragmentManager, OnBeautyLayoutChangeListener onBeautyLayoutChangeListener);

    /**
     * 默认美颜回调
     */
    void addDefaultBeautyLevelChangeListener(OnDefaultBeautyLevelChangeListener onDefaultBeautyLevelChangeListener);


    void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info);


    int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix,int currentCameraType);


    void setDeviceOrientation(int deviceOrientation, int displayOrientation);

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    String getVersion();

    /**
     * 获取SDK类型
     *
     * @return SDK类型
     */
    BeautySDKType getSdkType();

    /**
     * 设置debug模式
     *
     * @param isDebug
     */
    void setDebug(boolean isDebug);
}
