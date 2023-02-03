package com.aliyun.svideo.template.sample.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideo.template.sample.R;

public class TitleBar extends FrameLayout {
    private Activity mActivity;
    private TextView tvTitle;
    private ImageView btnBack;
    public TitleBar(@NonNull Context context) {
        this(context, null);
    }
    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public TitleBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivity = ((Activity) context);
        LayoutInflater.from(context).inflate(R.layout.title_bar, this, true);
        tvTitle = findViewById(R.id.title);
        btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(v -> {
            mActivity.onBackPressed();
        });
    }
    public void setTitle(int resId) {
        tvTitle.setText(resId);
    }
    public void setTitle(String content){
         tvTitle.setText(content);
    }
    public void showBackButton(boolean b) {
        btnBack.setVisibility(b?VISIBLE:GONE);
    }

}
