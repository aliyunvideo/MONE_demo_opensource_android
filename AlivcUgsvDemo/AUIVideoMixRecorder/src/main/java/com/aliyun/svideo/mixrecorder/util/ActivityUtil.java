package com.aliyun.svideo.mixrecorder.util;

import android.app.Activity;
import android.view.Surface;

public class ActivityUtil {
    public static int getDegrees(final Activity activity) {
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
            default: break;
        }
        return degrees;
    }
}
