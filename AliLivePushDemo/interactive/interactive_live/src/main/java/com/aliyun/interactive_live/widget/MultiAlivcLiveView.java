package com.aliyun.interactive_live.widget;

import android.widget.FrameLayout;
import android.widget.TextView;

public class MultiAlivcLiveView {

    private FrameLayout mUnConnectFrameLayout;
    private FrameLayout mSmallFrameLayout;
    private TextView mConnectTextView;

    public MultiAlivcLiveView(FrameLayout unConnectFrameLayout,FrameLayout smallFrameLayout,TextView connectTextView){
        this.mUnConnectFrameLayout = unConnectFrameLayout;
        this.mSmallFrameLayout = smallFrameLayout;
        this.mConnectTextView = connectTextView;
    }

    public FrameLayout getUnConnectFrameLayout() {
        return mUnConnectFrameLayout;
    }

    public void setUnConnectFrameLayout(FrameLayout mUnConnectFrameLayout) {
        this.mUnConnectFrameLayout = mUnConnectFrameLayout;
    }

    public FrameLayout getSmallFrameLayout() {
        return mSmallFrameLayout;
    }

    public void setSmallFrameLayout(FrameLayout mSmallFrameLayout) {
        this.mSmallFrameLayout = mSmallFrameLayout;
    }

    public TextView getConnectTextView() {
        return mConnectTextView;
    }

    public void setConnectTextView(TextView mConnectTextView) {
        this.mConnectTextView = mConnectTextView;
    }
}
