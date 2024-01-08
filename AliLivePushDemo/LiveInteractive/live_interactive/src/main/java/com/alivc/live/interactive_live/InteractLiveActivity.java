package com.alivc.live.interactive_live;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.acker.simplezxing.activity.CaptureActivity;
import com.alivc.live.baselive_common.AutoScrollMessagesView;
import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.ConnectionLostListener;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.InteractiveCommonInputView;
import com.alivc.live.interactive_common.widget.InteractiveConnectView;
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.interactive_common.widget.RoomAndUserInfoView;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.aliyunsdk.queen.menu.QueenBeautyMenu;
import com.aliyunsdk.queen.menu.QueenMenuPanel;

public class InteractLiveActivity extends AppCompatActivity {

    private static final int REQ_CODE_PERMISSION = 0x1111;
    public static final String DATA_IS_ANCHOR = "data_is_anchor";
    public static final String DATA_HOME_ID = "data_home_id";
    public static final String DATA_USER_ID = "data_user_id";
    public static final String DATA_IS_URL = "data_is_url";
    public static final String DATA_INTERACTIVE_URL = "data_interactive_url";

    private AUILiveDialog mAUILiveDialog;

    //是否是主播端
    private boolean mIsAnchor = true;
    //Dialog 弹窗的意图
    private InteractLiveIntent mCurrentIntent;
    private String mRoomId;
    private String mAnchorId;
    private String mUserId;
    private ImageView mCloseImageView;
    private TextView mShowConnectIdTextView;
    //大窗口
    private FrameLayout mBigFrameLayout;
    //小窗口
    private FrameLayout mSmallFrameLayout;
    private SurfaceView mBigSurfaceView;
    private AnchorController mAnchorController;
    private ViewerController mViewerController;
    private ConnectionLostTipsView mConnectionLostTipsView;
    private RoomAndUserInfoView mAnchorInfoView;
    private RoomAndUserInfoView mAudienceInfoView;
    private boolean mIsMute = false;
    //输入 URL 方式连麦
    private String mInteractiveURL;
    private InteractiveCommonInputView commonInputView;
    private InteractiveConnectView mInteractiveConnectView;
    private InteractiveSettingView mInteractiveSettingView;
    private ImageView mBeautyImageView;
    private ImageView mMuteLocalCameraImageView;

    // 美颜menu
    private QueenMenuPanel mBeautyMenuPanel;
    private QueenBeautyMenu mQueenBeautyMenu;

    private ImageView mSeiImageView;
    private AutoScrollMessagesView mSeiMessageView;
    private boolean mMuteLocalCamera = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);

        setContentView(R.layout.activity_interact_live);

        mIsAnchor = getIntent().getBooleanExtra(DATA_IS_ANCHOR, true);
        mRoomId = getIntent().getStringExtra(DATA_HOME_ID);
        mUserId = getIntent().getStringExtra(DATA_USER_ID);
        if (mIsAnchor) {
            mAnchorId = mUserId;
        }
        mInteractiveURL = getIntent().getStringExtra(DATA_INTERACTIVE_URL);

        if (mIsAnchor) {
            mAnchorController = new AnchorController(this, mRoomId, mUserId);
        } else {
            InteractiveUserData userData = new InteractiveUserData();
            userData.channelId = mRoomId;
            userData.userId = mAnchorId;

            InteractiveUserData viewerUserData = new InteractiveUserData();
            viewerUserData.channelId = mRoomId;
            viewerUserData.userId = mUserId;
            mViewerController = new ViewerController(this, userData, viewerUserData);
        }

        initView();
        initListener();
        initData();
    }

    private void initData() {
        if (mIsAnchor) {
            mAnchorController.setAnchorRenderView(mBigFrameLayout);
            mAnchorController.setViewerRenderView(mSmallFrameLayout);
            mAnchorController.startPush();
            mAnchorInfoView.setUserInfo(mRoomId, mUserId);
        } else {
            mViewerController.setAnchorRenderView(mBigFrameLayout);
            mViewerController.setViewerRenderView(mSmallFrameLayout);
            mViewerController.setAnchorCDNRenderView(mBigSurfaceView);
            changeConnectRenderView(false);
            showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_author_tips), true);
        }
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mInteractiveConnectView = findViewById(R.id.connect_view);
        mBigSurfaceView = findViewById(R.id.big_surface_view);
        mCloseImageView = findViewById(R.id.iv_close);
        mShowConnectIdTextView = findViewById(R.id.tv_show_connect);
        mBigFrameLayout = findViewById(R.id.big_fl);
        mSmallFrameLayout = findViewById(R.id.small_fl);
        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);
        mBeautyImageView = findViewById(R.id.iv_beauty);
        mMuteLocalCameraImageView = findViewById(R.id.iv_mute_local_camera);
        mSeiMessageView = findViewById(R.id.sei_receive_view);

        mBeautyMenuPanel = QueenBeautyMenu.getPanel(this);
        mBeautyMenuPanel.onHideMenu();
        mBeautyMenuPanel.onHideValidFeatures();
        mBeautyMenuPanel.onHideCopyright();

        mQueenBeautyMenu = findViewById(R.id.beauty_beauty_menuPanel);
        mQueenBeautyMenu.addView(mBeautyMenuPanel);

