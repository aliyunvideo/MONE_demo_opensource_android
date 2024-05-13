package com.alivc.live.commonbiz;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alivc.live.commonbiz.download.DownloadUtil;
import com.alivc.live.commonbiz.network.HttpUtils;
import com.alivc.live.commonutils.FileUtil;

import java.io.File;

import okhttp3.Response;

/**
 * 远端资源下载管理类
 *
 * @note 用于下载存放在云端的yuv文件，该yuv文件在外部音视频推流时使用
 */
public class ResourcesDownload {

    private static final String TAG = "ResourcesDownload";

    private static final String BASIC_YUV_OSS_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/livePush/capture0.yuv";
    private static final String GLOBAL_YUV_OSS_URL = "https://alivc-demo-cms.alicdn.com/versionProduct/resources/livePush/capture0_SG.yuv";
    private static final String FINAL_OSS_YUV_URL = BASIC_YUV_OSS_URL;

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private ResourcesDownload() {
    }

    /**
     * 下载yuv文件
     */
    public static long downloadCaptureYUV(Context context, DownloadUtil.OnDownloadListener listener) {
        DownloadUtil downloadUtil = new DownloadUtil(context.getApplicationContext());
        File file = ResourcesConst.localCaptureYUVFilePath(context);
        Log.d(TAG, "downloadYUV file : " + file.getAbsolutePath());

        downloadUtil.setDownloadListener(new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(long downloadId) {
                Log.d(TAG, "onDownloadSuccess : " + downloadId);
                verifyFile(downloadUtil, file, listener);
            }

            @Override
            public void onDownloadProgress(long downloadId, double percent) {
                Log.i(TAG, "onDownloadProgress : " + downloadId + ", " + percent);
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadProgress(downloadId, percent));
                }
            }

            @Override
            public void onDownloadError(long downloadId, int errorCode, String errorMsg) {
                Log.e(TAG, "onDownloadError : " + downloadId + ", " + errorCode + ", " + errorMsg);
                if (listener != null) {
                    mHandler.post(() -> listener.onDownloadError(downloadId, errorCode, errorMsg));
                }
                FileUtil.safeDeleteFile(file);
            }
        });

        if (file.exists()) {
            //文件存在，验证完整性
            verifyFile(downloadUtil, file, listener);
            return -1;
        } else {
            return downloadUtil.startDownload(FINAL_OSS_YUV_URL, file);
        }
    }

    /**
     * 验证文件完整性
     * 通过 Response 中 etag 字段获取 OSS 文件的 MD5
     */
    private static void verifyFile(DownloadUtil downloadUtil, File file, DownloadUtil.OnDownloadListener listener) {
        HttpUtils.get(FINAL_OSS_YUV_URL, new HttpUtils.OnNetWorkListener() {
            @Override
            public void onSuccess(Object obj) {
                Response response = (Response) obj;

                // 1、通过oss url获取etag，和本地文件的md5进行比对，是否一致
                String responseMD5 = "";
                try {
                    responseMD5 = response.header("etag").replace("\"", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!TextUtils.isEmpty(responseMD5)) {
                    String fileMD5 = FileUtil.getFileMD5(file).trim();
                    if (responseMD5.equalsIgnoreCase(fileMD5)) {
                        if (listener != null) {
                            mHandler.post(() -> listener.onDownloadSuccess(-1));
                        }
                        return;
                    }
                }

                // 2、有时候etag是个非法值，并不代表文件的实际md5（坑），那么直接对比文件的大小是否一致
                long serverFileSize = -1;
                try {
                    serverFileSize = Long.parseLong(response.header("Content-Length").trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (file != null && serverFileSize == file.length()) {
                    if (listener != null) {
                        mHandler.post(() -> listener.onDownloadSuccess(-1));
                    }
                    return;
                }

                FileUtil.safeDeleteFile(file);
                downloadUtil.startDownload(FINAL_OSS_YUV_URL, file);
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
