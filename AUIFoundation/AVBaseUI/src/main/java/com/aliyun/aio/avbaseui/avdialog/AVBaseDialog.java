package com.aliyun.aio.avbaseui.avdialog;

import android.app.Activity;
import android.content.Context;
import android.view.Window;

import androidx.appcompat.app.AppCompatDialog;

public class AVBaseDialog extends AppCompatDialog {
    public AVBaseDialog(Context context) {
        super(context);
        initView(context);
    }

    public AVBaseDialog(Context context, int theme) {
        super(context, theme);
        initView(context);
    }

    protected AVBaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView(context);
    }

    public void initView(Context context) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void dismiss() {
        Context context = getContext();
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return;
            }
            super.dismiss();
        } else {
            try {
                super.dismiss();
            } catch (Throwable ignore) {
                super.dismiss();

            }
        }

    }
}
