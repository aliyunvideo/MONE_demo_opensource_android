package com.aliyun.svideo.mixrecorder.view.countdown;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aliyun.svideo.record.R;

/**
 * 倒计时view
 */
public class AlivcCountDownView extends FrameLayout {

    private TextView tvCount;
    private View view;
    private CountDownTimer countDownTimer;
    /**
     * 倒计时结束监听
     */
    private OnCountDownFinishListener onCountDownFinishListener;

    public AlivcCountDownView(Context context) {
        this(context, null);
    }

    public AlivcCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlivcCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.alivc_recorder_view_count_down, this, true);
        setVisibility(GONE);
        tvCount = findViewById(R.id.tv_count);
    }

    public void start() {
        setVisibility(VISIBLE);
        countDownTimer = new CountDownTimer(3000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                millisUntilFinished /= 1000;
                updateCount(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (onCountDownFinishListener != null) {
                    onCountDownFinishListener.onFinish();
                }
                setVisibility(GONE);
            }
        };
        countDownTimer.start();
    }

    public void cancle() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            setVisibility(GONE);
        }
    }

    public void updateCount(long seconds) {
        tvCount.setText(seconds + "");
    }

    public interface OnCountDownFinishListener {
        /**
         * 倒计时结束
         */
        void onFinish();
    }

    /**
     * 设置倒计时结束回调监听
     * @param listener
     */
    public void setOnCountDownFinishListener(OnCountDownFinishListener listener) {
        this.onCountDownFinishListener = listener;
    }



}

