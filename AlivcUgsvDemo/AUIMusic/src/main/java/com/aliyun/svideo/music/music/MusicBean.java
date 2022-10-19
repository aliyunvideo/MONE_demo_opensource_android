package com.aliyun.svideo.music.music;

import java.util.Objects;

/**
 * @author zsy_18 data:2019/3/6
 */
public class MusicBean {

    /**
     * musicId : T10033153645
     * title : Ring Ring Ring
     * artistName : N.O.D
     * duration : 208
     * source : TaiHe
     */

    private String musicId;
    private String title;
    private String artistName;
    private String duration;
    private String source;
    private String image;

    public String getMusicId() {
        return musicId;
    }

    public void setMusicId(String musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        MusicBean musicBean = (MusicBean)o;
        return musicId.equals(musicBean.musicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(musicId);
    }
}
