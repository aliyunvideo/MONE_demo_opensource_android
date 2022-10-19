package com.alivc.live.pusher.widget;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.alivc.live.pusher.demo.R;
import com.aliyun.aio.avbaseui.avdialog.AVBaseBottomSheetDialog;

import java.util.List;

public class PushConfigBottomSheet extends AVBaseBottomSheetDialog {
    private OptionSelectorView mOptionSelectorView;
    private int mSelectIndex ;
    private String mTips;

    public PushConfigBottomSheet(Context context) {
        super(context);
    }

    public PushConfigBottomSheet(Context context, int theme) {
        super(context, theme);
    }

    protected PushConfigBottomSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    public void setData(List<String> data, int defaultPosition) {
        mSelectIndex = defaultPosition;
        mTips = data.get(defaultPosition);
        mOptionSelectorView.setData(data, defaultPosition);
    }

    @Override
    protected View getContentView() {
        View rootView = getLayoutInflater().inflate(R.layout.push_config_dialog, null, false);
        mOptionSelectorView = rootView.findViewById(R.id.optionSelectorView);
        mOptionSelectorView.setOnItemSelectedListener(new OptionSelectorView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(String data, int position) {
                mSelectIndex = position;
                mTips = data;
            }
        });
        rootView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rootView.findViewById(R.id.confirm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnPushConfigSelectorListener != null) {
                    mOnPushConfigSelectorListener.confirm(mTips,mSelectIndex);
                }
                dismiss();
            }
        });
        return rootView;
    }

    @Override
    protected ViewGroup.LayoutParams getContentLayoutParams() {
        return new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dip2px(getContext(), 304));
    }

    private OnPushConfigSelectorListener mOnPushConfigSelectorListener;

    public void setOnPushConfigSelectorListener(OnPushConfigSelectorListener onPushConfigSelectorListener) {
        mOnPushConfigSelectorListener = onPushConfigSelectorListener;
    }

    public interface OnPushConfigSelectorListener {
        void confirm(String data,int index);
    }

}
