/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class VideoTrimFrameLayout extends FrameLayout implements SizeChangedNotifier {
	protected int mNextX;
	protected int mNextY;
	private int mMaxX = Integer.MAX_VALUE;
	protected Scroller mScroller;
	private GestureDetector mGesture;
	private OnVideoScrollCallBack VideoCallback;

	public interface OnVideoScrollCallBack {
        void onVideoScroll(float distanceX, float distanceY);

		void onVideoSingleTapUp();
    }

    public VideoTrimFrameLayout(Context context) {
        super(context);
        init();
    }

    public VideoTrimFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public VideoTrimFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
    	mNextX = 0;
    	mNextY = 0;
		mMaxX = Integer.MAX_VALUE;
    	mScroller = new Scroller(getContext());
    	mGesture = new GestureDetector(getContext(), mOnGesture);
    }

    public void setOnScrollCallBack(OnVideoScrollCallBack callback) {
    	VideoCallback = callback;
    }

    @Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		boolean handled = super.dispatchTouchEvent(ev);
		handled |= mGesture.onTouchEvent(ev);
		return handled;
	}

    private OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			if(VideoCallback != null){
				VideoCallback.onVideoSingleTapUp();
			}
			return false;
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return VideoTrimFrameLayout.this.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
			return VideoTrimFrameLayout.this.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {

			synchronized(VideoTrimFrameLayout.this){
				mNextX += (int)distanceX;
				mNextY += (int)distanceY;

			}

			VideoCallback.onVideoScroll(distanceX, distanceY);

			requestLayout();


			return true;
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}
    };

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                              float velocityY) {
    	synchronized(VideoTrimFrameLayout.this){
    		mScroller.fling(mNextX, mNextY, (int)-velocityX, (int)-velocityY, 0, mMaxX, 0, mMaxX);
	}
	requestLayout();

	return true;
}

    protected boolean onDown(MotionEvent e) {
		mScroller.forceFinished(true);
		return true;
	}

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private SizeChangedNotifier.Listener _OnSizeChangedListener;

    @Override
    public void setOnSizeChangedListener(SizeChangedNotifier.Listener listener) {
        _OnSizeChangedListener = listener;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (_OnSizeChangedListener != null) {
            _OnSizeChangedListener.onSizeChanged(this, w, h, oldw, oldh);
        }
    }
}
