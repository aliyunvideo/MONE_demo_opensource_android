package com.aliyun.aio.avbaseui.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.aio.avbaseui.R;

public class AVLoadingDialog extends Dialog {
    private Context context;
    private ImageView mIconView;
    private TextView mTipView;
    private CharSequence mTipMsg;

    public AVLoadingDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.av_loading_dialog_layout, null, false);
        mIconView = view.findViewById(R.id.av_loading_dialog_icon);
        mTipView = view.findViewById(R.id.av_loading_dialog_tip);
        if (mTipMsg != null) {
            mTipView.setVisibility(View.VISIBLE);
            mTipView.setText(mTipMsg);
        } else {
            mTipView.setVisibility(View.GONE);
        }
        setContentView(view);

        Window dialogWindow = getWindow();
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.alpha = 1.0f;
        lp.dimAmount = 0.0f;
        lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        lp.verticalMargin = 0.3f;
        dialogWindow.setAttributes(lp);

        setCancelable(false);
        setCanceledOnTouchOutside(false);

        startAnimation();
    }

    private void startAnimation() {
        if (mIconView == null || mIconView.getAnimation() != null) {
            return;
        }

        RotateAnimation rotateAnimation = new RotateAnimation(0f, 360f, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        //设置动画持续时长
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        //设置动画的重复模式：反转REVERSE和重新开始RESTART
        rotateAnimation.setRepeatMode(ScaleAnimation.RESTART);
        //设置动画播放次数
        rotateAnimation.setRepeatCount(ScaleAnimation.INFINITE);

        //开始动画
        mIconView.startAnimation(rotateAnimation);
    }

    private void stopAnimation() {
        if (mIconView == null || mIconView.getAnimation() == null) {
            return;
        }
        mIconView.clearAnimation();
    }

    public AVLoadingDialog tip(CharSequence tip) {
        mTipMsg = tip;
        return this;
    }

    @Override
    public void show() {
        super.show();
        startAnimation();
    }

    @Override
    public void dismiss() {
        stopAnimation();
        super.dismiss();
    }
}
