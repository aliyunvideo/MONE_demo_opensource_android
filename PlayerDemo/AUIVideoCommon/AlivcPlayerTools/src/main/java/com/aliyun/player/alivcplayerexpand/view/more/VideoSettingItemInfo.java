package com.aliyun.player.alivcplayerexpand.view.more;

public class VideoSettingItemInfo {
    public HalfViewVideoSettingPage.VideoSettingType settingType;
    public String content;
    public int position;

    public VideoSettingItemInfo(HalfViewVideoSettingPage.VideoSettingType settingType, String content, int position) {
        this.settingType = settingType;
        this.content = content;
        this.position = position;
    }
}
