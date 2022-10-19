package com.alivc.live.pusher.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.live.pusher.demo.R;

/**
 * 展示 SEI 信息 View
 */
public class SeiView extends FrameLayout {


    private TextView mDataTextView;

    public SeiView(@NonNull Context context) {
        super(context);
        initView(context);
    }

    public SeiView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SeiView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.live_sei_view, this, true);
        mDataTextView = inflate.findViewById(R.id.tv_data);
    }

    public void setText(String text){
        mDataTextView.setText(text);
    }
}
