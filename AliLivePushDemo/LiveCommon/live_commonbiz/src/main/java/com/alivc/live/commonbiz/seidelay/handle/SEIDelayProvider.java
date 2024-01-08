package com.alivc.live.commonbiz.seidelay.handle;

import com.alivc.live.commonbiz.seidelay.api.ISEIDelayEventListener;
import com.alivc.live.commonbiz.seidelay.api.ISEIDelayHandle;
import com.alivc.live.commonbiz.seidelay.data.SEIDelayProtocol;
import com.alivc.live.commonbiz.seidelay.time.SEIDelayTimeHandler;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public class SEIDelayProvider extends ISEIDelayHandle {
    private static final long SCHEDULED_EXECUTOR_SERVICE_PERIOD = 2 * 1000L;
    private ScheduledExecutorService mScheduledExecutorService = null;

    private volatile boolean mTaskRun = false;

    @Override
    public void addListener(String src, ISEIDelayEventListener listener) {
        super.addListener(src, listener);
        if (!mTaskRun && !listenerHashMap.isEmpty()) {
            mTaskRun = true;
            startTask();
        }
    }

    @Override
    public void removeListener(String src) {
        super.removeListener(src);
        if (mTaskRun && listenerHashMap.isEmpty()) {
            mTaskRun = false;
            stopTask();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        stopTask();
    }

    private void startTask() {
        stopTask();
        mScheduledExecutorService = Executors.newScheduledThreadPool(8);
        mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                provideSEI();
            }
        }, 0, SCHEDULED_EXECUTOR_SERVICE_PERIOD, TimeUnit.MILLISECONDS);
    }

    private void stopTask() {
        try {
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdown();
                if (!mScheduledExecutorService.awaitTermination(1000, TimeUnit.MICROSECONDS)) {
                    mScheduledExecutorService.shutdownNow();
                }
                mScheduledExecutorService = null;
            }
        } catch (InterruptedException e) {
            if (mScheduledExecutorService != null) {
                mScheduledExecutorService.shutdownNow();
            }
            e.printStackTrace();
        }
    }

    private void provideSEI() {
        for (Map.Entry<String, ISEIDelayEventListener> entry : listenerHashMap.entrySet()) {
            ISEIDelayEventListener eventListener = entry.getValue();
            if (eventListener != null) {
                if (SEIDelayTimeHandler.isNtpTimeUpdated()) {
                    long ntpTimestamp = SEIDelayTimeHandler.getCurrentTimestamp();
                    SEIDelayProtocol dataProtocol = new SEIDelayProtocol(entry.getKey(), ntpTimestamp);
                    eventListener.onEvent(dataProtocol.src, dataProtocol.src, dataProtocol.toString());
                }
            }
        }
    }
}
