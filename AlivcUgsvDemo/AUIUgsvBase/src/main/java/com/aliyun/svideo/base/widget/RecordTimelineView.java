/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.ugsv.common.utils.DensityUtils;

import java.util.concurrent.CopyOnWriteArrayList;

public class RecordTimelineView extends View {

    private final static String TAG = RecordTimelineView.class.getSimpleName();
    private int maxDuration;
    private int minDuration;
    private CopyOnWriteArrayList<DrawInfo> clipDurationList = new CopyOnWriteArrayList<>();
    private DrawInfo currentClipDuration = new DrawInfo();
    private Paint paint = new Paint();
    private RectF rectF = new RectF();
    private int durationColor;
    private int selectColor;
    private int offsetColor;
    private int backgroundColor;
    private boolean isSelected = false;

    public RecordTimelineView(Context context) {
        super(context);
        init();
    }

    public RecordTimelineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordTimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (backgroundColor != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                paint.setColor(getResources().getColor(backgroundColor));
                canvas.drawRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight(), paint);
            } else {
                canvas.drawColor(getResources().getColor(backgroundColor));
            }
        }
        int lastTotalDuration = 0;
        for (int i = 0; i < clipDurationList.size(); i++) {
            DrawInfo info = clipDurationList.get(i);
            switch (info.drawType) {
            case OFFSET:
                paint.setColor(getResources().getColor(offsetColor));
                break;
            case DURATION:
                paint.setColor(getResources().getColor(durationColor));
                break;
            case SELECT:
                paint.setColor(getResources().getColor(selectColor));
                break;
            default:
                paint.setColor(getResources().getColor(offsetColor));
            }

            if (info.drawType == DrawType.OFFSET) {
                canvas.drawRect((lastTotalDuration - info.length) / (float) maxDuration * getWidth(), 0f,
                                lastTotalDuration / (float) maxDuration * getWidth(), getHeight(), paint);
            } else {
                //第一个片段，在最左侧添加半圆
                if (i == 0) {
                    rectF.set(0, 0, getHeight(), getHeight());
                    canvas.drawArc(rectF, 90, 180, true, paint);
                    float right = (lastTotalDuration + info.length) / (float) maxDuration * getWidth();
                    if (right > getHeight() / 2) {
                        canvas.drawRect(getHeight() / 2, 0f,
                                        right, getHeight(), paint);
                    }

                } else {
                    canvas.drawRect(lastTotalDuration / (float) maxDuration * getWidth(), 0f,
                                    (lastTotalDuration + info.length) / (float) maxDuration * getWidth(), getHeight(), paint);
                }
                lastTotalDuration += info.length;
            }
        }
        if (currentClipDuration != null && currentClipDuration.length != 0) {
            paint.setColor(getResources().getColor(durationColor));
            float left = lastTotalDuration / (float) maxDuration * getWidth();
            float right = (lastTotalDuration + currentClipDuration.length) / (float) maxDuration * getWidth();
            //第一个片段，在最左侧添加半圆
            if (clipDurationList.size() == 0) {
                rectF.set(0, 0, getHeight(), getHeight());
                canvas.drawArc(rectF, 90, 180, true, paint);
                if (right > getHeight() / 2) {
                    canvas.drawRect(left + getHeight() / 2, 0f, right, getHeight(), paint);
                }
            } else {
                canvas.drawRect(left, 0f, right, getHeight(), paint);
            }

        }
        if (lastTotalDuration + currentClipDuration.length < minDuration) {
            paint.setColor(getResources().getColor(offsetColor));
            canvas.drawRect(minDuration / (float) maxDuration * getWidth(), 0f,
                            (minDuration + maxDuration / 200) / (float) maxDuration * getWidth(), getHeight(), paint);
        }
        //Log.d("onDraw", "lastTotalDuration" + lastTotalDuration + "\n" + "maxDuration" + maxDuration);
    }

    public void clipComplete() {
        clipDurationList.add(currentClipDuration);
        DrawInfo info = new DrawInfo();
        info.length = DensityUtils.px2dip(getContext(), maxDuration / 100);
        info.drawType = DrawType.OFFSET;
        clipDurationList.add(info);
        currentClipDuration = new DrawInfo();
        //Log.i(TAG, "TotalDuration :" + getTimelineDuration() + " ,currentDuration : " + currentClipDuration.length
        //      + " ,count : " + clipDurationList.size());
        invalidate();
    }

    public void deleteLast() {
        if (clipDurationList.size() >= 2) {
            clipDurationList.remove(clipDurationList.size() - 1);
            clipDurationList.remove(clipDurationList.size() - 1);
        }
        invalidate();
    }

    public void clear() {
        if (clipDurationList.size() >= 2) {
            clipDurationList.clear();
        }
        invalidate();
    }

    public void selectLast() {
        if (clipDurationList.size() >= 2) {
            DrawInfo info = clipDurationList.get(clipDurationList.size() - 2);
            info.drawType = DrawType.SELECT;
            invalidate();
            isSelected = true;
        }
    }

    public void setMaxDuration(int maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setMinDuration(int minDuration) {
        this.minDuration = minDuration;
    }

    public void setDuration(int duration) {
        if (isSelected) {
            for (DrawInfo info : clipDurationList) {
                if (info.drawType == DrawType.SELECT) {
                    info.drawType = DrawType.DURATION;
                    isSelected = false;
                    break;
                }
            }
        }
        this.currentClipDuration.drawType = DrawType.DURATION;
        this.currentClipDuration.length = duration;
        //Log.i(TAG, "currentDuration :" + duration + " ,cache TotalDuration :" + (getTimelineDuration() + duration));
        invalidate();
    }

    public void setColor(int duration, int select, int offset, int backgroundColor) {
        this.durationColor = duration;
        this.selectColor = select;
        this.offsetColor = offset;
        this.backgroundColor = backgroundColor;

    }

    /**
     * 获取时长控件显示的时间
     *
     * @return duration ms
     */
    public int getTimelineDuration() {
        int duration = 0;
        for (DrawInfo drawInfo : clipDurationList) {
            if (drawInfo.drawType == DrawType.DURATION) {
                duration += drawInfo.length;
            }
        }
        return duration;
    }

    class DrawInfo {
        int length;
        DrawType drawType = DrawType.DURATION;
    }

    enum DrawType {
        /**
         * 边界
         */
        OFFSET,
        /**
         * 时长
         */
        DURATION,
        /**
         * 当前
         */
        SELECT
    }
}
