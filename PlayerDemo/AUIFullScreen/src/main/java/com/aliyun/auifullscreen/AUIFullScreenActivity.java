package com.aliyun.auifullscreen;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aliyun.auifullscreen.widget.FunctionShadowView;
import com.aliyun.player.IPlayer;
import com.aliyun.player.alivcplayerexpand.constants.GlobalPlayerConfig;
import com.aliyun.player.alivcplayerexpand.listplay.ListPlayManager;
import com.aliyun.player.alivcplayerexpand.theme.Theme;
import com.aliyun.player.alivcplayerexpand.util.AliyunScreenMode;
import com.aliyun.player.alivcplayerexpand.util.FastClickUtil;
import com.aliyun.player.alivcplayerexpand.util.ScreenUtils;
import com.aliyun.player.alivcplayerexpand.view.choice.AlivcShowMoreDialog;
import com.aliyun.player.alivcplayerexpand.view.control.ControlView;
import com.aliyun.player.alivcplayerexpand.view.more.AliyunShowMoreValue;
import com.aliyun.player.alivcplayerexpand.view.more.VideoDeveloperModeView;
import com.aliyun.player.alivcplayerexpand.view.more.VideoSpeedView;
import com.aliyun.player.alivcplayerexpand.view.softinput.SoftInputDialogFragment;
import com.aliyun.player.alivcplayerexpand.view.tips.OnTipsViewBackClickListener;
import com.aliyun.player.alivcplayerexpand.widget.AliyunVodPlayerView;
import com.aliyun.player.alivcplayerexpand.view.tips.TipsView;
import com.aliyun.player.bean.InfoBean;
import com.aliyun.player.source.VidSts;
import com.aliyun.video.common.ui.BaseActivity;

import org.jetbrains.annotations.NotNull;

public class AUIFullScreenActivity extends BaseActivity {

    private static final String FULL_SCREEN_LONG_PRESS_KEY = "enable_long_press_full_screen";

    private ListPlayManager mCommonPlayManager;

    private SoftInputDialogFragment mSoftInputDialogFragment;
    private FunctionShadowView mFunctionShadowView;
    private VideoDeveloperModeView videoDeveloperModeView;
    private TextView mTitleTextView;
    private ImageView mBackImageView;
    private AUIFullScreenController mController;

    protected boolean isStrangePhone() {
        return "mx5".equalsIgnoreCase(Build.DEVICE)
                || "Redmi Note2".equalsIgnoreCase(Build.DEVICE)
                || "Z00A_1".equalsIgnoreCase(Build.DEVICE)
                || "hwH60-L02".equalsIgnoreCase(Build.DEVICE)
                || "hermes".equalsIgnoreCase(Build.DEVICE)
                || ("V4".equalsIgnoreCase(Build.DEVICE) && "Meitu".equalsIgnoreCase(Build.MANUFACTURER))
                || ("m1metal".equalsIgnoreCase(Build.DEVICE) && "Meizu".equalsIgnoreCase(Build.MANUFACTURER));
    }

    private AliyunVodPlayerView mAliyunVodPlayerView;
    private VidSts mVidSts;
    private AlivcShowMoreDialog mShowMoreDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //防录屏截屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_aui_full_screen);
        mController = new AUIFullScreenController(this);
        mCommonPlayManager = ListPlayManager.GlobalPlayer.getListPlayManager(getLifecycle());
        mCommonPlayManager.init(this);
        mCommonPlayManager.setContrastPlay(false);

        initPlayerView();
        initListener();
        initScreenFull();

