package com.alivc.player.videolist.auivideofunctionlist.domain;

import android.content.Context;
import android.content.SharedPreferences;

public class GestureGuidanceUseCase {

    private final String AUI_VIDEO_LIST_GESTURE = "aui_video_list_gesture_show";

    private final SharedPreferences mSharedPreferences;

    public GestureGuidanceUseCase(Context context) {
        mSharedPreferences = context.getApplicationContext().getSharedPreferences("aui_video_list", Context.MODE_PRIVATE);
    }

    public boolean isShowGestureGuidance() {
        return mSharedPreferences.getBoolean(AUI_VIDEO_LIST_GESTURE, false);
    }

    public void setGestureGuidance(boolean shown) {
        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putBoolean(AUI_VIDEO_LIST_GESTURE, shown);
        edit.apply();
    }
}
