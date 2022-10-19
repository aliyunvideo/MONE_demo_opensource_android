package com.aliyun.aio.avbaseui.avdialog;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import com.aliyun.aio.avbaseui.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public abstract class AVBaseBottomSheetDialog extends AVBaseDialog {
    private ViewGroup mBottomSheetContainer;
    private boolean mAnimateToCancel = false;
    private boolean mAnimateToDismiss = false;
    private BottomSheetBehavior<ViewGroup> mBottomSheetBehavior;

    public AVBaseBottomSheetDialog(Context context) {
        super(context,R.style.AIO_BottomSheetDialog);
    }

    public AVBaseBottomSheetDialog(Context context, int theme) {
        super(context, theme);
    }

    protected AVBaseBottomSheetDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void initView(Context context) {
        super.initView(context);
        View container = getLayoutInflater().inflate(R.layout.av_base_dialog_bottom_sheet, null);
        mBottomSheetContainer = container.findViewById(R.id.bottom_sheet_container);
        View contentView = getContentView();
        ViewGroup.LayoutParams contentParams = getContentLayoutParams();
        if (contentView != null && contentParams != null) {
            mBottomSheetContainer.addView(contentView, contentParams);
        }
        mBottomSheetBehavior = new BottomSheetBehavior<>();
        mBottomSheetBehavior.setHideable(true);
        mBottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    if (mAnimateToCancel) {
                        cancel();
                    } else if (mAnimateToDismiss) {
                        dismiss();
                    } else {
                        cancel();
                    }
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
        mBottomSheetBehavior.setPeekHeight(0);
        mBottomSheetBehavior.setSkipCollapsed(true);
        ((CoordinatorLayout.LayoutParams) mBottomSheetContainer.getLayoutParams()).setBehavior(mBottomSheetBehavior);
        container.findViewById(R.id.touch_outside).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING) {
                    return;
                }
                if (mBottomSheetBehavior.isHideable() && isShowing()) {
                    cancel();
                }
            }
        });

        mBottomSheetContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        setContentView(container, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setCancelable(boolean flag) {
        super.setCancelable(flag);
        mBottomSheetBehavior.setHideable(flag);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ViewCompat.requestApplyInsets(mBottomSheetContainer);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void cancel() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            mAnimateToCancel = false;
            super.cancel();
        } else {
            mAnimateToCancel = true;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void dismiss() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN){
            mAnimateToDismiss = false;
            super.dismiss();
        }else {
            mAnimateToDismiss = true;
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void show() {
        super.show();
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            setToExpandWhenShow();
        }
        mAnimateToCancel = false;
        mAnimateToDismiss = false;
    }

    private void setToExpandWhenShow() {
        mBottomSheetContainer.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }


    protected abstract View getContentView();

    protected abstract ViewGroup.LayoutParams getContentLayoutParams();
}
