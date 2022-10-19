package com.aliyun.svideo.editor.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.svideo.editor.common.R;

public class SquareProgressView extends View {
    private Context mContext;
    private Paint processPaint;
    private Canvas canvas;

    private int currentProgress;
    private float strokeWith = 5.0f;
    private int progressColor = Color.RED;


    public SquareProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initValue(attrs);
    }


    private void initValue(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.SquareProcessView);
        currentProgress = typedArray.getInteger(R.styleable.SquareProcessView_currentProgress, 0);
        strokeWith = typedArray.getDimension(R.styleable.SquareProcessView_strokeWith, strokeWith);
        progressColor = typedArray.getColor(R.styleable.SquareProcessView_progressColor, progressColor);
        initProcessPaint();
    }

    private void initProcessPaint() {
        processPaint = new Paint();
        processPaint.setColor(progressColor);
        processPaint.setStrokeWidth(strokeWith * 2);
        processPaint.setAntiAlias(true);
        processPaint.setStyle(Paint.Style.STROKE);
    }

    public void setProgress(int progress) {
        this.currentProgress = progress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        drawProcess(currentProgress);
    }

    /**
     * 画进度
     */
    private void drawProcess(int progress) {
        int maxProgress = (canvas.getWidth() + canvas.getHeight()) * 2;
        if (maxProgress == 0) {
            return;
        }
        int curProgress = (int) (progress / 100.0f * maxProgress);
        float topProcess = 0;
        float rightProcess = 0;
        float bottomProcess = 0;
        float leftProcess = 0;
        if (curProgress <= canvas.getWidth()) {
            topProcess = curProgress;
        } else if (curProgress <= (canvas.getWidth() + canvas.getHeight())) {
            topProcess = canvas.getWidth();
            rightProcess = curProgress - topProcess;
        } else if (curProgress <= (canvas.getWidth() + canvas.getHeight() + canvas.getWidth())) {
            topProcess = canvas.getWidth();
            rightProcess = canvas.getHeight();
            bottomProcess = curProgress - topProcess - rightProcess;
        } else if (curProgress <= maxProgress) {
            topProcess = canvas.getWidth();
            rightProcess = canvas.getHeight();
            bottomProcess = canvas.getWidth();
            leftProcess = curProgress - topProcess - rightProcess - bottomProcess;
        }
        drawProgressTopLine(topProcess);
        drawProgressRightLine(rightProcess);
        drawProgressBottomLine(bottomProcess);
        drawProgressLeftLine(leftProcess);
    }

    private void drawProgressTopLine(float progress) {
        Path path = new Path();
        path.moveTo(0, 0);
        path.lineTo(progress, 0);
        canvas.drawPath(path, processPaint);
    }

    private void drawProgressRightLine(float progress) {
        Path path = new Path();
        path.moveTo(canvas.getWidth(), 0);
        path.lineTo(canvas.getWidth(), progress);
        canvas.drawPath(path, processPaint);
    }

    private void drawProgressBottomLine(float progress) {
        Path path = new Path();
        path.moveTo(canvas.getWidth(), canvas.getHeight());
        path.lineTo(canvas.getWidth() - progress, canvas.getHeight());
        canvas.drawPath(path, processPaint);
    }

    private void drawProgressLeftLine(float progress) {
        Path path = new Path();
        path.moveTo(0, canvas.getHeight());
        path.lineTo(0, canvas.getHeight() - progress);
        canvas.drawPath(path, processPaint);
    }

}
