package com.aliyun.svideo.base.Form;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//
import com.google.gson.annotations.SerializedName;

public class PasterForm {
    @SerializedName("id")
    private int mId;
    @SerializedName("fontId")
    private int mFontId;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("name")
    private String mName;
    @SerializedName("priority")
    private int mPriority;
    @SerializedName("url")
    private String mDownloadUrl;
    @SerializedName("md5")
    private String mMD5;
    @SerializedName("preview")
    private String mPreviewUrl;
    @SerializedName("sort")
    private int mSort;
    @SerializedName("type")
    private int mType;
    @SerializedName("path")
    private String mPath;

    public PasterForm() {
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public int getFontId() {
        return this.mFontId;
    }

    public void setFontId(int fontId) {
        this.mFontId = fontId;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getPriority() {
        return this.mPriority;
    }

    public void setPriority(int priority) {
        this.mPriority = priority;
    }

    public String getDownloadUrl() {
        return this.mDownloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    public String getMD5() {
        return this.mMD5;
    }

    public void setMD5(String md5) {
        this.mMD5 = md5;
    }

    public String getPreviewUrl() {
        return this.mPreviewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.mPreviewUrl = previewUrl;
    }

    public int getSort() {
        return this.mSort;
    }

    public void setSort(int sort) {
        this.mSort = sort;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public String getPath() {
        return this.mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }
}

