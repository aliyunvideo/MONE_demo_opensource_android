package com.alivc.live.barestream_interactive;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.interactive_common.InteractiveSettingActivity;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.interactive_common.widget.InteractiveQrEditView;

public class InteractiveInputURLActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int REQ_CODE_PUSH_URL = 0x2222;
    private static final int REQ_CODE_PULL_URL = 0x3333;
    private static final int REQ_CODE_PULL_URL_1 = 0x4444;
    private static final int REQ_CODE_PULL_URL_2= 0x5555;
    private static final int REQ_CODE_PULL_URL_3 = 0x6666;
    private TextView mConfirmTextView;
    private TextView mSettingTextView;
    private ImageView mBackImageView;
    private InteractiveQrEditView mInteractivePushView;
    private InteractiveQrEditView mInteractivePullView;
    private InteractiveQrEditView mInteractivePullView1;
    private InteractiveQrEditView mInteractivePullView2;
    private InteractiveQrEditView mInteractivePullView3;
    private LinearLayout mMorePullViewRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_input_url);

        initView();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mConfirmTextView = findViewById(R.id.tv_confirm);
        mSettingTextView = findViewById(R.id.tv_setting);

        mMorePullViewRoot = findViewById(R.id.more_pull_view);
        mInteractivePushView = findViewById(R.id.push_view);
        mInteractivePullView = findViewById(R.id.pull_view);
        mInteractivePullView1 = findViewById(R.id.pull_view1);
        mInteractivePullView2 = findViewById(R.id.pull_view2);
        mInteractivePullView3 = findViewById(R.id.pull_view3);
    }

    private void initListener() {
        mInteractivePushView.setOnQrClickListener(() -> {
            startQr(REQ_CODE_PUSH_URL);
        });
        mInteractivePullView.setOnQrClickListener(() -> {
            startQr(REQ_CODE_PULL_URL);
        });

        mInteractivePullView1.setOnQrClickListener(() -> {
            startQr(REQ_CODE_PULL_URL_1);
        });

        mInteractivePullView2.setOnQrClickListener(() -> {
            startQr(REQ_CODE_PULL_URL_2);
        });

        mInteractivePullView3.setOnQrClickListener(() -> {
            startQr(REQ_CODE_PULL_URL_3);
        });

        mBackImageView.setOnClickListener(view -> finish());

        mConfirmTextView.setOnClickListener(view -> {
            String pushUrl = mInteractivePushView.getEditText();
            String pullUrl = mInteractivePullView.getEditText();
            Intent intent = new Intent(InteractiveInputURLActivity.this, InteractiveBareActivity.class);
            if (LivePushGlobalConfig.IS_MULTI_INTERACT) {
                intent  = new Intent(InteractiveInputURLActivity.this, InteractiveMultiBareActivity.class);
                String pullUrl1 = mInteractivePullView1.getEditText();
                String pullUrl2 = mInteractivePullView2.getEditText();
                String pullUrl3 = mInteractivePullView3.getEditText();
                intent.putExtra(InteractiveMultiBareActivity.DATA_INTERACTIVE_PULL_URL_1, pullUrl1);
                intent.putExtra(InteractiveMultiBareActivity.DATA_INTERACTIVE_PULL_URL_2, pullUrl2);
                intent.putExtra(InteractiveMultiBareActivity.DATA_INTERACTIVE_PULL_URL_3, pullUrl3);
            }
            intent.putExtra(InteractiveBareActivity.DATA_INTERACTIVE_PUSH_URL, pushUrl);
            intent.putExtra(InteractiveBareActivity.DATA_INTERACTIVE_PULL_URL, pullUrl);
            startActivity(intent);
        });

        mSettingTextView.setOnClickListener(view -> {
            Intent intent = new Intent(this, InteractiveSettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMorePullViewRoot.setVisibility(LivePushGlobalConfig.IS_MULTI_INTERACT ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_PUSH_URL:
            case REQ_CODE_PULL_URL:
            case REQ_CODE_PULL_URL_1:
            case REQ_CODE_PULL_URL_2:
            case REQ_CODE_PULL_URL_3:
                switch (resultCode) {
                    case RESULT_OK:
                        if (requestCode == REQ_CODE_PUSH_URL) {
                            if (mInteractivePushView != null) {
                                mInteractivePushView.setEditText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                        } else if (requestCode == REQ_CODE_PULL_URL) {
                            if (mInteractivePullView != null) {
                                mInteractivePullView.setEditText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                         } else if (requestCode == REQ_CODE_PULL_URL_1) {
                            if (mInteractivePullView1 != null) {
                                mInteractivePullView1.setEditText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                        } else if (requestCode == REQ_CODE_PULL_URL_2) {
                            if (mInteractivePullView2 != null) {
                                mInteractivePullView2.setEditText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                        } else {
                            if (mInteractivePullView3 != null) {
                                mInteractivePullView3.setEditText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
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

    private void startQr(int requestCode) {
        if (ContextCompat.checkSelfPermission(InteractiveInputURLActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(InteractiveInputURLActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult(requestCode);
        }
    }

    private void startCaptureActivityForResult(int requestCode) {
        Intent intent = new Intent(InteractiveInputURLActivity.this, CaptureActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean(CaptureActivity.KEY_NEED_BEEP, CaptureActivity.VALUE_BEEP);
        bundle.putBoolean(CaptureActivity.KEY_NEED_VIBRATION, CaptureActivity.VALUE_VIBRATION);
        bundle.putBoolean(CaptureActivity.KEY_NEED_EXPOSURE, CaptureActivity.VALUE_NO_EXPOSURE);
        bundle.putByte(CaptureActivity.KEY_FLASHLIGHT_MODE, CaptureActivity.VALUE_FLASHLIGHT_OFF);
        bundle.putByte(CaptureActivity.KEY_ORIENTATION_MODE, CaptureActivity.VALUE_ORIENTATION_AUTO);
        bundle.putBoolean(CaptureActivity.KEY_SCAN_AREA_FULL_SCREEN, CaptureActivity.VALUE_SCAN_AREA_FULL_SCREEN);
        bundle.putBoolean(CaptureActivity.KEY_NEED_SCAN_HINT_TEXT, CaptureActivity.VALUE_SCAN_HINT_TEXT);
        intent.putExtra(CaptureActivity.EXTRA_SETTING_BUNDLE, bundle);
        startActivityForResult(intent, requestCode);
    }
}