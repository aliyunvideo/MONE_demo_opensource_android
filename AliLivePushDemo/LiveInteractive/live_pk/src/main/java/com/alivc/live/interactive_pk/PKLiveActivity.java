package com.alivc.live.interactive_pk;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.baselive_common.AutoScrollMessagesView;
import com.alivc.live.baselive_common.LivePusherSEIView;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.interactive_common.listener.ConnectionLostListener;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.RoomAndUserInfoView;
import com.aliyunsdk.queen.menu.BeautyMenuPanel;

/**
 * PK 互动界面
 */
public class PKLiveActivity extends AppCompatActivity {

    private PKController mPKController;
    private AUILiveDialog mAUILiveDialog;
    private InteractLiveIntent mCurrentIntent;
    private String mRoomId;
    private String mUserId;
    private ImageView mCloseImageView;
    private TextView mConnectTextView;
    private TextView mShowConnectTextView;
    private FrameLayout mOwnerFrameLayout;
    private FrameLayout mOtherFrameLayout;
    private FrameLayout mUnConnectFrameLayout;
    private ConnectionLostTipsView mConnectionLostTipsView;
    private RoomAndUserInfoView mOwnerInfoView;
    private RoomAndUserInfoView mOtherInfoView;
    private boolean mIsMute = false;
    private InteractiveSettingView mInteractiveSettingView;
    private LivePusherSEIView mSeiView;
    private AutoScrollMessagesView mSeiMessageView;
    private ImageView mBeautyImageView;
    private BeautyMenuPanel mBeautyMenuPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_pklive);

        mRoomId = getIntent().getStringExtra(PKLiveInputActivity.DATA_HOME_ID);
        mUserId = getIntent().getStringExtra(PKLiveInputActivity.DATA_USER_ID);
        mPKController = new PKController(this, mRoomId, mUserId);

        initView();
        initListener();
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mCloseImageView = findViewById(R.id.iv_close);
        mConnectTextView = findViewById(R.id.tv_connect);
        mOwnerFrameLayout = findViewById(R.id.frame_owner);
        mOtherFrameLayout = findViewById(R.id.frame_other);
        mUnConnectFrameLayout = findViewById(R.id.fl_un_connect);
        mShowConnectTextView = findViewById(R.id.tv_show_connect);
        mSeiView = findViewById(R.id.sei_view);
        mSeiMessageView = findViewById(R.id.sei_receive_view);

        mBeautyImageView = findViewById(R.id.iv_beauty);
        mBeautyMenuPanel = findViewById(R.id.beauty_beauty_menuPanel);

        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);
        mHomeIdTextView.setText(mRoomId);

        mOwnerInfoView = findViewById(R.id.owner_info_view);
        mOtherInfoView = findViewById(R.id.other_info_view);
        mOtherInfoView.enableMute(true);
        mOtherInfoView.initListener(new RoomAndUserInfoView.OnClickEventListener() {
            @Override
            public void onClickInteractMute(boolean mute) {
                mPKController.mutePKMixStream(mute);
            }
        });
        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);
//        mOwnerInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
//        mOtherInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mPKController.startPush(mOwnerFrameLayout);
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

        mSeiView.setSendSeiViewListener(text -> {
            mPKController.sendSEI(text);
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

        mInteractiveSettingView.setOnInteractiveSettingListener(new InteractiveSettingView.OnInteractiveSettingListener() {
            @Override
            public void onSwitchCameraClick() {
                mPKController.switchCamera();
            }

            @Override
            public void onMuteClick() {
                mPKController.setMute(!mIsMute);
                mIsMute = !mIsMute;
                mInteractiveSettingView.changeMute(mIsMute);
            }

            @Override
            public void onSpeakerPhoneClick() {
                mPKController.changeSpeakerPhone();
            }
        });

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        //开始 PK
        mConnectTextView.setOnClickListener(view -> {
            if (mPKController.isPKing()) {
                mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                showInteractLiveDialog(getResources().getString(R.string.pk_live_connect_finish_tips), false);
            } else {
                showInteractLiveDialog(null, true);
            }
        });

        mPKController.setPKLivePushPullListener(new InteractLivePushPullListener() {
            @Override
            public void onPullSuccess() {
                mPKController.setPKLiveMixTranscoding(true);
                changeFrameLayoutViewVisible(true);
                updateConnectTextView(true);
            }

            @Override
            public void onPullError(AlivcLivePlayError errorType, String errorMsg) {
            }

            @Override
            public void onPullStop() {
                mPKController.setPKLiveMixTranscoding(false);
                changeFrameLayoutViewVisible(false);
                updateConnectTextView(false);
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
            public void onReceiveSEIMessage(int payload, byte[] data) {
                mSeiMessageView.appendMessage(new String(data));
            }

            @Override
            public void onPushSuccess() {
            }

            @Override
            public void onPushError() {
            }
        });
    }

    private void showInteractLiveDialog(String content, boolean showInputView) {
        PKLiveTipsView pkLiveTipsView = new PKLiveTipsView(PKLiveActivity.this);
        pkLiveTipsView.showInputView(showInputView);
        pkLiveTipsView.setContent(content);
        mAUILiveDialog.setContentView(pkLiveTipsView);
        mAUILiveDialog.show();

        pkLiveTipsView.setOnInteractLiveTipsViewListener(new InteractLiveTipsViewListener() {
            @Override
            public void onCancel() {
                if (mAUILiveDialog.isShowing()) {
                    mAUILiveDialog.dismiss();
                }
            }

            @Override
            public void onConfirm() {
                //退出直播
                if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PULL) {
                    mAUILiveDialog.dismiss();
                    mPKController.stopPK();
                    updateConnectTextView(false);
                    changeFrameLayoutViewVisible(false);
                }
            }

            @Override
            public void onInputConfirm(String content) {
                mAUILiveDialog.dismiss();
                if (content.contains("=")) {
                    String[] split = content.split("=");
                    mPKController.setPKOtherInfo(split[0], split[1]);
                    mPKController.startPK(mOtherFrameLayout);

                    setInfoView(mRoomId, split[0], mUserId, split[1]);
                }
            }
        });
    }

    public void updateConnectTextView(boolean connecting) {
        if (connecting) {
            mShowConnectTextView.setVisibility(View.VISIBLE);
            mConnectTextView.setText(getResources().getString(R.string.pk_stop_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_un_connect_btn_bg));
        } else {
            mShowConnectTextView.setVisibility(View.GONE);
            mConnectTextView.setText(getResources().getString(R.string.pk_start_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
        }
    }

    private void changeFrameLayoutViewVisible(boolean isShowSurfaceView) {
        mOtherFrameLayout.setVisibility(isShowSurfaceView ? View.VISIBLE : View.INVISIBLE);
        mUnConnectFrameLayout.setVisibility(isShowSurfaceView ? View.INVISIBLE : View.VISIBLE);
    }

    private void setInfoView(String ownerRoomId, String otherRoomId, String ownerId, String otherId) {
        mOwnerInfoView.setUserInfo(ownerRoomId, ownerId);
        mOtherInfoView.setUserInfo(otherRoomId, otherId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPKController.release();
    }
}