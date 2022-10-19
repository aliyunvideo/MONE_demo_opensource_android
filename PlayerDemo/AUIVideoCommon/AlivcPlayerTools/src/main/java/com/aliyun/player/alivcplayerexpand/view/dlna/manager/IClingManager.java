package com.aliyun.player.alivcplayerexpand.view.dlna.manager;

import com.aliyun.player.alivcplayerexpand.view.dlna.service.ClingUpnpService;

import org.fourthline.cling.registry.Registry;

public interface IClingManager extends IDLNAManager{

    void setUpnpService(ClingUpnpService upnpService);

    void setDeviceManager(IDeviceManager deviceManager);

    Registry getRegistry();

    void onSearchSuccess();

    void onStartConnectDevice();

    void onDeviceConnected();
}
