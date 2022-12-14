package com.alivc.live.test;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;

import com.alibaba.ha.adapter.AliHaAdapter;
import com.alibaba.ha.adapter.AliHaConfig;
import com.alibaba.ha.adapter.Plugin;
import com.alibaba.sdk.android.networkmonitor.NetworkMonitorManager;
import com.alibaba.sdk.android.networkmonitor.utils.Logger;
import com.alivc.live.pusher.AlivcLiveBase;
import com.alivc.live.pusher.demo.PushLaunchManager;
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
        registerReceiver(new ConnectivityChangedReceiver(), filter);
        PushLaunchManager.launch4All(this);
        initLancet();
        initHa();
    }

    private void initLancet() {
        String secret = "2d1ef6032b4a824f36c4434f2961a790";
        String version = AlivcLiveBase.getSDKVersion();
//        AndroidSystemUtil.setVersionName(version);
//        Log.i("testtest",version);
        String host = "https://alivcopen.lancet.aliyuncs.com";
        String wsHost = "wss://alivcopen.lancet.aliyuncs.com/ws/device";
//        CoreEngine.init(this, 72, 1, true, false, host,wsHost, secret, "alivcpusher", false, -1, false);
//        RpcProcessor.init(false);
//        WsMessageDispatcher.subscribe(InternalTopic.RPC_CALL.getTopicName(), new RpcWsClientController(false, 30000L));
    }

    private void initHa() {
        AliHaConfig config = new AliHaConfig();
        config.appKey = "333492130";
        config.appVersion = AlivcLiveBase.getSDKVersion();
        config.appSecret = "09d0467c36f14936ae3f68d7b9906156";
        config.channel = "mqc_test";
        config.userNick = null;
        config.application = this;
        config.context = getApplicationContext();
        config.isAliyunos = false;
        //??????CrashReporter
        AliHaAdapter.getInstance().addPlugin(Plugin.crashreporter);
        AliHaAdapter.getInstance().addPlugin(Plugin.apm);
        AliHaAdapter.getInstance().openDebug(true);          //??????????????????
        NetworkMonitorManager.getInstance().addLogger(new Logger() {
            @Override
            public void logd(String s, String s1) {
                Log.d(s, s1);
            }

            @Override
            public void logi(String s, String s1) {
                Log.i(s, s1);
            }

            @Override
            public void logw(String s, String s1) {
                Log.w(s, s1);
            }
        });//?????????????????????
        AliHaAdapter.getInstance().start(config);
    }

    class ConnectivityChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }

}
