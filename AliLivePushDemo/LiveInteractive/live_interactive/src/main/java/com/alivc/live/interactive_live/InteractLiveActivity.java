package com.alivc.live.interactive_live;

import android.content.Context;
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

import com.alivc.live.baselive_common.AutoScrollMessagesView;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.interactive_common.listener.ConnectionLostListener;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.commonutils.FastClickUtil;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.RoomAndUserInfoView;
import com.aliyunsdk.queen.menu.BeautyMenuPanel;

public class InteractLiveActivity extends AppCompatActivity {

    public static final String DATA_IS_ANCHOR = "data_is_anchor";
    public static final String DATA_HOME_ID = "data_home_id";
    public static final String DATA_USER_ID = "data_user_id";

    private AUILiveDialog mAUILiveDialog;

    //是否是主播端
    private boolean mIsAnchor = true;
    //Dialog 弹窗的意图
    private InteractLiveIntent mCurrentIntent;
    private String mRoomId;
    private String mUserId;
    private TextView mConnectTextView;
    private FrameLayout mUnConnectFrameLayout;
    private ImageView mCloseImageView;
    private ImageView mCameraImageView;
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
    private ImageView mMuteImageView;
    private boolean mIsMute = false;
    private ImageView mSpeakerPhoneImageView;
    private ImageView mBeautyImageView;
    private BeautyMenuPanel mBeautyMenuPanel;
    private ImageView mSeiImageView;
    private AutoScrollMessagesView mSeiView;


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
            mAnchorController = new AnchorController(this, mRoomId, mUserId);
        } else {
            mViewerController = new ViewerController(this, mRoomId, mUserId);
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
        mConnectTextView = findViewById(R.id.tv_connect);
        mUnConnectFrameLayout = findViewById(R.id.fl_un_connect);
        mBigSurfaceView = findViewById(R.id.big_surface_view);
        mCloseImageView = findViewById(R.id.iv_close);
        mCameraImageView = findViewById(R.id.iv_camera);
        mShowConnectIdTextView = findViewById(R.id.tv_show_connect);
        mBigFrameLayout = findViewById(R.id.big_fl);
        mSmallFrameLayout = findViewById(R.id.small_fl);
        mBeautyImageView = findViewById(R.id.iv_beauty);
        mBeautyMenuPanel = findViewById(R.id.beauty_beauty_menuPanel);
        mSeiImageView = findViewById(R.id.iv_sei);
        mSeiView = findViewById(R.id.sei_view);
        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);

        mHomeIdTextView.setText(mRoomId);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mBigSurfaceView.setZOrderOnTop(true);
        mBigSurfaceView.setZOrderMediaOverlay(true);

        mAudienceInfoView = findViewById(R.id.audience_info_view);
        mAnchorInfoView = findViewById(R.id.anchor_info_view);
        mMuteImageView = findViewById(R.id.iv_mute);
        mSpeakerPhoneImageView = findViewById(R.id.iv_speaker_phone);
