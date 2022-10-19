package com.aliyun.svideo.editor.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aliyun.svideosdk.editor.AliyunIEditor;

public abstract class BasePanel extends FrameLayout {
    private PanelManger mPanelManger;
    protected AliyunIEditor mAliyunIEditor;
    /**
     * 面板类型，对应菜单ID
     */
    private int mPanelType;

    public BasePanel(@NonNull Context context) {
        this(context, null);
    }

    public BasePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context ,attrs, 0);
    }

    public BasePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        onInitView();
    }

    protected abstract void onInitView();


    public void onInitData(PanelManger panelManger, AliyunIEditor editor, int panelType) {
        mPanelManger = panelManger;
        mAliyunIEditor = editor;
        mPanelType = panelType;
    }

    public void onPlayProgress(long currentPlayTime, long currentStreamPlayTime) {

    }

    public boolean onBackPressed() {
        return false;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    protected void onRemove() {

    }

    public void onDestroy() {
        mPanelManger = null;
        mAliyunIEditor = null;
    }

    /**
     * 弹出时渲染层是否上移
     *
     * @return
     */
    public boolean isSurfaceNeedZoom() {
        return true;
    }

    /**
     * 获取面板类型，对应菜单ID
     *
     * @return
     */
    public int getPanelType() {
        return mPanelType;
    }

    public PanelManger getPanelManger() {
        return mPanelManger;
    }

    public int getCalculateHeight() {
        int measuredHeight = getMeasuredHeight();
        if (measuredHeight == 0) {
            measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            measuredHeight = getMeasuredHeight();
        }
        return measuredHeight;
    }

    public void removeOwn() {
        ViewParent parent = this.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
        onRemove();
    }

}
