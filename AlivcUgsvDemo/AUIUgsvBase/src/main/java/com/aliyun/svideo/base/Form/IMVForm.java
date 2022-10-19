package com.aliyun.svideo.base.Form;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.Serializable;
import java.util.List;

public class IMVForm implements Serializable {
    private int id;
    private String name;
    private String nameEn;
    private String key;
    private int level;
    private String tag;
    private int cat;
    private String previewPic;
    private String previewMp4;
    private long duration;
    private int type;
    private int sort;
    private String icon;
    private List<AspectForm> aspectList;

    public IMVForm() {
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCat() {
        return this.cat;
    }

    public void setCat(int cat) {
        this.cat = cat;
    }

    public String getPreviewPic() {
        return this.previewPic;
    }

    public void setPreviewPic(String previewPic) {
        this.previewPic = previewPic;
    }

    public String getPreviewMp4() {
        return this.previewMp4;
    }

    public void setPreviewMp4(String previewMp4) {
        this.previewMp4 = previewMp4;
    }

    public long getDuration() {
        return this.duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSort() {
        return this.sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public List<AspectForm> getAspectList() {
        return this.aspectList;
    }

    public void setAspectList(List<AspectForm> aspectList) {
        this.aspectList = aspectList;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getNameEn() {
        return nameEn;
    }

    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
}

