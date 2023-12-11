package com.alivc.live.pusher.demo.backdoor;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.pusher.demo.R;

/**
 * @author keria
 * @date 2023/10/13
 * @brief
 */
public class BackDoorActivity extends AppCompatActivity {

    private Switch mBareStreamSw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_activity_backdoor);
        initViews();
        initData();
    }

    private void initViews() {
        mBareStreamSw = findViewById(R.id.sw_bare_stream);
        mBareStreamSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (BackDoorInstance.getInstance().isShowBareStream() != isChecked) {
                    BackDoorInstance.getInstance().setShowBareStream(isChecked);
                }
            }
        });
    }

    private void initData() {
        mBareStreamSw.setChecked(BackDoorInstance.getInstance().isShowBareStream());
    }
}
