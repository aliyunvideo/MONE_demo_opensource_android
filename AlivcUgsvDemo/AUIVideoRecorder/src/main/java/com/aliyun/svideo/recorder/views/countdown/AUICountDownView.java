package com.aliyun.svideo.recorder.views.countdown;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.aliyun.svideo.recorder.R;

/**
 * 倒计时view
 */
public class AUICountDownView extends FrameLayout {


    private TextView mTextTime;
    private View mCancelBtn;
    private CountDownTimer countDownTimer;
    private boolean mIsTimerStopped = true;
    /**
     * 倒计时结束监听
     */
    private OnCountdownListener mOnCountDownListener;

    public AUICountDownView(Context context) {
        this(context, null);
    }

    public AUICountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AUICountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.ugsv_recorder_panel_countdown, this, true);
        setVisibility(GONE);
        mTextTime = findViewById(R.id.ugsv_recorder_panel_countdown_time);
        mCancelBtn = findViewById(R.id.ugsv_recorder_panel_countdown_cancel);
        mCancelBtn.setOnClickListener(v -> {
            clickCancel(true);
        });
    }

    private void clickCancel(boolean byInnerClick) {
        if (mIsTimerStopped) {
            return;
        }
        mIsTimerStopped = true;
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        if (byInnerClick && mOnCountDownListener != null) {
            mOnCountDownListener.onCancel();
        }
        setVisibility(GONE);
    }

    public void updateCountdownTime(long seconds) {
        if (seconds > 0) {
            mTextTime.setText(seconds + "");
        }
    }

    /**
     * 启动倒计时
     * @param countDownSeconds 倒计时长，单位秒
     */
    public void start(long countDownSeconds) {
        setVisibility(VISIBLE);
        countDownTimer = new CountDownTimer(countDownSeconds * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (mIsTimerStopped) {
                    return;
                }
                millisUntilFinished /= 1000;
                updateCountdownTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (mIsTimerStopped) {
                    return;
                }
                mIsTimerStopped = true;
                if (mOnCountDownListener != null) {
                    mOnCountDownListener.onFinish();
                }
                setVisibility(GONE);
            }
        };
        countDownTimer.start();
        mIsTimerStopped = false;
    }

    public void cancel() {
        clickCancel(false);
    }

    public interface OnCountdownListener {
        /**
         * 倒计时结束
         */
        void onFinish();

        /**
         * 倒计时取消
         */
        void onCancel();
    }

    /**
     * 设置倒计时回调监听
     *
     * @param listener
     */
    public void setOnCountdownListener(OnCountdownListener listener) {
        this.mOnCountDownListener = listener;
    }

}

