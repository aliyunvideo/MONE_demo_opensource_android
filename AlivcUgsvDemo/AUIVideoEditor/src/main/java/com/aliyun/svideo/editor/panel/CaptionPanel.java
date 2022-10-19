package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aliyun.svideo.editor.caption.CaptionEntryPanelView;
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEditViewModel;
import com.aliyun.svideo.editor.caption.viewmodel.CaptionEntryViewModel;
import com.aliyun.svideo.editor.common.util.SpeedUtil;
import com.aliyun.svideosdk.editor.AliyunIEditor;

public class CaptionPanel extends BasePanel {
    private CaptionEntryPanelView mCaptionEntryPanelView;
    private CaptionEntryViewModel mCaptionEntryViewModel;

    public CaptionPanel(@NonNull Context context) {
        this(context, null);
    }

    public CaptionPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptionPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onInitView() {
        Context lContext = getContext();
        if(lContext instanceof ViewModelStoreOwner) {
            ViewModelProvider lViewModelProvider = new ViewModelProvider((ViewModelStoreOwner) lContext);
            CaptionEditViewModel lCaptionEditViewModel = lViewModelProvider.get(CaptionEditViewModel.class);
            mCaptionEntryViewModel = new CaptionEntryViewModel(lCaptionEditViewModel);

            mCaptionEntryPanelView = new CaptionEntryPanelView(getContext());
            addView(mCaptionEntryPanelView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        }

    }

    @Override
    public void onInitData(PanelManger panelManger,AliyunIEditor editor, int panelType) {
        super.onInitData(panelManger,editor, panelType);
        Context ctx = getContext();
        if(ctx instanceof LifecycleOwner) {
            LifecycleOwner owner = (LifecycleOwner) ctx;
            mCaptionEntryViewModel.bind(owner, mAliyunIEditor);
            mCaptionEntryPanelView.setViewModel(mCaptionEntryViewModel);
            float speed = SpeedUtil.Companion.getSpeed(mAliyunIEditor);
            mCaptionEntryPanelView.updateSpeed(speed);
        }
    }

    @Override
    protected void onRemove() {
        super.onRemove();
        mCaptionEntryViewModel.unBind();
    }

    @Override
    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {
        mCaptionEntryPanelView.updatePlayProgress(currentPlayTime);
    }

}
