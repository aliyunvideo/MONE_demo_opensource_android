package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aliyun.svideo.editor.common.panel.OnItemClickListener;
import com.aliyun.svideo.editor.common.util.SpeedUtil;
import com.aliyun.svideo.editor.sticker.StickerEntryPanelView;
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEditViewModel;
import com.aliyun.svideo.editor.sticker.viewmodel.StickerEntryViewModel;
import com.aliyun.svideosdk.editor.AliyunIEditor;

public class StickerPanel extends BasePanel {
    private StickerEntryPanelView mBubbleEntryPanelView;
    private StickerEntryViewModel mBubbleEntryViewModel;

    public StickerPanel(@NonNull Context context) {
        this(context, null);
    }

    public StickerPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            StickerEditViewModel lStickerEditViewModel = lViewModelProvider.get(StickerEditViewModel.class);

            mBubbleEntryViewModel = new StickerEntryViewModel(lStickerEditViewModel);
            mBubbleEntryPanelView = new StickerEntryPanelView(getContext());
            addView(mBubbleEntryPanelView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

    }

    @Override
    public void onInitData(PanelManger panelManger, AliyunIEditor editor, int panelType) {
        super.onInitData(panelManger, editor, panelType);
        Context ctx = getContext();
        if(ctx instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) ctx;

            mBubbleEntryViewModel.bind(owner, mAliyunIEditor);
            mBubbleEntryPanelView.setViewModel(mBubbleEntryViewModel);
            float speed = SpeedUtil.Companion.getSpeed(mAliyunIEditor);
            mBubbleEntryPanelView.updateSpeed(speed);
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mBubbleEntryViewModel.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        mBubbleEntryViewModel.unBind();
    }

    @Override
    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        mBubbleEntryPanelView.updatePlayProgress(currentPlayTime);
    }
}
