package com.aliyun.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.interactive_common.R;
import com.aliyun.interactive_common.listener.ConnectionLostListener;

public class ConnectionLostTipsView extends ConstraintLayout {

    private final Context mContext;
    private final View inflate;
    private TextView mConfirmTextView;
    private AUILiveDialog mAUILiveDialog;
    private ConnectionLostListener mConnectionLostListener;

    public ConnectionLostTipsView(@NonNull Context context) {
        this(context,null);
    }

    public ConnectionLostTipsView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ConnectionLostTipsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        mAUILiveDialog = new AUILiveDialog(context);
        inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_connect_lost_tips_view, this, true);
        initView();
        initListener();

        mAUILiveDialog.setContentView(this);
    }

    private void initView(){
        mConfirmTextView = inflate.findViewById(R.id.tv_confirm);
    }

    private void initListener(){
        mConfirmTextView.setOnClickListener(view -> {
            if(mConnectionLostListener != null){
                mConnectionLostListener.onConfirm();
            }
        });
    }

    public void show(){
        mAUILiveDialog.show();
    }

    public void hide(){
        mAUILiveDialog.dismiss();
    }

    public void setConnectionLostListener(ConnectionLostListener listener){
        this.mConnectionLostListener = listener;
    }
}
