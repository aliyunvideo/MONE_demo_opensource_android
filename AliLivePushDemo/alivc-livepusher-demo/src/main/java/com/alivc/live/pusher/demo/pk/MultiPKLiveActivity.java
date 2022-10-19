package com.alivc.live.pusher.demo.pk;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.alivc.live.pusher.demo.R;
import com.alivc.live.pusher.demo.interactlive.InteractLiveIntent;
import com.alivc.live.pusher.demo.listener.InteractLiveTipsViewListener;
import com.alivc.live.pusher.demo.listener.MultiInteractLivePushPullListener;
import com.alivc.live.pusher.widget.AUILiveDialog;
import com.alivc.live.pusher.widget.FastClickUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多人 PK 互动界面
 */
public class MultiPKLiveActivity extends AppCompatActivity {

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

    //停止 PK 时需要的 roomId 和 userId
    private String[] mStopPKSplit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mMultiPKLiveRecyclerViewAdapter = new MultiPKLiveRecyclerViewAdapter();

        mDataList.add(false);
        mDataList.add(false);
        mDataList.add(false);
        mDataList.add(false);

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

        mPKController.setMultiPKLivePushPullListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String userKey) {
                super.onPullSuccess(userKey);
                //开始 PK 成功
                if (mKeyPositionMap.containsKey(userKey)) {
                    if (!TextUtils.isEmpty(userKey) && userKey.contains("=")) {
                        MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mKeyPositionMap.get(userKey));
                        if (viewHolder != null) {
                            //添加混流
                            mPKController.addMultiPKLiveMixTranscoding(false, userKey.split("=")[0], viewHolder.getRenderFrameLayout());
                        }
                    }
                    changeFrameLayoutViewVisible(true, mKeyPositionMap.get(userKey));
                }
            }

            @Override
            public void onPullError(String userKey, AlivcLivePlayError errorType, String errorMsg) {
                super.onPullError(userKey, errorType, errorMsg);
            }

            @Override
            public void onPullStop(String userKey) {
                super.onPullStop(userKey);
                //停止 PK
                if (!TextUtils.isEmpty(userKey) && userKey.contains("=")) {
                    //删除对应的混流
                    mPKController.removeMultiPKLiveMixTranscoding(false, userKey.split("=")[0]);
                }
                if (mKeyPositionMap.containsKey(userKey)) {
                    changeFrameLayoutViewVisible(false, mKeyPositionMap.get(userKey));
                    mKeyPositionMap.remove(userKey);
                }
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
            String content = "";
            for (String key : mKeyPositionMap.keySet()) {
                Integer value = mKeyPositionMap.get(key);
                if (value != null && position == value) {
                    content = key;
                    break;
                }
            }
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
                        mKeyPositionMap.remove(mStopPKSplit[0] + "=" + mStopPKSplit[1]);
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
                    RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
                    if (layoutManager != null) {
                        layoutManager.scrollToPosition(position);
                    }
                    mRecyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder viewHolder = (MultiPKLiveRecyclerViewAdapter.MultiPKLiveViewHolder) mRecyclerView.findViewHolderForAdapterPosition(position);
                            if (viewHolder != null) {
                                mKeyPositionMap.put(content, position);
                                mPKController.setMultiPKOtherInfo(split[0], split[1]);
                                mPKController.startMultiPK(split[0], split[1], viewHolder.getRenderFrameLayout());
                            }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPKController.release();
    }
}