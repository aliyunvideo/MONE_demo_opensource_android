package com.aliyun.svideo.template.sample.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

/**
 * 监听输入法返回键
 */
public class BackEditText extends AppCompatEditText {
    private OnKeyBoardHideListener onKeyBoardHideListener;

    public BackEditText(Context context) {
        super(context);
    }

    public BackEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1 && onKeyBoardHideListener != null) {
            onKeyBoardHideListener.onKeyHide();
        }
        return super.onKeyPreIme(keyCode, event);
    }

    public void setOnKeyBoardHideListener(OnKeyBoardHideListener onKeyBoardHideListener) {
        this.onKeyBoardHideListener = onKeyBoardHideListener;
    }

    public interface OnKeyBoardHideListener {
        void onKeyHide();
    }
}
