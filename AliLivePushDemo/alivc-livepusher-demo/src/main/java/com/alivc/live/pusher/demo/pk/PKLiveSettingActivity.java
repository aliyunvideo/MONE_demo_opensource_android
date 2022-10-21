package com.alivc.live.pusher.demo.pk;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.interactlive.LivePushGlobalConfig;

public class PKLiveSettingActivity extends AppCompatActivity {


    private ImageView mBackImageView;
    private Switch mMultiInteractSwitch;
    private Button mCommitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact_live_setting);

        initView();
        initListener();
    }

    private void initView(){
        mBackImageView = findViewById(R.id.iv_back);
        mCommitButton = findViewById(R.id.btn_commit);
        mMultiInteractSwitch = findViewById(R.id.multi_interaction_control);

        mMultiInteractSwitch.setChecked(LivePushGlobalConfig.IS_MULTI_PK);
    }

    private void initListener(){
        mBackImageView.setOnClickListener(view -> {
            finish();
        });
        mCommitButton.setOnClickListener(view -> finish());
        mMultiInteractSwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.IS_MULTI_PK = b);
    }
}