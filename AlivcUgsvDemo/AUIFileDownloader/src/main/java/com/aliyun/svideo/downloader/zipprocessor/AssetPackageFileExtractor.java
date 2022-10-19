/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;

import org.apache.tools.zip.ZipEntry;

import java.io.File;
import java.io.IOException;


/**
 * extract an asset zip package input stream into a directory
 * stripping the top level dir
 *
 */
public final class AssetPackageFileExtractor extends ZipFileExtractor {

    private final File _OutputDir;

    public AssetPackageFileExtractor(File file, File output_dir) throws IOException {
        super(file);
        _OutputDir = output_dir;
    }

    @Override
    protected File getOutputFile(ZipEntry entry) {
        String name = entry.getName();
        int first_slash = name.indexOf('/');
        if (first_slash >= 0) {
            name = name.substring(first_slash + 1);
        }
        return new File(_OutputDir, name);
    }

}
