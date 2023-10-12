package com.aliyun.auiplayerapp;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class PlayerApplication extends MultiDexApplication {
    static {
        System.loadLibrary("RtsSDK");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}
