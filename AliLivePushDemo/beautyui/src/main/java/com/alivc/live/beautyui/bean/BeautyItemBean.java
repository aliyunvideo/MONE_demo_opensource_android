package com.alivc.live.beautyui.bean;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

/**
 * 美颜Item UI bean
 */
public class BeautyItemBean implements Cloneable {

    public enum BeautyItemType {

        NONE(0),

        DIVIDER(1),

        SWITCH(2),

        SEEK_BAR(3),

        CONFIG_PAGE(4);

        private int value;

        public int getValue() {
            return value;
        }

        BeautyItemType(int value) {
            this.value = value;
        }
    }

    public enum BeautyType {
        BEAUTY_NONE,
        /**
         * 美颜
         */
        BEAUTY_PARAM,
        /**
         * 美型
         */
        BEAUTY_FACE_SHAPE,
        /**
         * 美妆
         */
        BEAUTY_MAKEUP,
        /**
         * 滤镜
         */
        BEAUTY_LUT,
        /**
         * 贴纸
         */
        BEAUTY_STICKER,
    }

    private int id;
    private BeautyType beautyType;

    private String title;

    private int imageResId;

    private BeautyItemType itemType;

    private int enableImageResId;

    // for BeautyItemType.SWITCH
    private boolean isSelected = false;

    // for BeautyItemType.SEEK_BAR
    // this value is progress bar value
    private float maxValue = 0;
    private float minValue = 0;

    private float realValue = 0;
    private int seekValue = 0;

    // for BeautyItemType.CONFIG_PAGE
    private BeautyTabBean tabBean;

    private String materialPath;

    public BeautyItemBean() {
        this.itemType = BeautyItemType.DIVIDER;
    }

    public BeautyItemBean(BeautyType beautyType, @NonNull String title, int imageResId) {
        this.beautyType = beautyType;
        this.title = title;
        this.imageResId = imageResId;
        this.enableImageResId = imageResId;

        this.itemType = BeautyItemType.NONE;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.itemType = BeautyItemType.NONE;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, boolean isSelected) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.itemType = BeautyItemType.SWITCH;
        this.isSelected = isSelected;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, int imageResId) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.imageResId = imageResId;
        this.enableImageResId = imageResId;

        this.itemType = BeautyItemType.NONE;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, int imageResId, boolean isSelected) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.imageResId = imageResId;
        this.enableImageResId = imageResId;

        this.itemType = BeautyItemType.SWITCH;
        this.isSelected = isSelected;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, int imageResId, int enableImageResId, boolean isSelected) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.imageResId = imageResId;
        this.enableImageResId = enableImageResId;

        this.itemType = BeautyItemType.SWITCH;
        this.isSelected = isSelected;
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, int imageResId, int enableImageResId, float minValue, float maxValue, float value) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.imageResId = imageResId;
        this.enableImageResId = enableImageResId;

        this.itemType = BeautyItemType.SEEK_BAR;
        this.minValue = minValue;
        this.maxValue = maxValue;
        setRealValue(value);
    }

    public BeautyItemBean(int id, BeautyType beautyType, @NonNull String title, int imageResId, int enableImageResId, @NonNull BeautyTabBean tabBean) {
        this.id = id;
        this.beautyType = beautyType;
        this.title = title;

        this.imageResId = imageResId;
        this.enableImageResId = enableImageResId;

        this.itemType = BeautyItemType.CONFIG_PAGE;
        this.tabBean = tabBean;
    }

    public int getId() {
        return id;
    }

    public BeautyType getBeautyType() {
        return beautyType;
    }

    public String getTitle() {
        return title;
    }

    public int getImageResId() {
        return imageResId;
    }

    public BeautyItemType getItemType() {
        return itemType;
    }

    public void setItemType(BeautyItemType itemType) {
        this.itemType = itemType;
    }

    public int getEnableImageResId() {
        return enableImageResId;
    }

    public void setEnableImageResId(int enableImageResId) {
        this.enableImageResId = enableImageResId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
    }

    public int getSeekValue() {
        return seekValue;
    }

    public void setSeekValue(int seekValue) {
        this.seekValue = seekValue;
        this.realValue = (maxValue - minValue) * seekValue / 100 + minValue;
    }

    public float getRealValue() {
        return realValue;
    }

    public void setRealValue(float realValue) {
        this.realValue = realValue;
        this.seekValue = (int) ((realValue - minValue) * 100 / (maxValue - minValue));
    }

    public String getMaterialPath() {
        return materialPath;
    }

    public void setMaterialPath(String materialPath) {
        this.materialPath = materialPath;
    }

    @Nullable
    public BeautyTabBean getTabBean() {
        return tabBean;
    }

    public void setTabBean(BeautyTabBean tabBean) {
        this.tabBean = tabBean;
    }

    @Override
    public String toString() {
        return "BeautyItemBean{" +
                "id=" + id +
                ", beautyType=" + beautyType +
                ", title='" + title + '\'' +
                ", imageResId=" + imageResId +
                ", itemType=" + itemType +
                ", enableImageResId=" + enableImageResId +
                ", isSelected=" + isSelected +
                ", maxValue=" + maxValue +
                ", minValue=" + minValue +
                ", realValue=" + realValue +
                ", seekValue=" + seekValue +
                ", tabBean=" + tabBean +
                ", materialPath='" + materialPath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BeautyItemBean itemBean = (BeautyItemBean) o;
        return id == itemBean.id &&
                imageResId == itemBean.imageResId &&
                enableImageResId == itemBean.enableImageResId &&
                isSelected == itemBean.isSelected &&
                Float.compare(itemBean.maxValue, maxValue) == 0 &&
                Float.compare(itemBean.minValue, minValue) == 0 &&
                Float.compare(itemBean.realValue, realValue) == 0 &&
                seekValue == itemBean.seekValue &&
                beautyType == itemBean.beautyType &&
                Objects.equals(title, itemBean.title) &&
                itemType == itemBean.itemType &&
                Objects.equals(tabBean, itemBean.tabBean) &&
                Objects.equals(materialPath, itemBean.materialPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, beautyType, title, imageResId, itemType, enableImageResId, isSelected, maxValue, minValue, realValue, seekValue, tabBean, materialPath);
    }

    @NonNull
    @Override
    public BeautyItemBean clone() {
        try {
            return (BeautyItemBean) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
