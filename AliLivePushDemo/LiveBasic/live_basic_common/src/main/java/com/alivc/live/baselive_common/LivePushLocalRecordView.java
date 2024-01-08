package com.alivc.live.baselive_common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @author keria
 * @date 2023/10/11
 * @brief 本地录制视图
 */
public class LivePushLocalRecordView extends LinearLayout {

    private LocalRecordEventListener mLocalRecordEventListener;

    public LivePushLocalRecordView(@NonNull Context context) {
        super(context);
        initViews(context);
    }

    public LivePushLocalRecordView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public LivePushLocalRecordView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    public void initLocalRecordEventListener(LocalRecordEventListener listener) {
        mLocalRecordEventListener = listener;
    }

    private void initViews(@NonNull Context context) {
        View inflateView = LayoutInflater.from(context).inflate(R.layout.layout_local_record_view, this, true);

        Button startLocalRecordBtn = inflateView.findViewById(R.id.start_local_record_btn);
        Button stopLocalRecordBtn = inflateView.findViewById(R.id.stop_local_record_btn);

        startLocalRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalRecordEventListener != null) {
                    mLocalRecordEventListener.onStartLocalRecord();
                }
            }
        });

        stopLocalRecordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLocalRecordEventListener != null) {
                    mLocalRecordEventListener.onStopLocalRecord();
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLocalRecordEventListener = null;
    }

    public interface LocalRecordEventListener {
        public void onStartLocalRecord();

        public void onStopLocalRecord();
    }
}
