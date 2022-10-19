package com.aliyun.svideo.track.util;

import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;

public class Util {
    public static int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }
    public static int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }


    public static int dp2px(float f) {
        Resources system = Resources.getSystem();
        return (int) ((f * system.getDisplayMetrics().density) + 0.5f);
    }

    public static int sp2px(float f) {
        Resources system = Resources.getSystem();
        return (int) ((f * system.getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static void setViewInVisible(View hide) {
        hide.setVisibility(View.INVISIBLE);
    }

    public static void setViewVisible(View show) {
        show.setVisibility(View.VISIBLE);
    }

    public static void setViewGone(View gone) {
        gone.setVisibility(View.GONE);
    }

    @Nullable
    public static Point measureTextBasePoint(String content, Paint paint, int parentHeight) {
        if (!TextUtils.isEmpty(content)) {
            int baseX = (int) (paint.measureText(content) / 2);
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int diff = (fontMetricsInt.descent - fontMetricsInt.ascent) / 2 - fontMetricsInt.descent;
            int baseY = parentHeight / 2 + diff;
            return new Point(baseX, baseY);
        }
        return null;
    }
}
