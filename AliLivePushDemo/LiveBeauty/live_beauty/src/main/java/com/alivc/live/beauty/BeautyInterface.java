package com.alivc.live.beauty;

import com.alivc.live.beauty.constant.BeautyImageFormat;

/**
 * 美颜基础接口类
 * <p>
 * 实现：不同厂商的美颜，须继承自该类进行扩展实现；
 */
public interface BeautyInterface {

    ////////////    1、美颜生命周期相关接口    ////////////

    /**
     * 初始化美颜
     *
     * @note 注意：直播SDK 从v6.2.0 开始，接口发生变动，不再透出内部glcontext，以供外部进行上下文切换，并进行美颜处理
     * @note 后续所有video texture回调，将在gl线程回调；
     * @note 普通直播模式/直播连麦模式，应用层接入美颜Queen SDK的逻辑，将共享使用QueenBeautyImpl中（即：BeautySDKType.QUEEN）
     */
    void init();

    /**
     * 销毁、释放美颜
     */
    void release();

    ////////////    2、美颜接口    ////////////

    /**
     * 开启/关闭美颜
     *
     * @param enable 是否开启
     */
    void setBeautyEnable(boolean enable);

    ////////////    4、美颜输入输出    ////////////

    /**
     * 纹理输入接口，用于图像处理
     *
     * @param inputTexture  输入纹理id
     * @param textureWidth  纹理宽度
     * @param textureHeight 纹理高度
     * @return 输出纹理
     * @note 默认输出为Sample2D格式的纹理
     */
    int onTextureInput(int inputTexture, int textureWidth, int textureHeight);

    /**
     * 帧数据输入接口，用于图像处理
     *
     * @param image  byte数组形式的帧数据
     * @param format 帧数据类型，rgba、rgb、nv21等，参考{@linkplain BeautyImageFormat}
     * @param width  帧宽
     * @param height 帧高
     * @param stride 顶点间隔
     */
    void onDrawFrame(byte[] image, @BeautyImageFormat int format, int width, int height, int stride);

    void switchCameraId(int cameraId);

    ////////////    5、美颜其它接口    ////////////

    /**
     * 获取版本号
     *
     * @return 版本号
     */
    String getVersion();
}