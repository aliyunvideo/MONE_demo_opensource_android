package com.aliyun.aio.avtheme;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class AVBaseThemeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setBackgroundDrawable(null);
        getDelegate().setLocalNightMode(specifiedThemeMode());
        super.onCreate(savedInstanceState);
    }

    protected int specifiedThemeMode() {
        return AppCompatDelegate.MODE_NIGHT_NO;
    }
}
