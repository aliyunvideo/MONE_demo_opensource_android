package com.aliyun.svideo.track.bean;

public class StickerClipInfo extends MultiClipInfo {
    private String mPath;
    private String mText;

    public StickerClipInfo(int clipId) {
        setClipType(ClipType.STICKER);
        setClipId(clipId);
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }
}
