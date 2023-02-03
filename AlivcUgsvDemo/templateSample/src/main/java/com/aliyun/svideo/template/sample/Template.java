package com.aliyun.svideo.template.sample;

public class Template {
    public String id;
    public String name;
    public String description;
    public String cover;
    public int duration;
    public String downloadUrl;

    public Template(){}

    public Template(String id, String name, String description, String cover, int duration, String downloadUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.cover = cover;
        this.duration = duration;
        this.downloadUrl = downloadUrl;
    }
}
