package com.aliyun.player.alivcplayerexpand.view.dlna.domain;

import androidx.annotation.Nullable;

import org.fourthline.cling.model.meta.Device;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 单例设备列表, 保证全局只有一个设备列表
 */

public class ClingDeviceList {

    private static ClingDeviceList mInstance = null;

    /**
     * 投屏设备列表 都是引用该 list
     */
    private Collection<ClingDevice> mClingDeviceList;

    private ClingDeviceList(){
        mClingDeviceList = new ArrayList<>();
    }

    public static ClingDeviceList getInstance() {
        if (mInstance == null) {
            synchronized (ClingDeviceList.class){
                if(mInstance == null){
                    mInstance = new ClingDeviceList();
                }
            }
        }
        return mInstance;
    }

    public void removeDevice(ClingDevice device){
        mClingDeviceList.remove(device);
    }

    public void addDevice(ClingDevice device){
        if(mClingDeviceList == null){
            return ;
        }
        mClingDeviceList.add(device);
    }

    @Nullable
    public ClingDevice getClingDevice(Device device){
        for (ClingDevice clingDevice : mClingDeviceList){
            Device deviceTemp = clingDevice.getDevice();
            if (deviceTemp != null && deviceTemp.equals(device)){
                return clingDevice;
            }
        }
        return null;
    }

    public boolean contain(Device device){
        for (ClingDevice clingDevice : mClingDeviceList){
            Device deviceTemp = clingDevice.getDevice();
            if (deviceTemp != null && deviceTemp.equals(device)){
                return true;
            }
        }
        return false;
    }

    @Nullable
    public Collection<ClingDevice> getClingDeviceList(){
        return mClingDeviceList;
    }

    public void setClingDeviceList(Collection<ClingDevice> clingDeviceList) {
        mClingDeviceList = clingDeviceList;
    }

    public void destroy(){
        mClingDeviceList = null;
        mInstance = null;
    }
}