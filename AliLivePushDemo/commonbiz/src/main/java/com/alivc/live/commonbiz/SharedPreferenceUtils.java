package com.alivc.live.commonbiz;

import android.content.Context;

import androidx.annotation.NonNull;

import com.alivc.live.utils.SharedPrefUtils;

/**
 * 推流Demo业务SharedPreference
 */
public class SharedPreferenceUtils {
    private static final String AUTO_FOCUS = "autofocus";
    private static final String PREVIEW_MIRROR = "previewmirror";
    private static final String PUSH_MIRROR = "pushmirror";
    private static final String TARGET_BIT = "target_bit";
    private static final String MIN_BIT = "min_bit";
    private static final String SHOW_GUIDE = "guide";
    private static final String BEAUTY_ON = "beautyon";
    private static final String ANIMOJI_ON = "animoji_on";
    private static final String HINT_TARGET_BIT = "hint_target_bit";
    private static final String HINT_MIN_BIT = "hint_min_bit";
    private static final String DISPLAY_FIT = "display_fit";
    private static final String APP_ID = "app_id";
    private static final String APP_KEY = "app_key";
    private static final String PLAY_DOMAIN = "play_domain";

    public static void setPreviewMirror(@NonNull Context context, boolean previewMirror) {
        SharedPrefUtils.saveData(context, PREVIEW_MIRROR, previewMirror);
    }

    public static boolean isPreviewMirror(@NonNull Context context, boolean defaultValue) {
        return SharedPrefUtils.getBooleanData(context, PREVIEW_MIRROR, defaultValue);
    }

    public static void setPushMirror(@NonNull Context context, boolean pushMirror) {
        SharedPrefUtils.saveData(context, PUSH_MIRROR, pushMirror);
    }

    public static boolean isPushMirror(@NonNull Context context, boolean defaultValue) {
        return SharedPrefUtils.getBooleanData(context, PUSH_MIRROR, defaultValue);
    }

    public static void setAutofocus(@NonNull Context context, boolean autofocus) {
        SharedPrefUtils.saveData(context, AUTO_FOCUS, autofocus);
    }

    public static boolean isAutoFocus(@NonNull Context context, boolean defaultValue) {
        return SharedPrefUtils.getBooleanData(context, AUTO_FOCUS, defaultValue);
    }

    public static void setTargetBit(@NonNull Context context, int target) {
        SharedPrefUtils.saveData(context, TARGET_BIT, target);
    }

    public static int getTargetBit(@NonNull Context context) {
        return SharedPrefUtils.getIntData(context, TARGET_BIT, 0);
    }

    public static void setMinBit(@NonNull Context context, int min) {
        SharedPrefUtils.saveData(context, MIN_BIT, min);
    }

    public static int getMinBit(@NonNull Context context) {
        return SharedPrefUtils.getIntData(context, MIN_BIT, 0);
    }

    public static void setHintTargetBit(@NonNull Context context, int hintTarget) {
        SharedPrefUtils.saveData(context, HINT_TARGET_BIT, hintTarget);
    }

    public static int getHintTargetBit(@NonNull Context context) {
        return SharedPrefUtils.getIntData(context, HINT_TARGET_BIT, 0);
    }

    public static void setHintMinBit(@NonNull Context context, int hintMin) {
        SharedPrefUtils.saveData(context, HINT_MIN_BIT, hintMin);
    }

    public static int getHintMinBit(@NonNull Context context) {
        return SharedPrefUtils.getIntData(context, HINT_MIN_BIT, 0);
    }

    public static void setGuide(@NonNull Context context, boolean guide) {
        SharedPrefUtils.saveData(context, SHOW_GUIDE, guide);
    }

    public static boolean isGuide(@NonNull Context context) {
        return SharedPrefUtils.getBooleanData(context, SHOW_GUIDE, true);
    }

    public static void setBeautyOn(@NonNull Context context, boolean beautyOn) {
        SharedPrefUtils.saveData(context, BEAUTY_ON, beautyOn);
    }

    public static boolean isBeautyOn(@NonNull Context context) {
        return SharedPrefUtils.getBooleanData(context, BEAUTY_ON, true);
    }

    public static void setAnimojiOn(@NonNull Context context, boolean animojiOn) {
        SharedPrefUtils.saveData(context, ANIMOJI_ON, animojiOn);
    }

    public static boolean isAnimojiOn(@NonNull Context context) {
        return SharedPrefUtils.getBooleanData(context, ANIMOJI_ON, false);
    }

    public static void setDisplayFit(@NonNull Context context, int displayfit) {
        SharedPrefUtils.saveData(context, DISPLAY_FIT, displayfit);
    }

    public static int getDisplayFit(@NonNull Context context, int defaultValue) {
        return SharedPrefUtils.getIntData(context, DISPLAY_FIT, defaultValue);
    }

    public static void setAppId(@NonNull Context context, String appID) {
        SharedPrefUtils.saveData(context, APP_ID, appID);
    }

    public static void setAppKey(@NonNull Context context, String appKey) {
        SharedPrefUtils.saveData(context, APP_KEY, appKey);
    }

    public static void setPlayDomain(@NonNull Context context, String playDomain) {
        SharedPrefUtils.saveData(context, PLAY_DOMAIN, playDomain);
    }

    public static void setAppInfo(@NonNull Context context, String appId, String appKey, String playDomain) {
        setAppId(context, appId);
        setAppKey(context, appKey);
        setPlayDomain(context, playDomain);
    }

    public static String getAppId(@NonNull Context context) {
        return SharedPrefUtils.getStringData(context, APP_ID, "");
    }

    public static String getAppKey(@NonNull Context context) {
        return SharedPrefUtils.getStringData(context, APP_KEY, "");
    }

    public static String getPlayDomain(@NonNull Context context) {
        return SharedPrefUtils.getStringData(context, PLAY_DOMAIN, "");
    }

    public static void clear(@NonNull Context context) {
        SharedPrefUtils.clear(context);
    }
}
