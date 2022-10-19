package com.aliyun.player.alivcplayerexpand.view.dlna.manager;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.aliyun.player.alivcplayerexpand.view.dlna.DeviceSearchListener;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingControlPoint;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.ClingDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IControlPoint;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.service.ClingUpnpService;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.ServiceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Cling管理类
 * 对服务操作的代理类
 */
public class ClingManager implements IClingManager {

    private static ClingManager mInstance = null;

    public static final DeviceType DMR_DEVICE_TYPE = new UDADeviceType("MediaRenderer");
    public static final ServiceType AV_TRANSPORT_SERVICE = new UDAServiceType("AVTransport");
    /**
     * 控制服务
     */
    public static final ServiceType RENDERING_CONTROL_SERVICE = new UDAServiceType("RenderingControl");
    private static final long SEARCH_TIMEOUT = 60000;
    private static final long CONNECT_TIMEOUT = 10000;

    private ClingUpnpService mUpnpService;
    private IDeviceManager mDeviceManager;
    private CountDownTimer mSearchTimer;
    private CountDownTimer mConnectTimer;

    private ClingManager() {
    }

    public static ClingManager getInstance() {
        if (mInstance == null) {
            synchronized (ClingManager.class) {
                if (mInstance == null) {
                    mInstance = new ClingManager();
                }
            }
        }
        return mInstance;
    }

    @Override
    public void setUpnpService(ClingUpnpService upnpService) {
        mUpnpService = upnpService;
    }

    @Override
    public void setDeviceManager(IDeviceManager deviceManager) {
        mDeviceManager = deviceManager;
    }

    @Override
    public Registry getRegistry() {
        return mUpnpService.getRegistry();
    }

    @Override
    public void onSearchSuccess() {
        cancelSearchTimer();
    }

    @Override
    public void onStartConnectDevice() {
        startConnectTimer();
    }

    @Override
    public void onDeviceConnected() {
        cancelConnectTimer();
        mDeviceManager.selectedDeviceConnected();
        noticeDeviceConnected();
    }

    @Override
    public void searchDevices() {
        if (mUpnpService != null) {
            noticeSearchStart();
            mUpnpService.getControlPoint().search();
            startSearchTimer();
        }
    }


    @Override
    public Collection<ClingDevice> getDmrDevices() {
        if (mUpnpService == null) {
            return null;
        }

        Collection<Device> devices = mUpnpService.getRegistry().getDevices(DMR_DEVICE_TYPE);
        if (devices == null || devices.size() == 0) {
            return null;
        }

        Collection<ClingDevice> clingDevices = new ArrayList<>();
        for (Device device : devices) {
            ClingDevice clingDevice = new ClingDevice(device);
            clingDevices.add(clingDevice);
            Log.e("AliyunDLNA", "查找设备投屏 : " + clingDevice.getDevice().getDetails().getFriendlyName());
        }
        return clingDevices;
    }

    @Override
    public IControlPoint getControlPoint() {
        if (mUpnpService == null) {
            return null;
        }
        ClingControlPoint.getInstance().setControlPoint(mUpnpService.getControlPoint());

        return ClingControlPoint.getInstance();
    }

    @Override
    public IDevice getSelectedDevice() {
        if (mDeviceManager == null) {
            return null;
        }
        return mDeviceManager.getSelectedDevice();
    }

    @Override
    public void cleanSelectedDevice() {
        if (mDeviceManager == null) {
            return;
        }
        mDeviceManager.cleanSelectedDevice();
    }

    @Override
    public void setSelectedDeviceConnected() {
        if (mDeviceManager == null) {
            return;
        }
        mDeviceManager.selectedDeviceConnected();
    }

    @Override
    public void setSelectedDevice(IDevice device) {
        mDeviceManager.setSelectedDevice(device);
    }

    @Override
    public void registerAVTransport(Context context) {
        if (mDeviceManager == null) {
            return;
        }
        mDeviceManager.registerAVTransport(context);
    }

    @Override
    public void registerRenderingControl(Context context) {
        if (mDeviceManager == null) {
            return;
        }
        mDeviceManager.registerRenderingControl(context);
    }

    /**
     * 搜索设备超时计时
     */
    private void startSearchTimer() {
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
        }
        if (mSearchTimer == null) {
            mSearchTimer = new CountDownTimer(SEARCH_TIMEOUT, SEARCH_TIMEOUT) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    noticeSearchTimeout();
                }
            };
        }
        mSearchTimer.cancel();
        mSearchTimer.start();
    }

    private void cancelSearchTimer() {
        if (mSearchTimer != null) {
            mSearchTimer.cancel();
        }
    }

    /**
     * 连接设备超时计时
     */
    private void startConnectTimer() {
        if (mConnectTimer == null) {
            mConnectTimer = new CountDownTimer(CONNECT_TIMEOUT, CONNECT_TIMEOUT) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    noticeDeviceConnectTimeout();
                }
            };
        }
        mConnectTimer.cancel();
        mConnectTimer.start();
    }

    private void cancelConnectTimer() {
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
        }
    }

    private void noticeSearchStart() {
        Collection<RegistryListener> listeners = getRegistry().getListeners();
        for (RegistryListener listener : listeners) {
            if (listener instanceof DeviceSearchListener) {
                ((DeviceSearchListener) listener).onSearchStart();
            }
        }
    }

    private void noticeSearchTimeout() {
        Collection<RegistryListener> listeners = getRegistry().getListeners();
        for (RegistryListener listener : listeners) {
            if (listener instanceof DeviceSearchListener) {
                ((DeviceSearchListener) listener).onSearchTimeout();
            }
        }
    }


    private void noticeDeviceConnected() {
        Collection<RegistryListener> listeners = getRegistry().getListeners();
        for (RegistryListener listener : listeners) {
            if (listener instanceof DeviceSearchListener) {
                ((DeviceSearchListener) listener).onDeviceConnected();
            }
        }
    }

    private void noticeDeviceConnectTimeout() {
        Collection<RegistryListener> listeners = getRegistry().getListeners();
        for (RegistryListener listener : listeners) {
            if (listener instanceof DeviceSearchListener) {
                ((DeviceSearchListener) listener).onDeviceConnectTimeout();
            }
        }
    }

    @Override
    public void destroy() {
        if (mSearchTimer != null) {
            mSearchTimer.cancel();
            mSearchTimer = null;
        }
        if (mConnectTimer != null) {
            mConnectTimer.cancel();
            mConnectTimer = null;
        }

        mUpnpService.onDestroy();
        mDeviceManager.destroy();
    }
}
