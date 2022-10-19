package com.aliyun.svideo.base.Form;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ResourceForm {
    @SerializedName("id")
    private int mId;
    @SerializedName("icon")
    private String mIcon;
    @SerializedName("description")
    private String mDescription;
    @SerializedName("descriptionEn")
    private String mDescriptionEn;
    @SerializedName("isNew")
    private int mIsNew;
    @SerializedName("name")
    private String mName;
    @SerializedName("nameEn")
    private String mNameEn;
    @SerializedName("level")
    private int mLevel;
    @SerializedName("preview")
    private String mPreviewUrl;
    @SerializedName("sort")
    private int mSort;
    @SerializedName("pasterList")
    private List<PasterForm> mPasterList;
    private List<AnimationEffectForm> mAnimationEffectForms;
    private String path;
    private transient boolean isMore;

    public ResourceForm() {
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public String getIcon() {
        return this.mIcon;
    }

    public void setIcon(String icon) {
        this.mIcon = icon;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public int getIsNew() {
        return this.mIsNew;
    }

    public void setIsNew(int isNew) {
        this.mIsNew = isNew;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getLevel() {
        return this.mLevel;
    }

    public void setLevel(int level) {
        this.mLevel = level;
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

    public List<PasterForm> getPasterList() {
        return this.mPasterList;
    }

    public void setPasterList(List<PasterForm> pasterList) {
        this.mPasterList = pasterList;
    }

    public List<AnimationEffectForm> getAnimationEffectForms() {
        return mAnimationEffectForms;
    }

    public void setAnimationEffectForms(List<AnimationEffectForm> animationEffectForms) {
        this.mAnimationEffectForms = animationEffectForms;
    }

    public boolean isMore() {
        return this.isMore;
    }

    public void setMore(boolean more) {
        this.isMore = more;
    }

    public String getNameEn() {
        return mNameEn;
    }

    public void setNameEn(String nameEn) {
        mNameEn = nameEn;
    }

    public String getDescriptionEn() {
        return mDescriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        mDescriptionEn = descriptionEn;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ResourceForm var2 = (ResourceForm)o;
            return this.mId == var2.mId;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.mId;
    }
}

