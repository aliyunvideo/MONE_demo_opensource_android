package com.aliyun.svideo.beauty.faceunity.adapter.holder;

import android.content.Context;
import android.view.View;

import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyChooserCallback;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyDetailClickListener;
import com.aliyun.svideo.beauty.faceunity.inteface.OnBeautyFaceLevelChangeListener;
import com.aliyun.svideo.beauty.faceunity.view.face.AlivcBeautyFaceSettingView;

/**
 * faceunity美肌
 */
public class FaceUnitySkinViewHolder extends BaseCaptionViewHolder {
    private OnBeautyChooserCallback mOnBeautyChooserCallback;
    private int mCurrentLevel = 3;

    public FaceUnitySkinViewHolder(Context context, int currentLevel, OnBeautyChooserCallback onBeautyChooserCallback) {
        super(context);
        mCurrentLevel = currentLevel;
        mOnBeautyChooserCallback = onBeautyChooserCallback;
    }


    @Override
    public View onCreateView(Context context) {
        return new AlivcBeautyFaceSettingView(context);
    }

    @Override
    public void onBindViewHolder() {
        AlivcBeautyFaceSettingView itemView = (AlivcBeautyFaceSettingView) getItemView();
        itemView.setDefaultSelect(mCurrentLevel);
        itemView.setOnBeautyDetailClickListener(new OnBeautyDetailClickListener() {
            @Override
            public void onDetailClick(int level) {
                if (mOnBeautyChooserCallback != null) {
                    mOnBeautyChooserCallback.onShowSkinDetailView(level);
                }
            }
        });

        itemView.setOnBeautyFaceLevelChangeListener(new OnBeautyFaceLevelChangeListener() {
            @Override
            public void onLevelChanged(int level) {
                if (mOnBeautyChooserCallback != null) {
                    mOnBeautyChooserCallback.onShapeTypeChange(level);
                }
            }
        });

    }
}
