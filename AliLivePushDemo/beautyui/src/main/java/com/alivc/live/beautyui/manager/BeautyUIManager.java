package com.alivc.live.beautyui.manager;

import android.util.Log;

import androidx.annotation.Nullable;

import com.alivc.live.beautyui.bean.BeautyItemBean;
import com.alivc.live.beauty.BeautyInterface;

/**
 * 当美颜UI响应用户操作后，调用美颜相关的接口
 */
public class BeautyUIManager {
    private static final String TAG = BeautyUIManager.class.getSimpleName();

    public static void onTabItemDataChanged(@Nullable BeautyInterface beautyManager, @Nullable BeautyItemBean beautyItemBean) {
        if (beautyItemBean == null) return;

        BeautyItemBean.BeautyType beautyType = beautyItemBean.getBeautyType();
        if (beautyManager == null || beautyType == null) return;

        final int id = beautyItemBean.getId();
        final String title = beautyItemBean.getTitle();
        final boolean isSelected = beautyItemBean.isSelected();
        final float value = beautyItemBean.getRealValue();
        final String materialPath = beautyItemBean.getMaterialPath();

        // 检测函数调用耗时，目前2ms以下，可能是异步调用的原因。
        long start = System.currentTimeMillis();

        switch (beautyType) {
            case BEAUTY_PARAM:
                beautyManager.setBeautyParams(id, value);
                Log.d(TAG, "set beauty param: " + title + ", " + id + ", " + isSelected + ", " + value + ", cost: " + (System.currentTimeMillis() - start));
                break;
            case BEAUTY_FACE_SHAPE:
                beautyManager.setFaceShapeParams(id, value);
                Log.d(TAG, "set beauty faceshape: " + title + ", " + id + ", " + isSelected + ", " + value + ", cost: " + (System.currentTimeMillis() - start));
                break;
            case BEAUTY_MAKEUP:
                if (materialPath != null) {
                    beautyManager.setMakeupParams(id, isSelected ? materialPath : "");
                    Log.d(TAG, "set beauty makeup: " + title + ", " + id + ", " + isSelected + ", " + materialPath + ", cost: " + (System.currentTimeMillis() - start));
                }
                break;
            case BEAUTY_LUT:
                beautyManager.setFilterParams(isSelected ? materialPath : null);
                Log.d(TAG, "set beauty lut: " + title + ", " + isSelected + ", " + materialPath + ", cost: " + (System.currentTimeMillis() - start));
                break;
            case BEAUTY_STICKER:
                if (materialPath != null) {
                    if (isSelected) {
                        beautyManager.setMaterialParams(materialPath);
                    } else {
                        beautyManager.removeMaterialParams(materialPath);
                    }
                    Log.d(TAG, "set beauty sticker: " + title + ", " + isSelected + ", " + materialPath + ", cost: " + (System.currentTimeMillis() - start));
                }
                break;
            default:
                break;
        }
    }
}
