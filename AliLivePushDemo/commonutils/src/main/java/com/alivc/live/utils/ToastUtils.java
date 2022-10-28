package com.alivc.live.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

public class ToastUtils {

    public static void show(final String content) {
        if (!TextUtils.isEmpty(content)) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(ContextUtils.getSafeToastContext(), content, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
