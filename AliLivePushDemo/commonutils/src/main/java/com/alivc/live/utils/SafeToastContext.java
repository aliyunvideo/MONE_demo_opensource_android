package com.alivc.live.utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;

/**
 * Created by keria on 2022/4/6.
 */
final class SafeToastContext extends ContextWrapper {
    SafeToastContext(@NonNull Context base) {
        super(base);
    }

    @Override
    public Context getApplicationContext() {
        return new ApplicationContextWrapper(getBaseContext().getApplicationContext());
    }

    private static final class ApplicationContextWrapper extends ContextWrapper {

        private ApplicationContextWrapper(@NonNull Context base) {
            super(base);
        }

        @Override
        public Object getSystemService(@NonNull String name) {
            if (Context.WINDOW_SERVICE.equals(name)) {
                return new WindowManagerWrapper((WindowManager) getBaseContext().getSystemService(name));
            }
            return super.getSystemService(name);
        }
    }

    private static final class WindowManagerWrapper implements WindowManager {

        private static final String TAG = "WindowManagerWrapper";
        @NonNull
        private final WindowManager base;

        private WindowManagerWrapper(@NonNull WindowManager base) {
            this.base = base;
        }

        @Override
        public Display getDefaultDisplay() {
            return base.getDefaultDisplay();
        }

        @Override
        public void removeViewImmediate(View view) {
            base.removeViewImmediate(view);
        }

        @Override
        public void addView(View view, ViewGroup.LayoutParams params) {
            try {
                Log.d(TAG, "WindowManager's addView(view, params) has been hooked.");
                base.addView(view, params);
            } catch (BadTokenException e) {
                Log.i(TAG, e.getMessage());
            } catch (Throwable throwable) {
                Log.e(TAG, throwable.toString());
            }
        }

        @Override
        public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
            base.updateViewLayout(view, params);
        }

        @Override
        public void removeView(View view) {
            base.removeView(view);
        }
    }
}