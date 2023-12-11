package com.alivc.live.interactive_pk;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.commonutils.ToastUtils;
import com.alivc.live.interactive_common.bean.InteractiveUserData;
import com.alivc.live.interactive_common.listener.ConnectionLostListener;
import com.alivc.live.interactive_common.listener.InteractLivePushPullListener;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;
import com.alivc.live.interactive_common.utils.InteractLiveIntent;
import com.alivc.live.interactive_common.widget.AUILiveDialog;
import com.alivc.live.interactive_common.widget.ConnectionLostTipsView;
import com.alivc.live.interactive_common.widget.InteractiveCommonInputView;
import com.alivc.live.interactive_common.widget.InteractiveSettingView;
import com.alivc.live.player.annotations.AlivcLivePlayError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多人 PK 互动界面
 */
public class MultiPKLiveActivity extends AppCompatActivity {

    private static final String TAG = "MultiPKLiveActivity";

    private PKController mPKController;
    private AUILiveDialog mAUILiveDialog;
    private InteractLiveIntent mCurrentIntent;
    private String mRoomId;
    private String mUserId;
    private ImageView mCloseImageView;
    private TextView mShowConnectTextView;
    private FrameLayout mOwnerFrameLayout;
    private RecyclerView mRecyclerView;
    private MultiPKLiveRecyclerViewAdapter mMultiPKLiveRecyclerViewAdapter;
    private List<Boolean> mDataList = new ArrayList<>();
    private Map<String, Integer> mKeyPositionMap = new HashMap<>();
    private Map<String, InteractiveUserData> mUserDataMap = new HashMap<>();
    //当 item 划出屏幕时，防止 stop 事件调用导致 mKeyPositionMap 移除对应的 userKey，从而无法 resumeVideo
    private List<String> mDetachedUserKeyList = new ArrayList<>();
    //已经添加混流的 userId
    private List<String> mHasAddMixStreamUserId = new ArrayList<>();

