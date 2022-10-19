package com.aliyun.svideo.track.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.aliyun.svideo.track.inc.IMoveViewListener;


public class MoveView extends View {
    private boolean isTouchAble = false;
    private IMoveViewListener mMoveViewListener;
    private float downRawX;
    private float lastRawX;

    public MoveView(Context context) {
        super(context);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MoveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchAble(boolean touchAble) {
        isTouchAble = touchAble;
    }

    public void setMoveViewListener(IMoveViewListener moveViewListener) {
        this.mMoveViewListener = moveViewListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouchAble) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downRawX = event.getRawX();
                lastRawX = event.getRawX();
                if (mMoveViewListener != null) {
                    mMoveViewListener.onDown(event.getX());
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMoveViewListener != null){
                    mMoveViewListener.onMove(event.getRawX() - lastRawX,event.getRawX() - event.getX());
                }
                lastRawX = event.getRawX();
                break;
            case MotionEvent.ACTION_UP:
                float upRawX = event.getRawX();
                if (mMoveViewListener != null){
                    mMoveViewListener.onUp(upRawX -downRawX);
                }
                break;
            default:
                break;
        }
        return true;

    }
}
