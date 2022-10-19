package com.aliyun.svideo.track.thumbnail;

import android.graphics.Bitmap;

import com.aliyun.svideosdk.common.AliyunIThumbnailFetcher;

public class ThumbnailFetcherCallback implements AliyunIThumbnailFetcher.OnThumbnailCompletion {
    private String mPath;
    private ThumbnailRequestListener mListener;

    public ThumbnailFetcherCallback(String mPath, ThumbnailRequestListener mListener) {
        this.mPath = mPath;
        this.mListener = mListener;
    }

    @Override
    public void onThumbnailReady(Bitmap frameBitmap, long time, int index) {
        if (mListener != null) {
            mListener.onThumbnailReady(mPath, new ThumbnailBitmap(time, frameBitmap));
        }
    }

    @Override
    public void onError(int errorCode) {
        if (mListener != null) {
            mListener.onError(mPath, errorCode);
        }
    }
}
