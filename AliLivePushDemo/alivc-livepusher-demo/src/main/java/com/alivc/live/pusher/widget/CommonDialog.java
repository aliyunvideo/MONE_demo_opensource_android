package com.alivc.live.pusher.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alivc.live.pusher.demo.R;
import com.alivc.live.utils.TextFormatUtil;


public class CommonDialog extends Dialog {
    private TextView titleText;
    private TextView tipConetnt;
    private LinearLayout contentLayout;
    private FrameLayout contentContainer2;
    private ScrollView contentContainer;
    private FrameLayout expandContainer;
    private View btnTopLine;
    private TextView cancelButton;
    private View btnLine;
    private TextView confirmButton;
    private int buttonCount;

    public CommonDialog(@NonNull Context context) {
        this(context, R.style.DialogStyle);
    }

    public CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.initView();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.setGravity(17);
        LayoutParams params = window.getAttributes();
        params.width = -1;
        params.height = -2;
        window.setAttributes(params);
    }

    public void show() {
        if (!this.isShowing()) {
            if (this.buttonCount == 0) {
                this.btnLine.setVisibility(View.GONE);
                this.btnTopLine.setVisibility(View.GONE);
                this.setDialogCancelable(true);
            } else if (this.buttonCount == 1) {
                this.btnLine.setVisibility(View.GONE);
                this.btnTopLine.setVisibility(View.VISIBLE);
            } else if (this.buttonCount == 2) {
                this.btnLine.setVisibility(View.VISIBLE);
                this.btnTopLine.setVisibility(View.VISIBLE);
            }

            super.show();
        }

    }

    public void dismiss() {
        if (this.isShowing()) {
            super.dismiss();
        }

    }

    private void initView() {
        View tipDialogView = this.getLayoutInflater().inflate(R.layout.common_dialog, (ViewGroup)null);
        this.titleText = (TextView)tipDialogView.findViewById(R.id.dialog_title);
        this.contentLayout = (LinearLayout)tipDialogView.findViewById(R.id.dialog_content_layout);
        this.contentContainer2 = (FrameLayout)tipDialogView.findViewById(R.id.dialog_content_container2);
        this.contentContainer = (ScrollView)tipDialogView.findViewById(R.id.dialog_content_container);
        this.tipConetnt = (TextView)tipDialogView.findViewById(R.id.dialog_tip_content);
        this.expandContainer = (FrameLayout)tipDialogView.findViewById(R.id.dialog_expand_content_container);
        this.btnTopLine = tipDialogView.findViewById(R.id.dialog_btn_top_line);
        this.cancelButton = (TextView)tipDialogView.findViewById(R.id.dialog_cancel_btn);
        this.btnLine = tipDialogView.findViewById(R.id.dialog_btn_line);
        this.confirmButton = (TextView)tipDialogView.findViewById(R.id.dialog_confirm_btn);
        View bottomSpace = tipDialogView.findViewById(R.id.dialog_bottom_space);
        int space = (int)((double)DensityUtil.getDisplayMetrics(this.getContext()).heightPixels * 0.05D);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, space);
        bottomSpace.setLayoutParams(params);
        this.setContentView(tipDialogView);
    }

    public CommonDialog setDialogCancelable(boolean cancelable) {
        this.setCancelable(cancelable);
        this.setCanceledOnTouchOutside(cancelable);
        return this;
    }

    public CommonDialog setDialogTitle(int title) {
        CharSequence titleStr = title == 0 ? "" : TextFormatUtil.getTextFormat(this.getContext(), title);
        return this.setDialogTitle(titleStr);
    }

    public CommonDialog setDialogTitle(CharSequence title) {
        if (this.titleText != null && !TextUtils.isEmpty(title)) {
            this.titleText.setText(title);
            this.titleText.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CommonDialog setDialogTitleGravity(int gravity) {
        if (this.titleText != null) {
            this.titleText.setGravity(gravity);
        }

        return this;
    }

    public CommonDialog setDialogTitleMaxLines(int lines) {
        if (this.titleText != null && lines > 0) {
            this.setDialogTitleLinesNoLimit();
            this.titleText.setMaxLines(lines);
            this.titleText.setEllipsize(TruncateAt.END);
        }

        return this;
    }

    public CommonDialog setDialogTitleLinesNoLimit() {
        if (this.titleText != null) {
            this.titleText.setSingleLine(false);
        }

        return this;
    }

    public CommonDialog setDialogContentGravity(int gravity) {
        if (this.tipConetnt != null) {
            this.tipConetnt.setGravity(gravity);
        }

        return this;
    }

    public CommonDialog setDialogContent(int tip) {
        CharSequence tipStr = tip == 0 ? "" : TextFormatUtil.getTextFormat(this.getContext(), tip);
        return this.setDialogContent(tipStr);
    }

    public CommonDialog setDialogContent(CharSequence tip) {
        if (this.tipConetnt != null && !TextUtils.isEmpty(tip)) {
            this.tipConetnt.setText(tip);
            this.contentLayout.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CommonDialog setDialogContentView(View contentView) {
        if (this.contentContainer != null && contentView != null) {
            this.contentContainer.removeAllViews();
            this.contentContainer.addView(contentView);
            this.contentLayout.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CommonDialog setDialogContentView2(View contentView) {
        if (this.contentContainer2 != null && contentView != null) {
            this.contentContainer2.removeAllViews();
            this.contentContainer2.addView(contentView);
        }

        return this;
    }

    public CommonDialog setExpandView(View expandView) {
        if (this.expandContainer != null && expandView != null) {
            this.expandContainer.removeAllViews();
            this.expandContainer.addView(expandView);
            this.expandContainer.setVisibility(View.VISIBLE);
        }

        return this;
    }

    public CommonDialog setCancelButton(int cancel, OnClickListener listener) {
        return this.setCancelButton(cancel, 0, listener);
    }

    public CommonDialog setCancelButton(int cancel, int color, OnClickListener listener) {
        CharSequence cancelStr = cancel == 0 ? "" : TextFormatUtil.getTextFormat(this.getContext(), cancel);
        return this.setCancelButton(cancelStr, color, listener);
    }

    public CommonDialog setCancelButton(CharSequence cancel, OnClickListener listener) {
        this.setCancelButton(cancel, 0, listener);
        return this;
    }

    public CommonDialog setCancelButton(CharSequence cancel, int color, final OnClickListener listener) {
        if (this.cancelButton != null && !TextUtils.isEmpty(cancel)) {
            this.cancelButton.setText(cancel);
            if (color != 0) {
                this.cancelButton.setTextColor(color);
            }

            this.cancelButton.setVisibility(View.VISIBLE);
            ++this.buttonCount;
            if (this.cancelButton != null) {
                this.cancelButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CommonDialog.this.dismiss();
                        if (listener != null) {
                            listener.onClick(CommonDialog.this, -2);
                        }

                    }
                });
            }
        }

        return this;
    }

    public CommonDialog setConfirmButton(int confirm, OnClickListener listener) {
        return this.setConfirmButton(confirm, 0, listener);
    }

    public CommonDialog setConfirmButton(int confirm, int color, OnClickListener listener) {
        CharSequence confirmStr = confirm == 0 ? "" : TextFormatUtil.getTextFormat(this.getContext(), confirm);
        return this.setConfirmButton(confirmStr, color, listener);
    }

    public CommonDialog setConfirmButton(CharSequence confirm, OnClickListener listener) {
        this.setConfirmButton(confirm, 0, listener);
        return this;
    }

    public CommonDialog setConfirmButton(CharSequence confirm, int color, final OnClickListener listener) {
        if (this.confirmButton != null && !TextUtils.isEmpty(confirm)) {
            this.confirmButton.setText(confirm);
            if (color != 0) {
                this.confirmButton.setTextColor(color);
            }

            this.confirmButton.setVisibility(View.VISIBLE);
            ++this.buttonCount;
            this.confirmButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    CommonDialog.this.dismiss();
                    if (listener != null) {
                        listener.onClick(CommonDialog.this, -1);
                    }

                }
            });
        }

        return this;
    }

}
