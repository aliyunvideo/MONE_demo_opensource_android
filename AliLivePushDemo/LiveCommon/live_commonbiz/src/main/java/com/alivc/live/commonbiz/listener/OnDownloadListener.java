package com.alivc.live.commonbiz.listener;

public interface OnDownloadListener {
    void onDownloadSuccess(long downloadId);

    void onDownloadProgress(long downloadId, double percent);

    void onDownloadError(long downloadId, int errorCode, String errorMsg);
}
