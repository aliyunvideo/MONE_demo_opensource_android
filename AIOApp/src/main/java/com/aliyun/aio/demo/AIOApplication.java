package com.aliyun.aio.demo;

import com.alibaba.android.arouter.launcher.ARouter;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.aliyun.aio.avtheme.AVBaseThemeApplication;
import com.aliyun.aio.demo.utils.ReflectUtils;
import com.aliyun.common.AlivcBase;
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
        AlivcBase.setIntegrationWay("Demo_AIO");
        mUgcApplication = createChildApplication("com.aliyun.alivcsolution.MutiApplication");
        if(mUgcApplication != null) {
            ReflectUtils.reflect(mUgcApplication).method("attachBaseContext",base);
        }
        mPushApplication = createChildApplication("com.alivc.live.pusher.demo.LiveApplication");
        if (mPushApplication != null) {
            ReflectUtils.reflect(mPushApplication).method("attachBaseContext",base);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String licenseKey = appInfo.metaData.getString("com.aliyun.alivc_license.licensekey");
            if (TextUtils.isEmpty(licenseKey)) {
                throw new RuntimeException("\n\n\n" +
                        "=============================================================================================\n" +
                        "=        视频云终端SDK，License未设置\n" +
                        "=        请到阿里云官网(https://ice.console.aliyun.com/sdks/mine/list)申请，\n" +
                        "=        并设置到项目AIOApp/src/main/AndroidManifest.xml 中 com.aliyun.alivc_license.licensekey meta标签中\n" +
                        "=============================================================================================\n\n\n");
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (mUgcApplication != null) {
            mUgcApplication.onCreate();
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
