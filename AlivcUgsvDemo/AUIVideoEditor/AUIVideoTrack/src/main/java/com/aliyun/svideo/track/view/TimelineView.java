package com.aliyun.svideo.track.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.svideo.track.api.TrackConfig;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 时间刻度
 */
public class TimelineView extends View {
    private Paint mPaint;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("mm:ss");
    private final Point mTextSize = new Point();
    /**
     * 圆点大小
     */
    private float mCursorSize;
    /**
     * 当前时间单位
     */
    private float mTimeUnit;
    /**
     * 当前时间单位对应宽度
     */
    private float mTimeUnitWidth;
    /**
     * 当前时长
     */
    private long mDuration;
    /**
     * 当前时间刻度缩放值
     */
    private float mTimelineScale = 1.0f;
    private final List<String> mTextVec = new ArrayList<String>();

    public TimelineView(Context context) {
        this(context, null);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimelineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInit();
    }

    private void onInit() {
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setStrokeWidth(3.0F);
        this.mPaint.setColor(-7829368);
        this.mPaint.setTextSize(this.applyDimension(2, 10.0F));
        Rect bounds = new Rect();
        this.mPaint.getTextBounds("00:00", 0, 5, bounds);
        this.mTextSize.x = bounds.width();
        this.mTextSize.y = bounds.height();
        this.mCursorSize = this.applyDimension(1, 2.0F);
        this.setPadding(TrackConfig.TIMELINE_PADDING_LEFT, 0, 0, 0);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = View.getDefaultSize(heightSize, heightMeasureSpec);
        int width = Math.round((float) this.mDuration * TrackConfig.getPxUnit(mTimelineScale) + TrackConfig.TIMELINE_PADDING_LEFT);
        this.measureText();
        this.setMeasuredDimension(width, height);
    }

    private float applyDimension(int unit, float value) {
        Resources resources;
        if (getContext() == null) {
            resources = Resources.getSystem();
        } else {
            resources = getContext().getResources();
        }
        return TypedValue.applyDimension(unit, value, resources.getDisplayMetrics());
    }

    public void setDuration(long us) {
        if (us >= 0L && this.mDuration != us) {
            this.mDuration = us;
            post(new Runnable() {
                public final void run() {
                    TimelineView.this.mTextVec.clear();
                    TimelineView.this.measureText();
                    TimelineView.this.invalidate();
                }
            });
        }
    }

    public void setTimelineScale(float timelineScale) {
        this.mTimelineScale = timelineScale;
    }

    private int measureText() {
        if (this.mDuration <= 0L) {
            this.mTextVec.clear();
            this.mTextVec.add("00:00");
            return 1;
        } else {
            this.initTimeUnitWidth();
            int countInt = (int) Math.ceil(this.mDuration * 1.0F / this.mTimeUnit);
            this.mTextVec.clear();
            if (countInt > 1) {
                for (int i = 0; i < countInt; ++i) {
                    if (this.mTimeUnit > TrackConfig.sTimeUnit[5]) {
                        this.mTextVec.add(this.mDateFormat.format(new Date((long) ((float) i * this.mTimeUnit / (float) 1000))));
                    } else if (this.mTimeUnit == TrackConfig.sTimeUnit[5]) {
                        if (i % 2 == 0) {
                            this.mTextVec.add(this.mDateFormat.format(new Date((long) ((float) i * this.mTimeUnit / (float) 1000))));
                        } else {
                            this.mTextVec.add("15f");
                        }
                    } else if (this.mTimeUnit == TrackConfig.sTimeUnit[6]) {
                        switch (i % 3) {
                            case 0:
                                this.mTextVec.add(this.mDateFormat.format(new Date((long) ((float) i * this.mTimeUnit / (float) 1000))));
                                break;
                            case 1:
                                this.mTextVec.add("10f");
                                break;
                            case 2:
                                this.mTextVec.add("20f");
                        }
                    } else if (this.mTimeUnit == TrackConfig.sTimeUnit[7]) {
                        switch (i % 6) {
                            case 0:
                                this.mTextVec.add(this.mDateFormat.format(new Date((long) ((float) i * this.mTimeUnit / (float) 1000))));
                                break;
                            case 1:
                                this.mTextVec.add("5f");
                                break;
                            case 2:
                                this.mTextVec.add("10f");
                                break;
                            case 3:
                                this.mTextVec.add("15f");
                                break;
                            case 4:
                                this.mTextVec.add("20f");
                                break;
                            case 5:
                                this.mTextVec.add("25f");
                        }
                    } else if (this.mTimeUnit == TrackConfig.sTimeUnit[8]) {
                        switch (i % 10) {
                            case 0:
                                this.mTextVec.add(this.mDateFormat.format(new Date((long) ((float) i * this.mTimeUnit / (float) 1000))));
                                break;
                            case 1:
                                this.mTextVec.add("3f");
                                break;
                            case 2:
                                this.mTextVec.add("6f");
                                break;
                            case 3:
                                this.mTextVec.add("9f");
                                break;
                            case 4:
                                this.mTextVec.add("12f");
                                break;
                            case 5:
                                this.mTextVec.add("15f");
                                break;
                            case 6:
                                this.mTextVec.add("18f");
                                break;
                            case 7:
                                this.mTextVec.add("21f");
                                break;
                            case 8:
                                this.mTextVec.add("24f");
                                break;
                            case 9:
                                this.mTextVec.add("27f");
                        }
                    }
                }
            } else {
                this.mTextVec.add(this.mDateFormat.format(new Date(0L)));
            }
            return countInt;
        }
    }

    /**
     * 计算当前单位
     */
    private void initTimeUnitWidth() {
        float minTimeUnitWidth = (float) this.mTextSize.x * 1.5F;
        float maxTimeUnitWidth = (float) this.mTextSize.x * 3.0F;
        this.mTimeUnit = TrackConfig.sTimeUnit[0];
        for (float item : TrackConfig.sTimeUnit) {
            float width = TrackConfig.getPxUnit(mTimelineScale) * item;
            if (width >= minTimeUnitWidth && width < maxTimeUnitWidth) {
                this.mTimeUnit = item;
                this.mTimeUnitWidth = width;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float paddingLeft = getPaddingLeft();
        int height = getMeasuredHeight();
        @SuppressLint("DrawAllocation")
        Rect bounds = new Rect();
        for (int i = 0; i < mTextVec.size(); ++i) {
            String text = this.mTextVec.get(i);
            this.mPaint.getTextBounds(text, 0, text.length(), bounds);
            int textWidth = bounds.width();
            float x = paddingLeft - textWidth / 2.0F + this.mTimeUnitWidth * i;
            if (canvas != null) {
                canvas.drawText(text, x, (float) (height + this.mTextSize.y) / 2.0F, this.mPaint);
            }
            if (canvas != null) {
                canvas.drawCircle(paddingLeft + this.mTimeUnitWidth * i + this.mTimeUnitWidth / 2.0F - this.mCursorSize / 2 + 1, height / 2.0F, this.mCursorSize / 2.0F, this.mPaint);
            }
        }
    }
}
