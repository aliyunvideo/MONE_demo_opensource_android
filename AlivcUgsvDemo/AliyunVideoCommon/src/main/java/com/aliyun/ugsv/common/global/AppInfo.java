package com.aliyun.ugsv.common.global;

import android.content.Context;

import com.aliyun.ugsv.common.utils.SignatureUtils;


public class AppInfo {
    private static final String TAG = AppInfo.class.getName();
    private static class AppInfoHolder {
        private static AppInfo sAppInfoInstance = new AppInfo();
    }

    private AppInfo() {}


    private String mSignature;

    public String obtainAppSignature(Context context) {
        if (mSignature == null || "".equals(mSignature)) {
            mSignature = SignatureUtils.getSingInfo(context);
        }
        return mSignature;
    }


    public static AppInfo getInstance() {
        return AppInfoHolder.sAppInfoInstance;
    }
}
