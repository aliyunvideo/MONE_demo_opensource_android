package com.aliyun.interactive_pk;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.aliyun.interactive_common.listener.InteractLiveTipsViewListener;

public class PKLiveTipsView extends ConstraintLayout {

    private final Context mContext;
    private final View inflate;
    private TextView mCancelTextView;
    private TextView mConfirmTextView;
    private InteractLiveTipsViewListener mInteractLiveTipsViewListener;
    private TextView mTipsTextView;
    private EditText mInputUserIdEditText;
    private EditText mInPutRoomIdEditText;
    private ImageView mClearUserIdImageView;
    private ImageView mClearRoomIdImageView;
    private boolean mShowInputView;

    public PKLiveTipsView(@NonNull Context context) {
        this(context, null);
    }

    public PKLiveTipsView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public PKLiveTipsView(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        inflate = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_live_tips_view, this, true);
        initView();
        initListener();
    }

    private void initView() {
        mCancelTextView = inflate.findViewById(R.id.tv_cancel);
        mConfirmTextView = inflate.findViewById(R.id.tv_confirm);
        mTipsTextView = inflate.findViewById(R.id.tv_tips);
        mInputUserIdEditText = inflate.findViewById(R.id.et_input_user_id);
        mInPutRoomIdEditText = inflate.findViewById(R.id.et_input_room_id);
        mClearUserIdImageView = inflate.findViewById(R.id.iv_clear_user_id);
        mClearRoomIdImageView = inflate.findViewById(R.id.iv_clear_room_id);
    }

    private void initListener() {
        mCancelTextView.setOnClickListener(view -> {
            if (mInteractLiveTipsViewListener != null) {
                mInteractLiveTipsViewListener.onCancel();
            }

        });
        mConfirmTextView.setOnClickListener(view -> {
            if (mInteractLiveTipsViewListener != null) {
                if (mInputUserIdEditText.getVisibility() == View.VISIBLE && mInPutRoomIdEditText.getVisibility() == View.VISIBLE) {
                    String userId = mInputUserIdEditText.getText().toString().trim();
                    String roomId = mInPutRoomIdEditText.getText().toString().trim();
                    if(!TextUtils.isEmpty(userId) && !TextUtils.isEmpty(roomId)){
                        mInteractLiveTipsViewListener.onInputConfirm(mInputUserIdEditText.getText().toString() + "=" + mInPutRoomIdEditText.getText().toString());
                    }
                } else {
                    mInteractLiveTipsViewListener.onConfirm();
                }
            }
        });
        mClearUserIdImageView.setOnClickListener(view -> mInputUserIdEditText.setText(""));
        mClearRoomIdImageView.setOnClickListener(view -> mInPutRoomIdEditText.setText(""));
    }

    public void showInputView(boolean showInputView) {
        this.mShowInputView = showInputView;
        if (mShowInputView) {
            mInputUserIdEditText.setVisibility(View.VISIBLE);
            mInPutRoomIdEditText.setVisibility(View.VISIBLE);
            mClearUserIdImageView.setVisibility(View.VISIBLE);
            mClearRoomIdImageView.setVisibility(View.VISIBLE);
            mTipsTextView.setVisibility(View.GONE);
        } else {
            mTipsTextView.setVisibility(View.VISIBLE);
            mInputUserIdEditText.setVisibility(View.GONE);
            mInPutRoomIdEditText.setVisibility(View.GONE);
            mClearUserIdImageView.setVisibility(View.GONE);
            mClearRoomIdImageView.setVisibility(View.GONE);
        }
    }

    public void setContent(String content) {
        if (!mShowInputView) {
            mTipsTextView.setText(content);
        }
    }

    public void setOnInteractLiveTipsViewListener(InteractLiveTipsViewListener listener) {
        this.mInteractLiveTipsViewListener = listener;
    }
}
