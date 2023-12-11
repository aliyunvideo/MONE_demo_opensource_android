package com.aliyun.svideo.mixrecorder.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.WindowManager;

/**
 * 辅助计算工具类
 *  辅助计算复杂数据
 * @author xlx
 */
public class CaculatorHelper {

    /**
     * 辅助计算水印图片和屏幕宽高比例
     * @param context Context
     * @param bitmap Bitmap
     * @return float[0]: widthRatio, float[1]:heightRatio
     */
    public static float[] addWaterImageHelper (Context context, Bitmap bitmap) {
        float[] ratio = new float[2];
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        float windowWidth = windowManager.getDefaultDisplay().getWidth();
        float windowHeight = windowManager.getDefaultDisplay().getHeight();

        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        ratio[0] =  width / windowWidth;
        ratio[1] =  height / windowHeight;
        return ratio;
    }
}
