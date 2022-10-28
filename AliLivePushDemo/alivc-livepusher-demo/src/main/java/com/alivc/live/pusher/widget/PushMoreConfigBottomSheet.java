package com.alivc.live.pusher.widget;

import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PREVIEW_MIRROR;
import static com.alivc.live.pusher.AlivcLivePushConstants.DEFAULT_VALUE_PUSH_MIRROR;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.alivc.live.pusher.AlivcPreviewDisplayMode;
import com.alivc.live.pusher.AlivcQualityModeEnum;
import com.alivc.live.pusher.demo.Common;
import com.alivc.live.pusher.demo.R;
import com.alivc.live.commonbiz.SharedPreferenceUtils;
import com.aliyun.aio.avbaseui.avdialog.AVBaseBottomSheetDialog;

public class PushMoreConfigBottomSheet extends AVBaseBottomSheetDialog implements View.OnClickListener ,CompoundButton.OnCheckedChangeListener{

    private EditText mTargetRate = null;
    private EditText mMinRate = null;
    private Switch mPushMirror;
    private Switch mPreviewMirror;
    private TextView mPreviewMode;
    private OnMoreConfigListener mOnMoreConfigListener;
    private int mQualityMode = 0;
    private int mDisplayFit;
    private View mConfigListLn;
    private View mOrientationLn;
    private View mRootView;
    private View mDisplayModeFull;
    private View mDisplayModeFit;
    private View mDisplayModeCut;
    private View mOrientationBack;

    public PushMoreConfigBottomSheet(Context context) {
        super(context);
    }

    public PushMoreConfigBottomSheet(Context context, int theme) {
        super(context, theme);
    }

    protected PushMoreConfigBottomSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected View getContentView() {
        mRootView = getLayoutInflater().inflate(R.layout.push_more, null, false);
        mTargetRate = mRootView.findViewById(R.id.target_rate_edit);
        mMinRate = mRootView.findViewById(R.id.min_rate_edit);

        mPushMirror = mRootView.findViewById(R.id.push_mirror_switch);
        mPreviewMirror = mRootView.findViewById(R.id.preview_mirror_switch);
        mPreviewMode = mRootView.findViewById(R.id.setting_display_mode);
        mPushMirror.setChecked(SharedPreferenceUtils.isPushMirror(getContext().getApplicationContext(), DEFAULT_VALUE_PUSH_MIRROR));
        mPreviewMirror.setChecked(SharedPreferenceUtils.isPreviewMirror(getContext().getApplicationContext(), DEFAULT_VALUE_PREVIEW_MIRROR));
        mPushMirror.setOnCheckedChangeListener(this);
        mPreviewMirror.setOnCheckedChangeListener(this);


        mPreviewMode.setOnClickListener(this);
        mTargetRate.setText(String.valueOf(SharedPreferenceUtils.getTargetBit(getContext().getApplicationContext())));
        mMinRate.setText(String.valueOf(SharedPreferenceUtils.getMinBit(getContext().getApplicationContext())));

        mTargetRate.setHint(String.valueOf(SharedPreferenceUtils.getHintTargetBit(getContext().getApplicationContext())));
        mMinRate.setHint(String.valueOf(SharedPreferenceUtils.getHintMinBit(getContext().getApplicationContext())));
        if (mQualityMode != AlivcQualityModeEnum.QM_CUSTOM.getQualityMode()) {
            mTargetRate.setFocusable(false);
            mMinRate.setFocusable(false);
            mTargetRate.setFocusableInTouchMode(false);
            mMinRate.setFocusableInTouchMode(false);
            mMinRate.setTextColor(Color.GRAY);
            mTargetRate.setTextColor(Color.GRAY);
        } else {
            mMinRate.setBackgroundColor(Color.WHITE);
            mTargetRate.setBackgroundColor(Color.WHITE);
            mTargetRate.setFocusable(true);
            mMinRate.setFocusable(true);
            mTargetRate.setFocusableInTouchMode(true);
            mMinRate.setFocusableInTouchMode(true);
            mTargetRate.requestFocus();
            mMinRate.requestFocus();
        }
        mConfigListLn = mRootView.findViewById(R.id.config_list);
        mOrientationLn = mRootView.findViewById(R.id.orientation_list);
        mDisplayModeFull = mRootView.findViewById(R.id.full);
        mDisplayModeFit = mRootView.findViewById(R.id.fit);
        mDisplayModeCut = mRootView.findViewById(R.id.cut);
        mOrientationBack = mRootView.findViewById(R.id.back);
        mDisplayModeFull.setOnClickListener(this);
        mDisplayModeFit.setOnClickListener(this);
        mDisplayModeCut.setOnClickListener(this);
        mOrientationBack.setOnClickListener(this);

        mRootView.findViewById(R.id.cancel).setOnClickListener(this);
        mRootView.findViewById(R.id.confirm_button).setOnClickListener(this);
        mDisplayFit = SharedPreferenceUtils.getDisplayFit(getContext().getApplicationContext(),
                AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT.getPreviewDisplayMode());

        if (mDisplayFit == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL.getPreviewDisplayMode()) {
            onClick(mDisplayModeFull);
        } else if (mDisplayFit == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT.getPreviewDisplayMode()) {
            onClick(mDisplayModeFit);
        } else if (mDisplayFit == AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL.getPreviewDisplayMode()) {
            onClick(mDisplayModeCut);
        }

        return mRootView;
    }