//        mSeiImageView = findViewById(R.id.iv_sei);
//        mSeiView = findViewById(R.id.sei_view);
        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);

        mHomeIdTextView.setText(mRoomId);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mBigSurfaceView.setZOrderOnTop(true);
        mBigSurfaceView.setZOrderMediaOverlay(true);

        mAudienceInfoView = findViewById(R.id.audience_info_view);
        mAnchorInfoView = findViewById(R.id.anchor_info_view);
        mAudienceInfoView.setVisibility(View.VISIBLE);
        mAnchorInfoView.setVisibility(View.VISIBLE);
    }

    private void initListener() {
        //美颜
        mBeautyImageView.setOnClickListener(view -> {
            if (LivePushGlobalConfig.ENABLE_BEAUTY) {
                if (mQueenBeautyMenu.getVisibility() == View.VISIBLE) {
                    mQueenBeautyMenu.setVisibility(View.GONE);
                    mBeautyMenuPanel.onHideMenu();
                } else {
                    mQueenBeautyMenu.setVisibility(View.VISIBLE);
                    mBeautyMenuPanel.onShowMenu();
                }
            }
        });

        mMuteLocalCameraImageView.setOnClickListener(view -> {
            if (!FastClickUtil.isFastClick()) {
                mMuteLocalCamera = !mMuteLocalCamera;
                if (mIsAnchor) {
                    mAnchorController.muteLocalCamera(mMuteLocalCamera);
                } else {
                    mViewerController.muteLocalCamera(mMuteLocalCamera);
                }
            }
        });

        //SEI
//        mSeiImageView.setOnClickListener(view -> {
//            if (mSeiView.isShown()) {
//                mSeiView.setVisibility(View.GONE);
//            } else {
//                mSeiView.setVisibility(View.VISIBLE);
//            }
//        });
        mConnectionLostTipsView.setConnectionLostListener(new ConnectionLostListener() {
            @Override
            public void onConfirm() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                });
            }
        });

        if (mIsAnchor) {
            mAnchorController.setInteractLivePushPullListener(new InteractLivePushPullListener() {
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
                        mAnchorController.stopConnect();
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
                }

                @Override
                public void onPushError() {
                    super.onPushError();
                }

                @Override
                public void onConnectionLost() {
                    super.onConnectionLost();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectionLostTipsView.show();
                        }
                    });
                }

                @Override
                public void onReceiveSEIMessage(int payload, byte[] data) {
                    super.onReceiveSEIMessage(payload, data);
                }

                @Override
                public void onPlayerSei(int i, byte[] bytes) {
                    super.onPlayerSei(i, bytes);
//                    mSeiMessageView.appendMessage(new String(bytes));
                }

                @Override
                public void onReceiveSEIDelay(String src, String type, String msg) {
                    super.onReceiveSEIDelay(src, type, msg);
                    mSeiMessageView.appendMessage("[" + src + "][" + type + "][" + msg + "ms]");
                }
            });
        } else {
            mViewerController.setInteractLivePushPullListener(new InteractLivePushPullListener() {
                @Override
                public void onPullSuccess(InteractiveUserData userData) {
                    super.onPullSuccess(userData);
                }

                @Override
                public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                    super.onPullError(userData, errorType, errorMsg);
                    runOnUiThread(() -> {
                        if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                            finish();
                        }
                    });
                }

                @Override
                public void onPullStop(InteractiveUserData userData) {
                    super.onPullStop(userData);
                }

                @Override
                public void onPushSuccess() {
                    super.onPushSuccess();
                    if (mViewerController != null) {
                        mViewerController.pullOtherStream();
                    }
                    runOnUiThread(() -> {
                        updateConnectTextView(true);
                    });
                }

                @Override
                public void onPushError() {
                    super.onPushError();
                }

                @Override
                public void onConnectionLost() {
                    super.onConnectionLost();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectionLostTipsView.show();
                        }
                    });
                }

                @Override
                public void onReceiveSEIMessage(int payload, byte[] data) {
                    super.onReceiveSEIMessage(payload, data);
                }

                @Override
                public void onPlayerSei(int i, byte[] bytes) {
                    super.onPlayerSei(i, bytes);
                }

                @Override
                public void onReceiveSEIDelay(String src, String type, String msg) {
                    super.onReceiveSEIDelay(src, type, msg);
                    mSeiMessageView.appendMessage("[" + src + "][" + type + "][" + msg + "ms]");
                }
            });
        }

        //开始连麦
        mInteractiveConnectView.setConnectClickListener(() -> {
            if (mIsAnchor) {
                if (mAnchorController.isOnConnected()) {
                    //主播端停止连麦
                    mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                    showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_finish_tips), false);
                } else {
                    //主播端开始连麦，输入用户 id
                    showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_tips), true);
                }
            } else {
                if (mViewerController.isPushing()) {
                    //观众端停止连麦
                    mCurrentIntent = InteractLiveIntent.INTENT_STOP_PUSH;
                    showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_finish_tips), false);
                } else {
                    //观众端开始连麦
                    if (mViewerController.hasAnchorId()) {
                        changeConnectRenderView(true);
                        mViewerController.startConnect();
                        changeSmallSurfaceViewVisible(true);
                    } else {
                        showInteractLiveDialog(getResources().getString(R.string.interact_live_connect_author_tips), true);
                    }
                }
            }
        });

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mInteractiveSettingView.setOnInteractiveSettingListener(new InteractiveSettingView.OnInteractiveSettingListener() {
            @Override
            public void onSwitchCameraClick() {
                if (mIsAnchor) {
                    mAnchorController.switchCamera();
                } else {
                    mViewerController.switchCamera();
                }
            }

            @Override
            public void onMuteClick() {
                if (mIsAnchor) {
                    mAnchorController.setMute(!mIsMute);
                } else {
                    mViewerController.setMute(!mIsMute);
                }
                mIsMute = !mIsMute;
                mInteractiveSettingView.changeMute(mIsMute);
            }

            @Override
            public void onSpeakerPhoneClick() {
                if (mIsAnchor) {
                    mAnchorController.changeSpeakerPhone();
                } else {
                    mViewerController.changeSpeakerPhone();
                }
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
            mInteractiveConnectView.connected();
        } else {
            mShowConnectIdTextView.setVisibility(View.GONE);
            mInteractiveConnectView.unConnected();
        }
    }

    private void showInteractLiveDialog(String content, boolean showInputView, boolean showQR) {
        commonInputView = new InteractiveCommonInputView(InteractLiveActivity.this);
        commonInputView.setViewType(InteractiveCommonInputView.ViewType.INTERACTIVE);
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
                if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PULL && mIsAnchor) {
                    //主播结束连麦
                    mAUILiveDialog.dismiss();
                    mAnchorController.stopConnect();
                    updateConnectTextView(false);
                    changeSmallSurfaceViewVisible(false);
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PUSH && !mIsAnchor) {
                    //观众结束连麦
                    mAUILiveDialog.dismiss();
                    mViewerController.stopConnect();
                    changeConnectRenderView(false);
                    updateConnectTextView(false);
                    changeSmallSurfaceViewVisible(false);
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                }
            }

            @Override
            public void onInputConfirm(InteractiveUserData userData) {
                hideInputSoftFromWindowMethod(InteractLiveActivity.this, commonInputView);
                if (TextUtils.isEmpty(userData.userId)) {
                    ToastUtils.show(getResources().getString(R.string.interact_live_connect_input_error_tips));
                    return;
                }
                userData.channelId = mRoomId;
                mAUILiveDialog.dismiss();
                if (mIsAnchor) {
                    //主播端，输入观众 id 后，开始连麦
                    mAnchorController.startConnect(userData);
                } else {
                    mAnchorId = userData.userId;
                    //观众端，输入主播 id 后，观看直播
                    mViewerController.watchLive(mAnchorId);
                    setInfoView(mRoomId, mAnchorId, mUserId);
                }
            }

            @Override
            public void onQrClick() {
                startQr();
            }
        });
    }

    private void showInteractLiveDialog(String content, boolean showInputView) {
        showInteractLiveDialog(content, showInputView, false);
    }

    private void changeConnectRenderView(boolean connect) {
        mBigFrameLayout.setVisibility(connect ? View.VISIBLE : View.GONE);
        mBigSurfaceView.setVisibility(connect ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mIsAnchor) {
            mAnchorController.resume();
        } else {
            mViewerController.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsAnchor) {
            mAnchorController.pause();
        } else {
            mViewerController.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsAnchor) {
            mAnchorController.release();
        } else {
            mViewerController.release();
        }
    }

    public void hideInputSoftFromWindowMethod(Context context, View view) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInfoView(String roomId, String anchorId, String audienceId) {
        mAudienceInfoView.setUserInfo(roomId, audienceId);
        mAnchorInfoView.setUserInfo(roomId, anchorId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        if (commonInputView != null) {
                            commonInputView.setQrResult(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
                        }
                        break;
                    case RESULT_CANCELED:
                        if (data != null && commonInputView != null) {
                            // for some reason camera is not working correctly
                            commonInputView.setQrResult(data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT));
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

    private void startQr() {
        if (ContextCompat.checkSelfPermission(InteractLiveActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Do not have the permission of camera, request it.
            ActivityCompat.requestPermissions(InteractLiveActivity.this, new String[]{Manifest.permission.CAMERA}, REQ_CODE_PERMISSION);
        } else {
            // Have gotten the permission
            startCaptureActivityForResult();
        }
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(InteractLiveActivity.this, CaptureActivity.class);
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