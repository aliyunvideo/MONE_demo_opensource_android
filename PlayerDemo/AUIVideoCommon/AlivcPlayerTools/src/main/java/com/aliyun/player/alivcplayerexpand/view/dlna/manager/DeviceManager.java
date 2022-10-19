package com.aliyun.player.alivcplayerexpand.view.dlna.manager;


import android.content.Context;
import android.util.Log;


import com.aliyun.player.alivcplayerexpand.view.dlna.control.SubscriptionControl;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDeviceList;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.Config;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;

import java.util.Collection;

/**
 * DeviceManager实现类
 */

public class DeviceManager implements IDeviceManager {
    private static final String TAG = DeviceManager.class.getSimpleName();
    /**
     * 已选中的设备, 它也是 ClingDeviceList 中的一员
     */
    private ClingDevice mSelectedDevice;
    private SubscriptionControl mSubscriptionControl;

    public DeviceManager() {
        mSubscriptionControl = new SubscriptionControl();
    }

    @Override
    public IDevice getSelectedDevice() {
        return mSelectedDevice;
    }

    @Override
    public void setSelectedDevice(IDevice selectedDevice) {
//        if (selectedDevice != mSelectedDevice){
//            Intent intent = new Intent(Intents.ACTION_CHANGE_DEVICE);
//            sendBroadcast(intent);
//        }

        Log.i(TAG, "Change selected device.");
        mSelectedDevice = (ClingDevice) selectedDevice;

        // 重置选中状态
        Collection<ClingDevice> clingDeviceList = ClingDeviceList.getInstance().getClingDeviceList();
        if (clingDeviceList != null){
            for (ClingDevice device : clingDeviceList){
                device.setNormalState();
            }
        }
        // 设置正在连接状态
        mSelectedDevice.setConnectingState();
        // 清空状态
        Config.getInstance().setHasRelTimePosCallback(false);
    }

    @Override
    public void cleanSelectedDevice() {
        if (mSelectedDevice == null){
            return;
        }
        mSelectedDevice.setNormalState();
    }

    @Override
    public void selectedDeviceConnected() {
        if (mSelectedDevice == null){
            return;
        }
        mSelectedDevice.setConnectedState();
    }

    @Override
    public void registerAVTransport(Context context) {
        if (mSelectedDevice == null){
            return;
        }
        mSubscriptionControl.registerAVTransport(mSelectedDevice, context);
    }

    @Override
    public void registerRenderingControl(Context context) {
        if (mSelectedDevice == null){
            return;
        }
        mSubscriptionControl.registerRenderingControl(mSelectedDevice, context);
    }

    @Override
    public void destroy() {
        if (mSubscriptionControl != null){
            mSubscriptionControl.destroy();
        }
    }
}
