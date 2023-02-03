package com.alivc.live.pusher.demo;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.live.pusher.demo.bean.Constants;
import com.alivc.live.pusher.demo.test.PushDemoTestConstants;
import com.alivc.live.pusher.widget.ButtonClickListener;
import com.alivc.live.pusher.widget.CommonDialog;
import com.alivc.live.pusher.widget.PlayButtonListView;
import com.alivc.live.pusher.widget.SeiView;
import com.aliyun.aio.avbaseui.widget.AVToast;
import com.aliyun.aio.avtheme.AVBaseThemeActivity;
import com.alivc.live.commonutils.StatusBarUtil;
import com.alivc.live.commonutils.TextFormatUtil;
import com.aliyun.player.AliPlayer;
import com.aliyun.player.AliPlayerFactory;
import com.aliyun.player.IPlayer;
import com.aliyun.player.bean.ErrorInfo;
import com.aliyun.player.nativeclass.PlayerConfig;
import com.aliyun.player.source.UrlSource;
import com.cicada.player.utils.Logger;
import com.acker.simplezxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播拉流界面
 */
public class PlayerActivity extends AVBaseThemeActivity implements SurfaceHolder.Callback, ButtonClickListener {
    private static final String TAG = "PlayerActivity";
    private PlayButtonListView mButtonListView;
    private SurfaceView mSurfaceView;
    private String mPullUrl = "";
    private AliPlayer mAliPlayer;
    private EditText mPullUrlET;//拉流地址
    private Button mPullStartBtn;//开始拉流btn
    private Button mScanBtn;//扫码
    private boolean isStopPullFlag = false;//是否点击过停止播放
    private boolean isPulling = false;
    private StringBuilder mSeiStringBuilder = new StringBuilder();
    private CommonDialog mDialog;
    private SeiView mSeiView;

    static {
        try{
        
            if(BuildConfig.MTL_BUILD_FOR_AIO) {
                System.loadLibrary("all_in_one");
            } else {
                System.loadLibrary("RtsSDK");
            }
        } catch (Throwable ignore) {}

    }

    private View mPageVg;
    private ImageView mBackBtn;
    private View mInputRl;
    private TextView mTitleTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        StatusBarUtil.translucent(this, Color.TRANSPARENT);
        setContentView(R.layout.activity_player);
        initPlayer();
        initView();

