package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.aliyun.common.utils.DensityUtil;

import java.util.Random;

public class MusicWaveView extends View {
    private static final String TAG = "MusicWaveView";

    private int mDisplayTime;
    private int mTotalTime;
    private float[] mWaveArray;
    private int mStartOffset;
    private int mWaveHeight;
    private int mScreenWidth;
    private int mSelectBgWidth;

//    private static final int WAVE_STEP = 1 * 1000;
    private static final int WAVE_WIDTH = 6;
    private static final int WAVE_OFFSET = 2;
    private static final float MIN_WAVE_RATE = 0.25f;

    private Paint mPaint = new Paint();
    private Rect mRect = new Rect();

    private int mWidth;
    private int mHeight;

    public MusicWaveView(Context context) {
        super(context);
        init(context);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MusicWaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context){
        mWaveHeight = DensityUtil.dip2px(context,40);
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mSelectBgWidth = DensityUtil.dip2px(context,200);
        mStartOffset = (mScreenWidth - mSelectBgWidth)/2;
        setWillNotDraw(false);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
    }

    public void layout(){
        if(mDisplayTime != 0 && mTotalTime != 0){
            int lWidth = (int)((mTotalTime / (float)mDisplayTime) * mSelectBgWidth) + mStartOffset*2;
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
            params.width = lWidth;
            setLayoutParams(params);
            mWidth = lWidth;
            Log.e(TAG,"lWidth..." + lWidth);
            mHeight = params.height;
            generateWaveArray();
            invalidate();
        }

    }

    private void generateWaveArray(){
        int count = (mWidth -( mStartOffset*2)) / (WAVE_WIDTH + WAVE_OFFSET);
        mWaveArray = new float[count];
        Random random = new Random();
        random.setSeed(mTotalTime);
        for(int i = 0 ; i < count ; i++){
            mWaveArray[i] = random.nextFloat();
            if(mWaveArray[i] <  MIN_WAVE_RATE){
                mWaveArray[i] += MIN_WAVE_RATE;
            }
        }
    }

    public void setDisplayTime(int displayTime){
        mDisplayTime = displayTime;
    }

    public void setTotalTime(int totalTime){
        mTotalTime = totalTime;
    }

    public int getMusicLayoutWidth(){
        return (mWidth -(mStartOffset*2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(getHeight() != 0 && mWaveArray != null){
            for(int i = 0 ; i< mWaveArray.length ; i++){
                int height = (int) (mWaveHeight * mWaveArray[i]);
                int left = i * (WAVE_OFFSET + WAVE_WIDTH) + mStartOffset;
                int right = left + WAVE_WIDTH;
                int top = (getHeight() -  height)/2;
                int bottom = top + height ;
                mRect.set(left,top,right,bottom);
                canvas.drawRect(mRect,mPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int width = 0;
        int height = 0;
        //获得宽度MODE
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        //获得宽度的值
        if (modeW == MeasureSpec.AT_MOST) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        if (modeW == MeasureSpec.EXACTLY) {
            width = widthMeasureSpec;
        }
        if (modeW == MeasureSpec.UNSPECIFIED) {
            width = mWidth;
        }
        //获得高度MODE
        int modeH = MeasureSpec.getMode(height);
        //获得高度的值
        if (modeH == MeasureSpec.AT_MOST) {
            height = MeasureSpec.getSize(heightMeasureSpec);
        }
        if (modeH == MeasureSpec.EXACTLY) {
            height = heightMeasureSpec;
        }
        if (modeH == MeasureSpec.UNSPECIFIED) {
            //ScrollView和HorizontalScrollView
            height = mHeight;
        }
        //设置宽度和高度
        setMeasuredDimension(width, height);
    }
}
