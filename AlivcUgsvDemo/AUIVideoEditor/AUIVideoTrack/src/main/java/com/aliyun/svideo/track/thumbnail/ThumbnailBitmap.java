package com.aliyun.svideo.track.thumbnail;

import android.graphics.Bitmap;

public class ThumbnailBitmap {
    private long mTime;
    private Bitmap mBitmap;

    public ThumbnailBitmap(long time, Bitmap bitmap) {
        this.mTime = time;
        this.mBitmap = bitmap;
    }

    public long getTime() {
        return mTime;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}
