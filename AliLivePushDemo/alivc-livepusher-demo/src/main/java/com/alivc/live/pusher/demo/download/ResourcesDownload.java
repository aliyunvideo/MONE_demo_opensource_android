package com.alivc.live.pusher.demo.download;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alivc.live.commonbiz.Downloader;
import com.alivc.live.commonbiz.HttpUtils;
import com.alivc.live.commonbiz.listener.OnDownloadListener;
import com.alivc.live.commonbiz.listener.OnNetWorkListener;
import com.alivc.live.pusher.demo.Common;

import java.io.File;

import okhttp3.Response;

public class ResourcesDownload {

    private static final String OSS_YUV_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/livePush/capture0.yuv";

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public static long downloadCaptureYUV(Context context, OnDownloadListener listener) {
        Downloader downloader = new Downloader(context.getApplicationContext());
        File file = localCaptureYUVFilePath(context);
        Log.d("AliLivePusher", "downloadYUV file : " + file.getAbsolutePath());

        downloader.setDownloadListener(new OnDownloadListener() {
            @Override
            public void onDownloadSuccess(long downloadId) {
                verifyFile(downloader, file, listener);
            }

            @Override
            public void onDownloadProgress(long downloadId, double percent) {
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadProgress(downloadId, percent));
                }
            }

            @Override
            public void onDownloadError(long downloadId, int errorCode, String errorMsg) {
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadError(downloadId, errorCode, errorMsg));
                }
                deleteFile(file);
            }
        });

        if (file.exists()) {
            //文件存在，验证完整性
            verifyFile(downloader, file, listener);
            return -1;
        } else {
            return downloader.startDownload(OSS_YUV_URL, file);
        }
    }

    /**
     *  capture0.yuv 文件本地保存路径
     */
    public static File localCaptureYUVFilePath(Context context) {
        return new File(context.getApplicationContext().getExternalFilesDir(null), "capture0.yuv");
    }

    /**
     * 删除文件
     */
    private static void deleteFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }

    /**
     * 验证文件完整性
     * 通过 Response 中 etag 字段获取 OSS 文件的 MD5
     */
    private static void verifyFile(Downloader downloader, File file, OnDownloadListener listener) {
        HttpUtils.get(OSS_YUV_URL, new OnNetWorkListener() {
            @Override
            public void onSuccess(Object obj) {
                Response response = (Response) obj;
                String serverMD5 = response.header("etag");
                if (serverMD5 != null && !TextUtils.isEmpty(serverMD5)) {
                    String responseMD5 = serverMD5.replace("\"", "").trim();
                    String fileMD5 = Common.getFileMD5(file).trim();
                    if (fileMD5.equalsIgnoreCase(responseMD5)) {
                        //文件已存在，且完整
                        if (listener != null) {
                            mHandler.post(() -> listener.onDownloadSuccess(-1));
                        }
                    } else {
                        //文件不完整，重新下载
                        deleteFile(file);
                        downloader.startDownload(OSS_YUV_URL, file);
                    }
                } else {
                    deleteFile(file);
                    downloader.startDownload(OSS_YUV_URL, file);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadError(-1, DownloadManager.ERROR_HTTP_DATA_ERROR, "code=" + code + ",msg=" + msg));
                }
            }

            @Override
            public void onError() {
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadError(-1, DownloadManager.ERROR_HTTP_DATA_ERROR, "NetWork error"));
                }
            }
        });
    }
}
