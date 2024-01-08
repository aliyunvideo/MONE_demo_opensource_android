package com.alivc.live.commonbiz.seidelay.handle;

import android.util.Log;

import com.alivc.live.commonbiz.seidelay.SEISourceType;
import com.alivc.live.commonbiz.seidelay.api.ISEIDelayEventListener;
import com.alivc.live.commonbiz.seidelay.api.ISEIDelayHandle;
import com.alivc.live.commonbiz.seidelay.data.SEIDelayProtocol;
import com.alivc.live.commonbiz.seidelay.time.SEIDelayTimeHandler;

import java.util.Map;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public class SEIDelayReceiver extends ISEIDelayHandle {
    private static final String TAG = "SEIDelayReceiver";

    private static final long INVALID_THRESH_HOLD = 1000 * 60L;

    @Override
    public void destroy() {
        super.destroy();
    }

    public void receiveSEI(SEISourceType sourceType, String sei) {
        if (listenerHashMap.isEmpty()) {
            return;
        }

        if (!SEIDelayTimeHandler.isNtpTimeUpdated()) {
            return;
        }

        SEIDelayProtocol dataProtocol = new SEIDelayProtocol(sei);
        long ntpTimestamp = SEIDelayTimeHandler.getCurrentTimestamp();
        long delay = ntpTimestamp - dataProtocol.tm;
        if (delay > INVALID_THRESH_HOLD) {
            return;
        }

        Log.i(TAG, "receiveSEI: [" + dataProtocol.src + "][" + sourceType + "][" + delay + "ms]");
        for (Map.Entry<String, ISEIDelayEventListener> entry : listenerHashMap.entrySet()) {
            ISEIDelayEventListener eventListener = entry.getValue();
            if (eventListener != null) {
                eventListener.onEvent(dataProtocol.src, sourceType.toString(), String.valueOf(delay));
            }
        }
    }
}