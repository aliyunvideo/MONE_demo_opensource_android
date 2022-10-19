package com.alivc.live.pusher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.alivc.live.pusher.demo.R;

public class DataView extends LinearLayout {
    public DataView(Context context) {
        super(context);
        initView(context);
    }

    public DataView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DataView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
//        setBackgroundResource(R.color.color_background_white_alpha_30);
        LayoutInflater.from(context).inflate(R.layout.live_data_view, this, true);
        setOrientation(VERTICAL);
    }
}
