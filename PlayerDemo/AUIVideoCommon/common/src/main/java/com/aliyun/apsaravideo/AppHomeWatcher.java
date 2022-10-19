package com.aliyun.apsaravideo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

public class AppHomeWatcher {

    public Set<AppStatusWatchListener> listeners = new HashSet<>();

    private AppHomeWatcher() {

    }


    public void addWatchListener(AppStatusWatchListener listener) {
        listeners.add(listener);
    }

    public void removeListener(AppStatusWatchListener listener) {
        listeners.remove(listener);
    }

    public void startWatch(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_CREATE, activity);
                }
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_START, activity);
                }
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_RESUME, activity);
                }
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_PAUSE, activity);
                }
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_STOP, activity);
                }
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {


            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                for (AppStatusWatchListener listener : listeners) {
                    listener.onAppStateChange(AppStatusWatchListener.STATE_DESTROY, activity);
                }
            }
        });
    }

    public interface AppStatusWatchListener {
        int STATE_NONE = 0;
        int STATE_CREATE = 1;
        int STATE_START = 2;
        int STATE_RESUME = 3;
        int STATE_PAUSE = 4;
        int STATE_STOP = 5;
        int STATE_DESTROY = 6;

        void onAppStateChange(int state, Activity activity);
    }

    public static class InnerHolder {
        public final static AppHomeWatcher mInstance = new AppHomeWatcher();
    }
}
