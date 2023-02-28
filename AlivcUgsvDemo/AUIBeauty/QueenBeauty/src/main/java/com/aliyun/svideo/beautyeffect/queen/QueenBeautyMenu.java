package com.aliyun.svideo.beautyeffect.queen;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.svideo.beauty.queen.R;
import com.aliyunsdk.queen.menu.BeautyMenuPanel;

public class QueenBeautyMenu extends BaseChooser {
    private LinearLayout llBlank;
    private BeautyMenuPanel mBeautyMenuPanel;
    private FrameLayout.LayoutParams mMenuParams;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.QUDemoFullStyle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alivc_queen_dialog_menu_layout, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llBlank = view.findViewById(R.id.ll_blank);

        showMenuPanel((ViewGroup) view);
    }

    @Override
    public void onStart() {
        super.onStart();
        llBlank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                    解决crash:java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                    原因:after onSaveInstanceState invoke commit,而 show 会触发 commit 操作
                    fragment is added and its state has already been saved，
                    Any operations that would change saved state should not be performed if this method returns true
                */
                if (isStateSaved()) {
                    return;
                }
                hideMenuPanel();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        hideMenuPanel();
    }

    private void hideMenuPanel() {
        // 隐藏菜单
        mBeautyMenuPanel.onHideMenu();
        ViewGroup container = (ViewGroup) QueenBeautyMenu.this.getView();
        container.removeView(mBeautyMenuPanel);
        QueenBeautyMenu.this.dismissAllowingStateLoss();
    }

    private void showMenuPanel(ViewGroup container) {
        if (mBeautyMenuPanel == null) {
            mBeautyMenuPanel = new BeautyMenuPanel(this.getContext());
            mBeautyMenuPanel.onHideCopyright();
            mBeautyMenuPanel.onSetMenuBackgroundColor(getResources().getColor(R.color.bg_medium));
            mMenuParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            mMenuParams.gravity = Gravity.BOTTOM;
        }
        container.removeView(mBeautyMenuPanel);
        container.addView(mBeautyMenuPanel, mMenuParams);
        mBeautyMenuPanel.onShowMenu();
    }
}
