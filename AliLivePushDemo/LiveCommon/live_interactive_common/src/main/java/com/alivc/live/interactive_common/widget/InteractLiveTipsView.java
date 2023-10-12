package com.alivc.live.interactive_common.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alivc.live.interactive_common.R;
import com.alivc.live.interactive_common.listener.InteractLiveTipsViewListener;

public class InteractLiveTipsView extends ConstraintLayout {

    private final Context mContext;
    private final View inflate;
    private TextView mCancelTextView;
    private TextView mConfirmTextView;
    private InteractLiveTipsViewListener mInteractLiveTipsViewListener;
    private TextView mTipsTextView;
    private EditText mInputEditText;
    private ImageView mClearImageView;
    private boolean mShowInputView;
    private boolean mShowQrIcon;

    public InteractLiveTipsView(@NonNull Context context) {
        this(context,null);
    }

    public InteractLiveTipsView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public InteractLiveTipsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_interact_live_tips_view, this, true);
        initView();
        initListener();
    }

    public void showQrIcon(boolean showQrIcon) {
        this.mShowQrIcon = showQrIcon;
        if (showQrIcon) {
            mClearImageView.setVisibility(View.VISIBLE);
        }
        mClearImageView.setImageResource(showQrIcon ? R.drawable.scan_icon : R.drawable.ic_close);
    }

    private void initView(){
        mCancelTextView = inflate.findViewById(R.id.tv_cancel);
        mConfirmTextView = inflate.findViewById(R.id.tv_confirm);
        mTipsTextView = inflate.findViewById(R.id.tv_tips);
        mInputEditText = inflate.findViewById(R.id.et_input);
        mClearImageView = inflate.findViewById(R.id.iv_clear);
    }

    private void initListener(){
        mCancelTextView.setOnClickListener(view -> {
            if(mInteractLiveTipsViewListener != null){
                mInteractLiveTipsViewListener.onCancel();
            }

        });
        mConfirmTextView.setOnClickListener(view -> {
            if(mInteractLiveTipsViewListener != null){
                if(mInputEditText.getVisibility() == View.VISIBLE){
                    mInteractLiveTipsViewListener.onInputConfirm(mInputEditText.getText().toString());
                }else{
                    mInteractLiveTipsViewListener.onConfirm();
                }
            }
        });
        mClearImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShowQrIcon) {
                    if (mInteractLiveTipsViewListener != null) {
                        mInteractLiveTipsViewListener.onQrClick();
                    }
                } else {
                    mInputEditText.setText("");
                }
            }
        });
    }

    public void showInputView(boolean showInputView) {
        this.mShowInputView = showInputView;
        if (mShowQrIcon) {
            mInputEditText.setVisibility(View.VISIBLE);
            mClearImageView.setVisibility(View.VISIBLE);
        } else {
            if(mShowInputView){
                mInputEditText.setVisibility(View.VISIBLE);
                mClearImageView.setVisibility(View.VISIBLE);
                mTipsTextView.setVisibility(View.GONE);
            }else{
                mTipsTextView.setVisibility(View.VISIBLE);
                mInputEditText.setVisibility(View.GONE);
                mClearImageView.setVisibility(View.GONE);
            }
        }
    }

    public void setContent(String content){
        if(mShowInputView){
            mInputEditText.setHint(content);
        }else{
            mTipsTextView.setText(content);
        }
    }

    public void setOnInteractLiveTipsViewListener(InteractLiveTipsViewListener listener){
        this.mInteractLiveTipsViewListener = listener;
    }

    public void setText(String qrUrl) {
        mInputEditText.setText(qrUrl);
    }
}
