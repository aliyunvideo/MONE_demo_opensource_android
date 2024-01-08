package com.alivc.live.baselive_common;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alivc.live.pusher.AlivcResolutionEnum;

public class LivePushResolutionView extends LinearLayout {

    private SeekBar mResolution;
    private TextView mResolutionText;
    private LinearLayout mCustomResolutionRootView;
    private AlivcResolutionEnum mDefinition = AlivcResolutionEnum.RESOLUTION_720P;
    private OnResolutionChangedListener mOnResolutionChangedListener;
    private EditText mWidth;
    private EditText mHeight;

    public LivePushResolutionView(Context context) {
        this(context, null);
    }

    public LivePushResolutionView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public LivePushResolutionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initListener();
    }

    private void init(Context context) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.push_resolution_view_layout, this, true);
        mResolution = inflate.findViewById(R.id.resolution_seekbar);
        mResolutionText = inflate.findViewById(R.id.resolution_text);
        mWidth = inflate.findViewById(R.id.et_width);
        mHeight = inflate.findViewById(R.id.et_height);
        mCustomResolutionRootView = inflate.findViewById(R.id.custom_resolution_root);
    }

    private void initListener() {
        mResolution.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (progress <= LivePushProgressStep.PROGRESS_0) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_180P;
                    mResolutionText.setText(R.string.setting_resolution_180P);
                } else if (progress > LivePushProgressStep.PROGRESS_0 && progress <= LivePushProgressStep.PROGRESS_16) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_240P;
                    mResolutionText.setText(R.string.setting_resolution_240P);
                } else if (progress > LivePushProgressStep.PROGRESS_16 && progress <= LivePushProgressStep.PROGRESS_33) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_360P;
                    mResolutionText.setText(R.string.setting_resolution_360P);
                } else if (progress > LivePushProgressStep.PROGRESS_33 && progress <= LivePushProgressStep.PROGRESS_50) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_480P;
                    mResolutionText.setText(R.string.setting_resolution_480P);
                } else if (progress > LivePushProgressStep.PROGRESS_50 && progress <= LivePushProgressStep.PROGRESS_66) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_540P;
                    mResolutionText.setText(R.string.setting_resolution_540P);
                } else if (progress > LivePushProgressStep.PROGRESS_66 && progress <= LivePushProgressStep.PROGRESS_75) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_720P;
                    mResolutionText.setText(R.string.setting_resolution_720P);
                } else if (progress > LivePushProgressStep.PROGRESS_75 && progress <= LivePushProgressStep.PROGRESS_90) {
                    mCustomResolutionRootView.setVisibility(View.GONE);
                    mDefinition = AlivcResolutionEnum.RESOLUTION_1080P;
                    mResolutionText.setText(R.string.setting_resolution_1080P);
                } else {
                    mDefinition = AlivcResolutionEnum.RESOLUTION_SELF_DEFINE;
                    mResolutionText.setText(R.string.setting_resolution_self_define);
                    mCustomResolutionRootView.setVisibility(View.VISIBLE);
                }
                if (mOnResolutionChangedListener != null) {
                    mOnResolutionChangedListener.onResolutionChanged(mDefinition);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (progress <= LivePushProgressStep.PROGRESS_0) {
                    seekBar.setProgress(0);
                } else if (progress > LivePushProgressStep.PROGRESS_0 && progress <= LivePushProgressStep.PROGRESS_16) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_16);
                } else if (progress > LivePushProgressStep.PROGRESS_16 && progress <= LivePushProgressStep.PROGRESS_33) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_33);
                } else if (progress > LivePushProgressStep.PROGRESS_33 && progress <= LivePushProgressStep.PROGRESS_50) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_50);
                } else if (progress > LivePushProgressStep.PROGRESS_50 && progress <= LivePushProgressStep.PROGRESS_66) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_66);
                } else if (progress > LivePushProgressStep.PROGRESS_66 && progress <= LivePushProgressStep.PROGRESS_75) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_75);
                } else if (progress > LivePushProgressStep.PROGRESS_75 && progress <= LivePushProgressStep.PROGRESS_90) {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_90);
                } else {
                    seekBar.setProgress(LivePushProgressStep.PROGRESS_100);
                }
            }
        });
    }

    public void setResolution(AlivcResolutionEnum resolution) {
        if (resolution == AlivcResolutionEnum.RESOLUTION_180P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_0);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_180P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_240P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_16);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_240P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_360P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_33);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_360P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_480P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_50);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_480P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_540P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_66);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_540P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_720P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_75);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_720P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_1080P) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_90);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_1080P));
        } else if (resolution == AlivcResolutionEnum.RESOLUTION_SELF_DEFINE) {
            mResolution.setProgress(LivePushProgressStep.PROGRESS_100);
            mResolutionText.setText(getResources().getString(R.string.setting_resolution_self_define));
            int resolutionWidth = AlivcResolutionEnum.getResolutionWidth(resolution, null);
            int resolutionHeight = AlivcResolutionEnum.getResolutionHeight(resolution, null);
            mWidth.setText(String.valueOf(resolutionWidth));
            mHeight.setText(String.valueOf(resolutionHeight));
        }
    }

    public int getSelfDefineWidth() {
        return Integer.parseInt(mWidth.getText().toString());
    }

    public int getSelfDefineHeight() {
        return Integer.parseInt(mHeight.getText().toString());
    }

    public void setOnResolutionChangedListener(OnResolutionChangedListener listener) {
        this.mOnResolutionChangedListener = listener;
    }

    public interface OnResolutionChangedListener {
        void onResolutionChanged(AlivcResolutionEnum resolutionEnum);
    }
}