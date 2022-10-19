package com.aliyun.alivcsolution.model;

/**
 * Created by Mulberry on 2018/4/11.
 */

public class ScenesModel {
    private String name;
    private int imgUrl;

    public ScenesModel(String name, int imgUrl) {
        this.name = name;
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(int imgUrl) {
        this.imgUrl = imgUrl;
    }
}
