/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.aio.avbaseui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AVBaseDialogFragment extends DialogFragment {

    private DialogVisibleListener dismissListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();

        if (window != null) {
            //设置dialog动画
            window.getAttributes().windowAnimations = R.style.ugsv_bottom_dialog_animation;
        }

    }


    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        Dialog dialog = getDialog();
        window.setGravity(Gravity.BOTTOM);

        DisplayMetrics dpMetrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dpMetrics);
        WindowManager.LayoutParams p = window.getAttributes();


        if (dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            /*dpm获取的高度不准确，不要使用这种方式*/
            // dialog.getWindow().setLayout((int) (dpMetrics.widthPixels), (int) (dpMetrics.heightPixels ));

            // 适配传音CF8手机
            if (Build.MODEL.toUpperCase().contains("TECNO CF8")) {

                p.height = dpMetrics.heightPixels - getTECNOCF8NotchAndNaviHeight();
                dialog.getWindow().setLayout((int) (dpMetrics.widthPixels), p.height);

            }
        }
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        /*
            解决crash:java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
            原因:after onSaveInstanceState invoke commit,而 show 会触发 commit 操作
            fragment is added and its state has already been saved，
            Any operations that would change saved state should not be performed if this method returns true
         */
        if (isStateSaved()) {
            return;
        }
        super.show(manager, tag);
        if (dismissListener != null) {
            dismissListener.onDialogShow();
        }
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        if (dismissListener != null) {
            dismissListener.onDialogShow();
        }
        return super.show(transaction, tag);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDialogDismiss();
        }
    }

    public void setDismissListener(DialogVisibleListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface DialogVisibleListener {
        void onDialogDismiss();

        void onDialogShow();
    }

    /**
     * 获取传音CF8手机刘海和虚拟按键的总高度
     *
     * @return px
     */
    private int getTECNOCF8NotchAndNaviHeight() {

        int height = 0;
        if (Build.MODEL.toUpperCase().contains("TECNO CF8")) {
            //传音技术反馈这款手机的刘海是自定义的，不能通过反射得到高度
            //只能通过界面自己计算出来刘海和虚拟底部的总高度
            height = 72;
        }
        return height;
    }
}
