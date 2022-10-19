package com.aliyun.alivcsolution.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.StrictMode;

/**
 * DATE 2019/06/11
 * <p>描述: 严格模式
 */
public class StrictModeUtils {

    /**
     * 初始化严格模式
     * @param context applicationContext
     */
    public static void initStrictMode(Context context) {
        //ApplicationInfo applicationInfo = context.getApplicationInfo();
        //boolean isDebug = (applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        //if (isDebug) {
        //    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
        //                               .detectDiskReads()
        //                               .detectDiskWrites()
        //                               .detectNetwork()   // or .detectAll() for all detectable problems
        //                               .penaltyLog()
        //                               .build());
        //    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
        //                           .detectLeakedSqlLiteObjects()
        //                           .detectLeakedClosableObjects()
        //                           .penaltyLog()
        //                           .penaltyDeath()
        //                           .build());
        //}
    }
}
