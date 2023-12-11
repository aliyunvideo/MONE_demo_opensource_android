package com.aliyun.svideo.mixrecorder.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceUtils {

    private static final String SHAREDPRE_FILE = "svideo";


    private static final String AUTOFOCUS = "autofocus";
    private static final String PREVIEW_MIRROR = "previewmirror";
    private static final String PUSH_MIRROR = "pushmirror";
    private static final String TARGET_BIT = "target_bit";
    private static final String MIN_BIT = "min_bit";
    private static final String SHOWGUIDE = "guide";
    private static final String HINT_TARGET_BIT = "hint_target_bit";
    private static final String HINT_MIN_BIT = "hint_min_bit";


    private static final String ROLE_AUDIENCE_USER = "role_audience";
    private static final String ROLE_HOST_USER = "role_host";
    private static final String FORBID_USER = "forbid_user";
    private static final String USER_INFO = "user_info";

    private static final String NETCONFIG = "netConfig";

    private static final boolean DEFAULT_VALUE_PREVIEW_MIRROR = false;
    private static final boolean DEFAULT_VALUE_AUTO_FOCUS = false;
    private static final boolean DEFAULT_VALUE_PUSH_MIRROR = false;


    public static void setNetconfig(Context context, int netConfig) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(NETCONFIG, netConfig);
        editor.commit();
    }

    public static int getNetconfig(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int config = sharedPreferences.getInt(NETCONFIG, 0);
        return config;
    }


    public static void setPreviewMirror(Context context, boolean previewMirror) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(PREVIEW_MIRROR, previewMirror);
        editor.commit();
    }

    public static boolean isPreviewMirror(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        boolean previewMirror = sharedPreferences.getBoolean(PREVIEW_MIRROR, DEFAULT_VALUE_PREVIEW_MIRROR);
        return previewMirror;
    }

    public static void setPushMirror(Context context, boolean pushMirror) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(PUSH_MIRROR, pushMirror);
        editor.commit();
    }

    public static boolean isPushMirror(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        boolean pushMirror = sharedPreferences.getBoolean(PUSH_MIRROR, DEFAULT_VALUE_PUSH_MIRROR);
        return pushMirror;
    }

    public static void setAutofocus(Context context, boolean autofocus) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(AUTOFOCUS, autofocus);
        editor.commit();
    }

    public static boolean isAutoFocus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        boolean autoFocus = sharedPreferences.getBoolean(AUTOFOCUS, DEFAULT_VALUE_AUTO_FOCUS);
        return autoFocus;
    }

    public static void setTargetBit(Context context, int target) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(TARGET_BIT, target);
        editor.commit();
    }

    public static int getTargetBit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int target = sharedPreferences.getInt(TARGET_BIT, 0);
        return target;
    }

    public static void setMinBit(Context context, int min) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(MIN_BIT, min);
        editor.commit();
    }

    public static int getMinBit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int min = sharedPreferences.getInt(MIN_BIT, 0);
        return min;
    }

    public static void setHintTargetBit(Context context, int hintTarget) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(HINT_TARGET_BIT, hintTarget);
        editor.commit();
    }

    public static int getHintTargetBit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int hintTarget = sharedPreferences.getInt(HINT_TARGET_BIT, 0);
        return hintTarget;
    }

    public static void setHintMinBit(Context context, int hintMin) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(HINT_MIN_BIT, hintMin);
        editor.commit();
    }

    public static int getHintMinBit(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int hintMin = sharedPreferences.getInt(HINT_MIN_BIT, 0);
        return hintMin;
    }



    public static void setGuide(Context context, boolean guide) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putBoolean(SHOWGUIDE, guide);
        editor.commit();
    }

    public static boolean isGuide(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        boolean autoFocus = sharedPreferences.getBoolean(SHOWGUIDE, true);
        return autoFocus;
    }


    public static void setAudienceUser(Context context, int userid) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(ROLE_AUDIENCE_USER, userid);
        editor.commit();
    }

    public static int getAudienceUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        int audienceUser = sharedPreferences.getInt(ROLE_AUDIENCE_USER, -1);
        return audienceUser;
    }


    public static String getUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        String userInfo = sharedPreferences.getString(USER_INFO, "");
        return userInfo;
    }

    public static void setForbidUser(Context context, String users) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(FORBID_USER, users);
        editor.commit();
    }

    public static String getForbidUser(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        String forbidUser = sharedPreferences.getString(FORBID_USER, "");
        return forbidUser;
    }

    public static void clear(Context context) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(AUTOFOCUS);
        editor.remove(PREVIEW_MIRROR);
        editor.remove(PUSH_MIRROR);
        editor.remove(TARGET_BIT);
        editor.remove(MIN_BIT);
        editor.remove(HINT_MIN_BIT);
        editor.remove(HINT_TARGET_BIT);
        editor.remove(NETCONFIG);
        editor.commit();
    }

}
