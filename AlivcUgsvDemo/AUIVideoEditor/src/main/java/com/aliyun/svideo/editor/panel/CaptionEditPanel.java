package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aliyun.svideo.editor.caption.CaptionEditPanelView;
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel;
import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId;

public class CaptionEditPanel extends BasePanel{
    private CaptionEditPanelView mCaptionEditPanelView;

    public CaptionEditPanel(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onInitView() {

        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            mCaptionEditPanelView = new CaptionEditPanelView(getContext());
            CaptionEditViewModel captionEditViewModel = lViewModelProvider.get(CaptionEditViewModel.class);
            mCaptionEditPanelView.setViewModel(captionEditViewModel);

            FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lLayoutParams.gravity = Gravity.BOTTOM;
            this.addView(mCaptionEditPanelView, lLayoutParams);

            captionEditViewModel.setOnItemClickListener((view, id, obj) -> {
                if(id == PanelItemId.ITEM_ID_CANCEL) {
                    getPanelManger().popGlobalPanel();
                }
            });
        }
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            CaptionEditViewModel captionEditViewModel = lViewModelProvider.get(CaptionEditViewModel.class);
            captionEditViewModel.setOnItemClickListener(null);
        }
    }
}
