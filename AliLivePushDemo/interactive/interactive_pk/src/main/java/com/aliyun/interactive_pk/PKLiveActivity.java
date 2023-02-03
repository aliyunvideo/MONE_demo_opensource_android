package com.aliyun.interactive_pk;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.aliyun.interactive_common.listener.ConnectionLostListener;
import com.aliyun.interactive_common.listener.InteractLivePushPullListener;
import com.aliyun.interactive_common.listener.InteractLiveTipsViewListener;
import com.aliyun.interactive_common.utils.InteractLiveIntent;
import com.aliyun.interactive_common.widget.AUILiveDialog;
import com.alivc.live.commonutils.FastClickUtil;
import com.aliyun.interactive_common.widget.ConnectionLostTipsView;
import com.aliyun.interactive_common.widget.RoomAndUserInfoView;

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
    private ImageView mCameraImageView;
    private TextView mConnectTextView;
    private TextView mShowConnectTextView;
    private FrameLayout mOwnerFrameLayout;
    private FrameLayout mOtherFrameLayout;
    private FrameLayout mUnConnectFrameLayout;
    private ConnectionLostTipsView mConnectionLostTipsView;
    private RoomAndUserInfoView mOwnerInfoView;
    private RoomAndUserInfoView mOtherInfoView;
    private ImageView mMuteImageView;
    private boolean mIsMute = false;

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
        mCameraImageView = findViewById(R.id.iv_camera);
        mConnectTextView = findViewById(R.id.tv_connect);
        mOwnerFrameLayout = findViewById(R.id.frame_owner);
        mOtherFrameLayout = findViewById(R.id.frame_other);
        mUnConnectFrameLayout = findViewById(R.id.fl_un_connect);
        mShowConnectTextView = findViewById(R.id.tv_show_connect);

        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);
        mHomeIdTextView.setText(mRoomId);

        mOwnerInfoView = findViewById(R.id.owner_info_view);
        mOtherInfoView = findViewById(R.id.other_info_view);
        mMuteImageView = findViewById(R.id.iv_mute);
//        mOwnerInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
//        mOtherInfoView.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mPKController.startPush(mOwnerFrameLayout);
    }

    private void initListener() {
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
                mPKController.setMute(!mIsMute);
                mIsMute = !mIsMute;
                mMuteImageView.setImageResource(mIsMute ? R.drawable.ic_interact_volume_off : R.drawable.ic_interact_volume_on);
            }
        });

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mCameraImageView.setOnClickListener(view -> {
            if (!FastClickUtil.isFastClick()) {
                mPKController.switchCamera();
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
        mOwnerInfoView.setRoomId(ownerRoomId);
        mOwnerInfoView.setUserId(ownerId);

        mOtherInfoView.setRoomId(otherRoomId);
        mOtherInfoView.setUserId(otherId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPKController.release();
    }
}