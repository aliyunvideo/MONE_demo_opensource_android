package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.view.Gravity;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aliyun.svideo.editor.common.panel.viewmodel.PanelItemId;
import com.aliyun.svideo.editor.sticker.StickerEditPanelView;
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEditViewModel;

public class StickerEditPanel extends BasePanel{
    private StickerEditPanelView mStickerEditPanelView;

    public StickerEditPanel(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onInitView() {

        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            mStickerEditPanelView = new StickerEditPanelView(lContext);
            StickerEditViewModel lStickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);
            mStickerEditPanelView.setViewModel(lStickerEditViewModel);
            FrameLayout.LayoutParams lLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            lLayoutParams.gravity = Gravity.BOTTOM;
            this.addView(mStickerEditPanelView, lLayoutParams);
            lStickerEditViewModel.setOnItemClickListener((view1, id1, obj1) -> {

                if(id1 == PanelItemId.ITEM_ID_CLOSE) {
                    getPanelManger().popGlobalPanel();
                } else if(id1 == PanelItemId.ITEM_ID_DELETE) {
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
            StickerEditViewModel lStickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);
            lStickerEditViewModel.setOnItemClickListener(null);
        }
    }

    @Override
    public boolean onBackPressed() {

        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            StickerEditViewModel lStickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);
            lStickerEditViewModel.onCloseClick(null);
            return true;
        }

        return super.onBackPressed();
    }
}
