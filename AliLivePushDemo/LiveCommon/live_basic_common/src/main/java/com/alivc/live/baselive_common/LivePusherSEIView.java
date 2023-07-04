package com.alivc.live.baselive_common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class LivePusherSEIView extends LinearLayout {

    private Context mContext;
    private EditText mSeiEditText;
    private Button mSendSEIButton;

    private SendSeiViewListener mListener;

    public LivePusherSEIView(Context context) {
        super(context);
        init(context);
    }

    public LivePusherSEIView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LivePusherSEIView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        View inflateView = LayoutInflater.from(context).inflate(R.layout.push_sei_view_layout,this,true);

        mSeiEditText = inflateView.findViewById(R.id.et_sei);
        mSendSEIButton = inflateView.findViewById(R.id.btn_send_sei);

        mSendSEIButton.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onSendSeiClick(mSeiEditText.getText().toString());
            }
        });
    }

    public interface SendSeiViewListener {
        void onSendSeiClick(String text);
    }

    public void setSendSeiViewListener(SendSeiViewListener mListener) {
        this.mListener = mListener;
    }
}