    //停止 PK 时需要的 roomId 和 userId
    private InteractiveUserData mStopPKUserData;
    private ConnectionLostTipsView mConnectionLostTipsView;
    private boolean mIsMute = false;
    private InteractiveSettingView mInteractiveSettingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_multi_pklive);

        mRoomId = getIntent().getStringExtra(PKLiveInputActivity.DATA_HOME_ID);
        mUserId = getIntent().getStringExtra(PKLiveInputActivity.DATA_USER_ID);
        mPKController = new PKController(this, mRoomId, mUserId);

        initView();
        initRecyclerView();
        initListener();
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mCloseImageView = findViewById(R.id.iv_close);
        mOwnerFrameLayout = findViewById(R.id.frame_owner);
        mShowConnectTextView = findViewById(R.id.tv_show_connect);
        mRecyclerView = findViewById(R.id.recycler_view);

        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);
        mHomeIdTextView.setText(mRoomId);

        mConnectionLostTipsView = new ConnectionLostTipsView(this);

        mPKController.startPush(mOwnerFrameLayout);
        mPKController.addMultiPKLiveMixTranscoding(true, mPKController.mOwnerUserData, null);

        mInteractiveSettingView = findViewById(R.id.interactive_setting_view);
    }

    private void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMultiPKLiveRecyclerViewAdapter = new MultiPKLiveRecyclerViewAdapter();

        for (int i = 0; i < 16; i++) {
            mDataList.add(false);
        }

        mMultiPKLiveRecyclerViewAdapter.setData(mDataList);
        mRecyclerView.setAdapter(mMultiPKLiveRecyclerViewAdapter);
    }

    private void initListener() {

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
        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(0, getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mMultiPKLiveRecyclerViewAdapter.setOnPKItemViewChangedListener(new MultiPKLiveRecyclerViewAdapter.OnPKItemViewChangedListener() {
            @Override
            public void onItemViewAttachedToWindow(int position) {
                String userKey = getUserKeyByPosition(position);
                if (TextUtils.isEmpty(userKey)) {
                    return;
                }
                mDetachedUserKeyList.remove(userKey);
                InteractiveUserData userData = mUserDataMap.get(userKey);
                if (userData == null) {
                    return;
                }
                MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                if (viewHolder != null) {
                    boolean reCreated = mMultiPKLiveRecyclerViewAdapter.reSetFrameLayout(position, viewHolder.getRenderFrameLayout().hashCode());
                    if (reCreated) {
                        mPKController.setPullView(userData, viewHolder.getRenderFrameLayout());
                    }
                }
                Log.d(TAG, "onItemViewAttachedToWindow: userKey = " + userKey + " --- position = " + position);
                mPKController.resumeVideoPlaying(userKey);
            }

            @Override
            public void onItemViewDetachedToWindow(int position) {
                String userKey = getUserKeyByPosition(position);
                if (TextUtils.isEmpty(userKey)) {
                    return;
                }
                mDetachedUserKeyList.add(userKey);
                Log.d(TAG, "onItemViewDetachedToWindow: userKey = " + userKey + " --- position = " + position);
                mPKController.pauseVideoPlaying(userKey);
            }
        });

        mPKController.setMultiPKLivePushPullListener(new InteractLivePushPullListener() {
            @Override
            public void onPullSuccess(InteractiveUserData userData) {
                super.onPullSuccess(userData);
                if (userData == null) {
                    return;
                }
                String userKey = userData.getKey();
                //开始 PK 成功
                if (mKeyPositionMap.containsKey(userKey)) {
                    MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mKeyPositionMap.get(userKey));
                    if (viewHolder != null && !mHasAddMixStreamUserId.contains(userData.userId)) {
                        viewHolder.updateUserInfo(userData.channelId, userData.userId);
                        mHasAddMixStreamUserId.add(userData.userId);
                        //添加混流
                        mPKController.addMultiPKLiveMixTranscoding(false, userData, viewHolder.getRenderFrameLayout());
                    }
                    changeFrameLayoutViewVisible(true, mKeyPositionMap.get(userKey));
                }
            }

            @Override
            public void onPullError(InteractiveUserData userData, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userData, errorType, errorMsg);
                if (userData == null) {
                    return;
                }
                if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                    ToastUtils.show("用户号:" + userData.userId + " 房间号:" + userData.channelId + " 主播离开");
                }
            }

            @Override
            public void onPullStop(InteractiveUserData userData) {
                super.onPullStop(userData);
                if (userData == null) {
                    return;
                }
                String userKey = userData.getKey();
                //停止 PK
                if (mHasAddMixStreamUserId.contains(userData.userId)) {
                    //删除对应的混流
                    mHasAddMixStreamUserId.remove(userData.userId);
                    mPKController.removeMultiPKLiveMixTranscoding(false, userData);

                    if (mKeyPositionMap.containsKey(userKey) && mKeyPositionMap.get(userKey) != null) {
                        Integer position = mKeyPositionMap.get(userKey);
                        MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                        if (viewHolder != null) {
                            viewHolder.getRenderFrameLayout().removeAllViews();
                        }
                    }

                }
                if (mKeyPositionMap.containsKey(userKey) && !mDetachedUserKeyList.contains(userKey)) {
                    changeFrameLayoutViewVisible(false, mKeyPositionMap.get(userKey));
                }
                mKeyPositionMap.remove(userKey);
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
                super.onPushSuccess();
            }

            @Override
            public void onPushError() {
                super.onPushError();
            }
        });

        mMultiPKLiveRecyclerViewAdapter.setOnPKConnectClickListener(new MultiPKLiveRecyclerViewAdapter.OnPKItemClickListener() {
            @Override
            public void onPKConnectClick(int position) {
                String content = getUserKeyByPosition(position);
                mStopPKUserData = mUserDataMap.get(content);
                if (mStopPKUserData != null && mPKController.isPKing(mStopPKUserData)) {
                    mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                    showInteractLiveDialog(position, getResources().getString(R.string.pk_live_connect_finish_tips), false);
                } else {
                    showInteractLiveDialog(position, null, true);
                }
            }

            @Override
            public void onPKMuteClick(int position, boolean mute) {
                String userKey = getUserKeyByPosition(position);
                mPKController.muteAnchor(userKey, mute);
            }
        });
    }

    private void showInteractLiveDialog(int position, String content, boolean showInputView) {
        InteractiveCommonInputView commonInputView = new InteractiveCommonInputView(MultiPKLiveActivity.this);
        commonInputView.setViewType(InteractiveCommonInputView.ViewType.PK);
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
                //退出直播
                if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PULL) {
                    mAUILiveDialog.dismiss();
                    if (mStopPKUserData != null) {
                        mPKController.stopMultiPK(mStopPKUserData);
                    }
                    changeFrameLayoutViewVisible(false, position);
                }
            }

            @Override
            public void onInputConfirm(InteractiveUserData userData) {
                mAUILiveDialog.dismiss();
                if (userData == null || TextUtils.isEmpty(userData.channelId) || TextUtils.isEmpty(userData.userId)) {
                    return;
                }
                String userDataKey = userData.getKey();
                if (mKeyPositionMap.containsKey(userDataKey)) {
                    return;
                }
                //同一个用户 id 不能重复 PK。
                if (isInPK(userData.userId)) {
                    return;
                }
                RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                if (layoutManager != null) {
                    layoutManager.scrollToPosition(position);
                }
                mRecyclerView.post(() -> {
                    MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                    if (viewHolder != null) {
                        mKeyPositionMap.put(userDataKey, position);
                        mUserDataMap.put(userDataKey, userData);
                        mPKController.setMultiPKOtherInfo(userData);
                        mPKController.startMultiPK(userData, viewHolder.getRenderFrameLayout());
                    }
                });
            }
        });
    }

    private void changeFrameLayoutViewVisible(boolean isShowSurfaceView, int position) {
        mDataList.set(position, isShowSurfaceView);
        if (isShowSurfaceView) {
            mShowConnectTextView.setVisibility(View.VISIBLE);
        } else {
            mShowConnectTextView.setVisibility(View.GONE);
        }
        mMultiPKLiveRecyclerViewAdapter.notifyItemChanged(position, "payload");
    }

    /**
     * 判断该用户 id 是否在 PK 中
     *
     * @param userId 用户 ID
     */
    private boolean isInPK(String userId) {
        return mKeyPositionMap.containsKey(userId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPKController.release();
    }

    private String getUserKeyByPosition(int position) {
        String content = "";
        for (String key : mKeyPositionMap.keySet()) {
            Integer value = mKeyPositionMap.get(key);
            if (value != null && position == value) {
                content = key;
                break;
            }
        }
        return content;
    }
}