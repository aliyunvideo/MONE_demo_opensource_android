package com.aliyun.interactive_live;

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

import com.alivc.live.player.annotations.AlivcLivePlayError;
import com.aliyun.interactive_common.listener.InteractLiveTipsViewListener;
import com.aliyun.interactive_common.listener.MultiInteractLivePushPullListener;
import com.aliyun.interactive_common.utils.InteractLiveIntent;
import com.aliyun.interactive_common.widget.AUILiveDialog;
import com.aliyun.interactive_live.widget.MultiAlivcLiveView;
import com.alivc.live.utils.StatusBarUtil;
import com.alivc.live.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MultiInteractLiveActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATA_IS_ANCHOR = "data_is_anchor";
    public static final String DATA_HOME_ID = "data_home_id";
    public static final String DATA_USER_ID = "data_user_id";

    private AUILiveDialog mAUILiveDialog;

    //Dialog 弹窗的意图
    private InteractLiveIntent mCurrentIntent;
    private String mRoomId;
    private TextView mConnectTextView1;
    private TextView mConnectTextView2;
    private TextView mConnectTextView3;
    private TextView mConnectTextView4;
    private ImageView mCloseImageView;
    private ImageView mCameraImageView;
    private TextView mShowConnectIdTextView;
    //大窗口
    private FrameLayout mBigFrameLayout;
    private SurfaceView mBigSurfaceView;
    private MultiAnchorController mMultiAnchorController;
    //根据 TextView 获取其他 View
    private final Map<TextView, MultiAlivcLiveView> mViewCombMap = new HashMap<>();
    //根据 id 获取其他 View
    private final Map<String, MultiAlivcLiveView> mIdViewCombMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);

        setContentView(R.layout.activity_multi_interact_live);

        mRoomId = getIntent().getStringExtra(DATA_HOME_ID);
        String anchorId = getIntent().getStringExtra(DATA_USER_ID);

        mMultiAnchorController = new MultiAnchorController(this, mRoomId, anchorId);

        initView();
        initListener();
        initData();
    }

    private void initData() {
        mMultiAnchorController.setAnchorRenderView(mBigFrameLayout);
        mMultiAnchorController.startPush();
    }

    private void initView() {
        mAUILiveDialog = new AUILiveDialog(this);
        mConnectTextView1 = findViewById(R.id.tv_connect_1);
        mConnectTextView2 = findViewById(R.id.tv_connect_2);
        mConnectTextView3 = findViewById(R.id.tv_connect_3);
        mConnectTextView4 = findViewById(R.id.tv_connect_4);
        FrameLayout mUnConnectFrameLayout1 = findViewById(R.id.fl_un_connect_1);
        FrameLayout mUnConnectFrameLayout2 = findViewById(R.id.fl_un_connect_2);
        FrameLayout mUnConnectFrameLayout3 = findViewById(R.id.fl_un_connect_3);
        FrameLayout mUnConnectFrameLayout4 = findViewById(R.id.fl_un_connect_4);
        mBigSurfaceView = findViewById(R.id.big_surface_view);
        mCloseImageView = findViewById(R.id.iv_close);
        mCameraImageView = findViewById(R.id.iv_camera);
        mShowConnectIdTextView = findViewById(R.id.tv_show_connect);
        mBigFrameLayout = findViewById(R.id.big_fl);
        //小窗口
        FrameLayout mSmallFrameLayout1 = findViewById(R.id.small_fl_1);
        FrameLayout mSmallFrameLayout2 = findViewById(R.id.small_fl_2);
        FrameLayout mSmallFrameLayout3 = findViewById(R.id.small_fl_3);
        FrameLayout mSmallFrameLayout4 = findViewById(R.id.small_fl_4);
        TextView mHomeIdTextView = findViewById(R.id.tv_home_id);

        mHomeIdTextView.setText(mRoomId);

        mBigSurfaceView.setZOrderOnTop(true);
        mBigSurfaceView.setZOrderMediaOverlay(true);

        MultiAlivcLiveView multiAlivcLiveView1 = new MultiAlivcLiveView(mUnConnectFrameLayout1, mSmallFrameLayout1, mConnectTextView1);
        MultiAlivcLiveView multiAlivcLiveView2 = new MultiAlivcLiveView(mUnConnectFrameLayout2, mSmallFrameLayout2, mConnectTextView2);
        MultiAlivcLiveView multiAlivcLiveView3 = new MultiAlivcLiveView(mUnConnectFrameLayout3, mSmallFrameLayout3, mConnectTextView3);
        MultiAlivcLiveView multiAlivcLiveView4 = new MultiAlivcLiveView(mUnConnectFrameLayout4, mSmallFrameLayout4, mConnectTextView4);

        mViewCombMap.put(mConnectTextView1, multiAlivcLiveView1);
        mViewCombMap.put(mConnectTextView2, multiAlivcLiveView2);
        mViewCombMap.put(mConnectTextView3, multiAlivcLiveView3);
        mViewCombMap.put(mConnectTextView4, multiAlivcLiveView4);
    }

    private void initListener() {
        mMultiAnchorController.setMultiInteractLivePushPullListener(new MultiInteractLivePushPullListener() {
            @Override
            public void onPullSuccess(String audienceId) {
                MultiAlivcLiveView multiAlivcLiveView = mIdViewCombMap.get(audienceId);
                if (multiAlivcLiveView != null) {
                    multiAlivcLiveView.getSmallFrameLayout().setVisibility(View.VISIBLE);
                    multiAlivcLiveView.getUnConnectFrameLayout().setVisibility(View.INVISIBLE);
                    updateConnectTextView(true, multiAlivcLiveView.getConnectTextView());
                }
            }

            @Override
            public void onPullError(String audienceId, AlivcLivePlayError errorType, String errorMsg) {
                runOnUiThread(() -> {
                    if (errorType == AlivcLivePlayError.AlivcLivePlayErrorStreamStopped) {
                        mMultiAnchorController.stopConnect(audienceId);
                        MultiAlivcLiveView multiAlivcLiveView = mIdViewCombMap.get(audienceId);
                        if (multiAlivcLiveView != null) {
                            changeSmallSurfaceViewVisible(false, multiAlivcLiveView);
                            updateConnectTextView(false, multiAlivcLiveView.getConnectTextView());
                        }
                        ToastUtils.show(getResources().getString(R.string.interact_live_viewer_left));
                    }
                });
            }

        });

        //开始连麦
        mConnectTextView1.setOnClickListener(this);
        mConnectTextView2.setOnClickListener(this);
        mConnectTextView3.setOnClickListener(this);
        mConnectTextView4.setOnClickListener(this);

        mCloseImageView.setOnClickListener(view -> {
            mCurrentIntent = InteractLiveIntent.INTENT_FINISH;
            showInteractLiveDialog(null, getResources().getString(R.string.interact_live_leave_room_tips), false);
        });

        mCameraImageView.setOnClickListener(view -> mMultiAnchorController.switchCamera());
    }

    private void changeSmallSurfaceViewVisible(boolean isShowSurfaceView, MultiAlivcLiveView alivcLiveView) {
        alivcLiveView.getSmallFrameLayout().setVisibility(isShowSurfaceView ? View.VISIBLE : View.INVISIBLE);
        alivcLiveView.getUnConnectFrameLayout().setVisibility(isShowSurfaceView ? View.INVISIBLE : View.VISIBLE);
    }

    public void updateConnectTextView(boolean connecting, TextView mConnectTextView) {
        if (connecting) {
            mShowConnectIdTextView.setVisibility(View.VISIBLE);
            mConnectTextView.setText(getResources().getString(R.string.interact_stop_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_un_connect_btn_bg));
        } else {
            mShowConnectIdTextView.setVisibility(View.GONE);
            mConnectTextView.setText(getResources().getString(R.string.interact_start_connect));
            mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
            mConnectTextView.setTag("");
        }
    }

    private void showInteractLiveDialog(TextView textView, String content, boolean showInputView) {
        InteractLiveTipsView interactLiveTipsView = new InteractLiveTipsView(MultiInteractLiveActivity.this);
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
                if (mCurrentIntent == InteractLiveIntent.INTENT_STOP_PULL) {
                    //主播结束连麦
                    mAUILiveDialog.dismiss();
                    mMultiAnchorController.stopConnect((String) textView.getTag());
                    updateConnectTextView(false, textView);
                    MultiAlivcLiveView multiAlivcLiveView = mViewCombMap.get(textView);
                    if (multiAlivcLiveView != null) {
                        changeSmallSurfaceViewVisible(false, multiAlivcLiveView);
                    }
                } else if (mCurrentIntent == InteractLiveIntent.INTENT_FINISH) {
                    finish();
                }
            }

            @Override
            public void onInputConfirm(String content) {
                hideInputSoftFromWindowMethod(MultiInteractLiveActivity.this, interactLiveTipsView);
                if (TextUtils.isEmpty(content)) {
                    ToastUtils.show(getResources().getString(R.string.interact_live_connect_input_error_tips));
                } else {
                    mAUILiveDialog.dismiss();
                    //每个观众对应一个 AlvcLivePlayer
                    boolean createSuccess = mMultiAnchorController.createAlivcLivePlayer(content);
                    if (!createSuccess) {
                        return;
                    }
                    if (textView != null) {
                        textView.setTag(content);
                    }
                    mIdViewCombMap.put(content, mViewCombMap.get(textView));
                    //主播端，输入观众 id 后，开始连麦
                    mMultiAnchorController.startConnect(content, Objects.requireNonNull(mViewCombMap.get(textView)).getSmallFrameLayout());
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMultiAnchorController.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMultiAnchorController.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMultiAnchorController.release();
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
    public void onClick(View view) {
        String key = "";
        TextView mCurrentTextView = null;
        if (view == mConnectTextView1) {
            mCurrentTextView = mConnectTextView1;
            key = (String) mConnectTextView1.getTag();
        } else if (view == mConnectTextView2) {
            mCurrentTextView = mConnectTextView2;
            key = (String) mConnectTextView2.getTag();
        } else if (view == mConnectTextView3) {
            mCurrentTextView = mConnectTextView3;
            key = (String) mConnectTextView3.getTag();
        } else if (view == mConnectTextView4) {
            mCurrentTextView = mConnectTextView4;
            key = (String) mConnectTextView4.getTag();
        }
        if (mMultiAnchorController.isOnConnected(key)) {
            //主播端停止连麦
            mCurrentIntent = InteractLiveIntent.INTENT_STOP_PULL;
            showInteractLiveDialog(mCurrentTextView, getResources().getString(R.string.interact_live_connect_finish_tips), false);
        } else {
            //主播端开始连麦，输入用户 id
            showInteractLiveDialog(mCurrentTextView, getResources().getString(R.string.interact_live_connect_tips), true);
        }
    }
}