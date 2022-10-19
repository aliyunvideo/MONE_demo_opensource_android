/*
 * Copyright (C) 2010-2017 Alibaba Group Holding Limited.
 */

package com.aliyun.svideo.downloader.zipprocessor;

import java.io.File;

public interface FileProcessor {

    File process(File file);

}
