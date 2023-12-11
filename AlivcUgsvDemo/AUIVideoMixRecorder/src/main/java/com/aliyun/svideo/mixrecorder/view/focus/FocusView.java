package com.aliyun.svideo.mixrecorder.view.focus;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.aliyun.svideo.record.R;
import com.aliyun.ugsv.common.utils.DensityUtils;

import java.lang.ref.WeakReference;

/**
 * 焦点显示
 *
 * @author xlx
 */
public class FocusView extends View {

    /**
     * Handler message code
     */
    private static final int MSG_HIDE_VIEW = 1000;

    /**
     * 延迟隐藏时间
     */
    private static final int DELAYED_HIDE_DURATION = 2700;

    private Paint mPaint;

    /**
     * side length (px)
     */
    private int mSideLength = 240;

    /**
     * aim line length (px)
     */
    private int mAimLength = 15;

    /**
     * 旋转角度
     */
    private static final int ROTATION_DEGRESS = 90;
    /**
     * 旋转次数
     */
    private static final int ROTATE_COUNT = 4;

    /**
     * 画笔宽度
     */
    private static final int STROKE_WIDTH = 2;

    /**
     * view self padding (px)
     */
    private static final int OFFEST_SIZE = 50;

    /**
     * 动画时间
     */
    private static final int ANIM_DURATION = 300;

    Rect mSideRect = new Rect();

    private TimeHandler mTimeHandler;
    private OnViewHideListener mOnViewHideListener;
    private int mHalfSideLength;
    private ObjectAnimator mScaleAnimX;
    private ObjectAnimator mScaleAnimY;

    public FocusView(Context context) {
        this(context, null);
    }

    public FocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setVisibility(GONE);
        init(context);
    }

    private void init(Context context) {
        initUI();
        initSize(context);

    }

    private void initUI() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.parseColor("#FECB2F"));
        mPaint.setStrokeWidth(STROKE_WIDTH);
        setBackgroundColor(Color.TRANSPARENT);
    }

    private void initSize(Context context) {
        mSideLength = (int) context.getResources().getDimension(R.dimen.alivc_record_focus_rect);
        mHalfSideLength = mSideLength >> 1;
        mAimLength = DensityUtils.px2dip(context, mAimLength);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mSideRect.set(STROKE_WIDTH, STROKE_WIDTH, mSideLength + STROKE_WIDTH, mSideLength + STROKE_WIDTH);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(mSideRect, mPaint);
        canvas.save();
        for (int i = 0; i < ROTATE_COUNT; i++) {
            canvas.rotate(ROTATION_DEGRESS * i, mHalfSideLength + STROKE_WIDTH, mHalfSideLength + STROKE_WIDTH);
            canvas.drawLine(mHalfSideLength + STROKE_WIDTH, STROKE_WIDTH, mHalfSideLength + STROKE_WIDTH, mAimLength + STROKE_WIDTH, mPaint);
        }
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mSideLength + OFFEST_SIZE, mSideLength + OFFEST_SIZE);
    }


    public void showView() {
        if (mTimeHandler == null) {
            mTimeHandler = new TimeHandler(this);
        }
        int visibility = getVisibility();
        if (visibility == VISIBLE) {
            mTimeHandler.removeMessages(MSG_HIDE_VIEW);
        } else {
            setVisibility(VISIBLE);
        }
        startScaleAnim();
        mTimeHandler.sendMessageDelayed(mTimeHandler.obtainMessage(MSG_HIDE_VIEW), DELAYED_HIDE_DURATION);
    }

    private void startScaleAnim() {
        mScaleAnimX = ObjectAnimator.ofFloat(this, "scaleX", 1.5f, 1.0f).setDuration(ANIM_DURATION);
        mScaleAnimY = ObjectAnimator.ofFloat(this, "scaleY", 1.5f, 1.0f).setDuration(ANIM_DURATION);

        AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();

        mScaleAnimX.setInterpolator(accelerateInterpolator);
        mScaleAnimX.start();

        mScaleAnimY.setInterpolator(accelerateInterpolator);
        mScaleAnimY.start();
    }

    public void activityStop() {
        setVisibility(GONE);
        if (mTimeHandler != null) {
            if (mTimeHandler.weakReference != null) {
                mTimeHandler.weakReference.clear();
            }
            mTimeHandler.removeMessages(MSG_HIDE_VIEW);
            mTimeHandler = null;
        }

        if (mScaleAnimX != null) {
            mScaleAnimX.cancel();
            mScaleAnimX.removeAllListeners();
            mScaleAnimX = null;
        }

        if (mScaleAnimY != null) {
            mScaleAnimY.cancel();
            mScaleAnimY.removeAllListeners();
            mScaleAnimY = null;
        }
    }


    public void setOnViewHideListener(OnViewHideListener listener) {
        this.mOnViewHideListener = listener;
    }

    public void setLocation(float x, float y) {
        float mTranslationX = x - mSideLength / 2;
        setX(mTranslationX);
        setY(y - mSideLength);
    }

    static class TimeHandler extends Handler {
        WeakReference<FocusView> weakReference;

        TimeHandler(FocusView focusView) {
            weakReference = new WeakReference<>(focusView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE_VIEW) {
                FocusView focusView = weakReference.get();
                focusView.hideView();
            }
        }
    }

    private void hideView() {
        setVisibility(GONE);
        if (mOnViewHideListener != null) {
            mOnViewHideListener.onHided();
        }
    }

    public interface OnViewHideListener {
        void onHided();
    }
}
