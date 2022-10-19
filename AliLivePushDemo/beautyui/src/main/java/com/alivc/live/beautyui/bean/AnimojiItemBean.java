package com.alivc.live.beautyui.bean;

import java.util.Objects;

public class AnimojiItemBean {
    private int id;

    private String title;

    private String path;

    public AnimojiItemBean(int id, String title, String path) {
        this.id = id;
        this.title = title;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnimojiItemBean that = (AnimojiItemBean) o;
        return id == that.id &&
                Objects.equals(title, that.title) &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, path);
    }

    @Override
    public String toString() {
        return "AnimojiItemBean{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
