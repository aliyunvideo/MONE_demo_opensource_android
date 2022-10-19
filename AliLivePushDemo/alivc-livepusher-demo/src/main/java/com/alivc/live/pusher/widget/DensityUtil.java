package com.alivc.live.pusher.widget;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class DensityUtil {
    private static final String TAG = DensityUtil.class.getSimpleName();
    /**
     * dp、sp 转换为 px 的工具类
     *
     * @author fxsky 2012.11.12
     *
     */
    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @param scale   （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @param scale    （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        if (context == null || context.getResources() == null)
            return 1;
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @param fontScale （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    private static DisplayMetrics sDisplayMetrics;
    private static int sStatusBarHeight = -1;

    public static DisplayMetrics getDisplayMetrics(Context context) {
        if (sDisplayMetrics == null) {
            sDisplayMetrics = context.getResources().getDisplayMetrics();
        }
        return sDisplayMetrics;
    }

    public static int getStatusBarHeight(Context context) {
        if (sStatusBarHeight == -1) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                sStatusBarHeight = context.getResources().getDimensionPixelSize(x);
            } catch (ClassNotFoundException e) {
                Log.e(TAG,  e.getMessage());
            } catch (InstantiationException e) {
                Log.e(TAG,  e.getMessage());
            } catch (IllegalAccessException e) {
                Log.e(TAG,  e.getMessage());
            } catch (NoSuchFieldException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return sStatusBarHeight;
    }

    /**
     * 获取view的宽度
     */
    public static int getViewWidth(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredWidth();
    }

    /**
     * 获取view的高度
     */
    public static int getViewHeight(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return view.getMeasuredHeight();
    }


    /**
     * 获取适配后的高度
     *
     * @return
     */
    public static int getScaleHeight(Context context, int srcHeight) {
        double realHeight = getScreenRealHeight(context);
        if (realHeight <= 0) {
            return srcHeight;
        }

        if (realHeight >= 37) {// [37, +∞)
            srcHeight = (int) (srcHeight * 0.9 * 0.9 * 0.9);
        } else if (realHeight >= 33) {// [33, 37)
            srcHeight = (int) (srcHeight * 0.9 * 0.9);
        } else if (realHeight >= 27) {// [27, 33)
            srcHeight = (int) (srcHeight * 0.9);
        }
        // [0, 27]
        return srcHeight;
    }


    /**
     * 获取当前手机屏幕的尺寸(单位:像素)
     */
    public static double getScreenRealHeight(Context context) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Point point = new Point();
                manager.getDefaultDisplay().getRealSize(point);
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                // dm.ydpi是屏幕y方向的真实密度值
                return new BigDecimal(Math.pow(point.y / dm.ydpi, 2)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
        } catch (Exception e) {
            Log.d("DensityUtil", e.getMessage());
        }
        return 0;
    }

    /**
     * 获取当前手机屏幕的尺寸(单位:像素)
     */
    public static double getScreenSize(Context context) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                Point point = new Point();
                manager.getDefaultDisplay().getRealSize(point);
                DisplayMetrics dm = context.getResources().getDisplayMetrics();
                // dm.xdpi是屏幕x方向的真实密度值
                double x = Math.pow(point.x / dm.xdpi, 2);
                // dm.ydpi是屏幕y方向的真实密度值
                double y = Math.pow(point.y / dm.ydpi, 2);
                return new BigDecimal(Math.sqrt(x + y)).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            }
        } catch (Exception e) {
            Log.d("DensityUtil", e.getMessage());
        }
        return 0;
    }
}

