package com.aliyun.auivideolist.bean;

import java.util.ArrayList;

public class ListVideoBean {

    private String title;
    private String url;
    private String coverURL;
    private ArrayList<HListVideoBean> horizontalVideoData;

    public static class HListVideoBean{
        private String title;
        private String url;
        private String coverURL;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getCoverURL() {
            return coverURL;
        }

        public void setCoverURL(String coverURL) {
            this.coverURL = coverURL;
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public ArrayList<HListVideoBean> getHorizontalVideoData() {
        return horizontalVideoData;
    }

    public void setHorizontalVideoData(ArrayList<HListVideoBean> horizontalVideoData) {
        this.horizontalVideoData = horizontalVideoData;
    }
}
