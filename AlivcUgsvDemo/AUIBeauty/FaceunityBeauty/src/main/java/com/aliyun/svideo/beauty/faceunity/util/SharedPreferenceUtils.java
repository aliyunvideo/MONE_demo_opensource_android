package com.aliyun.svideo.beauty.faceunity.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceUtils {

    private static final String SHAREDPRE_FILE = "svideo_face_unity";
    private static final String BEAUTY_FACE_LEVEL = "beauty_face_level";
    private static final String BEAUTY_SKIN_LEVEL = "beauty_skin_level";
    private static final String BEAUTY_PARAMS = "face_unity_params";



    public static void setFaceUnityBeautyCustomParams(Context context, String data) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString(BEAUTY_PARAMS, data);
        editor.apply();
    }

    public static String getFaceUnityBeautyCustomParams(Context context) {
        if (context == null){
            return "";
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        String data = sharedPreferences.getString(BEAUTY_PARAMS, "");
        return data;
    }


    public static int getFaceUnityBeautyLevel(Context context) {
        if (context == null){
            return 3;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(BEAUTY_FACE_LEVEL, 3);
    }

    public static int getFaceUnityBeautySkinLevel(Context context) {
        if (context == null){
            return 3;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE,
                                              Activity.MODE_PRIVATE);
        return sharedPreferences.getInt(BEAUTY_SKIN_LEVEL, 3);
    }


    public static void setBeautyFaceLevel(Context context, int level) {
        if (context != null){
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.remove(BEAUTY_FACE_LEVEL);
        editor.putInt(BEAUTY_FACE_LEVEL, level);
        editor.apply();
        }
    }


    public static void setBeautySkinLevel(Context context, int level) {
        if (context != null) {
            SharedPreferences mySharedPreferences = context.getSharedPreferences(SHAREDPRE_FILE, Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPreferences.edit();
            editor.remove(BEAUTY_SKIN_LEVEL);
            editor.putInt(BEAUTY_SKIN_LEVEL, level);
            editor.apply();
        }
    }


}
