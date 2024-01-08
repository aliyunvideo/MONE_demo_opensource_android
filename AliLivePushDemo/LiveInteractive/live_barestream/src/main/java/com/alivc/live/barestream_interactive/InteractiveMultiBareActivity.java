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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.barestream_interactive.adapter.InteractiveMultiBareRecyclerViewAdapter;
import com.alivc.live.barestream_interactive.bean.BareStreamBean;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.InteractiveCommonInputView;
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.player.annotations.AlivcLivePlayError;

import java.util.ArrayList;
import java.util.List;

public class InteractiveMultiBareActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;
    private static final int REQ_CODE_PUSH_URL = 0x2222;
    private static final int REQ_CODE_PULL_URL = 0x3333;
    public static final String DATA_INTERACTIVE_PUSH_URL = "data_interactive_push_url";
    public static final String DATA_INTERACTIVE_PULL_URL = "data_interactive_pull_url";
    public static final String DATA_INTERACTIVE_PULL_URL_1 = "data_interactive_pull_url_1";
    public static final String DATA_INTERACTIVE_PULL_URL_2 = "data_interactive_pull_url_2";
    public static final String DATA_INTERACTIVE_PULL_URL_3 = "data_interactive_pull_url_3";

    private AUILiveDialog mAUILiveDialog;

    //Dialog 弹窗的意图
    private InteractLiveIntent mCurrentIntent;
    private ImageView mCloseImageView;
    private TextView mShowConnectIdTextView;
    //大窗口
    private FrameLayout mBigFrameLayout;
    //小窗口
    private ConnectionLostTipsView mConnectionLostTipsView;
    private boolean mIsMute = false;

    //输入 URL 方式连麦
    private InteractiveUserData mPushUserData;
    private InteractiveUserData mPullUserData;
    private InteractiveUserData mPullUserData1;
    private InteractiveUserData mPullUserData2;
    private InteractiveUserData mPullUserData3;

    private InteractiveCommonInputView commonInputView;
    private InteractiveSettingView mInteractiveSettingView;
    private MultiBareStreamController mBareStreamController;
    private EditText mInteractivePushUrlEditText;
    private ImageView mPushUrlQrImageView;
    private TextView mStartPushTextView;
    private InteractiveMultiBareRecyclerViewAdapter mAdapter;
    private final List<BareStreamBean> mFakeList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private BareStreamBean mCurrentBareStreamBean;
    private int mCurrentCheckedPosition;
    private boolean mIsPushing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);
        setContentView(R.layout.activity_interactive_multi_bare);

        InteractiveUserData pushUserData = new InteractiveUserData();
        pushUserData.url = getIntent().getStringExtra(DATA_INTERACTIVE_PUSH_URL);
        mPushUserData = pushUserData;

        InteractiveUserData pullUserData = new InteractiveUserData();
        pullUserData.url = getIntent().getStringExtra(DATA_INTERACTIVE_PULL_URL);
        mPullUserData = pullUserData;

        InteractiveUserData pullUserData1 = new InteractiveUserData();
        pullUserData1.url = getIntent().getStringExtra(DATA_INTERACTIVE_PULL_URL_1);
        mPullUserData1 = pullUserData1;

        InteractiveUserData pullUserData2 = new InteractiveUserData();
        pullUserData2.url = getIntent().getStringExtra(DATA_INTERACTIVE_PULL_URL_2);
        mPullUserData2 = pullUserData2;

        InteractiveUserData pullUserData3 = new InteractiveUserData();
        pullUserData3.url = getIntent().getStringExtra(DATA_INTERACTIVE_PULL_URL_3);
        mPullUserData3 = pullUserData3;

        mBareStreamController = new MultiBareStreamController(this);

        initView();
        initListener();
        initData();
    }

    private void initData() {
        mInteractivePushUrlEditText.setText(mPushUserData != null ? mPushUserData.url : "");

        mBareStreamController.setAnchorRenderView(mBigFrameLayout);

        for (int i = 0; i < 4; i++) {
            BareStreamBean bareStreamBean = new BareStreamBean();
            switch (i) {
                case 0:
                    bareStreamBean.setUserData(mPullUserData);
                    break;
                case 1:
                    bareStreamBean.setUserData(mPullUserData1);
                    break;
                case 2:
                    bareStreamBean.setUserData(mPullUserData2);
                    break;
                case 3:
                    bareStreamBean.setUserData(mPullUserData3);
                    break;
            }
            mFakeList.add(bareStreamBean);
        }
        mAdapter.setData(mFakeList);
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mCloseImageView = findViewById(R.id.iv_close);
        mShowConnectIdTextView = findViewById(R.id.tv_show_connect);
        mBigFrameLayout = findViewById(R.id.big_fl);
        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mPushUrlQrImageView = findViewById(R.id.iv_qr_push_url);
        mStartPushTextView = findViewById(R.id.tv_start_push);
        mInteractivePushUrlEditText = findViewById(R.id.et_interactive_push_url);

        mRecyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new InteractiveMultiBareRecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);


    }

    private void initListener() {
        mPushUrlQrImageView.setOnClickListener(view -> {
            startQr(REQ_CODE_PUSH_URL);
        });

        mAdapter.setOnConnectClickListener(position -> {
            mCurrentCheckedPosition = position;
            mCurrentBareStreamBean = mFakeList.get(position);
            if (mCurrentBareStreamBean.isConnected()) {
                mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_finish_tips), false);
            } else {
                if (TextUtils.isEmpty(mCurrentBareStreamBean.getUrl())) {
                    showInteractLiveDialog(getResources().getString(R.string.interact_live_url_connect_tips), true);
                } else {
                    FrameLayout renderViewByPosition = getRenderViewByPosition(position);
                    if (renderViewByPosition != null) {
                        mBareStreamController.startConnect(mCurrentBareStreamBean.getUserData(), renderViewByPosition);
                    }
                }
            }
        });

        mStartPushTextView.setOnClickListener(view -> {
            if (mIsPushing) {
                mBareStreamController.stopPush();
                changePushState(false);
            } else {
                InteractiveUserData userData = new InteractiveUserData();
                userData.url = mInteractivePushUrlEditText.getText().toString().trim();
                mBareStreamController.setPushData(userData);
                mBareStreamController.startPush();
            }
        });

        mConnectionLostTipsView.setConnectionLostListener(() -> runOnUiThread(() -> finish()));

        mBareStreamController.setMultiInteractLivePushPullListener(new InteractLivePushPullListener() {

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
            public void onPullSuccess(InteractiveUserData userData) {
                BareStreamBean bean = mFakeList.get(Integer.parseInt(userData.getKey()));
                if (bean != null) {
                    bean.setConnected(true);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userData, errorType, errorMsg);
                runOnUiThread(() -> {
                    BareStreamBean bean = mFakeList.get(Integer.parseInt(userData.getKey()));
                    if (bean != null) {
                        bean.setConnected(false);
                    }
                    mAdapter.notifyDataSetChanged();
                    mBareStreamController.stopConnect(userData);
                    updateConnectTextView(false);
                    ToastUtils.show(getResources().getString(R.string.interact_live_viewer_left));
                });
            }

            @Override
            public void onPullStop(InteractiveUserData userData) {
                runOnUiThread(() -> {
                    updateConnectTextView(false);
                });
            }

            @Override
            public void onConnectionLost() {
                runOnUiThread(() -> mConnectionLostTipsView.show());
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

    public void updateConnectTextView(boolean connecting) {
        mShowConnectIdTextView.setVisibility(connecting ? View.VISIBLE : View.GONE);
    }

    private void showInteractLiveDialog(String content, boolean showInputView) {
        commonInputView = new InteractiveCommonInputView(InteractiveMultiBareActivity.this);
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
                    mBareStreamController.stopConnect(mCurrentBareStreamBean.getUserData());
                    updateConnectTextView(false);
                    mCurrentBareStreamBean.setConnected(false);
                    mAdapter.notifyDataSetChanged();
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                }
            }

            @Override
            public void onInputConfirm(InteractiveUserData userData) {
                hideInputSoftFromWindowMethod(InteractiveMultiBareActivity.this, commonInputView);
                if (userData == null || TextUtils.isEmpty(userData.url)) {
                    ToastUtils.show(getResources().getString(R.string.interact_live_connect_input_error_tips));
                    return;
                }
                mAUILiveDialog.dismiss();
                //主播端，输入观众 id 后，开始连麦
                mCurrentBareStreamBean.setUserData(userData);
                FrameLayout renderViewByPosition = getRenderViewByPosition(mCurrentCheckedPosition);
                mBareStreamController.startConnect(mCurrentBareStreamBean.getUserData(), renderViewByPosition);
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

    private InteractiveMultiBareRecyclerViewAdapter.InteractiveMultiBareViewHolder getViewHolderByPosition(int position) {
        return (InteractiveMultiBareRecyclerViewAdapter.InteractiveMultiBareViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
    }

    private FrameLayout getRenderViewByPosition(int position) {
        InteractiveMultiBareRecyclerViewAdapter.InteractiveMultiBareViewHolder viewHolder = getViewHolderByPosition(position);
        if (viewHolder != null) {
            return viewHolder.getRenderFrameLayout();
        } else {
            return null;
        }
    }

    private int getAdapterPositionFromByKey(String key) {
        for (int i = 0; i < mFakeList.size(); i++) {
            if (key.equals(mFakeList.get(i).getUrl())) {
                return i;
            }
        }
        return -1;
    }

    private void startQr(int requestCode) {
        if (ContextCompat.checkSelfPermission(InteractiveMultiBareActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(InteractiveMultiBareActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult(requestCode);
        }
    }

    private void startCaptureActivityForResult(int requestCode) {
        Intent intent = new Intent(InteractiveMultiBareActivity.this, CaptureActivity.class);
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