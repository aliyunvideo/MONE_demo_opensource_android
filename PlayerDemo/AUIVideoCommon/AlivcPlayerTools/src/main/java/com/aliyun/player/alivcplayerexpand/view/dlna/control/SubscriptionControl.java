package com.aliyun.player.alivcplayerexpand.view.dlna.control;

import android.content.Context;
import androidx.annotation.NonNull;


import com.aliyun.player.alivcplayerexpand.util.ClingUtils;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.AVTransportSubscriptionCallback;
import com.aliyun.player.alivcplayerexpand.view.dlna.callback.RenderingControlSubscriptionCallback;
import com.aliyun.player.alivcplayerexpand.view.dlna.domain.IDevice;
import com.aliyun.player.alivcplayerexpand.view.dlna.manager.ClingManager;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.meta.Device;

/**
 * 控制点
 */

public class SubscriptionControl implements ISubscriptionControl<Device> {

    private AVTransportSubscriptionCallback mAVTransportSubscriptionCallback;
    private RenderingControlSubscriptionCallback mRenderingControlSubscriptionCallback;

    public SubscriptionControl() {
    }

    @Override
    public void registerAVTransport(@NonNull IDevice<Device> device, @NonNull Context context) {
        if (mAVTransportSubscriptionCallback != null) {
            mAVTransportSubscriptionCallback.end();
        }
        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl == null) {
            return;
        }

        mAVTransportSubscriptionCallback = new AVTransportSubscriptionCallback(device.getDevice().findService(ClingManager.AV_TRANSPORT_SERVICE), context);
        controlPointImpl.execute(mAVTransportSubscriptionCallback);
    }

    @Override
    public void registerRenderingControl(@NonNull IDevice<Device> device, @NonNull Context context) {
        if (mRenderingControlSubscriptionCallback != null) {
            mRenderingControlSubscriptionCallback.end();
        }
        final ControlPoint controlPointImpl = ClingUtils.getControlPoint();
        if (controlPointImpl != null) {
            return;
        }
        mRenderingControlSubscriptionCallback = new RenderingControlSubscriptionCallback(device.getDevice().findService(ClingManager
                .RENDERING_CONTROL_SERVICE), context);
        controlPointImpl.execute(mRenderingControlSubscriptionCallback);
    }

    @Override
    public void destroy() {
        if (mAVTransportSubscriptionCallback != null) {
            mAVTransportSubscriptionCallback.end();
        }
        if (mRenderingControlSubscriptionCallback != null) {
            mRenderingControlSubscriptionCallback.end();
        }
    }
}