        String initUrl = PushDemoTestConstants.getTestPullUrl();
        if (!initUrl.isEmpty()) {
            mPullUrlET.setText(initUrl);
            mPullUrl = initUrl;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            当前屏幕为横屏
        } else {
//            当前屏幕为竖屏
        }
    }

    private void initPlayer() {
        Logger.getInstance(this).enableConsoleLog(true);
        Logger.getInstance(this).setLogLevel(Logger.LogLevel.AF_LOG_LEVEL_TRACE);
        mAliPlayer = AliPlayerFactory.createAliPlayer(this.getApplicationContext());
        mAliPlayer.setAutoPlay(true);
        mAliPlayer.setOnRenderingStartListener(new IPlayer.OnRenderingStartListener() {
            @Override
            public void onRenderingStart() {
                setViewState(true);
            }
        });

        mAliPlayer.setOnErrorListener(new IPlayer.OnErrorListener() {
            @Override
            public void onError(ErrorInfo errorInfo) {
                if (mAliPlayer != null) {
                    mAliPlayer.reload();
                }
                if (mDialog == null || !mDialog.isShowing()) {
                    mDialog = new CommonDialog(PlayerActivity.this);
                    mDialog.setDialogTitle("播放器出错");
                    mDialog.setDialogContent(errorInfo.getMsg());
                    mDialog.setConfirmButton(TextFormatUtil.getTextFormat(PlayerActivity.this, R.string.cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    mDialog.setCancelButton(TextFormatUtil.getTextFormat(PlayerActivity.this, R.string.restart), new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (mAliPlayer == null) {
                                initPlayer();
                                mSurfaceView = new SurfaceView(PlayerActivity.this);
                                mSurfaceView.getHolder().addCallback(PlayerActivity.this);
                            }
                            UrlSource source = new UrlSource();
                            String url = mPullUrlET.getText().toString();
                            if (TextUtils.isEmpty(url)) {
                                return;
                            }
                            source.setUri(url);
                            if (mAliPlayer != null) {
                                PlayerConfig cfg = mAliPlayer.getConfig();
                                if (url.startsWith("artc://")) {
                                    cfg.mMaxDelayTime = 1000;
                                    cfg.mHighBufferDuration = 10;
                                    cfg.mStartBufferDuration = 10;
                                }
                                mAliPlayer.setConfig(cfg);
                                mAliPlayer.setDataSource(source);
                                mAliPlayer.prepare();
                            }
                        }
                    });
                    mDialog.show();
                }
            }
        });

        mAliPlayer.setOnSeiDataListener((type, data) -> {
//            addSeiText(data);
        });
    }

    private void addSeiText(byte[] data) {
        if(!mSeiView.isShown()){
            mSeiView.setVisibility(View.VISIBLE);
        }
        mSeiStringBuilder.append(new String(data));
        mSeiView.setText(mSeiStringBuilder.toString());
    }

    private void initView() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mPageVg = findViewById(R.id.page_bg);
        mInputRl = findViewById(R.id.input_rl);
        mTitleTv = findViewById(R.id.title);
        mButtonListView = (PlayButtonListView) findViewById(R.id.live_buttonlistview);
        mSeiView = findViewById(R.id.sei_view);
        List<String> data = new ArrayList<>();
        data.addAll(Constants.getPlayActivityButtonList());
        mButtonListView.setData(data);
        mButtonListView.setClickListener(this);
        mButtonListView.setVisibility(View.GONE);
        mSurfaceView.getHolder().addCallback(this);
        mScanBtn = (Button) findViewById(R.id.player_scan_image);
        mScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCaptureActivityForResult();
            }
        });
        mBackBtn = findViewById(R.id.pull_common_btn_close);
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPageVg.getVisibility() == View.GONE) {
                    onButtonClick("暂停播放",0);
                }else {
                    finish();
                }
            }
        });
        mPullUrlET = (EditText) findViewById(R.id.pull_common_push_url);
        mPullStartBtn = (Button) findViewById(R.id.pull_common_start_btn);
        mPullStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isPulling = true;
                startPull(mPullUrlET.getText().toString());
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mAliPlayer != null) {
            mAliPlayer.setSurface(holder.getSurface());
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mAliPlayer != null) {
            mAliPlayer.surfaceChanged();
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mAliPlayer != null) {
            mAliPlayer.setSurface(null);
        }

    }

    private void stopPull() {
        stopPlay();
    }

    private void startPull(String url) {
        if (TextUtils.isEmpty(url)) {
            showTipDialog("拉流地址错误，请检查后重新输入");
        } else {
            showTipDialog( "正在拉流中");
            startPlay();
        }
    }

    private void startCaptureActivityForResult() {
        Intent intent = new Intent(PlayerActivity.this, CaptureActivity.class);
        startActivityForResult(intent, CaptureActivity.REQ_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CaptureActivity.REQ_CODE:
                switch (resultCode) {
                    case RESULT_OK:
                        String pullUrl = data.getStringExtra(CaptureActivity.EXTRA_SCAN_RESULT);
                        mPullUrlET.setText(pullUrl);
                        break;
                    case RESULT_CANCELED:
                        break;
                    default:
                        break;
                }
            default:
                break;
        }
    }

    public void startPlay() {
        isPulling = true;
        mSurfaceView.setVisibility(View.VISIBLE);
        if (TextUtils.isEmpty(mPullUrl) && TextUtils.isEmpty(mPullUrlET.getText().toString())) {
            Toast.makeText(this, "拉流地址为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(mPullUrlET.getText().toString())) {
            mPullUrl = mPullUrlET.getText().toString();
        }
        UrlSource source = new UrlSource();
        String pullUrl = mPullUrlET.getText().toString();
        source.setUri(pullUrl);
        if (mAliPlayer != null) {
            PlayerConfig cfg = mAliPlayer.getConfig();
            cfg.mEnableSEI = true;
            if (pullUrl.startsWith("artc://")) {
                cfg.mMaxDelayTime = 1000;
                cfg.mHighBufferDuration = 10;
                cfg.mStartBufferDuration = 10;
            }
            mAliPlayer.setConfig(cfg);
            mAliPlayer.setDataSource(source);
            mAliPlayer.prepare();
        }
        mButtonListView.setVisibility(View.VISIBLE);
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (mAliPlayer != null) {
            mAliPlayer.stop();
        }
    }

    @Override
    protected void onDestroy() {
        if (mAliPlayer != null) {
            stopPlay();
            mAliPlayer.setSurface(null);
            mAliPlayer.release();
            mAliPlayer = null;
        }
        mSeiStringBuilder.delete(0, mSeiStringBuilder.length());
        super.onDestroy();
    }

    private void showTipDialog( String msg) {
        AVToast.show(this,true,msg);
    }

    @Override
    public void onButtonClick(String message, int position) {
        switch (position) {
            case 0:
                if (isPulling) {
                    stopPull();
                    isPulling = false;
                   setViewState(false);
                } else {
                    startPull(mPullUrlET.getText().toString());
                    isPulling = true;
                }
                break;
            default:
                break;
        }
    }

    private void setViewState(boolean isPlaying){
        if (isPlaying){
            mPageVg.setVisibility(View.GONE);
            mButtonListView.setVisibility(View.VISIBLE);
            mInputRl.setVisibility(View.GONE);
            mTitleTv.setTextColor(Color.WHITE);
            mBackBtn.setImageResource(R.drawable.ic_av_actionbar_close);
        }else {
            mPageVg.setVisibility(View.VISIBLE);
            mButtonListView.setVisibility(View.GONE);
            mInputRl.setVisibility(View.VISIBLE);
            mTitleTv.setTextColor(ContextCompat.getColor(this,R.color.border_infrared));
            mBackBtn.setImageResource(R.drawable.ic_av_action_bar_black_back);
        }
    }
}
