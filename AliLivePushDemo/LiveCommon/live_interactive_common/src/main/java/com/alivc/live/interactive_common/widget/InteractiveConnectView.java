package com.alivc.live.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alivc.live.interactive_common.R;

/**
 * 连麦 View
 */
public class InteractiveConnectView extends LinearLayout {

    private FrameLayout mConnectFrameLayout;
    private TextView mConnectTextView;
    private OnConnectClickListener mOnConnectClickListener;

    public InteractiveConnectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public InteractiveConnectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public InteractiveConnectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.VERTICAL);
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_interactive_connect_view, this, true);
        mConnectFrameLayout = inflate.findViewById(R.id.fl_un_connect);
        mConnectTextView = inflate.findViewById(R.id.tv_connect);

        initListener();
    }

    private void initListener() {
        mConnectTextView.setOnClickListener(view -> {
            if (mOnConnectClickListener != null) {
                mOnConnectClickListener.onConnectClick();
            }
        });
    }

    public void isShow(boolean isShowSurfaceView) {
        mConnectFrameLayout.setVisibility(isShowSurfaceView ? View.INVISIBLE : View.VISIBLE);
    }

    public void setDefaultText(String text) {
        mConnectTextView.setText(text);
    }

    /**
     * 开始连接
     */
    public void connected() {
        connected(getResources().getString(R.string.interact_stop_connect));
    }

    /**
     * 断开连接
     */
    public void unConnected() {
        unConnected(getResources().getString(R.string.interact_start_connect));
    }

    public void connected(String text) {
        mConnectTextView.setText(text);
        mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_interact_live_un_connect_btn_bg));
    }

    public void unConnected(String text) {
        mConnectTextView.setText(text);
        mConnectTextView.setBackground(getResources().getDrawable(R.drawable.shape_pysh_btn_bg));
    }

    public FrameLayout getConnectFrameLayout() {
        return mConnectFrameLayout;
    }

    public TextView getConnectTextView() {
        return mConnectTextView;
    }


    public void setConnectClickListener(OnConnectClickListener listener) {
        this.mOnConnectClickListener = listener;
    }

    /**
     * 连麦按钮点击监听
     */
    public interface OnConnectClickListener {
        void onConnectClick();
    }
}
