/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.alivcsolution;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.alibaba.sdk.android.vod.upload.ILogger;
import com.alibaba.sdk.android.vod.upload.UploadLogger;
import com.alibaba.sdk.android.vod.upload.model.OSSConfig;
import com.aliyun.aio.avtheme.AVBaseThemeApplication;
import com.aliyun.alivcsolution.utils.StrictModeUtils;
import com.aliyun.ugsv.common.httpfinal.QupaiHttpFinal;
import com.aliyun.svideo.base.http.EffectService;
import com.aliyun.svideo.downloader.DownloaderManager;
import com.aliyun.svideo.editor.effect.VideoEffectResourceManager;
import com.aliyun.svideo.editor.filter.VideoFilterResourceManager;
import com.aliyun.svideosdk.AlivcSdkCore;
import com.aliyun.svideosdk.AlivcSdkCore.AlivcDebugLoggerLevel;
import com.aliyun.svideosdk.AlivcSdkCore.AlivcLogLevel;
import com.aliyun.ugsv.common.utils.ThreadUtils;
import com.aliyunsdk.queen.menu.download.BeautyMenuMaterial;

import static com.aliyun.svideo.base.ui.SdkVersionActivity.DEBUG_DEVELOP_URL;
import static com.aliyun.svideo.base.ui.SdkVersionActivity.DEBUG_PARAMS;

/**
 * Created by Mulberry on 2018/2/24.
 */
public class MutiApplication extends AVBaseThemeApplication {
    /**
     * 友盟数据统计key值
     */
    private static final String UM_APP_KEY = "5c6e4e6cb465f5ff4700120e";
    private String mLogPath;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        QupaiHttpFinal.getInstance().initOkHttpFinal();
        initDownLoader();
        AlivcSdkCore.register(this);
        AlivcSdkCore.setLogLevel(AlivcLogLevel.AlivcLogDebug);
        AlivcSdkCore.setDebugLoggerLevel(AlivcDebugLoggerLevel.AlivcDLAll);
        setSdkDebugParams();
        if (TextUtils.isEmpty(mLogPath)) {
            //保证每次运行app生成一个新的日志文件
            mLogPath = getExternalFilesDir("Log").getAbsolutePath() + "/ShortVideo";
            AlivcSdkCore.setLogPath(mLogPath);
        }
        UploadLogger.setLogger(new ILogger() {
            @Override
            public void onLog(String msg) {
                Log.e("aven", msg);
            }
        });
        ThreadUtils.runOnSubThread(new Runnable() {
            @Override
            public void run() {
                VideoEffectResourceManager.Companion.copyAnimationEffect(getApplicationContext());
                VideoFilterResourceManager.Companion.copyAnimationEffect(getApplicationContext());
            }
        });
        StrictModeUtils.initStrictMode(this);
        EffectService.setAppInfo(getResources().getString(R.string.ugc_app_name), getPackageName(), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE);
    }


    private void initDownLoader() {
        DownloaderManager.init(this);
        BeautyMenuMaterial.getInstance().prepare(this);
    }

    private void setSdkDebugParams() {
        //Demo 调试用，外部客户请勿使用
        SharedPreferences mySharedPreferences = this.getSharedPreferences(DEBUG_PARAMS, Activity.MODE_PRIVATE);
        int hostType = mySharedPreferences.getInt(DEBUG_DEVELOP_URL, 0);
        //AlivcSdkCore.setDebugHostType(hostType);
    }

}
