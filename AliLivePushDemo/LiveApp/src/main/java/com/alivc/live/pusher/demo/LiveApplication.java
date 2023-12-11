package com.alivc.live.pusher.demo;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.alivc.live.baselive_common.Common;
import com.alivc.live.baselive_push.ui.LivePushActivity;
import com.alivc.live.commonutils.ContextUtils;

public class LiveApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setContext(this);
        ARouter.init(this);
        Common.copyAsset(this);
        Common.copyAll(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new LivePushActivity.ConnectivityChangedReceiver(), filter);
        PushLaunchManager.launch4All(this);
    }

}
