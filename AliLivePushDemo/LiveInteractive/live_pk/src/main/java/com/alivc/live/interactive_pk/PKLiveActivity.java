package com.alivc.live.interactive_pk;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.annotations.AlivcLiveRecordAudioQuality;
import com.alivc.live.annotations.AlivcLiveRecordMediaFormat;
import com.alivc.live.annotations.AlivcLiveRecordStreamType;
import com.alivc.live.baselive_common.AutoScrollMessagesView;
import com.alivc.live.baselive_common.LivePushLocalRecordView;
import com.alivc.live.baselive_common.LivePusherSEIView;
import com.alivc.live.commonbiz.ResourcesConst;
import com.alivc.live.commonutils.FileUtil;
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
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.interactive_common.widget.RoomAndUserInfoView;
import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.pusher.AlivcLiveLocalRecordConfig;
import com.aliyunsdk.queen.menu.QueenBeautyMenu;
import com.aliyunsdk.queen.menu.QueenMenuPanel;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    // 美颜menu
    private QueenBeautyMenu mQueenBeautyMenu;
    private QueenMenuPanel mBeautyMenuPanel;
    private LivePushLocalRecordView mLivePushLocalRecordView;

    private Button mExternalAudioStreamBtn;
    private Button mExternalVideoStreamBtn;
    private boolean mIsExternalAudioStreamSelected = false;
    private boolean mIsExternalVideoStreamSelected = false;

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

        mBeautyMenuPanel = QueenBeautyMenu.getPanel(this);
        mBeautyMenuPanel.onHideMenu();
        mBeautyMenuPanel.onHideValidFeatures();
        mBeautyMenuPanel.onHideCopyright();

        mQueenBeautyMenu = findViewById(R.id.beauty_beauty_menuPanel);
        mQueenBeautyMenu.addView(mBeautyMenuPanel);

        mLivePushLocalRecordView = findViewById(R.id.local_record_v);

        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);
        mHomeIdTextView.setText(mRoomId);

        mOwnerInfoView = findViewById(R.id.owner_info_view);
        mOtherInfoView = findViewById(R.id.other_info_view);
        mOwnerInfoView.setVisibility(View.VISIBLE);
        mOtherInfoView.setVisibility(View.VISIBLE);
        mOtherInfoView.enableMute(true);
        mOtherInfoView.initListener(new RoomAndUserInfoView.OnClickEventListener() {
            @Override
            public void onClickInteractMute(boolean mute) {
                mPKController.mutePKMixStream(mute);
            }
        });
        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mExternalAudioStreamBtn = findViewById(R.id.btn_external_audio_stream);
        mExternalVideoStreamBtn = findViewById(R.id.btn_external_video_stream);

        mPKController.startPush(mOwnerFrameLayout);
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

            @Override
            public void onEnableAudioClick(boolean enable) {
                mPKController.enableAudioCapture(enable);
            }

            @Override
            public void onEnableVideoClick(boolean enable) {
                mPKController.enableLocalCamera(enable);
            }
        });

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mExternalAudioStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 输入外部音频流数据
                File pcmFile = ResourcesConst.localCapturePCMFilePath(getBaseContext());
                if (!pcmFile.exists()) {
                    ToastUtils.show("Please ensure that the local pcm files have been downloaded!");
                    return;
                }
                if (mIsExternalAudioStreamSelected) {
                    mPKController.stopExternalAudioStream();
                } else {
                    mPKController.startExternalAudioStream();
                }
                mIsExternalAudioStreamSelected = !mIsExternalAudioStreamSelected;
            }
        });
        mExternalVideoStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File yuvFile = ResourcesConst.localCaptureYUVFilePath(getBaseContext());
                if (!yuvFile.exists()) {
                    ToastUtils.show("Please ensure that the local yuv files have been downloaded!");
                    return;
                }
                if (mIsExternalVideoStreamSelected) {
                    mPKController.stopExternalVideoStream();
                } else {
                    mPKController.startExternalVideoStream();
                }
                mIsExternalVideoStreamSelected = !mIsExternalVideoStreamSelected;
            }
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

        mLivePushLocalRecordView.initLocalRecordEventListener(new LivePushLocalRecordView.LocalRecordEventListener() {
            @Override
            public void onStartLocalRecord() {
                if (mPKController != null) {
                    AlivcLiveLocalRecordConfig recordConfig = new AlivcLiveLocalRecordConfig();
                    String dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
                    recordConfig.storagePath = FileUtil.getExternalCacheFolder(getBaseContext()) + "/" + dateFormat + ".mp4";
                    recordConfig.streamType = AlivcLiveRecordStreamType.AUDIO_VIDEO;
                    recordConfig.audioQuality = AlivcLiveRecordAudioQuality.MEDIUM;
                    recordConfig.mediaFormat = AlivcLiveRecordMediaFormat.MP4;
                    mPKController.startLocalRecord(recordConfig);
                }
            }

            @Override
            public void onStopLocalRecord() {
                if (mPKController != null) {
                    mPKController.stopLocalRecord();
                }
            }
        });

        mPKController.setPKLivePushPullListener(new InteractLivePushPullListener() {
            @Override
            public void onPullSuccess(InteractiveUserData userData) {
                super.onPullSuccess(userData);
                mPKController.setPKLiveMixTranscoding(true);
                changeFrameLayoutViewVisible(true);
                updateConnectTextView(true);
            }

            @Override
            public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userData, errorType, errorMsg);
            }

            @Override
            public void onPullStop(InteractiveUserData userData) {
                super.onPullStop(userData);
                mPKController.setPKLiveMixTranscoding(false);
                changeFrameLayoutViewVisible(false);
                updateConnectTextView(false);
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

                // 断网后停止本地录制
                mPKController.stopLocalRecord();

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
                mSeiMessageView.appendMessage(new String(data));
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

    private void showInteractLiveDialog(String content, boolean showInputView) {
        InteractiveCommonInputView interactiveCommonInputView = new InteractiveCommonInputView(PKLiveActivity.this);
        interactiveCommonInputView.setViewType(InteractiveCommonInputView.ViewType.PK);
        interactiveCommonInputView.showInputView(content, showInputView);
        mAUILiveDialog.setContentView(interactiveCommonInputView);
        mAUILiveDialog.show();

        interactiveCommonInputView.setOnInteractLiveTipsViewListener(new InteractLiveTipsViewListener() {
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
            public void onInputConfirm(InteractiveUserData userData) {
                mAUILiveDialog.dismiss();
                if (userData != null && !TextUtils.isEmpty(userData.userId) && !TextUtils.isEmpty(userData.channelId)) {
                    mPKController.setPKOtherInfo(userData);
                    mPKController.startPK(userData, mOtherFrameLayout);
                    setInfoView(mRoomId, userData.userId, mUserId, userData.channelId);
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