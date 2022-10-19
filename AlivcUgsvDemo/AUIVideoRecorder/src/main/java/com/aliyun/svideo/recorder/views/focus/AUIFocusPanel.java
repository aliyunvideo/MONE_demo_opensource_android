package com.aliyun.svideo.recorder.views.focus;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.aliyun.ugsv.common.utils.DensityUtils;
import com.aliyun.svideo.recorder.R;

import java.lang.ref.WeakReference;

/**
 * 焦点显示
 */
public class AUIFocusPanel extends RelativeLayout {

    /**
     * Handler message code
     */
    private static final int MSG_HIDE_VIEW = 1000;

    /**
     * 延迟隐藏时间
     */
    private static final int DELAYED_HIDE_DURATION = 2700;

    /**
     * 动画时间
     */
    private static final int ANIM_DURATION = 300;

    private TimeHandler mTimeHandler;
    private OnViewHideListener mOnViewHideListener;
    private int mFocusSize;
    private ObjectAnimator mScaleAnimX;
    private ObjectAnimator mScaleAnimY;

    private View mFocusView;

    public AUIFocusPanel(Context context) {
        this(context, null);
    }

    public AUIFocusPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        initSize(context);
        initUI();
    }

    private void initSize(Context context) {
        mFocusSize = DensityUtils.dip2px(context, 84);
    }

    private void initUI() {
        setBackgroundColor(Color.TRANSPARENT);
        mFocusView = new View(getContext());
        mFocusView.setBackgroundResource(R.drawable.ic_ugsv_recorder_focus);


        addView(mFocusView, mFocusSize, mFocusSize);
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
        mScaleAnimX = ObjectAnimator.ofFloat(mFocusView, "scaleX", 1.5f, 1.0f).setDuration(ANIM_DURATION);
        mScaleAnimY = ObjectAnimator.ofFloat(mFocusView, "scaleY", 1.5f, 1.0f).setDuration(ANIM_DURATION);

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
        float mTranslationX = x - mFocusSize / 2;
        mFocusView.setX(mTranslationX);
        mFocusView.setY(y - mFocusSize);
    }

    static class TimeHandler extends Handler {
        WeakReference<AUIFocusPanel> weakReference;

        TimeHandler(AUIFocusPanel AUIFocusView) {
            weakReference = new WeakReference<>(AUIFocusView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_HIDE_VIEW) {
                AUIFocusPanel AUIFocusView = weakReference.get();
                AUIFocusView.hideView();
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
