package com.aliyun.svideo.track.thumbnail;

import java.util.List;

public interface ThumbnailRequestListener {

    String getPath();

    long getTimelineIn();

    long getTimelineOut();

    void onThumbnailReady(String path, List<ThumbnailBitmap> list);

    void onThumbnailReady(String path, ThumbnailBitmap thumbnailBitmap);

    void onError(String path, int errorCode);
}
