package com.aliyun.svideo.base.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.TextView;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.aliyun.common.global.Version;
import com.aliyun.svideo.base.R;

/**
 * 显示sdk的版本信息
 * 短视频的三个版本都会使用
 */
public class SdkVersionActivity extends AppCompatActivity {
    public static String DEBUG_DEVELOP_URL = "debug_develop_url";
    public static String DEBUG_PARAMS = "DebugParams";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_version);
        showVersionInfo();
        initDebugParams();
    }

    @SuppressLint("SetTextI18n")
    private void showVersionInfo() {
        ((TextView)findViewById(R.id.tv_version)).setText("VERSION :" + Version.VERSION);
        ((TextView)findViewById(R.id.tv_module)).setText("MODULE :" + Version.MODULE);
        ((TextView)findViewById(R.id.tv_build_id)).setText("BUILD_ID :" + Version.BUILD_ID);
        ((TextView)findViewById(R.id.tv_src_commit_id)).setText("SRC_COMMIT_ID :" + Version.SRC_COMMIT_ID);
        ((TextView)findViewById(R.id.tv_alivc_commit_id)).setText("ALIVC_COMMIT_ID :" + Version.ALIVC_COMMIT_ID);
        ((TextView)findViewById(R.id.tv_android_commit_id)).setText("ANDROID_COMMIT_ID :" + Version.ANDROID_COMMIT_ID);
    }

    /**
     * 仅仅限制demo 使用，外部客户请勿要调用
     */
    private void initDebugParams() {
        final RadioButton developRadioButton = findViewById(R.id.btn_develop_url);
        final RadioButton prereleaseRadioButton = findViewById(R.id.btn_pre_release_url);
        final RadioButton releaseRadioButton = findViewById(R.id.btn_release_url);

        int hostType = getIntValue(DEBUG_DEVELOP_URL);
        if (hostType == 1) {
            developRadioButton.setChecked(true);
            prereleaseRadioButton.setChecked(false);
            releaseRadioButton.setChecked(false);
        } else if (hostType == 2) {
            developRadioButton.setChecked(false);
            prereleaseRadioButton.setChecked(true);
            releaseRadioButton.setChecked(false);
        } else {
            developRadioButton.setChecked(false);
            prereleaseRadioButton.setChecked(false);
            releaseRadioButton.setChecked(true);
        }
        findViewById(R.id.btn_sure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int hostType = 0;
                if(developRadioButton.isChecked()){
                    hostType = 1;
                }else if(prereleaseRadioButton.isChecked()){
                    hostType = 2;
                }
                //AlivcSdkCore.setDebugHostType(hostType);
                setLicenseDebugParams(hostType);
                Toast.makeText(SdkVersionActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void setLicenseDebugParams(int hostType) {
        SharedPreferences mySharedPreferences = this.getSharedPreferences(DEBUG_PARAMS, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putInt(DEBUG_DEVELOP_URL, hostType);
        editor.apply();
    }

    private int getIntValue(String key) {
        SharedPreferences mySharedPreferences = this.getSharedPreferences(DEBUG_PARAMS, Activity.MODE_PRIVATE);
        return mySharedPreferences.getInt(key, 0);
    }
}
