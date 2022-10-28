package com.aliyun.svideo.recorder.views.control;

/**
 * @author zsy_18 data:2018/7/31
 */
public interface AUIControlViewListener {
    /**
     * 返回事件
     */
    void onBackClick();

    /**
     * 下一个事件
     */
    void onFinishClick();

    /**
     * 显示美颜选择view
     */
    void onBeautyFaceClick();

    void onMusicClick();

    /**
     * 摄像头转换事件
     */
    void onCameraSwitch();

    /**
     * 闪光灯模式
     * @param flashType
     */
    void onLightSwitch(AUIFlashType flashType);

    /**
     * 播放速率选择
     *
     * @param rate
     */
    void onRateSelect(float rate);

    /**
     * 现实动图效果选择view
     */
    void onGifEffectClick();

    /**
     * 倒计时点击
     */
    void onCountdownClick();

    /**
     * 开始录制视频事件
     */
    void onStartRecordClick();

    /**
     * 停止录制视频事件
     */
    void onStopRecordClick();

    /**
     * 删除点击事件
     */
    void onDeleteClick();

    /**
     * 弹出滤镜选择弹窗
     */
    void onFilterEffectClick();
    /**
     * 切换画幅比例
     */
    void onChangeAspectRatioClick();

    /**
     * 弹出特效滤镜选择框
     */
    void onAnimFilterClick();

    /**
     * 拍照
     */
    void onTakePhotoClick();

    /**
     * race的debug开关
     */
    void onRaceDebug(boolean debug);
}