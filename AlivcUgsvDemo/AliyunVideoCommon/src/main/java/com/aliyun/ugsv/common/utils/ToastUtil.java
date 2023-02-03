/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.ugsv.common.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    public static void showToast(Context context, String text) {
        showToast(context, text, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int rsid) {
        showToast(context, rsid, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, int rsid, int gravity, int duration) {
        showToast(context, context.getString(rsid), gravity, 0, 0, duration);
    }

    public static void showToast(Context context, String text, int gravity, int duration) {
        showToast(context, text, gravity, 0, 0, duration);
    }

    public static void showToast(Context context, String text, int gravity, int xOffset, int yOffset, int duration) {
        Toast toast = Toast.makeText(context, text, duration);
//        toast.setGravity(gravity, xOffset,
//                (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, 118,
//                        context.getResources().getDisplayMetrics()));
        toast.show();
    }

}
