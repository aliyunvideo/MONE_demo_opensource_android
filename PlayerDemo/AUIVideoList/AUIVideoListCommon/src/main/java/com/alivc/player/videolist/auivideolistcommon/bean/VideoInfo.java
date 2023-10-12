package com.alivc.player.videolist.auivideolistcommon.bean;

import java.util.Objects;
import java.util.Random;

public class VideoInfo {

    private int id = new Random().nextInt();
    private String url;
    private String title;
    private String author;

    private int position = 0;

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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPosition(){
        return position;
    }

    public void setPosition(int position){
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoInfo)) return false;
        VideoInfo videoInfo = (VideoInfo) o;
        return id == videoInfo.id && Objects.equals(url, videoInfo.url) && Objects.equals(title, videoInfo.title) && Objects.equals(author, videoInfo.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, title, author);
    }
}
