package com.alivc.live.pusher.demo.test;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alivc.live.pusher.demo.BuildConfig;
import com.alivc.live.pusher.demo.R;
import com.aliyun.interactive_common.utils.AliLiveUserSigGenerate;
import com.alivc.live.commonutils.ContextUtils;
import com.alivc.live.commonbiz.SharedPreferenceUtils;

/**
 * Created by baorunchen on 2022/8/31.
 * <p>
 * Develop test data class. ONLY FOR TEST!!!!
 * <p>
 * First, we store dev test data into `local.properties` file, it's a gitignore file.
 * <p>
 * Such as:
 * sdk.dir=/Users/keria/Library/Android/sdk
 * push.url=rtmp://push-demo-rtmp.aliyunlive.com/test/stream/keriatest?&auth_key=xxxx
 * pull.url=
 * <p>
 * And then, we can parse it from `build.gradle` file,
 * if `local.properties` hasn't the property, it will use the default value.
 */
public class PushDemoTestConstants {
    private static final String PLACEHOLDER = "PLACEHOLDER";

    /**
     * Print version info for demo apk
     */
    public static void printVersionInfo() {
        Log.d("livepushdemo", "BUILD_TYPE--->" + BuildConfig.BUILD_TYPE);
        Log.d("livepushdemo", "MTL_BUILD_ID--->" + BuildConfig.MTL_BUILD_ID);
        Log.d("livepushdemo", "MTL_BUILD_TIMESTAMP--->" + BuildConfig.MTL_BUILD_TIMESTAMP);
    }

    /**
     * Get test push url, you won't need to scan url again!!!
     * <p>
     * put your test push url into `local.properties`
     * such as: push.url=rtmp://xxx
     *
     * @return test push url
     */
    public static String getTestPushUrl() {
        Context context = ContextUtils.getContext();
        return (context != null) ? context.getString(R.string.test_push_url) : "";
    }

    /**
     * Get test pull url, you won't need to scan url again!!!
     * <p>
     * put your test pull url into `local.properties`
     * such as: pull.url=rtmp://xxx
     *
     * @return test push url
     */
    public static String getTestPullUrl() {
        Context context = ContextUtils.getContext();
        return (context != null) ? context.getString(R.string.test_pull_url) : "";
    }

    /**
     * Get test interactive app id
     * <p>
     * put your app id into `local.properties`
     * such as: interactive.appid=keriatest-appid
     * <p>
     * or, you can export it into system environment
     * such as: export INTERACTIVE_APP_ID=keriatest-appid
     *
     * @return interactive app id
     */
    public static String getTestInteractiveAppID() {
        if (!checkIsPlaceholder(AliLiveUserSigGenerate.ALILIVE_APPID)) {
            return AliLiveUserSigGenerate.ALILIVE_APPID;
        }
        
        if(!TextUtils.isEmpty(SharedPreferenceUtils.getAppId(ContextUtils.getApplicationContext()))){
            return SharedPreferenceUtils.getAppId(ContextUtils.getApplicationContext());
        }

        if (!TextUtils.isEmpty(BuildConfig.INTERACTIVE_APP_ID)) {
            return BuildConfig.INTERACTIVE_APP_ID;
        }

        Context context = ContextUtils.getContext();
        if (context != null) {
            return context.getString(R.string.interactive_appid);
        }

        return AliLiveUserSigGenerate.ALILIVE_APPID;
    }

    /**
     * Get test interactive app key
     * <p>
     * put your app key into `local.properties`
     * such as: interactive.appkey=keriatest-appkey
     * <p>
     * or, you can export it into system environment
     * such as: export INTERACTIVE_APP_KEY=keriatest-appkey
     *
     * @return interactive app key
     */
    public static String getTestInteractiveAppKey() {
        if (!checkIsPlaceholder(AliLiveUserSigGenerate.ALILIVE_APPKEY)) {
            return AliLiveUserSigGenerate.ALILIVE_APPKEY;
        }

        if(!TextUtils.isEmpty(SharedPreferenceUtils.getAppKey(ContextUtils.getApplicationContext()))){
            return SharedPreferenceUtils.getAppKey(ContextUtils.getApplicationContext());
        }

        if (!TextUtils.isEmpty(BuildConfig.INTERACTIVE_APP_KEY)) {
            return BuildConfig.INTERACTIVE_APP_KEY;
        }

        Context context = ContextUtils.getContext();
        if (context != null) {
            return context.getString(R.string.interactive_appkey);
        }

        return AliLiveUserSigGenerate.ALILIVE_APPKEY;
    }

    /**
     * Get test interactive play domain
     * <p>
     * put your app key into `local.properties`
     * such as: interactive.playdomain=pullkeriatest.alivecdn.com
     * <p>
     * or, you can export it into system environment
     * such as: export INTERACTIVE_PLAY_DOMAIN=pullkeriatest.alivecdn.com
     *
     * @return interactive play domain
     */
    public static String getTestInteractivePlayDomain() {
        if (!checkIsPlaceholder(AliLiveUserSigGenerate.ALILIVE_PLAY_DOMAIN)) {
            return AliLiveUserSigGenerate.ALILIVE_PLAY_DOMAIN;
        }

        if(!TextUtils.isEmpty(SharedPreferenceUtils.getPlayDomain(ContextUtils.getApplicationContext()))){
            return SharedPreferenceUtils.getPlayDomain(ContextUtils.getApplicationContext());
        }

        if (!TextUtils.isEmpty(BuildConfig.INTERACTIVE_PLAY_DOMAIN)) {
            return BuildConfig.INTERACTIVE_PLAY_DOMAIN;
        }

        Context context = ContextUtils.getContext();
        if (context != null) {
            return context.getString(R.string.interactive_play_domain);
        }

        return AliLiveUserSigGenerate.ALILIVE_PLAY_DOMAIN;
    }

    /**
     * Check whether configuration value is placeholder or not
     *
     * @param configuration configuration value
     * @return true, false
     */
    public static boolean checkIsPlaceholder(String configuration) {
        return TextUtils.isEmpty(configuration) || TextUtils.equals(configuration, PLACEHOLDER);
    }
}
