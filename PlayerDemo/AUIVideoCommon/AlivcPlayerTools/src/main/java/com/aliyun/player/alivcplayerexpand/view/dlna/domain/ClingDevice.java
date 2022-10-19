package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import org.fourthline.cling.model.meta.Device;

/**
 * ClingDevices
 */

public class ClingDevice implements IDevice<Device> {

    private Device mDevice;

    private int model = 0;


    public ClingDevice(Device device) {
        this.mDevice = device;
    }

    @Override
    public Device getDevice() {
        return mDevice;
    }


    public void setConnectingState() {
        model = 1;
    }

    public void setConnectedState() {
        model = 2;
    }

    public void setNormalState() {
        model = 0;
    }

    public boolean isConnectedState() {
        return model == 2;
    }

    public boolean isConnectingState() {
        return model == 1;
    }
}