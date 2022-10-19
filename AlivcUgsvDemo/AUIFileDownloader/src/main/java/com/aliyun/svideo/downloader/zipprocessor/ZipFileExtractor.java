/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;


import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;



public abstract
class ZipFileExtractor {

    private final Enumeration<?> entries;
    private final ZipFile zipFile;

    public ZipFileExtractor(File file) throws IOException  {
        zipFile = new ZipFile(file, "gbk");
        entries = zipFile.getEntries();
    }

    public boolean extractNext() throws IOException {
        if (!entries.hasMoreElements()) {
            return false;
        }
        ZipEntry entry = ((ZipEntry)entries.nextElement());

        if (entry == null) {
            return false;
        }
        if (entry.isDirectory()) {

            return true;
        }

        File output_file = getOutputFile(entry);

        if (output_file == null) {
            return true;
        }

        File output_dir = output_file.getParentFile();
        if (output_dir != null) {
            output_dir.mkdirs();
        }

        InputStream is = zipFile.getInputStream(entry);

        FileOutputStream ostream = new FileOutputStream(output_file);

        try {
            // FIXME use guava ByteStreams.copy
            copy(is, ostream);
        } finally {
            is.close();
            ostream.close();
        }

        return true;
    }

    protected abstract File getOutputFile(ZipEntry entry);

    public void close() throws IOException {
        if (zipFile != null) {
            zipFile.close();
        }
    }

    public static long copy(InputStream from, OutputStream to)
    throws IOException {
        byte[] buf = new byte[0x1000];
        long total = 0;
        while (true) {
            int r = from.read(buf);
            if (r == -1) {
                break;
            }
            to.write(buf, 0, r);
            total += r;
        }
        return total;
    }
}
