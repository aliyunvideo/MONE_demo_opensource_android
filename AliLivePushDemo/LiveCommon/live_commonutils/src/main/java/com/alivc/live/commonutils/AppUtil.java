package com.alivc.live.commonutils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author keria
 * @date 2021/7/27
 */
public class AppUtil {

    private static final String BUILD_TYPE_DEBUG = "DEBUG";
    private static final String BUILD_TYPE_RELEASE = "RELEASE";

    private static final String CPU_INFO_FILE_PATH = "/proc/cpuinfo";

    // read from cpu info file only once to reduce method cost.
    private static final ArrayList<String> mCpuInfo = new ArrayList<>();

    private AppUtil() {
    }

    /**
     * Get app name
     *
     * @param context android context
     * @return app name, not null
     */
    @NonNull
    public static String getAppName(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        try {
            ApplicationInfo ai = context.getApplicationInfo();
            PackageManager pm = context.getPackageManager();
            return ai.loadLabel(pm).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get app id
     *
     * @param context android context
     * @return app id, not null
     */
    @NonNull
    public static String getAppId(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        String pn = context.getPackageName();
        return (pn != null) ? pn : "";
    }

    /**
     * Get app build type
     *
     * @param context android context
     * @return app build type, debug or release
     */
    @NonNull
    public static String getApkBuildType(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        boolean isDebug = false;
        try {
            ApplicationInfo ai = context.getApplicationInfo();
            isDebug = (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isDebug ? BUILD_TYPE_DEBUG : BUILD_TYPE_RELEASE;
    }

    /**
     * Get package version name
     *
     * @param context android context
     * @return package version name
     */
    @NonNull
    public static String getPackageVersionName(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        PackageManager manager = context.getPackageManager();

        String name = null;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            name = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return name != null ? name : "";
    }

    /**
     * Get package version code
     *
     * @param context android context
     * @return package version code
     */
    public static int getPackageVersionCode(@Nullable Context context) {
        if (context == null) {
            return 0;
        }
        PackageManager manager = context.getPackageManager();

        int code = 0;
        try {
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            code = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return code;
    }

    /**
     * Get android device unique id
     *
     * @param context android context
     * @return android device unique id, not null
     * @link {https://developer.android.com/training/articles/user-data-ids}
     */
    @SuppressLint("HardwareIds")
    @NonNull
    public static String getDeviceId(@Nullable Context context) {
        if (context != null) {
            try {
                return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "uuid-" + UUID.randomUUID();
    }

    /**
     * Get full cpu info
     *
     * @return full cpu info
     */
    @NonNull
    public static String getFullCPUInfo() {
        if (mCpuInfo.isEmpty()) {
            mCpuInfo.addAll(readFromCpuInfo());
        }

        StringBuilder sb = new StringBuilder();
        for (String line : mCpuInfo) {
            sb.append(line).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * Get brief cpu info
     *
     * @return brief cpu info
     */
    @NonNull
    public static String getBriefCPUInfo() {
        return getCPUHardwareInfo() + "\n" + getCPUProcessorArchInfo();
    }

    /**
     * Get cpu processor name
     *
     * @return cpu processor name
     */
    @NonNull
    public static String getCPUProcessorArchInfo() {
        return getSpecificCpuInfoByKey("Processor");
    }

    /**
     * Get CPU hardware name
     *
     * @return cpu hardware name, not null
     */
    @NonNull
    public static String getCPUHardwareInfo() {
        return getSpecificCpuInfoByKey("Hardware");
    }

    /**
     * Get specific cpu info by key.
     *
     * @param key cpu info key
     * @return cpu info value
     */
    @NonNull
    public static String getSpecificCpuInfoByKey(@NonNull String key) {
        if (mCpuInfo.isEmpty()) {
            mCpuInfo.addAll(readFromCpuInfo());
        }

        String value = "";
        for (String str : mCpuInfo) {
            if (str.startsWith(key)) {
                String[] data = str.split(":");
                if (data.length > 1) {
                    value = data[1].trim();
                }
                break;
            }
        }
        return value;
    }

    /**
     * Read from cpuinfo file
     *
     * @return file lines
     */
    @NonNull
    public static ArrayList<String> readFromCpuInfo() {
        ArrayList<String> cpuInfo = new ArrayList<>();
        try {
            FileReader fr = new FileReader(CPU_INFO_FILE_PATH);
            BufferedReader br = new BufferedReader(fr, 8192);
            String str;
            while ((str = br.readLine()) != null) {
                cpuInfo.add(str.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuInfo;
    }

    /**
     * Get brief device info
     *
     * @return brief device info, not null
     */
    @NonNull
    public static String getBriefDeviceInfo() {
        return DeviceUtil.getDeviceBrand()
                + " " + DeviceUtil.getDeviceModel()
                + ", Android " + DeviceUtil.getDeviceVersion()
                + ", " + DeviceUtil.getDeviceCPUArch();
    }

    /**
     * Get detailed device info
     *
     * @return detailed device info, not null
     */
    @SuppressLint("HardwareIds")
    @NonNull
    public static String getDeviceInfo() {
        String s = "主板: " + Build.BOARD +
                "\n系统启动程序版本号: " + Build.BOOTLOADER +
                "\n系统定制商: " + Build.BRAND +
                "\ncpu指令集: " + Build.CPU_ABI +
                "\ncpu指令集2: " + Build.CPU_ABI2 +
                "\n设置参数: " + Build.DEVICE +
                "\n显示屏参数: " + Build.DISPLAY +
                "\n无线电固件版本: " + Build.getRadioVersion() +
                "\n硬件识别码: " + Build.FINGERPRINT +
                "\n硬件名称: " + Build.HARDWARE +
                "\nHOST: " + Build.HOST +
                "\n修订版本列表: " + Build.ID +
                "\n硬件制造商: " + Build.MANUFACTURER +
                "\n版本: " + Build.MODEL +
                "\n硬件序列号: " + Build.SERIAL +
                "\n手机制造商: " + Build.PRODUCT +
                "\n描述Build的标签: " + Build.TAGS +
                "\nTIME: " + Build.TIME +
                "\nbuilder类型: " + Build.TYPE +
                "\nUSER: " + Build.USER;
        return s.trim();
    }

    /**
     * Get OpenGL ES info
     *
     * @param context android context, nullable
     * @return OpenGL ES info, not null
     */
    @NonNull
    public static String getOpenGLESInfo(@Nullable Context context) {
        if (context == null) {
            return "";
        }
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                return "";
            }
            ConfigurationInfo ci = am.getDeviceConfigurationInfo();
            if (ci == null) {
                return "";
            }
            return "OpenGL ES " + ci.getGlEsVersion() + " " + ci.reqGlEsVersion;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get android device network status
     *
     * @param context android context
     * @return network connected, true or false
     */
    public static boolean isNetworkAvailable(@Nullable Context context) {
        if (context == null) {
            return false;
        }
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return false;
            }
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                return false;
            }
            return ni.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
