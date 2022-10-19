package com.aliyun.player.alivcplayerexpand.bean;

import java.io.Serializable;

/**
 * 打点信息
 */
public class DotBean implements Serializable {

    /**
     * "time":"15","content":"测试打点内容"
     */
    private String time;
    private String content;

    public String getTime() {
        return time;
    }

    public String getContent() {
        return content;
    }


    public DotBean(String time, String content) {
        this.time = time;
        this.content = content;
    }

    @Override
    public String toString() {
        return "DotBean{" +
                "time='" + time + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
