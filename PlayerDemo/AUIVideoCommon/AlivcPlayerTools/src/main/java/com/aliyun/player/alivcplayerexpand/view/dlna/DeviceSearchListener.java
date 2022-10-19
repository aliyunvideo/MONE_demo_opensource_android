package com.aliyun.player.alivcplayerexpand.view.dlna;


import com.aliyun.player.alivcplayerexpand.view.dlna.callback.DeviceListChangedListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDeviceList;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.ClingManager;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

/**
 * 搜索设备监听
 *
 * @author hanyu
 */
public class DeviceSearchListener extends DefaultRegistryListener {

    private DeviceListChangedListener mOnDeviceListChangedListener;

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        deviceAdded(device);
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        deviceRemoved(device);
    }

    @Override
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {
    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {
        deviceRemoved(device);
    }

    public void setOnDeviceListChangedListener(DeviceListChangedListener onDeviceListChangedListener) {
        mOnDeviceListChangedListener = onDeviceListChangedListener;
    }

    public void onSearchStart() {
        if (mOnDeviceListChangedListener != null) {
            mOnDeviceListChangedListener.onDeviceSearchStart();
        }
    }

    public void onSearchTimeout() {
        if (mOnDeviceListChangedListener != null) {
            mOnDeviceListChangedListener.onDeviceSearchTimeout();
        }
    }
    public void onDeviceConnected(){
        if (mOnDeviceListChangedListener != null) {
            mOnDeviceListChangedListener.onDeviceConnected();
        }
    }
    public void onDeviceConnectTimeout(){
        if (mOnDeviceListChangedListener != null) {
            mOnDeviceListChangedListener.onDeviceConnectTimeout();
        }
    }

    private void deviceAdded(Device device) {
        if (!device.getType().equals(ClingManager.DMR_DEVICE_TYPE)) {
            return;
        }

        if (mOnDeviceListChangedListener != null) {
            ClingDevice clingDevice = new ClingDevice(device);
            ClingDeviceList.getInstance().addDevice(clingDevice);
            mOnDeviceListChangedListener.onDeviceAdded(clingDevice);
        }
    }

    public void deviceRemoved(Device device) {
        if (mOnDeviceListChangedListener != null) {
            ClingDevice clingDevice = ClingDeviceList.getInstance().getClingDevice(device);
            if (clingDevice != null) {
                ClingDeviceList.getInstance().removeDevice(clingDevice);
                mOnDeviceListChangedListener.onDeviceRemoved(clingDevice);
            }
        }
    }
}
