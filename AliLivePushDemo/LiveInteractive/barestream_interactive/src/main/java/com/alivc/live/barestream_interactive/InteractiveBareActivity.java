package com.alivc.live.barestream_interactive;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.InteractiveCommonInputView;
import com.alivc.live.interactive_common.widget.InteractiveConnectView;
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.player.annotations.AlivcLivePlayError;

public class InteractiveBareActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int REQ_CODE_PUSH_URL = 0x2222;
    private static final int REQ_CODE_PULL_URL = 0x3333;
    public static final String DATA_INTERACTIVE_PUSH_URL = "data_interactive_push_url";
    public static final String DATA_INTERACTIVE_PULL_URL = "data_interactive_pull_url";

    private AUILiveDialog mAUILiveDialog;

    //Dialog 弹窗的意图
    private InteractLiveIntent mCurrentIntent;
    private ImageView mCloseImageView;
    private TextView mShowConnectIdTextView;
    //大窗口
    private FrameLayout mBigFrameLayout;
    //小窗口
    private FrameLayout mSmallFrameLayout;
    private ConnectionLostTipsView mConnectionLostTipsView;
    private boolean mIsMute = false;

    //输入 URL 方式连麦
    private InteractiveUserData mPushUserData;
    private InteractiveUserData mPullUserData;

    private InteractiveCommonInputView commonInputView;
    private InteractiveConnectView mInteractiveConnectView;
    private InteractiveSettingView mInteractiveSettingView;
    private BareStreamController mBareStreamController;
    private EditText mInteractivePushUrlEditText;
    private ImageView mPushUrlQrImageView;
    private TextView mStartPushTextView;
    private boolean mIsPushing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);
        setContentView(R.layout.activity_interactive_bare);

        InteractiveUserData pushUserData = new InteractiveUserData();
        pushUserData.url = getIntent().getStringExtra(DATA_INTERACTIVE_PUSH_URL);
        mPushUserData = pushUserData;

        InteractiveUserData pullUserData = new InteractiveUserData();
        pullUserData.url = getIntent().getStringExtra(DATA_INTERACTIVE_PULL_URL);
        mPullUserData = pullUserData;

        mBareStreamController = new BareStreamController(this);

        initView();
        initListener();
        initData();
    }

    private void initData() {
        mInteractivePushUrlEditText.setText(mPushUserData != null ? mPushUserData.url : "");

        mBareStreamController.setAnchorRenderView(mBigFrameLayout);
        mBareStreamController.setViewerRenderView(mSmallFrameLayout);
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mInteractiveConnectView = findViewById(R.id.connect_view);
        mCloseImageView = findViewById(R.id.iv_close);
        mShowConnectIdTextView = findViewById(R.id.tv_show_connect);
        mBigFrameLayout = findViewById(R.id.big_fl);
        mSmallFrameLayout = findViewById(R.id.small_fl);
        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mPushUrlQrImageView = findViewById(R.id.iv_qr_push_url);
        mStartPushTextView = findViewById(R.id.tv_start_push);
        mInteractivePushUrlEditText = findViewById(R.id.et_interactive_push_url);

        mInteractiveConnectView.setDefaultText(getResources().getString(R.string.interactive_bare_stream_start_pull));

    }

    private void initListener() {
        mPushUrlQrImageView.setOnClickListener(view -> {
            startQr(REQ_CODE_PUSH_URL);
        });

        mStartPushTextView.setOnClickListener(view -> {
            if (mIsPushing) {
                mBareStreamController.stopPush();
                changePushState(false);
            } else {
                mPushUserData.url = mInteractivePushUrlEditText.getText().toString();
                mBareStreamController.startPush(mPushUserData);
            }
        });

        mConnectionLostTipsView.setConnectionLostListener(() -> runOnUiThread(() -> finish()));

        mBareStreamController.setInteractLivePushPullListener(new InteractLivePushPullListener() {
            @Override
            public void onPullSuccess(InteractiveUserData userData) {
                super.onPullSuccess(userData);
                changeSmallSurfaceViewVisible(true);
                updateConnectTextView(true);
            }

            @Override
            public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userData, errorType, errorMsg);
                runOnUiThread(() -> {
                    changeSmallSurfaceViewVisible(false);
                    mBareStreamController.stopPull();
                    updateConnectTextView(false);
                    ToastUtils.show(getResources().getString(R.string.interact_live_viewer_left));
                });
            }

            @Override
            public void onPullStop(InteractiveUserData userData) {
                super.onPullStop(userData);
                runOnUiThread(() -> {
                    changeSmallSurfaceViewVisible(false);
                    updateConnectTextView(false);
                });
            }

            @Override
            public void onPushSuccess() {
                super.onPushSuccess();
                changePushState(true);
            }

            @Override
            public void onPushError() {
                super.onPushError();
                changePushState(false);
            }

            @Override
            public void onConnectionLost() {
                super.onConnectionLost();
                runOnUiThread(() -> mConnectionLostTipsView.show());
            }

            @Override
            public void onReceiveSEIMessage(int payload, byte[] data) {
                super.onReceiveSEIMessage(payload, data);
            }

            @Override
            public void onPlayerSei(int i, byte[] bytes) {
                super.onPlayerSei(i, bytes);
            }
        });

        //开始连麦
        mInteractiveConnectView.setConnectClickListener(() -> {
            if (mBareStreamController.isPulling()) {
                //主播端停止连麦
                mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_finish_tips), false);
            } else {
                //主播端开始连麦，输入用户 id
                if (mPullUserData == null || TextUtils.isEmpty(mPullUserData.url)) {
                    showInteractLiveDialog(getResources().getString(R.string.interact_live_url_connect_tips), true);
                    return;
                }
                mBareStreamController.startPull(mPullUserData);
            }
        });

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mInteractiveSettingView.setOnInteractiveSettingListener(new InteractiveSettingView.OnInteractiveSettingListener() {
            @Override
            public void onSwitchCameraClick() {
                mBareStreamController.switchCamera();
            }

            @Override
            public void onMuteClick() {
                mBareStreamController.setMute(!mIsMute);
                mIsMute = !mIsMute;
                mInteractiveSettingView.changeMute(mIsMute);
            }

            @Override
            public void onSpeakerPhoneClick() {
                mBareStreamController.changeSpeakerPhone();
            }

            @Override
            public void onEnableAudioClick(boolean enable) {

            }

            @Override
            public void onEnableVideoClick(boolean enable) {

            }
        });
    }

    private void changeSmallSurfaceViewVisible(boolean isShowSurfaceView) {
        mSmallFrameLayout.setVisibility(isShowSurfaceView ? View.VISIBLE : View.INVISIBLE);
        mInteractiveConnectView.isShow(isShowSurfaceView);
    }

    public void updateConnectTextView(boolean connecting) {
        if (connecting) {
            mShowConnectIdTextView.setVisibility(View.VISIBLE);
            mInteractiveConnectView.connected(getResources().getString(R.string.interactive_bare_stream_stop_pull));
        } else {
            mShowConnectIdTextView.setVisibility(View.GONE);
            mInteractiveConnectView.unConnected(getResources().getString(R.string.interactive_bare_stream_start_pull));
        }
    }

    private void showInteractLiveDialog(String content, boolean showInputView) {
        commonInputView = new InteractiveCommonInputView(InteractiveBareActivity.this);
        commonInputView.setViewType(InteractiveCommonInputView.ViewType.BARE_STREAM);
        commonInputView.showInputView(content, showInputView);
        mAUILiveDialog.setContentView(commonInputView);
        mAUILiveDialog.show();

        commonInputView.setOnInteractLiveTipsViewListener(new InteractLiveTipsViewListener() {
            @Override
            public void onCancel() {
                if (mAUILiveDialog.isShowing()) {
                    mAUILiveDialog.dismiss();
                }
            }

            @Override
            public void onConfirm() {
                if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PULL) {
                    //主播结束连麦
                    mAUILiveDialog.dismiss();
                    mBareStreamController.stopPull();
                    updateConnectTextView(false);
                    changeSmallSurfaceViewVisible(false);
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                }
            }

            @Override
            public void onInputConfirm(InteractiveUserData userData) {
                hideInputSoftFromWindowMethod(InteractiveBareActivity.this, commonInputView);
                if (userData == null || TextUtils.isEmpty(userData.url)) {
                    ToastUtils.show(getResources().getString(R.string.interact_live_connect_input_error_tips));
                    return;
                }
                mAUILiveDialog.dismiss();
                // 主播端，输入观众 id 后，开始连麦
                mPullUserData = userData;
                mBareStreamController.startPull(userData);
            }

            @Override
            public void onQrClick() {
                startQr(REQ_CODE_PULL_URL);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBareStreamController.release();
    }

    public void hideInputSoftFromWindowMethod(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_PUSH_URL:
            case REQ_CODE_PULL_URL:
                switch (resultCode) {
                    case RESULT_OK:
                        if (requestCode == REQ_CODE_PUSH_URL) {
                            if (mInteractivePushUrlEditText != null) {
                                mInteractivePushUrlEditText.setText(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                        } else {
                            if (commonInputView != null) {
                                commonInputView.setQrResult(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                            }
                        }

                        break;
                    case RESULT_CANCELED:
                        if (data != null) {
                            // for some reason camera is not working correctly
                            ToastUtils.show(getString(R.string.live_push_switch_to_facing_back_camera));
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

    private void changePushState(boolean isSuccess) {
        this.mIsPushing = isSuccess;
        if (isSuccess) {
            mStartPushTextView.setText(getResources().getString(com.alivc.live.interactive_common.R.string.interact_stop_push));
            mStartPushTextView.setBackground(getResources().getDrawable(com.alivc.live.interactive_common.R.drawable.shape_interact_live_un_connect_btn_bg));
        } else {
            mStartPushTextView.setText(getResources().getString(com.alivc.live.interactive_common.R.string.interact_start_push));
            mStartPushTextView.setBackground(getResources().getDrawable(com.alivc.live.interactive_common.R.drawable.shape_pysh_btn_bg));
        }
    }

    private void startQr(int requestCode) {
        if (ContextCompat.checkSelfPermission(InteractiveBareActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(InteractiveBareActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult(requestCode);
        }
    }

    private void startCaptureActivityForResult(int requestCode) {
        Intent intent = new Intent(InteractiveBareActivity.this, CaptureActivity.class);
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