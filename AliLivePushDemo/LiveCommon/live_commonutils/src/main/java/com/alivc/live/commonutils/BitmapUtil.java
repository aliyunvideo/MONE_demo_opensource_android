package com.alivc.live.commonutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author keria
 * @date 2023/9/15
 * @brief
 */
public class BitmapUtil {
    private BitmapUtil() {
    }

    /**
     * 文字生成bitmap
     *
     * @param context  android context
     * @param contents 文字内容
     * @return bitmap图像
     */
    public static Bitmap createTextBitmap(Context context, String contents) {
        if (context == null || TextUtils.isEmpty(contents)) {
            return null;
        }

        float scale = context.getResources().getDisplayMetrics().scaledDensity;

        TextView tv = new TextView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(layoutParams);
        tv.setText(contents);
        tv.setTextSize(scale * 12);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setDrawingCacheEnabled(true);
        tv.setTextColor(Color.WHITE);
        tv.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tv.layout(0, 0, tv.getMeasuredWidth(), tv.getMeasuredHeight());

        tv.setBackgroundColor(Color.TRANSPARENT);

        tv.buildDrawingCache();
        return tv.getDrawingCache();
    }

    /**
     * 翻转bitmap
     *
     * @param bitmap  bitmap图像
     * @param isFlipX X轴flip
     * @param isFlipY Y轴flip
     * @return 翻转后的bitmap
     */
    public static Bitmap flipBitmap(Bitmap bitmap, boolean isFlipX, boolean isFlipY) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Matrix matrix = new Matrix();
        matrix.setScale(isFlipX ? -1 : 1, isFlipY ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

}
