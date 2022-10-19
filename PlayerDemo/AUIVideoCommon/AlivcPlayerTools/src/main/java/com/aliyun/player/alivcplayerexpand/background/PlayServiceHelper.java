package com.aliyun.player.alivcplayerexpand.background;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class PlayServiceHelper {
    public static boolean mServiceStart = false;
    public static final String KEY_USER_NAME = "key_user_name";
    public static boolean mServiceHasForeground = false;
    public static boolean mPendingStopService = false;

    public static void startPlayService(Context context, String authorName) {
        try {
            Log.i("PlayServiceHelper", "startPlayService mServiceStart:" + mServiceStart);
            if (mServiceStart)
                return;
            mServiceStart = true;
            Intent intent = new Intent(context, BackgroundPlayService.class);
            intent.putExtra(KEY_USER_NAME, authorName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        } catch (Exception e) {

        }
    }

    public static void stopService(Context context) {
        Log.i("PlayServiceHelper", "stopService mServiceStart:" + mServiceStart + " mServiceHasForeground:" + mServiceHasForeground);
        if (!mServiceHasForeground && mServiceStart) {
            mPendingStopService = true;
            return;
        }
        if (mServiceStart) {
            mServiceStart = false;
            context.stopService(new Intent(context, BackgroundPlayService.class));
        }
    }
}
