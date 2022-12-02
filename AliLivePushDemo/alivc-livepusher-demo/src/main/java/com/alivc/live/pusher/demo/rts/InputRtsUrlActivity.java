package com.alivc.live.pusher.demo.rts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.pusher.demo.R;

/**
 * URL 输入界面
 */
public class InputRtsUrlActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;

    private ImageView mBackImageView;
    private EditText mUrlEditText;
    private ImageView mIconImageView;
    private TextView mStartPlayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_rts_url);

        initView();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mUrlEditText = findViewById(R.id.et_url);
        mIconImageView = findViewById(R.id.iv_icon);
        mStartPlayTextView = findViewById(R.id.tv_start_play);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(view -> finish());

        mUrlEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                changeState();
            }
        });

        mStartPlayTextView.setOnClickListener(view -> {
            String url = mUrlEditText.getText().toString();
            if (!urlEditTextIsEmpty()) {
                Intent intent = new Intent(InputRtsUrlActivity.this,RtsPlayActivity.class);
                intent.putExtra("rts_url",url);
                startActivity(intent);
            }
        });

        //二维码扫描
        mIconImageView.setOnClickListener(view -> {
            if (urlEditTextIsEmpty()) {
                if (ContextCompat.checkSelfPermission(InputRtsUrlActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // Do not have the permission of camera, request it.
                    ActivityCompat.requestPermissions(InputRtsUrlActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
                } else {
                    // Have gotten the permission
                    startCaptureActivityForResult();
                }
            } else {
                mUrlEditText.setText("");
            }
        });
    }

    private void changeState() {
        if (urlEditTextIsEmpty()) {
            mIconImageView.setImageResource(R.drawable.scan_icon);
            mStartPlayTextView.setTextColor(getResources().getColor(R.color.text_ultraweak));
            mStartPlayTextView.setBackgroundResource(R.drawable.shape_rts_rect_enable_blue);
        } else {
            mIconImageView.setImageResource(R.drawable.ic_close);
            mStartPlayTextView.setTextColor(getResources().getColor(R.color.text_strong));
            mStartPlayTextView.setBackgroundResource(R.drawable.shape_rts_rect_unable_blue);
        }
    }

    private boolean urlEditTextIsEmpty() {
        return TextUtils.isEmpty(mUrlEditText.getText().toString());
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(InputRtsUrlActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    if (mUrlEditText != null) {
                        mUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                    }
                    break;
                case RESULT_CANCELED:
                    if (data != null && mUrlEditText != null) {
                        // for some reason camera is not working correctly
                        mUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}