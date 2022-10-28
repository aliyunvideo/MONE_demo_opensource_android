package com.aliyun.interactive_common;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alibaba.android.arouter.launcher.ARouter;
import com.alivc.live.commonbiz.SharedPreferenceUtils;

public class InteractAppInfoActivity extends AppCompatActivity {

    public static final String FROM_EDITOR = "from_editor";
    public static final String INTENT_FROM_PK = "intent_from_pk";
    private static final int REQ_CODE_PERMISSION = 0x1111;

    private ImageView mBackImageView;
    private EditText mAppIdEditText;
    private EditText mAppKeyEditText;
    private EditText mPlayDomainEditText;
    private TextView mConfirmTextView;
    private ImageView mAppIdClearImageView;
    private ImageView mAppKeyClearImageView;
    private ImageView mPlayDomainClearImageView;

    private boolean mFromEditor = false;
    private boolean mIntentPK = false;
    private EditText mCurrentEditText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interact_app_info);

        mFromEditor = getIntent().getBooleanExtra(FROM_EDITOR, false);
        mIntentPK = getIntent().getBooleanExtra(INTENT_FROM_PK, false);

        initView();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);

        mAppIdEditText = findViewById(R.id.et_app_id);
        mAppKeyEditText = findViewById(R.id.et_app_key);
        mPlayDomainEditText = findViewById(R.id.et_play_domain);

        mAppIdClearImageView = findViewById(R.id.iv_app_id_clear);
        mAppKeyClearImageView = findViewById(R.id.iv_app_key_clear);
        mPlayDomainClearImageView = findViewById(R.id.iv_play_domain_clear);

        mConfirmTextView = findViewById(R.id.tv_confirm);
    }

    private void initListener() {
        mBackImageView.setOnClickListener((view) -> finish());

        mAppIdEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAppIdClearImageView.setImageResource(TextUtils.isEmpty(editable.toString()) ? R.drawable.scan_icon : R.drawable.ic_close);
                changeConfirmTextView(checkEnable());
            }
        });

        mAppKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAppKeyClearImageView.setImageResource(TextUtils.isEmpty(editable.toString()) ? R.drawable.scan_icon : R.drawable.ic_close);
                changeConfirmTextView(checkEnable());
            }
        });

        mPlayDomainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPlayDomainClearImageView.setImageResource(TextUtils.isEmpty(editable.toString()) ? R.drawable.scan_icon : R.drawable.ic_close);
                changeConfirmTextView(checkEnable());
            }
        });

        mAppIdClearImageView.setOnClickListener((view) -> checkForQrOrClear(mAppIdEditText));
        mAppKeyClearImageView.setOnClickListener((view) -> checkForQrOrClear(mAppKeyEditText));
        mPlayDomainClearImageView.setOnClickListener((view) -> checkForQrOrClear(mPlayDomainEditText));

        mConfirmTextView.setOnClickListener((view) -> {
            if (checkEnable()) {
                SharedPreferenceUtils.setAppId(getApplicationContext(), mAppIdEditText.getText().toString());
                SharedPreferenceUtils.setAppKey(getApplicationContext(), mAppKeyEditText.getText().toString());
                SharedPreferenceUtils.setPlayDomain(getApplicationContext(), mPlayDomainEditText.getText().toString());
                if (!mFromEditor) {
                    if (mIntentPK) {
                        ARouter.getInstance().build("/interactivePK/pkInput").navigation();
                    } else {
                        ARouter.getInstance().build("/interactiveLive/liveInput").navigation();
                    }
                }
                finish();
            }
        });

        mAppIdEditText.setText(SharedPreferenceUtils.getAppId(getApplicationContext().getApplicationContext()));
        mAppKeyEditText.setText(SharedPreferenceUtils.getAppKey(getApplicationContext().getApplicationContext()));
        mPlayDomainEditText.setText(SharedPreferenceUtils.getPlayDomain(getApplicationContext().getApplicationContext()));
    }

    private void checkForQrOrClear(EditText editText) {
        String content = editText.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            mCurrentEditText = editText;
            startQr();
        } else {
            editText.setText("");
        }
    }

    private void changeConfirmTextView(boolean enable) {
        if (enable) {
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        } else {
            mConfirmTextView.setBackground(getResources().getDrawable(R.drawable.shape_rect_blue));
        }
    }

    private boolean checkEnable() {
        String appId = mAppIdEditText.getText().toString();
        String appKey = mAppKeyEditText.getText().toString();
        String playDomain = mPlayDomainEditText.getText().toString();
        return !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(appKey) && !TextUtils.isEmpty(playDomain);
    }

    private void startQr() {
        if (ContextCompat.checkSelfPermission(InteractAppInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(InteractAppInfoActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agree the permission
                    startCaptureActivityForResult();
                } else {
                    // User disagree the permission
                    Toast.makeText(this, "You must agree the camera permission request before you use the code scan function", Toast.LENGTH_LONG).show();
                }
            }
            break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        if (mCurrentEditText != null) {
                            mCurrentEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    case RESULT_CANCELED:
                        if (data != null && mCurrentEditText != null) {
                            // for some reason camera is not working correctly
                            mCurrentEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(InteractAppInfoActivity.this, CaptureActivity.class);
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
}