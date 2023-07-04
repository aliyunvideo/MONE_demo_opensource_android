package com.alivc.live.interactive_common;

import static com.alivc.live.pusher.AlivcFpsEnum.FPS_10;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_12;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_15;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_20;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_25;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_30;
import static com.alivc.live.pusher.AlivcFpsEnum.FPS_8;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FIVE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FOUR;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_ONE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_THREE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_TWO;

import android.app.DownloadManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.alivc.live.pusher.AlivcResolutionEnum;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;

public class InteractiveSettingActivity extends AppCompatActivity {

    private static final int PROGRESS_0 = 0;
    private static final int PROGRESS_16 = 16;
    private static final int PROGRESS_20 = 20;
    private static final int PROGRESS_33 = 33;
    private static final int PROGRESS_40 = 40;
    private static final int PROGRESS_50 = 50;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_66 = 66;
    private static final int PROGRESS_80 = 80;
    private static final int PROGRESS_90 = 90;
    private static final int PROGRESS_100 = 100;

    private ImageView mBackImageView;
    private Switch mMultiInteractSwitch;
    private Button mCommitButton;
    private Switch mAudioOnlySwitch;
    private Switch mHardwareDecodeSwitch;
    private Switch mH265Switch;
    private SeekBar mGopSeekBar;
    private TextView mGopTextView;
    private Switch mExternVideoSwitch;
    private AVLoadingDialog mLoadingDialog;

