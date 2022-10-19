package com.aliyun.aio.avtheme;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.aliyun.aio.avtheme.utils.NightModeUtil;

public class AVBaseThemeApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NightModeUtil.initNightMode(this);
    }
}
