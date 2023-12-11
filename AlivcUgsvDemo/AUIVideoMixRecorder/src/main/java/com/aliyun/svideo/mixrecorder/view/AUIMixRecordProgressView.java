package com.aliyun.svideo.mixrecorder.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.aio.avbaseui.widget.AVCircleProgressView;

import java.util.ArrayList;
import java.util.List;

public class AUIMixRecordProgressView extends AVCircleProgressView {
    private static final float CLIP_END_POINT_SWEEP_ANGLE = 2;
    ArrayList<Float> mClipTimeRatioList = new ArrayList<>();

    public AUIMixRecordProgressView(Context context) {
        super(context);
    }

    public AUIMixRecordProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AUIMixRecordProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int center = getWidth() / 2;
        int radius = center - mAnnulusWidth / 2;
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(mAnnulusWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mAnnulusColor);
        float timeRatio = 0.0f;
        float sweepAngle = CLIP_END_POINT_SWEEP_ANGLE;
        //最后一段不需绘制分段标识
        for (int i = 0; i < mClipTimeRatioList.size(); i++) {
            timeRatio += mClipTimeRatioList.get(i);
            if (timeRatio >= 1.0f) {
                break;
            }
            float endAngle = timeRatio * 360 - 90;
            RectF ovalStroke = new RectF(center - radius, center - radius, center + radius, center + radius);
            canvas.drawArc(ovalStroke, endAngle, sweepAngle, false, mPaint);
        }
    }

    public void updateClipTimeRatioList(@NonNull List<Float> timeRatioList) {
        mClipTimeRatioList.clear();
        mClipTimeRatioList.addAll(timeRatioList);
    }
}
