/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader;

import com.liulishuo.filedownloader.BaseDownloadTask;

public class FileDownloaderCallback {

    /**
     * onPending 等待，已经进入下载队列	数据库中的soFarBytes与totalBytes
     * connected 已经连接上	ETag, 是否断点续传, soFarBytes, totalBytes
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param preProgress
     */
    public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {

    }

    /**
     * 下载进度回调	soFarBytes
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param speed
     * @param progress
     */
    public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress) {

    }

    /**
     * 等待
     * @param downloadId
     */
    public void onWait(int downloadId) {
    }

    /**
     * paused	暂停下载	soFarBytes
     * error	下载出现错误	抛出的Throwable
     * @param downloadId
     * @param soFarBytes
     * @param totalBytes
     * @param progress
     */
    public void onStop(int downloadId, long soFarBytes, long totalBytes, int progress) {

    }

    /**
     * 完成整个下载过程
     * @param downloadId
     * @param path
     */
    public void onFinish(int downloadId, String path) {

    }

    /**
     * 下载由于外部原因暂停
     * @param task
     * @param e
     */
    public void onError(final BaseDownloadTask task, final Throwable e) {

    }
}
