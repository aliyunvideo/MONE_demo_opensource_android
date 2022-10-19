package com.aliyun.svideo.track.bean;

public class CaptionClipInfo extends MultiClipInfo {
    private String mPath;
    private String mText;

    public CaptionClipInfo(int clipId) {
        setClipType(ClipType.CAPTION);
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
