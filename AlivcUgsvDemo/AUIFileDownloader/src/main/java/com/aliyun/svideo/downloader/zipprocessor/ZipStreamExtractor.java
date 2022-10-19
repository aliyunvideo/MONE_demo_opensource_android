/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public abstract class ZipStreamExtractor {

    private final ZipInputStream _Input;

    public
    ZipStreamExtractor(InputStream istream) {
        _Input = new ZipInputStream(istream);
    }

    public
    boolean extractNext() throws IOException {
        ZipEntry entry = _Input.getNextEntry();
        if (entry == null) {
            return false;
        }
        if (entry.isDirectory()) {
            _Input.closeEntry();
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

        FileOutputStream ostream = new FileOutputStream(output_file);

        try {
            // FIXME use guava ByteStreams.copy
            copy(_Input, ostream);
        } finally {
            ostream.close();
        }

        return true;
    }

    protected abstract
    File getOutputFile(ZipEntry entry);

    public
    void close() throws IOException {
        _Input.close();
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
