package com.aliyun.svideo.template.sample.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.aliyun.svideo.template.sample.ui.view.BackEditText;
import com.aliyun.svideo.template.sample.R;

/**
 * 文本输入
 */
public class TemplateTextEditDialogFragment extends DialogFragment {
    private View mRootView;
    private BackEditText mEdtInput;
    private TextView mTvShow;
    private TextView mTvFinish;
    private OnResultListener mOnResultListener;
    private String mText;

    public interface OnResultListener {
        void onResult(String text);
    }

    public void setText(final String text) {
        mText = text;
    }

    public void setOnResultListener(final OnResultListener onResultListener) {
        mOnResultListener = onResultListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogFullScreen);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.alivc_editor_template_text_edit, null);
        mTvShow = mRootView.findViewById(R.id.tv_text_show);
        mTvShow.setText(mText);
        mTvShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mEdtInput = mRootView.findViewById(R.id.edt_text_input);
        mEdtInput.setText(mText);
        mEdtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mText = charSequence.toString();
                mTvShow.setText(mText);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mEdtInput.setOnKeyBoardHideListener(new BackEditText.OnKeyBoardHideListener() {
            @Override
            public void onKeyHide() {
                dismiss();
            }
        });
        mEdtInput.requestFocus();

        mTvFinish = mRootView.findViewById(R.id.tv_finish);
        mTvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnResultListener != null) {
                    mOnResultListener.onResult(mText);
                }
                dismiss();
            }
        });
        return mRootView;
    }

}
