package com.aliyun.interactive_pk;

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

import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.utils.ToastUtils;
import com.aliyun.interactive_common.listener.InteractLiveTipsViewListener;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;
import com.aliyun.interactive_common.utils.InteractLiveIntent;
import com.aliyun.interactive_common.widget.AUILiveDialog;
import com.alivc.live.utils.FastClickUtil;

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
    private ImageView mCameraImageView;
    private TextView mShowConnectTextView;
    private FrameLayout mOwnerFrameLayout;
    private RecyclerView mRecyclerView;
    private MultiPKLiveRecyclerViewAdapter mMultiPKLiveRecyclerViewAdapter;
    private List<Boolean> mDataList = new ArrayList<>();
    private Map<String, Integer> mKeyPositionMap = new HashMap<>();
    //当 item 划出屏幕时，防止 stop 事件调用导致 mKeyPositionMap 移除对应的 userKey，从而无法 resumeVideo
    private List<String> mDetachedUserKeyList = new ArrayList<>();
    //已经添加混流的 userId
    private List<String> mHasAddMixStreamUserId = new ArrayList<>();

    //停止 PK 时需要的 roomId 和 userId
    private String[] mStopPKSplit;

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
        mCameraImageView = findViewById(R.id.iv_camera);
        mOwnerFrameLayout = findViewById(R.id.frame_owner);
        mShowConnectTextView = findViewById(R.id.tv_show_connect);
        mRecyclerView = findViewById(R.id.recycler_view);

        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);
        mHomeIdTextView.setText(mRoomId);

        mPKController.startPush(mOwnerFrameLayout);
        mPKController.addMultiPKLiveMixTranscoding(true, mUserId, null);
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
        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(0, getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mCameraImageView.setOnClickListener(view -> {
            if (!FastClickUtil.isFastClick()) {
                mPKController.switchCamera();
            }
        });

        mMultiPKLiveRecyclerViewAdapter.setOnPKItemViewChangedListener(new MultiPKLiveRecyclerViewAdapter.OnPKItemViewChangedListener() {
            @Override
            public void onItemViewAttachedToWindow(int position) {
                String userKey = getUserKeyByPosition(position);
                if (!TextUtils.isEmpty(userKey)) {
                    mDetachedUserKeyList.remove(userKey);
                    MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                    if (viewHolder != null) {
                        boolean reCreated = mMultiPKLiveRecyclerViewAdapter.reSetFrameLayout(position, viewHolder.getRenderFrameLayout().hashCode());
                        if (reCreated) {
                            String[] split = userKey.split("=");
                            mPKController.setPullView(split[0], split[1], viewHolder.getRenderFrameLayout());
                        }
                    }
                    Log.d(TAG, "onItemViewAttachedToWindow: userKey = " + userKey + " --- position = " + position);
                    mPKController.resumeVideoPlaying(userKey);
                }
            }

            @Override
            public void onItemViewDetachedToWindow(int position) {
                String userKey = getUserKeyByPosition(position);
                if (!TextUtils.isEmpty(userKey)) {
                    mDetachedUserKeyList.add(userKey);
                    Log.d(TAG, "onItemViewDetachedToWindow: userKey = " + userKey + " --- position = " + position);
                    mPKController.pauseVideoPlaying(userKey);
                }

            }
        });

        mPKController.setMultiPKLivePushPullListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String userKey) {
                super.onPullSuccess(userKey);
                //开始 PK 成功
                if (mKeyPositionMap.containsKey(userKey)) {
                    if (!TextUtils.isEmpty(userKey) && userKey.contains("=")) {
                        MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mKeyPositionMap.get(userKey));
                        String userId = userKey.split("=")[0];
                        if (viewHolder != null && !mHasAddMixStreamUserId.contains(userId)) {
                            mHasAddMixStreamUserId.add(userId);
                            //添加混流
                            mPKController.addMultiPKLiveMixTranscoding(false, userId, viewHolder.getRenderFrameLayout());
                        }
                    }
                    changeFrameLayoutViewVisible(true, mKeyPositionMap.get(userKey));
                }
            }

            @Override
            public void onPullError(String userKey, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userKey, errorType, errorMsg);
                if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                    ToastUtils.show("用户号:" + userKey.split("=")[0] + " 房间号:" + userKey.split("=")[1] + " 主播离开");
                }
            }

            @Override
            public void onPullStop(String userKey) {
                super.onPullStop(userKey);
                //停止 PK
                if (!TextUtils.isEmpty(userKey) && userKey.contains("=") && mHasAddMixStreamUserId.contains(userKey.split("=")[0])) {
                    //删除对应的混流
                    mHasAddMixStreamUserId.remove(userKey.split("=")[0]);
                    mPKController.removeMultiPKLiveMixTranscoding(false, userKey.split("=")[0]);

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
            public void onPushSuccess() {
                super.onPushSuccess();
            }

            @Override
            public void onPushError() {
                super.onPushError();
            }
        });

        mMultiPKLiveRecyclerViewAdapter.setOnPKConnectClickListener(position -> {
            String content = getUserKeyByPosition(position);
            mStopPKSplit = new String[]{};
            if (!TextUtils.isEmpty(content) && content.contains("=")) {
                mStopPKSplit = content.split("=");
            }
            if (mStopPKSplit.length > 0 && mPKController.isPKing(mStopPKSplit[0], mStopPKSplit[1])) {
                mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
                showInteractLiveDialog(position, getResources().getString(R.string.pk_live_connect_finish_tips), false);
            } else {
                showInteractLiveDialog(position, null, true);
            }
        });
    }

    private void showInteractLiveDialog(int position, String content, boolean showInputView) {
        PKLiveTipsView pkLiveTipsView = new PKLiveTipsView(MultiPKLiveActivity.this);
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
                    if (mStopPKSplit != null && mStopPKSplit.length > 0) {
                        mPKController.stopMultiPK(mStopPKSplit[0], mStopPKSplit[1]);
                    }
                    changeFrameLayoutViewVisible(false, position);
                }
            }

            @Override
            public void onInputConfirm(String content) {
                mAUILiveDialog.dismiss();
                if (mKeyPositionMap.containsKey(content)) {
                    return;
                }
                if (content.contains("=")) {
                    String[] split = content.split("=");
                    //同一个用户 id 不能重复 PK。
                    if (isInPK(split[0])) {
                        return;
                    }
                    RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.scrollToPosition(position);
                    }
                    mRecyclerView.post(() -> {
                        MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                        if (viewHolder != null) {
                            mKeyPositionMap.put(content, position);
                            mPKController.setMultiPKOtherInfo(split[0], split[1]);
                            mPKController.startMultiPK(split[0], split[1], viewHolder.getRenderFrameLayout());
                        }
                    });
                }
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