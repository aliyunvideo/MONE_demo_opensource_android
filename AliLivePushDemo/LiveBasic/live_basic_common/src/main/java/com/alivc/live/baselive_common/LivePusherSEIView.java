package com.alivc.live.baselive_common;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

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
        View inflateView = LayoutInflater.from(context).inflate(R.layout.push_sei_view_layout, this, true);

        mSeiEditText = inflateView.findViewById(R.id.et_sei);
        mSendSEIButton = inflateView.findViewById(R.id.btn_send_sei);

        mSendSEIButton.setOnClickListener(view -> {
            if (mListener != null) {
                String text = mSeiEditText.getText().toString();
                if (TextUtils.isEmpty(text)) {
                    // 如果输入框为空，默认输出指定的json格式以供测试
                    text = getDefaultJsonContent();
                }
                mListener.onSendSeiClick(text);
            }
        });
    }

    public interface SendSeiViewListener {
        void onSendSeiClick(String text);
    }

    public void setSendSeiViewListener(SendSeiViewListener mListener) {
        this.mListener = mListener;
    }

    private String getDefaultJsonContent() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put("customizeData", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject2.toString();
    }
}
