package com.alivc.live.commonbiz.seidelay;

import com.alivc.live.commonbiz.seidelay.api.ISEIDelayEventListener;
import com.alivc.live.commonbiz.seidelay.handle.SEIDelayProvider;
import com.alivc.live.commonbiz.seidelay.handle.SEIDelayReceiver;
import com.alivc.live.commonbiz.seidelay.time.SEIDelayTimeHandler;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public class SEIDelayManager {
    private SEIDelayProvider mProvider;
    private SEIDelayReceiver mReceiver;

    public SEIDelayManager() {
        requestNTPTime();
    }

    public void registerProvider(String src, ISEIDelayEventListener listener) {
        mProvider = new SEIDelayProvider();
        mProvider.addListener(src, listener);
    }

    public void registerReceiver(ISEIDelayEventListener listener) {
        mReceiver = new SEIDelayReceiver();
        mReceiver.addListener("receiver", listener);
    }

    public void receiveSEI(SEISourceType sourceType, String sei) {
        if (mReceiver != null) {
            mReceiver.receiveSEI(sourceType, sei);
        }
    }

    private void requestNTPTime() {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SEIDelayTimeHandler.requestNTPTime();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (mProvider != null) {
            mProvider.destroy();
            mProvider = null;
        }
        if (mReceiver != null) {
            mReceiver.destroy();
            mReceiver = null;
        }
    }
}
