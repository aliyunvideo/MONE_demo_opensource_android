package com.aliyun.alivcsolution.utils;

import android.content.Context;


public class LeakCanaryUtils {
    public static void initLeakCanary(Context context) {
        //排除一些Android Sdk引起的泄漏
        //ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
        //                            .instanceField("android.view.inputmethod.InputMethodManager", "sInstance")
        //                            .instanceField("android.view.inputmethod.InputMethodManager", "mLastSrvView")
        //                            .instanceField("com.android.internal.policy.PhoneWindow$DecorView", "mContext")
        //                            .instanceField("androidx.appcompat.widget.SearchView$SearchAutoComplete", "mContext")
        //                            .instanceField("android.app.ActivityThread$ActivityClientRecord", "activity")
        //                            .instanceField("android.media.MediaScannerConnection", "mContext")
        //                            .build();
        //
        //LeakCanary.refWatcher(context)
        //.listenerServiceClass(DisplayLeakService.class)
        //.excludedRefs(excludedRefs)
        //.buildAndInstall();
    }
}