//        mAudienceInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
//        mAnchorInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void initListener() {
        //美颜
        mBeautyImageView.setOnClickListener(view -> {
            if (LivePushGlobalConfig.ENABLE_BEAUTY) {
                if (mBeautyMenuPanel.isShown()) {
                    mBeautyMenuPanel.setVisibility(View.GONE);
                    mBeautyMenuPanel.onHideMenu();
                } else {
                    mBeautyMenuPanel.setVisibility(View.VISIBLE);
                    mBeautyMenuPanel.onShowMenu();
                }
            }
        });

        //SEI
        mSeiImageView.setOnClickListener(view -> {
            if (mSeiView.isShown()) {
                mSeiView.setVisibility(View.GONE);
            } else {
                mSeiView.setVisibility(View.VISIBLE);
            }
        });
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

        mMuteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsAnchor) {
                    mAnchorController.setMute(!mIsMute);
                }else{
                    mViewerController.setMute(!mIsMute);
                }
                mIsMute = !mIsMute;
                mMuteImageView.setImageResource(mIsMute ? R.drawable.ic_interact_volume_off : R.drawable.ic_interact_volume_on);
            }
        });

        mSpeakerPhoneImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!FastClickUtil.isFastClick()) {
                    Boolean tag = (Boolean) mSpeakerPhoneImageView.getTag();
                    if (tag == null || !tag) {
                        mSpeakerPhoneImageView.setColorFilter(R.color.text_blue);
                        mSpeakerPhoneImageView.setTag(true);
                    } else {
                        mSpeakerPhoneImageView.clearColorFilter();
                        mSpeakerPhoneImageView.setTag(false);
                    }
                    if (mIsAnchor) {
                        mAnchorController.changeSpeakerPhone();
                    } else {
                        mViewerController.changeSpeakerPhone();
                    }
                }
            }
        });

        if (mIsAnchor) {
            mAnchorController.setInteractLivePushPullListener(new InteractLivePushPullListener() {
                @Override
                public void onPullSuccess() {
                    changeSmallSurfaceViewVisible(true);
                    updateConnectTextView(true);
                }

                @Override
                public void onPullError(AlivcLivePlayError errorType, String errorMsg) {
                    super.onPullError(errorType, errorMsg);
                    runOnUiThread(() -> {
                        if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                            changeSmallSurfaceViewVisible(false);
                            mAnchorController.stopConnect();
                            updateConnectTextView(false);
                            ToastUtils.show(getResources().getString(R.string.interact_live_viewer_left));
                        }
                    });
                }

                @Override
                public void onPullStop() {
                    super.onPullStop();
                    runOnUiThread(() -> {
                        changeSmallSurfaceViewVisible(false);
                        updateConnectTextView(false);
                    });
                }

                @Override
                public void onConnectionLost() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectionLostTipsView.show();
                        }
                    });
                }

                @Override
                public void onPlayerSei(int i, byte[] bytes) {
                    mSeiView.appendMessage(new String(bytes));
                }
            });
        } else {
            mViewerController.setInteractLivePushPullListener(new InteractLivePushPullListener() {

                @Override
                public void onPullError(AlivcLivePlayError errorType, String errorMsg) {
                    runOnUiThread(() -> {
                        if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                            finish();
                        }
                    });
                }

                @Override
                public void onPushSuccess() {
                    super.onPushSuccess();
                    runOnUiThread(() -> {
                        updateConnectTextView(true);
                    });
                }

                @Override
                public void onConnectionLost() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConnectionLostTipsView.show();
                        }
                    });
                }

                @Override
                public void onPlayerSei(int i, byte[] bytes) {
                    mSeiView.appendMessage(new String(bytes));
                }
            });
        }

        //开始连麦
        mConnectTextView.setOnClickListener(view -> {
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

        mCameraImageView.setOnClickListener(view -> {
            if (!FastClickUtil.isFastClick()) {
                if (mIsAnchor) {
                    mAnchorController.switchCamera();
                } else {
                    mViewerController.switchCamera();
                }
            }
        });
    }

    private void changeSmallSurfaceViewVisible(boolean isShowSurfaceView) {
        mSmallFrameLayout.setVisibility(isShowSurfaceView ? View.VISIBLE : View.INVISIBLE);
        mUnConnectFrameLayout.setVisibility(isShowSurfaceView ? View.INVISIBLE : View.VISIBLE);
    }

    public void updateConnectTextView(boolean connecting) {
        if (connecting) {
            mShowConnectIdTextView.setVisibility(View.VISIBLE);
            mConnectTextView.setText(getResources().getString(R.string.interact_stop_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_un_connect_btn_bg));
        } else {
            mShowConnectIdTextView.setVisibility(View.GONE);
            mConnectTextView.setText(getResources().getString(R.string.interact_start_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        }
    }

    private void showInteractLiveDialog(String content, boolean showInputView) {
        InteractLiveTipsView interactLiveTipsView = new InteractLiveTipsView(InteractLiveActivity.this);
        interactLiveTipsView.showInputView(showInputView);
        interactLiveTipsView.setContent(content);
        mAUILiveDialog.setContentView(interactLiveTipsView);
        mAUILiveDialog.show();

        interactLiveTipsView.setOnInteractLiveTipsViewListener(new InteractLiveTipsViewListener() {
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
            public void onInputConfirm(String content) {
                hideInputSoftFromWindowMethod(InteractLiveActivity.this, interactLiveTipsView);
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.show(getResources().getString(R.string.interact_live_connect_input_error_tips));
                } else {
                    mAUILiveDialog.dismiss();
                    if (mIsAnchor) {
                        //主播端，输入观众 id 后，开始连麦
                        mAnchorController.startConnect(content);

                        setInfoView(mRoomId, mUserId, content);
                    } else {
                        //观众端，输入主播 id 后，观看直播
                        mViewerController.watchLive(content);

                        setInfoView(mRoomId, content, mUserId);
                    }
                }
            }
        });
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
}