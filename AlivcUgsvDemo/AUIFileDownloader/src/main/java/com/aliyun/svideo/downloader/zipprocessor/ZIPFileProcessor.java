/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;

import android.util.Log;

import java.io.File;
import java.io.IOException;

import com.aliyun.common.utils.FileUtils;
public class ZIPFileProcessor implements FileProcessor {

    private final File mPackageDir;
    private final long id;

    public ZIPFileProcessor(File packageDir, long id) {
        this.mPackageDir = packageDir;
        this.id = id;
    }

    @Override
    public File process(File file) {
        if (file == null) {
            return null;
        }
        AssetPackageFileExtractor apf = null;
        try {
            apf = new AssetPackageFileExtractor(file, mPackageDir);
            while (apf.extractNext()) {

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("process", "AssetPackageFileExtractor error" + e.getMessage());
            file.delete();
            FileUtils.deleteDirectory(mPackageDir);

            return null;
        } finally {
            try {
                if (apf != null) {
                    apf.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        try {
//            ZipUtils.unZip(file.getAbsolutePath(), mPackageDir.getAbsolutePath());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        file.delete();
        return mPackageDir;
    }


}
