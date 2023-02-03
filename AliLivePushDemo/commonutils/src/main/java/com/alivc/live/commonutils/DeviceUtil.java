package com.alivc.live.commonutils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author keria
 * @date 2022/4/19
 */
public class DeviceUtil {

    private DeviceUtil() {
    }

    /**
     * Get device brand
     *
     * @return brand name
     */
    @NonNull
    public static String getDeviceBrand() {
        return Build.BRAND;
    }

    /**
     * Get device os version
     *
     * @return device os version
     */
    @NonNull
    public static String getDeviceVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * Get device os version code
     *
     * @return device os version code
     */
    public static int getDeviceVersionCode() {
        return Build.VERSION.SDK_INT;
    }

    /**
     * Get device model
     *
     * @return device model name
     */
    @NonNull
    public static String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * Get device cpu arch
     *
     * @return device cpu arch name
     */
    @SuppressLint("ObsoleteSdkInt")
    @NonNull
    public static String getDeviceCPUArch() {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) ? Build.CPU_ABI : Build.SUPPORTED_ABIS[0];
    }

    /**
     * Get device orientation
     *
     * @param context android context, nullable
     * @return device orientation
     */
    public static int getDisplayOrientation(@Nullable Context context) {
        int displayOrientation = 0;

        if (context == null) {
            return displayOrientation;
        }

        int angle = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (angle) {
            case Surface.ROTATION_0:
                displayOrientation = 0;
                break;
            case Surface.ROTATION_90:
                displayOrientation = 90;
                break;
            case Surface.ROTATION_180:
                displayOrientation = 180;
                break;
            case Surface.ROTATION_270:
                displayOrientation = 270;
                break;
            default:
                break;
        }

        return displayOrientation;
    }
}
