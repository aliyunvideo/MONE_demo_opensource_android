package com.alivc.live.commonutils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Created by keria on 2022/10/12.
 * <p>
 * Android数据持久化
 */
public class SharedPrefUtils {

    private static final String PREF_APP = "livepush";

    private SharedPrefUtils() {
    }

    /**
     * Gets boolean data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     * @return the boolean data
     */
    public static boolean getBooleanData(@NonNull Context context, String key, boolean val) {
        return getSharedPref(context).getBoolean(key, val);
    }

    /**
     * Gets int data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     * @return the int data
     */
    public static int getIntData(@NonNull Context context, String key, int val) {
        return getSharedPref(context).getInt(key, val);
    }

    /**
     * Gets string data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     * @return the string data
     */
    public static String getStringData(@NonNull Context context, String key, String val) {
        return getSharedPref(context).getString(key, val);
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     */
    public static void saveData(@NonNull Context context, String key, String val) {
        getSharedPrefEditor(context).putString(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     */
    public static void saveData(@NonNull Context context, String key, int val) {
        getSharedPrefEditor(context).putInt(key, val).apply();
    }

    /**
     * Save data.
     *
     * @param context the context
     * @param key     the key
     * @param val     default value
     */
    public static void saveData(@NonNull Context context, String key, boolean val) {
        getSharedPrefEditor(context).putBoolean(key, val).apply();
    }

    /**
     * Clear all data
     *
     * @param context the context
     */
    public static void clear(@NonNull Context context) {
        getSharedPrefEditor(context).clear();
    }

    private static SharedPreferences getSharedPref(@NonNull Context context) {
        return context.getSharedPreferences(PREF_APP, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor getSharedPrefEditor(@NonNull Context context) {
        return getSharedPref(context).edit();
    }
}
