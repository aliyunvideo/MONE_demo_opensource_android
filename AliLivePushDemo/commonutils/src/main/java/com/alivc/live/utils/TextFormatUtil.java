package com.alivc.live.utils;

import android.content.Context;

public class TextFormatUtil {

    public static final String REGULAR = "[0-9a-zA-Z]{1,20}";

    public static String getTextFormat(Context context, int id, Object... o){
        String s = context.getResources().getString(id);

        return String.format(s, o);
    }
    public static String getTextFormat(Context context, String paramString, Object... o){
        int id = context.getResources().getIdentifier(paramString, "string",  context.getPackageName());
        String s = context.getResources().getString(id);
        return String.format(s, o);
    }
    public static String getTextFormat(Context context, int id){
        return context.getResources().getString(id);
    }
    public static String[] getTextArray(Context context, int id){

        return context.getResources().getStringArray(id);
    }
    public static int[] getIntArray(Context context, int id){

        return context.getResources().getIntArray(id);
    }


}
