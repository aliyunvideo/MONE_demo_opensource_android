package com.aliyun.ugsv.auibeauty;

import android.content.Context;
import android.hardware.Camera;
import android.view.View;

import androidx.fragment.app.FragmentManager;

import com.aliyun.svideo.base.BaseChooser;
import com.aliyun.ugsv.auibeauty.api.constant.BeautyConstant;
import com.aliyun.ugsv.auibeauty.api.constant.BeautySDKType;
import com.aliyun.ugsv.auibeauty.ui.BeautySettingChooser;

public class BeautyManager implements BeautyInterface, OnDefaultBeautyLevelChangeListener {

    private BeautySettingChooser mBeautySettingChooser;
    private int mCurrentLevel = 60;
    private OnBeautyLayoutChangeListener mOnBeautyLayoutChangeListener;
    private OnDefaultBeautyLevelChangeListener mOnDefaultBeautyLevelChangeListener;

    @Override
    public void init(Context context, IAliyunBeautyInitCallback iAliyunBeautyInitCallback) {
        if (iAliyunBeautyInitCallback != null) {
            iAliyunBeautyInitCallback.onInit(BeautyConstant.BEAUTY_INIT_SUCCEED);
        }
    }

    @Override
    public void release() {

    }

    @Override
    public void initParams() {

    }

    @Override
    public void showControllerView(FragmentManager fragmentManager, OnBeautyLayoutChangeListener onBeautyLayoutChangeListener) {
        if (mBeautySettingChooser == null) {
            mBeautySettingChooser = new BeautySettingChooser();
        }
        mOnBeautyLayoutChangeListener = onBeautyLayoutChangeListener;
        mBeautySettingChooser.setOnDefaultBeautyLevelChangeListener(this);
        mBeautySettingChooser.setDismissListener(new BaseChooser.DialogVisibleListener() {
            @Override
            public void onDialogDismiss() {
                if (mOnBeautyLayoutChangeListener != null) {
                    mOnBeautyLayoutChangeListener.onLayoutChange(View.GONE);
                }
            }

            @Override
            public void onDialogShow() {
                if (mOnBeautyLayoutChangeListener != null) {
                    mOnBeautyLayoutChangeListener.onLayoutChange(View.VISIBLE);
                }
            }
        });
        mBeautySettingChooser.show(fragmentManager, "beauty", mCurrentLevel);

    }

    @Override
    public void addDefaultBeautyLevelChangeListener(OnDefaultBeautyLevelChangeListener onDefaultBeautyLevelChangeListener) {
        mOnDefaultBeautyLevelChangeListener = onDefaultBeautyLevelChangeListener;
    }

    @Override
    public void onFrameBack(byte[] bytes, int width, int height, Camera.CameraInfo info) {

    }

    @Override
    public int onTextureIdBack(int textureId, int textureWidth, int textureHeight, float[] matrix, int currentCameraType) {
        return textureId;
    }

    @Override
    public void setDeviceOrientation(int deviceOrientation, int displayOrientation) {

    }


    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public BeautySDKType getSdkType() {
        return BeautySDKType.DEFAULT;
    }

    @Override
    public void setDebug(boolean isDebug) {

    }


    @Override
    public void onDefaultBeautyLevelChange(int level) {
        mCurrentLevel = level;
        if (mOnDefaultBeautyLevelChangeListener != null) {
            mOnDefaultBeautyLevelChangeListener.onDefaultBeautyLevelChange(level);
        }
    }
}
