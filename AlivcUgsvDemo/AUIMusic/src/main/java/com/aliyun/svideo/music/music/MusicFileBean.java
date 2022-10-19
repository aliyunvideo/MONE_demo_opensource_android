package com.aliyun.svideo.music.music;

import java.util.Objects;

public class MusicFileBean {
    public MusicFileBean(String title, String artist, String musicId, String image) {
        this.title = title;
        this.artist = artist;
        this.musicId = musicId;
        this.image = image;
    }

    public MusicFileBean() {
    }

    /**
     * id标识
     */
    public int id;
    /**
     * 显示名称
     */
    public String title;
    /**
     * 文件名称
     */
    public String displayName;
    /**
     * 音乐文件的路径
     */
    public String path;
    /**
     * 音乐文件的Uri
     */
    public String uri;
    /**
     * 媒体播放总时间
     */
    public int duration;
    /**
     * 艺术家
     */
    public String artist;
    /**
     * 音乐id
     */
    public String musicId;
    /**
     * 音乐图片地址
     */
    public String image;
    public long size;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MusicFileBean fileBean = (MusicFileBean)o;
        return musicId.equals(fileBean.musicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicId);
    }
}
