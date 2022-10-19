package com.aliyun.svideo.recorder.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * notch screen (刘海屏) 检测utils
 *
 * @author xlx
 */
public class NotchScreenUtil {
    private static final String TAG = "NotchScreenUtil...";

    /**
     * 设置应用窗口在华为notch手机使用刘海区的flag值, 该值为华为官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_HW = 0x00010000;

    /**
     * 华为手机判断用户是否开启 "隐藏刘海区域" 的参数值
     */
    private static final String DISPLAY_NOTCH_STATUS_HW = "display_notch_status";

    /**
     * 小米手机判断用户是否开启 "隐藏刘海区域" 的参数值
     */
    private static final String DISPLAY_NOTCH_STATUS_MIUI = "force_black";

    /**
     * vivo手机判断是否是notch, vivo官方提供, 不要修改
     */
    private static final int FLAG_NOTCH_SUPPORT_VIVO = 0x00000020;

    public static boolean checkNotchScreen(Context context) {
        if (checkHuaWei(context)) {
            return true;
        } else if (checkVivo(context)) {
            return true;
        } else if (checkMiUI(context)) {
            return true;
        } else if (checkOppo(context)) {
            return true;
        }

        return false;
    }

    /**
     * oppo提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkOppo(Context context) {
        try {
            return context.getPackageManager().hasSystemFeature("com.oppo.feature.screen.heteromorphism");
        } catch (Exception e) {
            Log.e(TAG, "checkOppo notchScreen exception");
        }
        return false;
    }

    /**
     * 小米提供: 刘海屏判断.
     *
     * @return true, 刘海屏; false: 非刘海屏
     */
    private static boolean checkMiUI(Context context) {

        int result = 0;
        try {
            ClassLoader classLoader = context.getClassLoader();
            @SuppressLint("PrivateApi")
            @SuppressWarnings("rawtypes")
            Class systemProperties = classLoader.loadClass("android.os.SystemProperties");
            //参数类型
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[2];
            paramTypes[0] = String.class;
            paramTypes[1] = int.class;
            Method getInt = systemProperties.getMethod("getInt", paramTypes);
            //参数
            Object[] params = new Object[2];
            params[0] = "ro.miui.notch";
            params[1] = 0;
            result = (Integer)getInt.invoke(systemProperties, params);
            return result == 1;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 华为提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：刘海屏；false：非刘海屏
     */
    private static boolean checkHuaWei(Context context) {

        boolean ret = false;

        try {

            ClassLoader cl = context.getClassLoader();

            Class hwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");

            Method get = hwNotchSizeUtil.getMethod("hasNotchInScreen");

            ret = (boolean)get.invoke(hwNotchSizeUtil);

        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hasNotchInScreen ClassNotFoundException");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hasNotchInScreen NoSuchMethodException");
        } catch (Exception e) {
            Log.e(TAG, "hasNotchInScreen Exception");

        }
        return ret;
    }

    /**
     * vivo提供: 判断是否是刘海屏
     *
     * @param context Context
     * @return true：是刘海屏；false：非刘海屏
     */
    private static boolean checkVivo(Context context) {

        boolean ret;
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressLint("PrivateApi")
            Class ftFeature = cl.loadClass("android.util.FtFeature");
            Method isFeatureSupport = ftFeature.getMethod("isFeatureSupport");
            ret = (boolean)isFeatureSupport.invoke(ftFeature, FLAG_NOTCH_SUPPORT_VIVO);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }
        return ret;
    }

    /**
     * 华为提供: 获取刘海尺寸
     *
     * @param context Context
     * @return int[0]值为刘海宽度 int[1]值为刘海高度。
     */
    public static int[] getHwNotchSize(Context context) {

        int[] ret = new int[] {0, 0};

        try {

            ClassLoader cl = context.getClassLoader();

            Class hwnotchsizeutil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");

            Method get = hwnotchsizeutil.getMethod("getNotchSize");

            ret = (int[])get.invoke(hwnotchsizeutil);

        } catch (ClassNotFoundException e) {

            Log.e(TAG, "getNotchSize ClassNotFoundException");

        } catch (NoSuchMethodException e) {

            Log.e(TAG, "getNotchSize NoSuchMethodException");

        } catch (Exception e) {

            Log.e(TAG, "getNotchSize Exception");

        }
        return ret;
    }

