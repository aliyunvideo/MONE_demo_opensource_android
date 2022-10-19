package com.aliyun.aio.demo;

import com.alibaba.android.arouter.launcher.ARouter;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.aliyun.aio.avtheme.AVBaseThemeApplication;
import com.aliyun.aio.demo.utils.ReflectUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

/**
 * <pre>
 * 作者：@Alibaba
 * <b>版本新增</br>
 *
 * </pre>
 */

public class AIOApplication extends AVBaseThemeApplication {

    Application mUgcApplication ;
    Application mPlayerApplication ;
    Application mPushApplication ;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
        mUgcApplication = createChildApplication("com.aliyun.alivcsolution.MutiApplication");
        if(mUgcApplication != null) {
            ReflectUtils.reflect(mUgcApplication).method("attachBaseContext",base);
        }
        mPlayerApplication = createChildApplication("com.alivc.live.pusher.demo.LiveApplication");
        if (mPlayerApplication != null) {
            ReflectUtils.reflect(mPlayerApplication).method("attachBaseContext",base);
        }
        mPushApplication = createChildApplication("com.aliyun.player.demo.PlayerApplication");
        if (mPushApplication != null) {
            ReflectUtils.reflect(mPushApplication).method("attachBaseContext",base);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mUgcApplication != null) {
            mUgcApplication.onCreate();
        }
        if (mPlayerApplication != null) {
            mPlayerApplication.onCreate();
        }
        if (mPushApplication != null) {
            mPushApplication.onCreate();
        }
        if (BuildConfig.DEBUG) {
            ARouter.openLog();
        }
        ARouter.openDebug();
        ARouter.init(this);
        UMConfigure.preInit(getApplicationContext(),"62b9765305844627b5cadb41","aio");
        UMConfigure.init(getApplicationContext(), "62b9765305844627b5cadb41","aio", UMConfigure.DEVICE_TYPE_PHONE, "");
        MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mUgcApplication != null) {
            mUgcApplication.onConfigurationChanged(newConfig);
        }
        if (mPlayerApplication != null) {
            mPlayerApplication.onConfigurationChanged(newConfig);
        }
        if (mPushApplication != null) {
            mPushApplication.onConfigurationChanged(newConfig);
        }

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mUgcApplication != null) {
            mUgcApplication.onLowMemory();
        }
        if (mPlayerApplication != null) {
            mPlayerApplication.onLowMemory();
        }
        if (mPushApplication != null) {
            mPushApplication.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (mUgcApplication != null) {
            mUgcApplication.onTrimMemory(level);
        }
        if (mPlayerApplication != null) {
            mPlayerApplication.onTrimMemory(level);
        }
        if (mPushApplication != null) {
            mPushApplication.onTrimMemory(level);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (mUgcApplication != null) {
            mUgcApplication.onTerminate();
        }
        if (mPlayerApplication != null) {
            mPlayerApplication.onTerminate();
        }
        if (mPushApplication != null) {
            mPushApplication.onTerminate();
        }
    }

    private Application createChildApplication(String className) {
        try {
           return ReflectUtils.reflect(className).newInstance().get();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        return null;
    }

}
