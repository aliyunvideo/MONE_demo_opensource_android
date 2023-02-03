package com.alivc.live.commonutils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.core.view.ViewCompat;

public class StatusBarUtil {
    /**
     * 沉浸式状态栏。
     * 支持 4.4 以上版本的 MIUI 和 Flyme，以及 5.0 以上版本的其他 Android。
     *
     * @param activity 需要被设置沉浸式状态栏的 Activity。
     */
    public static void translucent(Activity activity, @ColorInt int colorOn5x) {
        try {
            Window window = activity.getWindow();
            translucent(window, colorOn5x);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @TargetApi(19)
    private static void translucent(Window window, @ColorInt int colorOn5x) {
        if (isNotchOfficialSupport()) {
            handleDisplayCutoutMode(window);
        }
        int systemUiVisibility = window.getDecorView().getSystemUiVisibility();
        systemUiVisibility |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        window.getDecorView().setSystemUiVisibility(systemUiVisibility);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // android 6以后可以改状态栏字体颜色，因此可以自行设置为透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }



    @TargetApi(28)
    private static void handleDisplayCutoutMode(final Window window) {
        View decorView = window.getDecorView();
        if (decorView != null) {
            if (ViewCompat.isAttachedToWindow(decorView)) {
                realHandleDisplayCutoutMode(window, decorView);
            } else {
                decorView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                    @Override
                    public void onViewAttachedToWindow(View v) {
                        v.removeOnAttachStateChangeListener(this);
                        realHandleDisplayCutoutMode(window, v);
                    }

                    @Override
                    public void onViewDetachedFromWindow(View v) {

                    }
                });
            }
        }
    }

    @TargetApi(28)
    private static void realHandleDisplayCutoutMode(Window window, View decorView) {
        if (decorView.getRootWindowInsets() != null &&
                decorView.getRootWindowInsets().getDisplayCutout() != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams
                    .LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            window.setAttributes(params);
        }
    }

    public static boolean isNotchOfficialSupport(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P;
    }
}