    @Override
    protected ViewGroup.LayoutParams getContentLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(),304));
    }


    public void setQualityMode(int mode) {
        this.mQualityMode = mode;
        if (mTargetRate == null || mMinRate == null) {
            return;
        }
        if (mQualityMode != AlivcQualityModeEnum.QM_CUSTOM.getQualityMode()) {
            mTargetRate.setFocusable(false);
            mMinRate.setFocusable(false);
            mTargetRate.setFocusableInTouchMode(false);
            mMinRate.setFocusableInTouchMode(false);
            mMinRate.setBackgroundColor(Color.GRAY);
            mTargetRate.setBackgroundColor(Color.GRAY);
        } else {
            mMinRate.setBackgroundColor(Color.WHITE);
            mTargetRate.setBackgroundColor(Color.WHITE);
            mTargetRate.setFocusable(true);
            mMinRate.setFocusable(true);
            mTargetRate.setFocusableInTouchMode(true);
            mMinRate.setFocusableInTouchMode(true);
            mTargetRate.requestFocus();
            mMinRate.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.confirm_button || id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.setting_display_mode) {
            mConfigListLn.setVisibility(View.GONE);
            mOrientationLn.setVisibility(View.VISIBLE);
        } else if (id == R.id.back) {
            mOrientationLn.setVisibility(View.GONE);
            mConfigListLn.setVisibility(View.VISIBLE);
        } else if (id == R.id.full) {
            mRootView.findViewById(R.id.cut_fit).setVisibility(View.GONE);
            mRootView.findViewById(R.id.cut_selected).setVisibility(View.GONE);
            mRootView.findViewById(R.id.full_fit).setVisibility(View.VISIBLE);
            mPreviewMode.setText(R.string.display_mode_full);
            if (mOnMoreConfigListener != null) {
                mOnMoreConfigListener.onDisplayModeChanged(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL);
            }
            SharedPreferenceUtils.setDisplayFit(getContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_SCALE_FILL.getPreviewDisplayMode());
        } else if (id == R.id.fit) {
            mRootView.findViewById(R.id.full_fit).setVisibility(View.GONE);
            mRootView.findViewById(R.id.cut_selected).setVisibility(View.GONE);
            mRootView.findViewById(R.id.cut_fit).setVisibility(View.VISIBLE);
            mPreviewMode.setText(R.string.display_mode_fit);
            if (mOnMoreConfigListener != null) {
                mOnMoreConfigListener.onDisplayModeChanged(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT);
            }
            SharedPreferenceUtils.setDisplayFit(getContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FIT.getPreviewDisplayMode());
        } else if (id == R.id.cut) {
            mRootView.findViewById(R.id.full_fit).setVisibility(View.GONE);
            mRootView.findViewById(R.id.cut_fit).setVisibility(View.GONE);
            mRootView.findViewById(R.id.cut_selected).setVisibility(View.VISIBLE);
            mPreviewMode.setText(R.string.display_mode_cut);
            if (mOnMoreConfigListener != null) {
                mOnMoreConfigListener.onDisplayModeChanged(AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL);
            }
            SharedPreferenceUtils.setDisplayFit(getContext(), AlivcPreviewDisplayMode.ALIVC_LIVE_PUSHER_PREVIEW_ASPECT_FILL.getPreviewDisplayMode());

        }


    }

    public void setOnMoreConfigListener(OnMoreConfigListener onMoreConfigListener) {
        mOnMoreConfigListener = onMoreConfigListener;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        try {
            if (id == R.id.push_mirror_switch) {
                if (mOnMoreConfigListener != null) {
                    mOnMoreConfigListener.onPushMirror(isChecked);
                }
                SharedPreferenceUtils.setPushMirror(getContext().getApplicationContext(), isChecked);
            } else if (id == R.id.preview_mirror_switch) {
                if (mOnMoreConfigListener != null) {
                    mOnMoreConfigListener.onPreviewMirror(isChecked);
                }
                SharedPreferenceUtils.setPreviewMirror(getContext().getApplicationContext(), isChecked);
            } else if (id == R.id.autofocus_switch) {
                if (mOnMoreConfigListener != null) {
                    mOnMoreConfigListener.onAutoFocus(isChecked);
                }
                SharedPreferenceUtils.setAutofocus(getContext().getApplicationContext(), isChecked);
            }
        } catch (IllegalStateException e) {
            Common.showDialog(getContext(), e.getMessage());
        }

    }

    public interface OnMoreConfigListener {
        void onDisplayModeChanged(AlivcPreviewDisplayMode mode);

        void onPushMirror(boolean state);

        void onPreviewMirror(boolean state);

        void onAutoFocus(boolean state);

        void onAddDynamic();

        void onRemoveDynamic();
    }
}
