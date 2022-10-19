package com.alivc.live.pusher.demo;

import android.app.Application;
import android.content.Context;

import com.aliyun.aio.utils.ThreadUtils;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.alivc.live.pusher.AlivcLivePushInstance;
import com.aliyun.animoji.AnimojiDataFactory;
import com.aliyun.animoji.utils.LogUtils;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;

public class LiveApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setContext(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new LivePushActivity.ConnectivityChangedReceiver(), filter);
        AlivcLivePushInstance.loadInstance(this);
        AnimojiDataFactory.INSTANCE.loadResources(this);
        LogUtils.init(true);
        BeautyMenuMaterial.getInstance().prepare(this);

        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                Common.copyAsset(getApplicationContext());
                Common.copyAll(getApplicationContext());
            }
        });
    }


}
