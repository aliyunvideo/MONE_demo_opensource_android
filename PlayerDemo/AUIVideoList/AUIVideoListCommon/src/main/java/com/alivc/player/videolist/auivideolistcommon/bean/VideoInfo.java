package com.alivc.player.videolist.auivideolistcommon.bean;

import java.util.Objects;
import java.util.Random;

public class VideoInfo {

    private int id = new Random().nextInt();
    private String url;
    private String title;
    private String coverUrl;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoInfo)) return false;
        VideoInfo videoInfo = (VideoInfo) o;
        return id == videoInfo.id && Objects.equals(url, videoInfo.url) && Objects.equals(title, videoInfo.title) && Objects.equals(coverUrl, videoInfo.coverUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, title, coverUrl);
    }
}