    private SeekBar mResolution;
    private TextView mResolutionText;
    private EditText mTargetRateEditText;
    private SeekBar mMinFps;
    private TextView mMinFpsText;
    private SeekBar mFps;
    private TextView mFpsText;
    private Switch mBeautySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interactive_setting);

        initView();
        initListener();
    }

    private void initView() {
        mBackImageView = findViewById(R.id.iv_back);
        mCommitButton = findViewById(R.id.btn_commit);
        mMultiInteractSwitch = findViewById(R.id.multi_interaction_control);
        mAudioOnlySwitch = findViewById(R.id.audio_only_switch);
        mHardwareDecodeSwitch = findViewById(R.id.hardware_decode_switch);
        mH265Switch = findViewById(R.id.h265_switch);
        mGopSeekBar = findViewById(R.id.gop_seekbar);
        mExternVideoSwitch = findViewById(R.id.extern_video_switch);
        mGopTextView = findViewById(R.id.gop_text);
        mBeautySwitch = findViewById(R.id.beauty_switch);

        mBeautySwitch.setChecked(LivePushGlobalConfig.ENABLE_BEAUTY);
        mMultiInteractSwitch.setChecked(LivePushGlobalConfig.IS_MULTI_INTERACT);
        mAudioOnlySwitch.setChecked(LivePushGlobalConfig.IS_AUDIO_ONLY);
        mHardwareDecodeSwitch.setChecked(LivePushGlobalConfig.VIDEO_ENCODE_HARD);
        mH265Switch.setChecked(LivePushGlobalConfig.VIDEO_CODEC_H265);
        mExternVideoSwitch.setChecked(LivePushGlobalConfig.ENABLE_EXTERN_AV);

        mResolution = findViewById(R.id.resolution_seekbar);
        mResolutionText = findViewById(R.id.resolution_text);

        mTargetRateEditText = findViewById(R.id.target_rate_edit);
        mTargetRateEditText.setHint(String.valueOf(LivePushGlobalConfig.TARGET_RATE));

        mMinFps = findViewById(R.id.min_fps_seekbar);
        mMinFpsText = findViewById(R.id.min_fps_text);

        mFps = findViewById(R.id.fps_seekbar);
        mFpsText = findViewById(R.id.fps_text);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(view -> {
            finish();
        });
        mCommitButton.setOnClickListener(view -> {
            String targetRate = mTargetRateEditText.getText().toString();
            if (!TextUtils.isEmpty(targetRate)) {
                LivePushGlobalConfig.TARGET_RATE = Integer.parseInt(targetRate);
            }

            finish();
        });
        mMultiInteractSwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.IS_MULTI_INTERACT = b);
        mAudioOnlySwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.IS_AUDIO_ONLY = b);
        mHardwareDecodeSwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.VIDEO_ENCODE_HARD = b);
        mH265Switch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.VIDEO_CODEC_H265 = b);
        mBeautySwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.ENABLE_BEAUTY = b);
        mExternVideoSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            LivePushGlobalConfig.ENABLE_EXTERN_AV = b;
            if (b) {
                ResourcesDownload.downloadCaptureYUV(this, new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(long downloadId) {
                        hideProgressDialog();
                        AVToast.show(InteractiveSettingActivity.this, true, "Download Success");
                    }

                    @Override
                    public void onDownloadProgress(long downloadId, double percent) {

                    }

                    @Override
                    public void onDownloadError(long downloadId, int errorCode, String errorMsg) {
                        hideProgressDialog();
                        AVToast.show(InteractiveSettingActivity.this, true, errorMsg);
                        if (errorCode != DownloadManager.ERROR_FILE_ALREADY_EXISTS) {
                            mExternVideoSwitch.setChecked(false);
                        }
                    }
                });
                showProgressDialog(R.string.waiting_download_video_resources);
            }
        });

        mGopSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress <= PROGRESS_20) {
                    LivePushGlobalConfig.GOP = GOP_ONE;
                    mGopTextView.setText("1/s");
                } else if (progress <= PROGRESS_40) {
                    LivePushGlobalConfig.GOP = GOP_TWO;
                    mGopTextView.setText("2/s");
                } else if (progress <= PROGRESS_60) {
                    LivePushGlobalConfig.GOP = GOP_THREE;
                    mGopTextView.setText("3/s");
                } else if (progress <= PROGRESS_80) {
                    LivePushGlobalConfig.GOP = GOP_FOUR;
                    mGopTextView.setText("4/s");
                } else if (progress <= PROGRESS_100) {
                    LivePushGlobalConfig.GOP = GOP_FIVE;
                    mGopTextView.setText("5/s");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mMinFps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress <= PROGRESS_0) {
                    LivePushGlobalConfig.MIN_FPS = FPS_8;
                    mMinFpsText.setText(String.valueOf(FPS_8.getFps()));
                } else if (progress <= PROGRESS_16) {
                    LivePushGlobalConfig.MIN_FPS = FPS_10;
                    mMinFpsText.setText(String.valueOf(FPS_10.getFps()));
                } else if (progress <= PROGRESS_33) {
                    LivePushGlobalConfig.MIN_FPS = FPS_12;
                    mMinFpsText.setText(String.valueOf(FPS_12.getFps()));
                } else if (progress <= PROGRESS_50) {
                    LivePushGlobalConfig.MIN_FPS = FPS_15;
                    mMinFpsText.setText(String.valueOf(FPS_15.getFps()));
                } else if (progress <= PROGRESS_66) {
                    LivePushGlobalConfig.MIN_FPS = FPS_20;
                    mMinFpsText.setText(String.valueOf(FPS_20.getFps()));
                } else if (progress <= PROGRESS_80) {
                    LivePushGlobalConfig.MIN_FPS = FPS_25;
                    mMinFpsText.setText(String.valueOf(FPS_25.getFps()));
                } else {
                    LivePushGlobalConfig.MIN_FPS = FPS_30;
                    mMinFpsText.setText(String.valueOf(FPS_30.getFps()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mFps.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress <= PROGRESS_0) {
                    LivePushGlobalConfig.FPS = FPS_8;
                    mFpsText.setText(String.valueOf(FPS_8.getFps()));
                } else if (progress <= PROGRESS_16) {
                    LivePushGlobalConfig.FPS = FPS_10;
                    mFpsText.setText(String.valueOf(FPS_10.getFps()));
                } else if (progress <= PROGRESS_33) {
                    LivePushGlobalConfig.FPS = FPS_12;
                    mFpsText.setText(String.valueOf(FPS_12.getFps()));
                } else if (progress <= PROGRESS_50) {
                    LivePushGlobalConfig.FPS = FPS_15;
                    mFpsText.setText(String.valueOf(FPS_15.getFps()));
                } else if (progress <= PROGRESS_66) {
                    LivePushGlobalConfig.FPS = FPS_20;
                    mFpsText.setText(String.valueOf(FPS_20.getFps()));
                } else if (progress <= PROGRESS_80) {
                    LivePushGlobalConfig.FPS = FPS_25;
                    mFpsText.setText(String.valueOf(FPS_25.getFps()));
                } else {
                    LivePushGlobalConfig.FPS = FPS_30;
                    mFpsText.setText(String.valueOf(FPS_30.getFps()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mResolution.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress <= PROGRESS_0) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_180P;
                    mResolutionText.setText("180P");
                } else if (progress <= PROGRESS_20) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_240P;
                    mResolutionText.setText("240P");

                } else if (progress <= PROGRESS_40) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_360P;
                    mResolutionText.setText("360P");

                } else if (progress <= PROGRESS_60) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_480P;
                    mResolutionText.setText("480P");

                } else if (progress <= PROGRESS_80) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_540P;
                    mResolutionText.setText("540P");

                } else if (progress <= PROGRESS_90) {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_720P;
                    mResolutionText.setText("720P");

                } else {
                    LivePushGlobalConfig.RESOLUTION = AlivcResolutionEnum.RESOLUTION_1080P;
                    mResolutionText.setText("1080P");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void showProgressDialog(@StringRes int tipsResId) {
        if (null == mLoadingDialog) {
            mLoadingDialog = new AVLoadingDialog(this).tip(getString(tipsResId));
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }
}