    /**
     * Vivo提供: 获取刘海区域尺寸 vivo官方文档上没有提供动态获取宽高的方法, 只提供了固定值,
     *          宽:        100dp
     *          高:        27dp
     *          状态栏高度: 32dp
     *          屏幕宽高比: 19:9
     *          屏幕圆角:   32dp
     *
     * @param context Context
     * @return int[0]:刘海宽度, int[1]:刘海高度, 单位px
     */
    public static int[] getVivoNotchSize(Context context) {
        int notchWidth = dip2px(context, 100);
        int notchHeight = dip2px(context, 27);
        return new int[] {notchWidth, notchHeight};
    }

    /**
     * Vivo提供: 获取刘海区域尺寸 Oppo官方文档上没有提供动态获取宽高的方法, 只提供了固定值, 并且官方文档标明, 后续可能会有变化, 会适配Android P,并兼容老的方案
     *          宽:  324px
     *          高:  80px
     *
     * @param context Context
     * @return int[0]:刘海宽度, int[1]:刘海高度, 单位px
     */
    public static int[] getOppoNotchSize(Context context) {
        return new int[] {324, 80};
    }

    /**
     * 小米提供:
     *      获取刘海尺寸的方法
     *
     * @param context Context
     * @return int[0]:刘海宽度, int[1]:刘海高度, 单位px
     */
    public static int[] getMiUINotchHeight(Context context) {

        int[] notchSize = new int[2];

        int widthResourceId = context.getResources().getIdentifier("notch_width", "dimen", "android");
        if (widthResourceId > 0) {
            notchSize[0] = context.getResources().getDimensionPixelSize(widthResourceId);
        }

        int heightResourceId = context.getResources().getIdentifier("notch_height", "dimen", "android");
        if (heightResourceId > 0) {
            notchSize[1] = context.getResources().getDimensionPixelSize(heightResourceId);
        }
        return notchSize;
    }

    /**
     * 小米提供:
     *      获取刘海屏类型手机, 状态栏高度的方法
     *      当用户在 设置里面调整刘海屏为 "隐藏屏幕刘海" 时, 系统会强制   盖黑状态栏, 无视应用的Notch设置和声明,
     *      由于 Notch 设备的状态栏高度与正常机器不一样，因此在需要使用状态栏高度时，不建议写死一个值，而应该改为读取系统的值
     *
     *      开关状态可以通过本工具类的 checkMiUINotchSwitchState()方法检查
     * @param context Context
     * @return 状态栏高度
     */
    public static int getMiUIStatuBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statuBarHeight = 0;
        if (resourceId > 0) {
            statuBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statuBarHeight;
    }

    /**
     * 小米提供:
     *      用于检测用户是否开启了 "隐藏屏幕刘海" 的设置
     * @param context Context
     * @return  true: 开启, false: 未开启
     */
    public static boolean checkMiUINotchSwitchState (Context context) {
        boolean forceBlack = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            forceBlack = Settings.Global.getInt(context.getContentResolver(), DISPLAY_NOTCH_STATUS_MIUI, 0) == 1;
        }
        return forceBlack;
    }

    /**
     * 华为提供:
     *      用于检测用户是否开启了 "隐藏屏幕刘海" 的设置
     * @param context Context
     * @return  true: 开启, false: 未开启
     */
    public static boolean checkHWNotchSwitchState (Context context) {
        // 0表示“默认”，1表示“隐藏显示区域”
        int mIsNotchSwitchOpen = Settings.Secure.getInt(context.getContentResolver(), DISPLAY_NOTCH_STATUS_HW, 0);
        return mIsNotchSwitchOpen == 1;
    }



    /**
     * 华为提供: 设置应用窗口在华为刘海屏手机使用刘海区
     *
     * @param window 应用页面window对象
     */
    public static void setNotFullScreenWindowLayoutInDisplayCutout(Window window) {
        if (window == null) {
            return;
        }
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        try {
            Class layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx");
            Constructor con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams.class);
            Object layoutParamsExObj = con.newInstance(layoutParams);
            Method method = layoutParamsExCls.getMethod("clearHwFlags", int.class);
            method.invoke(layoutParamsExObj, FLAG_NOTCH_SUPPORT_HW);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (IllegalAccessException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (InstantiationException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (InvocationTargetException e) {
            Log.e(TAG, "hw clear notch screen flag api error");
        } catch (Exception e) {
            Log.e(TAG, "other Exception");
        }
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(pxValue / scale + 0.5f);
    }
}
