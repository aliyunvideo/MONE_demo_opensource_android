package com.alivc.live.commonbiz;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.alivc.live.commonbiz.listener.OnDownloadListener;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * 下载类
 * 使用 DownloadManager 实现下载功能
 */
public class Downloader {

    private static DownloadManager mDownloadManager;
    private OnDownloadListener mOnDownloadListener;

    public Downloader(Context context) {
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) context.getApplicationContext().getSystemService(DOWNLOAD_SERVICE);
        }
    }

    public long startDownload(String url, File file) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationUri(Uri.fromFile(file));
        long downloadId = mDownloadManager.enqueue(request);
        Timer timer = new Timer();
        //检测下载进度和下载完成事件
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                DownloadManager.Query query = new DownloadManager.Query();
                Cursor cursor = mDownloadManager.query(query.setFilterById(downloadId));
                if (cursor != null && cursor.moveToFirst()) {
                    //获取当前下载状态
                    int columnIndexForStatus = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    switch (cursor.getInt(columnIndexForStatus)) {
                        case DownloadManager.STATUS_SUCCESSFUL:
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onDownloadSuccess(downloadId);
                            }
                            cancel();
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onDownloadError(downloadId, DownloadManager.ERROR_FILE_ALREADY_EXISTS, "File already exists");
                            }
                            cancel();
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                        case DownloadManager.ERROR_FILE_ERROR:
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onDownloadError(downloadId, DownloadManager.ERROR_HTTP_DATA_ERROR, "other error");
                            }
                            cancel();
                            break;
                        case DownloadManager.STATUS_RUNNING:
                            int columnIndexForDownloadBytes = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                            int columnIndexForTotalSize = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                            //已下载大小
                            int downloadedBytes = cursor.getInt(columnIndexForDownloadBytes);
                            //总大小
                            int totalBytes = cursor.getInt(columnIndexForTotalSize);
                            double percent = (downloadedBytes * 100.00) / totalBytes;
                            if (mOnDownloadListener != null) {
                                mOnDownloadListener.onDownloadProgress(downloadId, Math.round(percent));
                            }
                            break;
                    }
                    cursor.close();
                }
            }
        };
        timer.schedule(timerTask, 0, 2000);

        return downloadId;
    }

    public void cancelDownload(long id) {
        mDownloadManager.remove(id);
    }

    public void setDownloadListener(OnDownloadListener downloadListener) {
        this.mOnDownloadListener = downloadListener;
    }
}
