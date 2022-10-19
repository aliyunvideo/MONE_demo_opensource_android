/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

public class CircularColorView extends View {
	
	private Paint mPaint;
	private int strokeColor;
	private int color;

	public CircularColorView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public CircularColorView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public CircularColorView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init();
	}

	private void init(){
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}
	
//	public void setCircularColorResources(int resid){
//		setCircularColor(getResources().getColor(resid));
//	}
	
	public void setCircularColor(int color){
		this.color = color;
	}
	
	public void setStrokeColorResources(int resid){
		setStrokeColor(getResources().getColor(resid));
	}
	
	public void setStrokeColor(int color){
		strokeColor = color;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = getMeasuredWidth();
	    setMeasuredDimension(width, width);
	}

	@Override
	protected void onDraw(Canvas canvas) {		
		super.onDraw(canvas);		
		mPaint.setColor(color);
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(0);
		Log.d("COLOR", "left : " + getLeft() + " right : " + getRight() + " top : " + getTop() + " bottom : " + getBottom());
		canvas.drawOval(new RectF(getLeft(), getTop(), getRight(), getBottom()), mPaint);
		if(strokeColor != 0){
			if(strokeColor == Color.BLACK){
				drawWhiteStroke(canvas);
			}else{
				drawCircleStroke(canvas);
			}
		}else if(color == Color.BLACK){
			drawWhiteStroke(canvas);
		} else if(color == Color.parseColor("#121212")){
			drawBlackStroke(canvas);
		}
		
	}
	
	private void drawCircleStroke(Canvas canvas){
		float stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		mPaint.setStrokeWidth(stroke);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setColor(strokeColor);
		stroke -= TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.6f, getResources().getDisplayMetrics());
		canvas.drawOval(new RectF(getLeft() + stroke, getTop() + stroke, getRight() - stroke, getBottom() - stroke), mPaint);
	}

	private void drawBlackStroke(Canvas canvas){
		float stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		mPaint.setStrokeWidth(stroke);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setColor(Color.parseColor("#33ffffff"));
		canvas.drawOval(new RectF(getLeft() + stroke, getTop() + stroke, getRight() - stroke, getBottom() - stroke), mPaint);
	}
	
	private void drawWhiteStroke(Canvas canvas){
		float stroke = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());
		mPaint.setStrokeWidth(stroke);
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeCap(Cap.ROUND);
		mPaint.setStrokeJoin(Join.ROUND);
		mPaint.setColor(Color.WHITE);
		canvas.drawOval(new RectF(getLeft() + stroke, getTop() + stroke, getRight() - stroke, getBottom() - stroke), mPaint);
	}
	
}
