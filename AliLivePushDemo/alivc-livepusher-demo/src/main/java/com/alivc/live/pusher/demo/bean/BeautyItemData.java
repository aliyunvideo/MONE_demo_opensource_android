package com.alivc.live.pusher.demo.bean;

/**
 * BeautyItemData
 */
public class BeautyItemData {
    private String mName;
    private BeautyItemMode mType;
    private boolean mNegative;
    private int mValue;
    private boolean isChecked;

    public BeautyItemData(String mName) {
        this.mName = mName;
        mType = BeautyItemMode.NULL;
    }

    public BeautyItemData(String mName, boolean mNegative, int mValue) {
        this.mName = mName;
        this.mNegative = mNegative;
        this.mValue = mValue;
        mType = BeautyItemMode.SEEKBAR;
    }

    public BeautyItemData(String mName, boolean isChecked) {
        this.mName = mName;
        this.isChecked = isChecked;
        mType = BeautyItemMode.SWITCH;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public BeautyItemMode getType() {
        return mType;
    }

    public void setType(BeautyItemMode mType) {
        this.mType = mType;
    }

    public boolean isNegative() {
        return mNegative;
    }

    public void setNegative(boolean mNegative) {
        this.mNegative = mNegative;
    }

    public int getValue() {
        return mValue;
    }

    public void setValue(int mValue) {
        this.mValue = mValue;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