        mController.initData();
        mAliyunVodPlayerView.showLongPressView(FULL_SCREEN_LONG_PRESS_KEY);
    }

    private void initPlayerView() {
        mFunctionShadowView = findViewById(R.id.function_click_shadow);
        mAliyunVodPlayerView = findViewById(R.id.player_view);

        mTitleTextView = findViewById(R.id.tv_title);
        mBackImageView = findViewById(R.id.iv_back);

        //保持屏幕敞亮
        mAliyunVodPlayerView.setKeepScreenOn(true);
        mAliyunVodPlayerView.setTheme(Theme.Blue);
        mAliyunVodPlayerView.setAutoPlay(true);

        int mFullScreenType = 0;
        mAliyunVodPlayerView.initVideoView(
                mCommonPlayManager.getIRenderView(),
                mCommonPlayManager,
                mFullScreenType
        );

        initSoftDialogFragment();
    }

    private void initListener() {
        mAliyunVodPlayerView.setOnShowMoreClickListener(new ControlView.OnShowMoreClickListener() {
            @Override
            public void showMore() {
                if (FastClickUtil.isFastClick()) {
                    return;
                }
                showMoreDialog();
            }
        });

        mAliyunVodPlayerView.setOnInfoListener(new IPlayer.OnInfoListener() {
            @Override
            public void onInfo(InfoBean infoBean) {
                if (videoDeveloperModeView != null && videoDeveloperModeView.isShown()) {
                    float option = mAliyunVodPlayerView.getOption(IPlayer.Option.RenderFPS);
                    videoDeveloperModeView.setUpData(option);
                }
            }
        });
        mAliyunVodPlayerView.setOnCompletionListener(new IPlayer.OnCompletionListener() {
            @Override
            public void onCompletion() {
                mAliyunVodPlayerView.showReplay();
            }
        });
        mAliyunVodPlayerView.setOnFinishListener(new AliyunVodPlayerView.OnFinishListener() {
            @Override
            public void onFinishClick() {
                finish();
            }
        });
        mAliyunVodPlayerView.setOnTipClickListener(new TipsView.OnTipClickListener() {
            @Override
            public void onContinuePlay() { }

            @Override
            public void onStopPlay() { }

            @Override
            public void onRetryPlay(int errorCode) {
                mAliyunVodPlayerView.hideErrorTipView();
                mController.initData();
            }

            @Override
            public void onReplay() {
                mAliyunVodPlayerView.setVidSts(mVidSts, true);
            }

            @Override
            public void onRefreshSts() { }

            @Override
            public void onWait() { }

            @Override
            public void onExit() { }
        });

        mAliyunVodPlayerView.setOnTipsViewBackClickListener(new OnTipsViewBackClickListener() {
            @Override
            public void onBackClick() {
                finish();
            }
        });

        mAliyunVodPlayerView.setOnVideoSpeedClickListener(new ControlView.OnVideoSpeedClickListener() {

            @Override
            public void onVideoSpeedClick() {
                showFunctionShadowView(true);
                mShowMoreDialog = new AlivcShowMoreDialog(AUIFullScreenActivity.this);
                float videoSpeed = mAliyunVodPlayerView.getCurrentSpeed();
                VideoSpeedView showMoreView = new VideoSpeedView(AUIFullScreenActivity.this, videoSpeed);
                mShowMoreDialog.setContentView(showMoreView);
                mShowMoreDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        showFunctionShadowView(false);
                    }
                });
                mShowMoreDialog.show();
                showMoreView.setMOnVideoSpeedSelected(new VideoSpeedView.OnVideoSpeedSelected() {
                    @Override
                    public void onVideoSpeedSelected(float speed) {
                        mAliyunVodPlayerView.changeSpeed(speed);
                        Toast.makeText(AUIFullScreenActivity.this, getString(R.string.play_speed_changed, "" + speed), Toast.LENGTH_SHORT).show();
                        closeFunctionDialog();
                    }
                });
            }
        });

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void closeFunctionDialog() {
        if (mShowMoreDialog != null && mShowMoreDialog.isShowing()) {
            mShowMoreDialog.dismiss();
        }
    }

    private void initScreenFull() {
        RelativeLayout.LayoutParams videoViewLp = (RelativeLayout.LayoutParams) mAliyunVodPlayerView.getLayoutParams();
        videoViewLp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        videoViewLp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        videoViewLp.leftMargin = 0;
        videoViewLp.rightMargin = 0;
        videoViewLp.topMargin = 0;
        mAliyunVodPlayerView.setLayoutParams(videoViewLp);
        mAliyunVodPlayerView.changeScreenMode(AliyunScreenMode.Full, false);
    }

    private void showMoreDialog() {
        showFunctionShadowView(true);
        AlivcShowMoreDialog showMoreDialog = new AlivcShowMoreDialog(this);
        AliyunShowMoreValue moreValue = new AliyunShowMoreValue();
        moreValue.setScaleMode(mAliyunVodPlayerView.getScaleMode());
        moreValue.setEnableHardDecodeType(GlobalPlayerConfig.mEnableHardDecodeType);

        videoDeveloperModeView = new VideoDeveloperModeView(this, moreValue);
        showMoreDialog.setContentView(videoDeveloperModeView);
        showMoreDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                showFunctionShadowView(false);
            }
        });

        showMoreDialog.show();
    }

    private void showFunctionShadowView(boolean shown) {
        mFunctionShadowView.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    private void initSoftDialogFragment() {
        mSoftInputDialogFragment = SoftInputDialogFragment.newInstance();
        mSoftInputDialogFragment.setOnBarrageSendClickListener(new SoftInputDialogFragment.OnBarrageSendClickListener() {
            @Override
            public void onBarrageSendClick(String danmu) {
                mAliyunVodPlayerView.setmDanmaku(danmu);
                mSoftInputDialogFragment.dismiss();
            }
        });
    }

    public void prepareDataSource(VidSts vidSts) {
        this.mVidSts = vidSts;
        mAliyunVodPlayerView.setVidSts(vidSts, true);
    }

    public void getDataSourceError(String msg){
        mAliyunVodPlayerView.showErrorTipView(0,"0",msg);
    }

    public void showLoading(boolean isShown){
        if(isShown){
            mAliyunVodPlayerView.showNetLoadingTipView();
        }else{
            mAliyunVodPlayerView.hideNetLoadingTipView();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull @NotNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updatePlayerViewMode();
    }

    private void updatePlayerViewMode() {
        if (mAliyunVodPlayerView != null) {
            int orientation = getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                mTitleTextView.setVisibility(View.VISIBLE);
                mBackImageView.setVisibility(View.VISIBLE);

                fullScreen(false);
                //设置view的布局，宽高之类
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = (int) (ScreenUtils.getWidth(this) * 9.0f / 16);
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mTitleTextView.setVisibility(View.GONE);
                mBackImageView.setVisibility(View.GONE);
                //转到横屏了。
                //设置view的布局，宽高
                RelativeLayout.LayoutParams aliVcVideoViewLayoutParams = (RelativeLayout.LayoutParams) mAliyunVodPlayerView
                        .getLayoutParams();
                aliVcVideoViewLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                aliVcVideoViewLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;

                fullScreen(true);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayerViewMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAliyunVodPlayerView.onDestroy();
    }
}