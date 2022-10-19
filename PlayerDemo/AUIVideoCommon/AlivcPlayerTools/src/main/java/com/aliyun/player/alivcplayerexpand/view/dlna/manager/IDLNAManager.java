package com.aliyun.player.alivcplayerexpand.view.dlna.manager;

import android.content.Context;


import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IControlPoint;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;

import java.util.Collection;

/**
 * DLNA管理类
 *
 * @author hanyu
 */
public interface IDLNAManager {

    /**
     * 搜索设备
     */
    void searchDevices();

    /**
     * 获取支持 Media 类型的设备
     */
    Collection<? extends IDevice> getDmrDevices();

    /**
     * 获取控制点
     */
    IControlPoint getControlPoint();

    /**
     * 获取选中的设备
     *
     * @return 选中的设备
     */
    IDevice getSelectedDevice();

    /**
     * 取消选中的设备
     */
    void cleanSelectedDevice();

    /**
     * 连接成功后，设置device为连接状态
     */
    void setSelectedDeviceConnected();

    /**
     * 设置选中的设备
     *
     * @param device 已选中设备
     */
    void setSelectedDevice(IDevice device);

    /**
     * 监听投屏端 AVTransport 回调
     */
    void registerAVTransport(Context context);

    /**
     * 监听投屏端 RenderingControl 回调
     */
    void registerRenderingControl(Context context);

    /**
     * 销毁
     */
    void destroy();

}
