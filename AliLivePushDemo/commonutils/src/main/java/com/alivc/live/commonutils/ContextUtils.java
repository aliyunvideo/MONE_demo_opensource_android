package com.alivc.live.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

/**
 * Created by keria on 2022/4/6.
 */
public class ContextUtils {
    @SuppressLint("StaticFieldLeak")
    private static Context sContext = null;

    @SuppressLint("StaticFieldLeak")
    private static Context sSafeContext = null;

    public static void setContext(@NonNull Context context) {
        sContext = context;
        sSafeContext = new SafeToastContext(context);
    }

    public static Context getContext() {
        return sContext;
    }

    public static Context getApplicationContext() {
        return sContext;
    }

    public static Context getSafeToastContext() {
        return sSafeContext;
    }
}
