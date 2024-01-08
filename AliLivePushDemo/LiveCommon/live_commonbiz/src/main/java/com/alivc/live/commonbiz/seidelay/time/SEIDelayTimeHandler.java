package com.alivc.live.commonbiz.seidelay.time;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */

import android.util.Log;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.net.InetAddress;

public class SEIDelayTimeHandler {

    private static final String TAG = "SEIDelayTimeHandler";
    private static final String NTP_HOST = "ntp.aliyun.com";

    private static long NTP_DELAY = 0L;
    private static boolean NTP_TIME_UPDATED = false;

    private SEIDelayTimeHandler() {
    }

    public static void requestNTPTime() {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(5000);
        try {
            InetAddress ntpServerAddress = InetAddress.getByName(NTP_HOST);
            Log.i(TAG, "Connecting to NTP server: " + NTP_HOST);

            TimeInfo timeInfo = client.getTime(ntpServerAddress);
            timeInfo.computeDetails();
            long returnTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
            long systemTime = System.currentTimeMillis();
            NTP_DELAY = returnTime - systemTime;
            NTP_TIME_UPDATED = true;
            Log.i(TAG, "Current time (from " + NTP_HOST + "): " + returnTime);
            Log.i(TAG, "Current time (from system): " + systemTime);
            Log.i(TAG, "Delay (NTP-system): " + NTP_DELAY + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.close();
        }
    }

    public static boolean isNtpTimeUpdated() {
        return NTP_TIME_UPDATED;
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis() + NTP_DELAY;
    }
}

