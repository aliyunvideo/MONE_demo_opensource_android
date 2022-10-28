package com.alivc.live.pusher.demo;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.alivc.live.utils.ContextUtils;

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
        PushLaunchManager.launch4All(this);
    }

}
