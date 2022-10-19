/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.base.widget.pagerecyclerview;

public class GiftListItemForm {
    private int mId;             //礼物ID
    private int mDynamic;       //是否支持动效    0：不支持 1：支持
    private int mSequence;    //是否支持连送    0：不支持 1：支持
    private String mIconUrl;     //Icon url
    private String mResourceUrl;   //资源下载地址
    private String mName;        //礼物名称
    private int      mPrice;     //礼物价格（趣币数）
    private int mOrderNum;        //礼物排序号（越小越靠前）
    private int mUpdated;         //更新时间

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getIconUrl() {
        return mIconUrl;
    }

    public void setIconUrl(String iconUrl) {
        mIconUrl = iconUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public int getDynamic() {
        return mDynamic;
    }

    public void setDynamic(int dynamic) {
        mDynamic = dynamic;
    }

    public int getSequence() {
        return mSequence;
    }

    public void setSequence(int sequence) {
        mSequence = sequence;
    }

    public String getResourceUrl() {
        return mResourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        mResourceUrl = resourceUrl;
    }

    public int getOrderNum() {
        return mOrderNum;
    }

    public void setOrderNum(int orderNum) {
        mOrderNum = orderNum;
    }

    public int getUpdated() {
        return mUpdated;
    }

    public void setUpdated(int updated) {
        mUpdated = updated;
    }

    public boolean isContinuous() {
        return mSequence == 1;
    }

    public boolean isDynamic() {
        return mDynamic == 1;
    }

    @Override
    public boolean equals(Object o) {
        if(o != null && o instanceof GiftListItemForm) {
            return ((GiftListItemForm) o).getId() == mId;
        }
        return super.equals(o);
    }
}
