/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;

import android.content.Context;
import android.os.Environment;

import com.aliyun.common.utils.StorageUtils;

import java.io.File;



public final class DownloadFileUtils {

    public static String initDownloadPath(Context context) {
        File asset_root_dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (asset_root_dir == null) {
            return null;
        }
        return asset_root_dir.getAbsolutePath();
    }

    public static File getAssetPackageDir(Context context, String name, long id) {
        return new File(StorageUtils.getFilesDirectory(context), name + "-" + String.valueOf(id));
    }

    public static boolean isPasterExist(Context context, String name, long id) {
        File downloadFile = getAssetPackageDir(context, name, id);

        return downloadFile.exists() && downloadFile.isDirectory();
    }
}
