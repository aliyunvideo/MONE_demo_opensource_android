package com.alivc.live.interactive_common;

import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FIVE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_FOUR;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_ONE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_THREE;
import static com.alivc.live.pusher.AlivcVideoEncodeGopEnum.GOP_TWO;

import android.app.DownloadManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import com.alivc.live.commonbiz.ResourcesDownload;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.interactive_common.utils.LivePushGlobalConfig;
import com.aliyun.aio.avbaseui.widget.AVLoadingDialog;
import com.aliyun.aio.avbaseui.widget.AVToast;

public class InteractiveSettingActivity extends AppCompatActivity {

    private static final int PROGRESS_20 = 20;
    private static final int PROGRESS_40 = 40;
    private static final int PROGRESS_60 = 60;
    private static final int PROGRESS_80 = 80;
    private static final int PROGRESS_100 = 100;

    private ImageView mBackImageView;
    private Switch mMultiInteractSwitch;
    private Button mCommitButton;
    private Switch mAudioOnlySwitch;
    private Switch mHardwareDecodeSwitch;
    private SeekBar mGopSeekBar;
    private TextView mGopTextView;
    private Switch mExternVideoSwitch;
    private AVLoadingDialog mLoadingDialog;

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
        mGopSeekBar = findViewById(R.id.gop_seekbar);
        mExternVideoSwitch = findViewById(R.id.extern_video_switch);
        mGopTextView = findViewById(R.id.gop_text);

        mMultiInteractSwitch.setChecked(LivePushGlobalConfig.IS_MULTI_INTERACT);
        mAudioOnlySwitch.setChecked(LivePushGlobalConfig.IS_AUDIO_ONLY);
        mHardwareDecodeSwitch.setChecked(LivePushGlobalConfig.VIDEO_ENCODE_HARD);
        mExternVideoSwitch.setChecked(LivePushGlobalConfig.ENABLE_EXTERN_AV);
    }

    private void initListener() {
        mBackImageView.setOnClickListener(view -> {
            finish();
        });
        mCommitButton.setOnClickListener(view -> {
            finish();
        });
        mMultiInteractSwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.IS_MULTI_INTERACT = b);
        mAudioOnlySwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.IS_AUDIO_ONLY = b);
        mHardwareDecodeSwitch.setOnCheckedChangeListener((compoundButton, b) -> LivePushGlobalConfig.VIDEO_ENCODE_HARD = b);
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